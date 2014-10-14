package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.RequesterSoapImpl;

public class SoapSensor extends GenericSensor{
	
	
	public SoapSensor(){
		super(new RequesterSoapImpl(), new SOAPResponseParser());
	
		
		
	}
	
	
	
	

}
