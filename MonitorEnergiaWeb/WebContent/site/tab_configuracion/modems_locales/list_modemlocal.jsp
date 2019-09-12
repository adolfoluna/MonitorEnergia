
	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<h1 class="h2">Lista modems locales</h1>
		<div class="btn-toolbar mb-2 mb-md-0">
			
			<div class="btn-group mr-2">
		    	<input type="text" size="20" id="buscarInput" placeholder="Buscar por idmodem..." value="">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="searchDataTable(tableViewData)">buscar</button>
		  	</div>
		  	
			<div class="btn-group mr-2">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="paginaAnterior()">&lt;</button>
		    	<input id="paginaInput" type="text" size="3" value="0">
		    	<button class="btn btn-sm btn-outline-secondary" onclick="paginaSiguiente()">&gt;</button>
		  	</div>
		  	
		  	<button class="btn btn-sm btn-outline-secondary" onclick="window.location.href='index?op=1&view=1&id=0'">+Nuevo</button>
		  	
		</div>
	</div>
	
	<div class="table-responsive">
		<table class="table table-striped table-sm" id="tablaDatos">
          	<thead>
				<tr>
					<th>Idmodem</th>
					<th>Numero</th>
					<th>Puerto</th>
					<th>Activo</th>
					<th>Monitoreo Activo</th>
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
		tableFields : ["idmodem","numero","puerto","activo","monitoreoActivo","edit"],
		prepareDataFunction : function(rowData){
			
			if(rowData.hasOwnProperty("stopbits")) {
				switch(rowData.stopbits){
					default:
					case 1: rowData.stopbits = 1; break;
					case 2: rowData.stopbits = 2; break;
					case 3: rowData.stopbits = 1.5; break;
				}	
			}

			if(rowData.activo)
				rowData.activo = "<font color=\"green\"><b>true<b></font>";
			else
				rowData.activo = "<font color=\"indianred\">fasle</font>";

			if(rowData.monitoreoActivo)
				rowData.monitoreoActivo = "<font color=\"green\"><b>true<b></font>";
			else
				rowData.monitoreoActivo = "<font color=\"indianred\">fasle</font>";
			
			rowData.edit = "<a href=\"index?op=1&view=1&id="+rowData.idmodem+"\">edit</a>"
		},
		listUrl: "rest/modemlocal/listar/20/",
		pageInput: "paginaInput",
		searchInput: "buscarInput",
		searchUrl: "rest/modemlocal/buscar/"
	}
	
</script>


<script>

	$( document ).ready(function() {
    	loadDataTable(tableViewData);
	});
	
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
	
	
	
</script>