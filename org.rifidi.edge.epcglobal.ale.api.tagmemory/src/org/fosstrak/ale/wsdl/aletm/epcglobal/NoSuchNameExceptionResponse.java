
package org.fosstrak.ale.wsdl.aletm.epcglobal;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF (incubator) 2.0.4-incubator
 * Fri Apr 16 14:42:07 KST 2010
 * Generated source version: 2.0.4-incubator
 * 
 */

@WebFault(name = "NoSuchNameException", targetNamespace = "urn:epcglobal:aletm:wsdl:1")

public class NoSuchNameExceptionResponse extends Exception {
    public static final long serialVersionUID = 20100416144207L;
    
    private org.fosstrak.ale.wsdl.aletm.epcglobal.NoSuchNameException noSuchNameException;

    public NoSuchNameExceptionResponse() {
        super();
    }
    
    public NoSuchNameExceptionResponse(String message) {
        super(message);
    }
    
    public NoSuchNameExceptionResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchNameExceptionResponse(String message, org.fosstrak.ale.wsdl.aletm.epcglobal.NoSuchNameException noSuchNameException) {
        super(message);
        this.noSuchNameException = noSuchNameException;
    }

    public NoSuchNameExceptionResponse(String message, org.fosstrak.ale.wsdl.aletm.epcglobal.NoSuchNameException noSuchNameException, Throwable cause) {
        super(message, cause);
        this.noSuchNameException = noSuchNameException;
    }

    public org.fosstrak.ale.wsdl.aletm.epcglobal.NoSuchNameException getFaultInfo() {
        return this.noSuchNameException;
    }
}
