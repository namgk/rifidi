/*
 *  ObixSensor.java
 *
 *  Created:	Aug 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.obix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;

/**
 * A obix plugin that can handle data coming in from any reader or source, if it
 * is sent to the serversocket in the correct format. The format is as follows:
 * 
 * ID:(tag ID)|Antenna:(antenna)|Timestamp:(millis since epoch)
 * 
 * Any amount of other values can be specified in pairs delimited by colons,
 * with pipes delimiting pairs.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@JMXMBean
public class ObixSensor extends AbstractSensor<ObixSensorSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ObixSensor.class);
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** A queue for putting commands to be executed next */
	// private final LinkedBlockingQueue<ObixCommandObjectWrapper>
	// propCommandsToBeExecuted;
	/** A hashmap containing all the properties for this reader */
	private final ConcurrentHashMap<String, String> readerProperties;
	/** IP address of the sensorSession. */
	private volatile String ipAddress = ObixReaderDefaultValues.IPADDRESS;
	/** Port to connect to. */
	private volatile Integer port = Integer
			.parseInt(ObixReaderDefaultValues.PORT);
	/** Username for the telnet interface. */
	private volatile String username = ObixReaderDefaultValues.USERNAME;
	/** Password for the telnet interface. */
	private volatile String password = ObixReaderDefaultValues.PASSWORD;
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = Integer
			.parseInt(ObixReaderDefaultValues.RECONNECTION_INTERVAL);
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = Integer
			.parseInt(ObixReaderDefaultValues.MAX_CONNECTION_ATTEMPTS);
	/** The port of the server socket */
	private volatile Integer notifyPort = 0;
	private volatile Integer ioStreamPort = 0;
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	private String displayName = "Obix";
	/** The Commands this session can use */
	private final Set<AbstractCommandConfiguration<?>> commands;

	/** The only session an obix reader allows. */
	private AtomicReference<ObixSensorIPPollSession> session = new AtomicReference<ObixSensorIPPollSession>();

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(ObixSensor.class);
	}

	public ObixSensor(Set<AbstractCommandConfiguration<?>> commands) {
		super();
		this.commands = commands;
		readerProperties = new ConcurrentHashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// No properties.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		logger.info("create Obix Session.");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new ObixSensorIPPollSession(this,
					Integer.toString(sessionID), ipAddress, port, notifyPort,
					ioStreamPort, (int) (long) reconnectionInterval,
					maxNumConnectionAttempts, username, password,
					notifierService, this.getID(), commands))) {
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		logger.info("create Obix Session with sessio DTO");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new ObixSensorIPPollSession(this,
					Integer.toString(sessionID), ipAddress, port, notifyPort,
					ioStreamPort, (int) (long) reconnectionInterval,
					maxNumConnectionAttempts, username, password,
					notifierService, this.getID(), commands))) {
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		ObixSensorIPPollSession obixSession = session.get();
		if (obixSession != null) {
			ret.put(obixSession.getID(), obixSession);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {

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

	/*
	 * JMX PROPERTY GETTER/SETTERS
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Obix", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the IPADDRESS
	 */
	@Property(displayName = "IP Address", description = "IP Address of "
			+ "the Reader", writable = true, type = PropertyType.PT_STRING, category = "conn"
			+ "ection", defaultValue = ObixReaderDefaultValues.IPADDRESS, orderValue = 0)
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param IPADDRESS
	 *            the IPADDRESS to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the PORT
	 */
	// 
	@Property(displayName = "Port", description = "Port of the" + " Reader", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", orderValue = 1, defaultValue = ObixReaderDefaultValues.PORT, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}

	/**
	 * @param PORT
	 *            the PORT to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the serverSocketPort
	 */
	@Property(displayName = "Notify Port", category = "connection", defaultValue = "54321", description = "The port configured in the Obix's Notify Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 1.5f)
	public Integer getNotifyPort() {
		return notifyPort;
	}

	/**
	 * @param ioStreamPort
	 *            the serverSocketPort to set
	 */
	public void setIOStreamPort(Integer ioStreamPort) {
		this.ioStreamPort = ioStreamPort;
	}

	/**
	 * @return the serverSocketPort
	 */
	@Property(displayName = "IO Stream Port", category = "connection", defaultValue = "54322", description = "The port configured in the Obix's IO Stream Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 1.75f)
	public Integer getIOStreamPort() {
		return ioStreamPort;
	}

	/**
	 * @param notifyPort
	 *            the serverSocketPort to set
	 */
	public void setNotifyPort(Integer notifyPort) {
		this.notifyPort = notifyPort;
	}

	/**
	 * @return the USERNAME
	 */
	@Property(displayName = "Username", description = "Username for logging "
			+ "into the Obix Reader", writable = true, category = "conn"
			+ "ection", defaultValue = ObixReaderDefaultValues.USERNAME, orderValue = 2)
	public String getUsername() {
		return username;
	}

	/**
	 * @param USERNAME
	 *            the USERNAME to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the PASSWORD
	 */
	@Property(displayName = "Password", description = "Password for logging"
			+ " into the Obix Reader", writable = true, category = "conn"
			+ "ection", defaultValue = ObixReaderDefaultValues.PASSWORD, orderValue = 3)
	public String getPassword() {
		return password;
	}

	/**
	 * @param PASSWORD
	 *            the PASSWORD to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the RECONNECTION_INTERVAL
	 */
	@Property(displayName = "Reconnection Interval", description = "Upon connection failure, the time to wait between two connection attempts (ms)", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", defaultValue = ObixReaderDefaultValues.RECONNECTION_INTERVAL, orderValue = 4, minValue = "0")
	public Integer getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * @param RECONNECTION_INTERVAL
	 *            the RECONNECTION_INTERVAL to set
	 */
	public void setReconnectionInterval(Integer reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * @return the MAX_CONNECTION_ATTEMPTS
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Upon connection failure, the number of times to attempt to recconnect before giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "connection", defaultValue = ObixReaderDefaultValues.MAX_CONNECTION_ATTEMPTS, orderValue = 5, minValue = "-1")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * @param MAX_CONNECTION_ATTEMPTS
	 *            the MAX_CONNECTION_ATTEMPTS to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

}
