package ch.ethz.inf.vs.a2.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class RequesterHttpImpl implements Requester{

	protected String request;
	protected String result;
	
	
	public RequesterHttpImpl(String request) {
		this.request = request;
		
		}

	@Override
	public String executeRequest() throws NullPointerException {
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new  HttpGet(this.request);
		try {
			HttpResponse response = client.execute(request);
			result = response.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		// TODO Auto-generated method stub
		return result;
	}
	
	
	
	
	
	
}
