package com.guxingke.simpleNioServer.core;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.io.IOException;

public abstract class Response {

	protected SocketChannel socketChannel;

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	/**
	 * 向客户端写数据
	 * 
	 */
	public void send(String dataStr) throws IOException {
		byte[] data = dataStr.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data, 0, data.length);
		buffer.flip();
		socketChannel.write(buffer);
		buffer.clear();

		// socketChannel.finishConnect();
		// socketChannel.socket().close();
		// socketChannel.close();
	}
}
