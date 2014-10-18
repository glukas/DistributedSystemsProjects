package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class ServerAcceptThread<T> extends Thread {


	public final int port;
	private final RequestParser<T> parser;
	private final ParsedRequestConsumer<T> consumer;
	private volatile boolean alive = true;
	ServerSocket serverSocket = null;
	ExecutorService threadPool = null;
	
	public void terminateGracefully() {
		alive = false;
		try {
			if (serverSocket!= null) serverSocket.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * THREADING:
	 * PARSER & CONSUMER MUST BE THREAD SAFE.
	 * They will be invoked on some other background thread, possibly by multiple threads at the same time.
	 * @param port
	 * @param consumer
	 * @param parser
	 */
	public ServerAcceptThread(int port, ParsedRequestConsumer<T> consumer, RequestParser<T> parser) {
		this.port = port;
		this.consumer = consumer;
		this.parser = parser;
		this.threadPool = Executors.newCachedThreadPool();
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
				
				ClientRequestRunnable<T> task = new ClientRequestRunnable<T>(consumer, parser, clientSocket);
				threadPool.execute(task);
			}
			serverSocket.close();
			
		} catch (SocketException e) {
			Log.v(this.getClass().toString(),e.getLocalizedMessage());
		} catch (IOException e) {
			
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
		}
		
		if (threadPool != null) {
			threadPool.shutdown();
		}
		
		Log.d(this.getClass().toString(), "SERVER THREAD EXITING");
	}
}
