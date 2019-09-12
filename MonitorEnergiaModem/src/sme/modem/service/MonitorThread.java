package sme.modem.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MonitorThread implements Runnable {
	
	private static Log log = LogFactory.getLog(MonitorThread.class);
	
	private boolean active = true;
	private String sendCommand = null;
	private String res = null;
	private long timeOut = 0;
	private Thread hilo;
	private boolean leerCola = false;
	
	private MonitoreoServiceRemote monitoreoServiceRemote;
	
	public MonitorThread(MonitoreoServiceRemote monitoreoServiceRemote,boolean leerCola) {
		this.monitoreoServiceRemote = monitoreoServiceRemote;
		this.leerCola = leerCola;
	}

	public void run() {
		
		log.info("inicio de proceso de monitoreo de nodos...........");
		
		while(active) {
			
			//si hay algun comando pendiente a enviar al modem enviarlo
			if( sendCommand != null ) {
				res = monitoreoServiceRemote.sendCommand(sendCommand, timeOut);
				sendCommand = null;
			}
			
			//si no hay que leer la cola de mensajes de monitoreo hacer delay y regresar al inicio
			//del ciclo
			if(!leerCola) {
				try { Thread.sleep(1_000); } catch (InterruptedException e) { log.info("proceso de monitoreo interrumpido");}
				continue;
			}
			
			//intentar leer de la cola, en caso de que el recurso este ocupado
			//hacer un delay de 1 segundo
			if( monitoreoServiceRemote.read() ) {
				//hacer una espera de 1 segundo para ver si el recurso ya se desocupo
				try { Thread.sleep(1_000); } catch (InterruptedException e) { log.info("proceso de monitoreo interrumpido");}
			}
			
		}
		
		monitoreoServiceRemote.stop();
		
		log.info("fin de proceso de monitoreo de nodos...........");
		
	}
	
	public void stop() {
		
		active = false;
		
		//esperar 1 segundo
		try {Thread.sleep(1_000);} catch (InterruptedException e) {}
		
		//interrumpir hilo en caso de que siga vivo
		if(hilo.isAlive())
			hilo.interrupt();
		
	}
	
	public String sendCommand(String command,long timeOut) {
		
		sendCommand = command;
		this.timeOut = timeOut;
		res = null;
		
		long cont = 0;
		
		while(res == null && cont < (timeOut/1_000)) {
			try {Thread.sleep(1_000);}catch(Exception ex) {}
			cont++;
		}
		
		return res;
		
	}
	
	public void start() {
		
		if(hilo != null ) {
			log.info("hilo ya iniciado..........");
			return;
		}
		
		//iniciar hilo
		hilo = new Thread(this);
		hilo.start();
	}
	
}
