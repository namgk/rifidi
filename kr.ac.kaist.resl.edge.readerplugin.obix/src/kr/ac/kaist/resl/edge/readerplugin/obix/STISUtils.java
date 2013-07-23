package kr.ac.kaist.resl.edge.readerplugin.obix;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;

public class STISUtils {

	public STISUtils() {
		// TODO Auto-generated constructor stub
	}

	public static DatacontainerEvent getTag(String szEPC) throws DecoderException {
		// the new event
		DatacontainerEvent tagData = null;

		BigInteger epc = null;
		epc = new BigInteger(Hex.decodeHex(szEPC.toCharArray()));
		int numbits = szEPC.length() * 4;
		{
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(epc, numbits);
			tagData = gen2event;
		}

		return tagData;
	}
}
