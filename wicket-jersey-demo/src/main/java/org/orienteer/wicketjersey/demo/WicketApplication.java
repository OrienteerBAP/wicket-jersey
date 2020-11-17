package org.orienteer.wicketjersey.demo;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.wicketjersey.WicketJersey;
import org.orienteer.wicketjersey.demo.rest.DemoResources;

/**
 * Demo Web Application for WicketJersey
 */
public class WicketApplication extends WebApplication
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		WicketJersey.mount("/rest", DemoResources.class.getPackage().getName());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unmount("/rest");
	}
}
