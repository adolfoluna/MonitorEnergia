package sme.modem.gsm;

public class GSMModemBufferEventDispatcher implements Runnable {
	
	private GSMModemBufferListener listener;
	private String event;
	
	public GSMModemBufferEventDispatcher(GSMModemBufferListener listener,String event) {
		this.listener = listener;
		this.event = event;
		
		if(this.listener!=null)
			new Thread(this).start();
	}
	
	public void run() {
		//System.out.println("inicio buffer event dispatcher");
		listener.gsmModemBufferEvent(event);
		//System.out.println("fin buffer event dispatcher");
	}

}
