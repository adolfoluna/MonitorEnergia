
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<h1 class="h2">Lista de servicios corriendo</h1>
		<div class="btn-toolbar mb-2 mb-md-0">
		    <button class="btn btn-sm btn-outline-secondary" onclick="iniciarTodosServicios()">Iniciar servicios activos</button>
		  	&nbsp;&nbsp;
		  	<button class="btn btn-sm btn-outline-secondary" onclick="detenerTodosServicios()">Detener todos los servicios</button>
		  	&nbsp;&nbsp;
		  	<div class="btn-group mr-2">
		    	<input type="text" size="10" id="idmodem" placeholder="Idmodem" value="">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="iniciarModem()">iniciar</button>
		    	<button class="btn btn-sm btn-outline-secondary" onclick="detenerModem()">detener</button>
		  	</div>
		  	
		</div>
	</div>
	
	<div class="table-responsive">
		<table class="table table-striped table-sm" id="tablaDatos">
          	<thead>
				<tr>
					<th>Id</th>
					<th>N&uacute;mero</th>
					<th>Puerto</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
             <tbody>
               
             </tbody>
		</table>
	</div>

<script>

$(document).ready(
		function () {
			listarServicios();
			}
		);
	var tableViewData = {
		tableId: "tablaDatos",
		tableFields : ["idmodem","numero","puerto","edit"],
		prepareDataFunction : function(rowData){
			rowData.edit = "<button onclick=\"iniciarModem2("+rowData.idmodem+")\">iniciar</button>&nbsp;&nbsp;";
			rowData.edit+= "<button onclick=\"detenerModem2("+rowData.idmodem+")\">detener</button>&nbsp;&nbsp;";
			temp = "index?op=3&view=1&id="+rowData.idmodem+"&num="+rowData.numero+"&puerto="+rowData.puerto;
			rowData.edit+= "<button onclick=\"window.location.href='"+temp+"'\">comando</button>";
			
		},
		listUrl: "rest/modemmanager/list",
	}

	function listarServicios() {
		loadDataTable(tableViewData);
	}
	
	function iniciarTodosServicios() {

		if(!confirm("Iniciar todos los servicios activos no iniciados?"))
			return;

		//modemmanager/start
		temp = "rest/modemmanager/start";
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}

	function detenerTodosServicios() {

		if(!confirm("Iniciar todos los servicios activos no iniciados?"))
			return;

		//modemmanager/start
		temp = "rest/modemmanager/stop";
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}

	function detenerModem2(aux) {

		if(!confirm("detener modem "+aux+"?"))
			return;
		
		temp = "rest/modemmanager/stopmodem/"+aux;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}

	function iniciarModem2(aux) {

		if(!confirm("iniciar modem "+aux+"?"))
			return;
		
		temp = "rest/modemmanager/startmodem/"+aux;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}

	

	function iniciarModem() {
		
		if( $("#idmodem").val().length <= 0 ) {
			alert("id en cero");
			return;
		}

		if(!confirm("Iniciar modem "+$("#idmodem").val()))
			return;

		temp = "rest/modemmanager/startmodem/"+$("#idmodem").val();
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}

	function detenerModem() {
		
		if( $("#idmodem").val().length <= 0 ) {
			alert("id en cero");
			return;
		}

		if(!confirm("Detener modem "+$("#idmodem").val()))
			return;

		temp = "rest/modemmanager/stopmodem/"+$("#idmodem").val();
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {
				alert(data);
			});
	}
</script>