package kr.ac.kaist.resl.ds;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import kr.ac.kaist.resl.epcisquery.EpcisCoAPPort;

import com.google.gson.Gson;

import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class EPCDSTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// The EPC used in the query
		String epc = "urn:epc:id:sgtin:0057000.123780.1118";// urn:epc:id:sgtin:369783.6810725.52500494167
		String dsServer = "2002:8ff8:38de::8ff8:38de";
		
		// Phase 1 : Query the EPCDS
		String dsResponse = Send_Request(dsServer, epc, "20130601000000",
				"20130607240000"); // Time format: YYYYMMDDHHMMSS
		// Parse the response
		Gson gson = new Gson();
		Response_CoAP rc = gson.fromJson(dsResponse, Response_CoAP.class);
		// Loop through the response and make query to EPCIS
		for (String epcisQueryInterface : rc.EPCIS_List) {
			System.out.println("EPCIS query interface :" + epcisQueryInterface);
			// Phase 2 : Query the EPCIS
			// Query using CoAP/JSON
			// create the epc coap port
			EpcisCoAPPort ecp = new EpcisCoAPPort();
			
			String response = ecp.query(epcisQueryInterface, epc);
			System.out.println(response);
		}
	}

	static String Send_Request(String address, String EPC, String Start_Time,
			String End_Time) {
		URI uri = null; // URI parameter of the request
		try {
			uri = new URI("coap://[" + address + "]/EPC_Request");
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			return "";
		}

		// create new request
		Request postRequest = new POSTRequest();
		Gson gson = new Gson();
		Request_CoAP req = new Request_CoAP();
		req.EPC = EPC;
		req.Start_time = Start_Time;
		req.End_time = End_Time;
		postRequest.setPayload(gson.toJson(req));
		postRequest.setURI(uri);
		postRequest.enableResponseQueue(true);

		// execute the request
		try {
			postRequest.execute();
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			return "";
		}

		// receive response
		try {
			Response response = postRequest.receiveResponse();

			if (response != null) {
				// response received, output a pretty-print
				response.prettyPrint();
				return response.getPayloadString();
				// System.out.println(response.getPayloadString());

			} else {
				System.out.println("No response received.");
			}

		} catch (InterruptedException e) {
			System.err.println("Receiving of response interrupted: "
					+ e.getMessage());
			return "";
		}
		return null;
	}
}
