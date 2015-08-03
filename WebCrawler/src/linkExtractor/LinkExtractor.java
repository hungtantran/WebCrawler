package linkExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

import static common.LogManager.writeGenericLog;

public class LinkExtractor implements ILinkExtractor {
	private static final Set<String> commonFileExtensions = new HashSet<String>(Arrays.asList("eps", "ps", "svg", "indd", "pct", "pdf", "xlr", "xls", "xlsx", "accdb", "db", "dbf", "mdb", "pdb", "sql", "apk", "app", "bat", "cgi", "com", "exe", "gadget", "jar", "pif", "vb", "wsf", "dem", "gam", "nes", "rom", "sav", "dwg", "dxf", "gpx", "kml", "kmz", "asp", "aspx", "cer", "cfm", "csr", "css", "htm", "html", "js", "jsp", "php", "rss", "xhtml", "crx", "plugin", "fnt", "fon", "otf", "ttf", "cab", "cpl", "cur", "deskthemepack", "dll", "dmp", "drv", "icns", "ico", "lnk", "sys", "cfg", "ini", "prf", "hqx", "mim", "uue", "7z", "cbr", "deb", "gz", "pkg", "rar", "rpm", "sitx", "tar.gz", "zip", "zipx", "bin", "cue", "dmg", "iso", "mdf", "toast", "vcd", "c", "class", "cpp", "cs", "dtd", "fla", "h", "java", "lua", "m", "pl", "py", "sh", "sln", "swift", "vcxproj", "xcodeproj", "bak", "tmp", "crdownload", "ics", "msi", "part", "torrent"));

	public LinkExtractor() {
		
	}
	
	private static boolean isFile(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.lastIndexOf('.'); 
		if (index == -1) {
			return false;
		}
		
		String extension = url.substring(index + 1, url.length());
		if (LinkExtractor.commonFileExtensions.contains(extension)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public CrError extractURLs(IWebPage webPage, ArrayList<URLObject> extractedUrls) {
		if (webPage == null || extractedUrls == null) {
			return CrError.CR_INVALID_ARGS;
		}
		
		Document doc = webPage.getDocument();
		if (doc == null) {
			return CrError.CR_INVALID_ARGS;
		}

		Set<String> extractedUrlsSet = new HashSet<String>();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Extract links: ");

		Elements linkElems = doc.select("a[href]");
		for (Element linkElem : linkElems) {
			URLObject url = new URLObject();
			
			String link = linkElem.attr("href").toString();

			// Don't duplicate url
			if (extractedUrlsSet.contains(link)) {
				continue;
			} else {
				extractedUrlsSet.add(link);
			}
			
			// Don't include file link
			if (LinkExtractor.isFile(link)) {
				writeGenericLog("Skip link file " + link);
				continue;
			}

			builder.append(link + "; ");

			url.setLink(link);
			extractedUrls.add(url);
		}
		
		builder.append(". Size: " + extractedUrls.size());
		writeGenericLog(builder.toString());
		
		return CrError.CR_OK;
	}

}
