package kr.ac.kaist.resl.edge.readerplugin.obix.commands;

import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ObixCommand_Update extends TimeoutCommand {

	private static final Log logger = LogFactory.getLog(ObixCommand_Update.class);
	IoTSensedEvent tagInfo;
	
	public void setTagInfo(IoTSensedEvent tagInfo) {
		this.tagInfo = tagInfo;
	}

	/**
	 * @param commandID
	 */
	public ObixCommand_Update(String commandID) {
		super(commandID);
		logger.info("Construct Obix Update Command");
	}

	@Override
	protected void execute() throws TimeoutException {
		logger.info("run Obix Update Command");
		sensorSession.getSensor().sendEvent(tagInfo);
	}
	

}
