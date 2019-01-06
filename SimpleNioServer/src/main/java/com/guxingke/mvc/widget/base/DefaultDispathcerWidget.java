package com.guxingke.mvc.widget.base;

import java.io.IOException;

import com.guxingke.mvc.Controller;
import com.guxingke.mvc.route.DispatcherStrategy;
import com.guxingke.mvc.route.DispathcherType;
import com.guxingke.mvc.route.Route;
import com.guxingke.mvc.route.Routes;
import com.guxingke.mvc.widget.DispatcherWidget;
import com.guxingke.mvc.widget.HandllerWidget;
import com.guxingke.mvc.widget.LayoutWidget;
import com.guxingke.mvc.widget.RouteWidget;
import com.guxingke.mvc.widget.WidgetException;
import com.guxingke.mvc.widget.WidgetFactory;
import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.HttpResponse;
import com.guxingke.simpleNioServer.event.EventAdapter;

/**
 * COC<br/>
 * 分发规则：<br/>
 * 1.xxx/admin/yy -->后台<br/>
 * 2.xxx/yy -->前台<br/>
 * 3.xxx/user -->登陆 <br/>
 * 4.xxx/do/zzz -->前台处理<br/>
 * 5.xxx/admin/do/zzz -->后台处理<br/>
 * 
 * @author 孤星可
 *
 */
public class DefaultDispathcerWidget extends EventAdapter implements
		DispatcherWidget {
	protected Routes routes;

	private final String USER = "/user";
	private final String BACK = "/admin";
	private final String BACK_DO = "/admin/do";
	private final String FRONT_DO = "/do";

	public DefaultDispathcerWidget(Routes routes) {
		this.routes = routes;
	}

	@Override
	public void onWrite(HttpRequest request, HttpResponse response)
			throws Exception {
		dispatcher(request, response);

		try {
			response.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dispatcher(HttpRequest request, HttpResponse response) {

		Route route = findRoute(request.getRequestUrl());
		Controller controller = new Controller(request, response);

		try {
			handle(route, controller);
		} catch (WidgetException e) {
			if (e.getErrorCode() == 404) {
				controller.render404();
			} else {
				controller.render500();
			}
		}

	}

	private Route findRoute(String requestUrl) {
		DispathcherType type = getDispathcherType(requestUrl);
		String target = getTargetUrl(type, requestUrl);
		String responseKey = null;
		int endIndex = target.lastIndexOf("/");
		if (endIndex == 0) {
			responseKey = target.substring(1);
		} else {
			responseKey = target.substring(1, endIndex);
		}
		return routes.getRoute(responseKey + type.toString());
	}

	private String getTargetUrl(DispathcherType type, String requestUrl) {

		String result = null;
		switch (type) {
		case BACK:
			result = requestUrl.substring(BACK.length());
			break;
		case BACK_DO:
			result = requestUrl.substring(BACK_DO.length());
			break;
		case USER:
			result = requestUrl.substring(USER.length());
			break;
		case FRONT:
			result = requestUrl;
			break;
		case FRONT_DO:
			result = requestUrl.substring(FRONT_DO.length());
			break;
		}
		return result;
	}

	private DispathcherType getDispathcherType(String requestUrl) {
		if (requestUrl.startsWith(BACK_DO)) {
			return DispathcherType.BACK_DO;
		} else if (requestUrl.startsWith(BACK)) {
			return DispathcherType.BACK;
		} else if (requestUrl.startsWith(FRONT_DO)) {
			return DispathcherType.FRONT_DO;
		} else if (requestUrl.startsWith(USER)) {
			return DispathcherType.USER;
		} else {
			return DispathcherType.FRONT;
		}
	}

	/**
	 * 
	 * @param route
	 * @param response
	 * @param request
	 * @throws WidgetException
	 */
	private void handle(Route route, Controller controller)
			throws WidgetException {
		if (route == null) {
			throw new WidgetException(404);
		}

		dohandle(route.getRouteClass(), controller);

		// 如果路由中viewPath为null,则说明render会被处理组件调用
		if (route.getDispatcherStrategy() == DispatcherStrategy.SELF
				|| route.getDispatcherStrategy() == null) {
			return;
		} else if (route.getDispatcherStrategy() == DispatcherStrategy.FORWARD) {
			controller.forward(route.getViewPath());
		} else {
			controller.redirect(route.getViewPath());
		}
	}

	private void dohandle(Class<? extends RouteWidget> routeClass,
			Controller controller) throws WidgetException {
		RouteWidget route = WidgetFactory.route(routeClass);

		try {
			LayoutWidget layout = (LayoutWidget) route;
			layout.excute(controller);
		} catch (ClassCastException exception) {
			HandllerWidget handle = (HandllerWidget) route;
			handle.handle(controller);
		}
	}
}
