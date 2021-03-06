package ch.ethz.inf.vs.a2.http;

import ch.ethz.inf.vs.a2.TemperatureActivity;

public class RequesterSocketImpl implements Requester {

	private final HttpSocket socket;
	private final String request;
	
	public RequesterSocketImpl(String request) {
		socket = HttpSocketFactory.getInstance();
		socket.setHost(TemperatureActivity.HOST_TEMPERATURE);
		socket.setPort(TemperatureActivity.PORT_TEMPERATURE);
		this.request = request;
	}
	
	
	@Override
	public String executeRequest() throws NullPointerException {
		return socket.execute(request);
	}
	
}
