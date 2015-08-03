package common;

import org.jsoup.nodes.Document;

import common.ErrorCode.CrError;

public interface IWebPage {
	public String getString();
	
	public Document getDocument();
	
	public CrError setString(String html);
	
	public CrError setDocument(Document doc);
}
