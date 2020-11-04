package sme.modem.service2;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.ModemLocalDto;
import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;
import sme.modem.cellphone.CellPhoneModuleCallStatusEnum;
import sme.modem.cellphone.CellPhoneModuleCallStatusListener;
import sme.modem.cellphone.CellPhoneModuleFactory;
import sme.modem.cellphone.CellPhoneModuleSMSListener;
import sme.modem.cellphone.CellPhoneModuleService;
import sme.modem.cellphone.CellPhoneModuleToneListener;
import sme.modem.cellphone.CellPhoneToneSenderThread;
import sme.modem.gsm.GSMModemCommandResponse;
import sme.modem.service.NodoToneParser;

@Stateless
@Remote(ModemServiceRemote.class)
public class ModemServiceHome implements CellPhoneModuleSMSListener, CellPhoneModuleCallStatusListener, CellPhoneModuleToneListener, ModemServiceRemote {
	
	private static final Log log = LogFactory.getLog(ModemServiceHome.class);
	
	private CellPhoneModuleService cellPhoneModuleService;
	
	private NodoDto nodo = null;
	private String respuesta = "";
	
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
		
		//si el recurso esta ocupado regresar false
		if( nodo != null || !cellPhoneModuleService.isInitialized() 
			|| cellPhoneModuleService.getCellPhoneModule().isSendingSMS()
			|| cellPhoneModuleService.getCellPhoneModule().getCallStatus().isCallInProgress() )
			return false;
		
		return true;
	}
	
	@Override
	public void monitorNodeByCall(NodoDto nodo) {
		
		//guardar el nodo que se esta monitoreando
		this.nodo = nodo;
		
		//borrar lo que hay en respuesta
		respuesta = "";
		
		//tomar el numero al que se tiene que marcar
		String temp = nodo.getNumero();
		
		//agregar el codigo del pais solo en caso de que este especificado
		if( nodo.getCodigo() != null )
			temp = nodo.getCodigo() + temp;
		
		//hacer la llamada
		cellPhoneModuleService.getCellPhoneModule().makeCall(temp);
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
			CellPhoneToneSenderThread aux = new CellPhoneToneSenderThread(cellPhoneModuleService.getCellPhoneModule(), "1");
			new Thread(aux).start();
		}
	}

	@Override
	public void toneDTMFDetected(String digit) {
		log.info("tono detectado "+digit);
		
		switch(digit) {
		
			case "#": 
				respuesta = ""; 
				break;
				
			case "*": 
				
				//colgar la llamada
				cellPhoneModuleService.getCellPhoneModule().hangupCall();
				
				//analizar respuesta de tonos
				analizarRespuesta(respuesta);
				
				break;
				
			default:
				respuesta+=digit;
				break;
		}
	}
	
	private void analizarRespuesta(String rx) {
		
		log.info("analizando respuesta:\""+rx+"\" len:"+rx.length()+".....................");
		
		NodoStatusNotification aux = NodoToneParser.parse(nodo, rx);
		
		//si se pudo parsear exitosamente la respuesta
		if( aux != null ) {
			log.info(aux);
			nodoRemote.updateNodoStatus(aux);
		}
		
		this.nodo = null;
		this.respuesta = "";
		
	}
	
}
