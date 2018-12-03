package org.orienteer.wicketjersey;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            URI baseUri = new URI(requestCycle.getUrlRenderer().getBaseUrl().toString());
            URI requestUri = new URI(requestCycle.getUrlRenderer().renderFullUrl(request.getClientUrl()));
            final ContainerRequest requestContext = new ContainerRequest(baseUri,
                    requestUri, httpRequest.getMethod(),
                    getSecurityContext(request), new WicketRequestPropertiesDelegate(request));
            requestContext.setEntityStream(httpRequest.getInputStream());
            Enumeration<String> headers = httpRequest.getHeaderNames();
            while(headers.hasMoreElements()) {
            	String headerName = headers.nextElement();
                requestContext.headers(headerName, request.getHeaders(headerName));
            }
            requestContext.setWriter(responseWriter);

            requestContext.setRequestScopedInitializer(injectionManager -> {
                injectionManager.<Ref<Request>>getInstance(mapper.requestTYPE).set(request);
                injectionManager.<Ref<Response>>getInstance(mapper.responseTYPE).set(response);
            });
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

}
