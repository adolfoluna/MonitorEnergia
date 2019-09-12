package sme.modem.cellphone;

public class CellPhoneModuleSMSStatus {

	private boolean sendingSMS = false;
	
	public CellPhoneModuleSMSStatus() {
		
	}
	
	public void updateStatus() {
		
	}

	public boolean isSendingSMS() {
		return sendingSMS;
	}

	public void setSendingSMS(boolean sendingSMS) {
		this.sendingSMS = sendingSMS;
	}
}
