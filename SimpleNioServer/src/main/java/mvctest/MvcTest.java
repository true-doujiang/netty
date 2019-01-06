package mvctest;

import java.io.IOException;

import com.guxingke.mvc.Mvc;
import com.guxingke.mvc.route.Routes;
import com.guxingke.mvc.widget.WidgetException;
import com.guxingke.mvc.widget.base.DefaultDispathcerWidget;
import com.guxingke.simpleNioServer.core.Notifier;
import com.guxingke.simpleNioServer.core.Options;
import com.guxingke.simpleNioServer.core.SimpleNioServer;
import com.guxingke.simpleNioServer.template.FreemakerUtil;

public class MvcTest {

	public static void main(String[] args) {
		// 配置常量，当前使用默认配置
		Options.setTemplatePath(System.getProperty("user.dir") + "/src/main/resources/templates");
		Options.setResourcesPath(System.getProperty("user.dir") + "/src/main/resources/resources");

		// 添加widget组件包到配置中，供widget初始化使用，所有的widget均为单例
		Options.addPackagePath("mvctest.widget");

		// 初始化模板工具
		try {
			FreemakerUtil.initTemplate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化mvc
		Mvc mvc = Mvc.getInstance();
		try {
			mvc.init();
		} catch (WidgetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Routes routes = mvc.getRoutes();

		// 获取分发器
		DefaultDispathcerWidget dispathcher = new DefaultDispathcerWidget(routes);
		// 获取通知者，并添加观察者
		Notifier notifier = Notifier.getNotifier();
		notifier.addListener(dispathcher);

		// 实例化服务器，启动服务线程
		SimpleNioServer server = null;
		try {
			server = new SimpleNioServer(20000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread tServer = new Thread(server);
		tServer.start();
	}

}
