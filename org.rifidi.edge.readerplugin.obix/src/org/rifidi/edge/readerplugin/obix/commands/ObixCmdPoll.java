package org.rifidi.edge.readerplugin.obix.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.rifidi.edge.core.sensors.commands.Command;

public class ObixCmdPoll extends Command {
	
	private volatile String aParameter = "bbb";
	
	public void setAParameter(String aParameter) {
		this.aParameter = aParameter;
	}

	private static final Log logger = LogFactory
			.getLog(ObixCmdPoll.class);
	
	HttpClient httpclient;	
	

	public ObixCmdPoll(String commandID) {
		super(commandID);
		httpclient = new DefaultHttpClient();
		logger.info("Construct Obix Poll Command");
	}
	

	@Override
	public void run() {
		logger.info("run Obix Poll Command");
		HttpResponse response;
		
//		HttpGet getReq = new HttpGet("http://" + super.getHost() + ":" + super.getPort() + "/obix");
//		try {
//			response = httpclient.execute(getReq);
//			String content = IOUtils.toString(response.getEntity().getContent());
//			Obj lobby = ObixDecoder.fromString(content);
//			Obj[] resources = lobby.list();
//			
//			System.out.println("done, lobby: \n" + content);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
