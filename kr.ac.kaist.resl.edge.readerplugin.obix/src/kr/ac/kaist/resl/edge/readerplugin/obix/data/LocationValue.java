package kr.ac.kaist.resl.edge.readerplugin.obix.data;

import java.util.ArrayList;

public class LocationValue {

	ArrayList<LatLong> nodes = new ArrayList<LatLong>();
	String tag = "obj";
	
	public LocationValue(String lat, String longtitute) {
		this.nodes.add(new LatLong(lat, "lat"));
		this.nodes.add(new LatLong(longtitute, "long"));
	}

	public class LatLong extends PrimitiveValue {
		String name;
		public String getName() {
			return name;
		}
		public LatLong(String val, String name) {
			super(val, "real");
			this.name = name;
		}
	}
	
	public ArrayList<LatLong> getNodes() {
		return nodes;
	}

	public String getTag() {
		return tag;
	}
}
