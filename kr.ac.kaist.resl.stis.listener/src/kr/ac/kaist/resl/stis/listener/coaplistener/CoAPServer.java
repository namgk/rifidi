/**
 * 
 */
package kr.ac.kaist.resl.stis.listener.coaplistener;

import java.net.SocketException;

import kr.ac.kaist.resl.stis.listener.STISHandler;

import com.google.gson.Gson;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class CoAPServer extends ServerEndpoint {

	Gson gson = new Gson();
	CoAPListener cls;

	public CoAPServer() throws SocketException {
	}

	@Override
	public void execute(Request request) {
		String resourcePath = request.getUriPath();
		
		if (!resourcePath.startsWith("/"))
			resourcePath = "/" + resourcePath;
		
		System.out.println("Coap server serving " + resourcePath + " for " + request.getPeerAddress().getAddress());
		
		STISHandler sh = cls.findHandler(resourcePath);
		if (sh != null)
			sh.execute(request);
		else
			request.respond(CodeRegistry.RESP_NOT_FOUND);
				
		request.sendResponse();
	}

	private class ObixCoAPLocationHandler implements STISHandler {

		@Override
		public void execute(Object request) {
			Request r = (Request) request;

			String resourcePath = r.getUriPath();
			System.out.println("Coap serving " + resourcePath + " for " + r.getPeerAddress().getAddress());
			String[] urlElements = resourcePath.split("/");
			for (String s : urlElements) {
				System.out.println(s);
			}

			System.out.println("******************** " + urlElements.length);

			String epc = urlElements[1];
			System.out.println("******************** " + epc);

			String locData = r.getPayloadString();
			System.out.println(locData);
			r.respond(CodeRegistry.RESP_CONTENT, "{\"error\":\"Bad Data\"}");
			r.sendResponse();

		}

	}

	public CoAPListener getCls() {
		return cls;
	}

	public void setCls(CoAPListener cls) {
		this.cls = cls;
		// this.cls.registerHandler("epc_location", new
		// ObixCoAPLocationHandler());
	}

}
