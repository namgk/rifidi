/*
 * 
 * SensorManagerServiceImpl.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.rmi.server;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.api.rmi.exceptions.CommandSubmissionException;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.services.ConfigurationService;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.exceptions.CannotCreateServiceException;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;

/**
 * 
 * 
 * @author kyle
 */
public class SensorManagerServiceImpl implements SensorManagerService {

	/** A data access object for the sensorSession configuration services */
	private ReaderDAO readerDAO;
	/** A data access object for configurations in OSGi */
	private ConfigurationService configurationService;
	/** A data access object for commands and command factories */
	private CommandDAO commandDAO;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(SensorManagerServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#createReader(java
	 * .lang.String, javax.management.AttributeList)
	 */
	@Override
	public void createReader(String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) {
		logger.debug("RMI call: createReader");
		try {
			configurationService.createService(readerConfigurationFactoryID,
					readerConfigurationProperties);
		} catch (CannotCreateServiceException e) {
			logger.warn("Sensor not created");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#deleteReader(java
	 * .lang.String)
	 */
	@Override
	public void deleteReader(String readerConfigurationID) {
		logger.debug("RMI call: deleteReader");
		configurationService.destroyService(readerConfigurationID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#getReaderFactories
	 * ()
	 */
	@Override
	public Set<ReaderFactoryDTO> getReaderFactories() {
		logger.debug("RMI call: getReaderFactories");
		Set<ReaderFactoryDTO> retVal = new HashSet<ReaderFactoryDTO>();
		for (AbstractSensorFactory<?> factory : this.readerDAO
				.getReaderFactories()) {
			retVal.add(factory.getReaderFactoryDTO());
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#getReaderFactory
	 * (java.lang.String)
	 */
	@Override
	public ReaderFactoryDTO getReaderFactory(String readerFactoryID) {
		logger.debug("RMI call: getReaderFactory");
		AbstractSensorFactory<?> factory = this.readerDAO
				.getReaderFactoryByID(readerFactoryID);
		if (factory != null) {
			return factory.getReaderFactoryDTO();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.rmi.services.SensorManagerService#getReaders()
	 */
	@Override
	public Set<ReaderDTO> getReaders() {
		logger.debug("RMI call: getReaders");
		Set<ReaderDTO> retVal = new HashSet<ReaderDTO>();

		// Get all ReaderConfigurations
		Set<AbstractSensor<?>> configurations = readerDAO.getReaders();
		for (AbstractSensor<?> readerConfig : configurations) {
			// get the ID of the sensorSession configuration
			String readerID = readerConfig.getID();
			// look up the associated service configuration object for the
			// sensorSession configuration
			Configuration config = configurationService
					.getConfiguration(readerID);
			if (config == null) {
				logger.debug("No Configuration Object with ID " + readerID
						+ " is available");
				break;
			}
			retVal.add(readerConfig.getDTO(config));
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#getReaderDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getReaderDescription(String readerConfigurationFactoryID) {
		logger.debug("RMI call: getReaderDescription");
		AbstractSensorFactory<?> configFactory = readerDAO
				.getReaderFactoryByID(readerConfigurationFactoryID);
		if (configFactory != null) {
			return configFactory
					.getServiceDescription(readerConfigurationFactoryID);
		} else {
			logger.warn("No SensorSession Configuration Factory with ID "
					+ readerConfigurationFactoryID + " is available");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#getReader(java.
	 * lang.String)
	 */
	@Override
	public ReaderDTO getReader(String readerConfigurationID) {
		logger.debug("RMI call: getReader");
		Configuration config = configurationService
				.getConfiguration(readerConfigurationID);
		AbstractSensor<?> reader = readerDAO
				.getReaderByID(readerConfigurationID);
		if ((config != null) && (reader != null)) {
			return reader.getDTO(config);
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#getSession(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public SessionDTO getSession(String readerID, String sessionID) {
		AbstractSensor<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader != null) {
			SensorSession session = reader.getSensorSessions().get(sessionID);
			if (session != null) {
				return session.getDTO();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#createSession(java
	 * .lang.String)
	 */
	@Override
	public Set<SessionDTO> createSession(String readerID) {
		logger.debug("RMI call: createSession");
		AbstractSensor<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return null;
		}
		try {
			reader.createSensorSession();
			logger.info("New reader session created on Reader " + readerID);
			Set<SessionDTO> sessionDTOs = new HashSet<SessionDTO>();
			for (SensorSession s : reader.getSensorSessions().values()) {
				sessionDTOs.add(s.getDTO());
			}
			return sessionDTOs;
		} catch (CannotCreateSessionException e) {
			logger.error("Cannot Create Sensor Session for " + reader.getID(),
					e);
			return Collections.emptySet();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#deleteSession(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void deleteSession(String readerID, String sessionID) {
		logger.debug("RMI call: deleteSession");
		AbstractSensor<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader != null) {
			try {
				reader.destroySensorSession(sessionID);
			} catch (CannotDestroySensorException ex) {
				logger.error(ex);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#killCommand(java
	 * .lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void killCommand(String readerID, String sessionID, Integer processID) {
		logger.debug("RMI call: killCommand");
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}
		SensorSession session = reader.getSensorSessions().get(sessionID);
		if (session != null) {
			session.killComand(processID);
			logger.info("Command with processID " + processID
					+ " killed on Session " + sessionID + " on reader "
					+ readerID);
		} else {
			logger.warn("No session with ID " + sessionID + " is available");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#startSession(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void startSession(String readerID, String sessionID) {
		logger.debug("RMI call: startSession");
		final String finalReaderID = readerID;
		final String finalSessionIndex = sessionID;

		Thread connectThread = new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				AbstractSensor<?> reader = readerDAO
						.getReaderByID(finalReaderID);
				if (reader == null) {
					logger.warn("No reader with ID " + finalReaderID
							+ " is available");
					return;
				}
				try {
					SensorSession session = reader.getSensorSessions().get(
							finalSessionIndex);
					if (session != null) {
						session.connect();
						logger.info("Session " + finalReaderID + " on Reader "
								+ finalReaderID + " has started");
						reader.applyPropertyChanges();
					} else {
						logger.warn("No session with ID " + finalSessionIndex
								+ " is available");
						return;
					}
				} catch (IOException e) {
					logger
							.error("Reader Session" + finalSessionIndex
									+ " on Reader " + finalReaderID
									+ " cannot connect");
				}
			}

		};

		connectThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#stopSession(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void stopSession(String readerID, String sessionID) {
		logger.debug("RMI call: stopSession");
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}

		SensorSession session = reader.getSensorSessions().get(sessionID);
		if (session != null) {
			session.disconnect();
		} else {
			logger.warn("No session with index " + sessionID + " is available");
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#submitCommand(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.Long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void submitCommand(String readerID, String sessionID,
			String commandID, Long repeatInterval, TimeUnit timeUnit)
			throws CommandSubmissionException {
		logger.debug("RMI call: submitCommand");
		submit(readerID, sessionID, commandID, repeatInterval, timeUnit);
	}

	/**
	 * Handles the work of submitting a command. A command can either be
	 * submitted as a single-shot or repeating command. Returns null if the
	 * command was
	 * 
	 * @param readerID
	 *            The ID of the reader to submit the command to
	 * @param sessionID
	 *            The session on the reader to submit the comand to
	 * @param commandID
	 *            The ID of the command to submit.
	 * @param repeatInterval
	 *            The repeat interval. If singleshot command, should be null
	 * @param timeUnit
	 *            The TimeIneterval. If singleshot command should be null
	 * @return JobID if recurring command, or null if singleshot command
	 */
	private Integer submit(String readerID, String sessionID, String commandID,
			Long repeatInterval, TimeUnit timeUnit)
			throws CommandSubmissionException {
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			String error = "No reader with ID " + readerID + " is available";
			logger.warn(error);
			throw new CommandSubmissionException(error);
		}
		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandID);
		if (commandConfig == null) {
			String error = "No command with ID " + commandID + " is available";
			logger.warn("No command with ID " + commandID + " is available");
			throw new CommandSubmissionException(error);
		}

		SensorSession session = reader.getSensorSessions().get(sessionID);
		if (session == null) {
			String error = "No session with ID " + sessionID + " is available";
			logger.warn(error);
			throw new CommandSubmissionException(error);
		}
		Command command = commandConfig.getCommand(readerID);
		if (command == null) {
			String error = "There was a problem creating a command with ID "
					+ commandID + " on reader with ID " + readerID;
			logger.warn(error);
			throw new CommandSubmissionException(error);
		}

		// if sinleshot command
		if (repeatInterval == null) {
			int processID = session.submit(commandID, 0L, null);
			logger.info("Command with ID " + commandID
					+ " submitted to session " + sessionID + " of reader "
					+ readerID + " for single shot execution");
			return processID;
		} else {
			Integer processID = session.submit(commandID, repeatInterval,
					timeUnit);
			logger.info("Command with ID " + commandID
					+ " submitted to session " + sessionID + " of reader "
					+ readerID + " with repeat interval " + repeatInterval
					+ " " + timeUnit + " processID=" + processID);
			return processID;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.rmi.services.SensorManagerService#setReaderProperties
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public void setReaderProperties(String readerConfigurationID,
			AttributeList readerConfigurationProperties) {
		logger.debug("RMI call: setReaderProperties");
		Configuration config = configurationService
				.getConfiguration(readerConfigurationID);
		AbstractSensor<?> reader = readerDAO
				.getReaderByID(readerConfigurationID);
		if ((config != null) && (reader != null)) {
			config.setAttributes(readerConfigurationProperties);
			reader.applyPropertyChanges();
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}
	}

	/**
	 * Used by spring to set the ReaderConfigDAO
	 * 
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerConfigDAO) {
		this.readerDAO = readerConfigDAO;
	}

	/**
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * Called by spring to set the commandDAO
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}
}
