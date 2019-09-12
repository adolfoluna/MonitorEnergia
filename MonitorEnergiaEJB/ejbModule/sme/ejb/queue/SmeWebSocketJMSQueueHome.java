package sme.ejb.queue;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSSessionMode;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.queue.SmeWebSocketJMSQueueRemote;

@Stateless
@Remote(SmeWebSocketJMSQueueRemote.class)
public class SmeWebSocketJMSQueueHome implements SmeWebSocketJMSQueueRemote {
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory") 
	@JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
	private JMSContext context;
	
	@Resource(lookup="java:/jms/queue/sme_websocket")
	private Queue jmsqueue;
	
	private static final Log log = LogFactory.getLog(SmeWebSocketJMSQueueHome.class);
	
	public SmeWebSocketJMSQueueHome() {
		
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.queue.SmeWebSocketJMSQueueRemote#write(java.io.Serializable)
	 */
	@Override
	public void write(Serializable object) {
		try {
			log.info("enviando mensaje a sme_websocket.....");
			context.createProducer().send(jmsqueue, context.createObjectMessage(object));
			log.info("mensaje a sme_websocket exitosamente enviado....");
		}catch(Exception ex) {
			log.error("error al intentar enviar mensaje a sme_websocket "+ex.getMessage());
			ex.printStackTrace();
		}
	}

}
