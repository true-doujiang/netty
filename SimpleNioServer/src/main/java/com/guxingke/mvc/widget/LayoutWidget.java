package com.guxingke.mvc.widget;

import java.util.HashSet;
import java.util.Set;

import com.guxingke.mvc.Controller;

/**
 * 所有布局组件的父类
 * 
 * @author guxingke
 */
public abstract class LayoutWidget extends Widget implements ProviderWidget {
	/**
	 * 组件的子组件
	 * */
	protected Set<ProviderWidget> children = new HashSet<ProviderWidget>();

	@Override
	public boolean init() {
		initChirdren();
		return true;
	}

	/**
	 * 添加组件到集合中
	 * 
	 * @param widget
	 */
	@Deprecated
	protected void add(ProviderWidget widget) {
		children.add(widget);
	};

	/**
	 * 添加组件到集合中
	 * 
	 * @param providerClass
	 */
	protected void add(Class<? extends ProviderWidget> providerClass) {
		children.add(WidgetFactory.provider(providerClass));
	};

	/**
	 * 初始化布局组件的子组件集合
	 */
	protected abstract void initChirdren();

	@Override
	public void excute(Controller controller) throws WidgetException {
		for (ProviderWidget widget : children) {
			widget.excute(controller);
		}
	}

	@Override
	public void refresh() {
		for (ProviderWidget widget : children) {
			widget.refresh();
		}
	}

	/**
	 * 刷新布局组件状态，一般是清理组件缓存
	 * 
	 * @param layoutClass
	 */
	public static final void refreshLayout(
			Class<? extends LayoutWidget> layoutClass) {
		WidgetFactory.layout(layoutClass).refresh();
	}
}
