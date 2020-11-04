package sme.modem.cellphone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.GSMModemEventListener;
import sme.modem.gsm.GSMModemWithCommands;

public class CellPhoneModule implements GSMModemEventListener  {

	private static final Log log = LogFactory.getLog(CellPhoneModule.class);
	
	private CellPhoneModuleCallStatus callStatus = new CellPhoneModuleCallStatus(this);
	private boolean sendingSMS = false;
	
	private CellPhoneModuleSMSListener listener;
	private CellPhoneModuleCallStatusListener statusCallListener;
	private CellPhoneModuleToneListener toneListener;
	
	private GSMModemWithCommands modem;
	
	public CellPhoneModule() {
		
	}

	public GSMModemWithCommands getModem() {
		return modem;
	}

	public void setModem(GSMModemWithCommands modem) {
		modem.setGSMModemEventListener(this);
		this.modem = modem;
	}
	
	synchronized public void sendSMS(String phoneNumber,String message) {
		
		//encender bandera de que se esta enviando un mensaje
		sendingSMS = true;
		
		log.info("intentando enviar mensaje a "+phoneNumber+" mensaje:"+message);
		
		if( modem.sendSMS(phoneNumber, message) )
			log.info("mensaje enviado exitosamente a "+phoneNumber);
		else
			log.error("error no se pudo enviar mensaje a "+phoneNumber);
		
		//apagar bandera de que se esta enviando un mensaje
		sendingSMS = false;
	}
	
	public void makeCall(String number) {
		
		log.info("intentando llamar a "+number);
		
		//ocupar la linea
		callStatus.setCallInProgress(true);

		if( !modem.call(number) ) {
			
			log.info("error no se pudo realizar llamada a "+number);
			
			//desocupar linea debido a que no se pudo realizar la llamada
			callStatus.setCallInProgress(false);
		}
		
		log.info("comando de llamada a "+number+" exitoso");
	}
	
	public void hangupCall() {
		new CellPhoneHandUpCall(this,false);
	}
	
	public void sendTone(String toneNumber) {
		//enviar el tono por medio segundo
		modem.sendCommand("AT+VTS="+toneNumber+"\r\n", 3_000);
	}

	@Override
	public void gsmModemEvent(String event) {
		
		int aux = 0;
		
		log.info("evento:"+event.replace("\r\n", "\\n"));
		
		//eliminar las nuevas lineas si es que existen
		event = event.replace("\r\n", " ").trim();
		
		handleCall(event);
		
		//si el listener esta asignado y llego un mensaje, extraer el numero de mensaje que llego
		if( event.startsWith("+CMTI:") ) {
			aux = extractReceivedMessageNumber(event);
			if( aux >= 0 ) {
				new CellPhoneModuleSMSReader(aux,modem,listener);
			}
		}
		
		if( event.startsWith("+UUDTMFD:") && toneListener != null ) {
			String temp = event.replace("+UUDTMFD: ", " ");
			temp = temp.replace('\n', ' ');
			temp = temp.trim();
			toneListener.toneDTMFDetected(temp);			
		}
	}
	
	public CellPhoneModuleCallStatus getCallStatus() {
		return callStatus;
	}
	
	public boolean isSendingSMS() {
		return sendingSMS;
	}

	public CellPhoneModuleSMSListener getListener() {
		return listener;
	}

	public void setListener(CellPhoneModuleSMSListener listener) {
		this.listener = listener;
	}
	
	public void setStatusCallListener(CellPhoneModuleCallStatusListener listener) {
		this.statusCallListener = listener;
	}
	
	public void setToneListener(CellPhoneModuleToneListener listener) {
		this.toneListener = listener;
	}
	

	private int extractReceivedMessageNumber(String text) {
		
		text = text.replace("\r\n", " ").trim();
        final Pattern pattern = Pattern.compile(".*\\+CMTI:\\s\".*\",(\\d+)");
        final Matcher matcher = pattern.matcher(text);

        //si no se encuentra el patron regresar nulo
        if(!matcher.find()) {
        	log.error("error no se encontro el numero de mensaje en el evento:"+text.replace("\r\n", "\\n"));
           return -1;
        }
       
        //extrae el numero de mensaje
        String ex = matcher.group(1);
        
        try {
        	return Integer.parseInt(ex);
        }catch(Exception e) {
        	log.error("error al intentar convertir cadena "+ex+" a numero entero "+e.getMessage());
        	return -1;
        }   
	}
	
	private void handleCall(String event) {
		
		//si el evento no empieza con +UCALlSTAT o RING abandonar rutina
		if( !event.startsWith("+UCALLSTAT:") && !event.startsWith("RING"))
			return; 
		
		//actualizar el estatus con el evento de llamada que llego
		int aux = callStatus.updateStatus(event);
		
		if( statusCallListener != null )
			statusCallListener.callStatusChanged(CellPhoneModuleCallStatusEnum.fromNumber(aux));
		//si el estatus es para contestar
		/*if( aux == 4 ) {
			
			//si no hay llamada en progreso iniciar el proceso de contestar
			if(!getCallStatus().isCallInProgress()) {
				getCallStatus().setCallInProgress(true);
				new CellPhoneAnswerCall(this);
			} else 
				log.info("llamada sin contestar debido a que esta ocupada la linea en otra llamada");
		}*/
		
	}

}
