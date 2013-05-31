/*
 * 
 * IoTSensedEvent.java
 */

package org.rifidi.edge.core.services.notification.data;

import java.io.Serializable;
import java.util.HashMap;

public class IoTSensedEvent implements Serializable {

	/** Serial Version ID for this class */
	private static final long serialVersionUID = 1L;	
	
	/** The tag event */
	//we assume that every thing contains its own EPC code like IEEE address
	private DatacontainerEvent epc=null;
	/** The time the the event was read */
	private long timestamp;
	/* the location that the event happens */
	private String location;
	/** The ID of device that read the events */
	private String sensingDevice;
	/* sensed value */
	private double sensedEventValue = -1;	
	/** The toString */
	private String toString;
	/**
	 * Any extra information that a tag contains would be stored here. Velocity
	 * or Distance information, or anything else about the tag which is not in
	 * the regular interface can go in here
	 */
	private HashMap<String, Serializable> sensedEvents = null;
	
	public IoTSensedEvent(DatacontainerEvent epcAddr, String sensorID, double value, long timestamp) {
		this.epc = epcAddr;
		this.sensingDevice = sensorID;
		this.sensedEvents = new HashMap<String, Serializable>();
		this.sensedEventValue = value;
		this.timestamp = timestamp;		
		toString = "[IoT RAW EVENT][EPC:" + epcAddr + "][sensingDevice:" + sensorID + 
					"][timestamp:" + timestamp + "]";
		//System.out.println("sensorID: " + sensorID + "/sensed value: " + value);
	}

	/**
	 * @return The ID of the reader that saw the tag
	 */
	public String getSensingDevice() {
		return sensingDevice;
	}

	/**
	 * @return the Tag data
	 */
	public DatacontainerEvent getTag() {
		return epc;
	}
	
	public void setTag(DatacontainerEvent epc)
	{
		this.epc = epc;
	}
	
	/**
	 * @return the sensed location
	 */
	public String getLocation() {
		return location;
	}	

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the sensed value
	 */
	public double getSensedValue() {
		return sensedEventValue;
	}	
	
	/**
	 * This returns the HashMap that stores the extra information for the
	 * TagReadEvent. An example of some extra information that a TagReadEvent
	 * might contain is the Velocity or RSSI information for an Alien tag.
	 * 
	 * @return
	 */
	public HashMap<String, Serializable> getExtraInformation() {
		return sensedEvents;
	}

	/**
	 * Adds a value to the Extra Information HashMap.
	 * 
	 * @param key
	 * @param value
	 */
	public void addExtraInformation(String key, Serializable value) {
		this.sensedEvents.put(key, value);
		toString = toString.concat("[" + key + ":" + value + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString;
	}
}
