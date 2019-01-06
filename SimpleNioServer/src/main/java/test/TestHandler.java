package test;

import com.guxingke.simpleNioServer.handler.Handler;

public class TestHandler extends Handler {

	@Override
	public void get(Object id) {
		String respData = "执行了get/id  ID:" + id;
		setAttr("test", respData);
		render("test.ftl");
	}

	@Override
	public void get() {
		String respData = "执行了get";
		setAttr("test", respData);
		render("test.ftl");
	}

	@Override
	public void put() {
		String respData = "执行了put";
		setAttr("test", respData);
		render("test.ftl");
	}

	@Override
	public void delete(Object id) {
		String respData = "执行了delete/id ID:" + id;
		setAttr("test", respData);
		render("test.ftl");
	}

	@Override
	public void post() {
		String respData = "执行了post";
		setAttr("test", respData);
		render("test.ftl");
	}

	public void testMethod() {
		String respData = "执行了自定义方法";
		setAttr("test", respData);
		render("test.ftl");
	}

	public void testRedirect() {
		redirect("test/testMethod");
	}

	public void testAddCookie() {
		String respData = "执行了AddCookie方法";
		setAttr("test", respData);
		response.setCookie("user", "test-test", 60 * 60 * 24 * 7);
		render("test.ftl");
	}

	public void testRemoveCookie() {
		String respData = "执行了RemoveCookie方法";
		setAttr("test", respData);
		response.setCookie("user", " ", 0);
		render("test.ftl");
	}
}
