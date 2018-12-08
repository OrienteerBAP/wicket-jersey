package org.orienteer.wicketjersey;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.CompoundRequestMapper;

public class JerseyCompoundRequestMapper  extends CompoundRequestMapper implements IDetachable {
	
	@Override
	public CompoundRequestMapper add(IRequestMapper mapper) {
		super.add(mapper);
		if(mapper instanceof JerseyRequestMapper) {
			startup((JerseyRequestMapper) mapper); 
		}
		return this;
	}
	
	@Override
	public CompoundRequestMapper remove(IRequestMapper mapper) {
		super.remove(mapper);
		if(mapper instanceof JerseyRequestMapper) {
			shutdown((JerseyRequestMapper) mapper);
		}
		return this;
	}

	@Override
	public void detach() {
		for (IRequestMapper mapper : this) {
			if(mapper instanceof JerseyRequestMapper) {
				shutdown((JerseyRequestMapper) mapper);
			}
		}
	}
	
	private void startup(JerseyRequestMapper mapper) {
		mapper.getApplicationHandler().onStartup(mapper);
	}
	
	private void shutdown(JerseyRequestMapper mapper) {
		mapper.getApplicationHandler().onShutdown(mapper);
	}
}
