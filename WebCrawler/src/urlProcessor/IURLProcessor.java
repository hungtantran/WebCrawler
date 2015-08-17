package urlProcessor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IURLProcessor {
	public CrError processURLs(ArrayList<URLObject> inoutUrls);
}
