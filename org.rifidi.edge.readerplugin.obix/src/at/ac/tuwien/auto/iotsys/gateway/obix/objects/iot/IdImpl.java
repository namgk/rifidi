package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot;

import obix.Contract;
import obix.Obj;
import obix.Str;
import obix.Uri;
import obix.contracts.Id;

public class IdImpl  extends Obj implements Id {

	protected Str value = new Str();
	
	public IdImpl(String epc){
		setHref(new Uri("id"));
		setIs(new Contract(Id.CONTRACT));
		value.setWritable(false);
		Uri valueUri = new Uri("value");
	
		value.setHref(valueUri);
		value.setName("value");
		value.set(epc);
		add(value);
	}
	@Override
	public Str value() {
		return this.value;
	}
	
	public Str getId(){
		return this.value;
	}

}
