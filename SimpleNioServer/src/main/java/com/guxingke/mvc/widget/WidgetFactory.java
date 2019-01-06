package com.guxingke.mvc.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.guxingke.mvc.route.Routes;

/**
 * 组件工厂类，保证组件的单例工厂,typecho4j中的组件均为单例，有状态
 * 
 * @author guxingke
 * */
public class WidgetFactory {

	/**
	 * 组件map
	 * */
	private static Map<Class<? extends Widget>, Widget> widgetMap = new HashMap<Class<? extends Widget>, Widget>();

	private WidgetFactory() {
	}

	/**
	 * 获取分发组件
	 * 
	 * @param dispatcherClass
	 * @return
	 */
	public static DispatcherWidget dispatcher(
			Class<? extends DispatcherWidget> dispatcherClass) {
		if (widgetMap.containsKey(dispatcherClass)) {
			return (DispatcherWidget) widgetMap.get(dispatcherClass);
		}
		return null;
	}

	/**
	 * 获取路由组件
	 * 
	 * @param routeClass
	 * @return
	 */
	public static RouteWidget route(Class<? extends RouteWidget> routeClass) {
		if (widgetMap.containsKey(routeClass)) {
			return (RouteWidget) widgetMap.get(routeClass);
		}
		return null;
	}

	/**
	 * 获取布局组件
	 * 
	 * @param layoutClass
	 * @return
	 */
	public static LayoutWidget layout(Class<? extends LayoutWidget> layoutClass) {
		if (widgetMap.containsKey(layoutClass)) {
			return (LayoutWidget) widgetMap.get(layoutClass);
		}
		return null;
	}

	/**
	 * 获取处理组件
	 * 
	 * @param handleClass
	 * @return
	 */
	public static HandllerWidget handle(
			Class<? extends HandllerWidget> handleClass) {
		if (widgetMap.containsKey(handleClass)) {
			return (HandllerWidget) widgetMap.get(handleClass);
		}
		return null;
	}

	/**
	 * 获取提供组件
	 * 
	 * @param providerClass
	 * @return
	 */
	public static ProviderWidget provider(
			Class<? extends ProviderWidget> providerClass) {
		if (widgetMap.containsKey(providerClass)) {
			return (ProviderWidget) widgetMap.get(providerClass);
		}
		return null;
	}

	/**
	 * 初始化组件池中的组件
	 * 
	 * @param constants
	 */
	public static void initWidget() {
		for (Entry<Class<? extends Widget>, Widget> entry : widgetMap
				.entrySet()) {
			entry.getValue().init();
		}
	}

	/**
	 * 构建组件，需要提供组件的包路径<br/>
	 * 
	 * @param providerPackagePath
	 * @param handlePackagePath
	 * @param layoutPackagePath
	 * @param dispatcherPackagePath
	 * @throws WidgetException
	 */
	public static void buildWidget(List<String> widgetPackageNames)
			throws WidgetException {
		for (String string : widgetPackageNames) {
			doBuilderWidget(string);
		}
	}

	@SuppressWarnings("rawtypes")
	private static void doBuilderWidget(String PackagePath)
			throws WidgetException {
		List<Class> widgetClass = null;

		try {
			widgetClass = ReflectUtil.getClassesByPackageName(PackagePath);
		} catch (Exception e) {
			throw new WidgetException(100, "构建widget出错");
		}

		for (Class clazz : widgetClass) {
			Object temp = null;

			try {
				temp = clazz.newInstance();
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				throw new WidgetException(100, "构建widget出错");
			}

			Widget widget = null;
			if (temp instanceof Widget) {
				widget = (Widget) temp;
			}
			widgetMap.put(widget.getClass(), widget);
		}
	}

	/**
	 * 获取当前组件池中的组件数量
	 * 
	 * @return
	 */
	public static int getMapSize() {
		return widgetMap.size();
	}

	/**
	 * 配置组件池中的route组件的路由设置
	 * 
	 * @param routes
	 */
	public static void configRoute(Routes routes) {
		RouteWidget route;
		for (Entry<Class<? extends Widget>, Widget> entry : widgetMap
				.entrySet()) {
			if (entry.getValue() instanceof RouteWidget) {
				route = (RouteWidget) entry.getValue();
				route.configRoute(routes);
			}
		}
	}
}
