package frontier;

import static common.ErrorCode.FAILED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.URLObject;
import database.IDatabaseConnection;

public class Frontier implements IFrontier {
    private static Logger LOG = LogManager.getLogger(Frontier.class.getName());

    // Back end queue that disperse urls to crawler threads
    private PriorityQueue<BackEndQueue> m_backEndQueues;

    // Front end queue that crawler threads enqueue into
    private Queue<URLObject> m_frontEndQueue;

    // Table mapping between domain and the backend queue
    private Map<String, BackEndQueue> m_domainToBackEndQueueMap;

    private int m_maxNumBackEndQueues;

    private IDatabaseConnection m_databaseConnection = null;

    public Frontier(int numQueues, IDatabaseConnection databaseConnection) {
        LOG.setLevel(Level.ALL);
        m_frontEndQueue = new LinkedList<URLObject>();
        m_backEndQueues = new PriorityQueue<BackEndQueue>(numQueues /* initialCapacity */, new BackEndQueue.BackEndQueueComparator());
        m_domainToBackEndQueueMap = new HashMap<String, BackEndQueue>();
        m_maxNumBackEndQueues = numQueues;
        m_databaseConnection = databaseConnection;
    }

    @Override
    public CrError releaseBackEndQueue(URLObject originalUrl) {
        synchronized (m_backEndQueues) {
            synchronized (m_domainToBackEndQueueMap) {
                if (m_domainToBackEndQueueMap.containsKey(originalUrl.getDomain())) {
                    BackEndQueue backEndQueue = m_domainToBackEndQueueMap.get(originalUrl.getDomain());

                    if (!m_backEndQueues.contains(backEndQueue)) {
                        LOG.info("Push back back end queue of domain " + backEndQueue.getDomain() + " with download duration " + originalUrl.get_downloadDuration());
                        backEndQueue.set_minNextProcessTimeInMillisec(Helper.getCurrentTimeInMillisec() + originalUrl.get_downloadDuration() * Globals.NPOLITENESSFACTOR);
                        m_backEndQueues.add(backEndQueue);
                    }
                }
            }
        }

        return CrError.CR_OK;
    }

    @Override
    public CrError pullUrl(URLObject outUrl) {
        ArrayList<URLObject> outUrls = new ArrayList<>();

        CrError hr = this.pullUrls(outUrls, 1);
        if (FAILED(hr)) {
            LOG.error("Pull url fail with hr = " + hr);
            return hr;
        }

        outUrl.assign(outUrls.get(0));
        LOG.info("Pull url " + outUrl.toString());

        return hr;
    }

    @Override
    public CrError pushUrl(URLObject originalUrl, URLObject inUrl) {
        synchronized (m_frontEndQueue) {
            m_frontEndQueue.add(inUrl);

            ArrayList<URLObject> inUrls = new ArrayList<>();
            inUrls.add(inUrl);
            CrError hr = m_databaseConnection.pushFrontierDatabase(inUrls);
            if (FAILED(hr)) {
                LOG.error("Fail to push url " + inUrl.getAbsoluteLink() + " into frontier database");
                return hr;
            }

            // Print out front end queue size every 100 times
            if (m_frontEndQueue.size() % 100 == 0) {
                LOG.info("Front end queue size : " + m_frontEndQueue.size());
            }
        }

        releaseBackEndQueue(originalUrl);

        return CrError.CR_OK;
    }

    @Override
    public CrError pullUrls(ArrayList<URLObject> outUrls, int maxNumUrls) {
        CrError hr = pullUrlsInternal(outUrls, maxNumUrls);

        if (FAILED(hr)) {
            LOG.error("Pull urls fail with hr = " + hr);
        }

        return hr;
    }

    public CrError pullUrlsInternal(ArrayList<URLObject> outUrls, int maxNumUrls) {
        BackEndQueue backEndQueue = null;
        CrError hr = CrError.CR_OK;

        synchronized (m_backEndQueues) {
            synchronized (m_domainToBackEndQueueMap) {
                while (true) {
                    while (m_backEndQueues.isEmpty()) {
                        if (m_domainToBackEndQueueMap.size() >= m_maxNumBackEndQueues) {
                            // Empty backend queue and the map is already full, return error
                            LOG.info("All back-end queues are in active. Can't dequeue any more url to crawl.");
                            return CrError.CR_EMPTY_QUEUE;
                        }

                        // Empty backend queue but the map is not full, try to get url from the front end queue
                        synchronized (m_frontEndQueue) {
                            if (m_frontEndQueue.isEmpty()) {
                                ArrayList<URLObject> frontierUrls = new ArrayList<>();
                                hr = m_databaseConnection.pullFrontierDatabase(frontierUrls, Globals.NMAXURLSFROMFRONTIERPERPULL);

                                if (FAILED(hr)) {
                                    LOG.error("Fail to pull urls from frontier");
                                    return hr;
                                }
                                m_frontEndQueue.addAll(frontierUrls);
                            }

                            if (m_frontEndQueue.isEmpty()) {
                                // Empty front end queue, there is no more url to crawl, return error
                                LOG.info("No more url in the front end queue to crawl");
                                return CrError.CR_EMPTY_QUEUE;
                            } else {
                                // Dequeue from the front end queue until we find a new web server that hasn't exists in the map yet
                                boolean foundNew = false;
                                while (true) {
                                    if (m_frontEndQueue.isEmpty()) {
                                        break;
                                    }

                                    URLObject curUrl = m_frontEndQueue.remove();
                                    if (curUrl.getDomain() == null) {
                                        continue;
                                    }

                                    // Ignore pages that are too far from relevant page
                                    if (curUrl.get_distanceFromRelevantPage() > Globals.MAXDISTANCEFROMRELEVANTPAGE) {
                                        LOG.info("Ignore link " + curUrl.getAbsoluteLink() + " too far from relevant page");
                                        continue;
                                    }

                                    if (!m_domainToBackEndQueueMap.containsKey(curUrl.getDomain()) && foundNew) {
                                        break;
                                    }

                                    if (!m_domainToBackEndQueueMap.containsKey(curUrl.getDomain()) && !foundNew) {
                                        LOG.info("Create new back end queue with new domain " + curUrl.getDomain());
                                        BackEndQueue newBackEndQueue = new BackEndQueue();
                                        newBackEndQueue.setDomain(curUrl.getDomain());
                                        newBackEndQueue.setPriority(curUrl.get_priority());

                                        m_backEndQueues.add(newBackEndQueue);
                                        m_domainToBackEndQueueMap.put(curUrl.getDomain(), newBackEndQueue);

                                        foundNew = true;
                                    }

                                    hr = m_domainToBackEndQueueMap.get(curUrl.getDomain()).pushUrl(curUrl);
                                    if (FAILED(hr)) {
                                        return hr;
                                    }
                                }

                                StringBuilder builder = new StringBuilder();
                                builder.append("Num backend queues : " + m_domainToBackEndQueueMap.size() + "\n");
                                for (Map.Entry<String, BackEndQueue> entry : m_domainToBackEndQueueMap.entrySet()) {
                                    builder.append("Domain " + entry.getKey() + " with queue size " + entry.getValue().size() + "\n");
                                }
                                LOG.info(builder.toString());
                            }
                        }
                    }

                    // The queue shouldn't be empty here
                    if (m_backEndQueues.isEmpty()) {
                        LOG.error("Back end queues is empty, unexpected");
                        System.exit(1);
                    }

                    backEndQueue = m_backEndQueues.remove();

                    if (backEndQueue.size() > 0) {
                        break;
                    } else {
                        m_domainToBackEndQueueMap.remove(backEndQueue.getDomain());
                    }
                }
            }
        }

        if (backEndQueue == null) {
            LOG.error("Back end queue is null unexpected");
            return CrError.CR_UNEXPECTED;
        }

        int numResults = 0;
        while (numResults < maxNumUrls) {
            URLObject url = new URLObject();

            hr = backEndQueue.pullUrl(url);
            if (FAILED(hr)) {
                LOG.error("Pull urls from backend queue fail with hr = " + hr);
                break;
            }

            outUrls.add(url);

            ++numResults;
        }

        // Wait for politeness to the webserver
        long minNextProcessTimeInMillisec = backEndQueue.get_minNextProcessTimeInMillisec();
        long waitDuration = minNextProcessTimeInMillisec - Helper.getCurrentTimeInMillisec();
        if (minNextProcessTimeInMillisec > 0 && waitDuration > 0) {
            if (waitDuration > Globals.MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC) {
                waitDuration = Globals.MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC;
            }

            if (waitDuration < Globals.MINWAITTIMETOPULLURLFROMFRONTIERINMILLISEC) {
                waitDuration = Globals.MINWAITTIMETOPULLURLFROMFRONTIERINMILLISEC;
            }

            Helper.waitMilliSec(waitDuration, waitDuration);
        }

        return CrError.CR_OK;
    }

    @Override
    public CrError pushUrls(URLObject originalUrl, ArrayList<URLObject> inUrls) {
        synchronized (m_frontEndQueue) {
            CrError hr = m_databaseConnection.pushFrontierDatabase(inUrls);
            if (FAILED(hr)) {
                LOG.error("Fail to push " + inUrls.size() + " urls extracted from url " + originalUrl.getAbsoluteLink() + " into frontier database");
                return hr;
            }

            m_frontEndQueue.addAll(inUrls);

            // Print out front end queue size every 100 times
            if (m_frontEndQueue.size() % 100 == 0) {
                LOG.info("Front end queue size : " + m_frontEndQueue.size());
            }
        }

        releaseBackEndQueue(originalUrl);

        return CrError.CR_OK;
    }
}
