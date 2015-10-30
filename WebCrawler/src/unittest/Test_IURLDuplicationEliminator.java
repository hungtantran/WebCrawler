package unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import common.Globals;
import common.Helper;
import common.URLObject;
import urlDuplicationEliminator.IURLDuplicationEliminator;
import urlDuplicationEliminator.URLDuplicationEliminator;

public class Test_IURLDuplicationEliminator {
	private static Logger LOG = LogManager.getLogger(Test_IURLDuplicationEliminator.class.getName());

	@Test
	public void test() {
		Test_URLDuplicationEliminator();
		Test_URLDuplicationEliminator2();
	}
	
	public void Test_URLDuplicationEliminator() {
		IURLDuplicationEliminator eliminator = new URLDuplicationEliminator(null, null, 100, null);
		URLObject originalUrl = new URLObject();
		originalUrl.setLink("http://www.microsoft.com");
		
		ArrayList<URLObject> urls = new ArrayList<URLObject>();
		for (int i = 2; i <= 9998; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://vnexpress.net/");
			urls.add(url);
		}
		eliminator.eliminateDuplicatedURLs(originalUrl, urls);
		assertEquals(9997, urls.size());
		
		ArrayList<URLObject> url2s = new ArrayList<URLObject>();
		for (int i = 1; i <= 10000; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://vnexpress.net/");
			url2s.add(url);
		}
		eliminator.eliminateDuplicatedURLs(originalUrl, url2s);
		for (int i = 0; i < url2s.size(); ++i) {
			System.out.println(url2s.get(i));
		}
		assertEquals(3, url2s.size());
		
		ArrayList<URLObject> url3s = new ArrayList<URLObject>();
		for (int i = 1; i <= 10000; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://google.com/");
			url3s.add(url);
		}
		eliminator.eliminateDuplicatedURLs(originalUrl, url3s);
		assertTrue(url3s.size() > 9900);
		
		ArrayList<URLObject> url4s = new ArrayList<URLObject>();
		for (int i = 100000; i <= 100010; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(100010));
			url.setDomain("http://vnexpress.net/");
			url4s.add(url);
		}
		eliminator.eliminateDuplicatedURLs(originalUrl, url4s);
		assertEquals(1, url4s.size());
	}
	
	public void Test_URLDuplicationEliminator2() {
		try {
			File bloomFilterFile = Helper.createFile("Test" + Globals.PATHSEPARATOR + "Bloomfilter");
			bloomFilterFile.delete();

			IURLDuplicationEliminator eliminator = new URLDuplicationEliminator("Test", "Bloomfilter", 400, null);
			URLObject originalUrl = new URLObject();
			originalUrl.setLink("http://www.microsoft.com");
			
			ArrayList<URLObject> urls = new ArrayList<URLObject>();
			for (int i = 1; i <= 10000; ++i) {
				URLObject url = new URLObject();
				url.setLink(Integer.toString(i));
				url.setDomain("http://vnexpress.net/");
				urls.add(url);
			}
	
			eliminator.eliminateDuplicatedURLs(originalUrl, urls);
			assertEquals(10000, urls.size());

			IURLDuplicationEliminator eliminator2 = new URLDuplicationEliminator("Test", "Bloomfilter", 400, null);
			eliminator2.eliminateDuplicatedURLs(originalUrl, urls);
			assertEquals(0, urls.size());
			
			for (int i = 5001; i <= 15000; ++i) {
				URLObject url = new URLObject();
				url.setLink(Integer.toString(i));
				url.setDomain("http://vnexpress.net/");
				urls.add(url);
			}
			
			eliminator2.eliminateDuplicatedURLs(originalUrl, urls);
			assertEquals(5000, urls.size());
		} finally {
			File bloomFilterFile = Helper.createFile("Test" + Globals.PATHSEPARATOR + "Bloomfilter");
			bloomFilterFile.delete();
		}
	}
}
