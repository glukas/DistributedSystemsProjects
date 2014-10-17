package ch.ethz.inf.vs.a2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

public class ClientRequestRunnable<T> implements Runnable {

	private final RequestParser<T> parser;
	private final ParsedRequestConsumer<T> consumer;
	private final Socket clientSocket;
	
	/**
	 * @param consumer is always called on the UI thread
	 * @param parser is called from a background thread
	 */
	public ClientRequestRunnable(ParsedRequestConsumer<T> consumer, RequestParser<T> parser, Socket socket) {
		this.consumer = consumer;
		this.parser = parser;
		this.clientSocket = socket;
	}
	
	@Override
	public void run() {
		//get the text from the client
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String request = "";
			String requestLine = null;
			while( (requestLine = in.readLine()) != null) {
				request += requestLine;
				if (requestLine.equals("")) {
					break;//HTTP ends the request header with an empty line, here we only need the header
				}
			}
			// call the request parser on the request string
			T requestParsed = parser.parse(request);
			
			// create the Request Handle
			ClientHandle<T> handle = new ClientHandle<T>(requestParsed, clientSocket);
			
			//process the result
			consumer.consume(handle);
			
		} catch (IOException e) {
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
		}

	}

}
