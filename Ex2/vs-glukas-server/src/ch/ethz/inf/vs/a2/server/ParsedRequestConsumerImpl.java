package ch.ethz.inf.vs.a2.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParsedRequestConsumerImpl implements ParsedRequestConsumer<ParsedRequest>{
	
	private ServerService service;
	private ExecutorService threadPool;
	
	public ParsedRequestConsumerImpl(ServerService service){
		this.service = service;
		this.threadPool = Executors.newCachedThreadPool();
	}
	
	@Override
	public void consume(ClientHandle<ParsedRequest> requestHandle) {
		Thread t = new ListenerThread(service, requestHandle, threadPool);
		threadPool.execute(t);
	}
}
