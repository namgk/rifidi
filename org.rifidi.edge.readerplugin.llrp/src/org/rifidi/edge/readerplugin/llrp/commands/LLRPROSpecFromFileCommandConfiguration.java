/*
 *  LLRPROSpecFromFileCommandConfiguration.java
 *
 *  Created:	Apr 26, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.util.Util;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * The configuration class for the LLRPROSpecFromFileCommand. This class
 * contains the "filename" property where the xml file where the ADD_ROSPEC
 * command will be stored in is, and it will also parse the file when the
 * command has been submitted.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@JMXMBean
public class LLRPROSpecFromFileCommandConfiguration extends
		AbstractCommandConfiguration<LLRPROSpecFromFileCommand> {

	/**
	 * Logger file.
	 */
	private static final Log logger = LogFactory
			.getLog(LLRPROSpecFromFileCommandConfiguration.class);

	/** The name of this command type */
	public static final String name = "LLRPADD_ROSPECFileCommand-Configuration";

	/** MBeanInfo, for Spring */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(LLRPROSpecFromFileCommandConfiguration.class);
	}

	/**
	 * Constructor
	 */
	public LLRPROSpecFromFileCommandConfiguration() {
	}

	/**
	 * The file where the ADD_ROSPEC command is located.
	 */
	private String filename = "addrospec.xml";

	/**
	 * 
	 */
	private static final String category = "ROSpec";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public LLRPROSpecFromFileCommand getCommand(String readerID) {
		LLRPROSpecFromFileCommand retval = null;
		try {
			LLRPROSpecFromFileCommand command = new LLRPROSpecFromFileCommand(
					super.getID());
			/*
			 * Parse the command here. This will go wrong if the file doesn't
			 * exist, or the file is not of the correct type, or if the file
			 * isn't correctly formatted. If you don't know how to format a
			 * file, generate an ADD_ROSPEC xml with LLRP Commander.
			 */
			String directory = System.getProperty("org.rifidi.home");
			String file = directory+File.separator+filename;
			command.setAddrospeccommand((ADD_ROSPEC) Util
					.loadXMLLLRPMessage(new File(file)));
			retval = command;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (JDOMException e) {
			logger.error(e.getMessage());
		} catch (InvalidLLRPMessageException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return retval;
	}

	/**
	 * 
	 * @return
	 */
	@Property(displayName = "Filename", description = "The location of the file where the "
			+ "ADD_ROSpec command is.  The file should have an XML representation of an ADD_ROSpec "
			+ "command in it.  ", writable = true, type = PropertyType.PT_STRING, defaultValue = "addrospec.xml"
			+ "", category = category)
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}

}
