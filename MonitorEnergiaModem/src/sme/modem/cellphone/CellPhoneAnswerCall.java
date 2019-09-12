package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.GSMModemCommandResponse;

public class CellPhoneAnswerCall implements Runnable {

	private static final Log log = LogFactory.getLog(CellPhoneAnswerCall.class);
	
	private CellPhoneModule modem;
	
	public CellPhoneAnswerCall(CellPhoneModule modem) {
		this.modem = modem;
		new Thread(this).start();
	}
	
	public void run() {
		
		try {
			
			log.info("inicio de proceso para contestar llamada........");
			
			//contestar la llamada
			GSMModemCommandResponse res = modem.getModem().sendCommand("ATA\r\n",5_000);
			
			//si la respuesta estuvo bien entonces salir
			if( res != null && res.isResponseWithOk() ) 
				return;
			
			//actualizar que ya no se esta realizando la llamada
			modem.getCallStatus().setCallInProgress(false);
			
			if( res == null  || res.getResponse() == null ) {
				log.error("error no se recibio respuesta al intentar contestar llamada");
				return;
			}
			
			if( !res.isResponseComplete() )
				log.error("error respuesta incompleta en comando para contestar llamada");
		
			if( res.isTimeOutReached() ) 
				log.error("error tiempo de espera agotado para contestar llamada");
			
			if( res.getResponse() == null || res.getResponse().indexOf("ERROR") >= 0 ) 
				log.error("error, respuesta con error, no se pudo contestar llamada");
			
		}finally {
			log.info("fin de proceso para contestar llamada........");
		}
	}
}
