package unittest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import common.URLObject;
import urlDuplicationEliminator.IURLDuplicationEliminator;
import urlDuplicationEliminator.URLDuplicationEliminator;

public class Test_IURLDuplicationEliminator {

	@Test
	public void test() {
		Test_URLDuplicationEliminator();
	}
	
	public void Test_URLDuplicationEliminator() {
		IURLDuplicationEliminator eliminator = new URLDuplicationEliminator();
		
		ArrayList<URLObject> urls = new ArrayList<URLObject>();
		for (int i = 2; i <= 9998; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://vnexpress.net/");
			urls.add(url);
		}
		eliminator.eliminateDuplicatedURLs(urls);
		assertEquals(9997, urls.size());
		
		ArrayList<URLObject> url2s = new ArrayList<URLObject>();
		for (int i = 1; i <= 10000; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://vnexpress.net/");
			url2s.add(url);
		}
		eliminator.eliminateDuplicatedURLs(url2s);
		assertEquals(3, url2s.size());
		
		ArrayList<URLObject> url3s = new ArrayList<URLObject>();
		for (int i = 1; i <= 10000; ++i) {
			URLObject url = new URLObject();
			url.setLink(Integer.toString(i));
			url.setDomain("http://google.com/");
			url3s.add(url);
		}
		eliminator.eliminateDuplicatedURLs(url3s);
		assertEquals(10000, url3s.size());
	}
}
