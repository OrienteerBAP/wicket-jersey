package org.orienteer.wicketjersey.mock;

import org.apache.log4j.BasicConfigurator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.wicketjersey.JerseyRequestMapper;
import org.orienteer.wicketjersey.WicketJersey;

public class MockWebApplication extends WebApplication {
	
	static {
		BasicConfigurator.configure();
	}
	
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
	
	@Override
	protected void init() {
		super.init();
		MockRestApplication restApp = new MockRestApplication();
		WicketJersey.mount(restApp);
		WicketJersey.mount("/overapi", restApp);
		WicketJersey.mount("/aspackage", TestWicketResource.class.getPackage().getName());
		mountPage("/overapi/justpage", HomePage.class);
	}

}
