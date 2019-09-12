package sme.ejb.db.model;
// Generated May 16, 2019 10:28:33 PM by Hibernate Tools 5.2.11.Final

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class ModemLocal.
 * @see sme.ejb.db.model.ModemLocal
 * @author Hibernate Tools
 */
@Stateless
public class ModemLocalHome {

	private static final Log log = LogFactory.getLog(ModemLocalHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(ModemLocal transientInstance) {
		log.debug("persisting ModemLocal instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(ModemLocal persistentInstance) {
		log.debug("removing ModemLocal instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public ModemLocal merge(ModemLocal detachedInstance) {
		log.debug("merging ModemLocal instance");
		try {
			ModemLocal result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ModemLocal findById(Integer id) {
		log.debug("getting ModemLocal instance with id: " + id);
		try {
			ModemLocal instance = entityManager.find(ModemLocal.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
