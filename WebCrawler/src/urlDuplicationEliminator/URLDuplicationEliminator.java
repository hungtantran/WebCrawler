package urlDuplicationEliminator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.URLObject;
import database.IDatabaseConnection;
import unittest.Test_IURLDuplicationEliminator;

public class URLDuplicationEliminator implements IURLDuplicationEliminator {
	private static Logger LOG = LogManager.getLogger(URLDuplicationEliminator.class.getName());

	private int[] m_bloomFilter;
	private int m_bloomFilterByteSize;
	private String m_bloomfilterFileName = null;
	private int m_numWriteToBloomfilter = 0;
	
	public URLDuplicationEliminator(String bloomFilterDirectory, String bloomFilterFileName, int bloomFilterSizeInMB, IDatabaseConnection databaseConnection) {
		LOG.setLevel(Level.ALL);
		m_bloomFilterByteSize = bloomFilterSizeInMB * 1024 * 1024;
		if (m_bloomFilterByteSize <= 0) {
			m_bloomFilterByteSize = Integer.MAX_VALUE;
		}

		int numElems = m_bloomFilterByteSize / 4;
		boolean createNewBloomfilter = true;
		
		// If the directory and filename exists, check to see if we can deserialize existing bloomfilter
		if (bloomFilterDirectory != null && bloomFilterFileName != null) {
			m_bloomfilterFileName = bloomFilterDirectory + Globals.PATHSEPARATOR + bloomFilterFileName;
	
			// Exit if can't create directory/file and the directory/file hasn't already been existed
			File dir = Helper.createDir(bloomFilterDirectory);
			if (dir == null) {
				System.out.println("Can't create log folder");
				System.exit(1);
			}
			
			// If file exists, deserialize the bloomfilter from that. Otherwises, recreate it
			
			if (Helper.fileExists(m_bloomfilterFileName)) {
				try {
					FileInputStream bloomFilterInputStream = new FileInputStream(m_bloomfilterFileName);
					ObjectInputStream inputOStream = new ObjectInputStream(bloomFilterInputStream);
					m_bloomFilter = (int[]) inputOStream.readObject();
					
					if (m_bloomFilter.length == numElems) {
						LOG.info("Reuse existing bloomfilter");
						createNewBloomfilter = false;
					}
					
					inputOStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
					System.exit(1);
				}
			}
		}
		
		// Create new bloomfilter if needed
		if (createNewBloomfilter) {
			LOG.info("Create new bloomfilter");
			m_bloomFilter = new int[numElems];
			for (int i = 0; i < numElems; ++i) {
				m_bloomFilter[i] = 0;
			}
		}
		
		new ReentrantReadWriteLock();
	}
	
	@Override
	public CrError eliminateDuplicatedURLs(URLObject originalUrl, ArrayList<URLObject> inoutUrls) {
		Set<String> urls = new HashSet<>();
		// Don't recursively include itself in the new to-be-crawl link set
		urls.add(originalUrl.getAbsoluteLink());
		
		boolean changeToBloomfilter = false;

		for (Iterator<URLObject> iterator = inoutUrls.iterator(); iterator.hasNext();) {
			URLObject url = iterator.next();
			String absoluteLink = url.getAbsoluteLink();

			// Remove duplicated links in the list itself first
			if (urls.contains(absoluteLink)) {
				iterator.remove();
				continue;
			} else {
				urls.add(absoluteLink);
			}

			int urlHashCode = Math.abs(Helper.encryptString(absoluteLink).hashCode());
			
			int indexInArray = (urlHashCode / 32) % (m_bloomFilterByteSize / 4);
			int bitPosInInt = urlHashCode % 32;
			boolean exists = false;
			
			synchronized(m_bloomFilter) {
				int val = m_bloomFilter[indexInArray];
				
				val = val >> bitPosInInt;
				val = val << 31;
				if (val != 0) {
					exists = true;
				} else {
					int tmp = 1 << bitPosInInt;
					m_bloomFilter[indexInArray] = m_bloomFilter[indexInArray] + tmp;
					++m_numWriteToBloomfilter;
					changeToBloomfilter = true;
				}
			}
			
			// If bloomfilter said it exists, check with the database itself
			if (exists) {
				iterator.remove();
			}
		}
		
		// If bloomfilter file exists, check if we should write to it yet
		if (m_bloomfilterFileName != null) {
			synchronized(m_bloomFilter) {
				try {
					if (changeToBloomfilter && m_numWriteToBloomfilter >= Globals.MAXWRITETOBLOOMFILTERBEFOREFLUSHINGTODISK) {
						LOG.info("Number of write to bloomfilter : " + m_numWriteToBloomfilter + ", flush to disk");
						
						File bloomFilterFile = Helper.createFile(this.m_bloomfilterFileName);
						if (bloomFilterFile != null) {
							if (!bloomFilterFile.delete()) {
								LOG.error("Can't delete bloomfilter file " + m_bloomfilterFileName);
								System.exit(1);
							} else {
								LOG.info("Delete old bloomfilter");
							}
						}
						
						FileOutputStream bloomfilterOutputStream = new FileOutputStream(m_bloomfilterFileName);
						ObjectOutputStream outputOStream = new ObjectOutputStream(bloomfilterOutputStream);
						outputOStream.writeObject(m_bloomFilter); 
						outputOStream.flush(); 
						outputOStream.close();
						
						LOG.info("Flush new bloomfilter file");
	
						m_numWriteToBloomfilter = 0;					
					}
				} catch(Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
					System.exit(1);
				}
			}
		}

		return null;
	}

}
