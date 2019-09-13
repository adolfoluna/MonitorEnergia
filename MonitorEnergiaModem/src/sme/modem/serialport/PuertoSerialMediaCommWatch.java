package sme.modem.serialport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.modem.media.comm.MediaCommConnectionListener;

public class PuertoSerialMediaCommWatch implements Runnable, PuertoSerialConnectionClosedListener {
	
	private static final Log log = LogFactory.getLog(PuertoSerialMediaCommWatch.class);
	
	private PuertoSerialMediaComm puerto;
	private PuertoSerialParams params;
	private Thread hilo;
	private boolean active = true;
	private boolean opened = false;
	private MediaCommConnectionListener listener;
	
	public PuertoSerialMediaCommWatch() {
		
	}
	
	public void setMediaCommConnectionListener(MediaCommConnectionListener listener) {
		this.listener = listener;
	}
	
	public void run() {
		
		log.info("inicio de proceso para monitorear puerto serial "+params.getPortName()+"....");
		
		while(active) {
			
			//intentar abrir puerto cada 15 segundos hasta lograrlo
			while(active && !opened) {
				
				//crear nueva instancia de puerto serial
				puerto = new PuertoSerialMediaComm();
				
				//indicar que se va a intentar abrir el puerto
				log.info("intentando abrir puerto "+params.getPortName()+"....................");
				
				//intentar abrir puerto y guardar bandera que indica si se abrio exitosamente
				opened = puerto.open(params);
				
				//si se abrio el puerto exitosamente salir de ciclo
				if( opened )
					break;
				
				log.info("no se pudo abrir puerto "+params.getPortName()+" reintentando en 15 segundos...........");
				
				//esperar 15 segundos
				try { Thread.sleep(15_000);	}catch(InterruptedException ex) { log.info("proceso de espera de 15 segundos interrumpido"); }
			}
			
			//escuchar cuando ocurra que se cierre el puerto
			puerto.setPuertoSerialConnectionClosedListener(this);
			
			//disparar el evento de que el medio de comunicacion esta disponible
			if( listener != null ) listener.mediaCommOpened(puerto);
			
			//esperar a que el puerto serial sea cerrado
			while(active && opened) {
				log.info("esperando evento de cierre de puerto "+params.getPortName());
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						log.info("proceso de espera interrumpido");
					}
				}
			}
			
			log.info("evento de cierre de puerto ocurrido "+params.getPortName());
		}
		
		log.info("fin de proceso para monitorear puerto serial "+params.getPortName()+"....");
	}
	
	public PuertoSerialMediaCommWatch(PuertoSerialParams params) {
		//guardar los parametros usados para conectarse al puerto serial
		this.params = params;
	}
	
	public void start() {
		
		//en caso de ya estar iniciado el hilo, indicarlo y salir 
		if( hilo != null ) {
			log.info("proceso de monitoreo de puerto serial ya iniciado....");
			return;
		}
		
		//guardar la referencia del hilo creado e iniciarlo
		hilo = new Thread(this);
		hilo.start();	
	}
	
	public void stop() {
		
		active = false;
		
		//si no hay ninguna referencia a un thread, salir de rutina
		if( hilo == null ) {
			log.info("proceso de monitoreo de puerto serial ya terminado.....");
			return;
		}
		
		//interrumpir hilo en caso de que este dormido
		if( hilo.isAlive() )
			hilo.interrupt();
		
		//cerrar puerto en caso de estar abierto
		if( puerto != null) {
			puerto.setPuertoSerialConnectionClosedListener(null);
			puerto.close();
		}
	}

	@Override
	public void puertoSerialConnectionClosed() {
		
		//quitar el listener para evitar que se vuelva a disparar evento de esta referencia
		puerto.setPuertoSerialConnectionClosedListener(null);
		
		//ocurre cuando el puerto serial se cierra
		opened = false;
		
		//disparar el evento de que el medio de comunicacion se cerro
		if( listener != null ) listener.mediaCommOpened(puerto);
		
		//notificar a este thread para despertarlo de la espera
		synchronized(this) {
			notifyAll();
		}
	}

	

}
