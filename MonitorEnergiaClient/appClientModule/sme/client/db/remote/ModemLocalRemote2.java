package sme.client.db.remote;

import java.util.List;

import sme.client.dto.ModemLocalDto;

public interface ModemLocalRemote2 {

	public List<ModemLocalDto> getModemList();
	
	public List<ModemLocalDto> getModemList(int limite,int pagina);
	
	public ModemLocalDto findByIdDto(int idmodem);
	
	public void mergeDto(ModemLocalDto modem);
	
	public int insertDto(ModemLocalDto modem);
	
	public void remove(int idmodem);

}