package org.orienteer.wicketjersey.mock;

import org.apache.log4j.BasicConfigurator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.wicketjersey.JerseyRequestMapper;

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
		mount(new JerseyRequestMapper(restApp));
		mount(new JerseyRequestMapper("/overapi", restApp));
		mountPage("/overapi/justpage", HomePage.class);
	}

}
