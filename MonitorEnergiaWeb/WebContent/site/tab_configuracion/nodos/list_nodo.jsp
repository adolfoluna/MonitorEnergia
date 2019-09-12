

	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<h1 class="h2">Lista nodos remotos</h1>
		<div class="btn-toolbar mb-2 mb-md-0">
			
			<div class="btn-group mr-2">
		    	<input type="text" size="20" id="buscarInput" placeholder="Buscar por idnodo..." value="">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="buscarporidnodo()">buscar</button>
		  	</div>
		  	
		  	<div class="btn-group mr-2">
		    	<input type="text" size="20" id="buscarInputNum" placeholder="Buscar por n&uacute;mero..." value="">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="buscarpornumero()">buscar</button>
		  	</div>
		  	
			<div class="btn-group mr-2">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="paginaAnterior()">&lt;</button>
		    	<input id="paginaInput" type="text" size="3" value="0">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="paginaSiguiente()">&gt;</button>
		  	</div>
		  	
		  	<button class="btn btn-sm btn-outline-secondary" onclick="window.location.href='index?op=2&view=1&id=0'">+Nuevo</button>
		  	
		</div>
	</div>
	
	<div class="table-responsive">
		<table class="table table-striped table-sm" id="tablaDatos">
          	<thead>
				<tr>
					<th>Idnodo</th>
					<th>Nombre</th>
					<th>C&oacute;digo</th>
					<th>N&uacute;mero</th>
					<th>CFE</th>
					<th>Fecha</th>
					<th>UPS</th>
					<th>Fecha</th>
					<th>Fecha Notificaci&oacute;n</th>
					<th>Fecha monitoreo</th>
					<th>Activo</th>
					<th>Version</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
             <tbody>
               
             </tbody>
		</table>
	</div>

<script>

	var tableViewData = {
		tableId: "tablaDatos",
		tableFields : ["idnodo","nombre","codigo","numero","cfePresente","cfeFecha","upsPresente","upsFecha","fechaNotificacion","fechaMonitoreo","activo","version","edit","sms","llamada"],
		prepareDataFunction : function(rowData){

			if( rowData.hasOwnProperty("cfeFecha")) {
				var aux = rowData.cfeFecha;
				aux = aux.replace("[UTC]","");
				rowData.cfeFecha = new Date(aux);
				rowData.cfeFecha = moment(rowData.cfeFecha).format('YYYY-MM-DD HH:mm:ss');
			}

			if( rowData.hasOwnProperty("upsFecha")) {
				var aux = rowData.upsFecha;
				aux = aux.replace("[UTC]","");
				rowData.upsFecha = new Date(aux);
				rowData.upsFecha = moment(rowData.upsFecha).format('YYYY-MM-DD HH:mm:ss');
			}

			if( rowData.hasOwnProperty("fechaNotificacion")) {
				var aux = rowData.fechaNotificacion;
				aux = aux.replace("[UTC]","");
				rowData.fechaNotificacion = new Date(aux);
				rowData.fechaNotificacion = moment(rowData.fechaNotificacion).format('YYYY-MM-DD HH:mm:ss');
			}

			if( rowData.hasOwnProperty("fechaMonitoreo")) {
				var aux = rowData.fechaMonitoreo;
				aux = aux.replace("[UTC]","");
				rowData.fechaMonitoreo = new Date(aux);
				rowData.fechaMonitoreo = moment(rowData.fechaMonitoreo).format('YYYY-MM-DD HH:mm:ss');
			}

			if(rowData.activo)
				rowData.activo = "<font color=\"green\"><b>true<b></font>";
			else
				rowData.activo = "<font color=\"indianred\">fasle</font>";
				
			rowData.edit = "&nbsp;<a href=\"index?op=2&view=1&id="+rowData.idnodo+"\">edit</a>"
			rowData.sms= "&nbsp;<a href=\"#\" onclick=\"sms("+rowData.idnodo+")\">sms</a>";
			rowData.llamada= "&nbsp;<a href=\"#\" onclick=\"llamada("+rowData.idnodo+")\">llamada</a>";
		},
		listUrl: "rest/nodo/listar/20/",
		pageInput: "paginaInput",
		searchInput: "buscarInput",
		searchUrl: "rest/nodo/buscar/"
	}
	

	$( document ).ready(function() {
    	loadDataTable(tableViewData);
	});

	function sms(idnodo) {
		
		temp = "rest/monitorearnodo_sms/"+idnodo;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "json",
				})	
			.done(function(data) {
				alert("idnodo:"+data.idnodo+" success:"+data.success);
			});

	}

	function llamada(idnodo) {
		
		temp = "rest/monitorearnodo/"+idnodo;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "json",
				})	
			.done(function(data) {
				alert("idnodo:"+data.idnodo+" success:"+data.success);
			});

	}
	
	function paginaAnterior() {
		i = parseInt($("#paginaInput").val());
		if( i <= 0 ) return;
		else i--;
		$("#paginaInput").val(i);
		loadDataTable(tableViewData);
	}
	
	function paginaSiguiente() {
		i = parseInt($("#paginaInput").val());
		i++;
		$("#paginaInput").val(i);
		loadDataTable(tableViewData);
	}

	function buscarporidnodo() {
		tableViewData.searchUrl = "rest/nodo/buscar/";
		tableViewData.searchInput = "buscarInput";
		searchDataTable(tableViewData);
	}

	function buscarpornumero() {
		tableViewData.searchUrl = "rest/nodo/buscar/numero/";
		tableViewData.searchInput = "buscarInputNum";
		searchDataTable(tableViewData);
	}
	
	
	
</script>