package com.gora.server.component;

import com.gora.server.common.utils.CommonUtils;
import com.gora.server.component.network.ClientManager;
import com.gora.server.component.network.PacketRouter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Monitor {
    private final ClientManager clientManager;

    @Scheduled(fixedDelay = 3000)
    public void start() {
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
        log.info("Used Memory: {}MB", CommonUtils.bytesToMegabytes(usedMemory));
        log.info("Max Memory: {}MB", CommonUtils.bytesToMegabytes(maxMemory));

        log.info("Router Max Que Szie: {}", System.getenv("MAX_DEFAULT_QUE_SZ"));
        log.info("Router Current Que Size: {}", PacketRouter.size());

        log.info("Client Size: {}", clientManager.getClientCount());

        int cores = Runtime.getRuntime().availableProcessors();
        log.info("Available process count: {}", cores);
        int activeThreadCount = Thread.activeCount();
        log.info("active thread count: {}", activeThreadCount);
    }
}
