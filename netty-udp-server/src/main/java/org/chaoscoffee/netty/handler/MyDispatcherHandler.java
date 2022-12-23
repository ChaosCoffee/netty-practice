package org.chaoscoffee.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.model.dto.UDPMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class MyDispatcherHandler extends SimpleChannelInboundHandler<UDPMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDPMessage message) throws Exception {
        log.info("[ business  message ]: {}", message);

    }
}
