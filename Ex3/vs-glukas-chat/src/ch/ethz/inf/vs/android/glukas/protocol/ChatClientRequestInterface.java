package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * Interface for posting asynchronous requests to the server.
 * Used in tandem with ChatEventListener interface, which can be used to receive responses by the server.
 * @author glukas
 */
public interface ChatClientRequestInterface {
	
	public void register(String username);
	
	public void deregister();
	
	public void sendMessage(String message, int messageId);
	
	public void getClients();
	
}
