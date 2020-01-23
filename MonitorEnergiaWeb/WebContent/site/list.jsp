<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

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
  		<script src="site/js/notify.js"></script>
  		
  		<style>
			.dot {
  				height: 25px;
  				width: 25px;
   				background-color: #bbb; 
  				border-radius: 50%;
  				display: inline-block;
  				vertical-align: middle;
				}
			
			.dotred {
				height: 25px;
  				width: 25px;
   				background-color: red; 
  				border-radius: 50%;
  				display: inline-block;
  				vertical-align: middle;
			}
			
			.dotgreen {
				height: 25px;
  				width: 25px;
   				background-color: green; 
  				border-radius: 50%;
  				display: inline-block;
  				vertical-align: middle;
			}
			
		</style>
	</head>
	
	<body>

		<nav id="headnav" class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow">
	      <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="list">SME</a>
	      
	    </nav>
	
		<div class="container-fluid">
		
			<div class="row">
				
				<nav class="col-md-2 d-none d-md-block bg-light sidebar" id="sidenav" style="display:none">
						<div class="sidebar-sticky"></div>
				</nav>
				
				<div id="maindiv" role="main" class="col-md-9 ml-sm-auto col-lg-10 px-4">
				
					<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
						<h1 class="h2">Lista nodos de monitoreo</h1>
						<button onclick="notificar_test()">Probar notificaci&oacute;n</button>
					</div>
					
					<div class="table-responsive">
						<table class="table table-striped table-sm" id="tablaDatos">
				          	<thead>
								<tr>
									<th>Id</th>
									<th>Nombre</th>
									<th>Numero</th>
									<th>CFE</th>
									<th>Fecha</th>
									<th>UPS</th>
									<th>Fecha</th>
								</tr>
							</thead>
				             <tbody>
				               
				             </tbody>
						</table>
					</div>
			</div>
				
		

      		</div>
    	</div>
	
<script type="text/javascript">


	var nodos = [];

	$(document).ready(function() {

		$.notify.addStyle("alert-success",{
				html:
					"<div class='notifyjs-wrapper notifyjs-hidable'>"+
						"<div class='notifyjs-arrow'></div>"+
						"<div class='notifyjs-container'>"+
							"<div class='notifyjs-bootstrap-base notifyjs-bootstrap-success'>"+
								"<span data-notify-html='title'/>"+
							"</div>"+
						"</div>"+
					"</div>"
			});

		$.notify.addStyle("alert-warn",{
			html:
				"<div class='notifyjs-wrapper notifyjs-hidable'>"+
					"<div class='notifyjs-arrow'></div>"+
					"<div class='notifyjs-container'>"+
						"<div class='notifyjs-bootstrap-base notifyjs-bootstrap-warn'>"+
							"<span data-notify-html='title'/>"+
						"</div>"+
					"</div>"+
				"</div>"
		});

		$.notify.addStyle("alert-error",{
			html:
				"<div class='notifyjs-wrapper notifyjs-hidable'>"+
					"<div class='notifyjs-arrow'></div>"+
					"<div class='notifyjs-container'>"+
						"<div class='notifyjs-bootstrap-base notifyjs-bootstrap-error'>"+
							"<span data-notify-html='title'/>"+
						"</div>"+
					"</div>"+
				"</div>"
		});
		
		let socket = new WebSocket("ws://localhost:8080/MonitorEnergiaWeb/monitorwebsocket");

		socket.onopen = function(e) {
		  //console.log("[open] Connection established, send -> server");
			  $.notify("Conexion exitosa a notificaciones","success");
		};

		socket.onmessage = function(event) {
			var obj = JSON.parse(event.data);
			procesar_data(obj);
	  		upsertRow(obj);
	  		notificar(obj);
		};

		socket.onclose = function(event) {
			$.notify("Conexion a las notificaciones cerrada","warn");
			
		  if (event.wasClean) {
				//console.log(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
		  } else {
		    // e.g. server process killed or network down
		    // event.code is usually 1006 in this case
			  socket = new WebSocket("ws://localhost:8080/MonitorEnergiaWeb/monitorwebsocket");	
		  }
		};

		socket.onerror = function(error) {
			$.notify("Error en la conexion a las notificaciones "+error,message,"error");
			//console.log("error "+error.message);
	  		socket = new WebSocket("ws://localhost:8080/MonitorEnergiaWeb/monitorwebsocket");
		};
	});
	
	//1550271617000
	function procesar_data(obj) {

		if( obj.hasOwnProperty("cfeFecha") && obj.cfeFecha != null) 
			obj.cfeFecha = moment(new Date(obj.cfeFecha)).format('YYYY-MM-DD HH:mm:ss');
		else
			obj.cfeFecha = "-";

        if( obj.hasOwnProperty("upsFecha") && obj.upsFecha != null) 
			obj.upsFecha = moment(new Date(obj.upsFecha)).format('YYYY-MM-DD HH:mm:ss');
		else
			obj.upsFecha = "-";
		
	}

	function upsertRow(obj) {

		if( $("#row"+obj.idnodo).length ) {
			updateRow(obj);
			return;
		}
		
		$("#tablaDatos tbody").append(createRow(obj));
		
	}

	function createRow(obj) {
        var cadena = "<tr id=\"row"+ obj.idnodo + "\">";
        cadena+="<td>"+obj.idnodo+"</td>";
        cadena+="<td>"+obj.nombre+"</td>";
        cadena+="<td>"+obj.numero+"</td>";
        cadena+="<td><span id=\"cfe"+obj.idnodo+"\" class=\""+getCFEDotClassName(obj)+"\"></span></td>"; 
        cadena+="<td id=\"cfefecha"+obj.idnodo+"\">"+obj.cfeFecha+"</td>";
        cadena+="<td><span  id=\"ups"+obj.idnodo+"\" class=\""+getUPSDotClassName(obj)+"\"></span></td>";
        cadena+="<td id=\"upsfecha"+obj.idnodo+"\">"+obj.upsFecha+"</td>";
        cadena+="</tr>";
        return cadena;
    }

    function updateRow(obj) {
    	$("#cfe"+obj.idnodo).attr("class",getCFEDotClassName(obj));
		$("#ups"+obj.idnodo).attr("class",getUPSDotClassName(obj));
		$("#cfefecha"+obj.idnodo).text(obj.cfeFecha);
		$("#upsfecha"+obj.idnodo).text(obj.upsFecha);
	}

    function notificar(obj) {
        //console.log(obj);
        
        //if( !($("#row"+obj.idnodo).length) )
        //	return;

		var clase = "alert-warn";
        
        var mensaje = obj.idnodo+" - " + obj.nombre + "<br/>";

        mensaje+="CFE:";
        
        if( obj.cfePresente == null ) mensaje+="<span class='dot'/>";
        if( obj.cfePresente != null && (obj.cfePresente == "true" || obj.cfePresente == true) ) mensaje+="<span class='dotgreen'/>";
       	if( obj.cfePresente != null && (obj.cfePresente == "false" || obj.cfePresente == false) ) mensaje+="<span class='dotred'/>";

        mensaje+="&nbsp;&nbsp;UPS:"
        if( obj.upsPresente == null ) mensaje+="<span class='dot'/>";
        if( obj.upsPresente != null && (obj.upsPresente == "true" || obj.upsPresente == true) ) mensaje+="<span class='dotgreen'/>"; 
        if( obj.upsPresente != null && (obj.upsPresente == "false" || obj.upsPresente == true) ) mensaje+="<span class='dotred'/>";

		$.notify({title:mensaje}, { style: getClase(obj)});
    	
    }

    function notificar_test() {

        var obj = {
                idnodo : 1,
                nombre : "Nodo",
                cfeFecha: 1550371617000,
                upsFecha: 1550371617000,
                upsPresente: "true",
                cfePresente: "true",
                };

        procesar_data(obj);

        var mensaje = obj.idnodo+" - " + obj.nombre + "<br/>";
        mensaje+="CFE:<span class='"+getCFEDotClassName(obj)+"'/>";
        mensaje+="&nbsp;&nbsp;UPS:<span class='"+getUPSDotClassName(obj)+"'/>";
        
		$.notify({title:mensaje}, { style: getClase(obj)});

		upsertRow(obj);
    }


    function getClase(obj) {

        if( obj.cfePresente == null || obj.upsPresente == null )
            return "alert-warn";

        if( (obj.cfePresente == "true" ||  obj.cfePresente == true) 
                && (obj.upsPresente == "true" || obj.upsPresente == true) )
            return "alert-success";

        return "alert-error";
    }

    function getCFEDotClassName(obj) {
		if( obj.cfePresente == null )
			return "dot";

		if( obj.cfePresente  == true || obj.cfePresente == "true")
			return "dotgreen";

		return "dotred";
    }

    function getUPSDotClassName(obj) {
		if( obj.upsPresente == null )
			return "dot";

		if( obj.upsPresente  == true || obj.upsPresente == "true")
			return "dotgreen";

		return "dotred";
    }
	
	




</script>
	</body>
</html>