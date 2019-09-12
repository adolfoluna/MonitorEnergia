package sme.modem.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GSMModemBuffer {

	private static final Log log = LogFactory.getLog(GSMModemBuffer.class);
	private StringBuilder sb = new StringBuilder();
	
	private GSMModemBufferListener listener;
	
	public GSMModemBuffer() {
		
	}
	
	public void setGSMModemBufferListener(GSMModemBufferListener listener) {
		this.listener = listener;
	}
	
	synchronized public void write(byte []bytes,boolean writingResponse) {
		
		for(int i = 0; i < bytes.length; i++) {
			
			switch(bytes[i]) {
			
				//en caso de encontrar una nueva linea
				//o el signo >
				case '>':
				case '\n':
					
					//agregar el byte al buffer
					sb.append((char) bytes[i]);
					
					//agregar la linea a la cola de respuestas
					agregarlinea(writingResponse);
					
					break;
					
				default:
					//agregar el byte al buffer
					sb.append((char) bytes[i]);
					break;
			}
		}
	}
	
	public void clear() {
		sb.setLength(0);
	}
	
	public String getBuffer() {
		String aux = sb.toString();
		clear();
		return aux;
	}
	
	private void agregarlinea(boolean writingResponse) {
		
		//si esta vacio el buffer o tiene 2 o menos caracteres salir
		if( sb.length() <= 2 )
			return;
		
		String aux = sb.toString();
		
		//si esta escribiendo la respuesta en el buffer 
		//revisar si termina en OK\r\n o ERROR\r\n
		if( writingResponse) {
			
			//analizar si hay algun evento dentro de la respuesta
			analizarSiHayEvento(aux);
			
			//si el buffer termina en OK o en ERROR significa que se termino la respuesta
			if( customEndsWith("OK", aux) || customEndsWith("ERROR", aux) || customEndsWith2(">", aux)) {
				
				//borrar buffer
				clear();
				
				//disparar el evento de respuesta solo si esta asignado
				if( listener != null ) new GSMModemBufferResponseDispatcher(listener, aux);
			}
			
			//salir de rutina
			return;
		}
		
		//si se esta escibiendo en el buffer un evento y termina en nueva linea
		//disparar evento de evento recibido
		if( sb.charAt(sb.length()-2) == '\r' && sb.charAt(sb.length()-1) == '\n' ) {
			
			//borrar buffer
			clear();
			
			if( listener != null )	new GSMModemBufferEventDispatcher(listener, aux);
		}

	}
	
	private boolean customEndsWith(String search,String haystack) {
		
		int index = haystack.length() - 1;
		
		//validar que haystack tenga la longitud de search mas 2 caracteres de nueva linea
		if( haystack.length()  < (search.length()+2) )
			return false;
		
		//si la cadena no termina en nueva linea regresar false
		if( haystack.charAt(index--) != '\n' || haystack.charAt(index--) != '\r')
			return false;
		
		for( int i = search.length()-1; i >= 0 && index >= 0; i-- ) {
			if( search.charAt(i) != haystack.charAt(index--) )
				return false;
		}
		
		return true;
	}
	
	private boolean customEndsWith2(String search,String haystack) {
		
		int index = haystack.length() - 1;
		
		//validar que haystack tenga la longitud de search mas 2 caracteres de nueva linea
		if( haystack.length()  < (search.length()+2) )
			return false;
		
		for( int i = search.length()-1; i >= 0; i-- ) {
			if( search.charAt(i) != haystack.charAt(index--))
				return false;
		}
		
		//si la cadena no empieza en nueva linea regresar false
		if( haystack.charAt(index--) != '\n' || haystack.charAt(index--) != '\r')
			return false;
		
		return true;
	}
	
	private void analizarSiHayEvento(String respuesta) {
		
				
		String temp[] = respuesta.split("\r\n");
		
		if(temp == null || temp.length < 0 )
			return;
		
		String cadenasineventos = "";
		
		boolean flag = false;
					
		for(String s : temp ) {
			
			if( s.startsWith("+UCALLSTAT:") || s.startsWith("RING") || s.startsWith("+CMTI:") ) {
				flag = true;
				
				log.info("evento "+s+" contenido en respuesta");
				
				if(listener!=null) {
					log.info("invocando el listener de eventos");
					new GSMModemBufferEventDispatcher(listener, s);
				}
			}
			else
				cadenasineventos = cadenasineventos +  s + "\r\n";
			
		}
		
		//si se encontro algun evento entonces asignar la nueva cadena sin eventos	
		if( flag ) {
			log.info("borrando buffer y asignando "+cadenasineventos.replace("\r\n", "\\n"));
			clear();
			sb.append(cadenasineventos);
		}
	}
}
