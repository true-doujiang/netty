package com.guxingke.simpleNioServer.core;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public final class Writer extends Thread {

	private static List<SelectionKey> pool = new LinkedList<SelectionKey>();
	private static Notifier notifier = Notifier.getNotifier();

	public Writer(String threadName) {
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

				write(key);
			} catch (Exception e) {
				continue;
			}
		}
	}

	public void write(SelectionKey key) {
		try {
			SocketChannel sc = (SocketChannel) key.channel();
			HttpResponse response = new HttpResponse();
			response.setSocketChannel(sc);

			notifier.fireOnWrite((HttpRequest) key.attachment(), response);

			sc.finishConnect();
			sc.socket().close();
			sc.close();

			notifier.fireOnClosed((HttpRequest) key.attachment());
		} catch (Exception e) {
			notifier.fireOnError("Error occured in Writer: " + e.getMessage());
		}
	}

	public static void processRequest(SelectionKey key) {
		synchronized (pool) {
			pool.add(pool.size(), key);
			pool.notifyAll();
		}
	}
}
