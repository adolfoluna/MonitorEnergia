
	<input type="hidden" id="idmodem" value="<%= request.getParameter("id")%>" >

	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<h1 class="h2">Envio de comandos</h1>
	</div>
	
	<div class="row">
			id:<%= request.getParameter("id")%>
			&nbsp;
			N&uacute;mero: <%= request.getParameter("num")%>
			&nbsp;
			Puerto: <%= request.getParameter("puerto")%>
	</div>
	
	<div class="row">
	
		<div class="dropdown">
			<button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Comandos</button>
			<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(0))" href="#">Listado de operadores disponibles</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(1))"  href="#">Firmarse a operador numerico</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(2))"  href="#">Firmarse a operador por nombre</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(3))"  href="#">Estatus registro operador</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(4))"  href="#">Enviar sms como texto</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(5))"  href="#">Avisar cuando llegue sms</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(6))"  href="#">Enviar sms</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(7))"  href="#">Leer sms en registro 1</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(8))"  href="#">Borrar sms en registro 1</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(13))"  href="#">Leer todos los mensajes</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(14))"  href="#">Borrar todos los mensajes</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(9))"  href="#">Habilitar notificaciones de llamadas</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(10))"  href="#">Hacer llamada</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(11))"  href="#">Colgar llamada</a>
			  <a class="dropdown-item" onclick="$('#textComando').val(getComando(12))"  href="#">Contestar llamada</a>
			</div>
		</div>
		&nbsp;&nbsp;
		Tiempo de espera:<input type="text" id="input_time" size="5" value="1000">
			<button onclick="enviar()">enviar comando</button>
	</div>
	
	<div class="row">
		
		<div class="col-sm-5">
			<textarea id="textComando" rows="10" cols="50"></textarea>
		</div>
		
		<div class="col-sm-6">
			Respuestas
			<pre id="span_res" style="white-space:pre-wrap; word-wrap:break-word;"></pre>	
		</div>
	</div>

<script>

	function getComando(cn) {
		switch(cn) {
			case 0: return "AT+COPS=?";
			case 1: return "AT+COPS=1,2,\"310260\"";
			case 2: return "AT+COPS=1,1,\"TELCEL\"";
			case 3: return "AT+CREG?";
			case 4: return "AT+CMGF=1";
			case 5: return "AT+CNMI=1,1";
			case 6: return "AT+CMGS=\"+5216641518045\"";
			case 7: return "AT+CMGR=1";
			case 8: return "AT+CMGD=1";
			case 9: return "AT+UCALLSTAT=1";
			case 10: return "ATD+5216641518045;";
			case 11: return "ATH";
			case 12: return "ATA";
			case 13: return "AT+CMGL";
			case 14: return "AT+CMGD=0,4";
			default: return "AT";
		}
	}
	
	function enviar() {

		if($("#textComando").val().length <= 0 ) {
			alert("comando vacio");
			return;
		}
		
		object = {
				idmodem: 1,
				command: $("#textComando").val()+"\r\n",
				timeout: $("#input_time").val()
				};
		
		$.ajax("rest/modemmanager/sendcommand", {
		    data : JSON.stringify(object),
		    contentType : 'application/json',
		    type : 'POST',
		    dataType: "text",
		})
		 .done(function(data) {
			 	//$("#span_res").text(data.replace(/\n/g, "\\n"));
			 	$("#span_res").append(data.replace(/\n/g, "\\n")+"\r\n");
				console.log(data);
				 
		});
		
		//console.log($("#textComando").val().replace(/\n/g, ","));
	}
	
</script>