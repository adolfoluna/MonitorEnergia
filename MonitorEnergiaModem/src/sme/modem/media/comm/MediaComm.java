package sme.modem.media.comm;

public interface MediaComm {
	
	//regresa true indicando que si se pudo realizar correctamente la escritura
	public boolean write(byte data[]);
	
	//evento que ocurre cuando hay datos disponibles
	public void setMediaCommDataListener(MediaCommDataListener listener);

}
