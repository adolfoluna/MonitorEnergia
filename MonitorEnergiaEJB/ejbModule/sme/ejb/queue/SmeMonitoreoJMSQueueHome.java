package sme.ejb.queue;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSSessionMode;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.queue.SmeMonitoreoJMSQueueRemote;

@Stateless
@Remote(SmeMonitoreoJMSQueueRemote.class)
public class SmeMonitoreoJMSQueueHome implements SmeMonitoreoJMSQueueRemote {
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory") 
	@JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
	private JMSContext context;
	
	@Resource(lookup="java:/jms/queue/sme_monitoreo")
	private Queue jmsqueue;
	
	private static final Log log = LogFactory.getLog(SmeMonitoreoJMSQueueHome.class);
	
	public SmeMonitoreoJMSQueueHome() {
		
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.queue.SmeMonitoreoJMSQueueRemote#write(java.io.Serializable, int)
	 */
	@Override
	public void write(Serializable object,int priority) {
		try {
			log.info("enviando mensaje a sme_monitoreo.....");
			context.createProducer().setPriority(priority).send(jmsqueue, context.createObjectMessage(object));
			log.info("mensaje a sme_monitoreo exitosamente enviado....");
		}catch(Exception ex) {
			log.error("error al intentar enviar mensaje a sme_monitoreo "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void write(Serializable object,int priority,long timetolive) {
		try {
			log.info("enviando mensaje a sme_monitoreo.....");
			context.createProducer().setPriority(priority).setTimeToLive(timetolive).send(jmsqueue, context.createObjectMessage(object));
			log.info("mensaje a sme_monitoreo exitosamente enviado....");
		}catch(Exception ex) {
			log.error("error al intentar enviar mensaje a sme_monitoreo "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.queue.SmeMonitoreoJMSQueueRemote#read(long)
	 */
	@Override
	public Serializable read(long timeout) {

		JMSConsumer consumer =null;
		
		try {
			
			//intentar crear el consumidor del mensaje
			consumer = context.createConsumer(jmsqueue);
			
			//intentar recibir el mensaje
			Message msg = consumer.receive(timeout);
			
			//si no se recibio respuesta regresar null
			if( msg == null ) 
				return null;
			
			//si la respuesta no es de tipo ObjectMessage, registrar error y abandonar rutina
			if( !(msg instanceof ObjectMessage) ) {
				log.error("error respuesta no es de tipo ObjectMessage");
				return null;
			}
			
			//tomar objeto del mensaje recibido
			Serializable res = ((ObjectMessage) msg).getObject();
			
			return res;
			
		}catch(Exception ex) {
			log.error("error al intentar leer mensaje de cola "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}finally {
			if( consumer != null ) {
				try {
					consumer.close();
				}catch(Exception ex) {
					log.error("error al intentar cerrar el consumer "+ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

}
