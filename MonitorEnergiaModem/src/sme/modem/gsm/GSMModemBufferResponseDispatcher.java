package sme.modem.gsm;

public class GSMModemBufferResponseDispatcher implements Runnable {
	
	private GSMModemBufferListener listener;
	private String response;
	
	public GSMModemBufferResponseDispatcher(GSMModemBufferListener listener,String response) {
		this.listener = listener;
		this.response = response;
		
		if(listener!=null)
			new Thread(this).start();
	}
	
	public void run() {
		//System.out.println("inicio buffer response dispatcher");
		listener.gsmModemBufferResponse(response);
		//System.out.println("fin buffer response dispatcher");
	}

}
