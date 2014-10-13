package ch.ethz.inf.vs.a2.http;

import org.apache.http.client.methods.HttpPost;

public class ManualSoapRequest {

	private HttpPost request;
	
	public ManualSoapRequest(){
		request = null;
	}
	
	public HttpPost getRequest(){
		return request;
	}
}
