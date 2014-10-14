package ch.ethz.inf.vs.a2.server;

public interface RequestParser<T> {

	/**
	 * turns a string representation of a request into another (more useful) representation of a request
	 * @param request
	 * @return
	 */
	T parse(String request);
	
}
