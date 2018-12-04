package org.orienteer.wicketjersey;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.AbstractMapper;
import org.apache.wicket.util.lang.Args;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JerseyRequestMapper extends AbstractMapper implements Container{
	
	private static final Logger LOG = LoggerFactory.getLogger(JerseyRequestMapper.class);

	protected final String[] mountSegments;
	
	private volatile ApplicationHandler appHandler;
	private volatile ScheduledExecutorService scheduler;
	
	
	
	final Type requestTYPE = (new GenericType<Ref<Request>>() { }).getType();
	final Type responseTYPE = (new GenericType<Ref<Response>>() { }).getType();
	
	public JerseyRequestMapper(Application app) {
		this(app.getClass().getAnnotation(ApplicationPath.class), app);
	}
	
	private JerseyRequestMapper(ApplicationPath path, Application app) {
		this(path!=null?path.value():null, app);
	}
	
	public JerseyRequestMapper(String mountPath, Application app) {
		Args.notEmpty(mountPath, "mountPath");
		this.mountSegments = getMountSegments(mountPath);
		this.appHandler = new ApplicationHandler(app, new WicketBinder());
		this.scheduler = new ScheduledThreadPoolExecutor(2);
	}
	
	ScheduledExecutorService getScheduler() {
		return scheduler;
	}
	
	String[] getMountSegments() {
		return mountSegments;
	}

	@Override
	public IRequestHandler mapRequest(Request request) {
		return new JerseyRequestHandler(this);
	}

	@Override
	public int getCompatibilityScore(Request request) {
		return urlStartsWith(request.getUrl(), mountSegments)?mountSegments.length:Integer.MIN_VALUE;
	}

	@Override
	public Url mapHandler(IRequestHandler requestHandler) {
		if(requestHandler instanceof JerseyRequestHandler)
		{
			if(((JerseyRequestHandler) requestHandler).getJerseyRequestMapper().equals(this) 
					|| ((JerseyRequestHandler) requestHandler).getApplicationHandler().equals(appHandler)) {
				Url url = new Url();
				url.getSegments().addAll(Arrays.asList(mountSegments));
				return url;
			}
		}
		return null;
	}

	@Override
	public ResourceConfig getConfiguration() {
		return appHandler.getConfiguration();
	}

	@Override
	public ApplicationHandler getApplicationHandler() {
		return appHandler;
	}

	@Override
	public void reload() {
		reload(appHandler.getConfiguration());
	}

	@Override
	public void reload(ResourceConfig configuration) {
		appHandler.onShutdown(this);

        appHandler = new ApplicationHandler(configuration, new WicketBinder());
        scheduler = new ScheduledThreadPoolExecutor(2);
        appHandler.onReload(this);
        appHandler.onStartup(this);
	}
	
	private static class WicketRequestReferencingFactory extends ReferencingFactory<Request> {

        @Inject
        public WicketRequestReferencingFactory(final Provider<Ref<Request>> referenceFactory) {
            super(referenceFactory);
        }
    }

    private static class WicketResponseReferencingFactory extends ReferencingFactory<Response> {

        @Inject
        public WicketResponseReferencingFactory(final Provider<Ref<Response>> referenceFactory) {
            super(referenceFactory);
        }
    }
	
	static class WicketBinder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(WicketRequestReferencingFactory.class).to(Request.class)
                    .proxy(false).in(RequestScoped.class);
            bindFactory(ReferencingFactory.<Request>referenceFactory()).to(new GenericType<Ref<Request>>() {})
                    .in(RequestScoped.class);

            bindFactory(WicketResponseReferencingFactory.class).to(Response.class)
                    .proxy(true).proxyForSameScope(false).in(RequestScoped.class);
            bindFactory(ReferencingFactory.<Response>referenceFactory()).to(new GenericType<Ref<Response>>() {})
                    .in(RequestScoped.class);
        }
    }

}
