package sme.web.dto;

import java.io.Serializable;

//{"sme":"6641690729","cfe":"true","ups":"false","fechaMensaje":"20/08/19,10:24:45-28","fechaEvento":"20/08/19,10:24:45-28"}
public class NetworkNotificationStatusDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 78434565260011712L;
	
	private String sme;
	private boolean cfe;
	private boolean ups;
	private String fechaMensaje;
	private String fechaEvento;
	
	public NetworkNotificationStatusDto() {
		
	}

	public String getSme() {
		return sme;
	}

	public void setSme(String sme) {
		this.sme = sme;
	}

	public boolean isCfe() {
		return cfe;
	}

	public void setCfe(boolean cfe) {
		this.cfe = cfe;
	}

	public boolean isUps() {
		return ups;
	}

	public void setUps(boolean ups) {
		this.ups = ups;
	}

	public String getFechaMensaje() {
		return fechaMensaje;
	}

	public void setFechaMensaje(String fechaMensaje) {
		this.fechaMensaje = fechaMensaje;
	}

	public String getFechaEvento() {
		return fechaEvento;
	}

	public void setFechaEvento(String fechaEvento) {
		this.fechaEvento = fechaEvento;
	}
	
	public String toString() {
		return "sme:"+sme+", cfe:"+cfe+", ups:"+ups+", fechaMensaje:"+fechaMensaje+", fechaEvento:"+fechaEvento;
	}
}
