package org.orienteer.wicketjersey;

import javax.ws.rs.core.Application;

public class JerseyWicket {
	private JerseyWicket() {
		throw new IllegalStateException(JerseyWicket.class.getSimpleName()+" is utility class");
	}
	
	public static void mount(Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(app));
	}
	
	public static void mount(String path, Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(path, app));
	}
}
