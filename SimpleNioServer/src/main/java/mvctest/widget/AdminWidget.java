package mvctest.widget;

import com.guxingke.mvc.Controller;
import com.guxingke.mvc.route.DispathcherType;
import com.guxingke.mvc.route.Route;
import com.guxingke.mvc.route.Routes;
import com.guxingke.mvc.widget.LayoutWidget;
import com.guxingke.mvc.widget.RouteWidget;
import com.guxingke.mvc.widget.WidgetException;

public class AdminWidget extends LayoutWidget implements RouteWidget {

	@Override
	public void configRoute(Routes routes) {
		routes.addRoute(new Route("test", this.getClass(),
				DispathcherType.BACK, "testmvc.ftl"));
	}

	@Override
	public void excute(Controller controller) throws WidgetException {
		super.excute(controller);
		String respData = "执行了AdminWidget,分发类型:back";
		controller.setAttr("test", respData);
	}

	@Override
	protected void initChirdren() {
		// TODO Auto-generated method stub
	}

}
