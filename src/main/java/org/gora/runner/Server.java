package org.gora.runner;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.gora.pipe.PipelineFactory;

import java.lang.reflect.InvocationTargetException;

public class Server {

    private final EventLoopGroup bossLoopGroup;

    private final ChannelGroup channelGroup;

    private final Class<? extends PipelineFactory> pipelineFactoryClass;

    public Server(Class<? extends PipelineFactory> pipelineFactoryType) {
        // Initialization private members

        this.bossLoopGroup = new NioEventLoopGroup();

        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        this.pipelineFactoryClass = pipelineFactoryType;
    }

    public final void startup(int port) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true);

        PipelineFactory pipelineFactory = pipelineFactoryClass.getDeclaredConstructor().newInstance();

        @SuppressWarnings("rawtypes")
        ChannelInitializer initializer = pipelineFactory.createInitializer();

        bootstrap.handler(initializer);

        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelGroup.add(channelFuture.channel());
    }

    public final void shutdown() {
        channelGroup.close();
        bossLoopGroup.shutdownGracefully();
    }

}
