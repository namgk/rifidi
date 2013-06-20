package org.rifidi.edge.readerplugin.obix.commands;

import java.io.IOException;
import java.math.BigInteger;

import obix.Obj;
import obix.contracts.Id;
import obix.io.ObixDecoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;
import org.rifidi.edge.readerplugin.obix.ObixSensorSession;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.IdImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.FanSpeedActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.FanSpeedActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.IndoorBrightnessSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.PresenceDetectorSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.Sensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.IndoorBrightnessSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.PresenceDetectorSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.SensorImpl;

public class ObixCommand_Poll extends TimeoutCommand {

	private ObixSensorSession session;

	private static final Log logger = LogFactory.getLog(ObixCommand_Poll.class);
	private String obixReaderId;
	HttpClient httpclient;

	IoTSensedEvent tagInfo = null;
	IoTSensedEvent[] tagInfos = new IoTSensedEvent[6];

	public ObixCommand_Poll(String commandID) {
		super(commandID);
		logger.info("Construct Obix Poll Command");
	}

	@Override
	public void execute() {
		logger.info("run Obix Poll Command");
		this.session = (ObixSensorSession) this.sensorSession;
		this.obixReaderId = this.session.getSensor().getID();

		httpclient = this.session.getHttpclient();

		String baseUrl = "http://" + this.session.getHost() + ":" + this.session.getPort();

		if (tagInfo == null) {

			tagInfo = new IoTSensedEvent(null, obixReaderId, 0, System.currentTimeMillis());
		}

		
		Obj[] resources = this.session.getLobby().list();
		for (Obj o : resources) {
			String oHref = o.getHref().getPath();
			String reqUrl = baseUrl + "/" + oHref;
			HttpGet aGetReq = new HttpGet(reqUrl);
			HttpResponse aResp;
			try {
				aResp = httpclient.execute(aGetReq);

				String aContent = IOUtils.toString(aResp.getEntity().getContent()).replace("</obj>\n", "</obj>");

				Obj kid = ObixDecoder.fromString(aContent);
				//System.out.println("kid: \n" + aContent);
				if ((kid.getIs() != null)) {
					try {
						if (kid instanceof Id) {
							String epc = ((Id) kid).value().get();
							tagInfo.setTag(getTag(new String("30744B5A1C0000100000000A")));
							logger.info("EPC: " + epc);
						}

						if (kid instanceof IndoorBrightnessSensor) {
							double brightnessValue = ((IndoorBrightnessSensor) kid).roomIlluminationValue().get();
							tagInfo.addExtraInformation("brightness", brightnessValue);
							logger.info("Brightness: " + brightnessValue);
						}

						if (kid instanceof PresenceDetectorSensor) {
							boolean presence = ((PresenceDetectorSensor) kid).presenceStatusValue().get();
							tagInfo.addExtraInformation("presence", presence);
							logger.info("Presence status: " + presence);
						}

						if (kid instanceof FanSpeedActuator) {
							long fanSpeed = ((FanSpeedActuator) kid).fanSpeedSetpointValue().get();
							tagInfo.addExtraInformation("fanSpeed", fanSpeed);
							logger.info("Fan speed set point: " + fanSpeed);
						}
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sensorSession.getSensor().sendEvent(tagInfo);
	}
	
	public DatacontainerEvent getTag(String szEPC) {
		// the new event
		DatacontainerEvent tagData = null;
		
		BigInteger epc = null;
		try {
			epc = new BigInteger(Hex.decodeHex(szEPC.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode tag: "
					+ szEPC);
		}
		int numbits = szEPC.length() * 4;

		// choose whether to make a gen1 or a gen2 tag
		/*
		if (alienTag.getProtocol() == 1) {
			EPCGeneration1Event gen1event = new EPCGeneration1Event();
			// make some wild guesses on the length of the epc field
			gen1event.setEPCMemory(epc, numbits);
			tagData = gen1event;
		} else
		*/
		//NOTE: We currently support only Gen2
		{
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(epc, numbits);
			tagData = gen2event;
		}
		
		return tagData;
	}

}
