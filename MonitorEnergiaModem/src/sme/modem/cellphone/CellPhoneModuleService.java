package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.GSMModemConfigInit;
import sme.modem.gsm.GSMModemConfigInitListener;
import sme.modem.gsm.GSMModemWithCommands;
import sme.modem.media.comm.MediaComm;
import sme.modem.media.comm.MediaCommConnectionListener;
import sme.modem.serialport.PuertoSerialMediaCommWatch;
import sme.modem.serialport.PuertoSerialParams;

public class CellPhoneModuleService implements MediaCommConnectionListener, GSMModemConfigInitListener {
	
	private int idmodem;
	
	private boolean initialized = false;
	
	private static Log log = LogFactory.getLog(CellPhoneModuleService.class);
	
	private PuertoSerialParams serialParams;
	
	private GSMModemConfigInit config;
	
	//para estar monitoreando el puerto serial, en caso de haber un error en el puerto serial renovar la conexion
	private PuertoSerialMediaCommWatch mediaCommWatch;
	
	private GSMModemWithCommands modem = new GSMModemWithCommands();
	private CellPhoneModule cellPhoneModule = new CellPhoneModule();

	public CellPhoneModuleService(int idmodem,PuertoSerialParams serialParams) {
		this.idmodem = idmodem;
		this.serialParams = serialParams;
		cellPhoneModule.setModem(modem);
	}
	
	public CellPhoneModule getCellPhoneModule() { return cellPhoneModule; }
	
	public int getIdmodem() { return idmodem; }
	
	public void start() {

		if( serialParams == null ) {
			log.error("error parametros de puerto serial en null, servicio no iniciado");
			return;
		}
		
		//iniciar proceso para monitorear el puerto serial
		mediaCommWatch = new PuertoSerialMediaCommWatch(serialParams);
		mediaCommWatch.setMediaCommConnectionListener(this);
		mediaCommWatch.start();
	 
	}
	
	public void stop() {
		
		//detener servicio para monitorear puerto serial, en caso de estar activo
		if( mediaCommWatch != null ) {
			mediaCommWatch.setMediaCommConnectionListener(null);
			mediaCommWatch.stop();
		}
		
		//detener servicio de configuracion del modem, en caso estar activo
		if(config != null) config.stop();
		
		//detener los thread que el objeto modem haya creado
		if(modem != null) modem.stop();
		
	}

	//ocurre cuando se abrio el puerto serial
	@Override
	public void mediaCommOpened(MediaComm mediaComm) {
		
		log.info("puerto serial disponible........");
		
		//solo la primera ves inicializa el modem
		if( modem.getMediaComm() == null ) {
			
			//actualizar el modem con el medio de comunicacion que se acaba de entregar
			modem.setMediaComm(mediaComm);
			
			//iniciar proceso para inicializar modem
			config = new GSMModemConfigInit(modem, this);
			config.start();
			
		} else
			modem.setMediaComm(mediaComm);//actualizar el modem con el medio de comunicacion que se acaba de entregar
		
	}

	//ocurre cuando se cerror el puerto serial
	@Override
	public void mediaCommClosed() {
		log.info("medio de comuniacion cerrado.....");
		modem.setMediaComm(null);
	}

	@Override
	public void initConfigFinished() {
		
		log.info("configuracion exitosamente terminada");
		
		//eliminar la referencia del proceso de configuracion del modem
		config = null;
		
		//encender bandera de que ya esta inicializado
		initialized = true;
	
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	

}
