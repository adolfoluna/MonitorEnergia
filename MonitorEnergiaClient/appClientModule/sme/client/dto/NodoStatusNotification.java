package sme.client.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NodoStatusNotification implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3298181536821703795L;
	
	private Date notificationDate;
	private String numero;
	private Boolean cfePresente;
	private Boolean upsPresente;
	private Date statusDate;
	private boolean notificarStatus;
	
	public NodoStatusNotification() {
		
	}
	
	public NodoStatusNotification(Date notificationDate, String numero, Boolean cfePresente, Boolean upsPresente,
			Date fecha, boolean notificarStatus) {
		super();
		this.notificationDate = notificationDate;
		this.numero = numero;
		this.cfePresente = cfePresente;
		this.upsPresente = upsPresente;
		this.statusDate = fecha;
		this.notificarStatus = notificarStatus;
	}

	public NodoStatusNotification(String numero, Boolean cfePresente, Boolean upsPresente,Date fecha) {
		super();
		this.numero = numero;
		this.cfePresente = cfePresente;
		this.upsPresente = upsPresente;
		this.statusDate = fecha;
	}
	

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Boolean getCfePresente() {
		return cfePresente;
	}

	public void setCfePresente(Boolean cfePresente) {
		this.cfePresente = cfePresente;
	}

	public Boolean getUpsPresente() {
		return upsPresente;
	}

	public void setUpsPresente(Boolean upsPresente) {
		this.upsPresente = upsPresente;
	}
	
	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date fecha) {
		this.statusDate = fecha;
	}

	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		return "numero:"+numero
				+" cfe:"+cfePresente
				+" ups:"+upsPresente
				+" fecha estatus:"+format.format(statusDate)
				+" fecha mensaje:"+format.format(notificationDate)
				+" notificar:"+notificarStatus;
	}

	public boolean isNotificarStatus() {
		return notificarStatus;
	}

	public void setNotificarStatus(boolean notificarStatus) {
		this.notificarStatus = notificarStatus;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}
}
