package urlProcessor;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import common.ErrorCode.CrError;
import common.Globals;
import common.URLObject;

import static common.LogManager.*;

public class DownloadURLProvider implements IURLProcessor, IURLProcessorProvider {
	private static String getFileName(String url) {
		if (url == null) {
			return null;
		}
		
		int index = url.lastIndexOf('/');
		if (index == -1) {
			return null;
		}

		return url.substring(index + 1);
	}
	
	@Override
	public CrError processURLs(ArrayList<URLObject> inoutUrls) {
		for (URLObject url : inoutUrls) {
			try {
				String absoluteLink = url.getAbsoluteLink();
				if (absoluteLink == null) {
					continue;
				}

				URL website = new URL(absoluteLink);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				
				String fileName = DownloadURLProvider.getFileName(absoluteLink);
				if (fileName == null) {
					writeGenericLog("Can't extract filename from link " + absoluteLink);
				}
				
				String downloadLocation = "Download" + Globals.PATHSEPARATOR + fileName;
				FileOutputStream fos = new FileOutputStream(downloadLocation);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				
				writeGenericLog("Finish downloading link " + absoluteLink + " to " + downloadLocation);
			} catch (Exception e) {
				e.printStackTrace();
				writeGenericLog("Fail to download link " + url.getAbsoluteLink() + ", with error " + e.getMessage());
				continue;
			}
		}
		
		return CrError.CR_OK;
	}

	@Override
	public IURLProcessor newURLProcessor() {
		return new DownloadURLProvider();
	}
}
