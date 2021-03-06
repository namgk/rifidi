/*
 * 
 * AlienCommandObject.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.readerplugin.alien.commandobject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

/**
 * This is an Command Object use to formulate individual request/response
 * interactions with an Alien SensorSession. It provides default behavior for
 * getting and setting properties on a command where the command is of the form
 * 
 * get commandname\n
 * 
 * where the response is of the form
 * 
 * commandname=value\n
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AlienCommandObject {

	/** The command to be sent to the Alien SensorSession */
	protected String command;
	/** The sensorSession to send the command to */
	protected Alien9800ReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param command
	 *            A command to be send to the Alien sensorSession (see the
	 *            static command strings in AlienReader)
	 * @param sensorSession
	 *            A live sensorSession to send the command to
	 */
	public AlienCommandObject(String command) {
		this.command = command;
	}

	/**
	 * Set the session on the reader
	 * 
	 * @param session
	 */
	public void setSession(Alien9800ReaderSession session) {
		this.readerSession = session;
	}

	/**
	 * Execute a get or set on the reader
	 * 
	 * @return The return value from the Call
	 * @throws IOException
	 * @throws AlienException
	 */
	public abstract String execute() throws IOException, AlienException,
			TimeoutException;

}
