package org.orienteer.wicketjersey;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Utili/Helper class which allow to mount JAX-RS related resources 
 */
public class WicketJersey {
	private WicketJersey() {
		throw new IllegalStateException(WicketJersey.class.getSimpleName()+" is utility class");
	}
	
	/**
	 * Mount JAX-RS application. Path will be specified by annotation {@link ApplicationPath}
	 * @param app {@link Application} to mount
	 */
	public static void mount(Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(app));
	}
	
	/**
	 * Mount {@link Application} to specified path.  {@link ApplicationPath} is ignored
	 * @param path path to mount to
	 * @param app {@link Application} to mount
	 */
	public static void mount(String path, Application app) {
		RestInitializer.getJerseyCompoundRequestMapper().add(new JerseyRequestMapper(path, app));
	}
	
	/**
	 * Mount JAX-RS resources from specified packages
	 * @param path path to mount to
	 * @param packages array of packages to look for JAX-RS resources
	 */
	public static void mount(String path, String... packages) {
		mount(path, new ResourceConfig() {
			{
				packages(packages);
			}
		});
	}
	
	/**
	 * Mount JAX-RS resources from specified packages
	 * @param path path to mount to
	 * @param recursive should resources be searched recursively in packages
	 * @param packages array of packages to look for JAX-RS resources
	 */
	public static void mount(String path, boolean recursive, String... packages) {
		mount(path, new ResourceConfig() {
			{
				packages(recursive, packages);
			}
		});
	}
}
