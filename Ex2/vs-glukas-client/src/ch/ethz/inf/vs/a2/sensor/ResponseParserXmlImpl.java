package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

public class ResponseParserXmlImpl implements ResponseParser{

	//TODO more proper xml parsing
	@Override
	public double parseResponse(String response) {
		String keyString = "<temperature>";
		int index = response.indexOf(keyString);
		if (index == -1) {
			return Double.NaN;
		}
		Double parsed = Double.parseDouble(response.substring(index+keyString.length(), index+keyString.length()+4));
		if (parsed == null) return Double.NaN;
		return parsed;
	}

}
