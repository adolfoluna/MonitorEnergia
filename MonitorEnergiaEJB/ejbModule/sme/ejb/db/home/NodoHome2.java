package sme.ejb.db.home;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;
import sme.client.queue.SmeWebSocketJMSQueueRemote;
import sme.ejb.db.model.Nodo;
import sme.ejb.db.model.NodoHome;

@Stateless
@Remote(NodoRemote2.class)
public class NodoHome2 extends NodoHome implements NodoRemote2 {
	
	private static final Log log = LogFactory.getLog(NodoHome2.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@EJB private HistorialRemote2 historialRemote;
	
	@EJB private SmeWebSocketJMSQueueRemote websocketqueueRemote;
	
	public NodoHome2() {
		
	}
	
	public NodoDto findByIdDto(int idnodo) {
		
		try {
			//public NodoDto(int idnodo, String nombre, String domicilio, String numero, Boolean cfePresente, Boolean upsPresente,
			//Date fechaStatus, Date fechaMonitoreo, boolean activo, int version)
			//toma todos los nodos activos
			Query q = entityManager.createQuery(getNodoDtoQuery() +" where n.idnodo=:idnodo");
			
			q.setParameter("idnodo", idnodo);
			
			NodoDto nodo = (NodoDto) q.getSingleResult();
			
			return nodo;
			
		}catch(NoResultException ex) {
			log.info("no se encontro nodo con idnodo:"+idnodo);
		} catch(Exception ex) {
			log.error("error al intentar consultar nodo a monitorear, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
	
		return null;
	}
	
	public NodoDto findByNumberDto(String number) {
		
		try {
			//public NodoDto(int idnodo, String nombre, String domicilio, String numero, Boolean cfePresente, Boolean upsPresente,
			//Date fechaStatus, Date fechaMonitoreo, boolean activo, int version)
			//toma todos los nodos activos
			Query q = entityManager.createQuery(getNodoDtoQuery() +" where n.numero=:num");
			
			q.setParameter("num", number);
			
			NodoDto nodo = (NodoDto) q.getSingleResult();
			
			return nodo;
			
		}catch(NoResultException ex) {
			log.info("no se encontro nodo con numero:"+number);
		} catch(Exception ex) {
			log.error("error al intentar consultar nodo a monitorear, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
	
		return null;
	}
	
	public Nodo findByNumber(String number) {
		
		try {
			
			//buscar el nodo por numero
			Query q = entityManager.createQuery("from Nodo n where n.numero=:num");
			
			//especificar el numero a buscar
			q.setParameter("num", number);
			
			//intentar obtener resultado
			Nodo nodo = (Nodo) q.getSingleResult();
			
			//regresar resultado
			return nodo;
			
		}catch(NoResultException ex) {
			log.info("no se encontro nodo con numero:"+number);
		} catch(Exception ex) {
			log.error("error al intentar consultar nodo, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public boolean updateNodoStatus(NodoStatusNotification aux,boolean soloStatusMasActual) {
		
		if( aux.getNumero() == null) {
			log.error("error, numero en nulo, descartando actualizacion de status");
			return false;
		}
		
		//buscar el nodo por numero de cel
		Nodo nodo  = findByNumber(aux.getNumero());
		
		//si no se encontro la entidad, salir
		if( nodo == null ) {
			log.info("nodo con numero "+aux.getNumero()+" no encontrado, descartando actualizacion de status");
			return false;
		}
		
		if( aux.getCfePresente() != null && aux.getUpsPresente() != null )
			historialRemote.insertHistorial(nodo.getIdnodo(), aux.getStatusDate(), aux.getCfePresente(), aux.getUpsPresente(),aux.getNotificationDate());
		
		if( soloStatusMasActual && aux.getStatusDate() == null ) {
			log.info("nodo sin fecha de estatus especificada, descartando actualizacion de status");
			return false;
		}
		
		if( soloStatusMasActual && nodo.getFechaNotificacion() !=null && nodo.getFechaNotificacion().getTime() >= aux.getNotificationDate().getTime() ) {
			log.info("nodo no actualizado debido a que la fecha en la base de datos es mas actual o igual a la fecha del mensaje recibido, descartando actualizacion de status");
			return false;
		}
			
		//actualizar los campos correspondientes al status del nodo
		nodo.setFechaNotificacion(aux.getNotificationDate());
		
		//si el estatus de CFE cambio, actualizar la fecha en que cambio el status
		if( nodo.getCfePresente() == null || nodo.getCfePresente().booleanValue() != aux.getCfePresente().booleanValue()) 
			nodo.setCfeFecha(aux.getStatusDate());
		
		//si el estatus de UPS cambio, actualizar la fecha en que cambio el status
		if( nodo.getUpsPresente() == null || nodo.getUpsPresente().booleanValue() != aux.getUpsPresente().booleanValue() )
			nodo.setUpsFecha(aux.getStatusDate());
		
		nodo.setCfePresente(aux.getCfePresente());
		nodo.setUpsPresente(aux.getUpsPresente());
		
		//enviar a la cola de websocket el nodo
		sendWebSocket(nodo);
		
		try {
			//actualizar los campos en la base de datos
			entityManager.persist(nodo);
		}catch(Exception ex) {
			log.error("error al intentar actualizar nodo "+nodo.getIdnodo()+" "+ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void updateNodoStatus(NodoStatusNotification aux) {
	
		if( aux.getNumero() == null) {
			log.error("error, numero en nulo, descartando actualizacion de status");
			return;
		}
		
		//buscar el nodo por numero de cel
		Nodo nodo  = findByNumber(aux.getNumero());
		
		//si no se encontro la entidad, salir
		if( nodo == null ) {
			log.info("nodo con numero "+aux.getNumero()+" no encontrado, descartando actualizacion de status");
			return;
		}
		
		//actualizar la fecha actual
		nodo.setFechaMonitoreo(new Date());
		
		//actualizar la fecha en que se esta notificando
		nodo.setFechaNotificacion(aux.getNotificationDate());
		
		//si el estatus de CFE cambio, actualizar la fecha en que cambio el status
		if( isCFEChanged(aux, nodo) ) {
			nodo.setCfePresente(aux.getCfePresente());
			nodo.setCfeFecha(aux.getStatusDate());
			log.info("actualizando estatus de cfe....");
		} else
			log.info("estatus de cfe sin cambio........");
		
		//si el estatus de UPS cambio, actualizar la fecha en que cambio el status
		if( isUPSChanged(aux, nodo) ) {
			nodo.setUpsPresente(aux.getUpsPresente());
			nodo.setUpsFecha(aux.getStatusDate());
			log.info("actualizando estatus de ups....");
		} else
			log.info("estatus de ups sin cambio......");
		
		try {
			//actualizar los campos en la base de datos
			entityManager.persist(nodo);
		}catch(Exception ex) {
			log.error("error al intentar actualizar nodo "+nodo.getIdnodo()+" "+ex.getMessage());
		}
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateMonitoreoDate(String numero) {
		
		//si el numero tiene mas de 10 digitos, eliminar los primeros digitos
    	//que corresponden a la clave del pais
    	if( numero.length() > 10 ) {
    		int index = numero.length() - 10;
    		if( index >= 0 )
    			numero = numero.substring(index);
    	}
    	
		//buscar el nodo por numero de cel
		Nodo nodo  = findByNumber(numero);
		
		if( nodo == null ) {
			log.info("nodo con numero "+numero+" no encontrado, descartando actualizacion de fecha");
			return;
		}
		
		nodo.setFechaMonitoreo(new Date());
		
		try {
			entityManager.merge(nodo);
		}catch(Exception ex) {
			log.error("error al intentar actualizar nodo "+nodo.getIdnodo()+" "+ex.getMessage());
		}
		
	}
	
	public void updateNodoDto(NodoDto nodo) {
			
			Nodo n = findById(nodo.getIdnodo());
			
			//validar que se haya encontrado entidad
			if( n == null ) {
				log.error("error, entidad con idnodo:"+nodo.getIdnodo()+" no encontrada");
				return;
			}
			
			n.setActivo(nodo.isActivo());
			n.setCfePresente(nodo.getCfePresente());
			n.setDomicilio(nodo.getDomicilio());
			n.setFechaMonitoreo(nodo.getFechaMonitoreo());
			n.setFechaNotificacion(nodo.getFechaNotificacion());
			n.setCfeFecha(nodo.getCfeFecha());
			n.setUpsFecha(nodo.getUpsFecha());
			n.setNombre(nodo.getNombre());
			n.setNumero(nodo.getNumero());
			n.setUpsPresente(nodo.getUpsPresente());
				
			persist(n);
	}
	
	public List<NodoDto> getNodosMonitoreo(int page,int limit,int segundos) {
		
		try {

			//toma todos los nodos activos
			Query q = entityManager.createQuery(getNodoDtoQuery()+" where activo is true and (n.fechaMonitoreo is NULL or (now()-n.fechaMonitoreo)>=:segundos) order by (now()-n.fechaMonitoreo) desc");
			q.setParameter("segundos",(double) segundos);
			
			//poner limite de resultados y numero de pagina
			q.setMaxResults(limit);
			q.setFirstResult(page*limit);
			
			//intentar ejecutar el query
			@SuppressWarnings("unchecked")
			List<NodoDto> lista = q.getResultList();
			
			//si hubo resultados, actualizarles la fecha de monitoreo
			
			//actualizar la fecha de monitoreo de todos los nodos que se acaban de consultar
			////////////////////////////////////////////////////////////////////////////////
			/*if( lista != null && lista.size() > 0 ) {
				
				String l = "";
				
				for( NodoDto aux : lista ) {
					if( l.length() > 0 ) l+="," ;
					l+=+aux.getIdnodo();
				}
				
				q = entityManager.createQuery("update Nodo n set n.fechaMonitoreo=now() where n.idnodo in("+l+")");
							
				q.executeUpdate();
			}*/
			/////////////////////////////////////////////////////////////////////////////////
			
			return lista;
			
		}catch(Exception ex) {
			log.error("error al intentar consultar nodos a monitorear, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public List<NodoDto> getNodos(int page,int limit) {
		
		try {

			//toma todos los nodos activos
			Query q = entityManager.createQuery(getNodoDtoQuery());
			
			//poner limite de resultados y numero de pagina
			q.setMaxResults(limit);
			q.setFirstResult(page*limit);
			
			//intentar ejecutar el query
			@SuppressWarnings("unchecked")
			List<NodoDto> lista = q.getResultList();
			
			//regresar lista de resultados
			return lista;
			
		}catch(Exception ex) {
			log.error("error al intentar consultar nodos a monitorear, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public List<NodoDto> getNodosActivos(int page,int limit) {
		
		try {

			//toma todos los nodos activos
			Query q = entityManager.createQuery(getNodoDtoQuery()+" where n.activo=true");
			
			//poner limite de resultados y numero de pagina
			q.setMaxResults(limit);
			q.setFirstResult(page*limit);
			
			//intentar ejecutar el query
			@SuppressWarnings("unchecked")
			List<NodoDto> lista = q.getResultList();
			
			//regresar lista de resultados
			return lista;
			
		}catch(Exception ex) {
			log.error("error al intentar consultar nodos a monitorear, mensaje:"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public void mergeDto(NodoDto nodo) {
		Nodo n = findById(nodo.getIdnodo());
		n.setActivo(nodo.isActivo());
		n.setCfePresente(nodo.getCfePresente());
		n.setCodigo(nodo.getCodigo());
		n.setDomicilio(nodo.getDomicilio());
		n.setFechaMonitoreo(nodo.getFechaMonitoreo());
		n.setFechaNotificacion(nodo.getFechaNotificacion());
		n.setNombre(nodo.getNombre());
		n.setNumero(nodo.getNumero());
		n.setUpsPresente(nodo.getUpsPresente());
		n.setCfeFecha(nodo.getCfeFecha());
		n.setUpsFecha(nodo.getUpsFecha());
		merge(n);
	}
	
	public int insertDto(NodoDto nodo) {
		Nodo n = new Nodo();
		n.setActivo(nodo.isActivo());
		n.setCfePresente(nodo.getCfePresente());
		n.setCodigo(nodo.getCodigo());
		n.setDomicilio(nodo.getDomicilio());
		n.setFechaMonitoreo(nodo.getFechaMonitoreo());
		n.setFechaNotificacion(nodo.getFechaNotificacion());
		n.setNombre(nodo.getNombre());
		n.setNumero(nodo.getNumero());
		n.setUpsPresente(nodo.getUpsPresente());
		n.setCfeFecha(nodo.getCfeFecha());
		n.setUpsFecha(nodo.getUpsFecha());
		persist(n);
		log.info("registro insertado con id:"+n.getIdnodo());
		return n.getIdnodo();
	}
	
	public void removeById(int idnodo) {
		Nodo n = findById(idnodo);
		remove(n);
	}
	
	private static String getNodoDtoQuery() {

		//public NodoDto(int idnodo, String nombre, String domicilio,String codigo, String numero, Boolean cfePresente,Date fechaCfe, Boolean upsPresente,
		//		Date fechaUps,Date fechaNotificacion, Date fechaMonitoreo, boolean activo, int version) 
		 return "select new sme.client.dto.NodoDto("
				+ "n.idnodo,n.nombre,n.domicilio,n.codigo,n.numero,n.cfePresente,n.cfeFecha,"
				+ "n.upsPresente,n.upsFecha,n.fechaNotificacion,n.fechaMonitoreo,n.activo,n.version"
				+ ") from Nodo n";
		
	}
	
	//intentar escribir a la cola de eventos del websocket
	private void sendWebSocket(Nodo nodo) {
		NodoDto d = new NodoDto();
		d.setIdnodo(nodo.getIdnodo());
		d.setNombre(nodo.getNombre());
		d.setCodigo(nodo.getCodigo());
		d.setNumero(nodo.getNumero());
		d.setCfePresente(nodo.getCfePresente());
		d.setCfeFecha(nodo.getCfeFecha());
		d.setUpsPresente(nodo.getUpsPresente());
		d.setUpsFecha(nodo.getUpsFecha());
		d.setFechaNotificacion(nodo.getFechaNotificacion());
		d.setFechaMonitoreo(nodo.getFechaMonitoreo());
		d.setActivo(nodo.isActivo());
		d.setVersion(nodo.getVersion());
		websocketqueueRemote.write(d);
	}
	
	private boolean isCFEChanged(NodoStatusNotification aux,Nodo nodo) {
		
		//si el status esta en null o la fecha del status esta en null entonces indicar que hay un cambio para forzar cambiar propiedades
		if( nodo.getCfePresente() == null || nodo.getCfeFecha() == null)
			return true;
		
		//fecha en base de datos es mas reciente que el status reportado, regresar fasle indicando que no hubo cambio
		if( nodo.getCfeFecha().getTime() >=  aux.getStatusDate().getTime() ) {
			log.info("fecha de estatus en cfe \""+formatDate(nodo.getCfeFecha())+"\" mas reciente que la fecha reportada \""+formatDate(aux.getStatusDate())+"\"");
			return false;
		}
		
		//si el status es igual al que esta en la base de datos entonces regresar false indicando que no hubo cambio en el status
		if( nodo.getCfePresente().booleanValue() != aux.getCfePresente().booleanValue() )
			return true;
		else
			return false;
	}
	
	private boolean isUPSChanged(NodoStatusNotification aux,Nodo nodo) {
		
		//si el status esta en null o la fecha del status esta en null entonces indicar que hay un cambio para forzar cambiar propiedades
		if( nodo.getUpsPresente() == null || nodo.getUpsFecha() == null)
			return true;
		
		//fecha en base de datos es mas reciente que el status reportado, regresar fasle indicando que no hubo cambio
		if( nodo.getUpsFecha().getTime() >=  aux.getStatusDate().getTime() ) {
			log.info("fecha de estatus en ups \""+formatDate(nodo.getUpsFecha())+"\" mas reciente que la fecha reportada \""+formatDate(aux.getStatusDate())+"\"");
			return false;
		}
		
		//si el status es igual al que esta en la base de datos entonces regresar false indicando que no hubo cambio en el status
		if( nodo.getUpsPresente().booleanValue() != aux.getUpsPresente().booleanValue() )
			return true;
		else
			return false;
	}
	
	private String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
}
