package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.HttpRawRequest;
import ch.ethz.inf.vs.a2.http.HttpRawRequestFactory;
import ch.ethz.inf.vs.a2.http.HttpSocketFactory;
import ch.ethz.inf.vs.a2.http.HttpSocket;
import ch.ethz.inf.vs.a2.http.RequesterImpl;

public class RawHttpSensor extends AbstractSensor {

	RequesterImpl requester;
	HttpRawRequest rawRequest;
	String reply;
	AsyncWorker asyncWorker;
	
	public RawHttpSensor(){
		super();
		requester = new RequesterImpl();
		rawRequest = HttpRawRequestFactory.getInstance();
		asyncWorker = new AsyncWorker ();
	}
	
	@Override
	public void getTemperature() {
		HttpSocket hsi = HttpSocketFactory.getInstance();
		//hard coded host, port and request... Bad!
		hsi.setHost("vslab.inf.ethz.ch");
		hsi.setPort(8081);
		requester.addRequest(hsi, rawRequest.generateRequest("vslab.inf.ethz.ch", 8081, "sunspots/Spot1"));

		//execute the request (requester works async), then you get something to parse
		String reply = requester.executeRequest();
		System.err.println(reply);
		
		//if you try with the asyncWorker, it crashes:
		/*
		//launch worker in background
		reply = asyncWorker.doInBackground(requester);
		//busy wait
		while(reply == null){
			
		}
		System.err.println(reply);
		*/
	}

	@Override
	public double parseResponse(String response) {
		// TODO Auto-generated method stub
		return 0;
	}

}
