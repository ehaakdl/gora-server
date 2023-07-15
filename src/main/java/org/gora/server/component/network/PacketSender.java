package org.gora.server.component.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.component.network.UdpClientManager;
import org.gora.server.model.CommonData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PacketSender {
    private final List<CommonData> sendQue;
    private final UdpClientManager udpClientManager;

    public PacketSender(UdpClientManager udpClientManager) {
        this.udpClientManager = udpClientManager;
        this.sendQue = new ArrayList<>(
                Integer.parseInt(
                        CommonUtils.getEnv(
                                eEnv.MAX_DEFAULT_QUE_SZ
                                , eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ)
                        )
                )
        );
    }

    public void push(CommonData data){
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

                byte[] sendBytes = CommonData.serialization(commonData);
                if(sendBytes == null){
                    log.error("[sender 스레드] 전송 실패 = 객체 직렬화 실패");
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
