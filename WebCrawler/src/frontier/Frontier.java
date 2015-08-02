package frontier;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import common.ErrorCode.CrError;
import common.URLObject;

public class Frontier implements IFrontier {
	private PriorityBlockingQueue<URLObject> urlsQueue;
	
	public Frontier()
	{
		urlsQueue = new PriorityBlockingQueue<URLObject>();
	}
	
	@Override
	public CrError pullUrl(URLObject outUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pushUrl(URLObject inUrl) {

		return null;
	}

	@Override
	public CrError pullUrls(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pushUrls(ArrayList<URLObject> inUrls) {
		// TODO Auto-generated method stub
		return null;
	}
}
