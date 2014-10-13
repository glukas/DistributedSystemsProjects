package ch.ethz.inf.vs.a2.http;


import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


public class RequesterXml implements Requester{

	private XmlRequest request;
	private HttpClient client;
	
	public RequesterXml(XmlRequest requestArg) {
		this.request = requestArg;
		client = new DefaultHttpClient();
	}
	
	@Override
	public String executeRequest() throws NullPointerException {
		try {
			HttpResponse response = client.execute(request.getRequest());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//String returnValue = makeSomethingClever(response);
		return null;
	}

}
