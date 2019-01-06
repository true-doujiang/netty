package com.guxingke.mvc.widget;

import com.guxingke.mvc.Controller;

/**
 * widget作为提供者的接口
 * 
 * @author guxingke
 * */
public interface ProviderWidget {

	/**
	 * 执行操作
	 * */
	void excute(Controller controller) throws WidgetException;

	/**
	 * 刷新widget状态
	 * */
	void refresh();
}
