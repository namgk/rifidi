package kr.ac.kaist.resl.edge.readerplugin.obix.server.command;

import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class ObixServerCmd_Update extends TimeoutCommand {

	public ObixServerCmd_Update(String commandID) {
		super(commandID);
		logger.info("Construct Obix Update Command");
	}

	private static final Log logger = LogFactory.getLog(ObixServerCmd_Update.class);
	IoTSensedEvent tagInfo;

	public void setTagInfo(IoTSensedEvent tagInfo) {
		this.tagInfo = tagInfo;
	}

	@Override
	protected void execute() throws TimeoutException {
		logger.info("run Obix Update Command");
		sensorSession.getSensor().sendEvent(tagInfo);
	}

}
