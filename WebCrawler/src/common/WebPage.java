package common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import common.ErrorCode.CrError;
import static common.LogManager.writeGenericLog;

public class WebPage implements IWebPage {
	private Document m_doc = null;
	private String m_html = null;
	private URLObject m_originalUrl = null;

	private long m_downloadDurationInMillisec;
	
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
		
		this.m_doc = null;
		
		if (compressed) {
			this.m_html = HTMLCompressor.compressHtmlContent(html);
		} else {
			this.m_html = html;
		}
		
		try {
			Document doc = Jsoup.parse(html);
					
			this.m_doc = doc;
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
			this.m_html = HTMLCompressor.compressHtmlContent(doc.outerHtml());
		} else {
			this.m_html = doc.outerHtml();
		}
		
		this.m_doc = doc;
		
		return CrError.CR_OK;
	}
	
	@Override
	public String getString() {
		return m_html;
	}

	@Override
	public Document getDocument() {
		return m_doc;
	}

	@Override
	public long getDownloadDuationInhMillisec() {
		return m_downloadDurationInMillisec;
	}

	@Override
	public CrError setDownloadDuationInMillisec(long downloadDuationInMillisec) {
		m_downloadDurationInMillisec = downloadDuationInMillisec;

		return CrError.CR_OK;
	}
	
	@Override
	public URLObject get_originalUrl() {
		return m_originalUrl;
	}

	@Override
	public void set_originalUrl(URLObject originalUrl) {
		this.m_originalUrl = originalUrl;
	}

	@Override
	public int get_id() {
		return m_originalUrl.get_id();
	}

	@Override
	public void set_id(int id) {
		m_originalUrl.set_id(id);
	}
}
