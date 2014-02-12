package kr.ac.kaist.resl.edge.readerplugin.snail.commands;

import javax.management.MBeanInfo;

import kr.ac.kaist.resl.edge.readerplugin.snail.SnailSensorFactory;

import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

public class SnailCommandFactory extends AbstractCommandConfigurationFactory<AbstractCommandConfiguration<SnailCommand_Poll>> {

	/** Name of the command. */
	public static final String name = "Snail-poll";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(SnailCommandConfig.class);
	}
	
	@Override
	public void createInstance(String serviceID) {
		SnailCommandConfig cmdCfgInstance = new SnailCommandConfig();
		cmdCfgInstance.setID(serviceID);
		cmdCfgInstance.register(getContext(), SnailSensorFactory.FACTORY_ID);
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
		return SnailSensorFactory.FACTORY_ID;
	}

	@Override
	public String getDisplayName() {
		return "Snail poll";
	}

	@Override
	public String getCommandDescription() {
		return "Command the Snail server to updpate changes";
	}

}
