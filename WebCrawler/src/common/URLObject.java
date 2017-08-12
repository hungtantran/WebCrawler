package common;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.ErrorCode.CrError;
import proto.message.message.URLmessage;

public class URLObject {
	private static Logger LOG = LogManager.getLogger(URLObject.class.getName());

	private int m_id;
	private String m_link;
	private String m_originalLink;
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
	private IWebPage m_webPage;

	public static class URLObjectRelevanceAndPriorityComparator implements Comparator<URLObject> {
		public URLObjectRelevanceAndPriorityComparator() {
		}

		@Override
		public int compare(URLObject arg0, URLObject arg1) {
			if (arg0.get_priority() != arg1.get_priority()) {
				return arg0.get_priority() - arg1.get_priority();
			}
			
			if (arg0.get_relevance() > arg1.get_relevance()) {
				return 1;
			}
			
			return -1;
		}
	}
	
	public URLObject()
	{
		this.m_link = null;
		this.m_originalLink = null;
		this.m_domain = null;
		this.m_absolute = false;
		this.m_id = -1;
		this.m_priority = Integer.MIN_VALUE;
		this.m_extractedTime = Integer.MIN_VALUE;
		this.m_crawledTime = Integer.MIN_VALUE;
		this.m_downloadDuration = Integer.MIN_VALUE;
		this.m_httpStatusCode = -1;
		this.m_relevance = 0;
		this.m_distanceFromRelevantPage = Integer.MAX_VALUE;
		this.m_freshness = 0;
		this.m_webPage = null;
	}

	public void assign(URLObject other) {
		this.set_id(other.m_id);
		this.setLink(other.getLink());
		this.set_originalLink(other.get_originalLink());
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
		this.set_webPage(other.get_webPage());
	}
	
	public String get_originalLink() {
		return m_originalLink;
	}

	public void set_originalLink(String originalLink) {
		this.m_originalLink = originalLink;
	}
	
	public IWebPage get_webPage() {
		return m_webPage;
	}

	public void set_webPage(IWebPage webPage) {
		this.m_webPage = webPage;
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
				URL absoluteURL = null;
				if (this.m_originalLink == null) {
					absoluteURL = new URL(this.m_domain, this.m_link.toString());
				} else {
					// TODO remove this superhack of adding surfix
					String originalLink = this.m_originalLink.toString();
					/*if (!originalLink.endsWith("/"))
					{
						originalLink += "/";
					}*/

					absoluteURL = new URL(new URL(originalLink), this.m_link.toString());
				}

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
			LOG.error("Malformed link " + e.getMessage());
			return CrError.CR_MALFORM_URL;
		} catch (MalformedURLException e) {
			LOG.error("Malformed link " + e.getMessage());
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
			LOG.error("Malformed domain " + domain + " " + e.getMessage());
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
		
		if (m_link != null) {
			builder.append("Original Link = " + m_link + ", ");
		} else {
			builder.append("Original Link = null, ");
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
	
	public URLmessage toProtobufMessage() {
		return URLObject.convertToProtobufMessage(this);
	}
	
	public static URLmessage convertToProtobufMessage(URLObject url) {
		return URLmessage.newBuilder()
			.setId(url.get_id())
			.setLink(url.getLink())
			.setOriginalLink(url.get_originalLink())
			.setAbsolute(url.is_absolute())
			.setPriority(url.get_priority())
			.setExtractedTime(url.get_extractedTime())
			.setCrawledTime(url.get_crawledTime())
			.setDownloadDuration(url.get_downloadDuration())
			.setHttpStatusCode(url.get_httpStatusCode())
			.setRelevance(url.get_relevance())
			.setDistanceFromRelevantPage(url.get_distanceFromRelevantPage())
			.setFreshness(url.get_freshness())
			.build();
	}
	
	public static URLObject convertFromProtobufMessage(URLmessage url) {
		URLObject urlObject = new URLObject();

		urlObject.set_id(url.getId());
		urlObject.setLink(url.getLink());
		urlObject.set_originalLink(url.getOriginalLink());
		urlObject.set_absolute(url.getAbsolute());
		urlObject.set_priority(url.getPriority());
		urlObject.set_extractedTime(url.getExtractedTime());
		urlObject.set_crawledTime(url.getCrawledTime());
		urlObject.set_downloadDuration(url.getDownloadDuration());
		urlObject.set_httpStatusCode(url.getHttpStatusCode());
		urlObject.set_relevance(url.getRelevance());
		urlObject.set_distanceFromRelevantPage(url.getDistanceFromRelevantPage());
		urlObject.set_freshness(url.getFreshness());
		
		return urlObject;
	}
}
