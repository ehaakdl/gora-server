package org.gora.server.component.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.TransportData;
import org.gora.server.model.exception.ExpiredPacketException;
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.ClientNetworkDataWrapper;
import org.gora.server.model.network.ClientResource;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.gora.server.service.CloseClientResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class ClientManager {
    @Value("${app.udp_client_port}")
    private int udpClientPort;
    private final UdpServer udpServer;
    private final CloseClientResource closeClientResource;

    // key는 채널 아이디(네티는 전체 채널 인스턴스에 대한 고유한 아이디를 가지고있다.)
    private final static Map<String, ClientResource> resources = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // 유저번호와 채널 아이디매치 저장소
    // key 는 userSeq
    private final static Map<Long, String> userResourceMap = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public Set<String> getResourceKeys() {
        return resources.keySet();
    }

    public static boolean existsResource(String resourceKey) {
        return resources.containsKey(resourceKey);
    }

    public static void createResource(String resourceKey, ClientConnection connection) {
        resources.putIfAbsent(resourceKey, ClientResource.create(connection));
    }

    private static List<TransportData> assembleData(List<NetworkPacket> packets, String resourceKey,
            eNetworkType networkType)
            throws IOException, ClassNotFoundException {
        List<TransportData> result = new ArrayList<>();

        ClientResource resource = resources.get(resourceKey);
        if (resource == null) {
            return Collections.emptyList();
        }

        String identify;
        int totalSize;
        byte[] data;
        eServiceType serviceType;
        int dataNonPaddingSize;
        NetworkPacket packet;
        for (int index = 0; index < packets.size(); index++) {
            packet = packets.get(index);
            identify = packet.getIdentify();
            totalSize = packet.getTotalSize();
            data = packet.getData().toByteArray();
            serviceType = eServiceType.convert(packet.getType());
            dataNonPaddingSize = packet.getDataSize();

            ClientNetworkBuffer clientNetworkBuffer = resource.getBuffer();
            ClientNetworkDataWrapper dataWrapper;
            ByteArrayOutputStream dataBuffer;
            if (clientNetworkBuffer.containDataWrapper(identify, networkType)) {
                dataWrapper = clientNetworkBuffer.getDataWrapper(identify, networkType);
                // 클린 리소스 스레드가 삭제할수도 있기 떄문에 null 체크필요하다.
                if (dataWrapper == null) {
                    dataWrapper = ClientNetworkDataWrapper.create();
                    clientNetworkBuffer.putDataWrapper(identify, networkType, dataWrapper);
                }
            } else {
                dataWrapper = ClientNetworkDataWrapper.create();
                clientNetworkBuffer.putDataWrapper(identify, networkType, dataWrapper);
            }

            dataBuffer = dataWrapper.getBuffer();
            // 패딩 제거(실 사이즈와 최대 데이터 크기 하여 패딩 삭제)
            if (dataNonPaddingSize < NetworkUtils.DATA_MAX_SIZE) {
                // 세션 체크용 패킷만 데이터가 비어있을수가 있다. 그외의 서비스 패킷은 다 에러 처리
                if (dataNonPaddingSize == 0) {
                    if (serviceType == eServiceType.health_check) {
                        TransportData transportData = TransportData.builder()
                                .chanelId(resourceKey)
                                .type(eServiceType.health_check)
                                .build();
                        result.add(transportData);
                        clientNetworkBuffer.removeDataWrapper(identify, networkType);
                    } else {
                        throw new RuntimeException();
                    }
                } else {
                    try {
                        TransportData transportData = TransportData.convert(dataBuffer, data, dataNonPaddingSize,
                                dataWrapper,
                                totalSize, serviceType, identify, networkType, resourceKey, clientNetworkBuffer);
                        if (transportData != null) {
                            result.add(transportData);
                            clientNetworkBuffer.removeDataWrapper(identify, networkType);
                        }
                    } catch (ExpiredPacketException e) {
                        clientNetworkBuffer.removeDataWrapper(identify, networkType);
                    }
                }
            } else if (dataNonPaddingSize > NetworkUtils.DATA_MAX_SIZE) {
                throw new RuntimeException();
            } else {
                try {
                    TransportData transportData = TransportData.convert(dataBuffer, data, dataNonPaddingSize,
                            dataWrapper,
                            totalSize, serviceType, identify, networkType, resourceKey, clientNetworkBuffer);
                    if (transportData != null) {
                        result.add(transportData);
                        clientNetworkBuffer.removeDataWrapper(identify, networkType);
                    }
                } catch (ExpiredPacketException e) {
                    clientNetworkBuffer.removeDataWrapper(identify, networkType);
                }
            }
        }

        return result;
    }

    public static List<TransportData> assemblePacket(String resourceKey, eNetworkType networkType,
            byte[] packetBytes)
            throws IOException, ClassNotFoundException {
        ClientResource resource = resources.get(resourceKey);
        if (resource == null) {
            return Collections.emptyList();
        }

        ClientNetworkBuffer buffer = resource.getBuffer();
        if (buffer == null) {
            return Collections.emptyList();
        }

        ByteArrayOutputStream recvBuffer;
        if (networkType == eNetworkType.tcp) {
            recvBuffer = buffer.getTcpRecvBuffer();
        } else {
            recvBuffer = buffer.getUdpRecvBuffer();
        }

        List<NetworkPacket> packets = new ArrayList<>();
        recvBuffer.write(packetBytes);
        int assembleTotalCount = 0;
        if (recvBuffer.size() >= NetworkUtils.TOTAL_MAX_SIZE) {
            int remainRecvByte = recvBuffer.size() % NetworkUtils.TOTAL_MAX_SIZE;
            assembleTotalCount = recvBuffer.size() / NetworkUtils.TOTAL_MAX_SIZE;

            // 네트워크 패킷 클래스로 역직렬화
            byte[] convertBytes = new byte[NetworkUtils.TOTAL_MAX_SIZE];
            int from = 0;
            int to;
            for (int count = 0; count < assembleTotalCount; count++) {
                from = count * NetworkUtils.TOTAL_MAX_SIZE;
                to = (count + 1) * NetworkUtils.TOTAL_MAX_SIZE;
                convertBytes = Arrays.copyOfRange(recvBuffer.toByteArray(), from, to);
                packets.add(NetworkPacket.parseFrom(convertBytes));
            }

            // 역직렬화 후 남은 데이터는 추출해서 buffer reset후 다시 buffer에 추가
            // 공유자원인 버퍼 리셋은 무조건 여기서만 해야한다. 다른 스레드에서 리셋을 하면안됨(배열 인덱스 예외 발생함)
            // 리셋 하더라도 배열 값이 0으로 초기화 되기때문에 여기서만 리셋한다.
            // 자원 해제는 버퍼를 담는 맵 자체를 삭제하고 버퍼 자체는 건들지 말자.
            if (remainRecvByte > 0) {
                int endPos = recvBuffer.toByteArray().length - remainRecvByte;
                byte[] remainBytes = new byte[remainRecvByte];
                System.arraycopy(recvBuffer.toByteArray(), endPos, remainBytes, 0, remainBytes.length);
                recvBuffer.reset();
                recvBuffer.write(remainBytes);
            } else {
                recvBuffer.reset();
            }

            return assembleData(packets, resourceKey, networkType);
        } else {
            return Collections.emptyList();
        }
    }

    public eNetworkType getNetworkProtocolTypeByChannelId(String channelId) {
        ClientResource clientResource = resources.getOrDefault(channelId, null);
        if (clientResource == null) {
            return null;
        }

        if (clientResource.getConnection().isConnectionTcp()) {
            return eNetworkType.tcp;
        } else if (clientResource.getConnection().getClientIp() != null) {
            return eNetworkType.udp;
        } else {
            return null;
        }
    }

    // todo 개선
    // 비동기/동기 방식으로 전달 가능하게 하기
    // 비동기 같은 경우 콜백함수 전달하여 사용자가 커스텀 가능하게 만들기
    // 지금 구조에서는 적당한거 같음 나중에 고도화 작업에 포함
    public boolean send(
            eNetworkType networkType, eServiceType serviceType, String identify, byte[] data,
            String chanelId) throws IOException {

        ClientResource resource = resources.getOrDefault(chanelId, null);
        if (resource == null) {
            return false;
        }

        if (networkType == eNetworkType.tcp) {
            if (!resource.getConnection().isConnectionTcp()) {
                return false;
            }
            ChannelHandlerContext handlerContext = resource.getConnection().getTcpChannel();

            // 패킷 분할생성
            List<NetworkPacket> packets = NetworkUtils.getSegment(data, serviceType, identify);
            if (packets == null) {
                return false;
            }

            // 송신
            for (NetworkPacket packet : packets) {
                ByteBuf sendBytebuf = Unpooled.wrappedBuffer(packet.toByteArray());
                handlerContext.writeAndFlush(sendBytebuf).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("tcp 송신 실패");
                        log.error(CommonUtils.getStackTraceElements(future.cause()));
                    }
                });
            }

            return true;
        } else {
            String clientIp = resource.getConnection().getClientIp();
            udpServer.send(clientIp, udpClientPort, data);
            return true;
        }
    }

    public static void close(String resourceKey) {
        ClientResource clientResource;
        clientResource = resources.get(resourceKey);
        if (clientResource == null) {
            return;
        }
        resources.remove(resourceKey);

        Long userSeq = clientResource.getUserSeq();
        if (userSeq != null) {
            userResourceMap.remove(clientResource.getUserSeq());
        }

        ClientConnection tcpConnection = clientResource.getConnection();
        if (tcpConnection != null) {
            if (tcpConnection.isConnectionTcp()) {
                tcpConnection.getTcpChannel().close();
            }
        }
    }

    public void close(Long userSeq) {
        String resourceKey = userResourceMap.get(userSeq);
        if (resourceKey == null) {
            return;
        }
        close(resourceKey);
    }

    public int getClientCount() {
        return resources.size();
    }

    public ClientResource getResource(String key) {
        return resources.getOrDefault(key, null);
    }
}
