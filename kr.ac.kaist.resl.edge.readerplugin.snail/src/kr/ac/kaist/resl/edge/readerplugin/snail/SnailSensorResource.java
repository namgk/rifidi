package kr.ac.kaist.resl.edge.readerplugin.snail;


public class SnailSensorResource {
	String epc;
	String[] resources;
	
	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String[] getResources() {
		return resources;
	}

	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public SnailSensorResource() {
		// TODO Auto-generated constructor stub
	}

	public SnailSensorResource(String epc) {
		this.epc = epc;
	}

	@Override
	public String toString(){
		String toReturn = "";
		toReturn += "{epc: " + this.epc + ", resources: [";
		int i = 0;
		if (resources == null){
			toReturn += "]}";
			return toReturn;
		}
		while (i < resources.length){
			toReturn += resources[i] + (i + 1 < resources.length ? "," : "");
			i++;
		}
		toReturn += "]}";
		return toReturn;
	}
}
