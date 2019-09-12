package sme.client.queue;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserJMSQueue {
	
	private String jmsQueueName;
	private Queue jmsQueue;
	
	private Session session;
	private MessageProducer sender;
	
	private static final Log log = LogFactory.getLog(UserJMSQueue.class);
			
	public UserJMSQueue(String jmsQueueName) {
		this.jmsQueueName = jmsQueueName;
		connect();
		createProducer();
	}
	
	public void setTimeToLive(long time) {
		
		if(sender == null )
			createProducer();
		
		if( sender == null ) {
			log.error("error, metodo setTimeToLive no se pudo ejecutar debido a sender=null");
			return;
		}
		
		try {
			sender.setTimeToLive(time);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void createProducer() {
		try {
			sender = session.createProducer(jmsQueue);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() {
		
		try {

			//crear contexto inicial
			final InitialContext ctx = new InitialContext();
			
			//buscar la cola
			jmsQueue = (Queue) ctx.lookup(jmsQueueName);
			
			
			//crear objeto para conectarse a una cola
			QueueConnectionFactory cf = (QueueConnectionFactory) ctx.lookup("/ConnectionFactory");

			//crear conexion
			Connection con = cf.createConnection();
			
			session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			con.start();
			
			
		} catch (JMSException jme) {
			log.error("error de jms, "+jme.getMessage(),jme);
			jme.printStackTrace();
		} catch (NamingException e) {
			log.error("error, cola JMS no encontrada \""+jmsQueueName+"\"",e);
			e.printStackTrace();
		}
		
	}
	
	public void write(Serializable object) {
		write(object,null,0);
	}
	
	public void write(Serializable object,int priority) {
		write(object, null,priority);
	}
	
	public void write(Serializable object,String filtro,int priority) {
		
		ObjectMessage emens = null;
		
		String mensaje = "cola:"+jmsQueueName;
		
		if( filtro != null )
			mensaje+=" filtro:"+filtro;
		
		if( sender == null ) {
			log.error("error, requerimiento no enviado, sender en nulo "+mensaje);
			return;
		}
		
		//intentar crear ObjectMessage
		try {
			emens = session.createObjectMessage(object);
		} catch (JMSException e) {
			log.error("error, no se pudo crear objectMessage "+mensaje);
			e.printStackTrace();
			return;
		}
		//////////////////////////////
		
		//intentar enviar mensaje
		try {
			sender.setPriority(priority);
			sender.send(emens);
		} catch (JMSException e) {
			log.error( "error al intentar enviar requerimiento "+mensaje);
			e.printStackTrace();
		}
		////////////////////////////////////
	}
	
	public Serializable read() {
		return read(null,0);
	}
	
	public Serializable read(String filtro) {
		return read(filtro,0);
	}
	
	public Serializable read(String filtro,long timeoutms) {
		
		MessageConsumer consumer = null;
		
		String mensaje = null;
		
		if( filtro != null )
			mensaje=" cola:"+jmsQueueName+" filtro:"+filtro;
		else
			mensaje=" cola:"+jmsQueueName;
		
		//intentar crear consumer
		//////////////////////////////////////////////////
		try {
		
			//crear consumer
			if( filtro != null )
				consumer = session.createConsumer(jmsQueue,filtro);
			else
				consumer = session.createConsumer(jmsQueue);
				
		
		} catch (JMSException e1) {

			//indicar error
			log.error("error al intentar crear consumer "+mensaje);
			
			//imprimir stacktrace
			e1.printStackTrace();
			
			//abandonar rutina
			return null;
			
		}
		///////////////////////////////////////////////
		
		try {
			
			//referencia al mensaje recibido
			Message recvMensaje = null;
			
			//esperar a recibir el mensaje
			if( timeoutms > 0 )
				recvMensaje = consumer.receive(timeoutms);
			else
				recvMensaje = consumer.receive();
			
			
			//si no se recibio respuesta, abandonar rutina
			if( recvMensaje == null ) {
				return null;
			}
			
			//si la respuesta no es de tipo ObjectMessage, registrar error y abandonar rutina
			if( !(recvMensaje instanceof ObjectMessage) ) {
				log.error("error no respuesta no es de tipo ObjectMessage "+mensaje);
				return null;
			}
			
			//regresar respuesta
			return ((ObjectMessage) recvMensaje).getObject();
			
		} catch (JMSException e1) {
			
			//si hubo error, indicarlo y regresar null
			log.error("error al intentar recibir mensaje "+e1.getMessage()+mensaje);
			e1.printStackTrace();
			return null;
			
		} finally {
			
			//intentar cerrar consumer si esta creados
			try { if(consumer != null )	consumer.close();} catch (JMSException e) {	e.printStackTrace(); }
			
		}
		
	}
	
	
	public String getQueueName() {
		return jmsQueueName;
	}

}
