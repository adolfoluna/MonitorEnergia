package sme.ejb.db.model;
// Generated May 16, 2019 10:28:33 PM by Hibernate Tools 5.2.11.Final

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class Historial.
 * @see sme.ejb.db.model.Historial
 * @author Hibernate Tools
 */
@Stateless
public class HistorialHome {

	private static final Log log = LogFactory.getLog(HistorialHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Historial transientInstance) {
		log.debug("persisting Historial instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Historial persistentInstance) {
		log.debug("removing Historial instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Historial merge(Historial detachedInstance) {
		log.debug("merging Historial instance");
		try {
			Historial result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Historial findById(Integer id) {
		log.debug("getting Historial instance with id: " + id);
		try {
			Historial instance = entityManager.find(Historial.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
