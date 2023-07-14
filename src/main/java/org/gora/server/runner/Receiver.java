package org.gora.server.runner;

import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.gora.server.common.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Receiver {
    private static final List<CommonData> receiveQue = new ArrayList<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, String.valueOf(1000))));

    public static void push(CommonData data){
        receiveQue.add(data);
    }

    public static void run() {
        receiveQue.stream().findFirst().ifPresent(commonData -> {
            if(!receiveQue.remove(commonData)){
                log.error("[수신 큐] 큐에서 읽은 데이터 삭제 실패");
            }

//            Todo udp 동작 테스트를 위해 임시로 작성된 코드
            Sender.push(commonData);
        });
    }
}
