package ch.ethz.inf.vs.a2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class ClientRequestTask<T> extends AsyncTask<Socket, Void, ClientHandle<T>> {
	
	private RequestParser<T> parser;
	private final ParsedRequestConsumer<T> consumer;
	
	/**
	 * @param consumer is always called on the UI thread
	 * @param parser is called from a background thread
	 */
	public ClientRequestTask(ParsedRequestConsumer<T> consumer, RequestParser<T> parser) {
		this.consumer = consumer;
		this.parser = parser;
	}
	
	/**
	 * Gets a request from socket[0] and invokes the parser on that string.
	 * Called from the background
	 * Never call directly - use execute
	 */
	@Override
	protected ClientHandle<T> doInBackground(Socket ... socket) {
		try {
			// wait for request
			Socket clientSocket = socket[0];
			
			//get the text from the client
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			StringBuilder request = new StringBuilder();
			String requestLine;
			while( (requestLine = in.readLine()) != null) {
				request.append(requestLine);
			}
			// call the request parser on the request string
			T requestParsed = parser.parse(request.toString());
			
			// create & return the Request Handle
			return new ClientHandle<T>(requestParsed, clientSocket);
			
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
	protected void onPostExecute(ClientHandle<T> requestHandle) {
		consumer.consume(requestHandle);
	}
	
}
