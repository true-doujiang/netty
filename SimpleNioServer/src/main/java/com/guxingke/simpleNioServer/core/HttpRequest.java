package com.guxingke.simpleNioServer.core;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends Request {

	private Map<String, String> header = new HashMap<String, String>();
	private Map<String, String> arguments = new HashMap<String, String>();
	private Map<String, String> cookies = new HashMap<String, String>();
	private Map<String, Object> attributes = new HashMap<String, Object>();

	private String requestUrl;
	private String method;
	private String queryString;
	private String protocol;

	public void parseInput(String inputData) {
		this.inputData = inputData;
		parseInput();
	}

	public void parseInput() {
		String[] temps = inputData.split("\n");

		// 解析第一行
		doParseFirstLine(temps[0]);

		int offset = 0;
		// 解析header
		for (int i = 1; i < temps.length; i++) {
			String temp = temps[i].trim();

			if (temp.length() < 1) {
				// 空行，header解析结束
				offset = i;
				break;
			}

			String[] splits = temp.split(":");

			if ("cookie".equals(splits[0].toLowerCase())) {
				doParseCookie(splits[1]);
				continue;
			}
			this.header.put(splits[0], splits[1].trim());
		}

		// 解析post内容
		for (int i = offset; i < temps.length; i++) {
			String temp = temps[i].trim();
			if (temp.length() < 1) {
				continue;
			}
			doParseArguments(temp);
		}
	}

	private void doParseArguments(String temp) {
		String[] split = temp.split("&");
		for (int i = 0; i < split.length; i++) {
			String string = split[i];
			String[] kvsplit = string.split("=");
			arguments.put(kvsplit[0], kvsplit[1]);
		}
	}

	private void doParseCookie(String string) {
		String[] temp = string.split(";");
		for (int i = 0; i < temp.length; i++) {
			String[] item = temp[i].trim().split("\\=");
			cookies.put(item[0].trim(), item[1].trim());
		}
	}

	private void doParseFirstLine(String string) {
		String[] temps = string.split(" ");
		this.method = temps[0];
		String[] paths = temps[1].split("\\?");
		this.requestUrl = paths[0];
		if (paths.length > 1) {
			this.queryString = paths[1];
			doParseArguments(queryString);
		}
		this.protocol = temps[2];
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public Map<String, String> getArguments() {
		return arguments;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public String getMethod() {
		return method;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getArg(String key) {
		if (arguments.containsKey(key)) {
			return arguments.get(key);
		}
		return null;
	}

	public void setAttr(String key, Object value) {
		attributes.put(key, value);
	}

	public Map<String, Object> getAttrs() {
		return attributes;
	}

	public String getUrlPara() {
		int start = requestUrl.lastIndexOf("/");
		if (start == 0) {
			return null;
		}
		return requestUrl.substring(start + 1);
	}
}
