				
			//Create ${entity}
			var store${entity} = db.createObjectStore('${entity}', {keyPath: 'instanceName'});
				
	<#list secretProperties as property, isSecret>
			store${entity}.createIndex('${property}', '${property}', {unique : false}); 
	</#list>
		
			var ${entity} = transaction.objectStore('${entity}');
			var ${entity}Data = [<#list data as d> ${d}<#sep>,</#list>];
        
    		${entity}Data.forEach(function (${entity}) {
         		store${entity}.add(${entity});
        	});
    		//End ${entity}
    
			