package sme.modem.service2;

import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;

public interface ModemServiceRemote {

	void start(ModemLocalDto modemdto);

	void stop();

	String sendCommand(String command, long timeOut);

	boolean isResourceAvailable();

	void monitorNodeByCall(NodoDto nodo);

}