package ch.ethz.inf.vs.a2.http;

import org.apache.http.client.methods.HttpPost;

public class XmlRequest {

	private HttpPost request;
	
	public XmlRequest(){
		request = null;
	}
	
	public HttpPost getRequest(){
		return request;
	}
}
