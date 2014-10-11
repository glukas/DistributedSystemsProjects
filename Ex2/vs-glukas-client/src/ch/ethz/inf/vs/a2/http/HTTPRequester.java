package ch.ethz.inf.vs.a2.http;

public class HTTPRequester implements Requester {

	final HttpSocket socket;
	final String request;
	
	public HTTPRequester(String request) {
		socket = HttpSocketFactory.getInstance();
		socket.setHost("vslab.inf.ethz.ch");
		socket.setPort(8081);
		
		this.request = request;
	}
	
	@Override
	public String executeRequest() throws NullPointerException {
		return socket.execute(request);
	}
	
}
