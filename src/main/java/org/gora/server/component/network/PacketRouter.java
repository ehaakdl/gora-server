package org.gora.server.component.network;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.utils.CommonUtils;
import org.gora.server.common.utils.NetworkUtils;
import org.gora.server.model.PacketRouterDTO;
import org.gora.server.model.exception.OverSizedException;
import org.gora.server.model.network.UdpInitialDTO;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eRouteServiceType;
import org.gora.server.model.network.eServiceType;
import org.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.model.network.protobuf.TestProtoBuf.Test;
import org.gora.server.service.ClientCloseService;
import org.gora.server.service.ClientConnectionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 수신된 패킷을 적절한 서비스로 라우팅 해주는 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PacketRouter {
    private final ClientManager clientManager;
    private final ClientConnectionService clientService;

    private static final BlockingQueue<PacketRouterDTO> routerQue = new LinkedBlockingQueue<>(Integer.parseInt(
            System.getenv("MAX_DEFAULT_QUE_SZ")));

    public static void push(PacketRouterDTO data) {
        try {
            routerQue.add(data);
        } catch (IllegalStateException e) {
            throw new OverSizedException();
        }

    }

    public static int size() {
        return routerQue.size();
    }

    @Async
    public void run() {
        while (true) {
            routerQue.stream().findFirst().ifPresent(packet -> {
                if (!routerQue.remove(packet)) {
                    log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                    return;
                }

                eRouteServiceType routeServiceType = packet.getType();

                if (!isInstance(routeServiceType, packet.getData())) {
                    log.error("잘못된 타입이 router에 들어왔다 {}", packet.getData());
                    return;
                }

                switch (routeServiceType) {
                    // todo 임시코드 지우기
                    case test:
                        doTestService(packet);
                        break;
                    case chat:

                        break;
                    case udp_initial:
                        if (!CommonUtils.isInstance(packet.getData(), UdpInitialDTO.class)) {
                            throw new RuntimeException();
                        }
                        clientService.initialUdp(packet);
                        break;
                    case clean_data_buffer:
                        break;
                    case close_client:
                        ClientCloseService.close(packet.getChanelId());
                        break;
                    default:
                        log.error("[router 큐] 처리할 수 없는 유형에 패킷이 왔습니다.");
                        return;
                }
            });
        }

    }

    private boolean isInstance(eRouteServiceType type, Object target) {
        switch (type) {
            // todo 임시코드 지우기
            case test:
                return true;
            case chat:

                return true;
            case udp_initial:

                return CommonUtils.isInstance(target, UdpInitialDTO.class);
            case clean_data_buffer:
                return true;
            case close_client:
                return true;

            default:
                return false;
        }
    }

    private void doTestService(PacketRouterDTO packet) {
        eNetworkType protocolType = clientManager
                .getNetworkProtocolTypeByChannelId(packet.getChanelId());
        if (protocolType == null) {
            return;
        }

        eServiceType serviceType = eServiceType.test;
        Test test = Test.newBuilder().setMsg(ByteString.copyFrom("2133".getBytes())).build();
        byte[] dataBytes = test.toByteArray();
        List<NetworkPacket> packet2 = NetworkUtils.generateSegmentPacket(dataBytes, serviceType,
                NetworkUtils.generateIdentify(), dataBytes.length, NetworkUtils.UDP_EMPTY_CHANNEL_ID);

        for (int i = 0; i < packet2.size(); i++) {
            boolean isSend = clientManager.send(protocolType, serviceType, packet2.get(i),
                    packet.getChanelId());
            if (!isSend) {
                log.error("udp 클라이언트 식별값 전달 실패");
            }
        }

    }
}
