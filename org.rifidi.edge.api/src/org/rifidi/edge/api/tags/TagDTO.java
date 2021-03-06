/*
 * TagDTO.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.tags;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * A data transfer object for a tag read. For serializing information about a
 * single tag event
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class TagDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;

	private BigInteger tagID;

	private int antennaNumber;

	private long timestamp;

	/**
	 * Constructor.
	 * 
	 * @param tagID
	 * @param antennaNumber
	 * @param timestamp
	 */
	public TagDTO(BigInteger tagID, int antennaNumber, long timestamp) {
		this.tagID = tagID;
		this.antennaNumber = antennaNumber;
		this.timestamp = timestamp;
	}

	/**
	 * Returns the ID for this tag.
	 * 
	 * @return the tagID
	 */
	public BigInteger getTagID() {
		return tagID;
	}

	/**
	 * Returns the antenna that this tag was seen on.
	 * 
	 * @return the antennaNumber
	 */
	public int getAntennaNumber() {
		return antennaNumber;
	}

	/**
	 * Returns the timestamp.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
