package sme.modem.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;
import sme.modem.cellphone.CellPhoneModuleFactory;
import sme.modem.cellphone.CellPhoneModuleSMSListener;
import sme.modem.cellphone.CellPhoneModuleSMSSender;
import sme.modem.cellphone.CellPhoneModuleService;
import sme.modem.gsm.GSMModemCommandResponse;
import sme.modem.queue.MonQueueReaderWriterRemote;

@Stateless
@Remote(MonitoreoServiceRemote.class)
public class MonitoreoServiceHome implements CellPhoneModuleSMSListener, MonitoreoServiceRemote {
	
	private static Log log = LogFactory.getLog(MonitoreoServiceHome.class);
	
	@EJB private MonQueueReaderWriterRemote queue;
	
	private CellPhoneModuleService cellPhoneModuleService;
	
	public MonitoreoServiceHome() {
		log.info("instancia MonitoreoServiceHome creada..........");
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.service.MonitoreoServiceRemote#start()
	 */
	@Override
	public void start(ModemLocalDto modem) {
		
		if( modem == null ) {
			log.error("error no se pudo iniciar servicio argumento en null");
			return;
		}
		
		cellPhoneModuleService = CellPhoneModuleFactory.createCellPhoneModuleService(modem);
		cellPhoneModuleService.getCellPhoneModule().setListener(this);//escuchar cuando llega un SMS
		cellPhoneModuleService.start();
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.service.MonitoreoServiceRemote#stop()
	 */
	@Override
	public void stop() {
		
		if( cellPhoneModuleService == null ) {
			log.info("servicio con ya terminado");
			return;
		}
		cellPhoneModuleService.getCellPhoneModule().setListener(null);
		cellPhoneModuleService.stop();
		cellPhoneModuleService = null;
	}
	
	/* (non-Javadoc)
	 * @see sme.modem.service.MonitoreoServiceRemote#read()
	 */
	@Override
	public boolean read() {
		
		//si el recurso esta ocupado regresar true
		if( 	!cellPhoneModuleService.isInitialized() 
				|| cellPhoneModuleService.getCellPhoneModule().isSendingSMS()
				|| cellPhoneModuleService.getCellPhoneModule().getCallStatus().isCallInProgress() )
				
			return true;
		
		//intentar leer de la cola
		NodoDto nodo = queue.read();
		
		//si no se leyo nada de la cola, salir de la rutina
		//regresar false indicando que el recurso no estaba ocupado
		if( nodo == null )
			return false;
		
		//procesar el mensaje que llego de la cola
		procesarMensaje(nodo);
		
		//regresar false indicando que el recurso no estaba ocupado
		return false;
	}
	
	public void procesarMensaje(NodoDto nodo) {
		
		if(nodo == null)
			return;
		
		//si de casualidad se ocupo el modem en el transcurso de la lectura de la cola, esperar a que se desocupe el modem
		while( cellPhoneModuleService.getCellPhoneModule().isSendingSMS() || cellPhoneModuleService.getCellPhoneModule().getCallStatus().isCallInProgress() ) {
			log.info("esperando a que se desocupe modem.........");
			try { Thread.sleep(1_000); } catch (InterruptedException e) { log.info("proceso de de espera interrumpido");}
		}
		
		log.info("procesando mensaje "+nodo);
		
		//en caso de estar especificado el codigo del pais, agregarlo
		String temp = nodo.getNumero();
		
		//completar el numero con el codigo espeficicado
		if( nodo.getCodigo() != null && nodo.getCodigo().trim().length() > 0 )
			temp = nodo.getCodigo().trim()+ temp;
		
		//si hay que llamar intentar hacer la llamada
		if(nodo.isLlamar() ) 
			cellPhoneModuleService.getCellPhoneModule().makeCall(temp);
		else //si hay que enviar mensaje intentar enviar mensaje
			cellPhoneModuleService.getCellPhoneModule().sendSMS(temp, "Getestatus "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		
	}
	
	//evento que ocurre cuando llega un mensaje SMS al modem celular
	public void cellPhoneSMS(String phoneNumber,String message) {
		
		//intentar convertir mensaje sms a NodoStatusNotification
		NodoStatusNotification ns = NodoSMSParser.parse(phoneNumber, message);
		
		//no se pudo generar instancia, salir
		if( ns == null )
			return;
		
		//escribir la instancia generada en la cola
		queue.write(ns);
		
		//si hay que mandar un mensaje de notificacion, hacerlo, iniciando un nuevo proceso
		if( ns.isNotificarStatus() ) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			if( ns.getNumeroMensaje() < 10 )
				new CellPhoneModuleSMSSender(cellPhoneModuleService, "Recibi0"+ns.getNumeroMensaje()+" "+format.format(new Date()), phoneNumber);
			else
				new CellPhoneModuleSMSSender(cellPhoneModuleService, "Recibi"+ns.getNumeroMensaje()+" "+format.format(new Date()), phoneNumber);
		}
			
	}
	
	public String sendCommand(String command,long timeOut) {
		GSMModemCommandResponse res = cellPhoneModuleService.getCellPhoneModule().getModem().sendCommand(command.getBytes(), timeOut);
		if( res == null )
			return null;
		else
			return res.getResponse();
	}

}
