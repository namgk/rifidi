/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.ale.server;

import java.util.ArrayList;
import java.util.List;

import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationException;
import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationException;
import org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse;

/**
 * This class represents an tag, filter or group pattern.
 * 
 * @author regli, limg00n (2009. 02. 04)
 */
public class PatternEPC implements Pattern {

	/** content of the first field */
	private static final String FIRST_FIELD = "urn";
	/** content of the second field */
	private static final String SECOND_FIELD = "epc";
	/** possible contents of the third field */
	private static final String[] THIRD_FIELDS = new String[] {"urn", "tag", "pat", "id", "idpat", "raw"};
	
	/** how this pattern is used (tag, filter or group) */
	private final PatternUsage usage;
	/** conent of third field */
	private final String thirdField;
	/** type of this pattern */
	private final PatternType type;
	
	/** data fields of this pattern */
	private final List<PatternDataField> dataFields = new ArrayList<PatternDataField>();

	/**
	 * Contructor sets the usage and parses the pattern.
	 * 
	 * @param pattern to parse
	 * @param usage of this pattern
	 * @throws ECSpecValidationException if the pattern is invalid
	 */
	public PatternEPC(String pattern, PatternUsage usage) throws ECSpecValidationExceptionResponse {
		// set fields
		this.usage = usage;
		
		StringBuffer thirdFieldString = new StringBuffer();
		thirdFieldString.append("(");
		for (int i = 0; i < THIRD_FIELDS.length; i++) {
			if (i > 0) thirdFieldString.append(" | ");
			thirdFieldString.append(THIRD_FIELDS[i]);
		}
		thirdFieldString.append(")");
		
		// split pattern and check first fields
		String[] parts = pattern.split(":");
		if (parts.length != 5) {
			throw new ECSpecValidationExceptionResponse("Invalid Pattern '" + pattern + "'." +
					" Pattern must have the form '" + FIRST_FIELD + ":" + SECOND_FIELD + ":" + thirdFieldString + ":tag-type:data-fields'.");
		} else {
			thirdField = parts[2];
			if (!FIRST_FIELD.equals(parts[0]) || !SECOND_FIELD.equals(parts[1]) || !containsString(THIRD_FIELDS, thirdField)) {
				throw new ECSpecValidationExceptionResponse("Invalid Pattern '" + pattern + "'." +
						" Pattern must start with '" + FIRST_FIELD + ":" + SECOND_FIELD + ":" + thirdFieldString + "'.");
			} else {
				
				// get pattern type
				type = PatternType.getType(parts[3]);
				
				// parse data fields
				parseDataFields(parts[4], pattern);
				
			}
		}
	}
	/**
	 * This method returns the type of this pattern.
	 * 
	 * @return type of pattern
	 */
	public PatternType getType() {
		
		return type;
		
	}
	
	/**
	 * This method returns the filter of this pattern.
	 * 
	 * @return filter of pattern
	 */
	public PatternDataField getFilter() {
		
		return dataFields.get(0);
		
	}
	
	/**
	 * This method returns the company of this pattern.
	 * 
	 * @return company of pattern
	 */
	public PatternDataField getCompany() {
		
		return dataFields.get(1);
		
	}
	
	/**
	 * This method returns the item of this pattern.
	 * 
	 * @return item of pattern
	 */
	public PatternDataField getItem() {
		
		return dataFields.get(2);
		
	}
	
	/**
	 * This method returns the serial of this patern.
	 * 
	 * @return serial of pattern
	 */
	public PatternDataField getSerial() {
		
		return dataFields.get(3);
		
	}
	
	/**
	 * This method returns the data fields of this pattern.
	 * 
	 * @return list of data fields
	 */
	public List<PatternDataField> getDataFields() {
		
		return dataFields;
		
	}
	
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @param pattern to check disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ECSpecValidationException if the pattern is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public boolean isDisjoint(String pattern) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		
		return isDisjoint(new PatternEPC(pattern, PatternUsage.GROUP));
		
	}
	
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @author limg00n 
	 * @param pattern to chek disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ImplementationException if an implementation exception occures
	 */
	public boolean isDisjoint(Pattern pattern) throws ImplementationExceptionResponse {
		if(pattern instanceof PatternEPC) {
			PatternEPC patternEPC = (PatternEPC)pattern;
			if (!type.equals(patternEPC.getType())) {
				
				// if types are different, then the patterns are disjoint
				return true;
			} else {
				
				// if some corresponding data fields are disjoint, then the patterns are disjoint 
				for (int i = 0; i < dataFields.size(); i++) {
					if (dataFields.get(i).isDisjoint(patternEPC.getDataFields().get(i))) {
						return true;
					}
				}
				return false;
			}
			
		} else {
			throw new ImplementationExceptionResponse("The two grouping patterns '" + this +
			"' and '" + pattern + "' are not the same type.");
		}
	}
	
	/**
	 * This method indicates if a tag is member of this filter or group pattern.
	 * If the pattern is a tag pattern, the return value is null.
	 * 
	 * @param tagURI to check for member
	 * @return true if tag is member of this pattern and false otherwise
	 * @throws ECSpecValidationException if the tag pattern is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public boolean isMember(String tagURI) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		
		if (usage == PatternUsage.TAG) {
			return false;
		}
		
		// create pattern of usage TAG ('*' and 'X' are not allowed)
		PatternEPC tag = new PatternEPC(tagURI, PatternUsage.TAG);
			
		// check type
		if (tag.getType().equals(getType())) {
				
			// get data fields
			List<PatternDataField> tagDataFields = tag.getDataFields();
				
			// check number of data fields
			if (tagDataFields.size() == dataFields.size()) {
				
				// check contents of data fields
				for (int i = 0; i < dataFields.size(); i++) {
					if (!dataFields.get(i).isMember(tagDataFields.get(i).getValue())) {
						return false;
					}
				}
				return true;
					
			}
		}
		
		return false;
		
	}

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
	public String getGroupName(String tagURI) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse {
		
		if (usage != PatternUsage.GROUP || !isMember(tagURI)) {
			return null;
		}
		
		try {
			
			// create pattern of usage TAG ('*', 'X' and ranges are not allowed)
			PatternEPC tag = new PatternEPC(tagURI, PatternUsage.TAG);
			
			// clone pattern to create a group name
			PatternEPC groupName = new PatternEPC(this.toString(), PatternUsage.GROUP);
			
			// get data fields
			List<PatternDataField> tagDataFields = tag.getDataFields();
			List<PatternDataField> groupNameDataFields = groupName.getDataFields();
			
			// replace 'X' in group name
			for (int i = 0; i < groupNameDataFields.size(); i++) {
				if (groupNameDataFields.get(i).isX()) {
					groupNameDataFields.set(i, tagDataFields.get(i));
				}
			}
			
			return groupName.toString();
			
		} catch (ECSpecValidationExceptionResponse e) {
			throw new ImplementationExceptionResponse(e.getMessage());
		}
		
	}
	/**
	 * compare equality for PatternEPC 
	 * @param p PattenEPC to compare
	 * @return
	 */
	public boolean equals(PatternEPC p) {
		if(p.toString().equals(this.toString())) {
			return true;
		}
		return false;
	}
	/**
	 * This method returns a string representation of this pattern.
	 * 
	 * @return string representation
	 */
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(FIRST_FIELD);
		buffer.append(":");
		buffer.append(SECOND_FIELD);
		buffer.append(":");
		buffer.append(thirdField);
		buffer.append(":");
		buffer.append(type.toSring());
		buffer.append(":");
		
		for (PatternDataField dataField : dataFields) {
			buffer.append(dataField.toString());
			buffer.append(".");
		}
		
		return buffer.substring(0, buffer.length() - 1);
		
	}
	
	//
	// private methods
	//
	
	/**
	 * This method parses the data fields of this pattern.
	 * 
	 * @param dataFieldsString to parse
	 * @param pattern the whole pattern
	 * @throws ECSpecValidationException if the data field string is invalid
	 */
	private void parseDataFields(String dataFieldsString, String pattern) throws ECSpecValidationExceptionResponse {
		
		// split data fields
		String[] dataFieldsStringArray = dataFieldsString.split("\\.");
		
		// check number of data fields
		int nbrOfDataFields = type.getNumberOfDatafields();
		if (dataFieldsStringArray.length < nbrOfDataFields) {
			throw new ECSpecValidationExceptionResponse("Too less data fields '" + dataFieldsString + "' in pattern '" +
					pattern +"'. Pattern Format '" + type + "' expects " + nbrOfDataFields + " data fields.");
		} else if (dataFieldsStringArray.length > nbrOfDataFields) {
			throw new ECSpecValidationExceptionResponse("Too many data fields '" + dataFieldsString + "' in pattern '" +
					pattern + "'. Pattern Format '" + type + "' expects " + nbrOfDataFields + " data fields.");
		}
		
		// check format of each field
		for (int i = 0; i < dataFieldsStringArray.length; i++) {
			dataFields.add(new PatternDataField(dataFieldsStringArray[i], usage));
		}
		
	}
	
	/**
	 * This method indicates, if the needle string is an element of the haystack string array.
	 * 
	 * @param haystack string array which possibly contains the needle string
	 * @param needle string to search in the haystack string array
	 * @return true if the haystack contains the needle and false otherwise
	 */
	private boolean containsString(String[] haystack, String needle) {
		
		if (needle == null) {
			for (String element : haystack) {
				if (element == null) {
					return true;
				}
			}
			return false;
		} else {
			boolean found = false;
			for (String element : haystack) {
				if (needle.equals(element)) {
					found = true;
				}
			}
			return found;
		}
		
	}

}