/*
 * 
 * NoSuchNameExceptionResponse.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.epcglobal.ale.api.lr.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.1.3
 * Thu Jan 29 11:03:18 EST 2009
 * Generated source version: 2.1.3
 * 
 */

@WebFault(name = "NoSuchNameException", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
public class NoSuchNameExceptionResponse extends Exception {
    public static final long serialVersionUID = 20090129110318L;
    
    private org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameException noSuchNameException;

    public NoSuchNameExceptionResponse() {
        super();
    }
    
    public NoSuchNameExceptionResponse(String message) {
        super(message);
    }
    
    public NoSuchNameExceptionResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchNameExceptionResponse(String message, org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameException noSuchNameException) {
        super(message);
        this.noSuchNameException = noSuchNameException;
    }

    public NoSuchNameExceptionResponse(String message, org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameException noSuchNameException, Throwable cause) {
        super(message, cause);
        this.noSuchNameException = noSuchNameException;
    }

    public org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameException getFaultInfo() {
        return this.noSuchNameException;
    }
}
