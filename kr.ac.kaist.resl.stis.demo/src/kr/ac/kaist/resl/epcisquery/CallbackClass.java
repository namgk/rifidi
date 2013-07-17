package kr.ac.kaist.resl.epcisquery;
/*
 * 2012 RESL KAIST in KOREA
 * Realtime Embedded System Laboratory
 * Author : 
 * 	Name : Jaewook Byun. Master Student in RESL KAIST
 * 	Mail : kr_spirit@hotmail.com
 * 		
 * This file is simple program for getting callback event.
 * 
 * FossTrak implementation is now providing socket "localhost:8899"
 * So, you should set the destination url as localhost:8899 when you subscribe the event
 * 
 */

import java.io.IOException;

import org.fosstrak.epcis.utils.QueryCallbackListener;


public class CallbackClass extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Simple Thread
		Thread t = new CallbackClass();
		t.start();
	}
	
	public void run()
	{
		try {
			//QueryCallbackListener
			QueryCallbackListener listener;
			listener = QueryCallbackListener.getInstance();
			while(true)
			{
			
				//Getting instance
				
				if (!listener.isRunning()) {
					//listener start
					listener.start();
				}
				synchronized (listener) {    
					try {    
						//if getting time is over 20000, you can not get response. 
						listener.wait(20000);    
					} 
					catch (InterruptedException e) 
					{        e.printStackTrace();    
					}
				}
				//you can get the result in response variable
				String response = listener.fetchResponse();		
				System.out.println(response);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
		
	}

}
