package org.orienteer.wicketjersey;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.CompoundRequestMapper;

/**
 * Wicket {@link IInitializer} which binds custom {@link CompoundRequestMapper} for JAX-RS applications 
 */
public class RestInitializer implements IInitializer{

	private JerseyCompoundRequestMapper mapper = new JerseyCompoundRequestMapper();
	
	@Override
	public void init(Application application) {
		if(application instanceof WebApplication) ((WebApplication)application).mount(mapper);
	}

	@Override
	public void destroy(Application application) {
		for (IRequestMapper iRequestMapper : mapper) {
			if(iRequestMapper instanceof JerseyRequestMapper) {
				((JerseyRequestMapper)iRequestMapper).getApplicationHandler().onShutdown((JerseyRequestMapper)iRequestMapper);
			}
		}
	}
	
	public static JerseyCompoundRequestMapper getJerseyCompoundRequestMapper() {
		return getJerseyCompoundRequestMapper(Application.get());
	}
	
	
	public static RestInitializer get() {
		return get(Application.get());
	}
	
	
	private static JerseyCompoundRequestMapper getJerseyCompoundRequestMapper(Application app) {
		RestInitializer initializer = get(app);
		return initializer!=null?initializer.mapper:null;
	}
	
	private static RestInitializer get(Application app) {
		for(IInitializer initializer : app.getInitializers()) {
			if(initializer instanceof RestInitializer) return (RestInitializer) initializer;
		}
		return null;
	}

}
