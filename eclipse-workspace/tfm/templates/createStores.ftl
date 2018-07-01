		//Create ${entity}
		var store${entity} = database.createObjectStore('${entity}', {keyPath: 'name'});

	<#list properties as property>
		store${entity}.createIndex('${property}', '${property}', {unique : false}); 
	</#list>
	
		store${entity}.transaction.oncomplete = function (event) {
  
        	var ${entity}Store = database.transaction('${entity}', 'readwrite').objectStore('${entity}');
        
      		var ${entity}Data = [<#list data as d> ${d}<#sep>,</#list>];
        
        	${entity}Data.forEach(function (${entity}) {
           		${entity}Store.add(${entity});
        	});
    	};
    	//End ${entity}
    
			