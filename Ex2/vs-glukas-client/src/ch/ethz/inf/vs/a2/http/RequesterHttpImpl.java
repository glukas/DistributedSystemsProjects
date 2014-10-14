package ch.ethz.inf.vs.a2.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class RequesterHttpImpl implements Requester{

	protected String request;
	protected String result = "";
	int responseCode;
	
	
	
	public RequesterHttpImpl(String request) {
		this.request = request;
		
		}

	@Override
	public String executeRequest() throws NullPointerException{
		
		HttpClient client = new DefaultHttpClient();
		HttpGet getrequest = new  HttpGet(this.request);
		// Adding Headers
		getrequest.setHeader("Connection", "close");
		getrequest.addHeader("Accept", "text/html");
		
		
		HttpResponse response;
		
		try {
			response = client.execute(getrequest);
			responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == 200)
			{
		        HttpEntity entity = response.getEntity();
		        if(entity != null)
		        
		        {
		         result = EntityUtils.toString(entity);             
		               }
		        response.getEntity().consumeContent();
			}
			
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
