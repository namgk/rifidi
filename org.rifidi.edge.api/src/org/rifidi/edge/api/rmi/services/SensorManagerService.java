/*
 * SensorManagerService.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.rmi.services;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.api.rmi.exceptions.CommandSubmissionException;

/**
 * This is the interface for a stub that allows users to interact with readers
 * on the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface SensorManagerService {

	/**
	 * These are the ReaderFactories that are available on the server
	 * 
	 * @return a list of IDs of ReaderFactories that are currently available
	 */
	Set<ReaderFactoryDTO> getReaderFactories();

	/**
	 * This returns the ReaderFactoryDTO for the given readerFactoryID
	 * 
	 * @param readerFactoryID
	 *            The ReaderFactoryID to return
	 * @return The DTO for the readerFactory
	 * @throws RemoteException
	 */
	ReaderFactoryDTO getReaderFactory(String readerFactoryID);

	/**
	 * This method gets the readers that are currently available on the server
	 * 
	 * @return a Data Transfer Object that contains all the information about a
	 *         reader for all the currently available readers
	 * @throws RemoteException
	 */
	Set<ReaderDTO> getReaders();

	/**
	 * Gets a description of a Reader. This will contain the information
	 * necessary to construct a new instance of the reader.
	 * 
	 * @param readerFactoryID
	 *            the ID of the ReaderFactory
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         Reader
	 * @throws RemoteException
	 */
	MBeanInfo getReaderDescription(String readerFactoryID);

	/**
	 * Creates a new Reader for the supplied configuration.
	 * 
	 * @param readerFactoryID
	 *            the ID of the ReaderRactory
	 * @param readerProperties
	 *            all the properties to set on the new Reader
	 * @throws RemoteException
	 */
	void createReader(String readerFactoryID, AttributeList readerProperties);

	/**
	 * This method returns the DTO for a particular reader
	 * 
	 * @param readerID
	 *            The ID of the Reader to get
	 * @return The DTO for a reader
	 * @throws RemoteException
	 */
	ReaderDTO getReader(String readerID);

	/**
	 * This method is used to update the properties of a Reader. It may contain
	 * only the properties that you intend to change, but may contain values
	 * that will stay the same
	 * 
	 * @param readerID
	 *            the ID of the Reader to update
	 * @param readerProperties
	 *            the new properties to set
	 */
	void setReaderProperties(String readerID, AttributeList readerProperties);

	/**
	 * Remove a Reader from the Edge Server
	 * 
	 * @param readerID
	 *            the ID of the Reader
	 * @throws RemoteException
	 */
	void deleteReader(String readerID);

	/**
	 * Get a session with the given IDs
	 * 
	 * @param readerID
	 *            The ID of the reader that contains the session
	 * @param sessionID
	 *            The ID of the session
	 * @return The DTO of the Session
	 * @throws RemoteException
	 */
	SessionDTO getSession(String readerID, String sessionID);

	/**
	 * Create a new session on the reader.
	 * 
	 * @param The
	 *            list of sessions currently on this reader
	 * @throws RemoteException
	 */
	Set<SessionDTO> createSession(String readerID);

	/**
	 * Delete a reader session
	 * 
	 * @param readerID
	 *            The ID of the reader which has the session
	 * @param sessionID
	 *            The session to delete
	 * @throws RemoteException
	 */
	void deleteSession(String readerID, String sessionID);

	/**
	 * Starts a session on the reader
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionID
	 *            The session to start
	 * @throws RemoteException
	 */
	void startSession(String readerID, String sessionID);

	/**
	 * Stops a session
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionID
	 *            The session to stop
	 * @throws RemoteException
	 */
	void stopSession(String readerID, String sessionID);

	/**
	 * Submit a command for execution on the reader. If the repeatInterval is <
	 * 0, this command will execute only once.
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionID
	 *            The session to use
	 * @param commandID
	 *            The command to use
	 * @param repeatInterval
	 *            How often the command should be repeated. If 0, this command
	 *            will only be executed once
	 * @param timeUnit
	 *            Which time unit to use for the repeatInterval. Ignored if
	 *            repeatInterval is set to 0
	 */
	void submitCommand(String readerID, String sessionID, String commandID,
			Long repeatInterval, TimeUnit timeUnit)
			throws CommandSubmissionException;

	/**
	 * Stop a repeated command. The is removed from the executor and will not be
	 * repeatedly executed
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionID
	 *            The session to use
	 * @param processID
	 *            The command to kill
	 */
	void killCommand(String readerID, String sessionID, Integer processID);

}
