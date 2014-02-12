package kr.ac.kaist.resl.edge.readerplugin.snail.commands;

import kr.ac.kaist.resl.edge.readerplugin.snail.STISUtils;
import kr.ac.kaist.resl.edge.readerplugin.snail.SnailSensorSession;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

public class SnailCommand_Poll extends TimeoutCommand {

	private SnailSensorSession session;

	private static final Log logger = LogFactory.getLog(SnailCommand_Poll.class);
	private String snailReaderId;
	IoTSensedEvent tagInfo;

	public SnailCommand_Poll(String commandID) {
		super(commandID);
		logger.info("Construct Snail Poll Command");
	}

	@Override
	public void execute() {
		logger.info("run Snail Poll Command");
		this.session = (SnailSensorSession) this.sensorSession;
		this.snailReaderId = this.session.getSensor().getID();

		if (tagInfo == null) {
			tagInfo = new IoTSensedEvent(null, snailReaderId, 0, System.currentTimeMillis());
		}
		
		String sensors = this.session.getResourceByCoAP("sensors");
		if (sensors == null) return;
		
		sensors = sensors.substring(1, sensors.length() -1);
		String[] resources = sensors.split(",");
		this.session.getResources().setResources(resources);
		System.out.println(this.session.getResources().toString());
		
		try {
			tagInfo.setTag(STISUtils.getTag(this.session.getResources().getEpc()));
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		
		tagInfo.addExtraInformation("Temperature", resources[0]);
		tagInfo.addExtraInformation("Humidity", resources[1]);
		tagInfo.addExtraInformation("Light", resources[2]);
				
		sensorSession.getSensor().sendEvent(tagInfo);
	}
}
