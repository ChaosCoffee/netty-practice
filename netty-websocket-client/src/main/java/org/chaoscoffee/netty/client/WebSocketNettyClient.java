package org.chaoscoffee.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.client.handler.ClientChannelInitializer;
import org.chaoscoffee.netty.client.handler.ClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WebSocketNettyClient implements CommandLineRunner {

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private Channel channel;

    @Autowired
    private ClientChannelInitializer clientChannelInitializer;

    @Autowired
    private ClientHandler clientHandler;

    @PostConstruct
    public void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(clientChannelInitializer);
    }

    @Override
    public void run(String... args) throws Exception {
        doConnect();
    }

    public void doConnect() {
        try {
            URI websocketURI = new URI("ws://127.0.0.1:8583");
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            //进行握手
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI,
                    WebSocketVersion.V13, null, true, httpHeaders);
            ChannelFuture future = bootstrap.connect(websocketURI.getHost(), websocketURI.getPort());
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    channel = channelFuture.channel();
                    clientHandler.setHandshaker(handshaker);
//                    handshaker.handshake(channel);
                    log.info("Websocket client connect success");
                    //阻塞等待是否握手成功
//                    clientHandler.handshakeFuture();
//                    log.info("Websocket handshake success!");
                } else {
                    log.info("Websocket client reconnect");
                    channelFuture.channel().eventLoop().schedule(this::doConnect, 5, TimeUnit.SECONDS);
                }
            });

        } catch (Exception e) {
            log.error("Netty client start error:", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }


}
