package urlDuplicationEliminator;

import java.util.ArrayList;

import common.URLObject;
import common.ErrorCode.CrError;

public interface IURLDuplicationEliminator {
	public CrError eliminateDuplicatedURLs(ArrayList<URLObject> inoutUrls);
}
