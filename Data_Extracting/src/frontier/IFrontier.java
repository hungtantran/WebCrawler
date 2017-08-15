package frontier;

import common.ErrorCode;
import database.RawHTML;

public interface IFrontier {
    ErrorCode.CrError pullRawHTML(RawHTML rawHTML);
}
