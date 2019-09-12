package sme.ejb.db.home;

import java.util.Date;

import sme.ejb.db.model.Historial;

public interface HistorialRemote2 {

	void insertHistorial(int idnodo, Date fecha, boolean cfePresente, boolean upsPresente,Date fechaNotificacion);
	
	void persist(Historial transientInstance);

	void remove(Historial persistentInstance);

	Historial merge(Historial detachedInstance);

	Historial findById(Integer id);

}