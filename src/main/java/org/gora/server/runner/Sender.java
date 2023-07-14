package org.gora.server.runner;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.gora.server.common.CommonUtils;
import org.gora.server.service.UdpClientManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Sender {
    private static final List<CommonData> sendQue = new ArrayList<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, String.valueOf(1000))));

    public static void push(CommonData data){
        sendQue.add(data);
    }
    public static void run() {
        sendQue.stream().findFirst().ifPresent(commonData -> {
            if(!sendQue.remove(commonData)){
                log.error("[송신 큐] 큐에서 읽은 데이터 삭제 실패");
                return;
            }

            String json = "{"
                    + "    \"message\":\"2ewqe\","
                    + "    \"type\":\"test\""
                    + "}";

            ChannelFuture channelFuture = UdpClientManager.getChannelFuture(commonData.getKey());
            if(channelFuture == null){
                return;
            }

            ByteBuf copyBuffer = Unpooled.copiedBuffer(json.getBytes());
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
