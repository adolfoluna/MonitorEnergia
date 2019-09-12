package sme.ejb.ops;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
//import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;
import sme.client.queue.SmeMonitoreoJMSQueueRemote;

@Startup
@Singleton
public class MonitoreoNodos {

	private static final Log log = LogFactory.getLog(MonitoreoNodos.class);
	
	@Resource(name="limiteNodos")
	private int limiteNodos = 10;
	
	@Resource(name="segundosMonitoreo")
	private int segundosMonitoreo = 60;
	
	@EJB private NodoRemote2 nodor;
	
	@EJB(lookup="java:app/MonitorEnergiaEJB/SmeMonitoreoJMSQueueHome")
	private SmeMonitoreoJMSQueueRemote cola;
	
	public MonitoreoNodos() {
		
	}
			
	//@Schedule(second = "*/30", minute = "*", hour = "*")
	public void monitoreo() throws InterruptedException {
		
		log.info("consultando lista de nodos a monitorear.......");
		
		List<NodoDto> lista = nodor.getNodosMonitoreo(0,limiteNodos,segundosMonitoreo);

		//si la lista esta vacia entonces abandonar rutina
		if( lista == null || lista.size() <= 0 ) {
			log.info("no se encontraron nodos a monitorear");
			return;
		}
		
		log.info("agregando "+lista.size()+" nodos a monitorear..........");
		
		//escribir en cola los nodos que se tienen que monitorear con prioridad 0
		for( NodoDto n : lista ) 
			cola.write(n, 0,segundosMonitoreo*1_000);
		
	}
}
