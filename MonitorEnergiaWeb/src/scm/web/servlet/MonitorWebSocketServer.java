package scm.web.servlet;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sme.client.db.remote.NodoRemote2;
import sme.client.dto.NodoDto;

@ServerEndpoint(value = "/monitorwebsocket")
public class MonitorWebSocketServer {
	
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	private static final Log log = LogFactory.getLog(MonitorWebSocketServer.class);
	
	//@EJB private NodoRemote2 nodoRemote;
	
	public MonitorWebSocketServer() {
		
	}
	
	@OnOpen
    public void onOpen(Session session) {
		
		NodoRemote2 nodoRemote = null;
		
		try {
			Context c = new InitialContext();
			nodoRemote = (NodoRemote2) c.lookup("java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2");
		} catch (NamingException e1) {
			log.error("error al intentar buscar nombre: java:global/MonitorEnergiaApp/MonitorEnergiaEJB/NodoHome2");
			e1.printStackTrace();
		}
		
        //System.out.println(format("%s joined the chat room.", session.getId()));
		log.info("cliente conectado:"+session.getId()+" conectado");
		
		int page = 0;
		
		while(true) {
			
			List<NodoDto> res = nodoRemote.getNodosActivos(page, 10);
			
			//si no se encontraron resultados romper ciclo
			if( res == null || res.size() <= 0 )
				break;
					
			//enviar todos los resultados al cliente conectado
			for(NodoDto n : res) {
				try {
					session.getBasicRemote().sendText(n.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//incrementar pagina
			page++;
		}
		
        peers.add(session);
    }

	@OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException {
		log.info("cliente:"+session.getId()+" mensaje:"+message);
        //broadcast the message
        for (Session peer : peers) {
            if (!session.getId().equals(peer.getId())) { // do not resend the message to its sender
                peer.getBasicRemote().sendObject(message);
            }
        }
    }
	
	@OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        peers.remove(session);
        log.info("cliente desconectado:"+session.getId()+" size:"+peers.size());
    }
	
	public static void sendMessages(String message) {
		
		try {
			
			log.info("enviando mensaje a clientes:"+peers.size()+" mensaje:"+message);
			
			//ciclo para recorrer todos los clientes conectados
			for( Session peer : peers ) {
				
				//intentar enviar el mensaje al cliente conectado
				try {
					peer.getBasicRemote().sendText(message);
				}catch(Exception ex) {
					log.error("error al intentar enviar mensaje a cliente "+ex.getMessage());
				}
				
			}
			
		} catch(Exception ex) {
			log.error("error al intentar recorrer los clientes "+ex.getMessage());
			ex.printStackTrace();
		}
		
	}

}

