package com.guxingke.mvc;

import java.util.Map;

import com.guxingke.mvc.widget.WidgetException;
import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;
import com.guxingke.simpleNioServer.template.FreemakerUtil;

/**
 * 简单封装一下request和response
 * 
 * @author 孤星可
 *
 */
public class Controller {
	private HttpRequest request;
	private HttpResponse response;

	public Controller(HttpRequest request, HttpResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void redirect(String viewPath) {
		response.setStatus(303);
		StringBuilder sb = new StringBuilder();
		if (viewPath.toLowerCase().indexOf("http://") < 0) {
			sb.append("http://");
		}
		sb.append(request.getHeader().get("Host"));
		if (request.getPort() != 80) {
			sb.append(":" + request.getPort());
		}
		if (!viewPath.startsWith("/")) {
			sb.append("/");
		}
		sb.append(viewPath);
		response.addHeader("Location", sb.toString());
	}

	public void forward(String viewPath) throws WidgetException {
		try {
			response.setContent(FreemakerUtil.renderTemplate(getAttrs(),
					viewPath));
		} catch (Exception e) {
			throw new WidgetException(500);
		}
	}

	public Object getArg(String key) {
		return request.getArg(key);
	}

	public void setAttr(String key, Object value) {
		request.setAttr(key, value);
	}

	public Map<String, Object> getAttrs() {
		return request.getAttrs();
	}

	public void render404() {
		response.setStatus(404);
		// TODO 会重构成渲染自定义视图
		response.setContent("404 NOT FOUND");
	}

	public void render500() {
		response.setStatus(500);
		// TODO 会重构成渲染自定义视图
		response.setContent("500 ERROR");
	}
}
