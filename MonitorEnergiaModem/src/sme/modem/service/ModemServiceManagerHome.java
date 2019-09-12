package sme.modem.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.ModemLocalRemote2;
import sme.client.db.remote.ModemServiceManagerRemote;
import sme.client.dto.ModemLocalDto;

@Startup
@Singleton
@Remote(ModemServiceManagerRemote.class)
public class ModemServiceManagerHome implements ModemServiceManagerRemote {
	
	private static final Log log = LogFactory.getLog(ModemServiceManagerHome.class);
	
	private static final String monitoreoServiceHome = "java:global/MonitorEnergiaModemApp/MonitorEnergiaModem/MonitoreoServiceHome";
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/ModemLocalHome2")
	private ModemLocalRemote2 mr;
	
	private Map<Integer,MonitorThread> mapa = new HashMap<Integer,MonitorThread>();

	public ModemServiceManagerHome() {
				
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.service.ModemServiceManagerRemote#start()
	 */
	@Override
	@PostConstruct
	public void start() {
		
		//metodo que se ejecuta automaticamente despues de instanciarse
		//con la anotacion @PostConstruct
		log.info("iniciando servicio...................");
		
		List<ModemLocalDto> modems = null;
		
		try {
			
			log.info("consultando lista de modems......");
			
			//intentar consultar la lista de modems que hay en la tabla de modem_local
			modems = mr.getModemList();
			
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
	
	/* (non-Javadoc)
	 * @see sme.modem.service.ModemServiceManagerRemote#stop()
	 */
	@Override
	public void stop() {

		log.info("terminando todos los servicios...........");
		
		if( mapa.size() <= 0 ) {
			log.info("no hay servicios de modem iniciados, saliendo de rutina");
			return;
		}
		
		//recorrer todos los hilos creados
		for( MonitorThread aux : mapa.values() ) { 
			aux.stop();//detener el thread y el puerto serial
		}
			
		//eliminar todos los objetos de la coleccion mapa
		mapa.clear();
	}
	
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
	
	public void stopModem(int idmodem) {
		
		if(!mapa.containsKey(idmodem) ) {
			log.info("no se encontro servicio de modem corriendo con id:"+idmodem);
			return;
		}
		
		//traer el thread con el idmodem especificado
		MonitorThread aux = mapa.get(idmodem);
		
		//detener servicio
		aux.stop();
		
		//eliminar servicio de mapa
		mapa.remove(idmodem);
		
	}
	
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
				MonitorThread x = mapa.get(m.getIdmodem());
				x.stop();
				mapa.remove(m.getIdmodem());
			}
		}
			
	}
	
	public String sendCommand(int idmodem,String command,long timeOut) {
		
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
		
		MonitoreoServiceRemote remote = null;
		
		//buscar un EJB disponible
		try {
			remote = (MonitoreoServiceRemote) ctx.lookup(monitoreoServiceHome);
		}catch (NamingException e) {
			log.error("error no se encontro el nombre "+monitoreoServiceHome+" "+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//crear puerto serial y todos los servicios del modem
		remote.start(modem);
		
		MonitorThread aux = new MonitorThread(remote,modem.isMonitoreoActivo());
		
		//iniciar el hilo que va a estar leyendo de la cola de monitoreo
		aux.start();
		
		//agregar servicio a la lista de servicios iniciados
		mapa.put(modem.getIdmodem(), aux);
			
	}
}
