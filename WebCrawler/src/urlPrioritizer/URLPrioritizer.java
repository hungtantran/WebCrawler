package urlPrioritizer;

import static common.LogManager.writeGenericLog;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.ErrorCode.CrError;
import common.Globals;
import common.IWebPage;
import common.URLObject;
import database.IDatabaseConnection;

public class URLPrioritizer implements IURLPrioritizer {
	private final String[] m_relevantWords = { "microsoft", "excel", "windows", "xbox", "bing", "visual studio", "sql server", "internet explorer" };
	private final String[] m_headerTags = { "h1", "h2", "h3" };
	private final String m_descriptionTag = "meta[name=description]";
	private final String m_bodyTag = "body";

	public URLPrioritizer(IDatabaseConnection databaseConnection) {
	}
	
	private long computeHeaderScore(Document doc) {
		for (int i = 0; i < m_headerTags.length; ++i) {
			Elements headerElems = doc.select(m_headerTags[i]);
			if (headerElems.size() > 0) {
				for (Element header : headerElems) {
					for (String relevantWord : m_relevantWords) {
						if (header.text().toLowerCase().contains(relevantWord)) {
							return Globals.MAXRELEVANCESCORE;
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	private long computeDescriptionScore(Document doc) {
		Elements descriptionElems = doc.select(m_descriptionTag);
		if (descriptionElems.size() > 0) {
			for (Element description : descriptionElems) {
				for (String relevantWord : m_relevantWords) {
					if (description.attr("content").toLowerCase().contains(relevantWord)) {
						return (long) (Globals.MAXRELEVANCESCORE * 0.8);
					}
				}
			}
		}
		
		return 0;
	}
	
	private long computeBodyScore(Document doc) {
		Elements bodyElems = doc.select(m_bodyTag);
		if (bodyElems.size() > 0) {
			Element body = bodyElems.get(0);
			
			for (String relevantWord : m_relevantWords) {
				if (body.text().toLowerCase().contains(relevantWord)) {
					return (long) (Globals.MAXRELEVANCESCORE * 0.5);
				}
			}
		}
		
		return 0;
	}
	
	private long computeRelevanceScore(IWebPage webPage) {
		long score = 0;
		
		if (webPage == null) {
			return score;
		}
		
		Document doc = webPage.getDocument();
		if (doc == null) {
			return score;
		}
		
		long headerScore = computeHeaderScore(doc);
		long descriptionScore = 0;
		long bodyScore = 0;
		if (headerScore == Globals.MAXRELEVANCESCORE) {
			score = headerScore;
		} else {	
			descriptionScore = computeDescriptionScore(doc);
			bodyScore = computeBodyScore(doc);
			
			score = (long) ((descriptionScore * 3 + bodyScore) / 4);
		}
		
		writeGenericLog("Compute score for url " + webPage.get_originalUrl().getAbsoluteLink() + ": score = " + score + ", headerScore = " + headerScore + ", descriptionScore = " + descriptionScore + ", bodyScore = " + bodyScore);
		
		return score;
	}
	
	@Override
	public CrError prioritizeUrl(URLObject originalUrl, ArrayList<URLObject> inoutUrls) {
		IWebPage originalWebpage = originalUrl.get_webPage();
		
		if (originalWebpage != null) {
			long relevanceScore = computeRelevanceScore(originalWebpage);
			
			// Only dilute relevance score if it doesn't attain max score
			if (relevanceScore != Globals.MAXRELEVANCESCORE) {
				if (relevanceScore == 0) {
					relevanceScore = originalUrl.get_relevance() / 4;
				} else {
					relevanceScore = (relevanceScore * 2 + originalUrl.get_relevance()) / 3;
				}
			}

			originalUrl.set_relevance(relevanceScore);
			
			// If the original url exceeds the relevance threshold, it's relevant and its distance
			// to relevant page is now 0.
			if (originalUrl.get_relevance() > Globals.MINRELEVANCESCORETOBERELEVANT) {
				originalUrl.set_distanceFromRelevantPage(0);
			}
		}
		
		for (URLObject inoutUrl : inoutUrls) {
			// TODO have some priority logic here
			inoutUrl.set_priority(1);
			
			inoutUrl.set_relevance((long)(originalUrl.get_relevance() * Globals.RELEVANCEDECAYFACTOR));
			inoutUrl.set_distanceFromRelevantPage(originalUrl.get_distanceFromRelevantPage() + 1);
		}
		
		return CrError.CR_OK;
	}

	@Override
	public CrError prioritizeUrl(URLObject originalUrl, URLObject inoutUrl) {
		IWebPage originalWebpage = originalUrl.get_webPage();
		
		if (originalWebpage != null) {
			long relevanceScore = computeRelevanceScore(originalWebpage);
			relevanceScore = (relevanceScore * 2 + originalUrl.get_relevance()) / 3;
			originalUrl.set_relevance(relevanceScore);
			
			// If the original url exceeds the relevance threshold, it's relevant and its distance
			// to relevant page is now 0.
			if (originalUrl.get_relevance() > Globals.MINRELEVANCESCORETOBERELEVANT) {
				originalUrl.set_distanceFromRelevantPage(0);
			}
		}
		
		// TODO have some priority logic here
		inoutUrl.set_priority(1);
		
		inoutUrl.set_relevance((long)(originalUrl.get_relevance() * Globals.RELEVANCEDECAYFACTOR));
		inoutUrl.set_distanceFromRelevantPage(originalUrl.get_distanceFromRelevantPage() + 1);

		return CrError.CR_OK;
	}

}
