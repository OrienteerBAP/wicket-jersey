package org.orienteer.wicketjersey;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;

public class WicketJersey {
	private WicketJersey() {
		throw new IllegalStateException(WicketJersey.class.getSimpleName()+" is utility class");
	}
	
	public static void mount(Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(app));
	}
	
	public static void mount(String path, Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(path, app));
	}
	
	public static void mount(String path, String... packages) {
		mount(path, new ResourceConfig() {
			{
				packages(packages);
			}
		});
	}
	
	public static void mount(String path, boolean recursive, String... packages) {
		mount(path, new ResourceConfig() {
			{
				packages(recursive, packages);
			}
		});
	}
}
