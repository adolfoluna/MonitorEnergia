package sme.modem.queue;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.IllegalStateRuntimeException;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSSessionMode;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;

@Stateless
@Remote(MonQueueReaderWriterRemote.class)
public class MonQueueReaderWriterHome implements MonQueueReaderWriterRemote {
	
	private static final Log log = LogFactory.getLog(MonQueueReaderWriterHome.class);
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory") 
	@JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
	private JMSContext context;
	
	@Resource(lookup="java:/jms/queue/sme_monitoreo_respuestas")
	private Queue writeJMSQueue;
	
	@Resource(lookup="java:/jms/queue/sme_monitoreo")
	private Queue readJMSQueue;
	
	
	public MonQueueReaderWriterHome() {
		
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.queue.MonitoreoQueueReaderWriterRemote#write(sme.client.dto.NodoStatusNotification)
	 */
	@Override
	public void write(NodoStatusNotification nodoStatus) {
		try {
			log.info("intentando enviar mensaje sme_monitoreo_respuestas....");
			ObjectMessage om = context.createObjectMessage(nodoStatus);
			context.createProducer().send(writeJMSQueue, om);
			log.info("mensaje enviado exitosamente a sme_monitoreo_respuestas....");
		}catch(Exception ex) {
			log.error("error al intentar enviar mensaje a sme_monitoreo_respuestas");
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.queue.MonitoreoQueueReaderWriterRemote#read()
	 */
	@Override
	public NodoDto read() {
		
		JMSConsumer consumer = null;
		
		try {
			
			consumer = context.createConsumer(readJMSQueue);
			
			Message msg = consumer.receive(100);
			
			if( msg == null ) 
				return null;
			
			if( !(msg instanceof ObjectMessage) ) {
				log.error("error respuesta no es de tipo ObjectMessage");
				return null;
			}
			
			//tomar objeto del mensaje recibido
			Serializable res = ((ObjectMessage) msg).getObject();
			
			//validar que el mensaje recibido no sea null
			if( res == null ) {
				log.error("error mensaje recibido en null");
				return null;
			}
			
			//validar que el mensaje sea de tipo NodoDto
			if( !(res instanceof NodoDto) ) {
				log.error("error, mensaje no es de tipo NodoDto");
				return null;
			}
			
			//regresar respuesta
			return (NodoDto) res;
			
		} catch(IllegalStateRuntimeException ex) {
			log.info("proceso de lectura interrumpido cola sme_monitoreo");
			return null;
		} catch(Exception ex) {
			log.error("error al intentar leer de cola sme_monitoreo "+ex.getMessage());
			ex.printStackTrace();
			return null;
		} finally {
			
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
