package kr.ac.kaist.resl.snail.sensor;

import kr.ac.kaist.resl.snail.sensor.coapEngine.CoAPServer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.Resource;


/**
 * WebSocket example - Server Sensor class.
 * <p>
 * The {@link ChatServlet} WebSocket example would typically be deployed in a
 * WAR file struction (in WEB-INF/classes or WEB-INF/lib). However, this is a
 * simple embedded Jetty server that will run the servlet at "/chat/*" together
 * with the static content served from the classpath resource
 * com/example/docroot/
 */
public class Sensor {
	public static void main(String... arg) throws Exception {
		// Get the server port and create a server
		int port = arg.length > 1 ? Integer.parseInt(arg[1]) : 5683;
		Server server = new Server(port);
		CoAPServer coapServer = new CoAPServer();

		// Create the servlet handler and define the Chat servlet
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(SensorServlet.class, "/sensors/*");
		// Create a resource handler for static content (eg index.html, chat.js,
		// chat.css)
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setBaseResource(Resource.newClassPathResource("com/nhong/docroot/"));

		// Create the default handler for all other requests
		DefaultHandler defaultHandler = new DefaultHandler();

		// Set the handlers on the server as a handler list
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletHandler, resourceHandler, defaultHandler });
		server.setHandler(handlers);

		// Start the server and join it to avoid exit.
		coapServer.start();
		server.start();
		server.join();

	}
}
