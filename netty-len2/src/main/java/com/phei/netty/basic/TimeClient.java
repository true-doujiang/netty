package com.phei.netty.basic;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeClient {


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
		new TimeClient().connect(port, "127.0.0.1");
	}


	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new TimeClientHandler());
						}
					});

			// 发起异步连接操作 , TCP连接成功后就会触发channelActive()
			ChannelFuture f = b.connect(host, port).sync();
			System.out.println(Thread.currentThread().getName() + "=====发起异步连接操作=====" + f);

			// 等待客户端链路关闭
			f.channel().closeFuture().sync();
			System.out.println(Thread.currentThread().getName() + "=====等待客户端链路关闭=====" + f);
		} finally {
			// 优雅退出，释放NIO线程组
			group.shutdownGracefully();
			System.out.println(Thread.currentThread().getName() + "====优雅退出，释放NIO线程组======");
		}
	}

}
