package com.guxingke.simpleNioServer.handler;

import java.io.IOException;
import java.util.Map;

import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;
import com.guxingke.simpleNioServer.template.FreemakerUtil;

import freemarker.template.TemplateException;

public abstract class Handler {

	protected HttpRequest request;
	protected HttpResponse response;

	public void init(HttpRequest request, HttpResponse response) {
		this.request = request;
		this.response = response;
	}

	protected Object getArg(String key) {
		return request.getArg(key);
	}

	protected void setAttr(String key, Object value) {
		request.setAttr(key, value);
	}

	protected Map<String, Object> getAttrs() {
		return request.getAttrs();
	}


	protected void render(String templateName) {
		try {
			response.setContent(FreemakerUtil.renderTemplate(request, templateName));
		} catch (IOException | TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void redirect(String url) {
		response.setStatus(303);

		StringBuilder sb = new StringBuilder();
		if (url.toLowerCase().indexOf("http://") < 0) {
			sb.append("http://");
		}
		sb.append(request.getHeader().get("Host"));

		if (request.getPort() != 80) {
			sb.append(":" + request.getPort());
		}

		if (!url.startsWith("/")) {
			sb.append("/");
		}

		sb.append(url);
		response.addHeader("Location", sb.toString());
	}

    // 获取单个资源
    public abstract void get(Object id);

    // 列表获取资源
    public abstract void get();

    // 更新资源
    public abstract void put();

    // 删除资源
    public abstract void delete(Object id);

    // 新建资源
    public abstract void post();


}
