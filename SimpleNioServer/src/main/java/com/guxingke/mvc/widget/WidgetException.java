package com.guxingke.mvc.widget;

/**
 * 自定义异常类
 * 
 * @author guxingke
 * 
 * */
public class WidgetException extends Exception {

	private static final long serialVersionUID = -1202363377313967978L;

	private final Integer errorCode;

	public WidgetException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public WidgetException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public WidgetException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public Integer getErrorCode() {
		return errorCode;
	}
}
