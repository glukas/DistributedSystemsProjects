package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;
import ch.ethz.inf.vs.a2.http.HTTPRequester;
import ch.ethz.inf.vs.a2.http.HttpRawRequest;
import ch.ethz.inf.vs.a2.http.HttpRawRequestFactory;
import ch.ethz.inf.vs.a2.http.HttpSocketFactory;
import ch.ethz.inf.vs.a2.http.HttpSocket;
import ch.ethz.inf.vs.a2.http.Requester;
import ch.ethz.inf.vs.a2.http.RequesterImpl;

public class RawHttpSensor extends GenericSensor {
	
	public RawHttpSensor(){
		
		super(new HTTPRequester(HttpRawRequestFactory.getInstance().generateRequest("vslab.inf.ethz.ch", 8081, "sunspots/Spot1")), null);
		
	}

	//TODO instead of overriding you should pass an appropriate ResponseParser as an argument to super in the constructor
	@Override
	public double parseResponse(String response) {
		Log.v("RAW HTTP SENSOR", response);
		return 0;
	}

}
