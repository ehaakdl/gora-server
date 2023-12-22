package org.gora.server.component.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.Env;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.TransportData;
import org.gora.server.model.eRouteServiceType;
import org.gora.server.model.exception.OverSizedException;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.TestProtoBuf.Test;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.gora.server.service.ClientService;
import org.gora.server.service.CloseClientResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 수신된 패킷을 라우트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PacketRouter {
    private final ClientManager clientManager;
    private final ClientService clientService;

    private static final BlockingQueue<TransportData> routerQue = new LinkedBlockingQueue<>(Integer.parseInt(
            System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public static void push(TransportData data) {
        try {
            routerQue.add(data);
        } catch (IllegalStateException e) {
            throw new OverSizedException();
        }

    }

    public static int size() {
        return routerQue.size();
    }

    public static boolean test = false;

    @Async
    public void run() {
        while (true) {
            routerQue.stream().findFirst().ifPresent(packet -> {
                if (!routerQue.remove(packet)) {
                    log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                    return;
                }

                eRouteServiceType routeServiceType = packet.getType();
                if (routeServiceType == null) {
                    return;
                }

                switch (routeServiceType) {
                    case test:
                        // 임시코드
                        eNetworkType protocolType = clientManager
                                .getNetworkProtocolTypeByChannelId(packet.getChanelId());
                        if (protocolType == null) {
                            return;
                        }

                        eServiceType serviceType = eServiceType.test;
                        Test test = Test.newBuilder().setMsg(ByteString.copyFrom("2133".getBytes())).build();
                        NetworkPacket packet2 = NetworkUtils.getPacket(test.toByteArray(), serviceType);
                        try {
                            if (!clientManager.send(protocolType, serviceType, packet2,
                                    packet.getChanelId())) {
                                throw new RuntimeException();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case chat:

                        break;
                    case udp_initial:
                        clientService.initialUdp(packet);
                        break;
                    case clean_data_buffer:
                        break;
                    case close_client:
                        CloseClientResource.close(packet.getChanelId());
                        break;
                    default:
                        log.error("[router 큐] 처리할 수 없는 유형에 패킷이 왔습니다.");
                        return;
                }
            });
        }

    }
}
