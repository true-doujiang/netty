/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phei.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Administrator
 * @date 2014年2月16日
 * @version 1.0
 */
public class TimeClientHandle implements Runnable {

	private String host;
	private int port;

	private Selector selector;
	private SocketChannel socketChannel;

	private volatile boolean stop;

	public TimeClientHandle(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + "=====run======");
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!stop) {
			try {
				System.out.println(Thread.currentThread().getName() + "=====run while======" + new Date().getSeconds());

				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		// 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//异步连接
	private void doConnect() throws IOException {
		// 如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			System.out.println(Thread.currentThread().getName() + "=====直接连接成功，则注册到多路复用器上，发送请求消息，读应答======");
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		} else {
			//没有直接连接成功，则说明服务端没有返回TCP握手应答消息，但这并不代表连接失败。
			//我们需要将socketChannel注册到多路复用器selector上，注册SelectionKey.OP_CONNECT,
			//当服务端返回TCP syn-ack消息后，selector就会轮询到socketChannel处于连接就绪状态。
			System.out.println(Thread.currentThread().getName() + "=====没有直接连接成功，则说明服务端没有返回TCP握手应答消息，但这并不代表连接失败======");
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			// 判断是否连接成功
			SocketChannel sc = (SocketChannel) key.channel();

			if (key.isConnectable()) {
				if (sc.finishConnect()) {
					System.out.println(Thread.currentThread().getName() + "=====连接成功======");
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				} else {
					System.out.println(Thread.currentThread().getName() + "=====连接失败，进程退出======");
					// 连接失败，进程退出
					System.exit(1);
				}
			}

			if (key.isReadable()) {
				System.out.println(Thread.currentThread().getName() + "=====isReadable======");

				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					System.out.println(Thread.currentThread().getName() + "=====isReadable readBytes====== " + readBytes);

					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("Now is : " + body);
					this.stop = true; //退出while循环 线程结束了
				} else if (readBytes < 0) {
					System.out.println(Thread.currentThread().getName() + "=====对端链路关闭======");
					// 对端链路关闭
					key.cancel();
					sc.close();
				} else {
					System.out.println(Thread.currentThread().getName() + "=====读到0字节，忽略======");
					; // 读到0字节，忽略
				}
			}
		} else {
			System.out.println(Thread.currentThread().getName() + "=====selectionkey false======");
		}
	}

	private void doWrite(SocketChannel sc) throws IOException {
		System.out.println(Thread.currentThread().getName() + "=====doWrite======");
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {
			System.out.println("Send order 2 server succeed.");
		}
	}

}
