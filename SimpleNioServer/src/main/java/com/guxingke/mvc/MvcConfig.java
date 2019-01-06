package com.guxingke.mvc;

import com.guxingke.mvc.route.Routes;

public class MvcConfig {
	private final static Routes routes = new Routes();

	static void configMvc(MvcConfig config) {
		config.configOptions();
		config.configRoutes(routes);
	}

	protected void configRoutes(Routes rts) {
	}

	protected void configOptions() {
	}

	public Routes getRoutes() {
		return routes;
	}
}
