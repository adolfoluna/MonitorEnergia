<%
	int sub = 0;
	
	if( request.getParameter("sub") != null ) 
		sub = Integer.parseInt(request.getParameter("sub"));
	
	int view = 0;
	
	if( request.getParameter("view") != null ) 
		view = Integer.parseInt(request.getParameter("view"));
	
	String pagina = "servicios/list_servicios.jsp";
	
	if(sub == 0)
		pagina = "servicios/list_servicios.jsp";
	
	if( sub == 0 ) {
		switch(view) {
			default:
			case 0: pagina = "servicios/list_servicios.jsp"; break;
			case 1: pagina = "servicios/command_servicios.jsp"; break;
		}
	}
	
	if( sub == 2 ) {
		switch(view) {
			default:
			case 0: pagina = "nodos/list_nodo.jsp"; break;
			case 1: pagina = "nodos/edit_nodo.jsp"; break;
		}
	}

%>

<%-- <jsp:include page="side_nav.jsp"/> --%>

<jsp:include page="<%= pagina %>"/>

