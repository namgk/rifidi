package com.nhong.test;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.FanSpeedActuator;
import obix.Obj;
import obix.io.ObixDecoder;

public class TestCast {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		at.ac.tuwien.auto.iotsys.gateway.obix.objects.ContractInit.init();
		String object =
				"<obj href=\"/virtualFanSpeed\" is=\"iot:FanSpeedActuator\">\n"
  +"<int name=\"fanSpeedSetpointValue\" href=\"fanSpeedSetpoint\" val=\"10\" writable=\"true\" unit=\"obix:units/percent\"/>\n"
  +"<bool name=\"enabled\" href=\"enabled\" val=\"false\" writable=\"true\"/>\n"
+"</obj>";

		System.out.println(object);
		Obj kid = ObixDecoder.fromString(object);
		String contract = kid.getIs().toString();
		System.out.println(contract);
		
		long fanSpeed = ((FanSpeedActuator) kid).fanSpeedSetpointValue().get();


		System.out.println("" + fanSpeed);
	}

}
