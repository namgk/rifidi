/**
 * 
 */
package kr.ac.kaist.resl.stis.listener.coaplistener;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.ac.kaist.resl.stis.listener.STISHandler;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class CoAPListenerImpl implements CoAPListener {

	private ConcurrentMap<Object, STISHandler> resources = new ConcurrentHashMap<Object, STISHandler>();
	
	public CoAPListenerImpl() {}
	
	@Override
	public void registerHandler(Object resourceId, STISHandler handler) {
		// TODO: fine grain put with patterned resourceId, check for match to find duplicates
		resources.putIfAbsent(resourceId, handler);
	}

	@Override
	public STISHandler findHandler(Object resourcePath) {
		if (resourcePath instanceof String){
			String token = (String) resourcePath;
			for (Entry<Object, STISHandler> es : resources.entrySet()){
				if (es.getKey() instanceof String){
					Pattern p = Pattern.compile((String)es.getKey());
					Matcher matcher = p.matcher(token);
					while (matcher.find()) {
						System.out.println("Handler found for: " + matcher.group());
						return es.getValue();
					}
				}
			}
		}
		return null;
	}

	@Override
	public void deregisterHandler(Object resourceId) {
		resources.remove(resourceId);
	}

}
