package com.guxingke.simpleNioServer.core;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SimpleNioServer implements Runnable {

	private static List<SelectionKey> wpool = new LinkedList<SelectionKey>();

	private static Selector selector;
	private ServerSocketChannel sschannel;
	private InetSocketAddress address;
	protected Notifier notifier;
	private int port;

	private static int MAX_THREADS = 4;

	public SimpleNioServer(int port) throws Exception {
		this.port = port;

		notifier = Notifier.getNotifier();

		for (int i = 0; i < MAX_THREADS; i++) {
			Thread r = new Reader("Reader-" + (i + 1));
			Thread w = new Writer("Writer-" + (i + 1));
			r.start();
			w.start();
		}

		selector = Selector.open();
		sschannel = ServerSocketChannel.open();
		address = new InetSocketAddress(port);
		ServerSocket ss = sschannel.socket();
		ss.bind(address);
		sschannel.configureBlocking(false);
		sschannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void run() {
		System.out.println("Server started ...");
		System.out.println("Server listening on port: " + port);
		while (true) {
			try {

                System.out.println(Thread.currentThread().getName() + " select 1");

                int num = 0;
				num = selector.select();

                System.out.println(Thread.currentThread().getName() + " select 2 num = " + num);

				if (num > 0) {
					Set<SelectionKey> selectedKeys = selector.selectedKeys();

					for (SelectionKey key : selectedKeys) {
						selectedKeys.remove(key);

						Object attach = key.attachment();
						// 如果有正在读取静态文件的标记就返回
						if (attach != null && attach.equals(0)) {
							continue;
						}

						if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
							ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
							notifier.fireOnAccept();

							SocketChannel sc = ssc.accept();
							sc.configureBlocking(false);

							HttpRequest request = new HttpRequest();
							request.setSocketChannel(sc);
							notifier.fireOnAccepted(request);

							sc.register(selector, SelectionKey.OP_READ, request);
						} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
							Reader.processRequest(key);
							key.cancel();

						} else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
							HttpRequest request = (HttpRequest) key.attachment();

							if (isAccessFile(request.getRequestUrl())) {
								HttpResponse response = new HttpResponse();
								StaticServer server = new StaticServer(request, response);
								SocketChannel socketChannel = request.getSocketChannel();
								response.setSocketChannel(socketChannel);

								if (server.isFileExist()) {
									server.read();
									key.attach(0);// 这个标记是正在读取资源文件
									continue;// 读取下一个循环
								} else {
									response.setStatus(404);
									response.send();
								}
							} else {
								Writer.processRequest(key);
							}
							key.cancel();
						}
					}
				} else {
					addRegister();
				}
			} catch (Exception e) {
				notifier.fireOnError("Error occured in Server: " + e.getMessage());
				continue;
			}
		}
	}

	private void addRegister() {
		synchronized (wpool) {
			while (!wpool.isEmpty()) {
				SelectionKey key = (SelectionKey) wpool.remove(0);
				SocketChannel schannel = (SocketChannel) key.channel();
				try {
					schannel.register(selector, SelectionKey.OP_WRITE, key.attachment());
				} catch (Exception e) {
					try {
						schannel.finishConnect();
						schannel.close();
						schannel.socket().close();
						notifier.fireOnClosed((HttpRequest) key.attachment());
					} catch (Exception e1) {

					}
					notifier.fireOnError("Error occured in addRegister: " + e.getMessage());
				}
			}
		}
	}

	public static void processWriteRequest(SelectionKey key) {
		synchronized (wpool) {
			wpool.add(key);
			wpool.notifyAll();
		}
		selector.wakeup();
	}

	private boolean isAccessFile(String requestUrl) {
		if (requestUrl.equals("/favicon.ico")
				|| requestUrl.equals("robots.txt")
				|| requestUrl.toLowerCase().substring(1).indexOf(Options.getResourcesFlag()) == 0) {
			return true;
		}
		return false;
	}
}
