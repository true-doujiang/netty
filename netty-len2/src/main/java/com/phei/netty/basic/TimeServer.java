package com.phei.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeServer {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认值
			}
		}
		new TimeServer().bind(port);
	}


	public void bind(int port) throws Exception {
		// 配置服务端的NIO线程组，专门用于网络事件的处理，实际上他们就是Reactor线程组。
		//一个用于服务端接收客户端的连接，一个用于进行SocketChannel的网络读写。
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)  //相当于JDK nio中的ServerSocketChannel
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChildChannelHandler());
			//绑定IO事件的处理类ChildChannelHandler，他的作用类似于Reactor模式中的Handler类，
            //主要用于处理网络IO事件，例如 记录日志、对消息进行编码等。

			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			System.out.println(Thread.currentThread().getName() + "=====绑定端口，同步等待成功=====" + f);

			// 等待服务端监听端口关闭
            Channel channel = f.channel();
            System.out.println(Thread.currentThread().getName() + "=====等待服务端监听端口关闭=====1==" + f);
            ChannelFuture cf = channel.closeFuture();
            System.out.println(Thread.currentThread().getName() + "=====等待服务端监听端口关闭=====2==" + f);
            cf.sync();//这个方法阻塞
			System.out.println(Thread.currentThread().getName() + "=====等待服务端监听端口关闭=====3==" + f);
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			System.out.println(Thread.currentThread().getName() + "====优雅退出======");
		}
	}


	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		//每次连接都会执行一次
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			System.out.println(Thread.currentThread().getName() + "====ChildChannelHandler.initChannel======");
            ch.pipeline().addLast(new TimeServerHandler());
		}
	}

}
