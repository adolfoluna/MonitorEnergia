package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.GSMModemCommandResponse;

public class CellPhoneHandUpCall implements Runnable {
	
	private static final Log log = LogFactory.getLog(CellPhoneAnswerCall.class);
	
	private CellPhoneModule modem;
	private boolean delay;
	
	public CellPhoneHandUpCall(CellPhoneModule modem,boolean delay) {
		this.modem = modem;
		this.delay = delay;
		new Thread(this).start();
	}
	
	public void run() {
		
		try {
			
			log.info("inicio de proceso para colgar llamada........");
			
			//si la bandera esta encendida, hacer un delay de 2 segundos
			if( delay ) {
				log.info("haciendo retardo de 2 segundos antes de intentar colgar llamada....");
				try {Thread.sleep(2_000);} catch (InterruptedException e) {	}
			}
			
			//intentar colgar y si es exitoso salir de rutina
			if(colgar()) 
				return;
			
			//intentar colgar y si es exitoso salir de rutina
			if(colgar()) 
				return;
			
			//hacer el ultimo intento de colgar
			colgar();
			
			
		}finally {
			log.info("fin de proceso para colgar llamada........");
		}
	}
	
	private boolean colgar() {
		
		//colgar la llamada
		GSMModemCommandResponse res = modem.getModem().sendCommand("ATH\r\n",5_000);
		
		//si la respuesta estuvo bien entonces salir
		if( res != null && res.isResponseWithOk()) {
			
			//actualizar que ya no se esta realizando la llamada
			modem.getCallStatus().setCallInProgress(false);
			
			log.info("llamada exitosamente colgada");
			
			//salir
			return true;
		}
		
		if( res == null || res.getClass() == null ) {
			
			log.error("error no se recibio respuesta al intentar colgar llamada");
			
			//hacer una espera de 1 segundo para el proximo reintento
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return false;
		}
			
		if( !res.isResponseComplete() ) 
			log.error("error no se recibio respuesta al intentar colgar llamada");
	
		if( res.isTimeOutReached() ) 
			log.error("error tiempo de espera agotado para colgar llamada");
		
		if( res.getResponse() == null || res.getResponse().indexOf("ERROR") >= 0 ) 
			log.error("error, respuesta con error, no se pudo colgar llamada");
		
		//hacer una espera de 1 segundo para el proximo reintento
		try {
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
