    
    function searchDataTable(tableviewdata) {
        
        temp =tableViewData.searchUrl + $("#"+tableviewdata.searchInput).val();	
        
        $("#"+tableviewdata.tableId+" tbody").empty();
        
        $.ajax(
            {
                url: temp,
                method: "GET",
                dataType: "json",
            })	
        .done(function(data) {

                if( data == null || data.length <= 0 ) {
                    $("#"+tableviewdata.tableId+" tbody").append("<tr><td colspan=\"8\"><b>No se encontraron resultados</b></td></tr>");
                    return;	
                }
                
                aux = null;
                
                if(tableviewdata.hasOwnProperty("prepareDataFunction"))
                    aux = tableviewdata.prepareDataFunction;
                
                if(aux != null )
                    aux(data);
                
                $("#"+tableviewdata.tableId+" tbody").append(createTableRow(tableviewdata.tableFields,data));
                
            });
    }

    function loadDataTable(tableviewdata) {
        
        if(!tableviewdata.hasOwnProperty("tableId")) {
            console.log("error, no se encontro atributo tableId");
            return;
        }
        
        temp = tableviewdata.listUrl;
        
        if(tableviewdata.hasOwnProperty("pageInput"))
            temp+=$("#"+tableviewdata.pageInput).val();
        
        $("#"+tableviewdata.tableId+" tbody").empty();
        
        $.ajax(
            {
                url: temp,
                method: "GET",
                dataType: "json",
            })	
        .done(function(data) {

                if( data == null || data.length <= 0 ) {
                    $("#tablaDatos").append("<tr><td colspan=\"8\"><b>No se encontraron resultados</b></td></tr>");
                    return;	
                }
               
                var aux = null;
                
                if(tableviewdata.hasOwnProperty("prepareDataFunction"))
                    aux = tableviewdata.prepareDataFunction;
                
                
                for( i = 0; i < data.length; i++ ) {
                    
                    if( aux != null)
                        aux(data[i]);
                    
                    $("#"+tableviewdata.tableId+" tbody").append(createTableRow(tableviewdata.tableFields,data[i]));
                }
                    
                
            });
        
    }

    function createTableRow(fields,dato) {
        
        var cadena = "<tr>";
        
        for(h =0; h < fields.length; h++) {
            
            col = fields[h];
            
            if( dato.hasOwnProperty(col) )
                cadena+="<td>"+dato[col]+"</td>";
            else
                cadena+="<td>null</td>";
        }
        
        cadena+="</tr>";
        
        return cadena;
    }

    function createObjectFromFields() {
        
        object = {};

		arr = $("[id^= \"field_\"]");

		for( i = 0 ; i < arr.length; i++ ) {

			element = $(arr[i]);
			
			id = element.attr("id").substring(6);

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "text") {
                if(element.val().length > 0 )
                    object[id] = element.val();    
                else
                    object[id] = null;
            }
				

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "hidden") {
                if(element.val().length > 0 )
                    object[id] = element.val();    
                else
                    object[id] = null;
            }

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "checkbox") 
				object[id] = element.is(":checked");

			if(element.prop("tagName").toLowerCase() == "select" ) {
                object[id] = element.children("option:selected").val();
                if(object[id] == "null")
                    object[id] = null;
            }
				
	
        }
        
        return object;
    }

    function setFieldsFromObject(ob) {
        
        for (var key in ob) {
			
			var element = $("#field_"+key);
			
			if(element.length <= 0 ) 
				continue;

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "text") 
                element.val(ob[key]); 

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "hidden")
				element.val(ob[key]);

			if(element.prop("tagName").toLowerCase() == "input" && element.attr("type") == "checkbox") 
				element.prop('checked',ob[key]);
			
			if(element.prop("tagName").toLowerCase() == "select" ) {
                if(ob[key] == null )
                    ob[key] = "null";
                element.val(ob[key].toString());
            }
				
		    //console.log(key+"->"+ob[key]);
		}
    }