package sme.modem.serialport;

import java.io.Serializable;

public class PuertoSerialParams implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6051915352940886816L;
	
	private String portName;
	private int baudrate;
	private int databits;
	private String parity;
	private int stopbits;
	private String flowControl;
	
	public PuertoSerialParams() {
		
	}

	public PuertoSerialParams(String portName,int baudrate, int databits, String parity, int stopbits, String flowControl) {
		super();
		this.portName = portName;
		this.baudrate = baudrate;
		this.databits = databits;
		this.parity = parity;
		this.stopbits = stopbits;
		this.flowControl = flowControl;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
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

	public String getFlowControl() {
		return flowControl;
	}

	public void setFlowControl(String flowControl) {
		this.flowControl = flowControl;
	}

}
