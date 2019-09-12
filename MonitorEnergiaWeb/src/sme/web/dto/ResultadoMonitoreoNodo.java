package sme.web.dto;

import java.io.Serializable;

public class ResultadoMonitoreoNodo implements Serializable {

	private boolean success = false;
	private int idnodo = 0;
	private String errorMessage ="";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1455268710825123804L;

	public ResultadoMonitoreoNodo() {
		
	}

	public ResultadoMonitoreoNodo(boolean success, int idnodo,String errorMessage) {
		super();
		this.success = success;
		this.idnodo = idnodo;
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getIdnodo() {
		return idnodo;
	}

	public void setIdnodo(int idnodo) {
		this.idnodo = idnodo;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
