package com.guxingke.mvc.widget;

import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;

/**
 * 组件分发方法接口
 * 
 * @author guxingke
 *
 */
public interface DispatcherWidget {

	/**
	 * 根据request中携带的信息，分发请求
	 * 
	 * @param request
	 * @param response
	 */
	void dispatcher(HttpRequest request, HttpResponse response);
}
