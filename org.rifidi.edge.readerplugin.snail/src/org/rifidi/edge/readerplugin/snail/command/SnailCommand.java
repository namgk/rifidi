package org.rifidi.edge.readerplugin.snail.command;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

import javax.management.MBeanInfo;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapEmptyMessage;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapRequestCode;

public class SnailCommand extends TimeoutCommand implements CoapClient {
	
	String m_sensorID = null;
	
	private int SnailCommandID = 1;
	
	private static final Log logger = LogFactory
	.getLog(SnailCommand.class);
	
	
	static int counter = 0;
    CoapChannelManager channelManager = null;
    CoapClientChannel clientChannel = null;
    SensorInformation sensors;
    static IoTSensedEvent tagInfo = null;
	
    static IoTSensedEvent[] tagInfos = new IoTSensedEvent[6];
	/**
	 * @param commandID
	 */
	public SnailCommand(String commandID) {
		super(commandID);
		// TODO Auto-generated constructor stub
		logger.info("Construct SnailCommand");
		
		sensors = new SensorInformation();
	}

	
	public void setChannel(CoapClientChannel ch)
	{
		clientChannel = ch;
	}
	
	public void setSensorID(String sensorID)
	{
		this.m_sensorID = sensorID;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void execute() {
		// TODO Auto-generated method stub
		//Invoke coap message here.
		logger.info("run SnailCommand");
		assert(clientChannel != null);

		/*Resource discovery example*/
		/*
		CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest.setContentType(CoapMediaType.text_plain);
		coapRequest.setUriPath("/.well-known/core");
		coapRequest.setMessageID(1);
		clientChannel.sendMessage(coapRequest);
		*/

		/*ID should always come first*/
		/*
		CoapRequest coapRequest0 =  clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest0.setContentType(CoapMediaType.text_plain);
		coapRequest0.setUriPath("/id");
		clientChannel.sendMessage(coapRequest0);
		
		CoapRequest coapRequest1 = clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest1.setContentType(CoapMediaType.text_plain);
		coapRequest1.setUriPath("/sensors/temp");
		coapRequest1.setMessageID(1);			
		clientChannel.sendMessage(coapRequest1);
		CoapRequest coapRequest2 = clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest2.setContentType(CoapMediaType.text_plain);
		coapRequest2.setUriPath("/sensors/humid");
		coapRequest2.setMessageID(2);			
		clientChannel.sendMessage(coapRequest2);
		CoapRequest coapRequest3 =  clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest3.setContentType(CoapMediaType.text_plain);
		coapRequest3.setUriPath("/sensors/light");
		coapRequest3.setMessageID(3);
		clientChannel.sendMessage(coapRequest3);
		*/
		CoapRequest coapRequest0 =  clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest0.setContentType(CoapMediaType.text_plain);
		coapRequest0.setUriPath("/id");
		clientChannel.sendMessage(coapRequest0);
		
		CoapRequest coapRequest1 = clientChannel.createRequest(true, CoapRequestCode.GET);
		coapRequest1.setContentType(CoapMediaType.text_plain);
		coapRequest1.setUriPath("/sensors");
		coapRequest1.setMessageID(1);			
		clientChannel.sendMessage(coapRequest1);
		
	}
	

	
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(SnailCommand.class);
	}
	
	@Override
	public void onConnectionFailed(CoapClientChannel channel,
			boolean notReachable, boolean resetByServer) {
		// TODO Auto-generated method stub
		logger.debug("Connection Failed");
	}

	@Override
	public void onSeparateResponseAck(CoapClientChannel channel,
			CoapEmptyMessage message) {
		// TODO Auto-generated method stub
		logger.debug("Received Ack of Separate Response");
	}

	@Override
	public void onSeparateResponse(CoapClientChannel channel,
			CoapResponse response) {
		// TODO Auto-generated method stub
		logger.debug("Received Separate Response");
	}


	@Override
	public void onResponse(CoapClientChannel channel, CoapResponse response) {

		logger.info("Received response, " + "Code: " + response.getMessageCodeValue() + ", "
				+ "Type: " + response.getPacketType() + ", " + "Id: " + response.getMessageID() + ", "
				+ "Payload: " + (new String(response.getPayload()))
				//+ "Option: " + response.getHeader().getOption(CoapHeaderOptions.HeaderOptionNumber.Location_Path).getLongLength()
				);
		logger.debug(new String(response.getPayload()));
		logger.debug("Channel Remote address: " + channel.getRemoteAddress() + " port:" + channel.getRemotePort() + 
				" MessageID : " + response.getMessageID() + "Code Value " + response.getMessageCodeValue());
		

		//processOneSNAILTag(response);
		
		if(tagInfo == null)
		{
			
			tagInfo = new IoTSensedEvent(null, m_sensorID, 0, System.currentTimeMillis());
		}
		
		
		if( response.getMessageID() == 1 )		//sensors
		{
			// payload is [ float, float, float ] format.
			String payload = new String(response.getPayload());
			String payload2 = payload.replace('[', ' ');
			String payload3 = payload2.replace(']', ' ');
			String payload4 = payload3.trim();
			String[] sensorValues = payload4.split(",");
			sensors.temperature = sensorValues[0];
			sensors.humidity = sensorValues[1];
			sensors.lightness = sensorValues[2];
			
			tagInfo.addExtraInformation("temperature", sensorValues[0]);
			tagInfo.addExtraInformation("humidity", sensorValues[1]);
			tagInfo.addExtraInformation("lightness", sensorValues[2]);
			
		} else {
			// EPC response
			tagInfo.setTag(getTag(new String(response.getPayload())));
		}
		
		HashMap<String, Serializable> extraInfo = tagInfo.getExtraInformation();
		logger.info("The extra info: number: " + extraInfo.size());
		
		/*FIXME: Temporal use, I just assume that once all the three types of sensor data is received from the node
		 * the EPC number is already there. Note this assumption is not reasonable in real-deployment.
		 * */
		if((extraInfo.size()) >= 3 && tagInfo.getTag() != null) 
		{	
			logger.info("@@ let's send event");
			sensorSession.getSensor().sendEvent(tagInfo);
			//extraInfo.clear();
			//tagInfo.setTag(null);
		}
		//System.out.println("sent");
		// for (String key : tag.getExtraInformation().keySet()) {
		// System.out.println(key + ", "
		// + tag.getExtraInformation().get(key));
		// }
		
		
	}


	private void processOneSNAILTag(CoapResponse response) {
		//these program lines are temporarily used for the development of the IoT project
		//only used for the other sensor events (not RFID event)
		if(tagInfo == null)
		{
			//FIXME: For now, we support only one SNAIL node.
			
			tagInfo = new IoTSensedEvent(null, m_sensorID, 0, System.currentTimeMillis());
		}
		
		
		if( response.getMessageID() == 1 )		//temperature
		{
			
			//sensors.temperature = new String(response.getPayload());
			tagInfo.addExtraInformation("temperature", new String(response.getPayload()));
			
		}
		else if( response.getMessageID() == 2 )		//humidity
		{
			//sensors.humidity = new String(response.getPayload());
			tagInfo.addExtraInformation("humidity", new String(response.getPayload()));
		}
		else if( response.getMessageID() == 3 )		//lightness
		{
			//sensors.lightness = new String(response.getPayload());
			tagInfo.addExtraInformation("light", new String(response.getPayload()));
		}else
		{
			//TODO 
			tagInfo.setTag(getTag(new String(response.getPayload())));
		}
		
		HashMap<String, Serializable> extraInfo = tagInfo.getExtraInformation();
		logger.info("The extra info: number: " + extraInfo.size());
		
		/*FIXME: Temporal use, I just assume that once all the three types of sensor data is received from the node
		 * the EPC number is already there. Note this assumption is not reasonable in real-deployment.
		 * */
		if((extraInfo.size()) >= 3 && tagInfo.getTag() != null) 
		{	
			logger.info("@@ let's send event");
			sensorSession.getSensor().sendEvent(tagInfo);
			//extraInfo.clear();
			//tagInfo.setTag(null);
		}
		// for (String key : tag.getExtraInformation().keySet()) {
		// System.out.println(key + ", "
		// + tag.getExtraInformation().get(key));
		// }
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



class SensorInformation
{
	public String temperature;
	public String humidity;
	public String lightness;
	
	public SensorInformation() {
		// TODO Auto-generated constructor stub
		temperature = "";
		humidity = "";
		lightness = "";
	}
}
