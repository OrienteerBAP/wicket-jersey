package org.orienteer.wicketjersey.mock;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class MockRestApplication extends ResourceConfig {
	
	public MockRestApplication() {
		packages(MockRestApplication.class.getPackage().getName());
	}
}
