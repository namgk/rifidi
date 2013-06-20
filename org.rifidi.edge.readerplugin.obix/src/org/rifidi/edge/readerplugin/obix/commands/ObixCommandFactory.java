package org.rifidi.edge.readerplugin.obix.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.readerplugin.obix.ObixSensorFactory;

public class ObixCommandFactory extends AbstractCommandConfigurationFactory<AbstractCommandConfiguration<ObixCommand_Poll>> {

	/** Name of the command. */
	public static final String name = "Obix-poll";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(ObixCommandConfig.class);
	}
	
	@Override
	public void createInstance(String serviceID) {
		ObixCommandConfig cmdCfgInstance = new ObixCommandConfig();
		cmdCfgInstance.setID(serviceID);
		cmdCfgInstance.register(getContext(), ObixSensorFactory.FACTORY_ID);
		
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

	@Override
	public String getFactoryID() {
		return name;
	}

	@Override
	public String getReaderFactoryID() {
		return ObixSensorFactory.FACTORY_ID;
	}

	@Override
	public String getDisplayName() {
		return "Obix poll";
	}

	@Override
	public String getCommandDescription() {
		return "Command the Obix server to updpate changes";
	}

}
