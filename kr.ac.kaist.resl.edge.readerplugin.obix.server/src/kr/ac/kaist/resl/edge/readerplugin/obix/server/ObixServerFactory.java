package kr.ac.kaist.resl.edge.readerplugin.obix.server;

import java.util.Map;

import javax.management.MBeanInfo;

import kr.ac.kaist.resl.stis.listener.coaplistener.CoAPListener;

import org.rifidi.edge.core.exceptions.InvalidStateException;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ObixServerFactory  extends AbstractSensorFactory<ObixServer> {

	/** The Unique FACTORY_ID for this Factory */
	public static final String FACTORY_ID = "ObixServer";
	/** Description of the sensorSession. */
	private static final String description = "A obix Rifidi Adapter. Server implementation  ";
	/** The name of the reader that will be displayed */
	private static final String displayname = "ObixServer";
	private CoAPListener cls;

	/** A JMS event notification sender */
	private volatile NotifierService notifierService;

	@Override
	public void createInstance(String serviceID) throws IllegalArgumentException,
			InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("ServiceID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("All services are not set");
		}
		ObixServer instance = new ObixServer(cls);
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
		instance.register(getContext(), FACTORY_ID);
		
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo)ObixServer.mbeaninfo;
	}

	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration, Map<?, ?> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration, Map<?, ?> properties) {
		// TODO Auto-generated method stub
		
	}
	
	public CoAPListener getCls() {
		return cls;
	}
	
	public void setCls(CoAPListener cls) {
		this.cls = cls;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Called by spring
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}
}
