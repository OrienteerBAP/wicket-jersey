package org.orienteer.wicketjersey.demo.rest;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


/**
 * JAX-RS Resources to demo this library 
 */
@Path("")
public class DemoResources {
	
	@GET
	@Path("threads")
	@Produces("application/json")
	public List<String> listThreads() {
		return Thread.getAllStackTraces().keySet().stream().map(t->t.getName()).collect(Collectors.toList());
	}
	
	@GET
	@Path("properties")
	@Produces("application/json")
	public Properties listProperties() {
		return System.getProperties();
	}
	
	@GET
	@Path("os")
	@Produces("application/json")
	public OperatingSystemMXBean listJars() {
		return ManagementFactory.getOperatingSystemMXBean();
	}
	
	@GET
	@Path("classes")
	@Produces("application/json")
	public ClassLoadingMXBean listClassloading() {
		return ManagementFactory.getClassLoadingMXBean();
	}
	
}
