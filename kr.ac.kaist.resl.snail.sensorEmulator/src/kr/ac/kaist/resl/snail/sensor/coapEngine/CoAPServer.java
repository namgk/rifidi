/**
Mr.Nam
Jul 8, 2013
*/

package kr.ac.kaist.resl.snail.sensor.coapEngine;

import java.net.SocketException;

import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;

public class CoAPServer extends ServerEndpoint {

	public CoAPServer() throws SocketException {
		super();
		addResource(new SensorsResource());
		addResource(new IdResource());
	}
}
