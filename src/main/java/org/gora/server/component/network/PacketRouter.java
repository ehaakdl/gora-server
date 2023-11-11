package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.Env;
import org.gora.server.model.TransportData;
import org.gora.server.model.exception.OverSizedException;
import org.gora.server.model.network.eServiceType;
import org.gora.server.service.CloseClientResource;
import org.gora.server.service.PlayerCoordinateService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 수신된 패킷을 라우트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PacketRouter {
    private final PlayerCoordinateService playerCoordinateService;
    private final CloseClientResource closeClientResource;
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

                eServiceType serviceType = packet.getType();
                if (serviceType == null) {
                    return;
                }

                switch (serviceType) {
                    case test:
                        if(test){
                            closeClientResource.close(packet.getChanelId());
                            test =false;
                        }
                        break;
                    default:
                        log.error("[router 큐] 처리할 수 없는 유형에 패킷이 왔습니다.");
                        return;
                }
            });
        }

    }
}
