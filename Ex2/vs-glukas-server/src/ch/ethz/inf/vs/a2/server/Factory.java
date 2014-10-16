package ch.ethz.inf.vs.a2.server;

public class Factory {
	public static RequestParser<ParsedRequest> getParser(ServerService service){
		return new RequestParserImpl(service);
	}
	
	public static ParsedRequestConsumer<ParsedRequest> getConsumer(ServerService service){
		return new ParsedRequestConsumerImpl(service);
	}
}
