package sme.ejb.db.model;
// Generated May 16, 2019 10:28:33 PM by Hibernate Tools 5.2.11.Final

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class Nodo.
 * @see sme.ejb.db.model.Nodo
 * @author Hibernate Tools
 */
@Stateless
public class NodoHome {

	private static final Log log = LogFactory.getLog(NodoHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Nodo transientInstance) {
		log.debug("persisting Nodo instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Nodo persistentInstance) {
		log.debug("removing Nodo instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Nodo merge(Nodo detachedInstance) {
		log.debug("merging Nodo instance");
		try {
			Nodo result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Nodo findById(Integer id) {
		log.debug("getting Nodo instance with id: " + id);
		try {
			Nodo instance = entityManager.find(Nodo.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
