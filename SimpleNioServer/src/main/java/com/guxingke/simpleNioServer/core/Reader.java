package com.guxingke.simpleNioServer.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 读线程
 * 
 * @author guxingke
 *
 */
public class Reader extends Thread {

	private static final int BUFFER_SIZE = 1024;

	private static List<SelectionKey> pool = new LinkedList<SelectionKey>();
	private static Notifier notifier = Notifier.getNotifier();

	public Reader(String threadName) {
	    super(threadName);
	}

	public void run() {
		while (true) {
			try {
				SelectionKey key;
                System.out.println(Thread.currentThread().getName() + " pool = " + pool);

				synchronized (pool) {
					while (pool.isEmpty()) {
						pool.wait();
					}
					key = (SelectionKey) pool.remove(0);
				}
				read(key);
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * 读取客户端发出请求数据
	 * 
	 * @param channel
	 *            套接通道
	 */
	public static String readRequest(SocketChannel channel) throws IOException {
		ByteBuffer clientBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		StringBuilder sb = new StringBuilder();

		int count = channel.read(clientBuffer);

		while (count > 0) {
			clientBuffer.flip();
			CharBuffer charBuffer = Charset.forName("UTF-8").decode(clientBuffer);
			sb.append(charBuffer);
			clientBuffer.clear();
			count = channel.read(clientBuffer);
		}
		return sb.toString();
	}

	/**
	 * 处理连接数据读取
	 * 
	 * @param key
	 *            SelectionKey
	 */
	public void read(SelectionKey key) {
		try {
			// 读取客户端数据
			SocketChannel sc = (SocketChannel) key.channel();
			HttpRequest request = (HttpRequest) key.attachment();

			request.setInputData(readRequest(sc));
			request.parseInput();
			// 触发onRead
			notifier.fireOnRead(request);

			// 提交主控线程进行写处理
			SimpleNioServer.processWriteRequest(key);
		} catch (Exception e) {
			notifier.fireOnError("Error occured in Reader: " + e.getMessage());
		}
	}

	/**
	 * 处理客户请求,管理用户的联结池,并唤醒队列中的线程进行处理
	 */
	public static void processRequest(SelectionKey key) {
		synchronized (pool) {
			pool.add(pool.size(), key);
			pool.notifyAll();
		}
	}
}
