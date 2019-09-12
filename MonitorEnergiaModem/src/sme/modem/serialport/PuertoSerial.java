package sme.modem.serialport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PuertoSerial implements SerialPortDataListener {
	
	private static final Log log = LogFactory.getLog(PuertoSerial.class);
	
	private SerialPort serialport;
	
	private String portName;
	
	private PuertoSerialConnectionClosedListener conClosedListener;
	private PuertoSerialDataReceivedListener dataReceivedListener;
	
	public PuertoSerial() {
	
	}
	
	public void setPuertoSerialConnectionClosedListener(PuertoSerialConnectionClosedListener listener) {
		this.conClosedListener = listener;
	}
	
	public void setPuertoSerialDataReceivedListener(PuertoSerialDataReceivedListener listener) {
		dataReceivedListener = listener;
	}
	
	private void setParams(PuertoSerialParams params) {
		
		//intentar abrir el puerto serial
		serialport = SerialPort.getCommPort(params.getPortName());
		
		//no continuar si el puerto serial que regreso la funcion es null
		if( serialport == null )
			return;
		
		serialport.setBaudRate(params.getBaudrate());
		serialport.setNumDataBits(params.getDatabits());
		
		//poner la paridad del puerto
		if( params.getParity() == null )
			params.setParity("NONE");
		
		//asignar la paridad
		switch(params.getParity().toUpperCase().trim()) {
			case "ODD": serialport.setParity(SerialPort.ODD_PARITY); break;
			case "EVEN": serialport.setParity(SerialPort.EVEN_PARITY); break;
			case "SPACE": serialport.setParity(SerialPort.SPACE_PARITY); break;
			case "MARK": serialport.setParity(SerialPort.MARK_PARITY); break;
			default: serialport.setParity(SerialPort.NO_PARITY); break;
		}
		
		//poner el numero de stop bits
		switch(params.getStopbits()) {
			default:
			case 1: serialport.setNumStopBits(SerialPort.ONE_STOP_BIT); break;
			case 2: serialport.setNumStopBits(SerialPort.TWO_STOP_BITS); break;
			case 3: serialport.setNumStopBits(SerialPort.ONE_POINT_FIVE_STOP_BITS); break;
		}
		
	}
		
	public boolean open(PuertoSerialParams params) {
		
		if( params == null ) {
			log.error("error, parametros de puerto serial en nulo");
			return false;
		}
		
		if( params.getPortName() == null ) {
			log.error("error, nombre de puerto serial a abrir en nulo");
			return false;
		}
		
		//guardar el nombre del puerto que se esta abriendo
		portName = params.getPortName();
		
		//actualizar los parametro de conexion del puerto serial
		setParams(params);
		
		if( serialport == null ) {
			log.error("error, puerto serial en null");
			return false;
		}
		
		log.info("intentando abir puerto "+portName+"..........");
		
		if(!serialport.openPort() ) {
			
			//disparar evento indicando que el puerto serial se cerro
			//en terminos practicos el NO abir existosamente el puerto, significa que se cerro
			if( conClosedListener != null )
				conClosedListener.puertoSerialConnectionClosed();
			
			return false;
		}
		
		log.info("puerto "+portName+" exitosamente abierto");
		
		//agregar para escuchar cuando llegue un dato
		serialport.addDataListener(this);
		
		//regresar true
		return true;
		
	}
	
	public void close() {
		
		//si no hay un puerto asignado salir
		if( serialport == null ) {
			log.error("error intentando cerrar puerto que no existe");
			return; 
		}
		
		//intentar cerrar el puerto
		try {
			serialport.closePort();
			log.info("puerto serial cerrado "+portName);
		}catch(Exception ex) {
			log.error("error al intentar cerrar puerto "+portName);
			ex.printStackTrace();
		}
		
		//disparar evento indicando que el puerto se cerro
		if( conClosedListener != null )
			conClosedListener.puertoSerialConnectionClosed();

	}

	public boolean write(byte buffer[]) {
		
		///validar que este asignado el puerto
		if( serialport == null ) {
			log.error("error, intentando escribir a un puerto que no esta abierto "+portName);
			return false;
		}
		
		//intentar escribir al puerto, en caso de ser exitoso regresar true
		if( serialport.writeBytes(buffer, buffer.length) > 0 )
			return true;
		
		//indicar error de que no se pudo realizar el write
		log.error("error, no se pudo escribir al puerto serial "+portName);
		
		//intentar cerrar puerto serial
		close();
		
		//regresar que no se pudo enviar el comando
		return false;
		
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		
		//si el evento es de que se recibio un dato continuar
		if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			return;
		
		if( serialport.bytesAvailable() <= 0 )
			return;
		
		try {
			
			//crear arreglo para leer la respuesta
			byte[] data = new byte[serialport.bytesAvailable()];
			
			//leer la respuesta del puerto serial
			int len = serialport.readBytes(data, data.length);

			//disparar evento en caso de haber un listener
			if( dataReceivedListener != null )
				dataReceivedListener.puertoSerialData(data, len);
			
		}catch(Exception ex) {
			
			log.error("error al intentar escribir a puerto");
			
			ex.printStackTrace();
			
			//intentar cerrar puerto
			close();
		}
					
	}
	
}
