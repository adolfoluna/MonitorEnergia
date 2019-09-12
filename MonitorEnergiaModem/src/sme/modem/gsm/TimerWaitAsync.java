package sme.modem.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimerWaitAsync extends TimerWait  implements Runnable {

	private static Log log = LogFactory.getLog(TimerWaitAsync.class);
	
	private boolean active = true;
	private Thread hilo = new Thread(this);
	private WaitTimeOutListener listener;
	private long timeOut = 10_000;
	
	public TimerWaitAsync() {
		
	}
	
	public void run() {
		
		log.info("inicio de proceso de TimerWaitAsync..........");
		try {
			while(active) {
				
				//esperar a que se dispare la espera de evento
	            synchronized(this) {
	                while(!isWaiting() && active) {
	                    try { wait(); } 
	                    catch (InterruptedException e) { log.info("proceso TimerWaitAsync interrumpido");return; }
	                }
	            }
	            
	            if(!active)
	            	return;
				
	            super.startWait(timeOut);
	            
	            if( isTimeOutReached() && listener != null )
	            	listener.waitTimedOut();
			}
		}finally {
			log.info("fin de proceso de TimerWaitAsync..........");
		}
		
	}
	
	public void setWaitTimeOutListener(WaitTimeOutListener listener) {
		this.listener = listener;
	}
	
	public void stop() {
		
		active = false;
		
		if( hilo != null && hilo.isAlive() )
    		hilo.interrupt();
		
		hilo = null;
	}
	
	synchronized public void startWait(long timeOut) {
		
		if( hilo == null )
			return;
		
		if( !hilo.isAlive() )
			hilo.start();
		
		this.timeOut = timeOut;
		
		//indicar que se iniciara la espera
		setWaiting(true);
		
		//notificar a este hilo que esta esperando a que cambie el estado de la bandera waiting
		notifyAll();
				
	}
	
	synchronized public void stopWait() {
		
		//indicar que ya no se va a esperar
		setWaiting(false);
						
		//notificar a todos los threads que esten esperando que hubo un cambio
		notifyAll();
	}
	
}
