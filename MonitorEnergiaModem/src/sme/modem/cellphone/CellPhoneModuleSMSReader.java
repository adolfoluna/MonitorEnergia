package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.GSMModemCommandResponse;
import sme.modem.gsm.GSMModemWithCommands;

public class CellPhoneModuleSMSReader implements Runnable {

	private static final Log log = LogFactory.getLog(CellPhoneModuleSMSReader.class);
	
	private int messageNumber = 0;
	private GSMModemWithCommands modem;
	private CellPhoneModuleSMSListener listener;
	
	private String message;
	private String phoneNumber;
	
	public CellPhoneModuleSMSReader(int messageNumber,GSMModemWithCommands modem,CellPhoneModuleSMSListener listener) {
		this.messageNumber = messageNumber;
		this.modem = modem;
		this.listener = listener;
		new Thread(this).start();
	}
	
	public void run() {
		
		try {
			
			log.info("inicio de proceso para leer SMS.......");
			
			//intentar leer mensaje
			String aux = readSMS();
			
			//en caso de no haber podido leer mensaje salir
			if( aux == null || aux.length() <= 0 )
				return;
			
			//intentar 3 veces borrar el mensaje
			deleteSMS();
			
			//intentar extraer el numero de telefono y el mensaje
			extractPhoneNumber(aux);
			
			//si no se pudo extraer el mensaje salir
			if( message == null || phoneNumber == null)
				return;
			
			//llamar al evento de que llego un mensaje
			if( listener != null ) listener.cellPhoneSMS(phoneNumber,message);
			
		}finally {
			log.info("fin de proceso para leer SMS.......");
		}
		
	}
	
	private String readSMS() {
		
		String comando = "AT+CMGR="+messageNumber+"\r\n";
		
		GSMModemCommandResponse res = null; 
		
		for(int i = 0; i < 3; i++ ) {
			
			if( i > 0 )
				log.info("reintentando leer mensaje sms con registro "+messageNumber);
			
			res =  modem.sendCommand(comando,5_000);
			
			if( res != null && res.isResponseComplete() )
				break;
			
			log.error("error, el enviar comando para leer registro de SMS "+messageNumber+" respuesta incompleta o en null");
			
		}
		
		if( res == null || !res.isResponseComplete() )
			return null;
		
		return res.getResponse();
	}
	
	private void extractPhoneNumber(String text) {
		
		message = null;
		phoneNumber = null;
		
		if( text == null ) {
			log.info("sms en null o vacio");
			return;
		}
		
		//quitar los caracteres de nueva linea
		text = text.replace("\r\n", " ").trim();
		
		//"\n+CMGR: \"REC READ\",\"+526641518045\",,\"19/01/16,18:54:30-50\"\r\ntest\r\nOK";
		//verificar que la respuesta cumpla con el formato
		
		//en caso de no cumplirlo salir de rutina
		if( !text.matches("\\+CMGR: \\\".+\\\",\\\".+\\\" .+") ) {
			log.error("error al intentar leer mensaje, no cumple con el formato sms:"+text+" ejemplo:+CMGR: \"REC READ\",\"+526641234567\",,\"19/01/16,18:54:30-50\" test OK");
			return;
		}
		
		//actualizar el mensaje que se va a enviar
		message = text;
		
		//extraer el numero telefonico de donde esta llegando el mensaje de texto
		phoneNumber = text.replaceFirst("\\+CMGR: \\\".+\\\",\\\"", "");
		phoneNumber = phoneNumber.replaceFirst("\\\",.+", "");
		
		//indicar el mensaje que llego
		log.info("numero:"+phoneNumber+" mensaje:"+text);
	}
	
	private void deleteSMS() {
		
		log.info("intentando borrar mensaje....");
		
		if( modem.deleteSMS(messageNumber) ) return;
		
		log.info("error, reintentando borrar mensaje....");
		
		if( modem.deleteSMS(messageNumber) ) return;
		
		log.info("error, reintentando borrar mensaje....");
		
		modem.deleteSMS(messageNumber);
		
	}
}
