package sme.web.dto;

import java.io.Serializable;

public class NotificationResultDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5241443756900149031L;
	
	private boolean success;
	private String message;
	
	public NotificationResultDto() {
		success = false;
		message = null;
	}
	
	public NotificationResultDto(boolean success,String message) {
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return "success:"+success+" message:"+message;
	}

}
