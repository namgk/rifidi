package kr.ac.kaist.resl.epcisquery;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class EpcisCoAPPort {

	public String query(String epcisQueryInterface, String epc) {
		URI uri = null;
		try {
			String coapEndpointIP = (new URI(epcisQueryInterface)).getHost();
			// retrieve all location for the specified epc
			uri = new URI("coap://" + coapEndpointIP + ":5683/location?epc="
					+ epc);
		} catch (URISyntaxException e1) {
			return "[QUERY ERROR] Epcis query interface error, please check: " + epcisQueryInterface;
		}
		// create new request
		Request request = new GETRequest();
		// specify URI of target endpoint
		request.setURI(uri);
		// enable response queue for blocking I/O
		request.enableResponseQueue(true);

		// execute the request
		try {
			request.execute();
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			return null;
		}

		// receive response
		try {
			Response response = request.receiveResponse();

			if (response != null) {
				return response.getPayloadString();
			} else {
				System.out.println("No response received.");
			}

		} catch (InterruptedException e) {
			System.err.println("Receiving of response interrupted: "
					+ e.getMessage());
			return null;
		}
		return null;
	}
}
