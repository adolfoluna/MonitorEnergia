package sme.web.queue;

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
  
    }

}
