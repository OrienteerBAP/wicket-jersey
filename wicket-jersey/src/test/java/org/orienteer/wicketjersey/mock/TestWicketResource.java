package org.orienteer.wicketjersey.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces("application/json")
@Path("wicket")
public class TestWicketResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestWicketResource.class);

	@GET
	@Path("cycle")
	public String cycle() {
		return RequestCycle.get()!=null?"IN CYCLE":"OUTSIDE";
	}

	@GET
	@Path("context")
	public String context(@Context Application app,
						  @Context WebApplication webApp,
						  @Context RequestCycle rc, 
						  @Context Request request,
						  @Context WebRequest webRequest,
						  @Context Response response,
						  @Context WebResponse webResponse) {
		boolean ret = app!=null
					&& webApp!=null
					&& rc!=null
					&& request!=null
					&& webRequest!=null
					&& response!=null
					&& webResponse!=null;
		return Boolean.valueOf(ret).toString();
	}
	
	
	@GET
	@Path("{url}")
	public String echo(@PathParam("url") String url) {
		return url;
	}
	
}
