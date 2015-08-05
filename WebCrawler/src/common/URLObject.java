package common;

import static common.LogManager.writeGenericLog;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import common.ErrorCode.CrError;

public class URLObject {
	private String m_link;
	private URL m_domain;
	private boolean m_duplicated;
	private boolean m_absolute;
	private int m_priority;
	private long m_downloadDuration;

	public URLObject()
	{
		this.m_link = null;
		this.m_domain = null;
		this.m_duplicated = false;
		this.m_absolute = false;
	}

	public long get_downloadDuration() {
		return m_downloadDuration;
	}

	public void set_downloadDuration(long m_downloadDuration) {
		this.m_downloadDuration = m_downloadDuration;
	}

	public Boolean getDuplicated() {
		return m_duplicated;
	}

	public void setDuplicated(Boolean duplicated) {
		this.m_duplicated = duplicated;
	}
	
	public String getLink() {
		if (m_link == null) {
			return null;
		}

		return m_link.toString();
	}
	
	public String getAbsoluteLink() {
		if (!m_absolute) {
			try {
				URL absoluteLink = new URL(this.m_domain, this.m_link.toString());
				return absoluteLink.toString();
			} catch (MalformedURLException e) {
				return null;
			}
		} else {
			return this.m_link.toString();
		}
	}
	
	public CrError setLink(String link) {
		try {
			this.m_link = link;
			this.m_absolute = new URI(link).isAbsolute();
			
			if (this.m_absolute) {
				URL linkURL = new URL(link);
				this.m_domain = new URL(linkURL.getProtocol() + "://" +linkURL.getHost());
			}
		} catch (URISyntaxException e) {
			writeGenericLog("Malformed link " + e.getMessage());
			return CrError.CR_MALFORM_URL;
		} catch (MalformedURLException e) {
			writeGenericLog("Malformed link " + e.getMessage());
			return CrError.CR_MALFORM_URL;
		}
		
		return CrError.CR_OK;
	}
	
	public String getDomain() {
		return m_domain.toString();
	}
	
	public CrError setDomain(String domain) {
		try {
			this.m_domain = new URL(domain);
		} catch (MalformedURLException e) {
			writeGenericLog("Malformed domain " + domain + " " + e.getMessage());
			return CrError.CR_MALFORM_URL;
		}
		
		return CrError.CR_OK;
	}
	
	public int get_priority() {
		return m_priority;
	}

	public void set_priority(int m_priority) {
		this.m_priority = m_priority;
	}
	
	public void assign(URLObject other) {
		this.set_downloadDuration(other.get_downloadDuration());
		this.set_priority(other.get_priority());
		this.setDomain(other.getDomain());
		this.setDuplicated(other.getDuplicated());
		this.setLink(other.getLink());
	}
	
	// Object overrides
	@Override
	public boolean equals(Object other) {
		URLObject otherLink = (URLObject)other;
		
		return this.m_link.equals(otherLink.m_link) &&
			this.m_domain.equals(otherLink.m_domain);
	}
}
