/*
 *  TemperatureSensingEvent.java
 */
package org.rifidi.app.temperature.events;

import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This event occurs whenever a raw temperature sensing event occurs.  
 * 
 */

public class TemperatureSensingEvent {
	/** The sensing event is transformed to tag event class */
	private final TagReadEvent tag;

	/**
	 * @param tag
	 */
	public TemperatureSensingEvent(TagReadEvent tag) {
		super();
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public TagReadEvent getTag() {
		return tag;
	}
}
