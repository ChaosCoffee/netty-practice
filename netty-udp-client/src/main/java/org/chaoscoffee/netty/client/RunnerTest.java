package org.chaoscoffee.netty.client;

import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.chaoscoffee.netty.utils.HexConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
@Slf4j
public class RunnerTest implements ApplicationRunner {

    @Autowired
    private UdpClient udpClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String msg = "0xfff";
        byte[] hexStringToBytes = HexConvert.hexStringToBytes(msg);
        udpClient.pushServer(Unpooled.wrappedBuffer(hexStringToBytes));
        log.info("client send udp msg: {}", msg);
    }
}
