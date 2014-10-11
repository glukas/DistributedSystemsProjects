package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.HttpRawRequest;
import ch.ethz.inf.vs.a2.http.HttpRawRequestFactory;
import ch.ethz.inf.vs.a2.http.HttpSocketImpl;
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
		HttpSocketImpl hsi = new HttpSocketImpl();
		hsi.setHost("vslab.inf.ethz.ch");
		hsi.setPort(8081);
		System.err.println("There");
		requester.addRequest(hsi, rawRequest.generateRequest("vslab.inf.ethz.ch", 8081, "sunspots/Spot1"));
		System.err.println("over");
		Thread t = new Thread(){
			@Override
			public void run(){
				reply = asyncWorker.doInBackground(requester);
			}
		};
		t.start();
		System.err.println("There over");
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println("Thread failed");
			e.printStackTrace();
		}
		System.err.println(reply);
	}

	@Override
	public double parseResponse(String response) {
		// TODO Auto-generated method stub
		return 0;
	}

}
