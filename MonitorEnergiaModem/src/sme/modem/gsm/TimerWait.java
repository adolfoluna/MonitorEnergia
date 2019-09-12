package sme.modem.gsm;

import java.util.Date;

public class TimerWait implements TimerWaitable {
	
	private boolean timeOutReached = false;
	private boolean waiting = false;
	
	public TimerWait() {
		
	}
	
	synchronized public void startWait(long timeOut) {
		
		//indicar que la espera se iniciara
		waiting = true;
		
		//inicializar bandera de tiempo de espera agotado
		timeOutReached = false;
		
		final long tiempoInicial = new Date().getTime();
        long tiempoTranscurrido = 0;
        
    	while( waiting && tiempoTranscurrido < timeOut ) {
            
    		try { 
    			
    			//esperar timeOut - tiempoTranscurrido
            	if( (timeOut-tiempoTranscurrido) > 0 )
            		wait(timeOut-tiempoTranscurrido);
            	else
            		wait(100);
            	
            	//calcular el tiempo transcurrido
            	tiempoTranscurrido = new Date().getTime()-tiempoInicial;
            	
            }catch(InterruptedException e) {
            	return; 
            }
        }
    	
    	//si sigue habiendo espera de respuesta significa que el tiempo de espera se agoto
    	if(waiting)
    		timeOutReached = true;
    	
    	//indicar que ya no se esta haciendo la espera
    	waiting = false;
	}
	
	synchronized public void stopWait() {
		
		//indicar que ya no se va a esperar
		waiting = false;
						
		//notificar a todos los threads que esten esperando que hubo un cambio
		notifyAll();
	}
	
	public boolean isTimeOutReached() {
		return timeOutReached;
	}
	
	public boolean isWaiting() {
		return waiting;
	}
	
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

}
