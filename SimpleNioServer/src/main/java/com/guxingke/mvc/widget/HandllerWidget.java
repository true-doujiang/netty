package com.guxingke.mvc.widget;

import com.guxingke.mvc.Controller;

/**
 * 组件处理方法接口<br/>
 * 要处理请求则必须实现widgetRoute接口
 * 
 * @author guxingke
 *
 */
public interface HandllerWidget extends RouteWidget {
	/**
	 * handle
	 */
	void handle(Controller controller) throws WidgetException;
}
