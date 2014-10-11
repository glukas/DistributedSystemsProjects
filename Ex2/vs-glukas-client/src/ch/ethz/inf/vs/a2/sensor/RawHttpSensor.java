package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.TemperatureActivity;
import ch.ethz.inf.vs.a2.http.RequesterSocketImpl;
import ch.ethz.inf.vs.a2.http.HttpRawRequestFactory;

public class RawHttpSensor extends GenericSensor {
	
	public RawHttpSensor(){
		
		super(new RequesterSocketImpl(HttpRawRequestFactory.getInstance().generateRequest(	
				TemperatureActivity.HOST_TEMPERATURE,
				TemperatureActivity.PORT_TEMPERATURE, 
				TemperatureActivity.PATH_TEMPERATURE)), null);
		
		/*super(new RequesterImpl(HttpRawRequestFactory.getInstance().generateRequest(	
				TemperatureActivity.HOST_TEMPERATURE,
				TemperatureActivity.PORT_TEMPERATURE, 
				TemperatureActivity.PATH_TEMPERATURE)), new ResponseParser());
		*/
		
	}

	@Override
	public double parseResponse(String response) {
		//Log.v("RAW HTTP SENSOR", response);
		//super.parseResponse(response);
		return 0;
	}

}
