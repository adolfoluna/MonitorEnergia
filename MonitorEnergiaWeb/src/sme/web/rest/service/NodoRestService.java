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

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;

@RequestScoped
@Path("nodo")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class NodoRestService {
	
	@EJB
	private NodoRemote2 nodor;
	
	private static final Log log = LogFactory.getLog(NodoRestService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("buscar/{idnodo}")
	public NodoDto buscarNodo(@PathParam("idnodo") int idnodo) {
		return nodor.findByIdDto(idnodo);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("buscar/numero/{number}")
	public NodoDto buscarNodo(@PathParam("number") String number) {
		return nodor.findByNumberDto(number);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("listar/{limite}/{pagina}")
	public List<NodoDto> listar(@PathParam("limite") int limit,@PathParam("pagina") int pagina) {
		return nodor.getNodos(pagina, limit);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("editar")
	public String edit(NodoDto nodo) {
		
		try {
			nodor.mergeDto(nodo);
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
	public String insert(NodoDto nodo) {
		int aux = 0;
		try {
			aux = nodor.insertDto(nodo);
		}catch(Exception ex) {
			log.error("error al intentar insertar modemlocal, "+ex.getMessage());
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return "success "+aux;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("eliminar/{idnodo}")
	public String remove(@PathParam("idnodo") int idnodo) {
		
		try {
			nodor.removeById(idnodo);
		}catch(Exception ex) {
			log.error("error al intentar eliminar nodo con idnodo:"+idnodo+" "+ex.getMessage());
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return "success";
	}
	

}
