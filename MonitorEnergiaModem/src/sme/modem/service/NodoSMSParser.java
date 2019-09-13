package sme.modem.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.dto.NodoStatusNotification;

public class NodoSMSParser {
	
	private static final Log log = LogFactory.getLog(NodoSMSParser.class);
	
	public static NodoStatusNotification parse(String numero,String msg) {
		
		NodoStatusNotification ns = new NodoStatusNotification();
		
		//actualizar el numero de telefono
		ns.setNumero(numero);
		
		//extraer los estados de CFE y UPS
		if(msg.indexOf("CFE-P") >= 0) ns.setCfePresente(true);
		if(msg.indexOf("CFE-A") >= 0) ns.setCfePresente(false);
		if(msg.indexOf("UPS-P") >= 0) ns.setUpsPresente(true);		
		if(msg.indexOf("UPS-A") >= 0) ns.setUpsPresente(false);		
		
		//si el mensaje tiene la leyenda AS seguido de uno o mas digitos
		//significa que hay que notificar a el nodo con un mensaje de Recibi
		if( msg.matches(".+MA\\d+.+") || msg.matches(".+LA\\d+.+") ) 
			ns.setNotificarStatus(true);
		
		//extraer la fecha del mensaje y asignarla al objeto notificacion
		ns.setNotificationDate(extractMessageDate(msg));
		
		//extraer la fecha en que ocurrio el evento de conexion o desconexion
		ns.setStatusDate(extractStatusDate(msg));
		
		//extraer el numero de mensaje, en caso de no haberlo se asigna el 0
		ns.setNumeroMensaje(extractMessageNumber(msg));
		
		//si las fechas no se podieron extraer entonces regresar null
		if( ns.getStatusDate() == null || ns.getNotificationDate() == null )
			return null;
		
		//si no se pudo determinar si hubo conexion o desconexion de cfe y ups entonces regresar null
		if( ns.getCfePresente() == null || ns.getUpsPresente() == null )
			return null;
		
		//[19/02/20,11:53:56-32] CFE-P UPS-A 19/02/20,11:50:35-32,AS22
		//indicar que se agregara el resultado a la cola
		log.info(ns);
		
		return ns;
	}
	
	private static Date extractStatusDate(String text) {
		
		final Pattern pattern = Pattern.compile(".+\\s(\\d\\d/\\d\\d/\\d\\d,\\d\\d:\\d\\d:\\d\\d-\\d+)");
		final Matcher matcher = pattern.matcher(text);
		
		//si no se encuentra el patron regresar nulo
		if(!matcher.find()) {
			log.error("error, no se encontro patron de fecha en texto:"+text+" patron:.+\\s(\\d\\d/\\d\\d/\\d\\d,\\d\\d:\\d\\d:\\d\\d-\\d+)");
			return null;
		}
		
		//extrae la fecha 19/01/28,16:44:28-32 de CFE-P UPS-A 19/01/28,16:44:28-32, AS32
		String fecha = matcher.group(1);
		
		//convierte el 32 a 8
		String temp = fecha.substring(fecha.indexOf("-")+1);
		fecha = fecha.substring(0, fecha.indexOf("-"));
		int i = Integer.parseInt(temp);
		i = i /4;
		if(i < 10 ) fecha+= "-0"+i;
		else 	    fecha+= "-"+i;
		//////////////////////////////////
		
		DateFormat format = new SimpleDateFormat("yy/MM/dd,HH:mm:ssX");
		
		try {
			Date date = format.parse(fecha);
			return date;
		} catch (ParseException e) {
			log.error("error al intentar convertir texto:->"+fecha+"<- a fecha "+e.getMessage());
			return null;
		}
	}
	
	private static Date extractMessageDate(String text) {
		
		final Pattern pattern = Pattern.compile(".*\\[(\\d\\d/\\d\\d/\\d\\d,\\d\\d:\\d\\d:\\d\\d-\\d+)\\].*");
		final Matcher matcher = pattern.matcher(text);
		
		//si no se encuentra el patron regresar nulo
		if(!matcher.find()) {
			log.error("error, no se encontro patron de fecha en texto:"+text+" patron:.*\\\\[(\\\\d\\\\d/\\\\d\\\\d/\\\\d\\\\d,\\\\d\\\\d:\\\\d\\\\d:\\\\d\\\\d-\\\\d+)\\\\].*");
			return null;
		}
		
		//extrae la fecha del mensaje que esta entre corchetes
		//[19/02/15,18:25:36-32] CFE-A UPS-A 19/02/11,18:19:43-32,AS32
		String fecha = matcher.group(1);
		
		//convierte el 32 a 8
		String temp = fecha.substring(fecha.indexOf("-")+1);
		fecha = fecha.substring(0, fecha.indexOf("-"));
		int i = Integer.parseInt(temp);
		i = i /4;
		if(i < 10 ) fecha+= "-0"+i;
		else 	    fecha+= "-"+i;
		//////////////////////////////////
		
		DateFormat format = new SimpleDateFormat("yy/MM/dd,HH:mm:ssX");
		
		try {
			Date date = format.parse(fecha);
			return date;
		} catch (ParseException e) {
			log.error("error al intentar convertir texto:->"+fecha+"<- a fecha "+e.getMessage());
			return null;
		}
	}
	
	private static int extractMessageNumber(String text) {
		
        final Pattern pattern = Pattern.compile("(.+),(\\d[A-Z][A-Z])(\\d\\d)");
        final Matcher matcher = pattern.matcher(text);
	
		//si no se encuentra el patron regresar nulo
        if( !matcher.find() || matcher.groupCount() < 3 ) {
            log.info("no se encontro numero de mensaje, poniendo el 0.............");
            return 0;
        }
        
        //el grupo 3 es el que tiene el numero de mensaje, intentar convertirlo a numero entero
        try {
        	return Integer.valueOf(matcher.group(3));
        }catch(NumberFormatException ex) {
        	log.error("error al intentar convertir el texto \""+matcher.group(3)+"\" a numero entero");
        }
        
        //salir regresando 0
		return 0;
	}
	

}
