package sme.ejb.queue;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoStatusNotification;

/**
 * Message-Driven Bean implementation class for: ModemLocalReaderMDB
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "java:/jms/queue/sme_monitoreo_respuestas"), @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		}, 
		mappedName = "java:/jms/queue/sme_monitoreo_respuestas")
public class SMEMonitoreoRespuestasMDB implements MessageListener {

	
	private static final Log log = LogFactory.getLog(SMEMonitoreoRespuestasMDB.class);
	
	@EJB
	private NodoRemote2 nodoremote;
	
	@Resource(name="actualizar_nodos_sin_fecha")
	private boolean actualizarNodosSinFecha = false;
	
    /**
     * Default constructor. 
     */
    public SMEMonitoreoRespuestasMDB() {
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
    	
    	//verificar que el objeto recibido es de tipo NodoDto
    	if( !(o instanceof NodoStatusNotification) ) {
    		log.error("error, mensaje no es de tipo NodoStatusNotification");
    		return;
    	}
    	
    	//convertir objeto a NodoDto
    	NodoStatusNotification nodo = (NodoStatusNotification) o;
    	
    	if( !actualizarNodosSinFecha && nodo.getStatusDate() == null) {
    		log.error("error, nodo no tiene fecha, descartando mensaje "+nodo);
    		return;
    	}
    	
    	if(nodo.getStatusDate() == null) {
    		nodo.setStatusDate(new Date());
    		log.info("fecha en nulo actualizando fecha "+nodo);
    	}
    	
    	//validar que el numero este correcto
    	if(nodo.getNumero() == null) {
    		log.error("error, numero en null, descartando mensaje "+nodo);
    		return;
    	}
    	
    	//validar que los status no esten en nulo
    	if(nodo.getCfePresente() == null || nodo.getUpsPresente() == null ) {
    		log.error("error, estatus de CFE o UPS en null, descartando mensaje "+nodo);
    		return;
    	}
    	
    	//si el numero tiene mas de 10 digitos, eliminar los primeros digitos
    	//que corresponden a la clave del pais
    	if( nodo.getNumero().length() > 10 ) {
    		int index = nodo.getNumero().length() - 10;
    		if( index >= 0 )
    			nodo.setNumero(nodo.getNumero().substring(index));
    	}
    	
    	//actualizar en la base de datos las variables de monitoreo
        if( nodoremote.updateNodoStatus(nodo,true) ) 
        	log.info("nodo exitosamente actualizado, "+nodo);
        else
        	log.error("error, al intentar actualizar el status de nodo, "+nodo);
        
    }

}
