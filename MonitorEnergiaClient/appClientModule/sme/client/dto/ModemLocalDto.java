package sme.client.dto;

import java.io.Serializable;

public class ModemLocalDto implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5153119979139835462L;
	
	private int idmodem;
	private int version;
	private String numero;
	private String puerto;
	private int baudrate;
	private int databits;
	private String parity;
	private int stopbits;
	private boolean activo;
	private boolean monitoreoActivo;//bandera para indicar si se va a estar leyendo de la cola de monitoreo o no
	
	public ModemLocalDto() {
		
	}

	public ModemLocalDto(int idmodem, int version, String numero, String puerto, int baudrate, int databits,
			String parity, int stopbits, boolean activo,boolean monitoreoActivo) {
		super();
		this.idmodem = idmodem;
		this.version = version;
		this.numero = numero;
		this.puerto = puerto;
		this.baudrate = baudrate;
		this.databits = databits;
		this.parity = parity;
		this.stopbits = stopbits;
		this.activo = activo;
		this.monitoreoActivo = monitoreoActivo;
	}

	public int getIdmodem() {
		return idmodem;
	}

	public void setIdmodem(int idmodem) {
		this.idmodem = idmodem;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public int getBaudrate() {
		return baudrate;
	}

	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}

	public int getDatabits() {
		return databits;
	}

	public void setDatabits(int databits) {
		this.databits = databits;
	}

	public String getParity() {
		return parity;
	}

	public void setParity(String parity) {
		this.parity = parity;
	}

	public int getStopbits() {
		return stopbits;
	}

	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	public boolean isMonitoreoActivo() {
		return monitoreoActivo;
	}

	public void setMonitoreoActivo(boolean monitoreoActivo) {
		this.monitoreoActivo = monitoreoActivo;
	}

	public boolean equals(Object o) {
		
		if( !(o instanceof ModemLocalDto) )
			return false;
		
		ModemLocalDto aux = (ModemLocalDto) o;
		
		if(aux.getIdmodem() == this.idmodem )
			return true;
		else
			return false;
	}
	
	public String toString() {
		return "idmodem:"+idmodem
				+" numero:"+numero
				+" puerto:"+puerto
				+" baudrate:"+baudrate
				+" databits:"+databits
				+" parity:"+parity
				+" stopbits:"+stopbits
				+" activo:"+activo
				+" version:"+version;
	}
}
