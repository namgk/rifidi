package kr.ac.kaist.resl.edge.readerplugin.obix.data;

public class PrimitiveValue {

	public String getVal() {
		return val;
	}

	public String getTag() {
		return tag;
	}

	String val;
	String tag;
	
	public PrimitiveValue(String val, String tag) {
		this.val = val;
		this.tag = tag;
	}

}
