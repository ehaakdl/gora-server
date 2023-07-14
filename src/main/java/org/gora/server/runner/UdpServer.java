package org.gora.server.runner;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.model.CommonData;
import org.gora.server.model.Message;
import org.gora.server.service.UdpClientManager;

@Slf4j
public class UdpServer {
    private final EventLoopGroup bossLoopGroup;
    private final ChannelGroup channelGroup;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UdpServer() {
        this.bossLoopGroup = new NioEventLoopGroup();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public final void startup(int port) throws InterruptedException {
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
                                CommonData commonData;
                                try{
                                    commonData = objectMapper.readValue(bytes, CommonData.class);
                                }catch (Exception e){
                                    log.error("UDP 서버 수신 데이터 치환 실패");
                                    log.error(CommonUtils.getStackTraceElements(e));
                                    return;
                                }

                                if(!UdpClientManager.contain(commonData.getKey())) {
                                    String key = UdpClientManager.connect(clientIp);
                                    commonData.setKey(key);
                                }

                                Receiver.push(commonData);
                            }
                        });
                    }
                });
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelGroup.add(channelFuture.channel());
    }

    public final void shutdown() {
        channelGroup.close();
        bossLoopGroup.shutdownGracefully();
    }

}
