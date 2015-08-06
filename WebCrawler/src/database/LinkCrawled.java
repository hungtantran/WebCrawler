package database;

public class LinkCrawled {
	// Constants
	// ----------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// Properties
	// ---------------------------------------------------------------------------------

	Integer m_id;
	String m_link;
	Integer m_priority;
	Integer m_domainTableId1;
	Long m_downloadDuration;
	Long m_extractedTime;
	String m_timeCrawled;
	String m_dateCrawled;

	// Getters/setters
	// ----------------------------------------------------------------------------

	public Integer getId() {
		return m_id;
	}

	public void setId(Integer id) {
		this.m_id = id;
	}

	public String getLink() {
		return m_link;
	}

	public void setLink(String link) {
		this.m_link = link;
	}

	public Integer getPriority() {
		return m_priority;
	}

	public void setPriority(Integer priority) {
		this.m_priority = priority;
	}

	public Integer getDomainTableId1() {
		return m_domainTableId1;
	}

	public void setDomainTableId1(Integer domainTableId1) {
		this.m_domainTableId1 = domainTableId1;
	}

	public String getTimeCrawled() {
		return m_timeCrawled;
	}

	public void setTimeCrawled(String timeCrawled) {
		this.m_timeCrawled = timeCrawled;
	}

	public String getDateCrawled() {
		return m_dateCrawled;
	}

	public void setDateCrawled(String dateCrawled) {
		this.m_dateCrawled = dateCrawled;
	}

	public Long get_extractedTime() {
		return m_extractedTime;
	}

	public void set_extractedTime(Long extractedTime) {
		this.m_extractedTime = extractedTime;
	}

	public Long get_downloadDuration() {
		return m_downloadDuration;
	}

	public void set_downloadDuration(Long downloadDuration) {
		this.m_downloadDuration = downloadDuration;
	}
	
	// Object overrides
	// ---------------------------------------------------------------------------

	/**
		 */
	@Override
	public boolean equals(Object other) {
		// TODO implement this
		return false;
	}

	/**
		 */
	@Override
	public int hashCode() {
		// TODO implement this
		return 0;
	}

	/**
		 */
	@Override
	public String toString() {
		// TODO implement this
		return this.m_link;
	}

	// Custom methods
	public boolean isValid() {
		return this.m_id != null && this.m_link != null
			&& this.m_domainTableId1 != null && this.m_timeCrawled != null
			&& this.m_dateCrawled != null;
	}
}
