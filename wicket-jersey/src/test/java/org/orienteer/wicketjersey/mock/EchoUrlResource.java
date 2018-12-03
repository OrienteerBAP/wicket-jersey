package org.orienteer.wicketjersey.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.wicket.request.cycle.RequestCycle;

@Produces("application/json")
@Path("wicket")
public class EchoUrlResource {

	
	@GET
	@Path("cycle")
	public String cycle() {
		return RequestCycle.get()!=null?"IN CYCLE":"OUTSIDE";
	}
	
	
	@GET
	@Path("{url}")
	public String echo(@PathParam("url") String url) {
		return url;
	}
	
}
