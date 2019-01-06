package com.guxingke.simpleNioServer.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;
import com.guxingke.simpleNioServer.handler.Handler;

public class DispathcherObserver extends EventAdapter {


	private Map<String, Class<? extends Handler>> mappings = new HashMap<String, Class<? extends Handler>>();

	private static DispathcherObserver me = new DispathcherObserver();


	private DispathcherObserver() { }

    /**
     * 单例
     * @return
     */
	public static DispathcherObserver getDispathcher() {
		return me;
	}

	public void addMapping(String resourceName, Class<? extends Handler> handler) {
		mappings.put(resourceName, handler);
	}

	@Override
	public void onWrite(HttpRequest request, HttpResponse response) {

		// 1.根据预先配置的mapping和requestUrl，匹配到对应的handler
		Class<? extends Handler> handlerClass = getHandler(request.getRequestUrl());
		if (handlerClass == null) {
			response.setStatus(404);
			response.setContent("404 NOT FOUND");

			try {
				response.send();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		Method userDefindedMethod = getUserDefindedMethod(request.getUrlPara(), handlerClass);
		Handler handler = null;
		try {
			handler = getHandler(handlerClass);
		} catch (InstantiationException | IllegalAccessException e) {
			handler = null;
		}

		if (handler == null) {
			response.setStatus(404);
		} else if (userDefindedMethod != null) {
			handler.init(request, response);
			try {
				userDefindedMethod.invoke(handler);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			handler.init(request, response);
			// 2.调用handler中对应的方法
			String method = getMethod(request);
			String id = request.getUrlPara();

			if (id != null) {
				if ("get".equals(method)) {
					handler.get(id);
				} else {
					handler.delete(id);
				}
			} else {
				if ("get".equals(method)) {
					handler.get();
				} else if ("put".equals(method)) {
					handler.put();
				} else {
					handler.post();
				}
			}
		}

		try {
			response.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler getHandler(Class<? extends Handler> handlerClass)
			            throws InstantiationException, IllegalAccessException {
		return handlerClass.newInstance();
	}

	/**
	 * 对应handler内是否有对应的无参公开方法
	 * 
	 * @param methodName
	 * @param handlerClass
	 * @return
	 */
	private Method getUserDefindedMethod(String methodName, Class<? extends Handler> handlerClass) {
		Method[] methods = handlerClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	private String getMethod(HttpRequest request) {
		String method = null;
		if (request.getArg("_method") != null) {
			// 前台请求时须带参数，put-put，get/id-get,get-list,post-post,delete-delete
			method = request.getArg("_method");
		} else {
			method = request.getMethod();
		}
		return method.toLowerCase();
	}

	private Class<? extends Handler> getHandler(String requestUrl) {
		String resourceName = getResoureName(requestUrl);
		return mappings.get(resourceName);
	}

	private String getResoureName(String requestUrl) {
		int start = requestUrl.indexOf("/");
		int end = requestUrl.lastIndexOf("/");
		if (end == 0) {
			return requestUrl.substring(start + 1);
		}
		return requestUrl.substring(start + 1, end);
	}

}
