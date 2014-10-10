package ch.ethz.inf.vs.a2.http;

/**
 * An HttpSocket executes an HTTP request with a given destination host and port and returns the response. 
 * 
 * @author Leyna Sadamori
 *
 */
public interface HttpSocket {

	/**
	 * @return String representation of destination host address
	 */
	public String getHost();

	/**
	 * @param host String representation of destination host address
	 */
	public void setHost(String host);

	/**
	 * @return Destination port
	 */
	public int getPort();

	/**
	 * @param port Destination port
	 */
	public void setPort(int port);

	/**
	 * Execute the HTTP request and return the response
	 * @param request HTTP request
	 * @return HTTP response
	 */
	public String execute(String request);
}
