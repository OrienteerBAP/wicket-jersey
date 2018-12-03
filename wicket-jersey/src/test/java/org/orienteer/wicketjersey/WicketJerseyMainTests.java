package org.orienteer.wicketjersey;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.wicketjersey.mock.HomePage;
import org.orienteer.wicketjersey.mock.MockWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketJerseyMainTests {
	
	private static final Logger LOG = LoggerFactory.getLogger(WicketJerseyMainTests.class);
	
    private WicketTester tester;

    @Before
    public void setUp(){
            tester = new WicketTester(new MockWebApplication());
    }
    
    @Test
    public void testMainPageAvailable() {
    	tester.startPage(HomePage.class);
    	tester.assertRenderedPage(HomePage.class);
    }
    
    @Test
    public void testEchoRest() {
    	tester.executeUrl("./api/wicket/cycle");
    	System.out.println("Status: "+tester.getLastResponse().getStatus());
    	System.out.println("Response: "+tester.getLastResponse().getDocument());
    	LOG.info("Response: "+tester.getLastResponse().getDocument());
    }
}
