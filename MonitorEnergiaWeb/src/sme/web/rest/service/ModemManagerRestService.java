package sme.web.rest.service;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sme.client.db.remote.ModemServiceManagerRemote;
import sme.client.dto.ModemLocalDto;
import sme.web.dto.SendModemCommandDto;

@RequestScoped
@Path("modemmanager")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ModemManagerRestService {

	
	@EJB(lookup="java:global/MonitorEnergiaModemApp/MonitorEnergiaModem/ModemServiceManagerHome")
	private ModemServiceManagerRemote mr;
	
	public ModemManagerRestService() {
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("list")
	public List<ModemLocalDto> listServices() {
		return mr.getServicios();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("start")
	public String start() {
		
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		mr.start();
		
		return "sart all invoked";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stop")
	public String stop() {
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		mr.stop();
		
		return "stop all invoked";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("startmodem/{idmodem}")
	public String startModem(@PathParam("idmodem") int idmodem) {
		
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		mr.startModem(idmodem);
		
		return "start modem "+idmodem;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stopmodem/{idmodem}")
	public String stopModem(@PathParam("idmodem") int idmodem) {
		
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		mr.stopModem(idmodem);
		
		return "stop modem "+idmodem;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stopinactive")
	public String startModem() {
		
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		mr.stopInactive();
		
		return "stop inactive invoked";
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sendcommand")
	public String sendcomand(SendModemCommandDto comando) {
		
		if( mr == null ) 
			return "error, ModemServiceManagerHome no encontrado";
				
		return mr.sendCommand(comando.getIdmodem(), comando.getCommand(), comando.getTimeout());
	}
}
