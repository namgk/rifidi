/*
 * 
 * NonExistentCommandFactoryException.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.core.exceptions;

/**
 * Thrown if somebody tried to use a command that either doesn't exist or got
 * removed from the registry.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class NonExistentCommandFactoryException extends Exception {
	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NonExistentCommandFactoryException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public NonExistentCommandFactoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public NonExistentCommandFactoryException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public NonExistentCommandFactoryException(Throwable arg0) {
		super(arg0);
	}

}
