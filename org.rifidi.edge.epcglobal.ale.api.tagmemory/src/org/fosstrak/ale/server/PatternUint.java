package org.fosstrak.ale.server;


import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse;

public class PatternUint implements Pattern {
	private final PatternUsage usage;
	private PatternDataField dataField;
	private final boolean isHexFormat;
	public PatternUint(String pattern, PatternUsage filter) throws ECSpecValidationExceptionResponse {
		this.usage = filter;
		/*
		if(pattern.charAt(0) == 'x') {
			isHexFormat = true;
			String hexStr = pattern.substring(1);
			String decStr = HexUtil.hexStringToDecimalString(hexStr);
			this.dataField = new PatternDataField(decStr, filter);
		} else {
			isHexFormat = false;
			this.dataField = new PatternDataField(pattern, filter);
		}
		*/
		isHexFormat = false;
		this.dataField = new PatternDataField(pattern, filter);
		
	}

	public String getGroupName(String tagURI)
			throws ImplementationExceptionResponse,
			ECSpecValidationExceptionResponse {
		
		if (usage != PatternUsage.GROUP || !isMember(tagURI)) {
			return null;
		}
		
		try {
			
			// create pattern of usage TAG ('*', 'X' and ranges are not allowed)
			PatternUint tag = new PatternUint(tagURI, PatternUsage.TAG);
			
			// clone pattern to create a group name
			PatternUint groupName = new PatternUint(this.toString(), PatternUsage.GROUP);
			
			// get data fields
			PatternDataField tagDataField = tag.getDataField();
			PatternDataField groupNameDataField = groupName.getDataField();
			
			// replace 'X' in group name
			if (groupNameDataField.isX()) {
				groupName.dataField = tagDataField;
			}

			
			return groupName.toString();
			
		} catch (ECSpecValidationExceptionResponse e) {
			throw new ImplementationExceptionResponse(e.getMessage());
		}
	}

	public boolean isDisjoint(String pattern)
			throws ECSpecValidationExceptionResponse,
			ImplementationExceptionResponse {
		return isDisjoint(new PatternUint(pattern, this.usage));
	}

	public boolean isDisjoint(Pattern pattern)
			throws ImplementationExceptionResponse {
		if(pattern instanceof PatternUint) {
			PatternUint uintPattern = (PatternUint) pattern; 
			dataField.isDisjoint(uintPattern.getDataField());			
		} else {
			throw new ImplementationExceptionResponse("The two grouping patterns '" + this +
			"' and '" + pattern + "' are not the same type.");
		}
		return false;
	}

	public boolean isMember(String value)
			throws ECSpecValidationExceptionResponse,
			ImplementationExceptionResponse {
		
		if (usage == PatternUsage.TAG) {
			return false;
		}
		
		// create pattern of usage TAG ('*' and 'X' are not allowed)
		PatternUint val = new PatternUint(value, PatternUsage.TAG);
			
		// get data fields
		PatternDataField tagDataField = val.getDataField();
			
		// check contents of data fields
		if (!dataField.isMember(tagDataField.getValue())) {
			return false;
		}
		return true;
	}

	public String toString() {
		return dataField.toString();
	}

	public PatternDataField getDataField() {
		return dataField;
	}
}
