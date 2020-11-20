package sme.modem.cellphone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.gsm.TimerWaitAsync;
import sme.modem.gsm.WaitTimeOutListener;

public class CellPhoneModuleCallStatus implements WaitTimeOutListener {

	private static final Log log = LogFactory.getLog(CellPhoneModuleCallStatus.class);
	private boolean callInProgress = false;
	
	private  TimerWaitAsync busylinetimer;
	
	private CellPhoneModule modem;
	
	public CellPhoneModuleCallStatus(CellPhoneModule modem) {
		this.modem = modem;
	}
	
	public int updateStatus(String event) {
		
		int eventn = -1;
		
		//extraer el digito que indica el status de la llamada
		//if(event.startsWith("+UCALLSTAT:"))
		//	event = event.replaceFirst("\\+UCALLSTAT: \\d,", "");
		if( event.indexOf("VOICE CALL: BEGIN") >= 0 )
			event="0";
		
		if( event.indexOf("VOICE CALL: END") >= 0 )
			event="6";
		
		if( event.indexOf("RING") >= 0 )
			event = "4";
		
		try {
			eventn = Integer.parseInt(event);
		}catch(Exception ex) {
			log.error("error no se pudo convertir '"+event+"' a numero entero");
			return -1;
		}
		
		//0-activa, 1-hold, 2-marcando 3-sonando llamada saliente, 4-sonando llamada entrante, 5-llamada en espera remoto, 6-desconectado
		//7-conectado
		if( eventn <= 3 || eventn == 5 || eventn == 7 )
			setCallInProgress(true);
		
		if( eventn == 6 )
			setCallInProgress(false);
		
		switch(eventn) {
			case 0: log.info("evento llamada activa.........."); break;
			case 1: log.info("evento llamada entrante en espera........"); break;
			case 2: log.info("evento marcando......."); break;
			case 3: log.info("evento sonando llamada saliente........"); break;
			case 4: log.info("evento sonando llamada entrante........"); break;
			case 5: log.info("evento llamada saliente en espera........"); break;
			case 6: log.info("evento llamada desconectada......"); break;
			case 7: log.info("evento conectado........"); break;
			default:
				log.error("error, evento de llamada no reconocido evento:"+eventn);
				break;
		}
		
		//si la llamada acaba de ser activada entonces iniciar proceso para colgar llamada
		//especificando que haga un delay de 2 segundos
		//if( eventn == 0 )
		//	new CellPhoneHandUpCall(modem,true);
		
		return eventn;
		
	}

	public boolean isCallInProgress() {
		return callInProgress;
	}

	synchronized public void setCallInProgress(boolean callInProgress) {
		
		if( callInProgress == this.callInProgress )
			return;
		
		this.callInProgress = callInProgress;

		if( callInProgress ) {
			log.info("cambio de estatus a linea OCUPADA.................");
			busylinetimer = new TimerWaitAsync();
			busylinetimer.setWaitTimeOutListener(this);
			busylinetimer.startWait(90_000);
		}
		else {
			log.info("cambio de estatus a linea LIBRE..............");
			if(busylinetimer != null ) {
				busylinetimer.stopWait();
				busylinetimer.stop();
				busylinetimer = null;
			}
		}
	}

	//ocurre cuando pasan mas de 10 segundos sin que callInProgress cambie false
	@Override
	public void waitTimedOut() {
		
		if( busylinetimer == null )
			return;
		
		busylinetimer.stop();
		busylinetimer = null;
		
		log.info("linea ocupada por mas de 120 segundos, iniciando proceso para colgar llamada.......");
		
		new CellPhoneHandUpCall(modem,false);
	}
	
}
