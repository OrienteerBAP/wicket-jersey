package org.orienteer.wicketjersey.demo.rest;

import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Produces("application/json")
@Path("wicket")
public class DemoResources {
	
	@Path("threads")
	public String listThreads() {
		return "TEST";
//		return Thread.getAllStackTraces().keySet().stream().map(t->t.getName()).collect(Collectors.joining("\n"));
	}
}
