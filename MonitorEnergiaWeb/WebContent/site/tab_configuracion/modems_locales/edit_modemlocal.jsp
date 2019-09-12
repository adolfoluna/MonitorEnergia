
<div>

	<div class="panel">
		<div class="panel-heading custom-header-panel">
			<h3 class="panel-title">Modem local</h3>
			<hr>
		</div>
	</div>
			
	<div class="form-group row">
		<label for="field_idmodem" class="col-2 col-form-label">Idmodem</label>
	  	<div class="col-5">
	    	<input type="text" id="field_idmodem" class="form-control" readonly="readonly" value="0">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_numero" class="col-2 col-form-label">N&uacute;mero</label>
	  	<div class="col-5">
	    	<input type="text" id="field_numero" class="form-control" value="0">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_puerto" class="col-2 col-form-label">Puerto</label>
	  	<div class="col-5">
	    	<input type="text" id="field_puerto" class="form-control" value="0">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_baudrate" class="col-2 col-form-label">Baud rate</label>
	  	<div class="col-5">
	    	<select id="field_baudrate" class="form-control">
				<option value="300">300</option>
				<option value="600">600</option>
				<option value="1200">1200</option>
				<option value="1800">1800</option>
				<option value="2400">2400</option>
				<option value="3600">3600</option>
				<option value="4800">4800</option>
				<option value="7200">7200</option>
				<option value="9600">9600</option>
				<option value="14400">14400</option>
				<option value="19200">19200</option>
				<option value="28800">28800</option>
				<option value="38400">38400</option>
				<option value="57600">57600</option>
				<option value="115200">115200</option>
				<option value="230400">230400</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_databits" class="col-2 col-form-label">Data bits</label>
	  	<div class="col-5">
	    	<select id="field_databits" class="form-control">
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
				<option value="8">8</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_parity" class="col-2 col-form-label">Paridad</label>
	  	<div class="col-5">
	    	<select id="field_parity" class="form-control">
				<option value="NONE">NONE</option>
				<option value="ODD">ODD</option>
				<option value="EVEN">EVEN</option>
				<option value="SPACE">SPACE</option>
				<option value="MARK">MARK</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_stopbits" class="col-2 col-form-label">Stop bits</label>
	  	<div class="col-5">
	    	<select id="field_stopbits" class="form-control">
				<option value="1">1</option>
				<option value="3">1.5</option>
				<option value="2">2</option>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_activo" class="col-2 col-form-label">Activo</label>
	  	<div class="col-5">
	    	<input type="checkbox" class="form-control" id="field_activo">
			<input type="hidden" id="field_version" value="0">
		</div>
	</div>
	
	<div class="form-group row">
		<label for="field_monitoreoActivo" class="col-2 col-form-label">Monitoreo Activo</label>
	  	<div class="col-5">
	    	<input type="checkbox" class="form-control" id="field_monitoreoActivo">
		</div>
	</div>
	
	<div class="form-group text-center">
		<button class="btn btn-orange-md roboto" onclick="guardarModemLocal()">Guardar</button>
		&nbsp;&nbsp;&nbsp;
		<button class="btn btn-red-md roboto" id="removeButton" onclick="eliminarModemLocal()">Eliminar</button>
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
			getModemLocal(oid);
		 
		if( oid <= 0 )
			$("#removeButton").hide();
	});
	
	var oid = <%= oid %>;

	function guardarModemLocal() {
		
		object = createObjectFromFields();
		
		if( oid > 0 )
			temp = "rest/modemlocal/editar";
		else
			temp = "rest/modemlocal/insertar";

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
					window.location.href = "index?op=1";
					return;
				}

				if( data.indexOf("success") == 0 ) {
					alert(data);
					window.location.href = "index?op=1";
					return;
				}

				alert("error al intentar actualizar, "+data);
				 
		});
		
	}

	function getModemLocal(oidaux) {

		temp = "rest/modemlocal/buscar/"+oidaux;
		
		$.ajax(
				{
					url: temp,
					method: "GET",
					dataType: "json",
				})	
			.done(function(data) {
				setFieldsFromObject(data);
			});

	}

	function eliminarModemLocal() {

		if( oid <= 0 )
			return;
		
		if(!confirm("Eliminar registro?"))
			return;
		
		temp = "rest/modemlocal/eliminar/"+$("#field_idmodem").val();
		
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
					 window.location.href = "index?op=1";
				else
					 alert("error al intentar eliminar registro, "+data);
			});	
	}

</script>
     
   