package com.guxingke.simpleNioServer.core;

import java.nio.channels.SocketChannel;

public abstract class Request {

	protected SocketChannel socketChannel;
	protected String inputData = null;

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public String getInputData() {
		return inputData;
	}

	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	public int getPort() {
		return socketChannel.socket().getLocalPort();
	}
}
