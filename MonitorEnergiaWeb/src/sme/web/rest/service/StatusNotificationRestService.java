package sme.web.rest.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoStatusNotification;
import sme.web.dto.NetworkNotificationStatusDto;
import sme.web.dto.NotificationResultDto;
import sme.web.queue.MonQueueReaderWriterRemote;

@RequestScoped
@Path("status")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class StatusNotificationRestService {

	private static final Log log = LogFactory.getLog(StatusNotificationRestService.class);
	
	@EJB
	private MonQueueReaderWriterRemote queue;
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2")
	private NodoRemote2 nodoRemote;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("notification")
	public NotificationResultDto listar(NetworkNotificationStatusDto status) {
		log.info("notificacion por red de estatus "+status);
		
		NodoStatusNotification ns = convertObject(status);

		//regresar error si no se pudo determinar la fecha de notificacion
		if( ns.getNotificationDate() == null )
			return new NotificationResultDto(false,"fecha de notificacion no presente o no se pudo determinar");
		
		//regresar error si no se pudo determinar la fecha del evento
		if( ns.getStatusDate() == null )
			return new NotificationResultDto(false,"fecha de evento no presente o no se pudo determinar");
		
		//escribir la instancia generada en la cola
		//queue.write(ns);
		
		//actualizar la fecha de monitoreo del nodo
		//nodoRemote.updateMonitoreoDate(status.getSme());
		
		//actualizar nodo y fecha de monitoreo
		nodoRemote.updateNodoStatus(ns);
			
		//regresar mensaje de exito
		return  new NotificationResultDto(true,null);
	}
	
	private NodoStatusNotification convertObject(NetworkNotificationStatusDto status) {
		
		NodoStatusNotification ns = new NodoStatusNotification();
		
		//actualizar el numero de telefono
		ns.setNumero(status.getSme());		
				
		//extraer los estados de CFE y UPS
		ns.setCfePresente(status.isCfe());
		ns.setUpsPresente(status.isUps());		
		
		//no es necesario notificar por SMS
		ns.setNotificarStatus(false);
		
		//extraer la fecha del mensaje y asignarla al objeto notificacion
		ns.setNotificationDate(convertToDate(status.getFechaMensaje()));
		
		//extraer la fecha en que ocurrio el evento de conexion o desconexion
		ns.setStatusDate(convertToDate(status.getFechaEvento()));
		
		//extraer el numero de mensaje, en caso de no haberlo se asigna el 0
		ns.setNumeroMensaje(0);
		
		//indicar que se agregara el resultado a la cola
		log.info(ns);
		
		return ns;
	}
	
	private static Date convertToDate(String fecha) {
		
		if( fecha.indexOf("-") < 0 )
			return null;
		
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
	
}
