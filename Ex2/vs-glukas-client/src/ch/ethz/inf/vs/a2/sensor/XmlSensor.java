package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.RequesterManualSoap;

public class XmlSensor extends GenericSensor {


	public XmlSensor(){
		super(new RequesterManualSoap(), null);
		
	}

	@Override
	public double parseResponse(String response) {
		return -1;
	}
}
