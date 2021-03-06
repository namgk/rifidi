/*
 * 
 * EsperReceiver.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.services.esper.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;

import com.espertech.esper.client.EPRuntime;

/**
 * Receive and handle ReadCycles from readers. Publish them to Esper.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EsperReceiver implements Runnable {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(EsperReceiver.class);
	/** Set containing the sensors the receiver currently handles. */
	private final Set<Sensor> sensors;
	/** The esper runtime. */
	private final EPRuntime runtime;

	/**
	 * Constructor.
	 * 
	 * @param runtime
	 */
	public EsperReceiver(final EPRuntime runtime) {
		sensors = new CopyOnWriteArraySet<Sensor>();
		this.runtime = runtime;
	}

	/**
	 * Add a new sensor to the receiver.
	 * 
	 * @param sensor
	 */
	public void addSensor(final Sensor sensor) {
		sensors.add(sensor);
	}

	/**
	 * Remove a sensor from the receiver.
	 * 
	 * @param sensor
	 */
	public void removeSensor(final Sensor sensor) {
		sensors.add(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			for (Sensor sensor : sensors) {
				try {
					EsperEventContainer container = sensor.receive(this);
					runtime.sendEvent(container.getReadCycle());
					for (Object event : container.getOtherEvents()) {
						runtime.sendEvent(event);
					}
				} catch (NotSubscribedException e) {
					throw new RuntimeException(e);
				}
				// when a service becomes unavailable the proxy throws a runtime
				// exception
				catch (RuntimeException re) {
					logger.debug("A sensor went away. " + re);
					sensors.remove(sensor);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

}
