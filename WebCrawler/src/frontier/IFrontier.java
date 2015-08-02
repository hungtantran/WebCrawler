package frontier;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IFrontier {
	public CrError pullUrl(URLObject outUrl);
	
	public CrError pushUrl(URLObject inUrl);
}
