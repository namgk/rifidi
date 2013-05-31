package org.fosstrak.ale.server;

import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationException;
import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationException;
import org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse;
/**
 * Interface for pattern
 * existing Pattern class changed to PatternEPC class
 * @author limg00n on 2009. 02. 04
 *
 */
public interface Pattern {
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @param pattern to check disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ECSpecValidationException if the pattern is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public boolean isDisjoint(String pattern) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse;
	
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @param pattern to chek disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ImplementationException if an implementation exception occures
	 */
	public boolean isDisjoint(Pattern pattern) throws ImplementationExceptionResponse;
	
	/**
	 * This method indicates if a tag is member of this filter or group pattern.
	 * If the pattern is a tag pattern, the return value is null.
	 * 
	 * @param tagURI to check for member
	 * @return true if tag is member of this pattern and false otherwise
	 * @throws ECSpecValidationException if the tag pattern is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public boolean isMember(String tagURI) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse;

	/**
	 * This method returns the group name for a tag depending on this group pattern.
	 * If the pattern is not a group pattern or the tag is not a member of this group pattern,
	 * the return value is null. 
	 * 
	 * @param tagURI to get the group name from
	 * @return group name
	 * @throws ImplementationException if an implementation exception occurs
	 * @throws ECSpecValidationException if the tag pattern is invalid
	 */
	public String getGroupName(String tagURI) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse; 

	/**
	 * This method returns a string representation of this pattern.
	 * 
	 * @return string representation
	 */
	public String toString();

}
