package org.gora.server.component.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UdpClientManager {
    private final ObjectMapper objectMapper;
    private final Map<String, UdpClient> clients;
    private final UdpClientInboundHandler udpClientInboundHandler;
    @Value("${app.udp_client_port}")
    private int udpClientPort;

    public UdpClientManager(@Autowired ObjectMapper objectMapper, UdpClientInboundHandler udpClientInboundHandler) {
        this.objectMapper = objectMapper;
        this.clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));
        this.udpClientInboundHandler = udpClientInboundHandler;
    }

    public boolean contain(String key){
        if(key==null){
            return false;
        }

        return clients.containsKey(key);
    }

    public void send(CommonData commonData) {
        ChannelFuture channelFuture = getChannelFuture(commonData.getKey());
        if (channelFuture == null) {
            log.error("[sender 스레드] 전송 실패 = channelFuture not empty");
            throw new RuntimeException();
        }

        byte[] sendBytes;
        try {
            sendBytes = objectMapper.writeValueAsBytes(commonData);
        } catch (JsonProcessingException e) {
            log.error("send 데이터 직렬화 실패");
            log.error(CommonUtils.getStackTraceElements(e));
            throw new RuntimeException();
        }

        ByteBuf copyBuffer = Unpooled.copiedBuffer(sendBytes);
        channelFuture.channel()
                .writeAndFlush(copyBuffer)
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("클라이언트로 송신 실패");
                        throw new RuntimeException();
                    }
                });
    }

    public String connect(String clientIp){
        String key = CommonUtils.replaceUUID();
        UdpClient udpClient;

        try {
            udpClient = new UdpClient(clientIp, udpClientPort, udpClientInboundHandler);
        } catch (InterruptedException e) {
            log.error("udp client connect fail");
            log.error(CommonUtils.getStackTraceElements(e));
            return null;
        }

        this.clients.put(key, udpClient);

        return key;
    }

    public ChannelFuture getChannelFuture(String key){
        if(!contain(key)){
            return null;
        }

        return clients.get(key).getChannelFuture();
    }

    public boolean shutdown(String key){
        if(!contain(key)){
            return false;
        }

        UdpClient udpClient = clients.get(key);
        udpClient.shutdown();

        clients.remove(key);

        return true;
    }
}
