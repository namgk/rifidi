/**
Mr.Nam
Jul 8, 2013
*/

package kr.ac.kaist.resl.snail.sensor.coapEngine;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;

public class IdResource extends LocalResource {

	public IdResource() {
		super("epc");
		// TODO Auto-generated constructor stub
	}

	@Override
  public void performGET(GETRequest request) {
		request.respond(CodeRegistry.RESP_CONTENT, "30744B5A1C0000100000000A");
	}
}
