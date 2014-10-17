package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

public class ServerAcceptThread<T> extends Thread {


	public final int port;
	private final RequestParser<ParsedRequest> parser;
	private final ParsedRequestConsumer<ParsedRequest> consumer;
	private volatile boolean alive = true;
	ServerSocket serverSocket = null;
	
	public void terminateGracefully() {
		alive = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * THREADING:
	 * PARSER MUST BE THREAD SAFE
	 * CONSUMER IS ALWAYS CALLED ON THE UI THREAD
	 * @param port
	 * @param consumer
	 * @param parser
	 */
	public ServerAcceptThread(int port, ParsedRequestConsumer<ParsedRequest> consumer, RequestParser<ParsedRequest> parser) {
		this.port = port;
		this.consumer = consumer;
		this.parser = parser;
	}
	
	public void run() {
		Log.d(this.getClass().toString(), "SERVER THREAD STARTED");
		
		try {
			serverSocket = new ServerSocket(port);
			
			//Accept clients and dispatch workers to deal with requests
			//Note that the port sequentializes the client requests anyway so it doesn't make sense
			//to have multiple threads waiting for clients
			while (alive) {
				Socket clientSocket = serverSocket.accept();
				Log.d(this.getClass().toString(), "ACCEPT CLIENT");
				ClientRequestTask<T> worker = new ClientRequestTask<T>(consumer, parser);
				worker.execute(clientSocket);
			}
			serverSocket.close();
			
		} catch (SocketException e) {
			Log.v(this.getClass().toString(),e.getLocalizedMessage());
		} catch (IOException e) {
			
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
		}
		Log.d(this.getClass().toString(), "SERVER THREAD EXITING");
	}
}
