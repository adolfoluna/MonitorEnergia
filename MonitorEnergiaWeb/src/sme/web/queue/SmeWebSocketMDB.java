package sme.web.queue;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import scm.web.servlet.MonitorWebSocketServer;
import sme.client.dto.NodoDto;
import sme.web.dto.NotificationResultDto;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


/**
 * Message-Driven Bean implementation class for: SmeWebSocketMDB
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "java:/jms/queue/sme_websocket"), @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		}, 
		mappedName = "java:/jms/queue/sme_websocket")
public class SmeWebSocketMDB implements MessageListener {

	private static final Log log = LogFactory.getLog(SmeWebSocketMDB.class);
	
	@Resource(name = "ws_consume")
	private boolean wsConsume = false;
	
	@Resource(name="ws_connect_timeout")
	private int wsConnectTimeOut = 15;
	
	@Resource(name="ws_read_timeout")
	private int wsReadTimeOut = 15;
	
	@Resource(name="ws_retries")
	private int wsRetries = 0;
	
	@Resource(name="ws_url")
	private String wsURL = null;
	
    /**
     * Default constructor. 
     */
    public SmeWebSocketMDB() {
       
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {

    	Object o = null;
    	
    	//intentar obtener el objeto del mensaje que acaba de llegar
    	//en caso de no poder, indicar error y salir de rutina
    	try {
			o = ((ObjectMessage) message).getObject();
		} catch (JMSException e) {
			log.error("error al intentar convertir mensaje a Objeto"+e.getMessage());
			e.printStackTrace();
			return;
		}
    	
    	if( !(o instanceof NodoDto) ) {
    		log.error("error objeto no es de tipo NodoDto......."+o.getClass().getName());
    		return;
    	}
    
    	//convertir mensaje a objeto NodoDto
    	NodoDto nodo = (NodoDto) o;
    	
    	//enviar nodo a todos los clientes del websocket
    	MonitorWebSocketServer.sendMessages(nodo.toJSONString());
    	
    	if( wsConsume )
    		consumeRestWebService(nodo);
    }
    
    private void consumeRestWebService(NodoDto nodo) {
    	
    	if( wsURL == null || wsURL.trim().length() <= 0 ) {
    		log.error("error al intenter consumir ws, url en null o en blanco......");
    		return;
    	}
    	
    	//Client client = ClientBuilder.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
    	//WebTarget target = client.target("http://localhost:8080/MonitoreoEnergiaWebRemote/rest/notification_service/test");
    	//String aux = target.request(MediaType.TEXT_PLAIN).get(String.class);

    	Client client = ClientBuilder.newBuilder().connectTimeout(wsConnectTimeOut, TimeUnit.SECONDS).readTimeout(wsReadTimeOut, TimeUnit.SECONDS).build();
    	WebTarget target = client.target(wsURL);
    	NotificationResultDto res = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(nodo, MediaType.APPLICATION_JSON),NotificationResultDto.class);
    	
    	if( res != null && res.isSuccess() )
    		log.info("exitosamente notificado a sistema remoto.....");
    	//log.info(res);
    }

}
