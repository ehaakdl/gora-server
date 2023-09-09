package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.gora.server.model.eCodeType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PacketSender {
    private final UdpClientManager udpClientManager;
    private final TcpClientManager tcpClientManager;

    private static final BlockingQueue<CommonData> sendQue = new LinkedBlockingQueue<>(
            Integer.parseInt(
                    CommonUtils.getEnv(
                            eEnv.MAX_DEFAULT_QUE_SZ
                            , eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ)
                    )
            )
    );

    public static void push(CommonData data){
        sendQue.add(data);
    }

    @Async
    public void run() {
        while (true){
            CommonUtils.sleep();
            sendQue.stream().findFirst().ifPresent(commonData -> {
                if(!sendQue.remove(commonData)){
                    log.error("[송신 큐] 큐에서 읽은 데이터 삭제 실패");

                }

                try {
                    if(commonData.getType() == eCodeType.tcp){
                        tcpClientManager.send(commonData);
                    }else{
                        udpClientManager.send(commonData);
                    }

                }catch (RuntimeException e){
                    log.error("전송 실패");
                    log.error(CommonUtils.getStackTraceElements(e));
                }
            });
        }
    }
}
