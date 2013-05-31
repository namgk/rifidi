package obix.contracts;

import obix.IObj;
import obix.Str;

public interface Id extends IObj {
	public static final String CONTRACT="iot:id";
	public static final String valueContract = "<str name='id' href='id' val='0057000.123430.2025'/>";
	public Str value();
}
