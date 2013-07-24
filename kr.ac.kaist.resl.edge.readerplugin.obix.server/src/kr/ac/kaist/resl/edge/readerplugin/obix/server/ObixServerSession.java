package kr.ac.kaist.resl.edge.readerplugin.obix.server;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kr.ac.kaist.resl.edge.readerplugin.obix.data.LocationValue;
import kr.ac.kaist.resl.edge.readerplugin.obix.data.PrimitiveValue;
import kr.ac.kaist.resl.edge.readerplugin.obix.server.command.ObixServerCmd_Update;
import kr.ac.kaist.resl.stis.listener.STISHandler;
import kr.ac.kaist.resl.stis.listener.coaplistener.CoAPListener;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ObixServerSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ObixServerSession.class);
	/** Service used to send out notifications */
	private volatile NotifierService notifierService;
	Gson gson = new Gson();
	private CoAPListener cls;
	private final String obixReaderID;

	public ObixServerSession(AbstractSensor<?> sensor, String ID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			NotifierService notifierService, String obixReaderID, CoAPListener cls) {
		super(sensor, ID, commandConfigurations);
		this.cls = cls;
		this.obixReaderID = obixReaderID;
		this.notifierService = notifierService;
		this.setStatus(SessionStatus.CLOSED);
	}

	@Override
	protected void _connect() throws IOException {
		setStatus(SessionStatus.CONNECTING);

		if (!processing.compareAndSet(false, true)) {
			logger.warn("Executor was already active! ");
		}
		executor = new ScheduledThreadPoolExecutor(1);

		cls.registerHandler("^/[A-Fa-f0-9]{1,30}/location/append$", new ObixCoAPLocationHandler());
		cls.registerHandler("^/[A-Fa-f0-9]{1,30}/sensor/append$", new ObixCoAPDataHandler());
		cls.registerHandler("^/[A-Fa-f0-9]{1,30}/light/append$", new ObixCoAPDataHandler());
		cls.registerHandler("^/[A-Fa-f0-9]{1,30}/temp/append$", new ObixCoAPDataHandler());

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
		cls.deregisterHandler("^/[A-Fa-f0-9]{1,30}/location/append$");
		cls.deregisterHandler("^/[A-Fa-f0-9]{1,30}/sensor/append$");
		cls.deregisterHandler("^/[A-Fa-f0-9]{1,30}/light/append$");
		cls.deregisterHandler("^/[A-Fa-f0-9]{1,30}/temp/append$");
		// notify anyone who cares that session is now closed
		setStatus(SessionStatus.CLOSED);

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

				ObixServerCmd_Update ocu = new ObixServerCmd_Update("obixUpdate");
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

			String resourcePath = r.getUriPath();
			// resolve between coap 8 and 12
			if (!resourcePath.startsWith("/"))
				resourcePath = "/" + resourcePath;

			System.out.println("Obix plugin serving " + resourcePath + " for " + r.getPeerAddress().getAddress());

			String[] urlElements = resourcePath.split("/");

			System.out.println("******************** " + urlElements.length);

			String epc = urlElements[1];
			System.out.println("******************** " + epc);

			String sensorId = urlElements[2];
			System.out.println("******************** " + sensorId);

			String senseData = r.getPayloadString();
			System.out.println(senseData);
			try {
				PrimitiveValue d = gson.fromJson(senseData, PrimitiveValue.class);
				if (d == null)
					throw new JsonSyntaxException("");
				System.out.println(gson.toJson(d));
				System.out.println("******************** VAL " + d.getVal());

				IoTSensedEvent tagInfo =
						new IoTSensedEvent(null, getSensor().getID(), 0, System.currentTimeMillis());
				tagInfo.setTag(STISUtils.getTag(new String(epc)));
				tagInfo.addExtraInformation(sensorId, d.getVal());

				ObixServerCmd_Update ocu = new ObixServerCmd_Update("obixUpdate");
				ocu.setTagInfo(tagInfo);
				submit(ocu);

				getSensor().sendEvent(tagInfo);
				r.respond(CodeRegistry.RESP_CONTENT, gson.toJson(d));

			} catch (DecoderException e) {
				r.respond(CodeRegistry.RESP_CONTENT, "{\"error\":\"Bad EPC\"}");
			} catch (JsonSyntaxException j) {
				r.respond(CodeRegistry.RESP_CONTENT, "{\"error\":\"Bad JSON Syntax\"}");
			}
		}
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
}
