package org.chaoscoffee.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.model.dto.UDPMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @project_name: device-kente-security-alarm
 * @class_name: ServerChannelInitializer
 * @author: alvin-wei
 * @date: 2019-11-23 09:21
 * @description:
 * @modify_description:
 * @version1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DecoderHandler extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf byteBuf = datagramPacket.content();
        String HEXES = "0123456789ABCDEF";
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        final StringBuilder hex = new StringBuilder(2 * req.length);
        for (byte b : req) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        String udp = hex.toString();
        log.info(" [ udp msg ] : {}", udp);
        try {
            UDPMessage message = new UDPMessage();
            message.setValue(Integer.parseInt(udp, 16));
            out.add(message);
        } catch (Exception e) {
            log.error("convert error", e);
        }
    }
}
