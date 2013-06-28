package kr.ac.kaist.resl.especmgmt;

import org.rifidi.edge.epcglobal.aleread.service.ECSPECManagerService;

public class SpringDmEcspecConsumer {

	private ECSPECManagerService ems;
	private MyThread thread;

	public synchronized void setEm(ECSPECManagerService ems) {
		System.out.println(">>>>>>>>>> setEms called");
		this.ems = ems;
		thread = new MyThread(ems);
		thread.start();
	}

	public synchronized void unsetEm(ECSPECManagerService ems) {
		System.out.println(">>>>>>>>>> unsetEms called");
		if (this.ems == ems)
			this.ems = null;
	}
	
	public SpringDmEcspecConsumer() {
		// TODO Auto-generated constructor stub
	}

	public void go() {
		System.out.println(">>>>>>>>>>> spring dm ecspec consumer gogo ");
		thread = new MyThread(ems);
		thread.start();
	}

	public static class MyThread extends Thread {

		private volatile boolean active = true;
		private ECSPECManagerService ems;

		public MyThread(ECSPECManagerService service) {
			this.ems = service;
		}

		public void run() {
			while (active) {
				try {
					System.out.println(">>>>>>>>>> Ecspec list: "
							+ ((ems != null) ? ems.getSubscriptionsByLRName("Obix_1")
									: "! opps no services!"));
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted " + e.getMessage());
				} catch (NullPointerException e){
					
				}
			}
		}

		public void stopThread() {
			active = false;
		}
	}
}
