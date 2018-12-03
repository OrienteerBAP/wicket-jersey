package org.orienteer.wicketjersey.mock;

import org.apache.log4j.BasicConfigurator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.wicketjersey.JerseyRequestMapper;

public class MockWebApplication extends WebApplication {
	
	public MockWebApplication() {
		BasicConfigurator.configure();
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
	
	@Override
	protected void init() {
		super.init();
		mount(new JerseyRequestMapper("/api", new MockRestApplication()));
	}

}
