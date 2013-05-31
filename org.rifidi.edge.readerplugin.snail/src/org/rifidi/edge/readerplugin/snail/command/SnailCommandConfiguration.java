package org.rifidi.edge.readerplugin.snail.command;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;


public class SnailCommandConfiguration extends 
AbstractCommandConfiguration<SnailCommand> {

	/** Logger for this class. */
	private static final Log logger=LogFactory.getLog(SnailCommandConfiguration.class);
	
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(SnailCommandConfiguration.class);
	}
	
	public SnailCommandConfiguration(){
	}
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public SnailCommand getCommand(String readerID) {
		SnailCommand snailCmd = new SnailCommand(readerID);
		return snailCmd;
	}

	/* (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}
}
