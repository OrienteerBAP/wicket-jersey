package org.orienteer.wicketjersey.demo.rest;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class DemoResources {
	
	@GET
	@Path("threads")
//	@Produces("text/plain")
	@Produces("application/json")
	public List<String> listThreads() {
//		return Thread.getAllStackTraces().keySet().stream().map(t->t.getName()).collect(Collectors.joining("\n"));
		return Thread.getAllStackTraces().keySet().stream().map(t->t.getName()).collect(Collectors.toList());
	}
	
	@GET
	@Path("properties")
//	@Produces("text/plain")
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
