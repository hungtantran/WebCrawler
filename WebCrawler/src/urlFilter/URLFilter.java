package urlFilter;

import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import common.ErrorCode.CrError;
import common.URLObject;

public class URLFilter implements IURLFilter {
	private static final Set<String> commonFileExtensions = new HashSet<String>(Arrays.asList("eps", "ps", "svg", "indd", "pct", "pdf", "xlr", "xls", "xlsx", "accdb", "db", "dbf", "mdb", "pdb", "sql", "apk", "app", "bat", "cgi", "com", "exe", "gadget", "jar", "pif", "vb", "wsf", "dem", "gam", "nes", "rom", "sav", "dwg", "dxf", "gpx", "kml", "kmz", "asp", "aspx", "cer", "cfm", "csr", "css", "js", "jsp", "php", "rss", "xhtml", "crx", "plugin", "fnt", "fon", "otf", "ttf", "cab", "cpl", "cur", "deskthemepack", "dll", "dmp", "drv", "icns", "ico", "lnk", "sys", "cfg", "ini", "prf", "hqx", "mim", "uue", "7z", "cbr", "deb", "gz", "pkg", "rar", "rpm", "sitx", "tar.gz", "zip", "zipx", "bin", "cue", "dmg", "iso", "mdf", "toast", "vcd", "c", "class", "cpp", "cs", "dtd", "fla", "h", "java", "lua", "m", "pl", "py", "sh", "sln", "swift", "vcxproj", "xcodeproj", "bak", "tmp", "crdownload", "ics", "msi", "part", "torrent"));

	public URLFilter() {
		
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
		if (URLFilter.commonFileExtensions.contains(extension)) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isPositionLinkInPage(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.lastIndexOf("#");
		if (index == -1) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public CrError filterURLs(ArrayList<URLObject> inoutUrls) {
		for (int i = 0; i < inoutUrls.size(); ++i) {
			URLObject url = inoutUrls.get(i);

			// Filter out file link
			String absoluteLink = url.getAbsoluteLink();
			if (URLFilter.isFile(absoluteLink)) {
				writeGenericLog("Filter out link file " + absoluteLink);

				inoutUrls.remove(i);
				--i;
			}
			
			if (URLFilter.isPositionLinkInPage(absoluteLink)) {
				writeGenericLog("Filter out position link in page " + absoluteLink);

				inoutUrls.remove(i);
				--i;
			}
		}
		
		return CrError.CR_OK;
	}
}
