package org.chaoscoffee.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
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
public class ServerChannelInitializer extends ChannelInitializer<Channel> {

	@Autowired
	private MyDispatcherHandler myDispatcherHandler;

	@Autowired
	private DecoderHandler decoderHandler;

	@Override
	protected void initChannel(Channel socketChannel) throws Exception {
		ChannelPipeline p = socketChannel.pipeline();
		p.addLast(decoderHandler);
		p.addLast(myDispatcherHandler);

	}
}
