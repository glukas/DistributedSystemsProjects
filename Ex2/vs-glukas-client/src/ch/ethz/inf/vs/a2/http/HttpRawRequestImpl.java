package ch.ethz.inf.vs.a2.http;

public class HttpRawRequestImpl implements HttpRawRequest {

	@Override
	public String generateRequest(String host, String path) {
		String firstLine = "GET "+path+" HTTP/1.1";
		String secondLine = "Host: "+host;
		return firstLine +"\n" + secondLine + "\n";
	}

	@Override
	public String generateRequest(String host, int port, String path) {
		String firstLine = "GET "+path+" HTTP/1.1";
		String secondLine = "Host: "+host;
		return firstLine +"\n" + secondLine + "\n";
	}

}
