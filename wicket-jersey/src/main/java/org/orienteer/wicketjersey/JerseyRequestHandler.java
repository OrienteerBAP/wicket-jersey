package org.orienteer.wicketjersey;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IRequestHandler} which routes requests to Jersey 
 */
public class JerseyRequestHandler implements IRequestHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(JerseyRequestHandler.class);
	
	private final JerseyRequestMapper mapper;
	
	public JerseyRequestHandler(JerseyRequestMapper mapper) {
		this.mapper = mapper;
	}
	
	public ApplicationHandler getApplicationHandler() {
		return mapper.getApplicationHandler();
	}
	
	public JerseyRequestMapper getJerseyRequestMapper() {
		return mapper;
	}
	

	@Override
	public void respond(IRequestCycle requestCycle) {
		final WebRequest request = (WebRequest) requestCycle.getRequest();
		final HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
		final WebResponse response = (WebResponse) requestCycle.getResponse();
		final WicketResponseWriter responseWriter = new WicketResponseWriter(response, mapper.getScheduler());
        try {
        	LOG.debug(JerseyRequestHandler.class.getSimpleName()+" started");
        	
        	Url root = Url.parse(request.getContextPath() + request.getFilterPath());
        	Url base = new Url(root);
        	base.getSegments().add("");
        	base.getSegments().addAll(Arrays.asList(mapper.getMountSegments()));
        	base.getSegments().add("");
            URI baseUri = new URI(requestCycle.getUrlRenderer().renderFullUrl(base));
            
            Url requestUrl = request.getClientUrl();
            requestUrl.prependLeadingSegments(root.getSegments());
            
            URI requestUri = new URI(requestUrl.toString(StringMode.FULL));
            
//            LOG.info("base: "+baseUri+"  request: "+requestUri);
            final ContainerRequest requestContext = new ContainerRequest(baseUri,
                    requestUri, httpRequest.getMethod().toUpperCase(),
                    getSecurityContext(request), new WicketRequestPropertiesDelegate(httpRequest));
            requestContext.setEntityStream(httpRequest.getInputStream());
            Enumeration<String> headers = httpRequest.getHeaderNames();
            while(headers.hasMoreElements()) {
            	String headerName = headers.nextElement();
                requestContext.headers(headerName, request.getHeaders(headerName));
            }
            requestContext.setWriter(responseWriter);

            /*requestContext.setRequestScopedInitializer(injectionManager -> {
                injectionManager.<Ref<Request>>getInstance(JerseyRequestMapper.requestTYPE).set(request);
                injectionManager.<Ref<WebResponse>>getInstance(JerseyRequestMapper.responseTYPE).set(response);
                injectionManager.<Ref<Request>>getInstance(JerseyRequestMapper.webRequestTYPE).set(request);
                injectionManager.<Ref<WebResponse>>getInstance(JerseyRequestMapper.webResponseTYPE).set(response);
            });*/
            mapper.getApplicationHandler().handle(requestContext);
        } catch (IOException | URISyntaxException e) {
			throw new WicketRuntimeException("Can't handle JAX-RS request", e);
		} finally {
        	LOG.debug(JerseyRequestHandler.class.getSimpleName()+" finished");
        }
	}
	
	private SecurityContext getSecurityContext(final WebRequest request) {
        return new SecurityContext() {

            @Override
            public boolean isUserInRole(final String role) {
            	if(WebSession.exists()) {
            		WebSession session = WebSession.get();
            		if(session instanceof AbstractAuthenticatedWebSession) {
            			AbstractAuthenticatedWebSession authSession = (AbstractAuthenticatedWebSession) session;
            			return authSession.getRoles().hasRole(role);
            		}
            	} 
            	return false;
            }

            @Override
            public boolean isSecure() {
            	return ((HttpServletRequest) request.getContainerRequest()).isSecure();
            }

            @Override
            public Principal getUserPrincipal() {
                if(WebSession.exists()) {
                	WebSession session = WebSession.get();
                	if(session instanceof AbstractAuthenticatedWebSession) {
            			AbstractAuthenticatedWebSession authSession = (AbstractAuthenticatedWebSession) session;
            			if(authSession.isSignedIn()) {
            				return mapper.getApplicationHandler().getInjectionManager().getInstance(Principal.class);
            			}
            		}
                }
                return null;
            }

            @Override
            public String getAuthenticationScheme() {
            	return WebSession.exists() && WebSession.get() instanceof AbstractAuthenticatedWebSession ? FORM_AUTH : null;
            }
        };
    }
	
	private static class WicketRequestPropertiesDelegate  implements PropertiesDelegate {
		
		private final HttpServletRequest httpRequest;
		
		public WicketRequestPropertiesDelegate(HttpServletRequest httpRequest) {
			this.httpRequest = httpRequest;
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

}
