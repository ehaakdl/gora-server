package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 수신된 패킷을 라우트
 */
@Slf4j
@Component
public class PacketRouter {
    private static final BlockingQueue<CommonData> routerQue = new LinkedBlockingQueue<>(Integer.parseInt(
            CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

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
                if (!routerQue.remove(commonData)) {
                    log.error("[수신 큐] 큐에서 읽은 데이터 삭제 실패");
                }
                log.info("router que size {}", routerQue.size());
                
                // todo 수신된 패킷에 type 보고 라우팅 하는 기능 추가 필요
                try{
                    PacketSender.push(commonData);
                }catch(IllegalStateException e){
                    log.error("송신 큐가 꽉 찼습니다. {}", PacketSender.size());
                }
                
            });
        }

    }
}
