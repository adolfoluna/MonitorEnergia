package sme.modem.serialport;

import sme.modem.media.comm.MediaComm;
import sme.modem.media.comm.MediaCommDataListener;

public class PuertoSerialMediaComm extends PuertoSerial implements MediaComm, PuertoSerialDataReceivedListener {

	private MediaCommDataListener mediaCommDataListener;
	
	public PuertoSerialMediaComm() {
		setPuertoSerialDataReceivedListener(this);
	}
	
	@Override
	public boolean write(byte[] data) {
		return super.write(data);
	}

	@Override
	public void setMediaCommDataListener(MediaCommDataListener listener) {
		mediaCommDataListener = listener;
	}

	@Override
	public void puertoSerialData(byte[] data, int length) {
		if(mediaCommDataListener != null)
			mediaCommDataListener.onMediaCommDataRecived(data, length);
	}
	
	

}
