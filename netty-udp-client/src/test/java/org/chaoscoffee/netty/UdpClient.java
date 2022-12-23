package org.chaoscoffee.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
import org.chaoscoffee.netty.utils.HexConvert;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetSocketAddress;

@Slf4j
public class UdpClient implements DisposableBean {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private Bootstrap bootstrap;

    //local
    private String host = "127.0.0.1";

    private Integer port = 31206;

    public static void main(String[] args) {
        UdpClient client = new UdpClient();
        client.init();
    }

    //	@PostConstruct
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
                    }
                })
        ;
        doStart();

    }

    public void doStart() {
        try {
            channel = bootstrap.bind(0).sync().channel();
            //16进制
//            testPush("48454152540098A72800220098A728C0A8021101000000000000000001323233000020");
            testPush("48454152540098C1A800330098C1A8C0A8000403000003FF000002800156322E300020");
//            testPush("BC010000A00098C191FFFFFFFFFFFFFFFF0018000300543030302A41410C3232303630313037313932322A42420200042A4242040098C1912A424204000000082A42420200132A424204000000082A42420200132A424204000004002A424204000000002A4242020020234DFD");
            Thread.sleep(5 * 60 * 1000);
        } catch (Exception e) {
            log.error("Failed to start client", e);
        }
//
//		ChannelFuture future = bootstrap.bind(0);
//		future.addListener((ChannelFutureListener) futureListener -> {
//			if (futureListener.isSuccess()) {
//				channel = futureListener.channel();
//				log.info("Connect to server successfully!-> host:{},port:{}", host, port);
//			} else {
//				log.info("Failed to connect to server, try connect after 5s-> host:{},:port:{}", host, port);
//				futureListener.channel().eventLoop().schedule(this::doConnect, 5, TimeUnit.SECONDS);
//			}
//		});
    }

    @Override
    public void destroy() {
        log.info("Shutdown Netty Client...");
        eventLoopGroup.shutdownGracefully();
        log.info("Shutdown Netty Success...");
    }


    private void testPush(String msg) {
        byte[] hexStringToBytes = HexConvert.hexStringToBytes(msg);
        channel.writeAndFlush(
                new DatagramPacket(Unpooled.wrappedBuffer(hexStringToBytes),
                        getCloudAddress()));
    }


    public void pushCloud(ByteBuf byteBuf) {
        channel.writeAndFlush(
                new DatagramPacket(byteBuf,
                        getCloudAddress()));
    }

    public InetSocketAddress getCloudAddress() {
        return SocketUtils.socketAddress(host, port);
    }
}
