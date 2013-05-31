package org.rifidi.edge.readerplugin.obix.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

@JMXMBean
public class ObixCmdCfg extends AbstractCommandConfiguration<ObixCmdPoll> {

	private volatile String aParameter = "localhost";
	
	@Property(displayName = "A Parameter", description = "set a parameter", writable = true, type = PropertyType.PT_STRING, defaultValue = "a parameter")
	public String getAParameter() {
		return aParameter;
	}

	public void setAParameter(String aParameter) {
		this.aParameter = aParameter;
	}

	public static final MBeanInfo mbeaninfo;
	static{
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(ObixCmdCfg.class);
	}
	
	@Override
	public ObixCmdPoll getCommand(String readerID) {
		ObixCmdPoll ocp = new ObixCmdPoll(readerID);
		ocp.setAParameter(aParameter);
		return ocp;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}
