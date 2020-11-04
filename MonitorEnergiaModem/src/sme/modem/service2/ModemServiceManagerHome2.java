package sme.modem.service2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.ModemLocalRemote2;
import sme.client.db.remote.ModemServiceManagerRemote2;
import sme.client.db.remote.NodoRemote2;
import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;

@Startup
@Singleton
@Remote(ModemServiceManagerRemote2.class)
public class ModemServiceManagerHome2 implements ModemServiceManagerRemote2 {
	
	private static final Log log = LogFactory.getLog(ModemServiceManagerHome2.class);
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/ModemLocalHome2")
	private ModemLocalRemote2 mr;
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2")
	private NodoRemote2 nodoRemote;
	
	private static final String modemServiceHome = "java:global/MonitorEnergiaModemApp/MonitorEnergiaModem/ModemServiceHome";
	
	private Map<Integer,ModemServiceRemote> mapa = new HashMap<Integer,ModemServiceRemote>();
	
	public ModemServiceManagerHome2() {
		log.info("ModemServiceManagerHome2 constructor.......................");
	}

	@Override
	@PostConstruct //para que se ejecute despues de que la clase se cree
	public void start() {
		
		//metodo que se ejecuta automaticamente despues de instanciarse
		log.info("iniciando servicio...................");
		
		List<ModemLocalDto> modems = null;
		
		try {
			
			log.info("consultando lista de modems......");
			
			//intentar consultar la lista de modems que hay en la tabla de modem_local
			modems = mr.getModemList();
			
			log.info("lista de modems consultada...........");
			
			//si la lista esta vacia entonces salir de rutina
			if( modems == null || modems.isEmpty() ) {
				log.info("no se encontraron modems para iniciar");
				return;
			}
			
			log.info("lista de modems consultada numero:"+modems.size());
				
		}catch(Exception ex) {
			log.error("error al intentar consultar la lista de modems "+ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		//recorrer todos los modems encontrados
		for(ModemLocalDto m : modems) {
			
			//si el servicio ya esta dado de alta no iniciar el hilo
			if( mapa.containsKey(m.getIdmodem()) ) {
				log.info("servicio de modem "+m.getIdmodem()+" ya iniciado, descantando inicio");
				continue;
			}
			
			//si modemlocal esta inactivo descartarlo y no iniciar el servicio
			if( !m.isActivo() ) {
				log.info("modem con idmodem:"+m.getIdmodem()+" inactivo descartando inicio");
				continue;
			}
			
			//crear servicio y agregarlo al mapa
			createService(m);
				
			}
}

	@Override
	public void stop() {
		
		log.info("terminando todos los servicios...........");
		
		if( mapa.size() <= 0 ) {
			log.info("no hay servicios de modem iniciados, saliendo de rutina");
			return;
		}
		
		//recorrer todos los hilos creados
		for( ModemServiceRemote aux : mapa.values() ) { 
			aux.stop();//detener el thread y el puerto serial
		}
			
		//eliminar todos los objetos de la coleccion mapa
		mapa.clear();
	}

	@Override
	public void startModem(int idmodem) {
		
		if( mapa.containsKey(idmodem) ) {
			log.info("servicio de modem "+idmodem+" ya iniciado, descantando inicio");
			return;
		}
		
		ModemLocalDto m = mr.findByIdDto(idmodem);
		
		if(m == null) {
			log.info("no se encontro modem con idmodemlocal "+idmodem+", descartando inicio");
			return;
		}
		
		//crear el servicio y agregarlo al mapa
		createService(m);
	}

	@Override
	public void stopModem(int idmodem) {
		
		if(!mapa.containsKey(idmodem) ) {
			log.info("no se encontro servicio de modem corriendo con id:"+idmodem);
			return;
		}
		
		//traer el thread con el idmodem especificado
		ModemServiceRemote aux = mapa.get(idmodem);
		
		//detener servicio
		aux.stop();
		
		//eliminar servicio de mapa
		mapa.remove(idmodem);
	}

	@Override
	public void stopInactive() {

		List<ModemLocalDto> modems = null;
		
		//intentar consultar la lista de modems
		try { modems = mr.getModemList(); } catch(Exception ex) { log.error("error al intentar consultar la lista de modems"); return;}
		
		//si no hay ningun modem en la lista detener todos y salir
		if(modems == null || modems.isEmpty() ) {
			stop();
			return;
		}
		
		//recorrer los modems encontrados en la base de datos
		for( ModemLocalDto m : modems ) {
			
			//si ya no esta activo en la base de datos y si esta en el mapa
			//detenerlo y eliminarlo de mapa
			if( !m.isActivo() && mapa.containsKey(m.getIdmodem())) {
				ModemServiceRemote x = mapa.get(m.getIdmodem());
				x.stop();
				mapa.remove(m.getIdmodem());
			}
		}
	}

	@Override
	public List<ModemLocalDto> getServicios() {
		List<ModemLocalDto> modems = null;
		List<ModemLocalDto> nmodems = new ArrayList<ModemLocalDto>();
	
		//intentar consultar la lista de modems que hay en la tabla de modem_local
		modems = mr.getModemList();
		
		for(ModemLocalDto modem : modems) {
			if( mapa.containsKey(modem.getIdmodem()) ) {
				nmodems.add(modem);
			}
				
		}
		
		return nmodems;
	}

	@Override
	public String sendCommand(int idmodem, String command, long timeOut) {
		
		if(! mapa.containsKey(idmodem) )
			return null;
		
		return mapa.get(idmodem).sendCommand(command, timeOut);
	}
	
	private void createService(ModemLocalDto modem) {

		Context ctx = null;
		
		//crear contexto inicial
		try { 
			ctx = new InitialContext();
		}catch(Exception ex) {
			log.error("error no se pudo crear contexto inicial "+ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		ModemServiceRemote remote = null;
		
		//buscar un EJB disponible
		try {
			remote = (ModemServiceRemote) ctx.lookup(modemServiceHome);
		}catch (NamingException e) {
			log.error("error no se encontro el nombre "+modemServiceHome+" "+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//crear puerto serial y todos los servicios del modem
		remote.start(modem);
		
		//agregar servicio a la lista de servicios iniciados
		mapa.put(modem.getIdmodem(), remote);
			
	}
	
	//@Schedule(second = "*/30", minute = "*", hour = "*") //run every 30 seconds
	
	//run every minute
	@Schedule(hour = "*", minute = "*/1") 
	public void monitorear() {
		
		log.info("monitoreando........");
		
		if( mapa.isEmpty() ) {
			log.info("no hay procesos de monitoreo corriendo, cancelando monitoreo.........");
			return;
		}
		
		List<NodoDto> list = nodoRemote.getNodosMonitoreo(0, mapa.size(), 120);
		
		if( list == null || list.size() <= 0 ) {
			log.info("no hay nodos a monitorear.........");
			return;
		}
		
		for( ModemServiceRemote modemRemote : mapa.values() ) {
			
			if( modemRemote.isResourceAvailable() ) {
				modemRemote.monitorNodeByCall(list.get(0));
				list.remove(0);
			} else
				log.info("recurso ocupado..........");
		}
		
	}

}
