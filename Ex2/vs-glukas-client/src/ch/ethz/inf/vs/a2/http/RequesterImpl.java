package ch.ethz.inf.vs.a2.http;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import android.util.Pair;

public class RequesterImpl implements Requester {

	private Queue<Pair<HttpSocket, String>> queueRequests;
	private Hashtable<Long, String> replies;
	
	
	public RequesterImpl(){
		super();
		this.queueRequests = new LinkedList<Pair<HttpSocket, String>>();
		this.replies = new Hashtable<Long, String>();
	}
	
	public void addRequest(HttpSocket httpSocket, String request){
		queueRequests.add(new Pair<HttpSocket, String>(httpSocket, request));
	}
	
	
	@Override
	public String executeRequest() throws NullPointerException {
		ExecuterThread t = new ExecuterThread();
		long id = t.getId();
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return replies.get(id);
	}
	
	private class ExecuterThread extends Thread{
		@Override
		public void run(){
			Pair<HttpSocket, String> toExecute = queueRequests.remove();
			String reply = toExecute.first.execute(toExecute.second);
			replies.put(this.getId(), reply);
		}
	}
}
