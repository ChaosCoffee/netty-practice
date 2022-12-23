package org.chaoscoffee.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.handler.ServerChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class UdpServer implements CommandLineRunner {

    @Autowired
    private ServerChannelInitializer channelInitializer;

    private EventLoopGroup bossGroup;

    private Bootstrap b;

    private Channel channel;

    private static Channel c;

    @Value("${netty.port}")
    private Integer port;

    @PostConstruct
    public void init() {
        bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(bossGroup)
                .channel(Epoll.isAvailable() ? EpollDatagramChannel.class : NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(channelInitializer);
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            InetSocketAddress address = new InetSocketAddress(port);
            channel = b.bind(address).sync().channel();
            c = channel;
            log.info("Netty server listening " + address.getHostName() + " on port " + address.getPort()
                    + " and ready for connections...");
        } catch (Exception e) {
            log.error("Netty start error:", e);
        }
    }
}
