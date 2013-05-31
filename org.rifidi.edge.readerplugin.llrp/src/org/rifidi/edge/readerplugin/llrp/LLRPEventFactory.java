/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldListSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMSpec;
import org.fosstrak.tdt.TDTEngine;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.interfaces.AirProtocolTagData;
import org.llrp.ltk.generated.messages.READER_EVENT_NOTIFICATION;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.parameters.AntennaID;
import org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2_CRC;
import org.llrp.ltk.generated.parameters.C1G2_PC;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.GPIEvent;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.StandardTagReadEventFieldNames;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.tagmemory.TMSpecManager;

/**
 * This factory creates events that can be added to the Esper runtime from LLRP
 * messages
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class LLRPEventFactory {
	
	/* file for seonghoon's request
	private static File sensorlogFile;
	private static FileWriter txt;
	private static PrintWriter out; 
	*/
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(LLRPEventFactory.class);

	/**
	 * Create an event that can be added to the esper runtime.
	 * 
	 * @param message
	 *            The message from the LLRP reader
	 * @param readerID
	 *            The ID of the reader that recieved the message
	 * @return the Rifidi Event or null if no relavent can be created
	 */
	public static Object createEvent(LLRPMessage message, String readerID) {
		// If we have A RO_ACCESS_REPORT, return a ReadCycle
		if (message instanceof RO_ACCESS_REPORT) {
			return createReadCycle((RO_ACCESS_REPORT) message, readerID);
		}
		// If we have a GPIEvent Notification, return a GPIEvent
		if (message instanceof READER_EVENT_NOTIFICATION) {
			READER_EVENT_NOTIFICATION notification = (READER_EVENT_NOTIFICATION) message;
			GPIEvent gpiEvent = notification.getReaderEventNotificationData()
					.getGPIEvent();
			if (gpiEvent != null) {
				return createGPIEvent(gpiEvent, readerID);
			}
		}
		return null;
	}

	/**
	 * This method parses a RO_ACCESS_REPORT into a ReadCycle
	 * 
	 * @param rar
	 *            The RO_ACCESS_REPORT to parse
	 * @param readerID
	 *            The reader ID of the reader that got the report
	 * @return
	 */
	private static ReadCycle createReadCycle(RO_ACCESS_REPORT rar,
			String readerID) {
		List<TagReportData> trdl = rar.getTagReportDataList();
		Set<TagReadEvent> tagreaderevents = new HashSet<TagReadEvent>();

		for (TagReportData t : trdl) {
			AntennaID antid = t.getAntennaID();
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			if (t.getEPCParameter() instanceof EPCData) {
				EPCData id = (EPCData) t.getEPCParameter();
				String EPCData = id.getEPC().toString(16);
				gen2event.setEPCMemory(parseString(EPCData),
						EPCData.length() * 4);

			} else {
				EPC_96 id = (EPC_96) t.getEPCParameter();
				String EPCData = id.getEPC().toString(16);
				gen2event.setEPCMemory(parseString(EPCData), 96);
			}
	
			//TagReadEvent tag = new TagReadEvent(readerID, gen2event, antid
			//		.getAntennaID().intValue(), System.currentTimeMillis());
			
			TagReadEvent tag = new TagReadEvent(readerID, gen2event, antid
					.getAntennaID().intValue(), System.currentTimeMillis());
			// Add the custom information to the tags.
			if (t.getROSpecID() != null) {
				String rosid = t.getROSpecID().getROSpecID().toInteger()
						.toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.ROSPEC_ID,
						rosid);
			}
			if (t.getPeakRSSI() != null) {
				String rssi = t.getPeakRSSI().getPeakRSSI().toInteger()
						.toString();
				tag.addExtraInformation(StandardTagReadEventFieldNames.RSSI,
						rssi);
			}

			if (t.getSpecIndex() != null) {
				String specindex = t.getSpecIndex().getSpecIndex().toInteger()
						.toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.SPEC_INDEX,
						specindex);
			}
			if (t.getInventoryParameterSpecID() != null) {
				String invparamspecid = t.getInventoryParameterSpecID()
						.getInventoryParameterSpecID().toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.INVPARAMSPECID,
						invparamspecid);
			}

			if (t.getChannelIndex() != null) {
				String channelindex = t.getChannelIndex().getChannelIndex()
						.toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.CHANNELINDEX, channelindex);
			}

			if (t.getFirstSeenTimestampUTC() != null) {
				String firstseenutc = t.getFirstSeenTimestampUTC()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.FIRSTSEENUTC, firstseenutc);
			}

			if (t.getFirstSeenTimestampUptime() != null) {
				String firstseenuptime = t.getFirstSeenTimestampUptime()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.FIRSTSEENUPTIME,
						firstseenuptime);
			}

			if (t.getLastSeenTimestampUTC() != null) {
				String lastseenutc = t.getLastSeenTimestampUTC()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(LLRPTagReadEventFieldNames.LASTSEENUTC,
						lastseenutc);
			}

			if (t.getLastSeenTimestampUptime() != null) {
				String lastseenuptime = t.getLastSeenTimestampUptime()
						.getMicroseconds().toBigInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.LASTSEENUPTIME,
						lastseenuptime);
			}

			if (t.getTagSeenCount() != null) {
				String tagseencount = t.getTagSeenCount().getTagCount()
						.toInteger().toString();
				tag.addExtraInformation(
						LLRPTagReadEventFieldNames.TAGSEENCOUNT, tagseencount);
			}

			for (AirProtocolTagData aptd : t.getAirProtocolTagDataList()) {
				if (aptd instanceof C1G2_CRC) {
					String crc = ((C1G2_CRC) aptd).getCRC().toInteger()
							.toString();
					tag.addExtraInformation(
							LLRPTagReadEventFieldNames.AIRPROT_CRC, crc);
				} else if (aptd instanceof C1G2_PC) {
					String pc = ((C1G2_PC) aptd).getPC_Bits().toInteger()
							.toString();
					tag.addExtraInformation(
							LLRPTagReadEventFieldNames.AIRPROT_PC, pc);
				}
			}
			// inserted by limg00n : extract userdata memory contents from C1G2ReadOpSpecResult
			for (AccessCommandOpSpecResult acosr : t.getAccessCommandOpSpecResultList()) {
				if(acosr instanceof C1G2ReadOpSpecResult) {
					C1G2ReadOpSpecResult c1g2OpspecResult = (C1G2ReadOpSpecResult)acosr;
					//System.out.println(c1g2OpspecResult.getResult());
					//System.out.println(c1g2OpspecResult.getOpSpecID());
					//System.out.println(c1g2OpspecResult.getReadData());
					//System.out.println(c1g2OpspecResult.getReadData().encodeBinary());
					//System.out.println(c1g2OpspecResult.getReadData().encodeBinary().toByteArray());
					
					/*
					byte[] result = c1g2OpspecResult.getReadData().encodeBinary().toByteArray();
					
					// first 16 bits represent length of user data memory
					// rest bits represent contents of user data memory
					byte[] usermemory = new byte[result.length - 2];
					for(int i=0 ; i<result.length-2 ; i++) {
						usermemory[i] = result[2+i];
					}
					*/
					byte[] result = c1g2OpspecResult.getReadData().encodeBinary().toByteArray();
					
					byte[] usermemory = new byte[result.length];
					for(int i=0 ; i<result.length ; i++) {
						usermemory[i] = result[i];
					}
					
					EPC_96 id = (EPC_96) t.getEPCParameter();
					String EPCData = id.getEPC().toString(16);

					// convert hex epc to tag epc via TDTEngine
					TDTEngine tdt = null;
					try {
						tdt = new TDTEngine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JAXBException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map<String, String> tdtParams = new HashMap<String, String>();
					tdtParams.put("taglength", "96");
					tdtParams.put("filter", "3");
					
					String epc_binary = tdt.hex2bin(EPCData); 
					while(epc_binary.length() < 96) {
						epc_binary = "0"+epc_binary;
					}
					String epc_tag = tdt.convert(epc_binary, tdtParams, LevelTypeList.TAG_ENCODING);
					//System.out.println(epc_tag);
					
					/*
					String tmspecname = null;
					try {
						tmspecname = TMSpecManager.getTMSpecNameByEPCPattern(epc_tag);
					} catch (ECSpecValidationExceptionResponse e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (org.fosstrak.ale.wsdl.ale.epcglobal.ImplementationExceptionResponse e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println(tmspecname);
					*/
					TMSpec tmspec = null;
					try {
						tmspec = TMSpecManager.getTMSpecByEPC(epc_tag);

					} catch(Exception e) {
						e.printStackTrace();
					}
					Date now = new Date();
					StringBuffer logMsg = new StringBuffer(now+" \t"+now.getTime()+"\t"+epc_tag+" \t");
					
					if(tmspec instanceof TMFixedFieldListSpec) {
						TMFixedFieldListSpec tmffls = (TMFixedFieldListSpec)tmspec;
						
						for(TMFixedFieldSpec tmff : tmffls.getFixedFields().getFixedField()) {
							String fieldname = tmff.getFieldname();
							try {
								String fieldvalue = TMSpecManager.extractFieldValue(epc_tag, usermemory, fieldname);
								
								tag.addExtraInformation(fieldname, fieldvalue);
								
								//System.out.println("fieldname:"+fieldname+"/"+"fieldvalue:"+fieldvalue+"\t");
								logMsg.append("\t"+fieldvalue);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		
							
						}
					}
					/*
					try {
						if(sensorlogFile == null) sensorlogFile = new File("d:\\sensor.log");
						if(txt == null) txt = new FileWriter(sensorlogFile);
						if(out == null) out = new PrintWriter(txt);
						out.println(logMsg);
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//logger.info(logMsg);
					*/
				}
			}
			if(logger.isDebugEnabled()){
				logger.debug(tag.getTag().getFormattedID() + " ANT: " + tag.getAntennaID());
			}

			// for (String key : tag.getExtraInformation().keySet()) {
			// System.out.println(key + ", "
			// + tag.getExtraInformation().get(key));
			// }
			
			tagreaderevents.add(tag);
			
		}
		return new ReadCycle(tagreaderevents, readerID, System
				.currentTimeMillis());
	}

	/**
	 * A helper method that takes in a hex string and returns a BigInteger
	 * 
	 * @param input
	 * @return
	 */
	private static BigInteger parseString(String input) {
		BigInteger retVal = null;

		try {
			if(input.length() % 2 != 0) input = "0"+input;
			input = input.trim();
			retVal = new BigInteger(Hex.decodeHex(input.toCharArray()));
		} catch (Exception e) {
			logger.warn("There was a problem when parsing LLRP Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}

	/**
	 * This method parses a GPIEvent from LLRP and returns a Rifidi GPI event
	 * that can be added to esper
	 * 
	 * @param llrpGPIEvent
	 * @param readerID
	 * @return
	 */
	private static org.rifidi.edge.core.services.notification.data.gpio.GPIEvent createGPIEvent(
			GPIEvent llrpGPIEvent, String readerID) {
		int port = llrpGPIEvent.getGPIPortNumber().toInteger();
		boolean state = llrpGPIEvent.getGPIEvent().toBoolean();
		return new org.rifidi.edge.core.services.notification.data.gpio.GPIEvent(
				readerID, port, state);
	}
}
