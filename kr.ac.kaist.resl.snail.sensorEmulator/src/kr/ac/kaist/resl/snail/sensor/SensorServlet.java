package kr.ac.kaist.resl.snail.sensor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ------------------------------------------------------------ */
/**
 * Example WebSocket Chat Servlet.
 * <p>
 * This servlet demonstrates the Jetty server side WebSocket APIs by
 * implementing a (very) simple chat room. All connections received with the
 * "chat" sub protocol name are added to the members set. All other connections
 * are rejected. Any message received on any connection is assumed to a chat
 * message and is echoed verbatim to all connections in the _member set.
 */
public class SensorServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
				IOException {
		String path = req.getRequestURI();
		String payload = null;
		if (path.equals("/.well-known/core")) {
			payload = "</sensors/temp>;ct=0;rt=\"C\";if=\"sensor\"," +
					"</sensors/light>;ct=0;rt=\"Lux\";if=\"sensor\"," +
					"</sensors/humid>;ct=0;rt=\"Percent\";if=\"sensor\"," +
					"</id>;ct=0;rt=\"null\";if=\"device\"";
		} else if (path.equals("/sensors/temp")) {
			payload = "21.2";
		} else if (path.equals("/sensors/light")) {
			payload = "71.99";
		} else if (path.equals("/sensors/humid")) {
			payload = "50";
		} else if (path.equals("/sensors")) {
			payload = "[21.2,50,71.99]";
		} else if (path.equals("/id")) {
			int a = (int)(Math.random() * 6);
			//payload = "30744B5A1C0000100000000A";
			payload = "30744B5A1C0000100000000";
			payload += a;
		} else {
			payload = "not found";
		}
		PrintWriter out = resp.getWriter();

		out.println(payload);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
				IOException {
		String field = req.getParameter("field");
		PrintWriter out = resp.getWriter();

		out.println("<html>");
		out.println("<body>");
		out.println("You entered \"" + field + "\" into the text box.");
		out.println("</body>");
		out.println("</html>");
	}

}
