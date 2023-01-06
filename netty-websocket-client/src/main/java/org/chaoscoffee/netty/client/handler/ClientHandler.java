package org.chaoscoffee.netty.client.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.client.WebSocketNettyClient;
import org.chaoscoffee.netty.client.utils.SpringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 握手协议返回，设置结束握手
        if (!this.handshaker.isHandshakeComplete()) {
            FullHttpResponse response = (FullHttpResponse) msg;
            this.handshaker.finishHandshake(ctx.channel(), response);
            this.handshakeFuture.setSuccess();
            log.info("WebSocketClientHandler::channelRead0 HandshakeComplete...");
            return;
        }
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) msg;
            log.info("WebSocketClientHandler::channelRead0 textFrame: {} ", textFrame.text());
        }
    }

    /**
     * 当客户端主动链接服务端的链接后，调用此方法
     *
     * @param channelHandlerContext ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        handshaker.handshake(ctx.channel());
//        handlerAdded(ctx);
    }

    /**
     * 客户端与服务器端断开连接的时候调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("websocket client disconnected.");
        super.channelInactive(ctx);
        SpringUtils.getBean(WebSocketNettyClient.class).doConnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("exceptionCaught: {}" ,cause.getMessage());
        ctx.close();
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }


}
