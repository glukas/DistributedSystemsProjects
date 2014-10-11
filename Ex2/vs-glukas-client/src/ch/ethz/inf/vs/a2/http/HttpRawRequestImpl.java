package ch.ethz.inf.vs.a2.http;

public class HttpRawRequestImpl implements HttpRawRequest {

	private static final String newLine = "\r"+"\n";
	private final String connectionHdr = "Connection: close"+newLine;
	private final String acceptHdr;
	
	private static final String acceptHdrFormat = "Accept: %s"+newLine;
	
	HttpRawRequestImpl() {
		acceptHdr = String.format(acceptHdrFormat, "text/html");
	}
	
	HttpRawRequestImpl(String type) {
		acceptHdr = String.format(acceptHdrFormat, type);
	}
	
	
	@Override
	public String generateRequest(String host, String path) {
		String getHdr = "GET "+path+" HTTP/1.1"+newLine;
		String hostHdr = "Host: "+host+newLine;
		return getHdr+hostHdr+connectionHdr+acceptHdr+newLine;
	}

	@Override
	public String generateRequest(String host, int port, String path) {
		return generateRequest(host+":"+port, path);
	}
}
