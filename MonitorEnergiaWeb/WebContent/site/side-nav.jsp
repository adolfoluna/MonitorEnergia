<%
	int opcion = 0;
	if( request.getParameter("op") != null ) {
		opcion = Integer.parseInt(request.getParameter("op"));
	}

%>
<nav class="col-md-2 d-none d-md-block bg-light sidebar" id="sidenav" style="display:none">

	<div class="sidebar-sticky">
	        
		<h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
			<span>Configuraci&oacute;n</span>
			<a class="d-flex align-items-center text-muted" href="#"></a>
		</h6>
		      
		<ul class="nav flex-column">
		  <li class="nav-item"><a class="nav-link <% if(opcion==0) out.print("active");  %>" href="index">Puertos seriales<span class="sr-only">(current)</span></a></li>
		  <li class="nav-item"><a class="nav-link <% if(opcion==1) out.print("active");  %>" href="index?op=1">Modem locales</a></li>
		  <li class="nav-item"><a class="nav-link <% if(opcion==2) out.print("active");  %>" href="index?op=2">Nodos</a></li>
		</ul>
	
		<h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
			<span>Administraci&oacute;n</span>
			<a class="d-flex align-items-center text-muted" href="#"></a>
		</h6>
		         
		<ul class="nav flex-column">
		  <li class="nav-item"><a class="nav-link <% if(opcion==3) out.print("active");  %>" href="index?op=3">Servicios corriendo<span class="sr-only">(current)</span></a></li>
		  <li class="nav-item"><a class="nav-link <% if(opcion==4) out.print("active");  %>" href="index?op=4">Monitoreo de colas</a></li>
		  <li class="nav-item"><a class="nav-link <% if(opcion==6) out.print("active");  %>" href="index?op=6">Monitoreo de websocket</a></li>
		  <li class="nav-item"><a class="nav-link <% if(opcion==5) out.print("active");  %>" href="index?op=5">Log sistema</a></li>
		</ul>
		
	</div>
	
</nav>