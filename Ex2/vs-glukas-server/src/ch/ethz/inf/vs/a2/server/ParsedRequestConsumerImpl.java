package ch.ethz.inf.vs.a2.server;

public class ParsedRequestConsumerImpl implements ParsedRequestConsumer<ParsedRequest>{
	
	private ServerService service;
	
	public ParsedRequestConsumerImpl(ServerService service){
		this.service = service;
	}
	
	@Override
	public synchronized void consume(ClientHandle<ParsedRequest> requestHandle) {
		Thread t = new ListenerThread(service, requestHandle);
		t.start();
	}
}
