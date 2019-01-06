package com.guxingke.mvc.widget;

/**
 * widget抽象类，所有组件的父类<br/>
 * widget 拥有数据，拥有数据 的操作方法
 * 
 * @author guxingke
 * */
public abstract class Widget {
	/**
	 * init方法是widget生命周期的一部分，typecho4j会在程序初始化时调用widget的init方法
	 * */
	public boolean init() {
		return true;
	}
}
