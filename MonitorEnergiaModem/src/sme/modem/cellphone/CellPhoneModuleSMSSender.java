package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CellPhoneModuleSMSSender implements Runnable {

	private static final Log log = LogFactory.getLog(CellPhoneModuleSMSSender.class);
	
	private CellPhoneModuleService modem;
	
	private boolean active = true;
	private Thread hilo;
	
	private String message;
	private String phoneNumber;
	
	public CellPhoneModuleSMSSender(CellPhoneModuleService modem,String message,String phoneNumber) {
		this.modem = modem;
		this.message = message;
		this.phoneNumber = phoneNumber;
		hilo = new Thread(this);
		hilo.start();
	}
	
	public void run() {
		
		try {
			
			log.info("inicio de proceso para enviar SMS.......");
			
			//repeir hasta que el recurso este disponible
			do {
				//esperar 1 segundo
				try { Thread.sleep(1_000); }catch(InterruptedException ex) {return;	}
				
				if(!active)
					return;
				
			}while( !modem.isInitialized() || modem.getCellPhoneModule().isSendingSMS() || modem.getCellPhoneModule().getCallStatus().isCallInProgress());
			
			//intentar enviar mensaje
			if(active)
				modem.getCellPhoneModule().sendSMS(phoneNumber, message);
			
		}finally {
			log.info("fin de proceso para enviar SMS.......");
		}
		
	}
	
	public void stop() {
		active = false;
		if( hilo != null && hilo.isAlive())
			hilo.interrupt();
	}
	
}
