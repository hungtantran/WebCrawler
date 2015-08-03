package common;

public class URLObject {
	private String m_link;
	private String m_domain;
	private Boolean m_duplicated;

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
		return m_link;
	}
	
	public void setLink(String link) {
		this.m_link = link;
	}
	
	public String getDomain() {
		return m_domain;
	}
	
	public void setDomain(String domain) {
		this.m_domain = domain;
	}
	
	// Object overrides
	@Override
	public boolean equals(Object other) {
		URLObject otherLink = (URLObject)other;
		
		return this.m_link.equals(otherLink.m_link) &&
				this.m_domain.equals(otherLink.m_domain);
	}
}
