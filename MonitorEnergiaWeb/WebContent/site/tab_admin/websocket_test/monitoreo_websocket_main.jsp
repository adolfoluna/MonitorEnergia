<br/>

<div class="row row-offcanvas row-offcanvas-left">
    
	<div class="col-xs-12 col-sm-9">
		<br>
		<div class="row">
			Host:<input type="text" size="50" id="direccion" value="ws://localhost:8080/MonitorEnergiaWeb/monitorwebsocket"><button onclick="conectar()">conectar</button>
			&nbsp;&nbsp;<button onclick="$('#texto').empty()">borrar</button>
		</div>
		<br>
		<div class="row">
			<textarea rows="50" cols="100" style="width:100%; height:100%;"  id="texto"></textarea>
		</div>
		<br/>
		
		<div class="row">
			
			<br/>
		</div>
	</div>
	
</div>

<script>

$(document).ready(function() {

});

	function conectar() {
		agregar_linea("intentando conectarse a "+$("#direccion").val()+".........");
		socket = new WebSocket($("#direccion").val());
		socket.onopen = socketOnOpen;
		socket.onmessage = socketOnMessage;
		socket.onclose = socketOnClose;
		socket.onerror = socketOnError;
	}

	function socketOnOpen(event) {
		agregar_linea("socket exitosamenta conectado......")
	}

	function socketOnMessage(event) {
		agregar_linea(event.data);
	}

	function socketOnClose(event) {
		agregar_linea("socket cerrado........");
	}

	function socketOnError(event) {
		//console.log("error de socket......"+event.error)
		agregar_linea("error de socket "+event.error);
	}

	function agregar_linea(msg) {
		$("#texto").append(msg+"\r\n");
	}
	
</script>