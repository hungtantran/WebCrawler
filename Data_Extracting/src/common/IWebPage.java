package common;

import common.ErrorCode.CrError;
import org.jsoup.nodes.Document;

public interface IWebPage {
	public String getString();
	
	public Document getDocument();
	
	public CrError setString(String html);
	
	public CrError setDocument(Document doc);
	
	public long getDownloadDuationInhMillisec();
	
	public CrError setDownloadDuationInMillisec(long downloadDuationInMillisec);

	public void set_originalUrl(URLObject originalUrl);

	public URLObject get_originalUrl();
	
	public int get_id();
	
	public void set_id(int id);
}
