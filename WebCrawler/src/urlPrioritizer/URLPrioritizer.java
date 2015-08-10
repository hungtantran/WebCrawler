package urlPrioritizer;

import java.util.ArrayList;

import com.mysql.jdbc.StringUtils;

import common.ErrorCode.CrError;
import common.Globals;
import common.IWebPage;
import common.URLObject;
import database.IDatabaseConnection;

public class URLPrioritizer implements IURLPrioritizer {
	public URLPrioritizer(IDatabaseConnection databaseConnection) {
	}

	private long computeRelevanceScore(String text) {
		long score = 0;
		
		int index = StringUtils.indexOfIgnoreCase(text, "microsoft");
		if (index != -1) {
			score = Globals.MAXRELEVANCESCORE;
		} else {
			score = 0;
		}
		
		return score;
	}
	
	@Override
	public CrError prioritizeUrl(URLObject originalUrl, ArrayList<URLObject> inoutUrls) {
		IWebPage originalWebpage = originalUrl.get_webPage();
		
		if (originalWebpage != null) {
			long relevanceScore = computeRelevanceScore(originalWebpage.getString());
			relevanceScore = (relevanceScore * 2 + originalUrl.get_relevance()) / 3;
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
			long relevanceScore = computeRelevanceScore(originalWebpage.getString());
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
