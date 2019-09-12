package sme.modem.service;

import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;

public interface MonitoreoServiceRemote {

	public void procesarMensaje(NodoDto nodo);

	public void start(ModemLocalDto modem);

	void stop();

	public boolean read();
	
	public String sendCommand(String command,long timeOut);

}