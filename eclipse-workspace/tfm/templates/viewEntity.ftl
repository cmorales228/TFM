<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="utf-8">
	<script type="text/javascript">

		var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

        function getObjectFromDB() {
                        
            var req = indexedDB.open("${appName}", 1);

			req.onupgradeneeded = function(evt){
				console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
			};
			
			req.onerror = function(evt){
				console.error("openDB:", evt.target.errorCode);
			};

			req.onsuccess=function(evt){
				
				//output variables
            	var outerTable = "";
            	
            	//Get argument from URL
            	var argURL = location.search.substr(1);
            	var instanceName = argURL.split("=")[1];
            	
            	//Get data from DB	
            	var active = req.result;
            	var data = active.transaction(['${entityName}'], 'readonly');
            	var objectStore = data.objectStore("${entityName}");
            	
            	var requestObject = objectStore.get(instanceName);
            	
            	requestObject.onerror = function(event) {
            		console.log("error while getting " + instanceName + " from ${entityName}");
  					// Handle errors!
				};
				requestObject.onsuccess = function(event) {
  					// Do something with the request.result!
  					var result${entityName} = requestObject.result;
  					document.querySelector("#${entityName}").innerHTML = result${entityName}.name;
  					
  					<#list linkEntitiesOrNull as linkProp, linkEntity>
  					<#if linkEntity??>
  					// ${entityName} has a property of type ${linkEntity}, so request for it
  					var linkData${linkEntity} = active.transaction(['${linkEntity}'], 'readonly');
            		var link${linkEntity}Store = linkData${linkEntity}.objectStore("${linkEntity}");
            		var requestLink${linkEntity} = link${linkEntity}Store.getAll();
           			requestLink${linkEntity}.onerror = function(event) {
            			console.log("error while getting " + requestValue + " from ${linkEntity}");
  						// Handle errors!
					};
					requestLink${linkEntity}.onsuccess = function(event) {

					</#if></#list>
					<#list linkEntitiesOrNull as linkProp, linkEnt>
					//Add entity ${entityName}, property ${linkProp?cap_first}
					outerTable += "\n\t<tr>\n\t\t<td>${linkProp?cap_first}:</td>\n\t\t<td><ul>";
					if (result${entityName}.${linkProp}!=null){
						result${entityName}.${linkProp}.split(", ").forEach(function (requestValue){
						if (result${entityName}.${linkProp}.includes(", ")){outerTable +="<li>";}
						<#if linkEnt??>
							requestLink${linkEnt}.result.forEach(function (instance){
								if (instance.instanceName == requestValue){
									<#list crudEntities as crudEntity, isCrud><#if crudEntity == linkEnt> 
									<#if isCrud>outerTable += "<a href='${linkEnt?lower_case}.html?val=" + requestValue + "'> " + instance.name + "</a>\n";
									<#else>outerTable += instance.name;</#if></#if></#list>}
							});
						<#else>
							outerTable += requestValue;
						</#if>
						if (result${entityName}.${linkProp}.includes(", ")){outerTable +="</li>";}
         		    	});		
					}else{
						<#if linkEnt??><#list inverseNonEditable as invEnt, invProp><#if invEnt == linkEnt>
						requestLink${invEnt}.result.forEach(function(result${invEnt}){
							if (result${invEnt}.${invProp}.split(", ").includes(result${entityName}.instanceName)){
								outerTable +="<li>";
								<#list crudEntities as crudEntity, isCrud><#if crudEntity == linkEnt> 
								<#if isCrud>outerTable += "<a href='${linkEnt?lower_case}.html?val=" + result${linkEnt}.instanceName + "'> " + result${linkEnt}.name + "</a>\n";
								<#else>outerTable += instance.name;</#if></#if></#list>
								outerTable +="</li>";
								}
						});</#if></#list>						
						<#else>
						outerTable += "0";
						</#if>
					}
					outerTable += "</ul></td>\n\t</tr>";
					</#list>
						document.querySelector("#view${entityName}").innerHTML = outerTable;
					<#list linkEntitiesOrNull as linkProp, linkEntity><#if linkEntity??>
					};
					</#if></#list>
					
				};          	
			};
		};
	</script>
</head>
<body onload="getObjectFromDB();">
	<h1 class="header section1" id=${entityName}></h1>
	<fieldset>
		<legend>Details</legend>
		<table id="view${entityName}">
		</table>
	</fieldset>
</body>
</html>