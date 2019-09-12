package sme.client.queue;

import java.io.Serializable;

public interface SmeMonitoreoJMSQueueRemote {

	void write(Serializable object, int priority);
	
	public void write(Serializable object,int priority,long timetolive);

	Serializable read(long timeout);

}