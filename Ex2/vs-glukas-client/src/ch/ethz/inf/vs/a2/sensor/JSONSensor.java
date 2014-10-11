package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.TemperatureActivity;
import ch.ethz.inf.vs.a2.http.HttpRawRequestFactory;
import ch.ethz.inf.vs.a2.http.Requester;
import ch.ethz.inf.vs.a2.http.RequesterSocketImpl;

public class JSONSensor extends GenericSensor {

	protected JSONSensor() {
		super(new RequesterSocketImpl(HttpRawRequestFactory.getJsonInstance().generateRequest(	
				TemperatureActivity.HOST_TEMPERATURE,
				TemperatureActivity.PORT_TEMPERATURE, 
				TemperatureActivity.PATH_TEMPERATURE)), new JSONSensorResponseParser());
	}
	
}
