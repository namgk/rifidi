package org.rifidi.edge.readerplugin.snail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.rifidi.edge.readerplugin.snail.command.SnailCommand;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapClientChannel;



public class SnailSession extends AbstractSensorSession {

	//private static final String SERVER_ADDRESS = "iotsensor1.iot.kr";
	private static final String SERVER_ADDRESS = "127.0.0.1";
	
	
	private static final String[] SERVER_ADDRESSES = {"ps.iot.kr"
		/*"iotsensor1.iot.kr",
		"iotsensor2.iot.kr",
		"iotsensor3.iot.kr",
		"iotsensor4.iot.kr",
		"iotsensor5.iot.kr",
		"iotsensor6.iot.kr"*/
	};
	
	/*
	private static final String[] SERVER_ADDRESSES = {"[::1]",
		"[::1]",
		"[::1]",
		"[::1]",
		"[::1]",
		"[::1]"
	};
	*/
	public CoapClientChannel[] channels;
	public SnailCommand[] cmds;
	
	
    private static final int port = 5683;
    /** atomic boolean that is true if we are inside the connection attempt loop */
	private AtomicBoolean connectingLoop = new AtomicBoolean(false);
	private int maxConAttempts = 10;
	int reconnectionInterval = 1000;
	
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(SnailSession.class);
	
	/**
	 * @param sensor
	 * @param ID
	 * @param commandConfigurations
	 */
	public SnailSession(AbstractSensor<?> sensor, String ID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		logger.info("Construct SnailSession");
	}


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		// TODO Auto-generated method stub
		logger.info("Snail Session " + this.getID() + " on sensor "
				+ this.getSensor().getID() + " attempting to connect to "
				+ SERVER_ADDRESS + ":" + port);
		
		this.setStatus(SessionStatus.CONNECTING);
		
		
		 //modified by Janggwan -------------------------------------------------------
		
		/*
		SnailCommand cmd = null;
		CoapClientChannel channel = null;
		
		
		cmd = new SnailCommand("Snail Timeout");
		
		channel = BasicCoapChannelManager.getInstance()
		.connect(cmd, InetAddress.getByName(SERVER_ADDRESS), port);
		
		// if not connected, exit
		//if (!connected) {
		//	setStatus(SessionStatus.CLOSED);
		//	throw new IOException("Cannot connect");
		//}
		
		if (!processing.compareAndSet(false, true)) {
			logger.warn("Executor was already active! ");
		}
		executor = new ScheduledThreadPoolExecutor(1);
		cmd.setChannel(channel);
		cmd.setSensorID(this.getSensor().getID());
		submit(cmd, 30, TimeUnit.SECONDS);
		setStatus(SessionStatus.PROCESSING);
		
		*/
		
		// to ----------------------------------------------------------------------
		
		cmds = new SnailCommand[SERVER_ADDRESSES.length];
		channels = new CoapClientChannel[SERVER_ADDRESSES.length];
		
		for(int i=0;i<SERVER_ADDRESSES.length;i++) {
			cmds[i] = new SnailCommand("Snail Timeout");
			channels[i] = BasicCoapChannelManager.getInstance()
				.connect(cmds[i], 
						InetAddress.getByName(SERVER_ADDRESSES[i]),
						port);
			
			if (!processing.compareAndSet(false, true)) {
				logger.warn("Executor was already active! ");
			}
			executor = new ScheduledThreadPoolExecutor(1);
			cmds[i].setChannel(channels[i]);
			cmds[i].setSensorID(this.getSensor().getID());
			//submit(cmds[i], 60, TimeUnit.SECONDS);
			//setStatus(SessionStatus.PROCESSING);
		}
		
		// ---------------------------------------------------------------------------

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Snail Session: " + "Test" + ":" + "port" + " (" + getStatus() + ")";
	}
	
}
