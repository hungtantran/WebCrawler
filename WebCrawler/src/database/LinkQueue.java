package database;

import common.Helper;
import common.URLObject;

public class LinkQueue {
	// Constants
	// ----------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// Properties
	// ---------------------------------------------------------------------------------

	private Integer m_id;
	private String m_link;
	private String m_originalLink;
	private Integer m_domainTableId1;
	private Integer m_priority;
	private Integer m_persistent;
	private Long m_extractedTime;
	private Long m_relevance;
	private Long m_distanceFromRelevantPage;
	private Integer m_freshness;
	private String m_timeCrawled;
	private String m_dateCrawled;

	public void Assign(URLObject url) {
		this.setLink(url.getAbsoluteLink());
		this.set_originalLink(url.get_originalLink());
		this.setPersistent(0);
		this.setPriority(url.get_priority());
		this.set_extractedTime(url.get_extractedTime());
		this.set_relevance(url.get_relevance());
		this.set_distanceFromRelevantPage(url.get_distanceFromRelevantPage());
		this.set_freshness(url.get_freshness());
		this.setTimeCrawled(Helper.getCurrentTime());
		this.setDateCrawled(Helper.getCurrentDate());
	}
	
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
	
	public String get_originalLink() {
		return m_originalLink;
	}

	public void set_originalLink(String originalLink) {
		this.m_originalLink = originalLink;
	}

	public Integer getPriority() {
		return m_priority;
	}

	public void setPriority(Integer priority) {
		this.m_priority = priority;
	}
	
	public Integer getPersistent() {
		return m_persistent;
	}

	public void setPersistent(Integer persistent) {
		this.m_persistent = persistent;
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

	public Long get_relevance() {
		return m_relevance;
	}

	public void set_relevance(Long relevance) {
		this.m_relevance = relevance;
	}

	public Long get_distanceFromRelevantPage() {
		return m_distanceFromRelevantPage;
	}

	public void set_distanceFromRelevantPage(Long distanceFromRelevantPage) {
		this.m_distanceFromRelevantPage = distanceFromRelevantPage;
	}

	public Integer get_freshness() {
		return m_freshness;
	}

	public void set_freshness(Integer freshness) {
		this.m_freshness = freshness;
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
