
<div>

	<div class="panel">
		<div class="panel-heading custom-header-panel">
			<h3 class="panel-title">Nodo</h3>
			<hr>
		</div>
	</div>
			
	<div class="form-group row">
		<label for="field_idnodo" class="col-2 col-form-label">Id Nodo</label>
	  	<div class="col-5">
	    	<input type="text" id="field_idnodo" class="form-control" readonly="readonly" value="0">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_nombre" class="col-2 col-form-label">Nombre</label>
	  	<div class="col-5">
	    	<input type="text" id="field_nombre" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_domicilio" class="col-2 col-form-label">Domicilio</label>
	  	<div class="col-5">
	    	<input type="text" id="field_domicilio" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_codigo" class="col-2 col-form-label">C&oacute;digo</label>
	  	<div class="col-5">
	    	<input type="text" id="field_codigo" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_numero" class="col-2 col-form-label">N&uacute;mero</label>
	  	<div class="col-5">
	    	<input type="text" id="field_numero" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_cfePresente" class="col-2 col-form-label">CFE presente</label>
	  	<div class="col-5">
	    	<select id="field_cfePresente" class="form-control">
				<option value="null">Nulo</option>
				<option value="true">Si</option>
				<option value="false">No</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_cfeFecha" class="col-2 col-form-label">CFE Fecha</label>
	  	<div class="col-5">
	    	<input type="text" id="field_cfeFecha" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_upsPresente" class="col-2 col-form-label">UPS presente</label>
	  	<div class="col-5">
	    	<select id="field_upsPresente" class="form-control">
				<option value="null">Nulo</option>
				<option value="true">Si</option>
				<option value="false">No</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_upsFecha" class="col-2 col-form-label">UPS Fecha</label>
	  	<div class="col-5">
	    	<input type="text" id="field_upsFecha" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_fechaNotificacion" class="col-2 col-form-label">Fecha Notificacion</label>
	  	<div class="col-5">
	    	<input type="text" id="field_fechaNotificacion" class="form-control">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_fechaMonitoreo" class="col-2 col-form-label">Fecha monitoreo</label>
	  	<div class="col-5">
	    	<input type="text" id="field_fechaMonitoreo" class="form-control">
	    	<div>Formato YYYY-MM-DD HH:mm:ss</div>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_activo" class="col-2 col-form-label">Activo</label>
	  	<div class="col-5">
	    	<input type="checkbox" class="form-control" id="field_activo">
			<input type="hidden" id="field_version" value="0">
		</div>
	</div>
	
	<div class="form-group text-center">
		<button class="btn btn-orange-md roboto" onclick="guardarNodo()">Guardar</button>
		&nbsp;&nbsp;&nbsp;
		<button class="btn btn-red-md roboto" id="removeButton" onclick="eliminarNodo()">Eliminar</button>
	</div>

</div>

<%
	int oid = 0;
	if( request.getParameter("id") != null )
		oid = Integer.parseInt(request.getParameter("id").toString());
%>

<script>


	$(document).ready(function() {

		if( oid > 0 )
			getNodo(oid);
		 
		if( oid <= 0 )
			$("#removeButton").hide();
	});
	
	var oid = <%= oid %>;

	function guardarNodo() {
		
		object = createObjectFromFields();

		if( object.fechaNotificacion != null )
			object.fechaNotificacion = moment(object.fechaNotificacion,'YYYY-MM-DD HH:mm:ss').toDate();

		if( object.fechaMonitoreo != null )
			object.fechaMonitoreo = moment(object.fechaMonitoreo,'YYYY-MM-DD HH:mm:ss').toDate();

		if( object.fechaStatus != null )
			object.fechaStatus = moment(object.fechaStatus,'YYYY-MM-DD HH:mm:ss').toDate();

		if( object.cfeFecha != null )
			object.cfeFecha = moment(object.cfeFecha,'YYYY-MM-DD HH:mm:ss').toDate();

		if( object.upsFecha != null )
			object.upsFecha = moment(object.upsFecha,'YYYY-MM-DD HH:mm:ss').toDate();

		if( oid > 0 )
			temp = "rest/nodo/editar";
		else
			temp = "rest/nodo/insertar";

		console.log(object)

		$.ajax(temp, {
		    data : JSON.stringify(object),
		    contentType : 'application/json',
		    type : 'POST',
		    dataType: "text",
		})
		 .done(function(data) {
			 
				if( data == null || data == undefined ) {
					alert("error desconocido");
					return;
				}

				if( data == "success" ) {
					window.location.href = "index?op=2";
					return;
				}

				if( data.indexOf("success") == 0 ) {
					alert(data);
					window.location.href = "index?op=2";
					return;
				}

				alert("error al intentar actualizar, "+data);
				 
		});
		
	}

	function getNodo(oidaux) {

		temp = "rest/nodo/buscar/"+oidaux;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "json",
				})	
			.done(function(data) {

				if( data.hasOwnProperty("fechaNotificacion")) {
					var aux = data.fechaNotificacion;
					aux = aux.replace("[UTC]","");
					data.fechaNotificacion = new Date(aux);
					data.fechaNotificacion = moment(data.fechaNotificacion).format('YYYY-MM-DD HH:mm:ss');
				}

				if( data.hasOwnProperty("fechaMonitoreo")) {
					var aux = data.fechaMonitoreo;
					aux = aux.replace("[UTC]","");
					data.fechaMonitoreo = new Date(aux);
					data.fechaMonitoreo = moment(data.fechaMonitoreo).format('YYYY-MM-DD HH:mm:ss');
				}

				if( data.hasOwnProperty("cfeFecha")) {
					var aux = data.cfeFecha;
					aux = aux.replace("[UTC]","");
					data.cfeFecha = new Date(aux);
					data.cfeFecha = moment(data.cfeFecha).format('YYYY-MM-DD HH:mm:ss');
				}

				if( data.hasOwnProperty("upsFecha")) {
					var aux = data.upsFecha;
					aux = aux.replace("[UTC]","");
					data.upsFecha = new Date(aux);
					data.upsFecha = moment(data.upsFecha).format('YYYY-MM-DD HH:mm:ss');
				}
				
				setFieldsFromObject(data);
			});

	}

	function eliminarNodo() {

		if( oid <= 0 )
			return;
		
		if(!confirm("Eliminar registro?"))
			return;
		
		temp = "rest/nodo/eliminar/"+$("#field_idnodo").val();
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "text",
				})	
			.done(function(data) {

				if( data == null || data == undefined ) {
					alert("error desconocido");
					return;
				}
				
				if( data == "success")
					 window.location.href = "index?op=2";
				else
					 alert("error al intentar eliminar registro, "+data);
			});	
	}

</script>
     
   