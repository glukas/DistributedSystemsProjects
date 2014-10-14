package ch.ethz.inf.vs.a2.sensor;

public class SOAPResponseParser implements ResponseParser {
	int index;
	char[] result = {'a','b','.','c','d'};
	double value = 1.23;
	String test;
	
	//public void ResponseParserImpl(){
		
	//}
	
	@Override
	public double parseResponse(String response) {
		
		index = response.indexOf("temperature=");
		// In case we get the wrong page
		if (index == -1)
			return Double.NaN;
		
		
		index = index + 12;
		response.getChars(index, (index + 5), result , 0);
		StringBuilder sb = new StringBuilder();
		for (char c : result){
			if (c != '<')
			sb.append(c);
			else 
			break;
		}
		String temp  = sb.toString();
		//Log.v("rightvalue", temp );
		value = Double.valueOf(temp);
		//Log.v("value1", String.valueOf(response.charAt(index+4)));
		return value;
	}

}
