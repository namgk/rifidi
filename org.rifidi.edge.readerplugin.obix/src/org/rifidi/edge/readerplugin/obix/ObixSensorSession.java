/*
 *  ObixSensorSession.java
 *
 *  Created:	Aug 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.obix;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * The session class for the Obix sensor.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ObixSensorSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ObixSensorSession.class);

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	private ObixSensorSessionTagHandler tagHandler = null;

	/** The ID for the reader. */
	private String readerID = null;

//	public ObixSensorSession(AbstractSensor<?> sensor, String ID,
//			NotifierService notifierService, String readerID,
//			int serverSocketPort,
//			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
//		super(sensor, ID, serverSocketPort, 10, commandConfigurations);
//		logger.info("constructing obix session, reader id: " + readerID + " session id: " + ID);
//		this.readerID = readerID;
//		this.notifierService = notifierService;
//		this.tagHandler = new ObixSensorSessionTagHandler(readerID);
//
//	}

	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public ObixSensorSession(AbstractSensor<?> sensor, String ID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		logger.info("Construct ObixSession");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		notifierService.sessionStatusChanged(this.readerID, this.getID(),
				status);
	}

	/**
	 * Parses and sends the tag to the desired destination.
	 * 
	 * @param tag
	 */
	public void sendTag(byte[] message) {
		TagReadEvent event = this.tagHandler.parseTag(new String(message));

		Set<TagReadEvent> tres = new HashSet<TagReadEvent>();
		tres.add(event);
		ReadCycle cycle = new ReadCycle(tres, readerID, System
				.currentTimeMillis());

		this.getSensor().send(cycle);
		
		//TODO: SEND TAGS
		//this.template.send(this.template.getDefaultDestination(),
		//		new ReadCycleMessageCreator(cycle));
	}

	@Override
	protected void _connect() throws IOException {
		logger.info("ObixSession connect");

	}

	@Override
	public void disconnect() {
		logger.info("ObixSession disconnect");
	}

}
