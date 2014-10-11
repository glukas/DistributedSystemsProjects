package ch.ethz.inf.vs.a2.sensor;

import org.json.JSONObject;

import android.util.Log;

public class JSONSensorResponseParser implements ResponseParser {

	@Override
	public double parseResponse(String response) {
		Log.v("JSON parsed Response", response);
		try {
			JSONObject parsedResponse = new JSONObject(response.substring(response.indexOf("{")));
			return parsedResponse.getDouble("value");
		} catch (Exception e) {
			return Double.NaN;
		}
	}

}
