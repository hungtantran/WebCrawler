package common;

public class URL {
	private String link;
	private String domain;

	public URL()
	{
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
		URL otherLink = (URL)other;
		
		return this.link.equals(otherLink.link) &&
				this.domain.equals(otherLink.domain);
	}
}
