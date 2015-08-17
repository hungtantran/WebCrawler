package urlFilter;

import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import common.ErrorCode.CrError;
import common.Globals;
import database.IDatabaseConnection;
import common.URLObject;

public class DefaultURLFilterProvider implements IURLFilter, IURLFilterProvider {
	private boolean m_filterNonExistingDomains = false;
	private Set<String> m_existingDomains;
	
	private boolean m_filterIncludeFileType = false;
	private Set<String> m_fileTypesToInclude = null;

	private boolean m_filterExcludeFileType = false;
	private Set<String> m_fileTypesToExclude = null;
	
	public DefaultURLFilterProvider() {
	}

	private boolean isExcludeFile(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.lastIndexOf('.'); 
		if (index == -1) {
			return false;
		}
		
		String extension = url.substring(index + 1, url.length()).toLowerCase();
		if (this.m_fileTypesToExclude.contains(extension)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isIncludeFile(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.lastIndexOf('.'); 
		if (index == -1) {
			return false;
		}
		
		String extension = url.substring(index + 1, url.length()).toLowerCase();
		if (this.m_fileTypesToInclude.contains(extension)) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isPositionLinkInPage(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.lastIndexOf("#");
		if (index == -1) {
			return false;
		}
		
		return true;
	}
	
	private static boolean isLinkWithQueryParameter(String url) {
		if (url == null) {
			return false;
		}
		
		int index = url.indexOf("?");
		if (index == -1) {
			return false;
		}
		
		return true;
	}
	
	private static boolean isHttpLink(String url) {
		if (url == null) {
			return false;
		}
		
		if (!url.startsWith("http")) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public CrError filterURLs(ArrayList<URLObject> inoutUrls) {
		for (int i = 0; i < inoutUrls.size(); ++i) {
			URLObject url = inoutUrls.get(i);

			// Filter out file link
			String absoluteLink = url.getAbsoluteLink();
			
			// TODO filter out using regex instead of separate functions like this
			// TODO filter non-english pages
			// TODO filter out javascript, mailto, etc... links
			boolean filterPage =
				DefaultURLFilterProvider.isLinkWithQueryParameter(absoluteLink) ||
				DefaultURLFilterProvider.isPositionLinkInPage(absoluteLink) ||
				!DefaultURLFilterProvider.isHttpLink(absoluteLink) ||
				Globals.BLACKLISTDOMAINSET.contains(url.getDomain());
			
			// Filter based on non-existing domains if enable
			if (!filterPage && m_filterNonExistingDomains && m_existingDomains != null) {
				filterPage = filterPage || !m_existingDomains.contains(url.getDomain());
			}
			
			// Filter based on excluding file type if enable
			if (!filterPage && m_filterExcludeFileType && m_fileTypesToExclude != null) {
				filterPage = filterPage || this.isExcludeFile(url.getAbsoluteLink());
			}
			
			// Filter based on including file type if enable
			if (!filterPage && m_filterIncludeFileType && m_fileTypesToInclude != null) {
				filterPage = filterPage || !this.isIncludeFile(url.getAbsoluteLink());
			}
			
			if (filterPage) {
				inoutUrls.remove(i);
				--i;
			}
		}
		
		return CrError.CR_OK;
	}

	@Override
	public IURLFilter newURLFilter() {
		return new DefaultURLFilterProvider();
	}

	@Override
	public CrError setDatabaseConnection(IDatabaseConnection databaseConnection) {
		return null;
	}

	@Override
	public CrError setFilterNonExistingDomains(Set<String> existingDomains) {
		m_filterNonExistingDomains = true;
		m_existingDomains = existingDomains;

		return CrError.CR_OK;
	}

	@Override
	public CrError setFileTypesToInclude(Set<String> fileTypesToInclude) {
		m_filterIncludeFileType = true;
		m_fileTypesToInclude = fileTypesToInclude;

		return null;
	}

	@Override
	public CrError setFileTypesToExclude(Set<String> fileTypesToExclude) {
		m_filterExcludeFileType = true;
		m_fileTypesToExclude = fileTypesToExclude;

		return null;
	}
}
