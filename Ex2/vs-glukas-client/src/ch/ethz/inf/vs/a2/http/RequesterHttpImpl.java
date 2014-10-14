package ch.ethz.inf.vs.a2.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class RequesterHttpImpl implements Requester {

	protected HttpUriRequest request;
	protected String result = "";
	int responseCode;

	/**
	 * Subclasses can use this constructor if they want to set their own request
	 * When calling executeRequest, this.request must be non null or there will
	 * be a null pointer exception
	 */
	protected RequesterHttpImpl() {
		this.request = null;
	}

	public RequesterHttpImpl(String request) {
		this.request = new HttpGet(request);

	}

	@Override
	public String executeRequest() throws NullPointerException {

		HttpClient client = new DefaultHttpClient();

		// Adding Headers
		this.request.setHeader("Connection", "close");
		this.request.addHeader("Accept", "text/html");

		HttpResponse response;

		try {
			response = client.execute(this.request);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					result = EntityUtils.toString(entity);
				}
			} else {
				Log.e(this.toString(),
						String.format("Http code %d", responseCode));
			}
			// release resources
			if (response.getEntity() != null) {
				response.getEntity().consumeContent();
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return result;
	}

}
