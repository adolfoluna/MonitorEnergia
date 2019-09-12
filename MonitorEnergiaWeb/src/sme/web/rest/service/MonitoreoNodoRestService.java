package sme.web.rest.service;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;
import sme.client.queue.SmeMonitoreoJMSQueueRemote;
import sme.web.dto.ResultadoMonitoreoNodo;

@RequestScoped
@Path("")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class MonitoreoNodoRestService {
	
	private static final Log log = LogFactory.getLog(MonitoreoNodoRestService.class);
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/SmeMonitoreoJMSQueueHome")
	private SmeMonitoreoJMSQueueRemote colaNodoMonitoreo;
	
	@EJB
	private NodoRemote2 nodor;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("monitorearnodo/{idnodo}")
	public ResultadoMonitoreoNodo monitorearNodo(
			@PathParam("idnodo") int idnodo
			) {
		
		
		log.info("monitorear nodo:"+idnodo);
		
		//buscar la entidad en la tabla Nodo
		NodoDto dto = nodor.findByIdDto(idnodo);
		
		//si el resultado en nulo regresar error
		if( dto == null ) {
			log.info("nodo "+idnodo+" no encontrado");
			return new ResultadoMonitoreoNodo(false, idnodo, "Nodo no encontrado");
		}
		
		//indicar que se tiene que llamar al numero
		dto.setLlamar(true);
		
		//insertar elemento en cola con prioridad 1
		colaNodoMonitoreo.write(dto, 1);
		
		log.info("nodo "+idnodo+" agregado en cola con prioridad 1");
		
		//regresar que se inserto elemento exitosamente
		return  new ResultadoMonitoreoNodo(true, dto.getIdnodo(),null);
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("monitorearnodo_sms/{idnodo}")
	public ResultadoMonitoreoNodo monitorearNodoSMS(
			@PathParam("idnodo") int idnodo
			) {
		
		
		log.info("monitorear nodo:"+idnodo);
		
		//buscar la entidad en la tabla Nodo
		NodoDto dto = nodor.findByIdDto(idnodo);
		
		//si el resultado en nulo regresar error
		if( dto == null ) {
			log.info("nodo "+idnodo+" no encontrado");
			return new ResultadoMonitoreoNodo(false, idnodo, "Nodo no encontrado");
		}
		
		//indicar que se tiene que enviar un SMS
		dto.setLlamar(false);
		
		//insertar elemento en cola con prioridad 1
		colaNodoMonitoreo.write(dto, 1);
		
		log.info("nodo "+idnodo+" agregado en cola con prioridad 1");
		
		//regresar que se inserto elemento exitosamente
		return  new ResultadoMonitoreoNodo(true, dto.getIdnodo(),null);
	}
}
