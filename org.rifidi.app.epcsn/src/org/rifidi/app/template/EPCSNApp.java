/**
 * 
 */
package org.rifidi.app.template;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.EPC;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportMemberField;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension.FieldList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports.Reports;
import org.rifidi.edge.epcglobal.aleread.ALEDataFormats;
import org.rifidi.edge.epcglobal.aleread.ALEDataTypes;
import org.rifidi.edge.epcglobal.aleread.EPCDataContainerAdapter;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.wrappers.ReportAnswer;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.client.UpdateListener;

/**
 * This is an app that developers can use as a template when getting started
 * with a new application. 
 */
public class EPCSNApp extends AbstractRifidiApp {

	/** JAXB for serializing the results. */
	private JAXBContext cont;
	private Marshaller marsh;
	
	public EPCSNApp() {
		// 'Templates' is the group name. 'TemplateApp' is the app name
		super("EPCSN", "EPCSNApp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();
		
		// subscribe to rifidservices, and create custom esper here.
		//addStatement("select * from TagReadEvent.win:time_batch(15 sec)", new EPCSNListener());
		addStatement("select * from TagReadEvent.win:time_batch(1 sec) where readerID='LLRP_2'", new EPCSNListener());
		
		
		//addStatement("insert into TagReadEventGrouped select distinct * from TagReadEvent.win:time_batch(10 sec)");
		//addStatement("select * from TagReadEventGrouped", new EPCSNListener2());

		
		try {
			cont = JAXBContext.newInstance(ReportAnswer.class, ECReports.class);
			marsh = cont.createMarshaller();
		} catch (JAXBException e) {
			
			throw new RuntimeException(e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();

		// unsubscribe from rifidiservices here

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		// Read in properties here. This method will always be called before the
		// start method.

	}
	
	
	public class EPCSNListener2 implements UpdateListener, StatementAwareUpdateListener {

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1) {
			
			
			for(EventBean eb : arg0) {
				String name = eb.getEventType().getName();
				System.out.print(name+"\t");
				for(String a : eb.getEventType().getPropertyNames()) {
					System.out.print("["+a+"="+eb.get(a)+"] ");					
				}

				
			}
			System.out.println();
			System.out.println();
			/*
			if (arg0 != null) {
				System.out.println( "[EPCSNApp: Listener] send all keys and values to EPCIS");
				
				ECReports reports = new ECReports();
				reports.setReports(new Reports());
				ECReport ecReport = new ECReport();
				ecReport.setReportName("IoT6");
				ecReport.getGroup().add(new ECReportGroup());
				ecReport.getGroup().get(0).setGroupList(new ECReportGroupList());
				List<ECReportGroupListMember> members = ecReport.getGroup().get(0).getGroupList().getMember();
				
				for(EventBean eb : arg0) {
					ECReportGroupListMember member = new ECReportGroupListMember();
					member.setExtension(new ECReportGroupListMemberExtension());
					member.getExtension().setFieldList(new FieldList());
					
					for(String name : eb.getEventType().getPropertyNames()) {
						if(name.equalsIgnoreCase("tag")) {
							EPC epc = new EPC();
							String epcHex = eb.get(name).toString(); 
							try {
								TDTEngine tdt = new TDTEngine();
								Map<String, String> params = new HashMap<String, String>();
								params.put("taglength", "96");
								params.put("filter", "3");
								params.put("companyprefixlength", "7");
								String binString = tdt.hex2bin(epcHex);
								if(binString.length() < 96) {
									for(int i=0;i<96-binString.length()+1;i++) {
										binString = "0"+binString;
									}
								}
								String epcString = tdt.convert(binString, params, LevelTypeList.PURE_IDENTITY);
								epc.setValue(epcString);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								epc.setValue(epcHex);
							} catch (JAXBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								epc.setValue(epcHex);
							}
							member.setEpc(epc);
						} else {
							ECReportMemberField memberFieldTimestamp = new ECReportMemberField();
							memberFieldTimestamp.setName(name);
							memberFieldTimestamp.setValue(eb.get(name).toString());
							member.getExtension().getFieldList().getField().add(memberFieldTimestamp);
							
						}

						members.add(member);
					}
					reports.getReports().getReport().add(ecReport);

				}
				
				reports.getReports().getReport().add(ecReport);
				
				//for(ECReport e : reports.getReports().getReport()) System.out.println(e);						
				
				
				
				try {
					Socket socket = new Socket("143.248.106.136", Integer.parseInt("9999"));
					//Socket socket = new Socket("143.248.106.137", Integer.parseInt("10300"));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

					try {
						ReportAnswer answer = new ReportAnswer();
						answer.reports = reports;
						marsh.marshal(answer, out);
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					out.flush();
					out.close();
					socket.close();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			*/
			
		}

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1,
				EPStatement arg2, EPServiceProvider arg3) {
			update(arg0, arg1);
			
		}
		
	}
	public class EPCSNListener implements UpdateListener, StatementAwareUpdateListener {

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1) {
			if (arg0 != null) {
				System.out.println( "[STISApp: Listener] send all keys and values to STIS");
				
				ECReports reports = new ECReports();
				reports.setReports(new Reports());
				
				
				ECReport ecReport = new ECReport();
				ecReport.setReportName("IoT6");
				ecReport.getGroup().add(new ECReportGroup());
				ecReport.getGroup().get(0).setGroupList(new ECReportGroupList());
				List<ECReportGroupListMember> members = ecReport.getGroup().get(0).getGroupList().getMember();
				for(EventBean eb : arg0) {
					
					TagReadEvent tre = (TagReadEvent)eb.getUnderlying();
					
					ECReportGroupListMember member = new ECReportGroupListMember();
					member.setExtension(new ECReportGroupListMemberExtension());
					member.getExtension().setFieldList(new FieldList());

					EPC epc = new EPC();
					

					
					//EPCDataContainerAdapter adapter = new EPCDataContainerAdapter();
					try {
						TDTEngine tdt = new TDTEngine();
						Map<String, String> params = new HashMap<String, String>();
						params.put("taglength", "96");
						params.put("filter", "3");
						params.put("companyprefixlength", "7");
						String binString = tdt.hex2bin(tre.getTag().toString());
						if(binString.length() < 96) {
							for(int i=0;i<96-binString.length()+1;i++) {
								binString = "0"+binString;
							}
						}
						String epcString = tdt.convert(binString, params, LevelTypeList.PURE_IDENTITY);
						epc.setValue(epcString);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						epc.setValue(tre.getTag().toString());
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						epc.setValue(tre.getTag().toString());
					}
					
					//epc.setValue(adapter.getEpc(tre.getTag(), ALEDataTypes.EPC, ALEDataFormats.EPC_DECIMAL));
					
					
					member.setEpc(epc);
					
					
					ECReportMemberField memberFieldTimestamp = new ECReportMemberField();
					memberFieldTimestamp.setName("timestamp");
					memberFieldTimestamp.setValue(String.valueOf(tre.getTimestamp()));
					member.getExtension().getFieldList().getField().add(memberFieldTimestamp);
					
					Map<String, Serializable> extraInfo = tre.getExtraInformation();
					for(String fieldname : extraInfo.keySet()) {
						ECReportMemberField memberField = new ECReportMemberField();
						memberField.setName(fieldname);
						memberField.setValue((String) extraInfo.get(fieldname));
						member.getExtension().getFieldList().getField().add(memberField);
					}
					/*
					
					String[] propertyNames = eb.getEventType().getPropertyNames();
					for(String propertyName : propertyNames) {
						ECReportMemberField memberField = new ECReportMemberField();
						if(propertyName.equalsIgnoreCase("epc")) {
							EPC epc = new EPC();
							epc.setValue(eb.get(propertyName).toString());
							member.setEpc(epc);
							continue;
						}
						memberField.setName(propertyName);
						memberField.setValue(eb.get(propertyName).toString());
						
						member.getExtension().getFieldList().getField().add(memberField);
					}
					*/
					members.add(member);
				}
				
				reports.getReports().getReport().add(ecReport);
				
				//for(ECReport e : reports.getReports().getReport()) System.out.println(e);						
				
				
				
				try {
					Socket socket = new Socket("143.248.106.136", Integer.parseInt("9999"));
					//Socket socket = new Socket("143.248.106.137", Integer.parseInt("10300"));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					
					try {
						ReportAnswer answer = new ReportAnswer();
						answer.reports = reports;
						
						marsh.marshal(answer, out);
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					out.flush();
					out.close();
					socket.close();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1,
				EPStatement arg2, EPServiceProvider arg3) {
			update(arg0, arg1);
			
		}
		
	}
}
