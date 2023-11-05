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
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.ClientNetworkDataWrapper;
import org.gora.server.model.network.ClientResource;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private UdpServer udpServer;

    // key는 채널 아이디(네티는 전체 채널 인스턴스에 대한 고유한 아이디를 가지고있다.)
    private final static Map<String, ClientResource> resources = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // 패킷 수신핸들러가 아닌 다른 진입점에서 채널 아이디를 가져오기 위한 저장소
    // key 는 userSeq
    private final static Map<Long, String> userResourceMap = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public Set<String> getResourceKeys() {
        return resources.keySet();
    }

    public boolean existsResource(String resourceKey) {
        return resources.containsKey(resourceKey);
    }

    public void createResource(String resourceKey, ClientConnection connection) {
        ClientNetworkBuffer buffer = ClientNetworkBuffer.create();
        resources.putIfAbsent(resourceKey, ClientResource.builder().buffer(buffer).connection(connection).build());
    }

    private List<TransportData> assembleData(List<NetworkPacket> packets, String resourceKey, eNetworkType networkType)
            throws IOException, ClassNotFoundException {
        List<TransportData> result = new ArrayList<>();

        ClientResource resource = resources.get(resourceKey);
        if (resource == null) {
            throw new RuntimeException();
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
                    dataBuffer.write(Arrays.copyOf(data, dataNonPaddingSize));
                    dataWrapper.setAppendAt(System.currentTimeMillis());
                    if (dataBuffer.size() == totalSize) {
                        result.add(TransportData.create(serviceType,
                                CommonUtils.bytesToObject(dataBuffer.toByteArray()), resourceKey));
                        clientNetworkBuffer.removeDataWrapper(identify, networkType);
                    } else if (dataBuffer.size() > totalSize) {
                        throw new RuntimeException();
                    }
                }
            } else if (dataNonPaddingSize > NetworkUtils.DATA_MAX_SIZE) {
                throw new RuntimeException();
            } else {
                dataBuffer.write(Arrays.copyOf(data, dataNonPaddingSize));
                dataWrapper.setAppendAt(System.currentTimeMillis());
                if (dataBuffer.size() == totalSize) {
                    result.add(TransportData.create(serviceType, CommonUtils.bytesToObject(dataBuffer.toByteArray()),
                            resourceKey));
                    clientNetworkBuffer.removeDataWrapper(identify, networkType);
                } else if (dataBuffer.size() > totalSize) {
                    throw new RuntimeException();
                }
            }
        }

        return result;
    }

    public List<TransportData> assemblePacket(String resourceKey, eNetworkType networkType,
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
                packets.add((NetworkPacket) CommonUtils.bytesToObject(convertBytes));
            }

            // 역직렬화 후 남은 데이터는 추출해서 buffer reset후 다시 buffer에 추가
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

    public boolean send(NetworkPacket data) {
        // ClientConnection clientConnection = clients.get(data.getKey());
        // if (clientConnection == null) {
        // log.error("클라이언트 존재 안함");
        // return false;
        // }

        // ByteBuf sendBuf = NetworkPacket.convertByteBuf(data, objectMapper);
        // if (sendBuf == null) {
        // log.error("송신 데이터 파싱 실패");
        // return false;
        // }

        // if (data.getProtocol() == eProtocol.tcp) {
        // if (!clientConnection.isConnectionTcp()) {
        // log.error("클라이언트와 TCP 연결 안됨");
        // return false;
        // }

        // clientConnection.getTcpChannel().writeAndFlush(sendBuf).addListener(future ->
        // {
        // if (!future.isSuccess()) {
        // log.error("송신 실패");
        // log.error(CommonUtils.getStackTraceElements(future.cause()));
        // }
        // });
        // } else {
        // udpServer.send(clientConnection.getClientIp(), udpClientPort,
        // sendBuf.array());
        // }

        return true;
    }

    public void close(String resourceKey, Long userSeq) {
        ClientResource clientResource;
        if (resourceKey != null) {
            clientResource = resources.get(resourceKey);
        } else if (userSeq != null) {
            resourceKey = userResourceMap.get(userSeq);
            if (resourceKey == null) {
                return;
            }
            clientResource = resources.get(resourceKey);
        } else {
            throw new RuntimeException();
        }

        if (clientResource == null) {
            return;
        }
        resources.remove(resourceKey);

        ClientNetworkBuffer buffer = clientResource.getBuffer();
        if (buffer != null) {
            buffer.getTcpRecvBuffer().reset();
            buffer.getUdpRecvBuffer().reset();
        }

        ClientConnection clientConnection = clientResource.getConnection();
        if (clientConnection != null) {
            if (clientConnection.isConnectionTcp()) {
                clientConnection.getTcpChannel().close();
            }
        }
    }

    public int getClientCount() {
        return resources.size();
    }

    public ClientResource getResource(String key) {
        return resources.getOrDefault(key, null);
    }
}
