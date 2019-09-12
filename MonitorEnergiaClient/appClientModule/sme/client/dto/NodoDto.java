package sme.client.dto;

import java.io.Serializable;
import java.util.Date;

public class NodoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1245461739239465636L;
	
	private int idnodo;
	private String nombre;
	private String domicilio;
	private String codigo;
	private String numero;
	private Boolean cfePresente;
	private Date cfeFecha;
	private Boolean upsPresente;
	private Date upsFecha;
	private Date fechaNotificacion;
	private Date fechaMonitoreo;
	private boolean activo;
	private boolean llamar;
	private int version;
	
	public NodoDto() {
		
	}

	public NodoDto(int idnodo, String nombre, String domicilio,String codigo, String numero, Boolean cfePresente,Date cfeFecha, Boolean upsPresente,
			Date upsFecha,Date fechaNotificacion, Date fechaMonitoreo, boolean activo, int version) {
		super();
		this.idnodo = idnodo;
		this.nombre = nombre;
		this.domicilio = domicilio;
		this.codigo = codigo;
		this.numero = numero;
		this.cfePresente = cfePresente;
		this.setCfeFecha(cfeFecha);
		this.upsPresente = upsPresente;
		this.setUpsFecha(upsFecha);
		this.fechaNotificacion = fechaNotificacion;
		this.fechaMonitoreo = fechaMonitoreo;
		this.activo = activo;
		this.version = version;
		llamar = false;
	}

	public int getIdnodo() {
		return idnodo;
	}

	public void setIdnodo(int idnodo) {
		this.idnodo = idnodo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
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

	public Date getFechaNotificacion() {
		return fechaNotificacion;
	}

	public void setFechaNotificacion(Date fechaNotificacion) {
		this.fechaNotificacion = fechaNotificacion;
	}

	public Date getFechaMonitoreo() {
		return fechaMonitoreo;
	}

	public void setFechaMonitoreo(Date fechaMonitoreo) {
		this.fechaMonitoreo = fechaMonitoreo;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public boolean isLlamar() {
		return llamar;
	}

	public void setLlamar(boolean llamar) {
		this.llamar = llamar;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Date getCfeFecha() {
		return cfeFecha;
	}

	public void setCfeFecha(Date cfeFecha) {
		this.cfeFecha = cfeFecha;
	}

	public Date getUpsFecha() {
		return upsFecha;
	}

	public void setUpsFecha(Date upsFecha) {
		this.upsFecha = upsFecha;
	}

	public String toString() {
		return "idnodo:"+idnodo
				+" ,nombre:"+nombre
				+" ,codigo:"+codigo
				+" ,numero:"+numero
				+" ,cfe:"+cfePresente
				+" ,cfeFecha:"+cfeFecha
				+" ,ups:"+upsPresente
				+" ,upsFecha:"+upsFecha
				+" ,fechaNotificacion:"+fechaNotificacion
				+" ,fechaMonitoreo:"+fechaMonitoreo
				+" ,activo:"+activo
				+" ,llamar:"+llamar
				+" ,version:"+version
				;
	}
	
	public String toJSONString() {
		
		String s = "{";
		
    	s+="\"idnodo\":"+idnodo+",";
    	s+="\"nombre\":\""+nombre+"\",";
    	s+="\"codigo\":\""+codigo+"\",";
    	s+="\"numero\":\""+numero+"\",";
    	
    	if(cfePresente!=null)
    		s+="\"cfePresente\":\""+cfePresente.toString()+"\",";
    	else
    		s+="\"cfePresente\":null,";
    	
    	if(cfeFecha != null)	
    		s+="\"cfeFecha\":"+cfeFecha.getTime()+",";
    	else
    		s+="\"cfeFecha\":null,";
    	
    	if( upsPresente != null )
    		s+="\"upsPresente\":\""+upsPresente.toString()+"\",";
    	else
    		s+="\"upsPresente\":null,";
    	
    	if(upsFecha != null)
    		s+="\"upsFecha\":"+upsFecha.getTime()+",";
    	else
    		s+="\"upsFecha\":null,";
    		
    	if(fechaNotificacion !=null)
    		s+="\"fechaNotificacion\":"+fechaNotificacion.getTime()+",";
    	else
    		s+="\"fechaNotificacion\":null,";
    	
    	if(fechaMonitoreo !=null)
    		s+="\"fechaMonitoreo\":"+fechaMonitoreo.getTime()+",";
    	else 
    		s+="\"fechaMonitoreo\":null,";
    	
    	s+="\"activo\":\""+activo+"\",";
    	s+="\"version\":"+version;
    	s+="}";
		return s;
	}
}
