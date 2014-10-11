package ch.ethz.inf.vs.a2.http;

import java.util.LinkedList;
import java.util.Queue;
import android.util.Pair;

public class RequesterImpl implements Requester {

	private Queue<Pair<HttpSocket, String>> queue;
	
	public RequesterImpl(){
		super();
		this.queue = new LinkedList<Pair<HttpSocket, String>>();
	}
	
	public void addRequest(HttpSocket httpSocket, String request){
		queue.add(new Pair<HttpSocket, String>(httpSocket, request));
	}
	
	
	@Override
	public String executeRequest() throws NullPointerException {
		Pair<HttpSocket, String> toExecute = queue.remove();
		return toExecute.first.execute(toExecute.second);
	}
}
