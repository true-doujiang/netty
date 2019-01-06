package com.guxingke.simpleNioServer.core;

import java.util.ArrayList;

import com.guxingke.simpleNioServer.event.ServerListener;

/**
 * 统治者
 */
public class Notifier {

	private static ArrayList<ServerListener> listeners = null;
	private static Notifier instance = null;

	private Notifier() {
		listeners = new ArrayList<ServerListener>();
	}

    /**
     *  单例
     * @return
     */
	public static synchronized Notifier getNotifier() {
		if (instance == null) {
			instance = new Notifier();
			return instance;
		} else
			return instance;
	}

    /**
     * 添加观察者
     * @param l
     */
	public void addListener(ServerListener l) {
		synchronized (listeners) {
			if (!listeners.contains(l))
				listeners.add(l);
		}
	}

	public void fireOnAccept() throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onAccept();
	}

	public void fireOnAccepted(HttpRequest request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onAccepted(request);
	}

	void fireOnRead(HttpRequest request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onRead(request);

	}

	void fireOnWrite(HttpRequest request, HttpResponse response) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onWrite(request, response);

	}

	public void fireOnClosed(HttpRequest request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onClosed(request);
	}

	public void fireOnError(String error) {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onError(error);
	}
}
