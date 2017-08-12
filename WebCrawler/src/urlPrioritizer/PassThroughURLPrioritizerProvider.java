package urlPrioritizer;

import common.ErrorCode;
import common.Globals;
import common.URLObject;

import java.util.ArrayList;

public class PassThroughURLPrioritizerProvider implements IURLPrioritizer, IURLPrioritizerProvider {
    public PassThroughURLPrioritizerProvider() {
    }

    @Override
    public IURLPrioritizer newURLPrioritizer() {
        return new PassThroughURLPrioritizerProvider();
    }

    @Override
    public ErrorCode.CrError prioritizeUrl(URLObject originalUrl, ArrayList<URLObject> inoutUrls) {
        for (URLObject inoutUrl : inoutUrls) {
            inoutUrl.set_priority(1);
            inoutUrl.set_relevance(Globals.MAXRELEVANCESCORE);
            inoutUrl.set_distanceFromRelevantPage(0);
        }
        return ErrorCode.CrError.CR_OK;
    }

    @Override
    public ErrorCode.CrError prioritizeUrl(URLObject originalUrl, URLObject inoutUrl) {
        inoutUrl.set_priority(1);
        inoutUrl.set_relevance(Globals.MAXRELEVANCESCORE);
        return ErrorCode.CrError.CR_OK;
    }
}
