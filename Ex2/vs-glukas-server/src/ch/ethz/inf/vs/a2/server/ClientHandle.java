package ch.ethz.inf.vs.a2.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientHandle<T> {

	public enum Status {//Constants for the http status (200 OK, ...) 
		OK("200 OK"),
		NOT_FOUND("404 NOT FOUND");//TODO
		
		Status(String name) {
			this.name = name;
		}
		
		String name;
	}

	public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	
	public final T request;//You can use this to formulate a response

	private final Socket socket;//don't use this directly. Use postResponse.
	
	public ClientHandle(T requestParsed, Socket socket) {
		this.socket = socket;
		request = requestParsed;
	}
	
	/**
	 * Posts the response to the client  (the sender of the request)
	 * Executed on the callers thread.
	 * DO NOT CALL THIS METHOD ON THE UI THREAD.
	 * The response should be the body of a valid html message;
	 * @param responseBody the body of the html message to be sent. The content type is text/html
	 */
	public void postResponse(String responseBody, Status status) throws IOException {
		if (!socket.isClosed()) {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writeHeader(out, status);
			out.write(responseBody);
			out.newLine();
			writeFooter(out);
			out.close();//flush & close
			socket.close();
		}
	}

	private void writeHeader(BufferedWriter out, Status status) throws IOException {
		out.write("HTTP/1.0 "+status.name); 
		out.newLine();
		SimpleDateFormat format = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
		out.write("Date: " + format.format(new Date()));
		out.newLine();
		out.write("Content-Type: text/html");
		out.newLine();
		out.newLine();
		out.write("<html>");
		out.newLine();
		out.write("<body>");
		out.newLine();
	}
	
	private void writeFooter(BufferedWriter out) throws IOException {
		out.write("</body>");
		out.newLine();
		out.write("</html>");
		out.newLine();
	}	
}
