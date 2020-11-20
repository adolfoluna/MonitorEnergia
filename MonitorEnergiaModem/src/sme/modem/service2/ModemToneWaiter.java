package sme.modem.service2;

import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;
import sme.modem.gsm.TimerWaitAsync;
import sme.modem.gsm.WaitTimeOutListener;
import sme.modem.service.NodoToneParser;

public class ModemToneWaiter implements WaitTimeOutListener {
	
	private static Log log = LogFactory.getLog(ModemToneWaiter.class);
	private static final String ejbName = "java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2";
	
	private static final long WAITING_TIME = 60_000;
	
	private NodoDto nodo;
	private String respuesta = "";
	private TimerWaitAsync waiter = new TimerWaitAsync();
	private ModemToneWaiterFinishedInterface listener;
	
	public ModemToneWaiter(NodoDto nodo, ModemToneWaiterFinishedInterface listener) {
		
		//guardar la referencia del nodo a monitorear
		this.nodo = nodo;
		
		//guardar la referencia del listener
		this.listener = listener;
		
		//escuchar cuando el timer se expire
		waiter.setWaitTimeOutListener(this);
		
		//esperar por la respuesta por 60 segundos
		waiter.startWait(WAITING_TIME);
	}
	
	public void addTone(String tone) {
		
		respuesta+=tone;
		
		if( respuesta.endsWith("*") ) {
			
			//detener la espera
			waiter.stopWait();
			
			//detener el thread
			waiter.stop();
			
			//lamar al listener si es que hay uno para indicar que ya se termino la espera
			if( this.listener != null )
				listener.waitingToneDone();
			
			//analizar respuesta
			analizarRespuesta();
		}
	}

	@Override
	public void waitTimedOut() {
		
		log.error("error tiempo de espera agotado , respuesta de tonos incompleta \""+respuesta+"\"");

		//detener el thread waiter
		waiter.stop();
		
		//lamar al listener si es que hay uno para indicar que ya se termino la espera
		if( this.listener != null )
			listener.waitingToneDone();
	}
	
	
	private void analizarRespuesta() {
		
		//validar que la respuesta empiece con # 
		if( !respuesta.startsWith("#") ) {
			log.error("error, respuesta de tonos mal formada debe iniciar con # respuesta:\""+respuesta+"\", descartando respuesta.....");
			return;
		}
		
		//validar que la respuesta termine con *
		if( !respuesta.endsWith("*") ) {
			log.error("error, respuesta de tonos mal formada debe terminar con * respuesta:\""+respuesta+"\", descartando respuesta.....");
			return;
		}
		
		//validar que la respuesta tenga 18 caracteres
		if( respuesta.length() != 18 ) {
			log.error("error, respuesta de tonos mal formada debe ser de 18 caracteres, respuesta:\""+respuesta+"\", descartando respuesta.....");
			return;
		}
		
		//eliminar el primer y ultima caracter
		respuesta = respuesta.substring(1, respuesta.length()-1);
		
		//intentar crear el objeto NodoStatusNotification a partir de la respuesta de tonos
		NodoStatusNotification nodoStatus = NodoToneParser.parse(nodo, respuesta);
		
		if( nodoStatus == null ) {
			log.error("error, respuesta de tonos mal formada, descartando respuesta..........");
			return;
		}
		
		//llamar al ejb para actualizar el estatus del nodo en la base de datos
		callEJB(nodoStatus);
		
	}
	
	private void callEJB(NodoStatusNotification nodoStatus) {
		
		Context ctx = null;
		
		//crear contexto inicial
		try { 
			ctx = new InitialContext();
		}catch(Exception ex) {
			log.error("error no se pudo crear contexto inicial "+ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		NodoRemote2 remote = null;
		
		//buscar un EJB disponible
		try {
			remote = (NodoRemote2) ctx.lookup(ejbName);
		}catch (NamingException e) {
			log.error("error no se encontro el nombre "+ejbName+" "+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		//la fecha de notificacion es ahorita
		nodoStatus.setNotificationDate(new Date());
		
		//actualizar la base de datos
		remote.updateNodoStatus(nodoStatus);
		
	}

}
