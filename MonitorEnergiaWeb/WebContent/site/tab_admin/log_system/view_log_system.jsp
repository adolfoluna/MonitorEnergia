
	<input type="hidden" id="logname" value="<%= request.getParameter("id")%>" >

	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<label><b>Archivo <%= request.getParameter("id") %></b></label>
		
		<div class="btn-toolbar mb-2 mb-md-0">
		
			<button class="btn btn-sm btn-outline-secondary" onclick="traercompleto()">Completo</button>
			&nbsp;&nbsp;
			<button class="btn btn-sm btn-outline-secondary" onclick="traer_ultimas_200()">Ultimas 200 lineas</button>
			&nbsp;&nbsp;
			<input type="checkbox" id="actualizarlog" checked="checked"><label for="actualizarlog">actualizar log</label>
			&nbsp;&nbsp;
			<input type="checkbox" id="scrolla" checked="checked"><label for="scrolla">scroll automatico</label>
			&nbsp;&nbsp;
			<button class="btn btn-sm btn-outline-secondary" onclick="$('#texto').html('');">Borrar</button>
			&nbsp;&nbsp;
			<div class="btn-group mr-2">
		    	<input type="text" size="20" id="filtro_exp" placeholder="Expresi&oacute;n regular" value="">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="filtrar()">aplicar filtro</button>
		    	<input type="checkbox" id="ignorar"><label for="ignorar">ignorar mayusculas</label>
		  	</div>
		</div>
	</div>
	
	<pre id="texto" class="texto_log" style="background-color: black; color: white;"></pre>

<script>

	var ocupado = true;

	$(document).ready(
		function() {
			$("#sidenav").removeClass("d-md-block");
			$("#maindiv").removeClass("col-md-9");
			$("#maindiv").removeClass("col-lg-10");

			$("#maindiv").addClass("col-md-12");
			$("#maindiv").addClass("col-lg-12");

			$("#headnav").removeClass("fixed-top");			
			setInterval(timer, 5000)
		}
	);

	function timer() {

		if(ocupado || !$('#actualizarlog').prop('checked'))
			return;
		
		ocupado = true;

		buscar_linea(0,0);
	}

	var url = "rest/server/getvalue";
	
	var querylog =  {
			"operation":"read-log-file",
	        "encoding":"UTF-8",
	        "lines": 200,
	        "skip": 0,
	        "tail":true,
	        "address": [
	 	       		{"subsystem":"logging"},
	        		{"log-file":$("#logname").val()},
	        		]
		};

	var requestvars = { 
			data : null,
	    	contentType : 'application/json',
	    	type : 'POST',
	    	dataType: "json",
		};

	var ultima_linea = "";

	var filtro = null;

	function traer_ultimas_200() {

		ocupado = true;

		$('#texto').html("");

		querylog.lines = 200;
		querylog.skip = 0;
		querylog.tail = true;
		requestvars.data = JSON.stringify({ message: JSON.stringify(querylog)});
		
		$.ajax(url, requestvars)
		 .done(function(data) {
			 
			 if( data.outcome != "success") {
				 alert(data["failure-description"]);
				 ocupado = false;
				 return;
			 }

			for( i =0; i < data.result.length; i++ ) 
				agregar_linea(data.result[i]);

			//$('#texto').scrollTop($('#texto')[0].scrollHeight);
			$('html, body').animate({scrollTop:$(document).height()}, 'slow');
			ocupado = false;
 
		});
		
	}

	function buscar_linea(numpagina,ciclos) {

		ocupado = true;

		if(ciclos > 200 ) {
			alert("error, recursion mas de 200 ciclos buscando linea de log");
			ocupado = false;
			return;
		}
		
		aux = $("#texto font").last().text();
		aux = aux.substring(0,aux.length-1);

		if(aux.length <= 0 && ultima_linea.length <= 0 ) {
			ocupado = false;
			return;
		}

		if( aux.length <= 0 && ultima_linea.length > 0 )
			aux = ultima_linea;

		querylog.lines = 20;
		querylog.skip = numpagina;
		querylog.tail = true;
		requestvars.data = JSON.stringify({ message: JSON.stringify(querylog)});
		
		$.ajax(url,requestvars)
		 .done(function(data) {
			 
				 if( data.outcome != "success") {
					 alert(data["failure-description"]);
					 ocupado = false;
					 return;
				 }

				for( i =0; i < data.result.length; i++ ) {
	
					if( data.result[i] == aux ) {
						i++;
						la = 0;
						while(i < data.result.length) { 
							agregar_linea(data.result[i++]);
							la++;
						}

						agregar_lineas_log(numpagina-20,0,la);
	
						return;
					}
				}

				buscar_linea(numpagina+20,ciclos+1);
			});
	}

	function agregar_lineas_log(numpagina,ciclos,lineas_agregadas) {

		ocupado = true;

		if(ciclos >= 200 ) {
			alert("error, recursion mas de 200 ciclos actualizando log");
			ocupado = false;
			return;
		}

		if( numpagina < 0 ) {
			
			if($('#scrolla').prop('checked') && lineas_agregadas > 0 )
				$('html, body').animate({scrollTop:$(document).height()}, 'slow');
				//$('#texto').scrollTop($('#texto')[0].scrollHeight);

			ocupado = false;
			return;
		}

		querylog.lines = 20;
		querylog.skip = numpagina;
		querylog.tail = true;
		requestvars.data = JSON.stringify({ message: JSON.stringify(querylog)});
		
		$.ajax(url, requestvars)
		 .done(function(data) {

			 if( data.outcome != "success") {
				 alert(data["failure-description"]);
				 ocupado=false;
				 return;
			 }

			for( i =0; i < data.result.length; i++ )
				agregar_linea(data.result[i]);		

			agregar_lineas_log(numpagina-20,ciclos+1,lineas_agregadas);
		 
		});
	}

	function agregar_linea(aux) {

		ultima_linea = aux;

		if(filtro !=null && !filtro.test(aux) )
			return;
		
		temp = "";

		if( aux.indexOf("ERROR") >= 0 || aux.indexOf("SEVERE") >= 0)
			temp = " color=\"red\"";

		if( aux.indexOf("WARN") >= 0 ) {
			temp = " color=\"yellow\"";
			aux = "<b>"+aux+"</b>";
		}
		
		$("#texto").append("<font"+temp+">"+aux+"\r\n</font>");
	}

	function traercompleto() {
		if(!confirm("cargar archivo completo?"))
			return;
		$('#texto').html("");
		traerlog(0);
	}

	function traerlog(npagina) {

		ocupado = true;

		querylog.lines = 20;
		querylog.skip =npagina;
		querylog.tail = false;
		requestvars.data = JSON.stringify({ message: JSON.stringify(querylog)});
		
		$.ajax(url,requestvars)
		 .done(function(data) {
			 
			 if( data.outcome != "success") {
				 alert(data["failure-description"]);
				 ocupado = false;
				 return;
			 }

			 if( data.result.length <= 0 ) {
				 //$('#texto').scrollTop($('#texto')[0].scrollHeight);
				 $('html, body').animate({scrollTop:$(document).height()}, 'slow');
				 ocupado = false;
				 return;
			}
				 

			for( i =0; i < data.result.length; i++ ) 
				agregar_linea(data.result[i]);

			traerlog(npagina+20);
		});
	}

	function filtrar() {

		filtro = null;

		aux = $("#filtro_exp").val();

		if(aux.trim().length<=0 )
			return;
		if($('#ignorar').prop('checked'))
			filtro =new RegExp(aux.trim(),'i');
		else 
			filtro =new RegExp(aux.trim());
	}
</script>