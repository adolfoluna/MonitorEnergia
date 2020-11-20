package sme.modem.service2;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;
import sme.modem.cellphone.CellPhoneModuleCallStatusEnum;
import sme.modem.cellphone.CellPhoneModuleCallStatusListener;
import sme.modem.cellphone.CellPhoneModuleFactory;
import sme.modem.cellphone.CellPhoneModuleSMSListener;
import sme.modem.cellphone.CellPhoneModuleService;
import sme.modem.cellphone.CellPhoneModuleToneListener;
import sme.modem.cellphone.CellPhoneToneSenderThread;
import sme.modem.gsm.GSMModemCommandResponse;


@Stateless
@Remote(ModemServiceRemote.class)
public class ModemServiceHome implements CellPhoneModuleSMSListener, CellPhoneModuleCallStatusListener, CellPhoneModuleToneListener, ModemServiceRemote, ModemToneWaiterFinishedInterface {
	
	private static final Log log = LogFactory.getLog(ModemServiceHome.class);
	
	private CellPhoneModuleService cellPhoneModuleService;
	
	private ModemToneWaiter toneWaiter = null;
	
	@EJB(lookup="java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2")
	private NodoRemote2 nodoRemote;
	
	@Override
	public void start(ModemLocalDto modemdto) {
		
		if( modemdto == null ) {
			log.error("error no se pudo iniciar servicio argumento en null");
			return;
		}
		
		cellPhoneModuleService = CellPhoneModuleFactory.createCellPhoneModuleService(modemdto);
		cellPhoneModuleService.getCellPhoneModule().setListener(this);//escuchar cuando llega un SMS
		cellPhoneModuleService.getCellPhoneModule().setStatusCallListener(this);//escuchar cuando una llamada cambia de status
		cellPhoneModuleService.getCellPhoneModule().setToneListener(this);//escuchar cuando escriban un tono en el telefono
		cellPhoneModuleService.start();
	}
	
	@Override
	public void stop() {
		
		if( cellPhoneModuleService == null ) {
			log.info("servicio con ya terminado");
			return;
		}
		
		cellPhoneModuleService.getCellPhoneModule().setListener(null);
		cellPhoneModuleService.getCellPhoneModule().setStatusCallListener(null);
		cellPhoneModuleService.stop();
		cellPhoneModuleService = null;
	}
	
	@Override
	public String sendCommand(String command,long timeOut) {
		
		GSMModemCommandResponse res = cellPhoneModuleService.getCellPhoneModule().getModem().sendCommand(command.getBytes(), timeOut);
		
		if( res == null )
			return null;
		else
			return res.getResponse();
	}
	
	@Override
	public boolean isResourceAvailable() {
		
		log.info("evaluando si recurso esta disponible....");
		
		if( toneWaiter != null ) {
			log.info("recurso no disponible, proceso esperando por respuesta en tonos....");
			return false;
		}
		
		if( !cellPhoneModuleService.isInitialized() ) {
			log.info("recurso no disponible, esperando a que se complete configuracion.....");
			return false;
		}
		
		if( cellPhoneModuleService.getCellPhoneModule().isSendingSMS() ) {
			log.info("recurso no disponible, esperando a que se complete el envio de un sms.....");
			return false;
		}
		
		if( cellPhoneModuleService.getCellPhoneModule().getCallStatus().isCallInProgress() ) {
			log.info("recurso no disponible, llamada en progreso.....");
			return false;
		}
		
		log.info("recurso disponible.....");
		
		//recurso disponible
		return true;
	}
	
	@Override
	public void monitorNodeByCall(NodoDto nodo) {
		
		//tomar el numero al que se tiene que marcar
		String temp = nodo.getNumero();
		
		//agregar el codigo del pais solo en caso de que este especificado
		if( nodo.getCodigo() != null )
			temp = nodo.getCodigo() + temp;
		
		//hacer la llamada
		cellPhoneModuleService.getCellPhoneModule().makeCall(temp);
		
		//empezar a esperar por los tonos
		toneWaiter = new ModemToneWaiter(nodo, this);
	}

	//ocurre cuando llega un sms 
	@Override
	public void cellPhoneSMS(String phoneNumber, String message) {
		log.info("sms numero:"+phoneNumber+" mensaje:"+message);
	}

	@Override
	public void callStatusChanged(CellPhoneModuleCallStatusEnum status) {

		log.info("cambio estatus de llamada a "+status);
		
		//si la llamada se acaba de conectar, enviar el comando 1 que significa que me de el ultimo status
		if( status == CellPhoneModuleCallStatusEnum.ACTIVE ) {
			CellPhoneToneSenderThread aux = new CellPhoneToneSenderThread(cellPhoneModuleService.getCellPhoneModule(), "2");
			new Thread(aux).start();
		}

	}

	@Override
	public void toneDTMFDetected(String digit) {
		
		log.info("tono detectado "+digit);
		
		if( toneWaiter != null )
			toneWaiter.addTone(digit);
		else
			log.info("tono detectado pero no existe ningun proceso esperando tonos, descartando tono.....");
		
	}

	//ocurre cuando se termina de esperar por los tonos de las llamadas
	@Override
	public void waitingToneDone() {
		
		//borrar la referencia porque ya se termino la espera
		toneWaiter = null;
		
		//colgar llamada en caso de que haya una llamada en progreso
		if( cellPhoneModuleService.getCellPhoneModule().getCallStatus().isCallInProgress() )
			cellPhoneModuleService.getCellPhoneModule().hangupCall();
	}
	
}
