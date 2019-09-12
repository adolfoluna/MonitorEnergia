package sme.web.rest.service;

import java.net.InetAddress;


import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import sme.client.dto.StringMessage;

@RequestScoped
@Path("server")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class ServerValueReadRestService {
	
	private static final Log log = LogFactory.getLog(ServerValueReadRestService.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("getvalue")
	public String getValue(StringMessage msg) {
		
		ModelControllerClient client = null;
		
		try {
			
			//intentar crear conexion
			client = ModelControllerClient.Factory.create(InetAddress.getByName("localhost"), 9990);
			
			//convertir de json a objeto de java
			ModelNode address = ModelNode.fromJSONString(msg.getMessage());
			
			//ejecutar el query
			ModelNode res = client.execute(address);
			
			//convertir de objeto a cadena de json
			String aux  = res.toJSONString(true);
			
			//regresar resultado
			return aux;
			
		} catch (Exception e) {
			log.error("error al intentar consultar valor "+e.getMessage());
			e.printStackTrace();
			String error = e.getMessage().replace("\"", "").replace("'", "");
			return "{outcome:\"failed\",failure-description:\""+error+"\"}";
		}finally {
			
			//intentar cerrar cliente
			if( client != null ) {
				try {
					client.close();
				}catch(Exception e) {
					log.error("error al intentar cerrar cliente, "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
}

/*

    	 para ver todos los atributos de un objeto
    	 *  {
         "operation":"read-resource",
         "recursive":"true",
         "include-runtime":"true",
         "json.pretty":1,
         "address": [
  	       		{"subsystem":"messaging-activemq"},
         		{"server":"default"},
         		{"jms-queue":"Send2SACQueueCasino"}
         		]
         
         }
    	 
    	
    	 para leer un atributo
    	 *  {
         "operation":"read-attribute",
         "name":"messages-added",
         "json.pretty":1,
         "address": [
  	       		{"subsystem":"messaging-activemq"},
         		{"server":"default"},
         		{"jms-queue":"Send2SACQueueCasino"}
         		]
         
         }
    	 
	}
 */
