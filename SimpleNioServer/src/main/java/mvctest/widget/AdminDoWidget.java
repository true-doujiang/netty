package mvctest.widget;

import com.guxingke.mvc.Controller;
import com.guxingke.mvc.route.DispathcherType;
import com.guxingke.mvc.route.Route;
import com.guxingke.mvc.route.Routes;
import com.guxingke.mvc.widget.HandllerWidget;
import com.guxingke.mvc.widget.Widget;
import com.guxingke.mvc.widget.WidgetException;

public class AdminDoWidget extends Widget implements HandllerWidget {

	@Override
	public void configRoute(Routes routes) {
		routes.addRoute(new Route("test", this.getClass(),
				DispathcherType.BACK_DO, "testmvc.ftl"));
	}

	@Override
	public void handle(Controller controller) throws WidgetException {
		String respData = "执行了AdminDoWidget,分发类型:backdo";
		controller.setAttr("test", respData);
	}

}
