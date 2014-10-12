package ch.ethz.inf.vs.a2.http;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * Classes that implement this interface should take an object that represents an HTTP request, execute this request and return its response.
 * The request should either be taken during construction or by a setter method.
 * A Request is not necessarily reusable.
 * 
 * @author Leyna Sadamori
 *
 */
public interface Requester {
	/**
	 * Executes the stored request and returns its response
	 * @return A String representation of the response
	 * @throws ClientProtocolException 
	 * @throws IOException 
	 */
	public String executeRequest() throws NullPointerException;
}
