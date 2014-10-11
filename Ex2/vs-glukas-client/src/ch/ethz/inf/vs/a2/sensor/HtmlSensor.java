package ch.ethz.inf.vs.a2.sensor;

import ch.ethz.inf.vs.a2.http.Requester;
import ch.ethz.inf.vs.a2.http.RequesterHttpImpl;

public class HtmlSensor extends GenericSensor{

	protected HtmlSensor() {
		super(new RequesterHttpImpl("http://vslab.inf.ethz.ch:8081/sunspots/") , null);

	}

}
