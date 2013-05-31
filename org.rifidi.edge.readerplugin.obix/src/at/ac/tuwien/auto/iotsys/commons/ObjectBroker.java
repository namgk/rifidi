package at.ac.tuwien.auto.iotsys.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;



import obix.Obj;
import obix.Uri;

public interface ObjectBroker {

	public abstract HashMap<String, String> get_ipv6MappingTable();

	public abstract Obj pullObj(Uri href);

	public abstract Obj pushObj(Uri href, Obj input, boolean isOp)
			throws Exception;

	public abstract void loadObjects(File file) throws Exception;

	public abstract void saveObjects(File file) throws Exception;

	public abstract ArrayList<String> addObj(Obj o, String ipv6Address);

	public abstract String getIPv6LinkedHref(String ipv6Address);

	public abstract boolean containsIPv6(String ipv6Address);

	public ArrayList<String> addObj(Obj o);

	public ArrayList<String> addObj(Obj o, boolean listInLobby);

	public abstract void removeObj(String href);

	public abstract Obj invokeOp(Uri uri, Obj input, boolean b);

	public abstract void addOperationHandler(Uri uri, OperationHandler handler);

	public abstract String getCoRELinks();
	
	public void addHistoryToDatapoints(Obj obj);
	
	public void addHistoryToDatapoints(Obj obj, int countMax);
}