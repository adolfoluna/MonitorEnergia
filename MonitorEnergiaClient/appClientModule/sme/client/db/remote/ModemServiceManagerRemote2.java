package sme.client.db.remote;

import java.util.List;

import sme.client.dto.ModemLocalDto;

public interface ModemServiceManagerRemote2 {

	void start();

	void stop();
	
	public void startModem(int idmodem);
	
	public void stopModem(int idmodem);
	
	public void stopInactive();
	
	public List<ModemLocalDto> getServicios();
	
	public String sendCommand(int idmodem,String command,long timeOut);

}