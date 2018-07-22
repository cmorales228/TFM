            
        //Get the instanceName to be updated
		var updateInstanceName = sessionStorage.getItem("currentInstance${entityName}");
           	
    	//Get data from DB (it was saved from last request)	
		var currentObject = requestObject;
		
		//Add all properties to object, depending on sessionStorage values.
	   <#list secretProperties as prop, isSecret>
		if("${entityName}${prop?cap_first}" in sessionStorage){
			currentObject.${prop} = sessionStorage.getItem("${entityName}${prop?cap_first}");
		}else{
			<#if isSecret>
			currentObject.${prop} = encryptPass(document.querySelector("#input${prop}").value)
			<#else>
			currentObject.${prop} = document.querySelector("#input${prop}").value;
			</#if>
		}
		</#list>
	
		//Update object into indexedDB
		insertItemToIndexedDB("${entityName}", currentObject, false);
		deleteSessionStorage();
    	window.location.href = window.location.protocol + '//' + window.location.host + "/${entityName?lower_case}.html?val=" + currentObject.instanceName;
    		