package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.model.CommonData;
import org.gora.server.model.eServiceRouteType;
import org.gora.server.model.network.PlayerCoordinate;
import org.gora.server.service.PlayerCoordinateService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private static final BlockingQueue<CommonData> routerQue = new LinkedBlockingQueue<>(Integer.parseInt(
            System.getenv(Env.MAX_DEFAULT_QUE_SZ)));
    private final ObjectMapper objectMapper;
    
    // todo queue full 경우 체크하기
    // 클라이언트에게 대기 메시지 송신
    // 클라이언트는 일정시간 이후 다시 보냄
    public static void push(CommonData data) {
        routerQue.add(data);
    }
    public static int size(){
        return routerQue.size();
    }

    @Async
    public void run() {
        while (true) {
            CommonUtils.sleep();
            routerQue.stream().findFirst().ifPresent(commonData -> {
                log.info("router que size {}", routerQue.size());
                if (!routerQue.remove(commonData)) {
                    log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                    return;
                }

                eServiceRouteType routeType = commonData.getType();
                if (routeType == null){
                    if (!routerQue.remove(commonData)) {
                        log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                        return;
                    }
                    return;
                }

                switch(routeType){
                    case player_coordinate:
                        PlayerCoordinate playerCoordinate = (PlayerCoordinate) commonData.getData();
                        if(playerCoordinate == null){
                            log.error("[router] convert fail");
                            return;
                        }
                        playerCoordinateService.broadcasePlayerCoordinate(commonData.getKey(), playerCoordinate);
                        break;
                    default:
                        if (!routerQue.remove(commonData)) {
                            log.error("[router 큐] 큐에서 읽은 데이터 삭제 실패");
                            return;
                        }
                }
            });
        }

    }
}
