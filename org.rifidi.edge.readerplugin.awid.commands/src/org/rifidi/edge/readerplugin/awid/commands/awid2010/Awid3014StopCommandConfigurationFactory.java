/*
 *  Awid3014StopCommandConfigurationFactory.java
 *
 *  Created:	Nov 12, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.awid.commands.awid2010;

import org.rifidi.edge.readerplugin.awid.awid2010.Awid3014SensorFactory;

/**
 * @author Matthew Dean
 *
 */
public class Awid3014StopCommandConfigurationFactory extends
		Awid2010StopCommandConfigurationFactory {

	public static final String FACTORY3014_ID = "Awid3014-Push-Stop";
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.readerplugin.awid.commands.awid2010.Awid2010StopCommandConfigurationFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY3014_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.readerplugin.awid.commands.awid2010.Awid2010StopCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return Awid3014SensorFactory.FACTORY_ID;
	}
}
