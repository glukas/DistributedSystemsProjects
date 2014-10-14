package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.RequesterXml;

public class XmlSensor extends GenericSensor {

	private static final String requestFormat = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
		    +"<S:Header/> "
		    +"<S:Body>"
		    +"    <ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\">"
		    +"        <id>Spot3</id>"
		    +"    </ns2:getSpot>"
		    +"</S:Body>"
		    +"</S:Envelope>";
	
	private static final String url = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";
	
	public XmlSensor(){
		super(new RequesterXml(requestFormat, url), new ResponseParserXmlImpl());
	}

	@Override
	public double parseResponse(String response) {
		return super.parseResponse(response);
	}
}
