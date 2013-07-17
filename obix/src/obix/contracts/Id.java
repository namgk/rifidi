package obix.contracts;

import obix.IObj;
import obix.Str;

public interface Id
extends IObj
{
public static final String CONTRACT="iot:Id";
	
	public static final String valueContract = "<str name='value' href='value' val='false'/>";
	public Str value();

}
