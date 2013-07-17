package org.rifidi.edge.readerplugin.obix.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

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

		if (tagInfo == null) {
			tagInfo = new IoTSensedEvent(null, obixReaderId, 0, System.currentTimeMillis());
		}
		
		Obj[] resources = this.session.getLobby().list();

		//if (this.session.isHttp()){

		
		
		for (Obj o : resources) {
			String oHref = o.getHref().getPath();
			try {
				String aContent = null;
				if (this.session.isHttp())
					aContent = getResourceByHttp(oHref);
				else 
					aContent = getResourceByCoAP(oHref);
				
				Obj kid = ObixDecoder.fromString(aContent);
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
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		sensorSession.getSensor().sendEvent(tagInfo);
	}
	
	public String getResourceByHttp(String resource) throws ClientProtocolException, IOException{
		String baseUrl = "http://" + this.session.getHost() + ":" + this.session.getPort();
		String reqUrl = baseUrl + "/" + resource;
		
		HttpResponse aResp = this.session.getHttpclient().execute(new HttpGet(reqUrl));
		String aContent = IOUtils.toString(aResp.getEntity().getContent()).replace("</obj>\n", "</obj>");
		
		return aContent;
	}
	
	public String getResourceByCoAP(String resource) throws URISyntaxException, IOException, InterruptedException{
		String baseUrl = "coap://" + this.session.getHost() + ":" + this.session.getCoAPPort();
		String reqUrl = baseUrl + "/" + resource;
		
		Request getRequest = new GETRequest();
		getRequest.setURI(new URI(reqUrl));
		getRequest.enableResponseQueue(true);
		getRequest.execute();
		
		Response response = getRequest.receiveResponse();
		
		return response.getPayloadString();
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
		{
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(epc, numbits);
			tagData = gen2event;
		}
		
		return tagData;
	}

}
