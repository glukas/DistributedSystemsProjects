package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.Requester;

public class GenericSensor extends AbstractSensor {
	
	protected final ResponseParser parser;
	protected final Requester requester;
	
	/**
	 * In a generic sensor, a requester holds all necessary information to retrieve the data for a particular sensor
	 * @param requester
	 * @param parser
	 * @throws IllegalArgumentException if requester or parser is null
	 */
	protected GenericSensor(Requester requester, ResponseParser parser) {
		if (requester == null || parser == null) throw new IllegalArgumentException();
		this.parser = parser;
		this.requester = requester;
	}

	/**
	 * Must be called on the UI thread.
	 */
	@Override
	public void getTemperature() throws NullPointerException {
		//If we would want to prematurely cancel a request we would need to keep a reference to the worker
		AsyncWorker worker = new AsyncWorker();
		worker.execute(requester);
	}

	@Override
	public double parseResponse(String response) {
		return parser.parseResponse(response);
	}

}
