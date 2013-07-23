package kr.ac.kaist.resl.edge.readerplugin.obix.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

@JMXMBean
public class ObixCommandConfig extends AbstractCommandConfiguration<ObixCommand_Poll> {

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
		mbeaninfo = strategy.getMBeanInfo(ObixCommandConfig.class);
	}
	
	@Override
	public ObixCommand_Poll getCommand(String readerID) {
		ObixCommand_Poll ocp = new ObixCommand_Poll(readerID);
		return ocp;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}
