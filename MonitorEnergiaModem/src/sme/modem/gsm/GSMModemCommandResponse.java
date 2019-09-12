package sme.modem.gsm;

import java.io.Serializable;

public class GSMModemCommandResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4197544929422623300L;
	
	private boolean responseComplete = false;
	private boolean timeOutReached = false;
	private String response = null;
	
	public GSMModemCommandResponse() {
		
	}

	public GSMModemCommandResponse(boolean responseComplete, boolean timeOutReached, String response) {
		this.responseComplete = responseComplete;
		this.timeOutReached = timeOutReached;
		this.response = response;
	}

	public boolean isResponseComplete() {
		return responseComplete;
	}

	public void setResponseComplete(boolean responseComplete) {
		this.responseComplete = responseComplete;
	}

	public boolean isTimeOutReached() {
		return timeOutReached;
	}

	public void setTimeOutReached(boolean timeOutReached) {
		this.timeOutReached = timeOutReached;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public boolean isResponseWithOk() {
		
		if( !responseComplete || timeOutReached || response == null )
			return false;
		
		if( response.indexOf("OK")>= 0 )
			return true;
		else
			return false;
	}

}
