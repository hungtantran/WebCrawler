package urlPrioritizer;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import database.IDatabaseConnection;
import common.URLObject;

import static common.ErrorCode.*;

public class URLPrioritizer implements IURLPrioritizer {
	private IDatabaseConnection m_databaseConnection = null;
	
	public URLPrioritizer(IDatabaseConnection databaseConnection) {
		m_databaseConnection = databaseConnection;
	}

	@Override
	public CrError prioritizeUrl(ArrayList<URLObject> inoutUrls) {
		CrError hr = CrError.CR_OK;

		for (URLObject inoutUrl : inoutUrls) {
			hr = prioritizeUrl(inoutUrl);
			if (FAILED(hr)) {
				return hr;
			}
		}
		
		return CrError.CR_OK;
	}

	@Override
	public CrError prioritizeUrl(URLObject inoutUrl) {
		// TODO have some priority logic here
		inoutUrl.set_priority(1);

		return CrError.CR_OK;
	}

}
