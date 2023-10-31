package org.gora.server.component;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.PacketRouter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class Monitor {

    @Async
    public void start(){
        while(true) {
            // 런타임 환경 가져오기
            Runtime runtime = Runtime.getRuntime();

            // 총 메모리 (total memory)
            long totalMemory = runtime.totalMemory();

            // 사용 가능한 메모리 (free memory)
            long freeMemory = runtime.freeMemory();

            // 사용 중인 메모리 (used memory)
            long usedMemory = totalMemory - freeMemory;

            // 최대 사용 가능 메모리 (max memory)
            long maxMemory = runtime.maxMemory();
            
            log.info("------------------------------------------------------------");
            log.info("Total Memory: {}MB", CommonUtils.bytesToMegabytes(totalMemory));
            log.info("Free Memory: {}MB", CommonUtils.bytesToMegabytes(freeMemory));
            log.info("Used Memory: {}MB",CommonUtils.bytesToMegabytes(usedMemory));
            log.info("Max Memory: {}MB", CommonUtils.bytesToMegabytes(maxMemory));

            log.info("Router Que Szie: {}", PacketRouter.size());
        
            CommonUtils.sleep(3000);
        }        
    }
}
