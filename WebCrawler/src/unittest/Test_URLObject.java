package unittest;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import org.junit.Test;

import common.URLObject;

public class Test_URLObject {

	@Test
	public void test() throws MalformedURLException {
		URLObject url = new URLObject();
		url.setDomain("http://www.vnexpress.net/");
		url.setLink("/tintuc/");
		assertEquals(url.getAbsoluteLink(), "http://www.vnexpress.net/tintuc");
		
		url = new URLObject();
		url.setDomain("http://www.vnexpress.net/");
		url.setLink("/tintuc");
		assertEquals(url.getAbsoluteLink(), "http://www.vnexpress.net/tintuc");
		
		url = new URLObject();
		url.setDomain("http://www.vnexpress.net/");
		url.setLink("http://www.hodinkee.com/news");
		assertEquals(url.getAbsoluteLink(), "http://www.hodinkee.com/news");
		assertEquals(url.getDomain(), "http://www.hodinkee.com");
		
		url = new URLObject();
		url.setDomain("http://techcrunch.com");
		url.setLink("http://feeds.feedburner.com/techcrunch/podcast/crunch-report");
		System.out.println(url.getDomain());
	}

}
