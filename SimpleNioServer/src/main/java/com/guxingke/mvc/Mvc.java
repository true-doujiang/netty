package com.guxingke.mvc;

import com.guxingke.mvc.route.Routes;
import com.guxingke.mvc.widget.WidgetException;
import com.guxingke.mvc.widget.WidgetFactory;
import com.guxingke.simpleNioServer.core.Options;

public final class Mvc {
	/**
	 * 运行时路由
	 */
	private Routes routes;

	private static final Mvc ME = new Mvc();

	private Mvc() {
	}

	public static Mvc getInstance() {
		return ME;
	}

	public void init() throws WidgetException {

		MvcConfig config = new MvcConfig();
		MvcConfig.configMvc(config);

		routes = config.getRoutes();

		WidgetFactory.buildWidget(Options.getPackagePathList());
		// 组件初始化
		WidgetFactory.initWidget();
		// 配置组件自带路由
		WidgetFactory.configRoute(routes);
	}

	public Routes getRoutes() {
		return routes;
	}
}
