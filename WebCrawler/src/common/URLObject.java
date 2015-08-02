package common;

public class URLObject {
	private String link;
	private String domain;
	private Boolean duplicated;

	public URLObject()
	{
		this.link = null;
		this.domain = null;
		this.duplicated = null;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	// Object overrides
	@Override
	public boolean equals(Object other) {
		URLObject otherLink = (URLObject)other;
		
		return this.link.equals(otherLink.link) &&
				this.domain.equals(otherLink.domain);
	}
}
