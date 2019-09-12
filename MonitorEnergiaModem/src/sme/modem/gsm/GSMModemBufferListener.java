package sme.modem.gsm;

public interface GSMModemBufferListener {
	
	public void gsmModemBufferResponse(String response);
	
	public void gsmModemBufferEvent(String event);

}
