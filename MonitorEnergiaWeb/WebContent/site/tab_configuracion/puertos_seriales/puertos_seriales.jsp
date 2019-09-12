

	<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
		<h1 class="h2">Lista de puertos seriales en maquina</h1>
	</div>
	
	<ul id="lista"></ul>

<script>

	$(document).ready( function() { listar(); });

	function listar() {

		$("#lista").html("<li>consultando........</li>");
		
		$.ajax(
			{url: "rest/puertoserial/listar",
			method: "GET",
			dataType: "json",
			
			})	
		.done(function(data) {

				$("#lista").html("");
				
				if( data == null || data.length <= 0 ) {
					$("#lista").append("<li><b>No se encontraron resultados</b></li>");
					return;	
				}
				
				for( i = 0; i < data.length; i++ ) {
					$("#lista").append("<li>"+data[i]+"</li>");
				}
				//$("#divres").html(msg);
			});
	}
</script>