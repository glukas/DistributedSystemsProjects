package ch.ethz.inf.vs.a2.http;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class RequesterXml extends RequesterHttpImpl {

	public RequesterXml(String requestXML, String url) {
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "text/xml");
		try {
			request.setEntity(new StringEntity(requestXML));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.request = request;
	}
}
