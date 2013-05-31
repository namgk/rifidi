package org.rifidi.edge.epcglobal.ale.api.tagmemory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fosstrak.ale.server.PatternEPC;
import org.fosstrak.ale.server.PatternUsage;
import org.fosstrak.ale.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.fosstrak.ale.wsdl.aletm.epcglobal.ImplementationExceptionResponse;
import org.fosstrak.ale.wsdl.aletm.epcglobal.TMSpecValidationExceptionResponse;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldListSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldListSpec.FixedFields;

public class TMSpecManager {
	/** 
	 * map of tag memory specification
	 * this is declared as thread-safe map. (but it still needs to be synchronized when iterating operation)   
	 * @author limg00n on 2009. 1. 21.
	 */	
	private static Map<String, TMSpec> tmspecs = Collections.synchronizedMap(new HashMap<String, TMSpec>());
	
	/**
	 * map of tag memory specification between EPC pattern and TMSpec name
	 * this is declared as thread-safe map. (but it still needs to be synchronized when iterating operation)
	 */
	private static Map<PatternEPC, String> mapEpcAndTMSpecName = Collections.synchronizedMap(new HashMap<PatternEPC, String>());
	/**
	 * defines a tag memory specification with its name
	 * @param specName: name of TMSpec
	 * @param spec: tag memory specification
	 * @author limg00n on 2009. 02. 04.
	 * @throws TMSpecValidationExceptionResponse  
	 * @throws ImplementationExceptionResponse 
	 */
	public synchronized static void defineTMSpec(String specName, TMSpec spec) throws TMSpecValidationExceptionResponse, ImplementationExceptionResponse {
		if(spec instanceof TMFixedFieldListSpec) {
			tmspecs.put(specName, spec);
			TMFixedFieldListSpec specFixed = (TMFixedFieldListSpec)spec;
			try {
				for(PatternEPC epcPattern : mapEpcAndTMSpecName.keySet()) {
	
						try {
							if(!epcPattern.isDisjoint(specFixed.getEpcPattern())) {
								throw new TMSpecValidationExceptionResponse();
							}
						} catch (org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
				}
				mapEpcAndTMSpecName.put(new PatternEPC(specFixed.getEpcPattern(), PatternUsage.FILTER), specName);
			} catch (ECSpecValidationExceptionResponse e) {
				// TODO Auto-generated catch block
				throw new TMSpecValidationExceptionResponse(e.getMessage());
			}
		}

	}
	/**
	 * undefines a tag memory specification by specify the name
	 * @param specName
	 * @author limg00n on 2009. 02. 04.
	 */
	public synchronized static void undefineTMSpec(String specName) {
		TMSpec tmspec = tmspecs.get(specName);
		if(tmspec instanceof TMFixedFieldListSpec) {
			TMFixedFieldListSpec tmFixed = (TMFixedFieldListSpec)tmspec;
			try {
				mapEpcAndTMSpecName.remove(new PatternEPC(tmFixed.getEpcPattern(), PatternUsage.FILTER));
			} catch (ECSpecValidationExceptionResponse e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tmspecs.remove(specName);
	}
	/** 
	 * returns the tag memory specification specified by its name
	 * @param specName
	 * @return
	 * @author limg00n on 2009. 02. 04.
	 */
	public static TMSpec getTMSpec(String specName) {
		return tmspecs.get(specName);
	}
	/**
	 * returns the list of names of tag memory specification registered in this ALE
	 * @return list of names of registered TMSpec
	 * @author limg00n on 2009. 02. 04.
	 */
	public static List<String> getTMSpecNames() {
		List<String> keyList = new ArrayList<String>();
		Set<String> keySet = tmspecs.keySet();
		synchronized(tmspecs) {
			for(String key : keySet) {
				keyList.add(key);
			}
		}
		return keyList;
	}
	public static void clear() {
		tmspecs.clear();
	}
	/**
	 * returns TMSpec by EPC Pattern
	 * @param EPCPattern
	 * @return
	 * @throws TMSpecValidationExceptionResponse 
	 * @throws ImplementationExceptionResponse 
	 */
	public static TMSpec getTMSpecByEPC(String epc) throws ImplementationExceptionResponse {
		try {
			for(PatternEPC epcPattern : mapEpcAndTMSpecName.keySet()) {
				try {
					try {
						if(epcPattern.isMember(epc)) {
							String tmspecName = mapEpcAndTMSpecName.get(epcPattern);
							//System.out.println("TMSpec name:"+tmspecName);
							return tmspecs.get(tmspecName);					
						}
					} catch (org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ECSpecValidationExceptionResponse e) {
					// ECSpecValidationExceptionResponse should be changed to TMSpecValidationExceptionResponse
					throw new TMSpecValidationExceptionResponse();
				}
			}
		} catch(TMSpecValidationExceptionResponse e) {
			return null;
		}
		return null;
	}
	/**
	 * returns TMSpec name by EPC Pattern
	 * @throws ECSpecValidationExceptionResponse 
	 * @throws ECSpecValidationExceptionResponse 
	 * @throws org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse 
	 * @throws ImplementationExceptionResponse 
	 * 
	 */
	public static String getTMSpecNameByEPCPattern(String EPCPattern) throws ECSpecValidationExceptionResponse, org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse, ImplementationExceptionResponse {
		for(PatternEPC pat : mapEpcAndTMSpecName.keySet()) {
			if(pat.isMember(EPCPattern)) {
				return mapEpcAndTMSpecName.get(pat);
			}
		}
		return null;
	}
	public static TMFixedFieldSpec getField(String EPCPattern, String fieldname) throws ImplementationExceptionResponse { 
		TMSpec tmspec = getTMSpecByEPC(EPCPattern);
		//System.out.println("getTMSpecByEPCPattern:"+tmspec+" by epc pattern:"+EPCPattern);
		if(tmspec instanceof TMFixedFieldListSpec) {
			TMFixedFieldListSpec spec = (TMFixedFieldListSpec)tmspec;
			List<TMFixedFieldSpec> fixedFields = spec.getFixedFields().getFixedField();
			for(TMFixedFieldSpec fixedField : fixedFields) {
				if(fixedField.getFieldname().equals(fieldname)) {
					return fixedField;
				}
			}
		}
		throw new ImplementationExceptionResponse("There is no such field");
	}	
	public static String extractFieldValue(String epc, byte[] usermemory, String fieldname) throws Exception {
		TMFixedFieldSpec tmffspec = getField(epc, fieldname);
		
		if(tmffspec.getDefaultDatatype().equalsIgnoreCase("float")) {
			if(tmffspec.getDefaultFormat().equalsIgnoreCase("float")) {
				if(tmffspec.getLength() == 4) {
					/*
					byte[] ofInterest = new byte[tmffspec.getLength()];
					
					for(int i=0;i<tmffspec.getLength();i++) {
						ofInterest[i] = usermemory[tmffspec.getOffset()+i];
					}
					
					byte[] data2 = new byte[tmffspec.getLength()];
					// endian transformation big_endian -> little_endian
					for(int i=0 ; i<tmffspec.getLength() ; i+=4) {
						data2[0] = ofInterest[2];
			            data2[1] = ofInterest[3];
			            data2[2] = ofInterest[0];
			            data2[3] = ofInterest[1];						
					}
		            
					ByteBuffer buf = ByteBuffer.wrap(data2);

					return buf.getFloat()+"";
					*/
		            int i = 0;
		            int len = 4;
		            int cnt = 0;
		            int start = tmffspec.getOffset();
		            byte[] tmp = new byte[len];

		            for (i = start; i < (start + len); i++) {
		                  tmp[cnt] = usermemory[i];
		                  cnt++;
		            }

		            int accum = 0;
		            i = 0;
		            for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
		                  accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
		                  i++;
		            }
		            //System.out.println(Float.intBitsToFloat(accum));
		            
		            return Float.intBitsToFloat(accum)+"";
				}
			}
		}
		
		
		return null;
	}
}
