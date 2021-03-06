/*
 * 
 * ECSPECManagerService.java
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
package org.rifidi.edge.epcglobal.aleread.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiBoundarySpec;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

/**
 * This service manages the lifecycle of all ECSPECs. TODO: remove dependencies
 * to generated code!
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ECSPECManagerService {
	/**
	 * Get the names of currently available specs.
	 */
	public Set<String> getNames();

	/**
	 * Subscribe to a spec.
	 * 
	 * @param specName
	 * @param uri
	 *            target for the reports
	 * @throws NoSuchNameExceptionResponse
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void subscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			DuplicateSubscriptionExceptionResponse;

	/**
	 * Unsubscribe from a spec.
	 * 
	 * @param specName
	 * @param uri
	 *            target for the reports
	 * @throws NoSuchNameExceptionResponse
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void unsubscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			NoSuchSubscriberExceptionResponse;

	/**
	 * Get a list containing all currently subscribed URIs.
	 * 
	 * @param specName
	 * @return
	 * @throws NoSuchSubscriberExceptionResponse
	 */
	public List<String> getSubscriptions(String specName)
			throws NoSuchNameExceptionResponse;
	
	public List<String> getSubscriptionsByLRName(String readerName);

	/**
	 * Create a new spec with the given name.
	 * 
	 * @param name
	 * @param spec
	 * @param rifidiBoundarySpec
	 * @param readers
	 * @param primarykeys
	 * @param reports
	 * @throws DuplicateNameExceptionResponse
	 * @throws ECSpecValidationExceptionResponse
	 */
	public void createSpec(String name, ECSpec spec,
			RifidiBoundarySpec rifidiBoundarySpec, Collection<String> readers,
			Collection<String> primarykeys, Collection<RifidiReport> reports)
			throws DuplicateNameExceptionResponse,
			ECSpecValidationExceptionResponse;

	/**
	 * Destroy an ECSpec.
	 * 
	 * @param name
	 * @throws NoSuchNameExceptionResponse
	 */
	public void destroySpec(String name) throws NoSuchNameExceptionResponse;

	/**
	 * Get a spec by its name.
	 * 
	 * @param name
	 * @return
	 */
	public ECSpec getSpecByName(String name) throws NoSuchNameExceptionResponse;
}
