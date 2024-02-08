package org.orienteer.wicketjersey.mock;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("api")
public class MockRestApplication extends ResourceConfig {
	
	public MockRestApplication() {
		packages(MockRestApplication.class.getPackage().getName());
	}
}
