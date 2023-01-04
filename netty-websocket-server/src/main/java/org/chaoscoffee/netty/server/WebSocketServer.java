package org.chaoscoffee.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketServer implements CommandLineRunner {
    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    @Override
    public void run(String... args) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec()); // HTTP 协议解析，用于握手阶段
                        pipeline.addLast(new HttpObjectAggregator(65536)); // HTTP 协议解析，用于握手阶段
                        pipeline.addLast(new WebSocketServerCompressionHandler()); // WebSocket 数据压缩扩展
                        pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true)); // WebSocket 握手、控制帧处理
                        pipeline.addLast(new MyWebSocketServerHandler());
                    }
                });
        ChannelFuture f = b.bind(8083).sync();
        channel = f.channel();
//        f.channel().closeFuture().sync();
    }


    @PreDestroy
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

    class MyWebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
            if (frame instanceof TextWebSocketFrame) { // 此处仅处理 Text Frame
                String request = ((TextWebSocketFrame) frame).text();
                log.info("#### request:{}",request);
                ctx.channel().writeAndFlush(new TextWebSocketFrame("收到: " + request));
            }
        }
    }
}
