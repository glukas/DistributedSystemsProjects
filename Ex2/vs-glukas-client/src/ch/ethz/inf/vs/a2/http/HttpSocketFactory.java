package ch.ethz.inf.vs.a2.http;


public abstract class HttpSocketFactory {
	public static HttpSocket getInstance() {
		return new HttpSocketImpl();
	}
}
