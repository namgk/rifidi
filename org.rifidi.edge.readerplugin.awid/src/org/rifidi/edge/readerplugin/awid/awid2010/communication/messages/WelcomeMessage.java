/*
 * WelcomeMessage.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

/**
 * The Welcome message from a awid reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class WelcomeMessage extends AbstractAwidMessage {

	private String message = null;
	
	public static final String WELCOME_PHRASE_3014 = "3014";
	
	/**
	 * 
	 * @param rawmessage
	 */
	public WelcomeMessage(byte[] rawmessage) {
		super(rawmessage);
		message = new String(rawmessage);
	}
	
	public boolean is3014() {
		if(message != null && message.contains(WELCOME_PHRASE_3014)) {
			return true;
		}
		return false;
	}
	
	

}
