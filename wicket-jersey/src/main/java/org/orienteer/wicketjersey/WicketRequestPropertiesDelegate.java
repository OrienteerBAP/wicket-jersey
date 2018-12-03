package org.orienteer.wicketjersey;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.http.WebRequest;
import org.glassfish.jersey.internal.PropertiesDelegate;

public class WicketRequestPropertiesDelegate  implements PropertiesDelegate {
	
	private final WebRequest request;
	private final HttpServletRequest httpRequest;
	
	public WicketRequestPropertiesDelegate(WebRequest request) {
		this.request = request;
		this.httpRequest = (HttpServletRequest) request.getContainerRequest();
	}

	@Override
	public Object getProperty(String name) {
		return httpRequest.getAttribute(name);
	}

	@Override
	public Collection<String> getPropertyNames() {
		return Collections.list(httpRequest.getAttributeNames());
	}

	@Override
	public void setProperty(String name, Object object) {
		httpRequest.setAttribute(name, object);
	}

	@Override
	public void removeProperty(String name) {
		httpRequest.removeAttribute(name);
	}

}
