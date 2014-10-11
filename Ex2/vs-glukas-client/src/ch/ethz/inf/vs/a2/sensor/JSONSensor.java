package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.Requester;

public class JSONSensor extends GenericSensor {

	protected JSONSensor() {
		//TODO use appropriate modification of Requester (almost the same as http requester / only different header) instead of null
		super(null, new JSONSensorResponseParser());
	}
	
}
