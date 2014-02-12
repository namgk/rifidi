package kr.ac.kaist.resl.wsn.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.services.notification.data.IoTSensedEvent;
import org.rifidi.edge.epcglobal.ale.api.read.EPC;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroup;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupList;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportGroupListMemberExtension;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportMemberField;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ObjectFactory;
import org.rifidi.edge.epcglobal.aleread.EPCDataContainerAdapter;
import org.rifidi.edge.epcglobal.aleread.service.ECSPECManagerService;
import org.rifidi.edge.epcglobal.aleread.wrappers.ReportAnswer;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class ObixEventListener implements StatementAwareUpdateListener {

	private static final Log logger = LogFactory.getLog(ObixEventListener.class);
	private static final ObjectFactory objectFactoryALE = new ObjectFactory();
	private ECSPECManagerService ecspecMgmt;

	public void setEcspecMgmt(ECSPECManagerService ecspecMgmt) {
		this.ecspecMgmt = ecspecMgmt;
	}

	/** JAXB for serializing the results. */
	private JAXBContext cont;
	private Marshaller marsh;

	public ObixEventListener() {
		try {
			cont = JAXBContext.newInstance(ReportAnswer.class, ECReports.class);
			marsh = cont.createMarshaller();
		} catch (JAXBException e) {
			logger.fatal("Unabel to create JAXB marshaller: " + e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2,
			EPServiceProvider arg3) {
		if ((arg0 == null) || (ecspecMgmt.getSubscriptionsByLRName("Obix_1") == null))
			return;
		IoTSensedEvent rawIoTEvent = (IoTSensedEvent) arg0[0].getUnderlying();
		System.out.println("[STISApp: Listener] send all keys and values to STIS");

		ECReports reports = new ECReports();
		reports.setReports(new ECReports.Reports());
		reports.setALEID("Rifidi Edge Server");

		// create event cycle report
		ECReport report = new ECReport();
		report.setReportName("IoT6");
		// create the default group
		ECReportGroup group = new ECReportGroup();

		// create the ECReport group list
		ECReportGroupList ecReportGroupList = new ECReportGroupList();
		group.setGroupList(ecReportGroupList);

		ECReportGroupListMember member = new ECReportGroupListMember();
		new EPCDataContainerAdapter();
		EPC epc = new EPC();

		try {
			TDTEngine tdt = new TDTEngine();
			Map<String, String> params = new HashMap<String, String>();
			params.put("taglength", "96");
			params.put("filter", "3");
			params.put("companyprefixlength", "7");
			String binString = tdt.hex2bin(rawIoTEvent.getTag().toString());
			if (binString.length() < 96) {
				for (int i = 0; i < 96 - binString.length() + 1; i++) {
					binString = "0" + binString;
				}
			}
			String epcString =
					tdt.convert(binString, params, LevelTypeList.PURE_IDENTITY);
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
		member.setRawHex(epc);

		// create the extension group
		ECReportGroupListMemberExtension ext = new ECReportGroupListMemberExtension();
		ext.setFieldList(new ECReportGroupListMemberExtension.FieldList());

		Iterator iterator = rawIoTEvent.getExtraInformation().keySet().iterator();
		while (iterator.hasNext()) {
			String eventName = iterator.next().toString();
			System.out.println("[IoT Event] " + eventName + " = " + rawIoTEvent.getExtraInformation().get(eventName));

			ECReportMemberField ecrepfield = new ECReportMemberField();
			// ecrepfield.setFieldspec(field.getFieldSpec());
			ecrepfield.setName(eventName);
			ecrepfield.setValue(rawIoTEvent.getExtraInformation().get(eventName).toString());
			ext.getFieldList().getField().add(ecrepfield);
		}
		member.setExtension(ext);
		group.getGroupList().getMember().add(member);
		report.getGroup().add(group);
		reports.getReports().getReport().add(report);

		for (String aSub : ecspecMgmt.getSubscriptionsByLRName("Obix_1")) {
			String host = aSub.split(":")[0];
			String port = aSub.split(":")[1];
			System.out.println("Sending obix report to Cap App: " + host + ":" + port);
			try {
				Socket socket = new Socket(host, Integer.parseInt(port));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				try {
					ReportAnswer answer = new ReportAnswer();
					answer.reports = reports;
					StringWriter sw = new StringWriter();
					marsh.marshal(objectFactoryALE.createECReports(reports), out);
					marsh.marshal(objectFactoryALE.createECReports(reports), sw);
					System.out.println("Sent ECReport via wsn app: \n" + sw.toString());
				} catch (JAXBException e) {
					logger.info("Problem serializing to xml: " + e);
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

}
