package sme.modem.gsm;

import sme.modem.cellphone.CellPhoneModuleService;
import sme.modem.serialport.PuertoSerialParams;

public class MainTest {
	
	 public static void main(String[] args) throws Exception {
		 
		 PuertoSerialParams params = new PuertoSerialParams("tty.usbserial-00002014A", 115200, 8, "NONE", 1, "NONE");
		 CellPhoneModuleService s = new CellPhoneModuleService(1, params);
		 s.start();
		 Thread.sleep(10_000);
		 System.out.println("**************************");
		 s.stop();
		 
	 }

}
