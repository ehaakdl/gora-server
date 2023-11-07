package org.gora.server.component.network;


import java.net.InetSocketAddress;

import org.gora.server.component.network.pipline.UdpPiplineInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UdpServer {
    // 연결과 패킷을 읽는 스레드
    private EventLoopGroup bossLoopGroup;
    private ChannelGroup recipients;
    private final UdpPiplineInitializer piplineInitializer;
    @Value("${app.udp_accept_event_thread_count}")
    private int threadCount;

    @PostConstruct
    public void init(){
        this.bossLoopGroup = new NioEventLoopGroup(threadCount);
        // todo 알아보기
        this.recipients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Async
    public void startup(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .handler(piplineInitializer);
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        recipients.add(channelFuture.channel());
    }

    public void send(String ip, int port, byte[] data){
        recipients.write(new DatagramPacket(
                    Unpooled.copiedBuffer(data),
                    new InetSocketAddress(ip, port))).addListener(future -> {
                        if(!future.isSuccess()){
                            log.error("udp 송신 실패 (클라이언트 아이피: {})", ip);
                        }
                    });
    }

    public void shutdown() {
        recipients.close();
        bossLoopGroup.shutdownGracefully();
    }

}
