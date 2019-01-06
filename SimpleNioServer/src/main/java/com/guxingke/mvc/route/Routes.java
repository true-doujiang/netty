package com.guxingke.mvc.route;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 路由容器
 * 
 * @author guxingke
 */
public final class Routes {

	private Map<String, Route> routeMap = new HashMap<String, Route>();

	public boolean addRoute(Route route) {
		routeMap.put(route.getResponseKey()
				+ route.getDispathcherType().toString(), route);
		return true;
	}

	public Route getRoute(String responseKey) {
		return routeMap.get(responseKey);
	}
}
