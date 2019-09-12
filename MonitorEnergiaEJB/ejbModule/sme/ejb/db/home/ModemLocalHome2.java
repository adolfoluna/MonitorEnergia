package sme.ejb.db.home;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.ModemLocalRemote2;
import sme.client.dto.ModemLocalDto;
import sme.ejb.db.model.ModemLocal;
import sme.ejb.db.model.ModemLocalHome;


@Stateless
@Remote(ModemLocalRemote2.class)
public class ModemLocalHome2 extends ModemLocalHome implements ModemLocalRemote2 {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private static final Log log = LogFactory.getLog(ModemLocalHome2.class);
	

	public ModemLocalHome2() {
		
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.db.home.ModemLocalRemote2#getModemList()
	 */
	@Override
	public List<ModemLocalDto> getModemList() {
		
		try {
			
			Query q = entityManager.createQuery("select new sme.client.dto.ModemLocalDto(m.idmodemLocal,m.version,m.numero,"
					+ "m.puerto,m.baudrate,m.databits,"
					+ "m.parity,m.stopbits,m.activo,m.monitoreoActivo) from ModemLocal m");
			
			@SuppressWarnings("unchecked")
			List<ModemLocalDto> res = q.getResultList();
			
			return res;

		}catch(Exception ex) {
			log.error("error al intentar consultar la lista de modems "+ex.getMessage());
		}
		
		
		return null;
	}
	
	@Override
	public List<ModemLocalDto> getModemList(int limite,int pagina) {
		
		try {
			
			Query q = entityManager.createQuery("select new sme.client.dto.ModemLocalDto(m.idmodemLocal,m.version,m.numero,"
					+ "m.puerto,m.baudrate,m.databits,"
					+ "m.parity,m.stopbits,m.activo,m.monitoreoActivo) from ModemLocal m");
			
			q.setMaxResults(limite);
			q.setFirstResult(pagina*limite);
			
			@SuppressWarnings("unchecked")
			List<ModemLocalDto> res = q.getResultList();
			
			return res;

		}catch(Exception ex) {
			log.error("error al intentar consultar la lista de modems "+ex.getMessage());
		}
		
		
		return null;
	}
	
	public ModemLocalDto findByIdDto(int idmodem) {

		try {
			//crear query
			Query q = entityManager.createQuery("select new sme.client.dto.ModemLocalDto(m.idmodemLocal,m.version,m.numero,"
					+ "m.puerto,m.baudrate,m.databits,"
					+ "m.parity,m.stopbits,m.activo,m.monitoreoActivo) from ModemLocal m where m.idmodemLocal=:id");
			
			//especificar parametro de query
			q.setParameter("id", idmodem);
			
			//regresar el resultado del query
			return (ModemLocalDto) q.getSingleResult();
	
		}catch(NoResultException ex) {
			log.info("no se encontro modem con id:"+idmodem);
			return null;
		}
		catch(Exception ex) {
			log.error("error al intentar consultar la lista de modems "+ex.getMessage());
		}
		
		
		return null;
	}
	
	public void mergeDto(ModemLocalDto modem) {
		ModemLocal m = findById(modem.getIdmodem());
		m.setActivo(modem.isActivo());
		m.setBaudrate(modem.getBaudrate());
		m.setDatabits(modem.getDatabits());
		m.setNumero(modem.getNumero());
		m.setParity(modem.getParity());
		m.setPuerto(modem.getPuerto());
		m.setStopbits(modem.getStopbits());
		m.setMonitoreoActivo(modem.isMonitoreoActivo());
		merge(m);
	}
	
	public int insertDto(ModemLocalDto modem) {
		ModemLocal m = new ModemLocal();
		m.setActivo(modem.isActivo());
		m.setBaudrate(modem.getBaudrate());
		m.setDatabits(modem.getDatabits());
		m.setNumero(modem.getNumero());
		m.setParity(modem.getParity());
		m.setPuerto(modem.getPuerto());
		m.setStopbits(modem.getStopbits());
		m.setMonitoreoActivo(modem.isMonitoreoActivo());
		persist(m);
		log.info("registro insertado con id:"+m.getIdmodemLocal());
		return m.getIdmodemLocal();
	}
	
	public void remove(int idmodem) {
		ModemLocal m = findById(idmodem);
		remove(m);
	}
}
