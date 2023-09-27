package org.gora.server.component.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.model.NetworkPacket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PacketSender {
    private final ClientManager clientManager;

    private static final BlockingQueue<NetworkPacket> sendQue = new LinkedBlockingQueue<>(
            Integer.parseInt(
                    System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // todo queue full 경우 체크하기
    // 클라이언트에게 대기 메시지 송신
    // 클라이언트는 일정시간 이후 다시 보냄
    public static void push(NetworkPacket data) {
        sendQue.add(data);
    }

    public static int size() {
        return sendQue.size();
    }

    @Async
    public void run() {
        while (true) {
            CommonUtils.sleep();
            sendQue.stream().findFirst().ifPresent(NetworkPacket -> {
                log.info("send que size {}", sendQue.size());
                if (!sendQue.remove(NetworkPacket)) {
                    log.error("[송신 큐] 큐에서 읽은 데이터 삭제 실패");

                }

                clientManager.send(NetworkPacket);
            });
        }
    }
}
