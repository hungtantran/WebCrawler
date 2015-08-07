package urlDuplicationEliminator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import common.ErrorCode.CrError;
import database.IDatabaseConnection;
import common.Globals;
import common.Helper;
import common.URLObject;

import static common.LogManager.*;

public class URLDuplicationEliminator implements IURLDuplicationEliminator {
	private int[] m_bloomFilter;
	private Set<String> m_setDuplicatedString;
	private int m_bloomFilterByteSize;
	private IDatabaseConnection m_databaseConnection = null;
	
	public URLDuplicationEliminator(IDatabaseConnection databaseConnection) {
		//TODO persist data onto disk or populate it in the database 
		m_bloomFilterByteSize = Globals.NMEGABYTESFORBLOOMFILTER * 1024 * 1024;
		if (m_bloomFilterByteSize <= 0) {
			m_bloomFilterByteSize = Integer.MAX_VALUE;
		}

		int numElems = m_bloomFilterByteSize / 4;
		m_bloomFilter = new int[numElems];
		for (int i = 0; i < numElems; ++i) {
			m_bloomFilter[i] = 0;
		}
		
		m_setDuplicatedString = new HashSet<String>();
		m_databaseConnection = databaseConnection;
	}
	
	@Override
	public CrError eliminateDuplicatedURLs(ArrayList<URLObject> inoutUrls) {
		Set<String> urls = new HashSet<String>();
		
		for (int i = 0; i < inoutUrls.size(); ++i) {
			URLObject url = inoutUrls.get(i);
			
			// Remove duplicated links in the list itself first
			if (urls.contains(url.getAbsoluteLink())) {
				inoutUrls.remove(i);
				--i;
				continue;
			} else {
				urls.add(url.getAbsoluteLink());
			}

			int urlHashCode = Math.abs(Helper.encryptString(url.getAbsoluteLink()).hashCode());
			
			int indexInArray = (urlHashCode / 32) % (m_bloomFilterByteSize / 4);
			int bitPosInInt = urlHashCode % 32;
			boolean exists = false;
			
			synchronized(m_bloomFilter) {
				int val = m_bloomFilter[indexInArray];
				
				val = val >> bitPosInInt;
				val = val << 31;
				if (val != 0) {
					if (!m_setDuplicatedString.contains(url.getAbsoluteLink())) {
						exists = false;
					} else {
						exists = true;
					}
				} else {
					int tmp = 1 << bitPosInInt;
					m_bloomFilter[indexInArray] = m_bloomFilter[indexInArray] + tmp;
					m_setDuplicatedString.add(url.getAbsoluteLink());
				}
			}
			
			// If bloomfilter said it exists, check with the database itself
			if (exists) {
				if (m_databaseConnection.checkURLDuplicationDatabase(url)) {
					inoutUrls.remove(i);
					--i;
				} else {
					writeGenericLog("Url " + url.getAbsoluteLink() + " got false positive");
				}
			} else {
				// Even if the bloomfilter said no, we still needs to check (refer to TODO at the beginning)
				if (m_databaseConnection.checkURLDuplicationDatabase(url)) {
					inoutUrls.remove(i);
					--i;
				}
			}
		}
		
		return null;
	}

}
