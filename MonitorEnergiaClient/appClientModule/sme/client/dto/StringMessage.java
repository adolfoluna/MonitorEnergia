package sme.client.dto;

import java.io.Serializable;

public class StringMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2706335446567378445L;
	
	private String message;
	
	public StringMessage() {
		
	}
	
	public StringMessage(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
