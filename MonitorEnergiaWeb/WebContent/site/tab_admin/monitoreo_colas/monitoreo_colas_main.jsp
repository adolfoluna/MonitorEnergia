<br/>

<div class="row row-offcanvas row-offcanvas-left">
    
	<div class="col-xs-12 col-sm-9">
		<br>
		<div class="row">
			<button onclick="traerValores()">Actualizar</button>
		</div>
		<br/>
		
		<div class="row">
			<table border="1" id="tablaDatos">
					<thead>
						<tr>
							<th>Nombre cola</th>
							<th>Mensajes encolados</th>
							<th>Agregados</th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>sme_monitoreo</td>
							<td id="mon_message_count">-</td>
							<td id="mon_message_added">-</td>
							<td><button onclick="borrar('sme_monitoreo')">borrar</button></td>
						</tr>	
						<tr>
							<td>sme_monitoreo_respuestas</td>
							<td id="monr_message_count">-</td>
							<td id="monr_message_added">-</td>
							<td><button onclick="borrar('sme_monitoreo_respuestas')">borrar</button></td>
						</tr>	
						<tr>
							<td>sme_websocket</td>
							<td id="monws_message_count">-</td>
							<td id="monws_message_added">-</td>
							<td><button onclick="borrar('sme_monitoreo_respuestas')">borrar</button></td>
						</tr>	
						<tr>
							<td>ExperyQueue</td>
							<td id="expq_message_count">-</td>
							<td id="expq_message_added">-</td>
							<td><button onclick="borrar('ExpiryQueue')">borrar</button></td>
						</tr>	
						<tr>
							<td>DLQ</td>
							<td id="dlq_message_count">-</td>
							<td id="dlq_message_added">-</td>
							<td><button onclick="borrar('DLQ')">borrar</button></td>
						</tr>	
					</tbody>
				</table>
			<br/>
		</div>
	</div>
	
</div>

<script>

$(document).ready(function() {
	traerValores();
	setInterval(traerValores,5000);
});

	function traerValores() {
		actualizarValoresCola("sme_monitoreo","mon_message_count","mon_message_added");
		actualizarValoresCola("sme_monitoreo_respuestas","monr_message_count","monr_message_added");
		actualizarValoresCola("sme_websocket","monws_message_count","monws_message_added");	
		actualizarValoresCola("ExpiryQueue","expq_message_count","expq_message_added");
		actualizarValoresCola("DLQ","dlq_message_count","dlq_message_added");
	}
	
	function actualizarValoresCola(nombre_cola,count_id,added_id) {
		
		temp = {
		        "operation":"read-resource",
		        "recursive":"true",
		        "include-runtime":"true",
		        "json.pretty":1,
		        "address": [
		 	       		{"subsystem":"messaging-activemq"},
		        		{"server":"default"},
		        		{"jms-queue":nombre_cola},
		        		]
		        
		        };
		    
			object = { message: JSON.stringify(temp)};
		
			$.ajax("rest/server/getvalue", {
			    data : JSON.stringify(object),
			    contentType : 'application/json',
			    type : 'POST',
			    dataType: "json",
			})
			 .done(function(data) {
				 
				 	if(data.outcome == "success") {
					 	$("#"+count_id).text(data.result["message-count"]);
					 	$("#"+added_id).text(data.result["messages-added"]);
					}
					 
			});
	}
	
	function borrar(nombreCola) {
		
		if(!confirm("borrar mensajes de cola "+nombreCola) )
			return;

		temp = {
		        "operation":"remove-messages",
		        "filter":null,
		        "address": [
		 	       		{"subsystem":"messaging-activemq"},
		        		{"server":"default"},
		        		{"jms-queue":nombreCola}
		        		]
		        
		        };
		    
			object = { message: JSON.stringify(temp)};
		
			temp = "rest/server/getvalue";
			
			$.ajax(temp, {
			    data : JSON.stringify(object),
			    contentType : 'application/json',
			    type : 'POST',
			    dataType: "json",
			})
			 .done(function(data) {
				 
				 if( data.outcome != "success")
					 alert(data["failure-description"]);
				else
					alert("Numero de mensajes eliminados:"+data.result);
				//console.log(data);		 
			});
		}

</script>