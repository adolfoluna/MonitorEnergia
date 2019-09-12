package sme.modem.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.media.comm.MediaComm;
import sme.modem.media.comm.MediaCommDataListener;

public class GSMModem implements MediaCommDataListener, GSMModemBufferListener, WaitTimeOutListener {

	private static final Log log = LogFactory.getLog(GSMModem.class);
	
	private MediaComm mediaComm;
	private GSMModemBuffer buffer = new GSMModemBuffer();
	private TimerWaitable responseWaiter = new TimerWait();
	private TimerWaitAsync eventWaiter = new TimerWaitAsync(); 
	
	private GSMModemCommandResponse respuesta;
	
	private GSMModemEventListener glistener;
	
	private static final long eventTimeOut = 15_000;
	
	public GSMModem() {
		eventWaiter.setWaitTimeOutListener(this);
		buffer.setGSMModemBufferListener(this);
	}
	
	public GSMModemCommandResponse sendCommand(String command,long timeOut) { return sendCommand(command.getBytes(), timeOut); };
	
	synchronized public GSMModemCommandResponse sendCommand(byte []command,long timeOut) {
		
		String aux = new String(command).replace("\r\n","\\n");
		
		if( mediaComm == null ) {
			log.error("error intentando enviar comando sin medio de comunicacion disponibe comando:"+aux);
			return null;
		}
		
		//si hay un evento pendiente esperar que el evento se complete
		while(eventWaiter.isWaiting()) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
				
		//inicializar respuesta apuntando a un nuevo Objeto
		respuesta = new GSMModemCommandResponse();
		
		//por defecto asumir que se agoto el tiempo de espera
		respuesta.setTimeOutReached(true);
		
		//escribir al puerto serial
		if( mediaComm.write(command) )
			log.info("tx:"+aux);
		else {
			log.error("error, no se pudo enviar comando '"+aux);
			return null;
		}
		
		//comenzar a esperar por la respuesta
		responseWaiter.startWait(timeOut);
		
		if( respuesta.isTimeOutReached() ) 
			log.error("error, tiempo de espera agotado comando:"+aux);
		
		if( !respuesta.isResponseComplete() ) 
			log.error("error, respuesta incompleta comando:"+aux);
		
		if( respuesta.getResponse() != null ) 
			log.info("rx:"+respuesta.getResponse().replace("\r\n", "\\n"));
		
		return respuesta;
	}
	
	public MediaComm getMediaComm() {
		return mediaComm;
	}

	public void setMediaComm(MediaComm mediaComm) {
		
		//borrar el listener del medio de comunicacion anteior
		if( this.mediaComm != null )
			this.mediaComm.setMediaCommDataListener(null);
		
		//guardar la referencia del medio de comunicacion
		this.mediaComm = mediaComm;
		
		//escuchar cuando llegue info al medio de comunicacion
		mediaComm.setMediaCommDataListener(this);
	}

	@Override
	public void onMediaCommDataRecived(byte[] data, int length) {

		//si no se esta esperando respuesta ni evento, empezar a esperar evento
		if( !responseWaiter.isWaiting() && !eventWaiter.isWaiting() )
			eventWaiter.startWait(eventTimeOut);
		
		//agregar a buffer la respuesta que llego del medio de comunicacion
		buffer.write(data,responseWaiter.isWaiting());
	}

	@Override
	public void gsmModemBufferResponse(String response) {
		
		if( respuesta != null ) {
			respuesta.setResponse(response);
			respuesta.setResponseComplete(true);
			respuesta.setTimeOutReached(false);
		}
		else 
			log.error("error, no existe objeto respuesta para ser asignado");
		
		//dejar de esperar a respuesta de comando
		responseWaiter.stopWait();
	}

	@Override
	public void gsmModemBufferEvent(String event) {
		
		//dejar de esperar por el evento
		eventWaiter.stopWait();
				
		//indicar evento en log
		log.info("evento:"+event.replace("\r\n", "\\n"));
		
		//disparar evento de que llego un evento al modem
		if( glistener != null ) 
			glistener.gsmModemEvent(event);
	}
	
	public void stop() {
		eventWaiter.stop();
	}

	//ocurre cuando la espera de un evento hace timeout
	@Override
	public void waitTimedOut() {
		
		if( respuesta != null )
			log.error("error, tiempo de espera agotado para recibir evento, rx:"+respuesta.getResponse().replace("\r\n", "\\n"));
		else
			log.error("error, tiempo de espera agotado para recibir evento, rx:null");
					
	}
	
	public void setGSMModemEventListener(GSMModemEventListener listener) {
		this.glistener = listener;
	}

}
