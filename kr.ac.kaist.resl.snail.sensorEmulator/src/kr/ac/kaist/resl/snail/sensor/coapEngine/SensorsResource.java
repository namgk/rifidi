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
		String randomTemp = ((25 + Math.random() * ((27 - 25) + 1)) + "").substring(0,4);
		String randomHumid = ((18 + Math.random() * ((20 - 18) + 1)) + "").substring(0,4);
		String randomLight = ((8 + Math.random() * ((10 - 8) + 1)) + "").substring(0,4);

		request.respond(CodeRegistry.RESP_CONTENT, "[" + randomTemp + "," + randomHumid +"," + randomLight + "]");
	}
}
