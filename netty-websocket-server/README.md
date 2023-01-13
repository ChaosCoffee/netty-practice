# Websocket介绍
1. 首先，客户端发起http请求，经过3次握手后，建立起TCP连接；http请求里存放WebSocket支持的版本号等信息，如：Upgrade、Connection、WebSocket-Version等；
2. 然后，服务器收到客户端的握手请求后，同样采用HTTP协议回馈数据；
3. 最后，客户端收到连接成功的消息后，开始借助于TCP传输信道进行全双工通信。

## Websocket协议
WebSocket是一种在单个TCP连接上进行全双工通信的协议。WebSocket通信协议于2011年被IETF定为标准RFC 6455，并由RFC7936补充规范。WebSocket API也被W3C定为标准。
WebSocket使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。在WebSocket API中，浏览器和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。

### 客户端
```text
GET ws://localhost:3000/ws/chat HTTP/1.1
Host: localhost
Upgrade: websocket
Connection: Upgrade
Origin: http://localhost:3000
Sec-WebSocket-Key: client-random-string
Sec-WebSocket-Version: 13
```

Websocket 通过HTTP/1.1 协议的101状态码进行握手。
该请求和普通的HTTP请求有几点不同：
1. GET请求的地址不是类似/path/，而是以ws://开头的地址；
2. 请求头Upgrade: websocket和Connection: Upgrade表示这个连接将要被转换为WebSocket连接；
3. Sec-WebSocket-Key是用于标识这个连接，并非用于加密数据；
4. Sec-WebSocket-Version指定了WebSocket的协议版本。

### 服务端
服务器如果接受该请求，就会返回如下响应：

```text
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: server-random-string
```

该响应代码101表示本次连接的HTTP协议即将被更改，更改后的协议就是Upgrade: websocket指定的WebSocket协议。

版本号和子协议规定了双方能理解的数据格式，以及是否支持压缩等等。如果仅使用WebSocket的API，就不需要关心这些。


## Websocket解决的问题
1. http存在的问题
   http是一种无状态协议，每当一次会话完成后，服务端都不知道下一次的客户端是谁，需要每次知道对方是谁，才进行相应的响应，因此本身对于实时通讯就是一种极大的障碍
   http协议采用一次请求，一次响应，每次请求和响应就携带有大量的header头，对于实时通讯来说，解析请求头也是需要一定的时间，因此，效率也更低下
   最重要的是，需要客户端主动发，服务端被动发，也就是一次请求，一次响应，不能实现主动发送

2. long poll(长轮询)
   对于以上情况就出现了http解决的第一个方法——长轮询
   基于http的特性，简单点说，就是客户端发起长轮询，如果服务端的数据没有发生变更，会 hold 住请求，直到服务端的数据发生变化，或者等待一定时间超时才会返回。返回后，客户端又会立即再次发起下一次长轮询
   优点是解决了http不能实时更新的弊端，因为这个时间很短，发起请求即处理请求返回响应，实现了“伪·长连接”
   张三取快递的例子，张三今天一定要取到快递，他就一直站在快递点，等待快递一到，立马取走