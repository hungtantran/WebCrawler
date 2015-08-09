package common;

import static common.LogManager.writeGenericLog;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import common.ErrorCode.CrError;

public class URLObject {
	private int m_id;
	private String m_link;
	private URL m_domain;
	private boolean m_absolute;
	private int m_priority;
	private long m_extractedTime;
	private long m_crawledTime;
	private long m_downloadDuration;
	private int m_httpStatusCode;
	private long m_relevance;
	private long m_distanceFromRelevantPage;
	private int m_freshness;

	public URLObject()
	{
		this.m_link = null;
		this.m_domain = null;
		this.m_absolute = false;
		this.m_id = -1;
		this.m_priority = Integer.MIN_VALUE;
		this.m_extractedTime = Integer.MIN_VALUE;
		this.m_crawledTime = Integer.MIN_VALUE;
		this.m_downloadDuration = Integer.MIN_VALUE;
		this.m_httpStatusCode = -1;
		this.m_relevance = Integer.MIN_VALUE;
		this.m_distanceFromRelevantPage = Integer.MAX_VALUE;
		this.m_freshness = 0;
	}

	public void assign(URLObject other) {
		this.set_id(other.m_id);
		this.setLink(other.getLink());
		this.setDomain(other.getDomain());
		this.set_absolute(other.is_absolute());
		this.set_priority(other.get_priority());
		this.set_extractedTime(other.get_extractedTime());
		this.set_crawledTime(other.get_crawledTime());
		this.set_downloadDuration(other.get_downloadDuration());
		this.set_httpStatusCode(other.get_httpStatusCode());
		this.set_relevance(other.get_relevance());
		this.set_distanceFromRelevantPage(other.get_distanceFromRelevantPage());
		this.set_freshness(other.get_freshness());
	}

	public int get_freshness() {
		return m_freshness;
	}

	public void set_freshness(int freshness) {
		this.m_freshness = freshness;
	}

	public long get_distanceFromRelevantPage() {
		return m_distanceFromRelevantPage;
	}

	public void set_distanceFromRelevantPage(long distanceFromRelevantPage) {
		this.m_distanceFromRelevantPage = distanceFromRelevantPage;
	}

	public long get_relevance() {
		return m_relevance;
	}

	public void set_relevance(long relevance) {
		this.m_relevance = relevance;
	}

	public long get_downloadDuration() {
		return m_downloadDuration;
	}

	public void set_downloadDuration(long downloadDuration) {
		this.m_downloadDuration = downloadDuration;
	}

	public int get_httpStatusCode() {
		return m_httpStatusCode;
	}

	public void set_httpStatusCode(int httpStatusCode) {
		this.m_httpStatusCode = httpStatusCode;
	}
	
	public String getLink() {
		if (m_link == null) {
			return null;
		}

		return m_link.toString();
	}
	
	public String getAbsoluteLink() {
		String absoluteLink = null; 

		if (!m_absolute) {
			try {
				URL absoluteURL = new URL(this.m_domain, this.m_link.toString());
				absoluteLink = absoluteURL.toString();
			} catch (MalformedURLException e) {
				return null;
			}
		} else {
			absoluteLink = this.m_link.toString();
		}
		
		if (absoluteLink.endsWith("/")) {
			absoluteLink = absoluteLink.substring(0, absoluteLink.length() - 1);
		}
		
		return absoluteLink;
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
		String domain = m_domain.toString();
		if (domain.endsWith("/")) {
			domain = domain.substring(0, domain.length() - 1);
		}
		
		return domain;
	}
	
	public CrError setDomain(String domain) {
		// If the link is already absolute, the domain value is already set
		if (this.m_absolute) {
			return CrError.CR_OK;
		}

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

	public void set_priority(int priority) {
		this.m_priority = priority;
	}
	
	public long get_extractedTime() {
		return m_extractedTime;
	}

	public void set_extractedTime(long extractedTime) {
		this.m_extractedTime = extractedTime;
	}

	public long get_crawledTime() {
		return m_crawledTime;
	}

	public void set_crawledTime(long crawledTime) {
		this.m_crawledTime = crawledTime;
	}
	
	public int get_id() {
		return m_id;
	}

	public void set_id(int id) {
		this.m_id = id;
	}

	public boolean is_absolute() {
		return m_absolute;
	}

	public void set_absolute(boolean absolute) {
		this.m_absolute = absolute;
	}
	
	// Object overrides
	@Override
	public boolean equals(Object other) {
		URLObject otherLink = (URLObject)other;
		
		return this.m_link.equals(otherLink.m_link) &&
			this.m_domain.equals(otherLink.m_domain);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Id = " + m_id + ", ");
		
		if (m_link != null) {
			builder.append("Link = " + m_link + ", ");
		} else {
			builder.append("Link = null, ");
		}
		
		if (m_domain != null) {
			builder.append("Domain = " + m_domain.toString() + ", ");
		} else {
			builder.append("Domain = null, ");
		}
		
		builder.append("Absolute = " + m_absolute + ", ");
		builder.append("Priority = " + m_priority + ", ");
		builder.append("ExtractedTime = " + m_extractedTime + ", ");
		builder.append("Download Duration = " + m_downloadDuration + ", ");
		
		return builder.toString();
	}
}
