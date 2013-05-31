package org.rifidi.edge.epcglobal.ale.api.tagmemory;

import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldListSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMSpec;
import org.fosstrak.ale.xsd.ale.epcglobal.TMFixedFieldListSpec.FixedFields;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		TMSpec tmspec = getExampleWarehouseZigbeeNodeTMSpec();
		TMSpecManager.defineTMSpec("WarehouseZigbeeNode", tmspec);
		
		TMSpec tmspec2 = getExampleWarehouseIPUSNNodeTMSpec();
		TMSpecManager.defineTMSpec("WarehouseIPUSNNode", tmspec2);
		
	}

	private TMFixedFieldListSpec getExampleWarehouseZigbeeNodeTMSpec() {
		TMFixedFieldListSpec tmFixedFieldListSpec = new TMFixedFieldListSpec();
		tmFixedFieldListSpec.setEpcPattern("urn:epc:tag:sgtin-96:3.1234567.000000.[7-12]");
		
		FixedFields ffs = new FixedFields();
		TMFixedFieldSpec tmffspecTemp = new TMFixedFieldSpec();
		tmffspecTemp.setFieldname("temperature");
		tmffspecTemp.setBank(3);
		tmffspecTemp.setLength(4);
		tmffspecTemp.setOffset(2);
		tmffspecTemp.setDefaultDatatype("float");
		tmffspecTemp.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecTemp);
		
		TMFixedFieldSpec tmffspecHumid = new TMFixedFieldSpec();
		tmffspecHumid.setFieldname("humidity");
		tmffspecHumid.setBank(3);
		tmffspecHumid.setLength(4);
		tmffspecHumid.setOffset(6);
		tmffspecHumid.setDefaultDatatype("float");
		tmffspecHumid.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecHumid);
		
		TMFixedFieldSpec tmffspecLight = new TMFixedFieldSpec();
		tmffspecLight.setFieldname("light");
		tmffspecLight.setBank(3);
		tmffspecLight.setLength(4);
		tmffspecLight.setOffset(10);
		tmffspecLight.setDefaultDatatype("float");
		tmffspecLight.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecLight);
		
		TMFixedFieldSpec tmffspecGyroX = new TMFixedFieldSpec();
		tmffspecGyroX.setFieldname("gyroX");
		tmffspecGyroX.setBank(3);
		tmffspecGyroX.setLength(4);
		tmffspecGyroX.setOffset(14);
		tmffspecGyroX.setDefaultDatatype("float");
		tmffspecGyroX.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecGyroX);
		
		TMFixedFieldSpec tmffspecGyroZ = new TMFixedFieldSpec();
		tmffspecGyroZ.setFieldname("gyroZ");
		tmffspecGyroZ.setBank(3);
		tmffspecGyroZ.setLength(4);
		tmffspecGyroZ.setOffset(14);
		tmffspecGyroZ.setDefaultDatatype("float");
		tmffspecGyroZ.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecGyroZ);
		
		tmFixedFieldListSpec.setFixedFields(ffs);
		return tmFixedFieldListSpec;
	}
	
	private TMFixedFieldListSpec getExampleWarehouseIPUSNNodeTMSpec() {
		TMFixedFieldListSpec tmFixedFieldListSpec = new TMFixedFieldListSpec();
		tmFixedFieldListSpec.setEpcPattern("urn:epc:tag:sgtin-96:3.1234567.000000.[5-7]");
		
		FixedFields ffs = new FixedFields();
		TMFixedFieldSpec tmffspecTemp = new TMFixedFieldSpec();
		tmffspecTemp.setFieldname("temperature");
		tmffspecTemp.setBank(3);
		tmffspecTemp.setLength(4);
		tmffspecTemp.setOffset(2);
		tmffspecTemp.setDefaultDatatype("float");
		tmffspecTemp.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecTemp);
		
		TMFixedFieldSpec tmffspecHumid = new TMFixedFieldSpec();
		tmffspecHumid.setFieldname("humidity");
		tmffspecHumid.setBank(3);
		tmffspecHumid.setLength(4);
		tmffspecHumid.setOffset(6);
		tmffspecHumid.setDefaultDatatype("float");
		tmffspecHumid.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecHumid);
		
		TMFixedFieldSpec tmffspecLight = new TMFixedFieldSpec();
		tmffspecLight.setFieldname("light");
		tmffspecLight.setBank(3);
		tmffspecLight.setLength(4);
		tmffspecLight.setOffset(10);
		tmffspecLight.setDefaultDatatype("float");
		tmffspecLight.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecLight);
		
		TMFixedFieldSpec tmffspecGyroX = new TMFixedFieldSpec();
		tmffspecGyroX.setFieldname("gyroX");
		tmffspecGyroX.setBank(3);
		tmffspecGyroX.setLength(4);
		tmffspecGyroX.setOffset(14);
		tmffspecGyroX.setDefaultDatatype("float");
		tmffspecGyroX.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecGyroX);
		
		TMFixedFieldSpec tmffspecGyroZ = new TMFixedFieldSpec();
		tmffspecGyroZ.setFieldname("gyroZ");
		tmffspecGyroZ.setBank(3);
		tmffspecGyroZ.setLength(4);
		tmffspecGyroZ.setOffset(14);
		tmffspecGyroZ.setDefaultDatatype("float");
		tmffspecGyroZ.setDefaultFormat("float");		
		ffs.getFixedField().add(tmffspecGyroZ);
		
		tmFixedFieldListSpec.setFixedFields(ffs);
		return tmFixedFieldListSpec;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World!!");
	}

}
