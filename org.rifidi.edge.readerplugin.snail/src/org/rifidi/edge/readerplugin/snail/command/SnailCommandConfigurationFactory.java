package org.rifidi.edge.readerplugin.snail.command;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.readerplugin.snail.*;


public class SnailCommandConfigurationFactory extends 
AbstractCommandConfigurationFactory <AbstractCommandConfiguration<?>>{

	/** Name of the command. */
	private static final String name = "Snail-Poll";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(SnailCommandConfiguration.class);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		// TODO Auto-generated method stub
		return SnailFactory.FACTORY_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID) {
		SnailCommandConfiguration commandConfig = new SnailCommandConfiguration();
		commandConfig.setID(serviceID);
		commandConfig.register(getContext(), getReaderFactoryID());
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

	@Override
	public String getFactoryID() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		// TODO Auto-generated method stub
		return "Command the Snail reader to do something";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Snail Poll";
	}
}
