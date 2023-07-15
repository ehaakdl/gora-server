package org.gora.server.component.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.component.network.UdpClientManager;
import org.gora.server.model.CommonData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class PacketSender {

    private static final BlockingQueue<CommonData> sendQue = new LinkedBlockingQueue<>(
            Integer.parseInt(
                    CommonUtils.getEnv(
                            eEnv.MAX_DEFAULT_QUE_SZ
                            , eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ)
                    )
            )
    );

    private final ObjectMapper objectMapper;
    private final UdpClientManager udpClientManager;


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
                    return;
                }

                ChannelFuture channelFuture = udpClientManager.getChannelFuture(commonData.getKey());
                if(channelFuture == null){
                    log.error("[sender 스레드] 전송 실패 = channelFuture not empty");
                    return;
                }

                byte[] sendBytes;
                try {
                    sendBytes = objectMapper.writeValueAsBytes(commonData);
                } catch (JsonProcessingException e) {
                    log.error("send 데이터 직렬화 실패");
                    log.error(CommonUtils.getStackTraceElements(e));
                    return;
                }

                ByteBuf copyBuffer = Unpooled.copiedBuffer(sendBytes);
                channelFuture.channel()
                        .writeAndFlush(copyBuffer)
                        .addListener((ChannelFutureListener) future -> {
                            if(!future.isSuccess()){
                                log.error("클라이언트로 송신 실패");
                            }
                        });
            });
        }
    }
}
