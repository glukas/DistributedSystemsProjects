package ch.ethz.inf.vs.a2.http;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class RequesterManualSoap implements Requester{

	private HttpPost post;
	private HttpClient client;
	
	public RequesterManualSoap() {
		client = new DefaultHttpClient();
		post = new HttpPost("http://vslab.inf.ethz.ch");
	}
	
	@Override
	public String executeRequest() throws NullPointerException {
	    return null;
	}

}
