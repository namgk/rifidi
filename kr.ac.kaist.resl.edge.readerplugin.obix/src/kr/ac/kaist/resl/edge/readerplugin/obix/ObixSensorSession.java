package kr.ac.kaist.resl.edge.readerplugin.obix;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kr.ac.kaist.resl.edge.readerplugin.obix.commands.ObixCommand_Update;
import kr.ac.kaist.resl.edge.readerplugin.obix.data.LocationValue;
import kr.ac.kaist.resl.stis.listener.STISHandler;
import kr.ac.kaist.resl.stis.listener.coaplistener.CoAPListener;
import obix.Obj;
import obix.io.ObixDecoder;

import org.apache.commons.codec.DecoderException;
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
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The session class for the Obix sensor.
 * 
 * @author Nam Giang - zang at kaist dot ac dot kr
 */
public class ObixSensorSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ObixSensorSession.class);
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
	Gson gson = new Gson();
	private CoAPListener cls;

	public boolean isHttp() {
		return http;
	}

	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public ObixSensorSession(AbstractSensor<?> sensor, String id, String host, int port,
			int coapPort, int notifyPort, int ioStreamPort, int reconnectionInterval,
			int maxConAttempts, String username, String password, CoAPListener cls,
			NotifierService notifierService, String obixReaderID,
			Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, commands);
		this.port = port;
		this.coapPort = coapPort;
		this.host = host;
		this.obixReaderID = obixReaderID;
		this.notifierService = notifierService;
		this.cls = cls;
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

		try {// / Checking http capability
			HttpResponse testHttp =
					httpclient.execute(new HttpGet("http://" + getHost() + ":" + getPort()));
			EntityUtils.consume(testHttp.getEntity());
		} catch (ClientProtocolException e1) {
			setHttp(false);
			e1.printStackTrace();
		} catch (IOException e1) {
			setHttp(false);
			e1.printStackTrace();
		}

		try {
			lobby =
					ObixDecoder.fromString(isHttp() ? getResourceByHttp("obix") : getResourceByCoAP("obix"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("done connect, lobby: \n" + lobby.toString());
		setStatus(SessionStatus.PROCESSING);
		
		cls.registerHandler("epc_location", new ObixCoAPLocationHandler());
		cls.registerHandler("epc_data", new ObixCoAPDataHandler());
	}

	public String getResourceByHttp(String resource) throws ClientProtocolException,
			IOException {
		String baseUrl = "http://" + getHost() + ":" + getPort();
		String reqUrl = baseUrl + "/" + resource;

		HttpResponse aResp = httpclient.execute(new HttpGet(reqUrl));
		String aContent =
				IOUtils.toString(aResp.getEntity().getContent()).replace("</obj>\n", "</obj>");

		return aContent;
	}

	public String getResourceByCoAP(String resource) throws URISyntaxException,
			IOException, InterruptedException {
		String baseUrl = "coap://" + getHost() + ":" + getCoAPPort();
		String reqUrl = baseUrl + "/" + resource;

		Request getRequest = new GETRequest();
		getRequest.setURI(new URI(reqUrl));
		getRequest.enableResponseQueue(true);
		getRequest.execute();

		Response response = getRequest.receiveResponse();

		return response.getPayloadString();
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

	private class ObixCoAPLocationHandler implements STISHandler {

		@Override
		public void execute(Object request) {
			Request r = (Request) request;

			String resourcePath = r.getUriPath();
			// resolve between coap 8 and 12
			if (!resourcePath.startsWith("/"))
				resourcePath = "/" + resourcePath;
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
			try {
				LocationValue l = gson.fromJson(locData, LocationValue.class);
				if (l == null)
					throw new JsonSyntaxException("");
				System.out.println(gson.toJson(l));
				System.out.println("******************** LAT " + l.getNodes().get(0).getVal());
				System.out.println("******************** LONG " + l.getNodes().get(1).getVal());

				IoTSensedEvent tagInfo =
						new IoTSensedEvent(null, getSensor().getID(), 0, System.currentTimeMillis());
				tagInfo.setTag(STISUtils.getTag(new String(epc)));
				tagInfo.addExtraInformation("location.lat", l.getNodes().get(0).getVal());
				tagInfo.addExtraInformation("location.long", l.getNodes().get(1).getVal());
				
				ObixCommand_Update ocu = new ObixCommand_Update("obixUpdate");
				ocu.setTagInfo(tagInfo);
				submit(ocu);
				
				getSensor().sendEvent(tagInfo);
				r.respond(CodeRegistry.RESP_CONTENT, gson.toJson(l));
			} catch (DecoderException e) {
				r.respond(CodeRegistry.RESP_CONTENT, "{\"error\":\"Bad EPC\"}");
			} catch (JsonSyntaxException j) {
				r.respond(CodeRegistry.RESP_CONTENT, "{\"error\":\"Bad JSON Syntax\"}");
			}
		}

	}

	private class ObixCoAPDataHandler implements STISHandler {

		@Override
		public void execute(Object request) {
			Request r = (Request) request;
			IoTSensedEvent tagInfo =
					new IoTSensedEvent(null, getSensor().getID(), 0, System.currentTimeMillis());

			String epc = r.getLocationPath().split("/")[1];
			System.out.println("******************** " + epc);

			String sensorId = r.getLocationPath().split("/")[2];
			System.out.println("******************** " + sensorId);

			String sensorValue = r.getPayloadString();
			try {
				tagInfo.setTag(STISUtils.getTag(new String(epc)));
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tagInfo.addExtraInformation(sensorId, sensorValue);

			getSensor().sendEvent(tagInfo);
		}
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
