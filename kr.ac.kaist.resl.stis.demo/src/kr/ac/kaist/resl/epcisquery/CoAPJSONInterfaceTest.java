package kr.ac.kaist.resl.epcisquery;

/*
 * 2012 RESL KAIST in KOREA
 * Realtime Embedded System Laboratory
 * Author : 
 * 	Name : Jaewook Byun. Master Student in RESL KAIST
 * 	Mail : kr_spirit@hotmail.com
 * 		
 * This file is simple program making you to easily use ONS and EPCIS 
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;

public class CoAPJSONInterfaceTest {

	private final static String ONS_SERVER_IP = "[2002:8ff8:3852::8ff8:3852]";

	public static void main(String[] args) {

		// The EPC used in the query
		String epc = "urn:epc:id:sgtin:0057000.123780.7788";//urn:epc:id:sgtin:369783.6810725.52500494167
		
		// EPC parameters
		Map<String, String> params;
		params = new HashMap<String, String>();
		params.put("taglength", "96");

		// TDTEngine initiation
		TDTEngine engine = null;
		if (engine == null) {
			try {
				engine = new TDTEngine();
			} catch (Exception e) {
				e.printStackTrace(System.err);

			}
		}

		// Phase 1: Convert EPC to ONS name representation
		String hostName = engine.convert(epc, params,
				LevelTypeList.ONS_HOSTNAME);
		System.out.println("ONS name of EPC : " + hostName);

		// Phase 2 : Resolve ONS name to EPCIS interface
		// We use JNDI for using DNS

		String epcisQueryInterface = "";
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put("java.naming.factory.initial",
					"com.sun.jndi.dns.DnsContextFactory");
			env.put("java.naming.provider.url", "dns://" + ONS_SERVER_IP);

			Attributes returnAttributes = null;
			NamingEnumeration<?> attributeEnum = null;
			ArrayList<String> results = new ArrayList<String>();

			DirContext ictx = new InitialDirContext(env);
			returnAttributes = ictx.getAttributes(hostName,
					new String[] { "NAPTR" });
			if (returnAttributes.size() > 0) {
				attributeEnum = returnAttributes.get("NAPTR").getAll();
				while (attributeEnum.hasMore())
					results.add((String) attributeEnum.next());
			}
			String result;
			String[] subResults = null;

			// Traverse over the returned NAPTR records from ONS
			// TODO: should take into account the Pref of NAPTR record and loop over the result. 
			// 		For now, just use any record possible
			for (int i = 0; i < results.size(); i++) {
				result = results.get(i);
				subResults = result.split(" ");
				if (result.contains("["))// get the ipv6 endpoint of epcis
					break;
			}

			epcisQueryInterface = subResults[4].substring(6,
					subResults[4].length() - 1);
			System.out.println("EPCIS query interface : " + epcisQueryInterface);
		} catch (NamingException ne) {
			ne.printStackTrace();
		}

		// Phase 3 : Query the EPCIS

		// Query using CoAP/JSON

		// create the epc coap port
		EpcisCoAPPort ecp = new EpcisCoAPPort();
		String response = ecp.query(epcisQueryInterface, epc);
		System.out.println(response);
	}
}
