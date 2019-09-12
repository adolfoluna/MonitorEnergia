package sme.modem.queue;

import sme.client.dto.NodoDto;
import sme.client.dto.NodoStatusNotification;

public interface MonQueueReaderWriterRemote {

	void write(NodoStatusNotification nodoStatus);

	NodoDto read();

}