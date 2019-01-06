package test;

import com.guxingke.simpleNioServer.core.Notifier;
import com.guxingke.simpleNioServer.core.Options;
import com.guxingke.simpleNioServer.core.SimpleNioServer;
import com.guxingke.simpleNioServer.event.DispathcherObserver;
import com.guxingke.simpleNioServer.template.FreemakerUtil;

public class HttpServerTest {

	public static void main(String[] args) throws Exception {
		// 1.配置常量，当前使用默认配置
		Options.setTemplatePath(System.getProperty("user.dir") + "/src/main/resources/templates");
		Options.setResourcesPath(System.getProperty("user.dir") + "/src/main/resources/resources");

		// 2.初始化模板工具
		FreemakerUtil.initTemplate();

		// 3.获取分发器
		DispathcherObserver dispathcher = DispathcherObserver.getDispathcher();

		// 4.获取统治者，并添加观察者
		Notifier notifier = Notifier.getNotifier();
		notifier.addListener(dispathcher);

		// 5.添加路由mapping
		dispathcher.addMapping("test", TestHandler.class);

		// 6.实例化服务器，启动服务线程
		SimpleNioServer server = new SimpleNioServer(20000);
		Thread tServer = new Thread(server, "thread-tServer");

		tServer.start();
	}
}
