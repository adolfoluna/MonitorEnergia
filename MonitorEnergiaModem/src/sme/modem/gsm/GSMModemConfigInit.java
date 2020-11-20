package sme.modem.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GSMModemConfigInit implements Runnable {
	
	private static final Log log = LogFactory.getLog(GSMModemConfigInit.class);
	
	private GSMModemWithCommands modem;
	
	private Thread hilo;
	private boolean active = true;
	private GSMModemConfigInitListener listener;
	
	private static final long timeWaitOnError = 20_000;
	
	public GSMModemConfigInit(GSMModemWithCommands modem,GSMModemConfigInitListener listener) {
		this.modem = modem;
		this.listener = listener;
	}
	
	public void run() {
		
		log.info("inicio de proceso de configuracion...........");
		
		int i = 0;
		
		try {
			
			//enviar nueva linea para interrumpir cualquier comando en caso de que exista
			modem.sendCommand("\r\n",5_000);
			
			//enviar los comandos de configuracion
			while(active && modem.getMediaComm() != null && i <= 4 ) {
				i = init(i);
			}
			
			//intentar seleccionar el operador
			while(active && modem.getMediaComm() != null && i > 4) {
				
				//si se pudo seleccionar el operador salir de rutina
				if(seleccionarOperador()) {
					
					if(listener!= null)
						listener.initConfigFinished();
					
					return;
				}
			/////////////////////////////////////////	
				
			}
		}finally {
			log.info("fin de proceso de configuracion.............");
		}
		
		
		
	}
	
	public void start() {
		
		if(hilo!=null) {
			log.error("error, proceso de configuracion ya iniciado");
			return;
		}
		
		hilo = new Thread(this);
		hilo.start();
	}
	
	public void stop() {
		
		active = false;
		
		if( hilo!= null && hilo.isAlive())
			hilo.interrupt();
	}
	
	private int init(int i) {
		
		String msg = "";
		String cmd = "";
		
		switch(i) {
			case 0:
				msg = "configurando para apagar eco........";
				cmd = "ATE0\r\n";
				break;
			case 1:
				msg = "configurando para que los mensajes se envien como sms texto............";
				cmd = "AT+CMGF=1\r\n";
				break;
			case 2:
				msg = "configurando para que lleguen las notificaciones de mensajes de texto..........";
				cmd = "AT+CNMI=1,1\r\n";
				break;
			case 3:
				msg = "configurando para que se puedan colgar las llamadas con el comando ATH..........";
				cmd ="AT+CVHU=0\r\n";
				break;
			/*case 3:
				msg = "configurando para que lleguen las notificaciones de llamadas..........";
				cmd ="AT+UCALLSTAT=1\r\n";
				break;
			case 4:
				msg = "configurando para que lleguen notificaciones DTMF.................";
				cmd = "AT+UDTMFD=1,2\r\n";
				break;*/
			case 4:
				msg = "configurando duracion de tomo en valor 10 (1 segundo)................";
				cmd = "AT+VTD=10\r\n";
				break;
		}

		log.info(msg);
		
		//enviar el comando de configuracion
		GSMModemCommandResponse res = modem.sendCommand(cmd,5_000);
		
		//si hubo un error en el puerto serial entonces esperar 10 segundos y salir
		if( res == null ) {
			try { Thread.sleep(timeWaitOnError); } catch(Exception ex) { }
			return i;
		}
				
		//si el modem respondio con un OK significa que el comando se ejecuto exitosamente
		if( res.isResponseComplete() && res.getResponse().contains("OK") )
			return i+1;
		
		log.error("comando "+cmd+" no pudo ser completado");
		
		//esperar un segundo para el proximo reintento
		try {
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			log.info("proceso interrumpido");
		}
		
		//regresar el mismo numero recibido impidiendo avanzar de comando
		return i;
	}
	
	public boolean seleccionarOperador() {
		
		log.info("listando operadores disponibles (tiempo de espera max 1min).........");
		
		//enviar comando para listar operadores
		GSMModemCommandResponse res = modem.sendCommand("AT+COPS=?\r\n",60_000);
		
		//si hubo un error en el puerto serial entonces esperar 10 segundos y salir
		if( res == null ) {
			try { Thread.sleep(timeWaitOnError); } catch(Exception ex) { }
			return false;
		}
		
		//si no ha llegado ninguna respuesta significa que se quedo buscando
		if( res.getResponse() == null ) {

			//interrumpir la busqueda de operadores
			res = modem.sendCommand("\r\n",5_000);
			
			//volver al inicio del ciclo, para nuevamente solicitar lista de operadores
			return false;
		}
		
		String temp = res.getResponse().replace("\r\n", " ").trim();
		
		//si no se encontro ni TELCEL ni T-MOBILE, regresar al inicio del ciclo
		if( temp.indexOf("\"334020\"") < 0 && temp.indexOf("\"310260\"") < 0 ) {
			log.error("error, no se encontro operador TELCEL o T-MOBILE");
			return false;
		}
		
		//red 334020 es de TELCEL firmarse a esa red
		if( temp.indexOf("\"334020\"") >= 0 ) {
			log.info("intentando firmarse a la red de TELCEL........");
			res = modem.sendCommand("AT+COPS=1,2,\"334020\"\r\n",30_000);
		} 
		
		//red 310260 es T-MOBILE
		if( temp.indexOf("\"310260\"") >= 0) {
			log.info("intentando firmarse a la red de T-MOBILE........");
			res = modem.sendCommand("AT+COPS=1,2,\"310260\"\r\n",30_000);
		}
		
		//si la respuesta fue null entonces cancelar el comando y regresar a inicio de ciclo
		if( res.getResponse() == null ) {
			
			log.error("error al intentar firmarse en red, no hubo respuesta");
			
			log.info("enviando comando para interrumpir proceso de firmarse a operador.....");
			
			//enviar comando para interrumpir la afiliacion de la red
			modem.sendCommand("\r\n",5_000);
			
			//regresar a inicio de ciclo
			return false;
			
		}
		
		//si la respuesta es OK significa que se pudo firmar la red correctamente
		if( res.getResponse().indexOf("OK") >= 0 ) {
			log.info("modem exitosamente firmado a la red");
			return true;
		}
		
		
		return false;
	}
	
}
	