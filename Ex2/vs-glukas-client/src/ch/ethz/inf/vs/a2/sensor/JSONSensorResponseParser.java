package ch.ethz.inf.vs.a2.sensor;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONSensorResponseParser implements ResponseParser {

	@Override
	public double parseResponse(String response) {
		
		try {
			JSONObject parsedResponse = new JSONObject(response);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = parsedResponse.keys();
			String key = keys.next();
			return parsedResponse.getDouble(key);
			
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

}
