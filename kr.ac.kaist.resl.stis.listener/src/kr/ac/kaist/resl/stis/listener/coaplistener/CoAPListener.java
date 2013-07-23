package kr.ac.kaist.resl.stis.listener.coaplistener;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
import kr.ac.kaist.resl.stis.listener.STISHandler;

public interface CoAPListener {

	public void registerHandler(Object resourceId, STISHandler handler);
	public STISHandler findHandler(Object resourceId);
}
