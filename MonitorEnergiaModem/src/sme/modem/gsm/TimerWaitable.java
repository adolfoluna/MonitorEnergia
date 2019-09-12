package sme.modem.gsm;

public interface TimerWaitable {
	
	public void startWait(long timeOut);
	
	public void stopWait();
	
	public boolean isWaiting();
	
	public boolean isTimeOutReached();

}
