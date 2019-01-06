package com.guxingke.simpleNioServer.event;

import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;

/**
 * 观察者
 */
public interface ServerListener {

	public void onError(String error);

	public void onAccept() throws Exception;

	public void onAccepted(HttpRequest request) throws Exception;

	public void onRead(HttpRequest request) throws Exception;

	public void onWrite(HttpRequest request, HttpResponse response) throws Exception;

	public void onClosed(HttpRequest request) throws Exception;
}
