package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.RequesterXml;
import ch.ethz.inf.vs.a2.http.XmlRequest;

public class XmlSensor extends GenericSensor {


	public XmlSensor(){
		super(new RequesterXml(new XmlRequest()), new ResponseParserXmlImpl());
		
	}

	@Override
	public double parseResponse(String response) {
		return super.parseResponse(response);
	}
}
