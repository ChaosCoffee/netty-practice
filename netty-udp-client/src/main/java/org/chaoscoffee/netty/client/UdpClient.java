package org.chaoscoffee.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.handler.ClientHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
@Slf4j
@Order(value = 1)
public class UdpClient implements DisposableBean, CommandLineRunner {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private Bootstrap bootstrap;

    @Value("${netty.server.host}")
    private String host;

    @Value("${netty.server.port}")
    private Integer port;

    @PostConstruct
    public void init() {
        eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        //创建Bootstrap
        bootstrap = new Bootstrap();
        //指定EventLoopGroup以处理客户端事件;需要适用于NIO的实现;适用于NIO传输的Channel类型
        bootstrap.group(eventLoopGroup)
                .channel(Epoll.isAvailable() ? EpollDatagramChannel.class : NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast(new ClientHandler());
                    }
                });
    }

    public void doStart() {
        try {
            channel = bootstrap.bind(0).sync().channel();
        } catch (Exception e) {
            log.error("Failed to start client", e);
        }
    }

    @Override
    public void destroy() {
        log.info("Shutdown Netty Client...");
        eventLoopGroup.shutdownGracefully();
        log.info("Shutdown Netty Success...");
    }

    @Override
    public void run(String... args) throws Exception {
        doStart();
    }

    public void pushServer(ByteBuf byteBuf) {
        channel.writeAndFlush(
                new DatagramPacket(byteBuf,
                        getCloudAddress()));
    }

    public InetSocketAddress getCloudAddress() {
        return SocketUtils.socketAddress(host, port);
    }
}
