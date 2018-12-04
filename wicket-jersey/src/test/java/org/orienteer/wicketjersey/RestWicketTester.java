package org.orienteer.wicketjersey;

import javax.servlet.ServletContext;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

public class RestWicketTester extends WicketTester {

	public RestWicketTester() {
		super();
	}

	public RestWicketTester(Class<? extends Page> homePage) {
		super(homePage);
	}

	public RestWicketTester(WebApplication application, boolean init) {
		super(application, init);
	}

	public RestWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
		super(application, servletCtx, init);
	}

	public RestWicketTester(WebApplication application, ServletContext servletCtx) {
		super(application, servletCtx);
	}

	public RestWicketTester(WebApplication application, String path) {
		super(application, path);
	}

	public RestWicketTester(WebApplication application) {
		super(application);
	}
	
	public String executeGET(String url) {
		getRequest().setMethod("get");
		executeUrl(url);
		return getLastResponse().getDocument();
	}
	
}
