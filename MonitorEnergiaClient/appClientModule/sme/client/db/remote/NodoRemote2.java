package sme.client.db.remote;

import java.util.List;
import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;

public interface NodoRemote2 {

	public List<NodoDto> getNodosMonitoreo(int page,int limit,int segundos);
	
	public void updateNodoDto(NodoDto nodo);
	
	public NodoDto findByIdDto(int idnodo);
	
	public boolean updateNodoStatus(NodoStatusNotification aux,boolean soloStatusMasActual);
	
	public List<NodoDto> getNodos(int page,int limit);
	
	public List<NodoDto> getNodosActivos(int page,int limit);
	
	public NodoDto findByNumberDto(String number);
	
	public void mergeDto(NodoDto nodo);
	
	public int insertDto(NodoDto nodo);
	
	public void removeById(int idnodo);
	
	public void updateMonitoreoDate(String numero);

	public void updateNodoStatus(NodoStatusNotification aux);
}