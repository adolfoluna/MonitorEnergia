package sme.ejb.db.home;

import java.util.Date;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.ejb.db.model.Historial;
import sme.ejb.db.model.HistorialHome;
import sme.ejb.db.model.Nodo;

@Stateless
@Remote(HistorialRemote2.class)
public class HistorialHome2 extends HistorialHome implements HistorialRemote2 {
	
	private static final Log log = LogFactory.getLog(HistorialHome2.class);
	
	/* (non-Javadoc)
	 * @see sme.ejb.db.home.HistorialRemote2#insertHistorial(int, java.util.Date, boolean, boolean)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public void insertHistorial(int idnodo,Date fecha, boolean cfePresente,boolean upsPresente,Date fechaNotificacion) {
	
		try {
			
			Historial h = new Historial();
			
			Nodo nodo = new Nodo();
			nodo.setIdnodo(idnodo);
			
			h.setNodo(nodo);
			h.setFecha(fecha);
			h.setCfePresente(cfePresente);
			h.setUpsPresente(upsPresente);
			h.setFechaNotificacion(fechaNotificacion);
			
			//intentar insertar elemento
			persist(h);
			
		}catch(Exception ex) {
			log.error("error al intentar insertar elemento en la tabla historial "+ex.getMessage());
			ex.printStackTrace();
		}
	}

}
