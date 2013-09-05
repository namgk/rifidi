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

	public CoAPListener getCls() {
		return cls;
	}

	public void setCls(CoAPListener cls) {
		this.cls = cls;
		// this.cls.registerHandler("epc_location", new
		// ObixCoAPLocationHandler());
	}

}
