package com.yhh.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jq on 2019/8/6.
 */

@Component
public class NettyTcpClient {

    private static final Logger log = LoggerFactory.getLogger(NettyTcpClient.class);


    @Autowired
    ClientChannelInitializer clientChannelInitializer;

    //与服务端建立连接后得到的通道对象
    private Channel channel;

    /**
     * 初始化 `Bootstrap` 客户端引导程序
     *
     * @return
     */
    private final Bootstrap getBootstrap() {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioSocketChannel.class)//通道连接者
                .handler(clientChannelInitializer)//通道处理者
                .option(ChannelOption.SO_KEEPALIVE, true)  //心跳报活
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535));
        return b;
    }

    /**
     * 建立连接，获取连接通道对象
     *
     * @return
     */
    public void connect() {
        ChannelFuture channelFuture = getBootstrap().connect("127.0.0.1", 6666).syncUninterruptibly();

        if (channelFuture != null && channelFuture.isSuccess()) {
            channel = channelFuture.channel();
            log.info("connect tcp server host = {}, port = {} success", "127.0.0.1", 6666);
        } else {
            log.error("connect tcp server host = {}, port = {} fail", "127.0.0.1", 6666);
        }
    }

    /**
     * 向服务器发送消息
     *
     * @param msg
     * @throws Exception
     */
    public void sendMsg(Object msg) throws Exception {
        if (channel != null) {
            channel.writeAndFlush(msg).sync();
        } else {
            log.warn("消息发送失败,连接尚未建立!");
        }
    }


}
