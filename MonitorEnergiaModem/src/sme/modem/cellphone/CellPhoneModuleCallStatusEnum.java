package sme.modem.cellphone;

public enum CellPhoneModuleCallStatusEnum {
	
	ACTIVE,
	INCOMING_CALL_ON_HOLD,
	DAILING,
	OUTGOING_CALL_RINGING,
	INCOMING_CALL_RINGING,
	OUTGOING_CALL_ONHOLD,
	CALL_DISCONNECTED,
	CALL_CONNECTED;
	
	public static CellPhoneModuleCallStatusEnum fromNumber(int status) {
		switch(status) {
			case 0: return ACTIVE;
			case 1: return INCOMING_CALL_ON_HOLD;
			case 2: return DAILING;
			case 3: return OUTGOING_CALL_RINGING;
			case 4: return INCOMING_CALL_RINGING; 
			case 5: return OUTGOING_CALL_ONHOLD; 
			case 6: return CALL_DISCONNECTED;
			case 7: return CALL_CONNECTED;
			default: return null;
		}
	}
	

}
