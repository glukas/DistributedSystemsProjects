package ch.ethz.inf.vs.a2.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.util.Log;

public class ClientHandle<T> {

	public enum Status {//Constants for the http status (200 OK, ...) 
		OK;//TODO
	}
	
	public final ParsedRequest request;//You can use this to formulate a response

	private final Socket socket;//don't use this directly. Use postResponse.
	
	public ClientHandle(ParsedRequest parsedRequest, Socket socket) {
		this.socket = socket;
		request = parsedRequest;
	}
	
	/**
	 * Posts the response to the client  (the sender of the request)
	 * Executed on the callers thread.
	 * DO NOT CALL THIS METHOD ON THE UI THREAD.
	 * The body need to be a valid http body
	 * @param responseBody the body of the http message to be sent. The content type is text/html
	 */
	public void postResponse(String responseBody, Status status) throws IOException {
		if (!socket.isClosed()) {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writeHeader(out, status);
			out.write(responseBody);
			writeFooter(out);
			out.flush();
			out.close();
			socket.close();
		}
	}

	private void writeHeader(BufferedWriter out, Status status) throws IOException {
		//TODO
	}
	
	private void writeFooter(BufferedWriter out) throws IOException {
		//TODO
	}	
}
