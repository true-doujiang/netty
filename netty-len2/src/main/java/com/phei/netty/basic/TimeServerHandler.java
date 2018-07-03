package com.phei.netty.basic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

	//客户端发送请求的时候执行
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(Thread.currentThread().getName() + "====channelRead======");

		//类似于JDKByteBuffer，不过他提供了更加强大和灵活的功能。
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println(Thread.currentThread().getName() + "The time server receive order : " + body);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";

		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		//netty的write方法并不直接将消息写入到SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲数组中，
		// 再通过调用flsh方法，将发送缓冲区中的消息全部写到SocketChannel中。
		ctx.write(resp);
	}

	//客户端断开连接的时候执行
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println(Thread.currentThread().getName() + "====channelReadComplete======");
		//
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println(Thread.currentThread().getName() + "====发生异常了 =======exceptionCaught======");
		ctx.close();
	}
}
