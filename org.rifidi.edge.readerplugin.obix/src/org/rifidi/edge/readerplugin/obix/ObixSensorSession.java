package org.rifidi.edge.readerplugin.obix;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import obix.Obj;
import obix.io.ObixDecoder;

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
 * The session class for the Obix sensor.
 * 
 * @author Nam Giang - zang at kaist dot ac dot kr
 */
public class ObixSensorSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger =
			LogFactory.getLog(ObixSensorSession.class);
	/** Service used to send out notifications */
	private volatile NotifierService notifierService;
	/** The ID of the reader this session belongs to */
	private final String obixReaderID;
	private HttpClient httpclient = new DefaultHttpClient();
	private String host;
	private int port;
	private int coapPort;
	private Obj lobby;
	private boolean http = false;

	public boolean isHttp() {
		return http;
	}

	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public ObixSensorSession(AbstractSensor<?> sensor, String id, String host,
			int port, int coapPort, int notifyPort, int ioStreamPort,
			int reconnectionInterval, int maxConAttempts, String username,
			String password, NotifierService notifierService,
			String obixReaderID, Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, commands);
		this.port = port;
		this.coapPort = coapPort;
		this.host = host;
		this.obixReaderID = obixReaderID;
		this.notifierService = notifierService;
		this.setStatus(SessionStatus.CLOSED);
		try {
			at.ac.tuwien.auto.iotsys.gateway.obix.objects.ContractInit.init();
			logger.info("---------------------------------- INITIATING OBIX CONTRACTS");
		} catch (IllegalStateException e) {
		}
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

		// / Checking http capability
		try {
			HttpResponse testHttp =
					httpclient.execute(new HttpGet("http://" + getHost() + ":"
							+ getPort()));
			EntityUtils.consume(testHttp.getEntity());
		} catch (ClientProtocolException e1) {
			setHttp(false);
			e1.printStackTrace();
		} catch (IOException e1) {
			setHttp(false);
			e1.printStackTrace();
		}

		if (isHttp()) {
			String baseUrl = "http://" + getHost() + ":" + getPort();
			HttpGet getReq = new HttpGet(baseUrl + "/obix");

			HttpResponse response;
			try {
				response = httpclient.execute(getReq);// blocking

				String content =
						IOUtils.toString(response.getEntity().getContent());
				lobby = ObixDecoder.fromString(content);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}
		} else {
			final String baseUrl = "coap://" + getHost() + ":5683";

			try {
				Request getRequest = new GETRequest();
				getRequest.setURI(new URI(baseUrl + "/obix"));
				getRequest.enableResponseQueue(true);
				getRequest.execute();

				Response response = getRequest.receiveResponse();
				lobby = ObixDecoder.fromString(response.getPayloadString());
				
			} catch (InterruptedException e) {
				System.err.println("Failed to receive response: "
						+ e.getMessage());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("done connect, lobby: \n" + lobby.toString());
		setStatus(SessionStatus.PROCESSING);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService;
		if (service != null) {
			service.sessionStatusChanged(this.obixReaderID, this.getID(), status);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#killComand(java
	 * .lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		super.killComand(id);
		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService;
		if (service != null) {
			service.jobDeleted(this.obixReaderID, this.getID(), id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#submit(java.lang
	 * .String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		Integer retVal = super.submit(commandID, interval, unit);
		// TODO: Remove this once we have aspectJ
		try {
			NotifierService service = notifierService;
			if (service != null) {
				service.jobSubmitted(this.obixReaderID, this.getID(), retVal, commandID, (interval > 0));
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

	public Obj getLobby() {
		return lobby;
	}

	public HttpClient getHttpclient() {
		return httpclient;
	}

	

}
