package sme.web.dto;

import java.io.Serializable;

public class SendModemCommandDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 553588710405541422L;
	private int idmodem;
	private String command;
	private long timeout;
	
	public SendModemCommandDto() {
		
	}

	public int getIdmodem() {
		return idmodem;
	}

	public void setIdmodem(int idmodem) {
		this.idmodem = idmodem;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}


	
	

}
