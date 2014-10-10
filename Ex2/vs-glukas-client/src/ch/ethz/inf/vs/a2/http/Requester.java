package ch.ethz.inf.vs.a2.http;

/**
 * Classes that implement this interface should take an object that represents an HTTP request, execute this request and return its response.
 * The request should either be taken during construction or by a setter method.
 * A Requester must be ready to execute a new request after it has returned from exectueRequest.
 * 
 * @author Leyna Sadamori
 *
 */
public interface Requester {
	/**
	 * Executes the stored request and returns its response
	 * @return A String representation of the response
	 */
	public String executeRequest() throws NullPointerException;
}
