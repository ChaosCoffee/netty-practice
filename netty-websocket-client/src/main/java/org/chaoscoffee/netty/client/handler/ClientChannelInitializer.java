package org.chaoscoffee.netty.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @project_name: device-kente-security-alarm
 * @class_name: ServerChannelInitializer
 * @author: alvin-wei
 * @date: 2019-11-23 09:21
 * @description:
 * @modify_description:
 * @version1.0
 */
@Scope("prototype")
@Component
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

	@Autowired
	private ClientHandler clientHandler;

	@Override
	protected void initChannel(Channel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		// 添加一个http的编解码器
		pipeline.addLast(new HttpClientCodec());
		// 添加一个用于支持大数据流的支持
		pipeline.addLast(new ChunkedWriteHandler());
		// 添加一个聚合器，这个聚合器主要是将HttpMessage聚合成FullHttpRequest/Response
		pipeline.addLast(new HttpObjectAggregator(1024 * 64));
		pipeline.addLast(clientHandler);

	}
}
