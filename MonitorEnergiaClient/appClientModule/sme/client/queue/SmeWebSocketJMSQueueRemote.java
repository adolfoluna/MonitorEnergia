package sme.client.queue;

import java.io.Serializable;

public interface SmeWebSocketJMSQueueRemote {

	void write(Serializable object);

}