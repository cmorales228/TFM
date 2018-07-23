		
		//Delete selected object from idexedDB
		deleteItemFromIndexedDB("${entityName}", sessionStorage.getItem("deleteInstance"));
		sessionStorage.removeItem("deleteInstance");
		
		//Delete all ocurrences of the object in all the stores linked to it.
		<#list propToDelete as delEntity, delProp>
    	var store${delEntity} = getObjectStore("${delEntity}", 'readwrite');
    	getAllFromIndexedDB("${delEntity}");
   
   		requestObjects.forEach(function(reqObject){
   			if(reqObject.${delProp} && reqObject.${delProp}.split(", ").includes(instanceToDelete)){
   				var items = reqObject.${delProp}.split(", ");
   				var itemPosition = items.indexOf(instanceToDelete);
   				items.splice(itemPosition, 1);
   				reqObject.${delProp} = items.join(", ");
   			
   				insertItemToIndexedDB("${delEntity}", reqObject, false);
   			}
   		});        	            		
   		</#list>
  
    	deleteSessionStorage();
  		window.location.href = window.location.protocol + '//' + window.location.host + "/manage${entityName?cap_first}.html";
    	