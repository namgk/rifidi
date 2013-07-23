package kr.ac.kaist.resl.stis.listener.coaplistener;


/**
 * The Activator class for the CoAP server.
 * Note: activated by spring framework, not by OSGi
 * 
 * @author Nam Giang - zang at kaist dot ac dot kr
 */

public class Activator {

	CoAPServer cs;
	public CoAPServer getCs() {
		return cs;
	}

	public void setCs(CoAPServer cs) {
		this.cs = cs;
	}

	public void start() throws Exception {
		cs.start();
	}

	public void stop() throws Exception {

	}



}
