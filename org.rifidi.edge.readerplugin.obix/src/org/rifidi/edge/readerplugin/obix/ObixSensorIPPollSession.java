package org.rifidi.edge.readerplugin.obix;

import java.io.IOException;
import java.util.Set;

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
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.poll.AbstractPollIPSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.TemperatureSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.SensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;

/**
 * The session class for the Obix sensor.
 * 
 * @author Mr.Nam - zang@kaist.ac.kr
 */
public class ObixSensorIPPollSession extends AbstractPollIPSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ObixSensorIPPollSession.class);

	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public ObixSensorIPPollSession(AbstractSensor<?> sensor, String id,
			String host, int port, int notifyPort, int ioStreamPort,
			int reconnectionInterval, int maxConAttempts, String username,
			String password, NotifierService notifierService, String readerID,
			Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, host, port, reconnectionInterval, maxConAttempts,
				commands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#_connect()
	 */
	@Override
	public void _connect() throws IOException {
		setStatus(SessionStatus.CONNECTING);

		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String baseUrl = "http://" + super.getHost() + ":" + super.getPort();
		HttpGet getReq = new HttpGet(baseUrl + "/obix");
		Obj lobby = null;

		response = httpclient.execute(getReq);
		String content = IOUtils.toString(response.getEntity().getContent());
		lobby = ObixDecoder.fromString(content);
		System.out.println("done, lobby: \n" + content);

		Obj[] resources = lobby.list();
		for (Obj o : resources) {
			String oHref = o.getHref().getPath();
			String reqUrl = baseUrl + "/" + oHref;
			HttpGet aGetReq = new HttpGet(reqUrl);
			HttpResponse aResp = httpclient.execute(aGetReq);
			String aContent = IOUtils.toString(aResp.getEntity().getContent()).replace("</obj>\n", "</obj>");
			Obj kid = ObixDecoder.fromString(aContent);
			System.out.println("kid: \n" + aContent);
			if ((kid.getIs() != null) && (kid.getIs().toString().equals(TemperatureSensor.CONTRACT))){
				System.out.println("Got a temperature sensor");
				TemperatureSensorImpl tempSensor = new TemperatureSensorImpl();
				System.out.println("Temp val: " + tempSensor.value());
			}
		}

		System.out.println("done connect");
		setStatus(SessionStatus.PROCESSING);
	}

	@Override
	protected boolean onConnect() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}
