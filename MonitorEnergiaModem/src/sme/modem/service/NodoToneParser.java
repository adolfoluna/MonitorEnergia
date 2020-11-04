package sme.modem.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;

public class NodoToneParser {
	
	private static final Log log = LogFactory.getLog(NodoToneParser.class);
	
	public static NodoStatusNotification parse(NodoDto nodo,String respuesta) {
		
		//ejemplo "2011040003042810"
		// 2020-11-04 00:03:04-7 cfe=presente ups=ausente
		//el timezone -7 se saca de 28/4
		
		log.info("analizando respuesta "+respuesta+" de nodo "+nodo.getNumero());
		
		if( respuesta.length() < 16 ) {
			log.info("respuesta debe ser de minimo 16 caracteres, abortando analisis de respuesta....");
			return null;
		}
		
		String fecha = respuesta.substring(0,12);
		
		//esta parte extrae el timezone
		try {
			int timezone = Integer.parseInt(respuesta.substring(12,14)) / 4;
			if( timezone < 10 ) fecha+="-0"+timezone;
			else    fecha="-"+timezone;
		}catch(Exception ex) {
			log.error("error al intentar convertir "+respuesta.substring(12,14)+" a entero");
			ex.printStackTrace();
			return null;
		}
		////////////////
		
		DateFormat format = new SimpleDateFormat("yyMMddHHmmssX");
		Date date = null;
		try {
			date = format.parse(fecha);
		} catch (ParseException e) {
			log.error("error al intentar convertir texto:->"+fecha+"<- a fecha "+e.getMessage());
			return null;
		}
		
		String cfe = respuesta.substring(respuesta.length()-2,respuesta.length()-1);
		String ups = respuesta.substring(respuesta.length()-1);
		
		NodoStatusNotification res = new NodoStatusNotification();
		
		if( cfe.equals("1"))
			res.setCfePresente(true);
		else
			res.setCfePresente(false);
		
		res.setNotificarStatus(false);
		res.setNotificationDate(new Date());
		res.setNumero(nodo.getNumero());
		res.setNumeroMensaje(0);
		res.setStatusDate(date);
		
		if( ups.equals("1") )
			res.setUpsPresente(true);
		else
			res.setUpsPresente(false);
		
		return res;
	}

}
