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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.ModemLocalRemote2;
import sme.client.dto.ModemLocalDto;

@RequestScoped
@Path("modemlocal")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ModemLocalRestService {
	
	@EJB
	private ModemLocalRemote2 mr;
	
	private static final Log log = LogFactory.getLog(ModemLocalRestService.class);
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("listar/{limite}/{pagina}")
	public List<ModemLocalDto> listar(@PathParam("limite") int limit,@PathParam("pagina") int pagina) {
		return mr.getModemList(limit,pagina);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("buscar/{idmodem}")
	public ModemLocalDto findbyid(@PathParam("idmodem") int idmodem) {
		return mr.findByIdDto(idmodem);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("editar")
	public String edit(ModemLocalDto modem) {
		
		try {
			mr.mergeDto(modem);
		}catch(Exception ex) {
			log.error("error al intentar editar modemlocal, "+ex.getMessage());
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return "success";
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("insertar")
	public String insert(ModemLocalDto modem) {
		int aux = 0;
		try {
			aux = mr.insertDto(modem);
		}catch(Exception ex) {
			log.error("error al intentar insertar modemlocal, "+ex.getMessage());
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return "success "+aux;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("eliminar/{idmodem}")
	public String remove(@PathParam("idmodem") int idmodem) {
		
		try {
			mr.remove(idmodem);
		}catch(Exception ex) {
			log.error("error al intentar eliminar modemlocal con idmodem:"+idmodem+" "+ex.getMessage());
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return "success";
	}
	

}
