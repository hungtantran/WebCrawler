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
	private Boolean m_duplicated;
	private Boolean m_absolute;
	private int m_priority;

	public Boolean getDuplicated() {
		return m_duplicated;
	}

	public void setDuplicated(Boolean duplicated) {
		this.m_duplicated = duplicated;
	}

	public URLObject()
	{
		this.m_link = null;
		this.m_domain = null;
		this.m_duplicated = null;
	}
	
	public String getLink() {
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
		} catch (URISyntaxException e) {
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
	
	// Object overrides
	@Override
	public boolean equals(Object other) {
		URLObject otherLink = (URLObject)other;
		
		return this.m_link.equals(otherLink.m_link) &&
			this.m_domain.equals(otherLink.m_domain);
	}
}
