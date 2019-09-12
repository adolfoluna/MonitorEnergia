package sme.web.rest.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fazecast.jSerialComm.SerialPort;

@RequestScoped
@Path("puertoserial")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class PuertoSerialRestService {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("listar")
	public String[] listarPuertosSeriales() {

		//toma la lista de puertos seriales disponibles en la maquina local
		SerialPort r[] = SerialPort.getCommPorts();
		
		//si la lista esta vacia regresar null
		if( r == null )
			return null;
		
		//crear arreglo de cadenas con la cantidad de puertos seriales encontrados
		String []res = new String[r.length];
		
		int index = 0;
		
		//recorrer todos los puertos seriales encontrador y guardar el nombre
		for(SerialPort p : r ) {
			res[index++] = p.getSystemPortName();
		}
		
		//regreasar todos los puertos encontrados
		return res;
	}

}
