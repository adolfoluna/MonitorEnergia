<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	int opcion = 0;

	if( request.getParameter("op") != null ) {
		opcion = Integer.parseInt(request.getParameter("op"));
	}
	
	int view = 0;
	
	if( request.getParameter("view") != null ) 
		view = Integer.parseInt(request.getParameter("view"));

	String pagina = "";
	
	if(opcion == 0)
		pagina = "tab_configuracion/puertos_seriales/puertos_seriales.jsp";
	
	if(opcion == 1 && view == 0 )
		pagina = "tab_configuracion/modems_locales/list_modemlocal.jsp";
	
	if(opcion == 1 && view == 1 )
		pagina = "tab_configuracion/modems_locales/edit_modemlocal.jsp";
	
	if(opcion == 2 && view == 0 )
		pagina = "tab_configuracion/nodos/list_nodo.jsp";
	
	if(opcion == 2 && view == 1 )
		pagina = "tab_configuracion/nodos/edit_nodo.jsp";
	
	if(opcion == 3 && view == 0 )
		pagina = "tab_admin/servicios/list_servicios.jsp";
	
	if(opcion == 3 && view == 1 )
		pagina = "tab_admin/servicios/command_servicios.jsp";
	
	if(opcion == 4 )
		pagina = "tab_admin/monitoreo_colas/monitoreo_colas_main.jsp";
	
	if(opcion == 5 && view == 0)
		pagina = "tab_admin/log_system/list_log_system.jsp";
	
	if(opcion == 5 && view == 1)
		pagina = "tab_admin/log_system/view_log_system.jsp";
	
	if(opcion == 6 )
		pagina = "tab_admin/websocket_test/monitoreo_websocket_main.jsp";
	
%>
<html>

	<head>
		<meta charset="UTF-8">
		<title>Sistema de monitoreo de energia</title>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script src="site/js/popper.min.js"></script>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
		<link rel="stylesheet" href="site/css/test.css">
		
  		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
  		<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>
  		<script src="site/js/main.js"></script>
	</head>
	
	<body>

		<nav id="headnav" class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow">
	      <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="index">SME</a>
	      <ul class="navbar-nav px-3">
	        <li class="nav-item text-nowrap">
	          <a class="nav-link" href="#">Sign out</a>
	        </li>
	      </ul>
	    </nav>
	
		<div class="container-fluid">
			<div class="row">
				
				<jsp:include page="side-nav.jsp"/>
				
				<div id="maindiv" role="main" class="col-md-9 ml-sm-auto col-lg-10 px-4">
					<jsp:include page="<%= pagina %>"/>
				</div>
				
		

      		</div>
    	</div>
	
<script type="text/javascript">

$(document).ready(function() {
	  $('[data-toggle=offcanvas]').click(function() {
	    $('.row-offcanvas').toggleClass('active');
	  });
	});




</script>
	</body>
</html>