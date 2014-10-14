package ch.ethz.inf.vs.a2.server;

public interface ParsedRequestConsumer<T> {

	/**
	 * Given a request Handle, generates an appropriate response and then calls requestHandle.postResponse on some background thread
	 * @param requestHandle
	 */
	public void consume(ClientHandle<T> requestHandle);
	
}
