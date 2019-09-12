package sme.ejb.timer;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;

@Stateless
public class TimerSessionBean {
	
	 @Resource
	 private SessionContext context;
	 
	 public TimerSessionBean() {
		 createTimer(10000);
	 }

	 public void createTimer(long duration) {
		 context.getTimerService().createTimer(duration, "Hello World!");
	 }

	 @Timeout
	 public void timeOutHandler(Timer timer) {
		 System.out.println("timeoutHandler : " + timer.getInfo());        
		 timer.cancel();
	 }

}
