/*
 *  SnailSensor.java
 *
 *  Created:	Aug 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package kr.ac.kaist.resl.edge.readerplugin.snail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

/**
 * The Snail sensor
 * 
 * @author Nam Giang - zang at kaist dot ac dot kr
 */

@JMXMBean
public class SnailSensor extends AbstractSensor<SnailSensorSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(SnailSensor.class);
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** IP address of the sensorSession. */
	private volatile String ipAddress = SnailReaderDefaultValues.IPADDRESS;
	/** Port to connect to. */
	private volatile Integer port = Integer
			.parseInt(SnailReaderDefaultValues.PORT);
	/** Port to connect to. */
	private volatile Integer coapPort = Integer
			.parseInt(SnailReaderDefaultValues.COAP_PORT);
	/** Username for the telnet interface. */
	private volatile String username = SnailReaderDefaultValues.USERNAME;
	/** Password for the telnet interface. */
	private volatile String password = SnailReaderDefaultValues.PASSWORD;
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = Integer
			.parseInt(SnailReaderDefaultValues.RECONNECTION_INTERVAL);
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = Integer
			.parseInt(SnailReaderDefaultValues.MAX_CONNECTION_ATTEMPTS);
	/** The port of the server socket */
	private volatile Integer notifyPort = 0;
	private volatile Integer ioStreamPort = 0;
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	private String displayName = "Snail";
	/** The Commands this session can use */
	private final Set<AbstractCommandConfiguration<?>> commands;

	/** The only session a snail reader allows. */
	private AtomicReference<SnailSensorSession> session = new AtomicReference<SnailSensorSession>();

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(SnailSensor.class);
	}

	public SnailSensor(Set<AbstractCommandConfiguration<?>> commands) {
		super();
		this.commands = commands;
		//new ConcurrentHashMap<String, String>();
	}

	@Override
	public void applyPropertyChanges() {
		// No properties.
	}

	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		logger.info("create Snail Session.");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new SnailSensorSession(this,
					Integer.toString(sessionID), ipAddress, port, coapPort, notifyPort,
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

	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		logger.info("create Snail Session with sessio DTO");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new SnailSensorSession(this,
					Integer.toString(sessionID), ipAddress, port, coapPort, notifyPort,
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

	@Override
	public void destroySensorSession(String id) {
		SnailSensorSession snailSession = session.get();
		if (snailSession != null) {
			if (snailSession.getID().equals(id)) {
				snailSession.killAllCommands();
				snailSession.disconnect();
				// TODO: remove this once we get AspectJ in here!
				session.set(null);
				notifierService
						.removeSessionEvent(this.getID(), id);
			}
		}
		logger.warn("Tried to delete a non existant session: " + id);

	}

	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		SnailSensorSession snailSession = session.get();
		if (snailSession != null) {
			ret.put(snailSession.getID(), snailSession);
		}
		return ret;
	}

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

	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Snail", orderValue = 0)
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
			+ "ection", defaultValue = SnailReaderDefaultValues.IPADDRESS, orderValue = 0)
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
			+ "ection", orderValue = 1, defaultValue = SnailReaderDefaultValues.PORT, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}
	
	@Property(displayName = "CoAP Port", description = "CoAP port of the" + " Reader", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", orderValue = 1, defaultValue = SnailReaderDefaultValues.COAP_PORT, minValue = "0", maxValue = "65535")
	public Integer getCoapPort() {
		return coapPort;
	}

	/**
	 * @param PORT
	 *            the PORT to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	public void setCoapPort(Integer coapPort) {
		this.coapPort = coapPort;
	}
	
//	/**
//	 * @return the serverSocketPort
//	 */
//	@Property(displayName = "Notify Port", category = "connection", defaultValue = "54321", description = "The port configured in the Snail's Notify Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 1.5f)
//	public Integer getNotifyPort() {
//		return notifyPort;
//	}
//
//	/**
//	 * @param ioStreamPort
//	 *            the serverSocketPort to set
//	 */
//	public void setIOStreamPort(Integer ioStreamPort) {
//		this.ioStreamPort = ioStreamPort;
//	}
//
//	/**
//	 * @return the serverSocketPort
//	 */
//	@Property(displayName = "IO Stream Port", category = "connection", defaultValue = "54322", description = "The port configured in the Snail's IO Stream Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 1.75f)
//	public Integer getIOStreamPort() {
//		return ioStreamPort;
//	}
//
//	/**
//	 * @param notifyPort
//	 *            the serverSocketPort to set
//	 */
//	public void setNotifyPort(Integer notifyPort) {
//		this.notifyPort = notifyPort;
//	}

	/**
	 * @return the USERNAME
	 */
	@Property(displayName = "Username", description = "Username for logging "
			+ "into the Snail Reader", writable = true, category = "conn"
			+ "ection", defaultValue = SnailReaderDefaultValues.USERNAME, orderValue = 2)
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
			+ " into the Snail Reader", writable = true, category = "conn"
			+ "ection", defaultValue = SnailReaderDefaultValues.PASSWORD, orderValue = 3)
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
			+ "ection", defaultValue = SnailReaderDefaultValues.RECONNECTION_INTERVAL, orderValue = 4, minValue = "0")
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
	@Property(displayName = "Maximum Connection Attempts", description = "Upon connection failure, the number of times to attempt to recconnect before giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "connection", defaultValue = SnailReaderDefaultValues.MAX_CONNECTION_ATTEMPTS, orderValue = 5, minValue = "-1")
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			super.destroy();
			SnailSensorSession snailSession = session.get();
			if (snailSession != null) {
					destroySensorSession(snailSession.getID());
			}
		}
	}

}
