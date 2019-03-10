package com.imooc.netty.ch3;

import com.imooc.netty.ch6.AuthHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author
 */
public final class Server {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            System.out.println("bossGroup: " + bossGroup + "  workerGroup:" + workerGroup);

            ServerHandler serverHandler = new ServerHandler();
            ChannelInitializer<SocketChannel> childHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    AuthHandler authHandler = new AuthHandler();
                    ch.pipeline().addLast(authHandler);
                    //..
                }
            };
            System.out.println("serverHandler: " + serverHandler + "  childHandler:" + childHandler);

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childAttr(AttributeKey.newInstance("childAttr"), "childAttrValue")
                    .handler(serverHandler)
                    .childHandler(childHandler);

            ChannelFuture f = b.bind(8888).sync();

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

       // test();
    }

    private static AtomicBoolean wakenUp = new AtomicBoolean();
    public static void test() throws IOException {

        //getAndSet ：以原子方式设置为给定值，并返回以前的值。
        boolean andSet = wakenUp.getAndSet(true);
        System.out.println(andSet);
        System.out.println(wakenUp);

        //如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。
        //这里需要注意的是这个方法的返回值实际上是是否成功修改，而与之前的值无关。
        boolean b = wakenUp.compareAndSet(false, false);
        System.out.println(b);
        System.out.println(wakenUp);

        Selector selector = Selector.open();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();


    }
}