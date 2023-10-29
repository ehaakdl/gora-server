package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.PlayerCoordinateProtoBuf.PlayerCoordinate;
import org.gora.server.model.network.eServiceRouteTypeProtoBuf.eServiceRouteType;
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
    private static final BlockingQueue<NetworkPacket> routerQue = new LinkedBlockingQueue<>(Integer.parseInt(
            System.getenv(Env.MAX_DEFAULT_QUE_SZ)));
    
    // todo queue full 경우 체크하기
    // 클라이언트에게 대기 메시지 송신
    // 클라이언트는 일정시간 이후 다시 보냄
    public static void push(NetworkPacket data) {
        routerQue.add(data);
    }
    public static int size(){
        return routerQue.size();
    }

    @Async
    public void run() {
        while (true) {
            CommonUtils.sleep();
            routerQue.stream().findFirst().ifPresent(NetworkPacket -> {
                log.info("router que size {}", routerQue.size());
                if (!routerQue.remove(NetworkPacket)) {
                    log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                    return;
                }

                eServiceRouteType routeType = NetworkPacket.getType();
                if (routeType == null){
                    if (!routerQue.remove(NetworkPacket)) {
                        log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                        return;
                    }
                    return;
                }

                // switch(routeType){
                    
                //     default:
                //         log.error("[router 큐] 처리할 수 없는 유형에 패킷이 왔습니다.");
                //         return;
                // }
            });
        }

    }
}
