package kr.ac.kaist.resl.edge.readerplugin.obix.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import kr.ac.kaist.resl.edge.readerplugin.obix.ObixSensorSession;
import kr.ac.kaist.resl.edge.readerplugin.obix.STISUtils;

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

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.FanSpeedActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.IndoorBrightnessSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.PresenceDetectorSensor;

public class ObixCommand_Poll extends TimeoutCommand {

	private ObixSensorSession session;

	private static final Log logger = LogFactory.getLog(ObixCommand_Poll.class);
	private String obixReaderId;
	IoTSensedEvent tagInfo;

	public ObixCommand_Poll(String commandID) {
		super(commandID);
		logger.info("Construct Obix Poll Command");
	}

	@Override
	public void execute() {
		logger.info("run Obix Poll Command");
		this.session = (ObixSensorSession) this.sensorSession;
		this.obixReaderId = this.session.getSensor().getID();

		if (tagInfo == null) {
			tagInfo = new IoTSensedEvent(null, obixReaderId, 0, System.currentTimeMillis());
		}
		
		Obj[] resources = this.session.getLobby().list();
		
		for (Obj o : resources) {
			String oHref = o.getHref().getPath();
			try {
				String aContent = null;
				if (this.session.isHttp())
					aContent = this.session.getResourceByHttp(oHref);
				else 
					aContent = this.session.getResourceByCoAP(oHref);
				
				Obj kid = ObixDecoder.fromString(aContent);
				if ((kid.getIs() != null)) {
					try {
						if (kid instanceof Id) {
							String epc = ((Id) kid).value().get();
							tagInfo.setTag(STISUtils.getTag(new String("30744B5A1C0000100000000A")));
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
					} catch (DecoderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		sensorSession.getSensor().sendEvent(tagInfo);
	}
	
	

}
