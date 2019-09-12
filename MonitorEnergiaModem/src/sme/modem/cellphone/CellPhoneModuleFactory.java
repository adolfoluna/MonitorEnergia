package sme.modem.cellphone;

import sme.client.dto.ModemLocalDto;
import sme.modem.serialport.PuertoSerialParams;

public class CellPhoneModuleFactory {
	
	
	public static CellPhoneModuleService createCellPhoneModuleService(ModemLocalDto modem ) {
		CellPhoneModuleService cs = new CellPhoneModuleService(modem.getIdmodem(), createSerialParams(modem));
		return cs;
	}
	
	private static PuertoSerialParams createSerialParams(ModemLocalDto m) {
		PuertoSerialParams r = new PuertoSerialParams();
		r.setPortName(m.getPuerto());
		r.setBaudrate(m.getBaudrate());
		r.setDatabits(m.getDatabits());
		r.setFlowControl(null);
		r.setParity(m.getParity());
		r.setStopbits(m.getStopbits());
		return r;
	}

}
