package com.guxingke.mvc.widget;

import com.guxingke.mvc.route.Routes;

/**
 * 路由处理接口 <br/>
 * 如果某个widget需要具备路由功能，则实现此接口
 * 
 * @author guxingke
 *
 */
public interface RouteWidget {

	/**
	 * 注册路由到系统路由中
	 * 
	 * @param routes
	 */
	void configRoute(Routes routes);
}
