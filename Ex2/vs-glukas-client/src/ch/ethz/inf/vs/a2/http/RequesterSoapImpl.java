package ch.ethz.inf.vs.a2.http;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class RequesterSoapImpl implements Requester{
	String response;
	String SOAP_ACTION = "";
	String METHOD_NAME= "getSpot";
	String NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
	String URL = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?WSDL";
	@Override
	public String executeRequest() throws NullPointerException {
		// TODO Auto-generated method stub
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	//	PropertyInfo newprop = new PropertyInfo();
	
		
		 request.addProperty("id", "Spot4"); 
	//	 request.addAttribute("parameters","Spot4");
		//request.setProperty(0, "Spot4");
		 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); 
         envelope.setOutputSoapObject(request);  
        
         
         HttpTransportSE httpTransport = new HttpTransportSE(URL);
         httpTransport.debug = true;
         try {
			httpTransport.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         SoapObject result = null;
         if (envelope.bodyIn != null)
        	 result = (SoapObject)envelope.bodyIn;
	
        
         response = result.toString();
        //Log.v("App",""+result.toString());
		return response;
	}

}
