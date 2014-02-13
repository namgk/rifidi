/**
Mr.Nam
Jul 8, 2013
*/

package kr.ac.kaist.resl.snail.sensor.coapEngine;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;

public class SensorsResource extends LocalResource {

	public SensorsResource() {
		super("sensors");
		// TODO Auto-generated constructor stub
	}

	@Override
  public void performGET(GETRequest request) {
		request.respond(CodeRegistry.RESP_CONTENT, "[25,42,444]");
	}
}
