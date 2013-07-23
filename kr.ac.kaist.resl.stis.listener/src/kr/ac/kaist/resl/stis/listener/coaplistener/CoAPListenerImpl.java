/**
 * 
 */
package kr.ac.kaist.resl.stis.listener.coaplistener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import kr.ac.kaist.resl.stis.listener.STISHandler;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class CoAPListenerImpl implements CoAPListener {

	private ConcurrentMap<Object, STISHandler> resources = new ConcurrentHashMap<Object, STISHandler>();
	
	public CoAPListenerImpl() {
		System.out.println("++++++++++++++++ initiated");
	}
	
	@Override
	public void registerHandler(Object resourceId, STISHandler handler) {
		// TODO Auto-generated method stub
		resources.putIfAbsent(resourceId, handler);

	}

	@Override
	public STISHandler findHandler(Object resourceId) {
		// TODO Auto-generated method stub
		return resources.get(resourceId);
	}

}
