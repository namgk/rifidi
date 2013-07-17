package kr.ac.kaist.resl.wsn.app;

import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class WsnRifidiApplication extends AbstractRifidiApp {

	private ObixEventListener ol;
	public void setOl(ObixEventListener ol) {
		this.ol = ol;
	}

	public WsnRifidiApplication() {
		super("IoT6", "Obix");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void _start() {
		super._start();
		
		addEventType(IoTSensedEvent.class);
		addStatement("create window sensedTemperatureWindow.win:time(60 sec) as IoTSensedEvent");	
		addStatement("insert into sensedTemperatureWindow select * from IoTSensedEvent where sensingDevice='Obix_1'");	
		addStatement("select * from sensedTemperatureWindow where sensingDevice='Obix_1'", ol);
	}
	
	@Override
	protected void _stop() {
		super._stop();
		// unsubscribe from rifidiservices here
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

}
