/**
 * 
 */
package org.rifidi.app.temperature;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;
import org.rifidi.edge.epcglobal.ale.api.read.EPC;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportMemberField;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ObjectFactory;
import org.rifidi.edge.epcglobal.aleread.EPCDataContainerAdapter;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.wrappers.ReportAnswer;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;


/**
 * This is an app that developers can use as a template when getting started
 * with a new application.
 */
public class TemperatureApp extends AbstractRifidiApp {

	private float indoorLowerBoundTemp = 0;
	private float indoorUpperBoundTemp = 0;	
	
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(TemperatureApp.class);
	
	/** JAXB for serializing the results. */
	private JAXBContext cont;
	private Marshaller marsh;
	
	private static final ObjectFactory objectFactoryALE = new ObjectFactory();
	
	//list of readers implemented by ArrayList
	List<String> readerList = new ArrayList<String>();
	
	public TemperatureApp() {
		// 'IoT6' is the group name. 'TemperatureApp' is the app name
		super("IoT6", "TemperatureApp");
		try {
			cont = JAXBContext.newInstance(ReportAnswer.class, ECReports.class);
			marsh = cont.createMarshaller();
		} catch (JAXBException e) {
			logger.fatal("Unabel to create JAXB marshaller: " + e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();
		
		/* These statements register the events we will use */
		addEventType(IoTSensedEvent.class);
		
		// create a window that holds unique events
		//addStatement("create window sensedTemperatureWindow.win:time(60 sec) " +
		//		"as TagReadEvent");
		addStatement("create window sensedTemperatureWindow.win:time(60 sec) " +
				"as IoTSensedEvent");		

		// listen to events that are added to that window. See the esper
		// documentation for more about esper
		StatementAwareUpdateListener sensedTemperatureEventListner = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					IoTSensedEvent rawIoTEvent = (IoTSensedEvent) arg0[0].getUnderlying();
					System.out.println( "[STISApp: Listener] send all keys and values to STIS");
															
					ECReports reports = new ECReports();
					reports.setReports(new ECReports.Reports());
					reports.setALEID("Rifidi Edge Server");
					//Set<DatacontainerEvent> events = new HashSet<DatacontainerEvent>();
					//events.add(IoTSensedEvent.getTag());
					
					// create event cycle report 
					ECReport report = new ECReport();
					report.setReportName("IoT6");
					// create the default group
					ECReportGroup group = new ECReportGroup();
					
					// create the ECReport group list
					ECReportGroupList ecReportGroupList = new ECReportGroupList();
					group.setGroupList(ecReportGroupList);
					
					ECReportGroupListMember member = new ECReportGroupListMember();
					EPCDataContainerAdapter adapter = new EPCDataContainerAdapter();
					EPC epc = new EPC();
					
					try {
						TDTEngine tdt = new TDTEngine();
						Map<String, String> params = new HashMap<String, String>();
						params.put("taglength", "96");
						params.put("filter", "3");
						params.put("companyprefixlength", "7");
						String binString = tdt.hex2bin(rawIoTEvent.getTag().toString());
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
						epc.setValue(rawIoTEvent.getTag().toString());
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						epc.setValue(rawIoTEvent.getTag().toString());
					}
					
					member.setEpc(epc);
					/*
					// crreate the ECReport group list Member 
					ECReportGroupListMember member = new ECReportGroupListMember();
					EPCDataContainerAdapter adapter = new EPCDataContainerAdapter();
					EPC epc = new EPC();
					epc.setValue(adapter.getEpc(rawIoTEvent.getTag(), ALEDataTypes.EPC, ALEDataFormats.EPC_DECIMAL));
					//member.setRawDecimal(epc);
					member.setEpc(epc);
					*/
					ECFieldSpec ecfSpec = new ECFieldSpec();
					ecfSpec.setDatatype("epc");
					ecfSpec.setFieldname("epc");
					ecfSpec.setFormat("epc-tag");
					ReportALEField field = new ReportALEField("temperature", ecfSpec);
					
					// create the extension group
					ECReportGroupListMemberExtension ext = new ECReportGroupListMemberExtension();
					ext.setFieldList(new ECReportGroupListMemberExtension.FieldList());
					
					Iterator iterator = rawIoTEvent.getExtraInformation().keySet().iterator();
					while(iterator.hasNext()) {
						String eventName = iterator.next().toString(); 
						//System.out.println( "[IoT Event] " + eventName + " = " + rawIoTEvent.getExtraInformation().get(eventName));
						
						ECReportMemberField ecrepfield = new ECReportMemberField();
						ecrepfield.setFieldspec(field.getFieldSpec());
						ecrepfield.setName(eventName);
						ecrepfield.setValue(rawIoTEvent.getExtraInformation().get(eventName).toString());
						ext.getFieldList().getField().add(ecrepfield);
					}
					member.setExtension(ext);					
					group.getGroupList().getMember().add(member);
					report.getGroup().add(group);
					reports.getReports().getReport().add(report);
					
					logger.info("Sending report to EPCIS");				
					try {
						Socket socket = new Socket("127.0.0.1", Integer.parseInt("9999"));
						//Socket socket = new Socket("143.248.106.136", Integer.parseInt("9999"));
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

						try {
							ReportAnswer answer = new ReportAnswer();
							answer.reports = reports;
							marsh.marshal(objectFactoryALE.createECReports(reports), out);
						} catch (JAXBException e) {
							logger.info("Problem serializing to xml: "+e);
						}
						out.flush();
						out.close();
						socket.close();
					} catch (NumberFormatException e) {
						logger.info(e);
					} catch (UnknownHostException e) {
						logger.info(e);
					} catch (IOException e) {
						logger.warn(e);
					}
					
				}

			}
		};
		// insert tags into the window
		//addStatement("insert into sensedTemperatureWindow select * " +
		//		"from ReadCycle[select * from tags where antennaID=0]");
		
		// insert tags into the window
		addStatement("insert into sensedTemperatureWindow select * " +
				"from IoTSensedEvent where sensingDevice='Alien_1'");	
		addStatement("insert into sensedTemperatureWindow select * " +
		"from IoTSensedEvent where sensingDevice='Obix_1'");	
		
		//register the listener for interesting readers
		Iterator it=readerList.iterator(); 		
		//while(it.hasNext()) {
			//addStatement("select * from sensedTemperatureWindow where readerID = '"+ (String)it.next() + "'",sensedTemperatureEventListner);
			addStatement("select * from sensedTemperatureWindow where sensingDevice='Alien_1'", sensedTemperatureEventListner);			
			addStatement("select * from sensedTemperatureWindow where sensingDevice='OBIX_1'", sensedTemperatureEventListner);
		//}
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
		
		//store the logical reader ID into the list
		readerList.add(getProperty("resl_room_2_1328", null));
		readerList.add(getProperty("resl_room_1_1331", null));
		readerList.add(getProperty("prof_room_1332", null));
		readerList.add(getProperty("server_room_1333", null));
		
		// the upper and lower bound of sensed temperature events
		// if a sensed temperatuere is not within the range, this event is reported to the application
		this.indoorLowerBoundTemp = Float.parseFloat(getProperty("preferredLowerBoundTemp", null));
		this.indoorUpperBoundTemp = Float.parseFloat(getProperty("preferredUpperBoundTemp", null));
	}

}
