# netty-practice
基于SpringBoot Netty tcp,udp,websocket...客户端/服务端Java实践

## Netty优点
Netty提供异步的、事件驱动的网络应用程序框架和工具，用以快速开发高性能、高可靠性的网络服务器和客户端程序。

Netty 是一个基于NIO的客户、服务器端的编程框架，使用Netty 可以确保你快速和简单的开发出一个网络应用，例如实现了某种协议的客户、服务端应用。
Netty相当于简化和流线化了网络应用的编程开发过程，例如：基于TCP和UDP的socket服务开发。

## Java版本
**Java 17**

## 父pom依赖
```xml
            <dependency>
                <groupId>io.netty</groupId>
                <artifactId>netty-all</artifactId>
                <version>4.1.86.Final</version>
            </dependency>
```

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.1</version>
    </parent>
```

## netty-udp-client
- [udp客户端](netty-udp-client/README.md)


## netty-udp-server
- [udp服务端](netty-udp-server/README.md)


## netty-websocket-client
- [websocket客户端](netty-websocket-client/README.md)


## netty-websocket-server
- [websocket服务端](netty-websocket-server/README.md)