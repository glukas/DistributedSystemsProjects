package ch.ethz.inf.vs.a2.http;


public abstract class HttpRawRequestFactory {
	public static HttpRawRequest getInstance() {
		return new HttpRawRequestImpl();
	}
}
