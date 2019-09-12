package scm.web.servlet;

import java.io.IOException;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sme.client.db.remote.NodoRemote2;
import sme.client.ops.remote.TestRemote;

/**
 * Servlet implementation class Test
 */
@WebServlet("/test")
public class TestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	@EJB(lookup="java:app/MonitorEnergiaEJB/NodoHome2!sme.client.db.remote.NodoRemote2")
	private NodoRemote2 r;
	
	@EJB(lookup="java:app/MonitorEnergiaEJB/TestHome!sme.client.ops.remote.TestRemote")
	private TestRemote tr;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//r.listarModems();
		//r.updateNodo(1, 1, new Date());
		//tr.test();
		response.getWriter().append("Hola mundo Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
