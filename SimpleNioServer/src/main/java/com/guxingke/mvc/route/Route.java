package com.guxingke.mvc.route;

import com.guxingke.mvc.widget.RouteWidget;

/**
 * 路由定义<br/>
 * 
 * @author guxingke
 */
public final class Route {
	/**
	 * 路由响应关键字
	 */
	String responseKey;

	/**
	 * 处理组件类
	 */
	Class<? extends RouteWidget> routeClass;

	/**
	 * 处理组件类型，do组件和其他组件
	 */
	DispathcherType dispathcherType;

	/**
	 * 视图路径
	 */
	String viewPath;

	/**
	 * 响应视图策略，分发和重定向
	 */
	DispatcherStrategy dispatcherStrategy;

	/**
	 * dispatcherStrategy默认设置为转发
	 * 
	 * @param responseKey
	 * @param routeClass
	 * @param widgetType
	 * @param viewPath
	 */
	public Route(String responseKey, Class<? extends RouteWidget> routeClass,
			DispathcherType dispathcherType, String viewPath) {
		super();
		this.responseKey = responseKey;
		this.routeClass = routeClass;
		this.dispathcherType = dispathcherType;
		this.viewPath = viewPath;
		this.dispatcherStrategy = DispatcherStrategy.FORWARD;
	}

	/**
	 * 标准的构造方法
	 * 
	 * @param responseKey
	 * @param routeClass
	 * @param widgetType
	 * @param viewPath
	 * @param dispatcherStrategy
	 */
	public Route(String responseKey, Class<? extends RouteWidget> routeClass,
			DispathcherType dispathcherType, String viewPath,
			DispatcherStrategy dispatcherStrategy) {
		this.responseKey = responseKey;
		this.routeClass = routeClass;
		this.dispathcherType = dispathcherType;
		this.viewPath = viewPath;
		this.dispatcherStrategy = dispatcherStrategy;
	}

	public String getResponseKey() {
		return responseKey;
	}

	public Class<? extends RouteWidget> getRouteClass() {
		return routeClass;
	}

	public DispathcherType getDispathcherType() {
		return dispathcherType;
	}

	public String getViewPath() {
		return viewPath;
	}

	public DispatcherStrategy getDispatcherStrategy() {
		return dispatcherStrategy;
	}
}
