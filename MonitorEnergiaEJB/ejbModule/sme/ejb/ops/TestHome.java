package sme.ejb.ops;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.dto.NodoDto;
import sme.client.ops.remote.TestRemote;

@Stateless
@Remote(TestRemote.class)
public class TestHome implements TestRemote {
	
	private static final Log log = LogFactory.getLog(TestHome.class);

	public TestHome() {
	
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.ops.TestRemote#test(sme.client.dto.NodoDto)
	 */
	@Override
	public void test(NodoDto nodo) {
		log.info("metodo test..........................");
		log.info(nodo.toString());
	}
	
	/* (non-Javadoc)
	 * @see sme.ejb.ops.TestRemote#test()
	 */
	@Override
	public void test() {
		log.info("metodo test..........................");
	}
	
}
