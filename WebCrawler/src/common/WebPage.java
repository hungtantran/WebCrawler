package common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import common.ErrorCode.CrError;
import static common.LogManager.writeGenericLog;

public class WebPage implements IWebPage {
	private Document doc = null;
	private String html = null;
	
	public WebPage() {
		
	}

	public CrError setString(String html) {
		return setString(html, true);
	}
	
	public CrError setDocument(Document doc) {
		return setDocument(doc, true);
	}
	
	public CrError setString(String html, boolean compressed) {
		if (html == null) {
			return CrError.CR_MALFORM_HTML;			
		}
		
		this.doc = null;
		
		if (compressed) {
			this.html = HTMLCompressor.compressHtmlContent(html);
		} else {
			this.html = html;
		}
		
		try {
			Document doc = Jsoup.parse(html);
					
			this.doc = doc;
		} catch (Exception e) {
			writeGenericLog(e.getMessage());
			return CrError.CR_MALFORM_HTML;
		}
		
		return CrError.CR_OK;
	}
	
	public CrError setDocument(Document doc, boolean compressed) {
		if (doc == null) {
			return CrError.CR_MALFORM_HTML;			
		}
		
		if (compressed) {
			this.html = HTMLCompressor.compressHtmlContent(doc.outerHtml());
		} else {
			this.html = doc.outerHtml();
		}
		
		this.doc = doc;
		
		return CrError.CR_OK;
	}
	
	@Override
	public String getString() {
		return html;
	}

	@Override
	public Document getDocument() {
		return doc;
	}

}
