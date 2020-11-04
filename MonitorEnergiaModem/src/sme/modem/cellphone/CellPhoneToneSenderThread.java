package sme.modem.cellphone;

public class CellPhoneToneSenderThread implements Runnable {

	private CellPhoneModule modem;
	private String tones;
	
	public CellPhoneToneSenderThread(CellPhoneModule modem,String tones) {
		this.modem = modem;
		this.tones = tones;
	}
	
	@Override
	public void run() {
		
		if( modem == null )
			return;
		
		//esperar 3 segundos despues de contestar la llamada
		try {Thread.sleep(3_000);} catch(Exception ex) { ex.printStackTrace(); }
		
		//recorrer todos los caracteres de la cadena de tonos a generar
		for( int index  = 0; index < tones.length(); index++ ) {
			
			//tomar solo un caracter
			String temp = tones.substring(index, index+1);
			
			//enviar solo un tono a la vez
			modem.sendTone(temp);
			
			//esperar 1.5 segundo antes de enviar el siguiente tono
			try {
				Thread.sleep(1_500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
