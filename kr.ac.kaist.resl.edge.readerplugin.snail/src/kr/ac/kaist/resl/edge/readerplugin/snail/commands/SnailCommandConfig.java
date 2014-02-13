package kr.ac.kaist.resl.edge.readerplugin.snail.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

@JMXMBean
public class SnailCommandConfig extends AbstractCommandConfiguration<SnailCommand_Poll> {

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
		mbeaninfo = strategy.getMBeanInfo(SnailCommandConfig.class);
	}
	
	@Override
	public SnailCommand_Poll getCommand(String readerID) {
		SnailCommand_Poll scp = new SnailCommand_Poll(readerID);
		return scp;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}
