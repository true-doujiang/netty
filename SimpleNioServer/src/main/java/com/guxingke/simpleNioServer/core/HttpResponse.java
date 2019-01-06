package com.guxingke.simpleNioServer.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse extends Response {

	private final String CTRL = "\r\n";
	private final String server = "SimpleNioServer";

	private String status;
	private String contentType;
	private String content;
	private int contentLength;

	private Map<String, String> header;
	private Map<String, String> cookies;

	public HttpResponse() {
		this.content = "";
		this.status = "HTTP/1.1 200 OK";
		this.contentType = "text/html;charset=UTF-8;";
		header = new HashMap<String, String>();
		cookies = new HashMap<String, String>();
	}

	public void setStatus(int code) {
		String status;
		switch (code) {
		case 200:
			status = "HTTP/1.1 200 OK";
			break;
		case 303:
			status = "HTTP/1.1 303 See Other";
			break;
		case 304:
			status = "HTTP/1.1 304 Not Modified";
			break;
		case 403:
			status = "HTTP/1.1 403 Forbidden";
			break;
		case 404:
			status = "HTTP/1.1 404 Not Found";
			break;
		case 500:
			status = "HTTP/1.1 500 Internal Server Error";
			break;
		default:
			status = "HTTP/1.1 200 OK";
		}
		this.status = status;
	}

	public void setContentLength(int length) {
		this.contentLength = length;
	}

	public void setContentType(String type) {
		this.contentType = type;
	}

	public String findContentType(String type) {
		HashMap<String, String> contentType = new HashMap<String, String>();
		contentType.put("html", "text/html;charset=UTF-8;");
		contentType.put("json", "application/json; charset=utf-8;");
		contentType.put("xml", "application/xml;charset=UTF-8;");
		contentType.put("zip", "application/x-zip-compressed");
		contentType.put("ico", "image/x-icon");
		contentType.put("css", "text/css");
		String returnType = "";
		if (contentType.containsKey(type)) {
			returnType = contentType.get(type);
		} else {
			returnType = "application/x-zip-compressed";
		}
		return returnType;
	}

	public void setContent(String content) {
		this.content = content;
		this.contentLength = content.length();
	}

	public void addContent(String content) {
		this.content = this.content + content;
	}

	/**
	 * 设置cookie
	 */
	public void setCookie(String name, String value, int maxAge, String path,
			String domain) {
		String key = "Set-Cookie";
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value);
		sb.append(";");
		sb.append("Max-Age").append("=").append(maxAge);
		sb.append(";");
		sb.append("path").append("=").append(path);
		sb.append(";");
		sb.append("domian").append("=").append(domain);
		sb.append(";");
		cookies.put(key, sb.toString());
	}

	/**
	 * 设置cookie
	 */
	public void setCookie(String name, String value, int maxAge, String path) {
		String key = "Set-Cookie";
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value);
		sb.append(";");
		sb.append("Max-Age").append("=").append(maxAge);
		sb.append(";");
		sb.append("path").append("=").append(path);
		sb.append(";");
		cookies.put(key, sb.toString());
	}

	/**
	 * 设置cookie
	 */
	public void setCookie(String name, String value, int maxAge) {
		String key = "Set-Cookie";
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value);
		sb.append(";");
		sb.append("Max-Age").append("=").append(maxAge);
		sb.append(";");
		cookies.put(key, sb.toString());
	}

	/**
	 * 设置cookie
	 */
	public void setCookie(String name, String value) {
		String key = "Set-Cookie";
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value);
		sb.append(";");
		cookies.put(key, sb.toString());
	}

	public void addHeader(String key, String value) {
		header.put(key, value);
	}

	/**
	 * 获取此次响应的header
	 *
	 * @return
	 */
	public String getMsgHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(status).append(CTRL);
		sb.append("Content-type: ").append(contentType).append(CTRL);
		sb.append("Content-ength: ").append(contentLength).append(CTRL);
		// 填充自定义Header
		fillHeader(sb);
		// 填充cookie
		fillCookie(sb);
		sb.append("Server:").append(server).append(CTRL).append(CTRL);
		return sb.toString();
	}

	private void fillCookie(StringBuilder sb) {
		for (Entry<String, String> entry : cookies.entrySet()) {
			sb.append(entry.getKey()).append(":").append(entry.getValue());
			sb.append(CTRL);
		}
	}

	private void fillHeader(StringBuilder sb) {
		for (Entry<String, String> entry : header.entrySet()) {
			sb.append(entry.getKey()).append(":").append(entry.getValue())
					.append(CTRL);
		}
	}

	public String getMsgContent() throws UnsupportedEncodingException {
		return this.content;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void send() throws IOException {
		String dataStr = getMsgHeader() + getMsgContent();
		send(dataStr);
	}
}
