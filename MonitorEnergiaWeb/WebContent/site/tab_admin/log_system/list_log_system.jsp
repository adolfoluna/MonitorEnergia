<br/>
<div class="row row-offcanvas row-offcanvas-left">
    
	<div class="col-xs-12 col-sm-9">
		Lista de archivos de log del sistema
		<br>
		<div class="row">
			<div>
				<ul id="lista">
				</ul>
			</div>	
		</div>
	</div>
</div>


<script>

	$(document).ready(
		function() {
			listarArchivos();	
		}
	);

	function listarArchivos() {
		
		temp = {
        		"operation":"list-log-files",
		        "address": [{"subsystem":"logging"}]
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
			 
			 if( data.outcome != "success") {
				 alert(data["failure-description"]);
				 return;
			 }

			 for( i =0; i < data.result.length; i++) {
				 url = "index?op=5&view=1&id="+data.result[i]["file-name"];
				$("#lista").append("<li><a href=\""+url+"\">"+data.result[i]["file-name"]
					+"</a>&nbsp;&nbsp;"
					+formatBytes(data.result[i]["file-size"],2)+"&nbsp;&nbsp;"
					+"("+data.result[i]["last-modified-date"]+")"
					+"</li>");
			}
			//console.log(data);		 
		});
	}

	function formatBytes(bytes,decimals) {
		   if(bytes == 0) return '0 Bytes';
		   var k = 1024,
		       dm = decimals <= 0 ? 0 : decimals || 2,
		       sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
		       i = Math.floor(Math.log(bytes) / Math.log(k));
		   return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
	}

	function test() {
		
		 temp = {
        		"operation":"read-log-file",
		        "encoding":"UTF-8",
		        "lines":5,
		        "skip": lastLine,
		        "tail":false,
		        "address": [
		 	       		{"subsystem":"logging"},
		        		{"log-file":"server.log"},
		        		]
		        
			};

		lastLine+=5;
			    
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
			
			console.log(data);		 
		});
	}
	</script>