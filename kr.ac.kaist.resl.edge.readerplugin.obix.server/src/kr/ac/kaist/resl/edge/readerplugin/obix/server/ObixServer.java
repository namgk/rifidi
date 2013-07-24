package kr.ac.kaist.resl.edge.readerplugin.obix.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import kr.ac.kaist.resl.stis.listener.coaplistener.CoAPListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class ObixServer extends AbstractSensor<ObixServerSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ObixServer.class);
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	private String displayName = "ObixServer";
	private CoAPListener cls;
	
	/** The only session an obix reader allows. */
	private AtomicReference<ObixServerSession> session = new AtomicReference<ObixServerSession>();

	
	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(ObixServer.class);
	}
	
	public ObixServer(CoAPListener cls) {
		this.cls = cls;
	}

	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		logger.info("create Obix Session.");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new ObixServerSession(this,
					Integer.toString(sessionID), null, notifierService, this.getID(), cls))) {
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
		logger.info("create Obix Session with sessio DTO");
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new ObixServerSession(this,
					Integer.toString(sessionID), null, notifierService, this.getID(), cls))) {
				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(),
						Integer.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		ObixServerSession obixSession = session.get();
		if (obixSession != null) {
			ret.put(obixSession.getID(), obixSession);
		}
		return ret;
	}

	@Override
	public void destroySensorSession(String id) throws CannotDestroySensorException {
		ObixServerSession obixSession = session.get();
		if (obixSession != null) {
			if (obixSession.getID().equals(id)) {
				obixSession.killAllCommands();
				obixSession.disconnect();
				// TODO: remove this once we get AspectJ in here!
				session.set(null);
				notifierService
						.removeSessionEvent(this.getID(), id);
			}
		}
		logger.warn("Tried to delete a non existant session: " + id);
		
	}

	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration, Map<?, ?> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}

	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "ObixServer", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
