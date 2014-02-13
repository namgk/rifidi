package kr.ac.kaist.resl.edge.readerplugin.snail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

/**
 * The session class for the Snail sensor.
 * 
 * @author Nam Giang - zang at kaist dot ac dot kr
 */
public class SnailSensorSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(SnailSensorSession.class);
	/** Service used to send out notifications */
	private volatile NotifierService notifierService;
	/** The ID of the reader this session belongs to */
	private final String snailReaderID;
	private HttpClient httpclient = new DefaultHttpClient();
	private String host;
	private int port;
	private int coapPort;
	private SnailSensorResource resources;
	private boolean http = false;

	public boolean isHttp() {
		return http;
	}

	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public SnailSensorSession(AbstractSensor<?> sensor, String id, String host, int port,
			int coapPort, int notifyPort, int ioStreamPort, int reconnectionInterval,
			int maxConAttempts, String username, String password, 
			NotifierService notifierService, String snailReaderID,
			Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, commands);
		this.port = port;
		this.coapPort = coapPort;
		this.host = host;
		this.snailReaderID = snailReaderID;
		this.notifierService = notifierService;
		this.setStatus(SessionStatus.CLOSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#_connect()
	 */
	@Override
	protected synchronized void _connect() {
		setStatus(SessionStatus.CONNECTING);

		if (!processing.compareAndSet(false, true)) {
			logger.warn("Executor was already active! ");
		}
		executor = new ScheduledThreadPoolExecutor(1);

//		try {// / Checking http capability
//			HttpResponse testHttp =
//					httpclient.execute(new HttpGet("http://" + getHost() + ":" + getPort()));
//			EntityUtils.consume(testHttp.getEntity());
//		} catch (ClientProtocolException e1) {
//			setHttp(false);
//		} catch (IOException e1) {
//			setHttp(false);
//		}

		try {
			String epc = isHttp() ? getResourceByHttp("id") : getResourceByCoAP("id");
			resources = new SnailSensorResource(epc);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			System.out.println("Cannot connect!");
			disconnect();
			return;
		}

		System.out.println("done connect, lobby: \n" + resources.toString());
		setStatus(SessionStatus.PROCESSING);
	}

	public String getResourceByHttp(String resource) throws ClientProtocolException,
			IOException {
		String baseUrl = "http://" + getHost() + ":" + getPort();
		String reqUrl = baseUrl + "/" + resource;

		HttpResponse aResp = httpclient.execute(new HttpGet(reqUrl));
		String aContent = IOUtils.toString(aResp.getEntity().getContent());

		return aContent;
	}

	public String getResourceByCoAP(String resource) {
		String baseUrl = "coap://" + getHost() + ":" + getCoAPPort();
		String reqUrl = baseUrl + "/" + resource;

		Request getRequest = new GETRequest();
		Response response = null;
		try {
			getRequest.setURI(new URI(reqUrl));
			getRequest.enableResponseQueue(true);
//			getRequest.setType(messageType.NON);
			getRequest.execute();
			response = getRequest.receiveResponse();//udp blocking
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ((response.getCode() == 69) || response == null) ? response.getPayloadString() : null;
	}

	@Override
	public void disconnect() {
		resetCommands();
		if (processing.get()) {
			if (processing.compareAndSet(true, false)) {
				logger.debug("Shuting down session");
			}
		}
		// make sure executor is shutdown!
		if (executor != null) {
			executor.shutdownNow();
			executor = null;
		}
		// notify anyone who cares that session is now closed
		setStatus(SessionStatus.CLOSED);
	}

	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		NotifierService service = notifierService;
		if (service != null) {
			service.sessionStatusChanged(this.snailReaderID, this.getID(), status);
		}
	}

	@Override
	public void killComand(Integer id) {
		super.killComand(id);
		NotifierService service = notifierService;
		if (service != null) {
			service.jobDeleted(this.snailReaderID, this.getID(), id);
		}
	}

	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		Integer retVal = super.submit(commandID, interval, unit);
		try {
			NotifierService service = notifierService;
			if (service != null) {
				service.jobSubmitted(this.snailReaderID, this.getID(), retVal, commandID, (interval > 0));
			}
		} catch (Exception e) {
			// make sure the notification doesn't cause this method to exit
			// under any circumstances
			logger.error(e);
		}
		return retVal;
	}

	public void setHttp(boolean http) {
		this.http = http;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getCoAPPort() {
		return coapPort;
	}

	public SnailSensorResource getResources() {
		return resources;
	}

	public HttpClient getHttpclient() {
		return httpclient;
	}

}
