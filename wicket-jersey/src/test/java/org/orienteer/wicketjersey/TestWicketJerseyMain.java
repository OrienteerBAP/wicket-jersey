package org.orienteer.wicketjersey;

import static org.junit.Assert.assertTrue;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.orienteer.wicketjersey.mock.HomePage;
import org.orienteer.wicketjersey.mock.MockWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWicketJerseyMain {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestWicketJerseyMain.class);
	
    private RestWicketTester tester;

    @Before
    public void setUp(){
            tester = new RestWicketTester(new MockWebApplication());
    }
    
    @Test
    public void testMainPageAvailable() {
    	tester.startPage(HomePage.class);
    	tester.assertRenderedPage(HomePage.class);
    }
    
    @Test
    public void testEchoRest() {
    	String response = tester.executeGET("./api/wicket/testme");
    	assertTrue(response.contains("testme"));
    	response = tester.executeGET("./overapi/wicket/testme2");
    	assertTrue(response.contains("testme2"));
    }
    
    @Test
    public void testFallbackToWicket() {
    	tester.executeGET("./overapi/justpage");
    	tester.assertRenderedPage(HomePage.class);
    }
}
