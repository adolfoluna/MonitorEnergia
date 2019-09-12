package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CellPhoneModuleMakeCall implements Runnable {
	
	private static final Log log = LogFactory.getLog(CellPhoneModuleMakeCall.class);
	private CellPhoneModule modem;
	private String number;
	
	public CellPhoneModuleMakeCall(CellPhoneModule modem,String number) {
		this.modem = modem;
		this.number = number;
		new Thread(this).start();
	}
	
	public void run() {
		
		log.info("inicio de proceso para hacer llamada.........");
		
		//ocupar la linea
		modem.getCallStatus().setCallInProgress(true);

		if( !modem.getModem().call(number) ) 
			modem.getCallStatus().setCallInProgress(false);//desocupar linea debido a que no se pudo realizar la llamada
			
		log.info("fin de proceso para hacer llamada.........");
	}

}
