package sme.modem.gsm;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GSMModemWithCommands extends GSMModem {

	private static final Log log = LogFactory.getLog(GSMModemWithCommands.class);
	
	public GSMModemWithCommands() {
		
	}
	
	public boolean sendSMS(String number,String msg) {
		
		String comando1 = "AT+CMGS=\""+number+"\"\r\n";
		
		//enviar comando para enviar sms
		GSMModemCommandResponse aux = sendCommand(comando1.getBytes(),3_000);
		
		//si hay un error en la respuesta salir de rutina
		if( !isResponseOk(aux) ) {
			log.error("error, no se pudo enviar mensaje a "+number+" msg:"+msg);
			return false;
		}
		
		//convertir el mensaje a un arrglo de bytes con un byte extra
		byte []comando2 = Arrays.copyOf(msg.getBytes(), msg.getBytes().length+1);
		
		//asignar al byte extra el valor ctr+z (codigo 0x1A)
		comando2[comando2.length-1] = (byte) 0x1A & (byte) 0xFF;
		
		//enviar al puerto serial el mensaje
		aux = sendCommand(comando2,10_000);
		
		if( !isResponseOk(aux) ) {
			log.error("error, no se pudo enviar mensaje a "+number+" msg:"+msg);
			return false;
		}
		
		int msgNumber = extractSentMessageNumber(aux.getResponse());
		
		if( msgNumber < 0 ) {
			log.info("no se pudo extraer el numero de mensaje sms enviado");
			return true;
		}
		
		//intentar borrar el mensaje
		deleteSMS(msgNumber);
		
		//regresar true indicando que se pudo enviar correctamente el mensaje
		return true;
	}
	
	public boolean deleteSMS(int messageNumber) {
		
		log.info("intentando borrar mensaje numero:"+messageNumber);
		
		GSMModemCommandResponse res = sendCommand("AT+CMGD="+messageNumber+"\r\n",3_000);
		
		if( res.isResponseComplete() && !res.isTimeOutReached() && res.getResponse() != null && res.getResponse().indexOf("OK") >= 0 ) {
			log.info("mensaje numero:"+messageNumber+" exitosamente borrado");
			return true;
		}
		
		log.error("error no se pudo borrar mensaje numero:"+messageNumber);
		
		return false;
	}
	
	public boolean call(String number) {
		
		String command = "ATD"+number+";\r\n";
		
		GSMModemCommandResponse res = sendCommand(command,15_000);
		
		//en caso de no haber llegado respuesta intentar cancelar llamada
		if( res == null || res.isTimeOutReached() ) {
			log.info("intentando cancelar comando de llamada.....");
			res = sendCommand("\r\n",1_000);
			return false;
		}
		
		if( isResponseOk(res) )
			return true;
		else
			return false;
	}
	
	private boolean isResponseOk(GSMModemCommandResponse r) {
		
		if( r == null || !r.isResponseComplete() || r.isTimeOutReached() || r.getResponse().replace("\r\n", "").trim().equals("ERROR") )
			return false;
		
		return true;
	
	}
	
	public int extractSentMessageNumber(String text) {
		
		text = text.replace("\r\n", " ").trim();
        final Pattern pattern = Pattern.compile(".*\\+CMGS:\\s(\\d+).*");
        final Matcher matcher = pattern.matcher(text);

        //si no se encuentra el patron regresar nulo
        if(!matcher.find()) 
           return -1;
       
        //extrae el numero de mensaje
        String ex = matcher.group(1);
        
        try {
        	return Integer.parseInt(ex);
        }catch(Exception e) {
        	log.error("error al intentar convertir cadena "+ex+" a numero entero "+e.getMessage());
        	return -1;
        }   
	}
}
