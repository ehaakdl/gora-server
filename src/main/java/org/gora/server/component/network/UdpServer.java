package org.gora.server.component.network;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.model.CommonData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UdpServer {
    private EventLoopGroup bossLoopGroup;
    private ChannelGroup channelGroup;

    @PostConstruct
    public void init(){
        this.bossLoopGroup = new NioEventLoopGroup();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Async
    public void startup(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(final Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                ByteBuf buf = packet.content();
                                byte[] bytes = new byte[buf.readableBytes()];
                                buf.readBytes(bytes);

                                String clientIp = packet.sender().getHostString();
                                CommonData commonData = CommonData.deserialization(bytes);
                                if(commonData == null){
                                    log.error("UDP 서버 수신 데이터 치환 실패");
                                    return;
                                }

//                                클라이언트 연결여부 체크
                                if(!UdpClientManager.contain(commonData.getKey())) {
                                    String key = UdpClientManager.connect(clientIp);
                                    if(key == null){
                                        log.error("UDP 클라이언트 연결실패했습니다.");
                                        return;
                                    }
                                    commonData.setKey(key);
                                }

//                                todo 테스트 용도
                                PacketSender.push(commonData);
                            }
                        });
                    }
                });
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelGroup.add(channelFuture.channel());
    }

    public void shutdown() {
        channelGroup.close();
        bossLoopGroup.shutdownGracefully();
    }

}
