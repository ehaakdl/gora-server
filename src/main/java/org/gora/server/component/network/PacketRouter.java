package org.gora.server.component.network;

import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 수신된 패킷을 라우트
 */
@Slf4j
@Component
public class PacketRouter {
    private static final List<CommonData> receiveQue = new ArrayList<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));;

    public static void push(CommonData data){
        receiveQue.add(data);
    }

    @Async
    public void run() {
        while(true) {
            CommonUtils.sleep();
            receiveQue.stream().findFirst().ifPresent(commonData -> {
                if(!receiveQue.remove(commonData)){
                    log.error("[수신 큐] 큐에서 읽은 데이터 삭제 실패");
                }

//                todo 수신된 패킷에 type 보고 라우팅 하는 기능 추가 필요
                PacketSender.push(commonData);
            });
        }

    }
}
