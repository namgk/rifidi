/*
 * 
 * ValidationExceptionResponse.java
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

@WebFault(name = "ValidationException", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
public class ValidationExceptionResponse extends Exception {
    public static final long serialVersionUID = 20090129110318L;
    
    private org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationException validationException;

    public ValidationExceptionResponse() {
        super();
    }
    
    public ValidationExceptionResponse(String message) {
        super(message);
    }
    
    public ValidationExceptionResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationExceptionResponse(String message, org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationException validationException) {
        super(message);
        this.validationException = validationException;
    }

    public ValidationExceptionResponse(String message, org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationException validationException, Throwable cause) {
        super(message, cause);
        this.validationException = validationException;
    }

    public org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationException getFaultInfo() {
        return this.validationException;
    }
}
