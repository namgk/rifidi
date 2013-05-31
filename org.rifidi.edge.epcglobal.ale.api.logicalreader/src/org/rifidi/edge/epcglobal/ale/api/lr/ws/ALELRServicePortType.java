/*
 * 
 * ALELRServicePortType.java
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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.1.3
 * Thu Jan 29 11:03:18 EST 2009
 * Generated source version: 2.1.3
 * 
 */
 
@WebService(targetNamespace = "urn:epcglobal:alelr:wsdl:1", name = "ALELRServicePortType")
@XmlSeeAlso({org.rifidi.edge.epcglobal.ale.api.lr.data.ObjectFactory.class,ObjectFactory.class,org.rifidi.edge.epcglobal.ale.api.lr.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ALELRServicePortType {

    @WebResult(name = "GetLRSpecResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "getLRSpecReturn")
    @WebMethod
    public org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec getLRSpec(
        @WebParam(partName = "parms", name = "GetLRSpec", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        GetLRSpec parms
    ) throws SecurityExceptionResponse, ImplementationExceptionResponse, NoSuchNameExceptionResponse;

    @WebResult(name = "GetStandardVersionResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "getStandardVersionReturn")
    @WebMethod
    public java.lang.String getStandardVersion(
        @WebParam(partName = "parms", name = "GetStandardVersion", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        EmptyParms parms
    ) throws ImplementationExceptionResponse;

    @WebResult(name = "UndefineResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "undefineReturn")
    @WebMethod
    public UndefineResult undefine(
        @WebParam(partName = "parms", name = "Undefine", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        Undefine parms
    ) throws SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse;

    @WebResult(name = "DefineResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "defineReturn")
    @WebMethod
    public DefineResult define(
        @WebParam(partName = "parms", name = "Define", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        Define parms
    ) throws SecurityExceptionResponse, ImplementationExceptionResponse, DuplicateNameExceptionResponse, ValidationExceptionResponse;

    @WebResult(name = "SetPropertiesResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "setPropertiesReturn")
    @WebMethod
    public SetPropertiesResult setProperties(
        @WebParam(partName = "parms", name = "SetProperties", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        SetProperties parms
    ) throws SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse, ValidationExceptionResponse;

    @WebResult(name = "RemoveReadersResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "removeReadersReturn")
    @WebMethod
    public RemoveReadersResult removeReaders(
        @WebParam(partName = "parms", name = "RemoveReaders", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        RemoveReaders parms
    ) throws NonCompositeReaderExceptionResponse, SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse;

    @WebResult(name = "AddReadersResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "addReadersReturn")
    @WebMethod
    public AddReadersResult addReaders(
        @WebParam(partName = "parms", name = "AddReaders", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        AddReaders parms
    ) throws ReaderLoopExceptionResponse, NonCompositeReaderExceptionResponse, SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse, ValidationExceptionResponse;

    @WebResult(name = "UpdateResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "updateReturn")
    @WebMethod
    public UpdateResult update(
        @WebParam(partName = "parms", name = "Update", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        Update parms
    ) throws ReaderLoopExceptionResponse, SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse, ValidationExceptionResponse;

    @WebResult(name = "SetReadersResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "setReadersReturn")
    @WebMethod
    public SetReadersResult setReaders(
        @WebParam(partName = "parms", name = "SetReaders", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        SetReaders parms
    ) throws ReaderLoopExceptionResponse, NonCompositeReaderExceptionResponse, SecurityExceptionResponse, InUseExceptionResponse, ImplementationExceptionResponse, ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse, ValidationExceptionResponse;

    @WebResult(name = "GetVendorVersionResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "getVendorVersionReturn")
    @WebMethod
    public java.lang.String getVendorVersion(
        @WebParam(partName = "parms", name = "GetVendorVersion", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        EmptyParms parms
    ) throws ImplementationExceptionResponse;

    @WebResult(name = "GetLogicalReaderNamesResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "getLogicalReaderNamesReturn")
    @WebMethod
    public ArrayOfString getLogicalReaderNames(
        @WebParam(partName = "parms", name = "GetLogicalReaderNames", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        EmptyParms parms
    ) throws SecurityExceptionResponse, ImplementationExceptionResponse;

    @WebResult(name = "GetPropertyValueResult", targetNamespace = "urn:epcglobal:alelr:wsdl:1", partName = "getPropertyValueReturn")
    @WebMethod
    public java.lang.String getPropertyValue(
        @WebParam(partName = "parms", name = "GetPropertyValue", targetNamespace = "urn:epcglobal:alelr:wsdl:1")
        GetPropertyValue parms
    ) throws SecurityExceptionResponse, ImplementationExceptionResponse, NoSuchNameExceptionResponse;
}
