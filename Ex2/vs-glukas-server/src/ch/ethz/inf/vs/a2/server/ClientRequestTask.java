package ch.ethz.inf.vs.a2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class ClientRequestTask<T> extends AsyncTask<Socket, Void, ClientHandle<ParsedRequest>> {
	
	private RequestParser<ParsedRequest> parser;
	private final ParsedRequestConsumer<ParsedRequest> consumer;
	
	/**
	 * @param consumer is always called on the UI thread
	 * @param parser is called from a background thread
	 */
	public ClientRequestTask(ParsedRequestConsumer<ParsedRequest> consumer, RequestParser<ParsedRequest> parser) {
		this.consumer = consumer;
		this.parser = parser;
	}
	
	/**
	 * Gets a request from socket[0] and invokes the parser on that string.
	 * Called from the background
	 * Never call directly - use execute
	 */
	@Override
	protected ClientHandle<ParsedRequest> doInBackground(Socket ... socket) {
		try {
			// wait for request
			Socket clientSocket = socket[0];
			
			//get the text from the client
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String request = "";
			String requestLine = null;
			while( (requestLine = in.readLine()) != null) {
				request += requestLine;
				//TODO understand why this loop doesn't finish. Note that we don't need more than one line 
				//for this particular project.
				if (requestLine.equals("")) break;//HTTP ends the request header with an empty line, here we only need the header
			}
			// call the request parser on the request string
			ParsedRequest requestParsed = parser.parse(request);
			
			// create & return the Request Handle
			return new ClientHandle<ParsedRequest>(requestParsed, clientSocket);
			
		} catch (IOException e) {
			
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
			return null;
		}
		
	}
	
	/**
	 * The consumer is given a request handle to respond to the request
	 * This is always called on the UI thread
	 */
	@Override
	protected void onPostExecute(ClientHandle<ParsedRequest> requestHandle) {
		consumer.consume(requestHandle);
	}
	
}
