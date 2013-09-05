package kr.ac.kaist.resl.edge.readerplugin.obix.data;

import java.util.ArrayList;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class AcelValue {

	ArrayList<Axis> acelerometeraxis = new ArrayList<Axis>();

	String tag = "obj";
	
	public AcelValue(String x, String y, String z) {
		this.acelerometeraxis.add(new Axis(x, "x"));
		this.acelerometeraxis.add(new Axis(y, "y"));
		this.acelerometeraxis.add(new Axis(z, "z"));
	}

	public class Axis extends PrimitiveValue {
		String name;
		public String getName() {
			return name;
		}
		public Axis(String val, String name) {
			super(val, "real");
			this.name = name;
		}
		
	}

	public ArrayList<Axis> getAxises() {
		return acelerometeraxis;
	}
	
	public String getTag() {
		return tag;
	}
}
