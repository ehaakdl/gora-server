package org.gora.server.component.network;

import java.net.InetSocketAddress;

import org.gora.server.component.network.pipline.UdpPiplineInitializer;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
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
    // 서버,클라이언트 채널 담는곳
    private ChannelGroup recipients;
    private final UdpPiplineInitializer piplineInitializer;
    @Value("${app.udp_accept_event_thread_count}")
    private int threadCount;

    @PostConstruct
    public void init() {
        this.bossLoopGroup = new NioEventLoopGroup(threadCount);
        this.recipients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Async
    public void startup(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(piplineInitializer);

        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        recipients.add(channelFuture.channel());

    }

    public void send(String ip, int port, NetworkPacket packet) {
        recipients.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(packet.toByteArray()),
                new InetSocketAddress(ip, port))).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("udp 송신 실패 (클라이언트 아이피: {})", ip);
                    }
                });
    }

    public void shutdown() {
        bossLoopGroup.shutdownGracefully();
    }

}
