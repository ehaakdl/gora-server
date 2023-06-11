package org.gora.runner;

import lombok.extern.slf4j.Slf4j;
import org.gora.constant.eEnv;
import org.gora.model.CommonData;
import org.gora.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Receiver {
    private static final List<CommonData> receiveQue = new ArrayList<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, String.valueOf(1000))));

    public static void run() {
        receiveQue.stream().findFirst().ifPresent(commonData -> {
//                꺼내기
            if(!receiveQue.remove(commonData)){
                log.error("[수신 큐] 큐에서 읽은 데이터 삭제 실패");
            }
        });
    }
}
