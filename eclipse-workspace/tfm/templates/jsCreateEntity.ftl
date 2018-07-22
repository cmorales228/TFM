
		var newInstanceName = "${entityName}" + (new Date()).getTime(); //unique name
		var newData = {instanceName: newInstanceName};
	
		//Add properties depending on sessionStorage
		<#list secretProperties as prop, isSecret>
		if("${entityName}${prop?cap_first}" in sessionStorage){
			newData['${prop}'] = sessionStorage.getItem("${entityName}${prop?cap_first}");
			sessionStorage.removeItem("${entityName}${prop?cap_first}");
		}else{
			<#if isSecret>
			newData['${prop}'] = encryptPass(document.querySelector("#input${prop}").value);
			<#else>
			newData['${prop}'] = document.querySelector("#input${prop}").value;
			</#if>
		}
		</#list>
	
		insertItemToIndexedDB("${entityName}", newData, true); //Insert item to indexedDB
		deleteSessionStorage();
		//Link to view the just created object
    	window.location.href = window.location.protocol + '//' + window.location.host + "/${entityName?lower_case}.html?val=" + newInstanceName;
    		