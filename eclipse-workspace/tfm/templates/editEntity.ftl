<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="utf-8">
	<script type="text/javascript">

	var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

    function getData() {
                       
    	var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
		};
			
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};

		req.onsuccess=function(evt){
			
			//Get argument from URL
            var argURL = location.search.substr(1);
            var instanceName = argURL.split("=")[1];
            
            if(!("currentInstance${entityName}" in sessionStorage)){
            	sessionStorage.setItem("currentInstance${entityName}", instanceName);
            }
            else{
            	instanceName = sessionStorage.getItem("currentInstance${entityName}");
            }
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
				
				document.querySelector("#edit${entityName}").innerHTML = "Edit " + result${entityName}.name;
			
				<#list properties as prop, isSet>
				<#if isSet>
				if (!("${entityName}${prop?cap_first}" in sessionStorage) && requestObject.result.${prop} != null){
					sessionStorage.setItem("${entityName}${prop?cap_first}", requestObject.result.${prop});
				}<#else>
				if(!("selected${entityName}${prop?cap_first}" in sessionStorage)){
					sessionStorage.setItem("selected${entityName}${prop?cap_first}", requestObject.result.${prop});
				}
				</#if>
				</#list>
			
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
					
					<#list linkEntitiesOrNull as prop, linkEnt>
					<#if linkEnt??> 
					selector ="";
					<#list properties as propName, isSet>
					<#if isSet><#if propName != prop>
					selector += "<option value></option>";					
					</#if></#if></#list>
					var alreadySelected = "";
					requestLink${linkEnt}.result.forEach(function(result${linkEnt}){
						sessionStorage.setItem(result${linkEnt}.instanceName, result${linkEnt}.name);
						if ("selected${entityName}${prop?cap_first}" in sessionStorage){
							selector += "<option value=" + result${linkEnt}.instanceName;
							if(result${linkEnt}.instanceName == sessionStorage.getItem("selected${entityName}${prop?cap_first}")){selector +=" selected";}
							selector += ">" + result${linkEnt}.name + "</option>";
						} else if (!("${entityName}${prop?cap_first}" in sessionStorage) || sessionStorage.getItem("${entityName}${prop?cap_first}").split(", ").indexOf(result${linkEnt}.instanceName)<0){
							selector += "<option value=" + result${linkEnt}.instanceName;
							if(result${entityName}.${prop} == result${linkEnt}.instanceName){selector +=" selected";}
							selector += ">" + result${linkEnt}.name + "</option>";
						}
						<#list properties as propName, isSet>
						<#if isSet><#if propName == prop>
						else{
							alreadySelected += "<fieldset id=${prop}><label>" + result${linkEnt}.name + "</label><button onclick='remove${entityName}Property(\""+result${linkEnt}.instanceName+"\")'>x</button></fieldset>";	
						}
						</#if></#if></#list>
					});
					<#if inverseNonEditable?has_content><#list inverseNonEditable as invEnt, invProp><#if invEnt == linkEnt> <#else>
					document.querySelector("#input${prop}").innerHTML = selector;
					if(alreadySelected.length>0){
						document.querySelector("#add${prop}").innerHTML = alreadySelected;
					}
					</#if></#list><#else>
					document.querySelector("#input${prop}").innerHTML = selector;
					if(alreadySelected.length>0){
						document.querySelector("#add${prop}").innerHTML = alreadySelected;
					}
					</#if>
					<#else>
					document.querySelector("#input${prop}").value = sessionStorage.getItem("selected${entityName}${prop?cap_first}");
					</#if>
					</#list>
					
				<#list linkEntitiesOrNull as prop, linkEnt>
				<#if linkEnt??>};</#if>
				</#list>
			};
		};           	
	};
	
	function add${entityName}Property(){
		
		<#list properties as prop, isSet>
		<#if isSet>		
		var sel = document.querySelector("#input${prop}");
		if (sel.options[sel.selectedIndex] != undefined){
			var item= sel.options[sel.selectedIndex].text;
		
			var newItem;
			for (key in sessionStorage){
				if (sessionStorage.getItem(key) == item){
					newItem = key;	
				}
			}
		
			if(newItem == undefined){
				newItem = item;
			}	
		
			var oldItems = sessionStorage.getItem("${entityName}${prop?cap_first}");
			var newItems;
			if (oldItems == null || oldItems.length <= 0){
				newItems = newItem;
			}
			else if(newItem.length>0){
				newItems = oldItems + ", " + newItem;
			}
			else{
				newItems = oldItems;
			}
			sessionStorage.setItem("${entityName}${prop?cap_first}", newItems);
		}
		<#else>
		sessionStorage.setItem("selected${entityName}${prop?cap_first}", document.querySelector("#input${prop}").value);
		</#if>
		</#list>
		window.history.back();
		//location.href = location.protocol + '//' + location.host + "/${entityName?lower_case}.html?val=" + sessionStorage.getItem("currentInstance${entityName}");
	}
	
	function remove${entityName}Property(item){
		
		<#list properties as prop, isSet>
		<#if isSet>		
		var oldItems = sessionStorage.getItem("${entityName}${prop?cap_first}").split(", ");
		var itemPosition = oldItems.indexOf(item);
		oldItems.splice(itemPosition, 1);
				
		sessionStorage.setItem("${entityName}${prop?cap_first}", oldItems.join(", "));
		window.history.back();
		<#else>
		sessionStorage.setItem("selected${entityName}${prop?cap_first}", document.querySelector("#input${prop}").value);	
		</#if>
		</#list>
	}

	function getDataFromDB(db, ent){
		
		var data = db.transaction([ent], 'readwrite');
		var objectStore = data.objectStore(ent);
		var requestObject = objectStore.getAll();
			requestObject.onerror = function (evt){
				console.log("Error while getting " + ent + " from database");
			};
			requestObject.onsucess = function (evt){	
				return requestObject.result;
			};
	};
	
	function update${entityName}() {
		var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
		};
			
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};

		req.onsuccess=function(evt){
		//Get argument from URL
            
            var updateInstanceName = sessionStorage.getItem("currentInstance${entityName}");
           	var active = req.result;
           	
            //Get data from DB	
           	var data = active.transaction(['${entityName}'], 'readwrite');
           	var objectStore = data.objectStore("${entityName}");
           	
           	var requestObject = objectStore.get(updateInstanceName);
           	
           	requestObject.onerror = function(event) {
           		console.log("error while getting " + instanceName + " from ${entityName}");
 					// Handle errors!
			};
			requestObject.onsuccess = function(event) {     
				
				var resultObject = requestObject.result;
				
				<#list secretProperties as prop, isSecret>
				if("${entityName}${prop?cap_first}" in sessionStorage){
					resultObject.${prop} = sessionStorage.getItem("${entityName}${prop?cap_first}");
				}else{
					<#if isSecret>
					var s = document.querySelector("#input${prop}").value;
					var h = 0, l = s.length, i = 0;
  					if ( l > 0 )
    					while (i < l)
      						h = (h << 5) - h + s.charCodeAt(i++) | 0;
					resultObject.${prop} = h.toString();
					<#else>
					resultObject.${prop} = document.querySelector("#input${prop}").value;
					</#if>
				}
				</#list>
				
				var requestUpdate = objectStore.put(resultObject);
   					requestUpdate.onerror = function(event) {
     				// Do something with the error
   				};
   				requestUpdate.onsuccess = function(event) {
   					sessionStorage.clear();
   				
     				location.href = location.protocol + '//' + location.host + "/${entityName?lower_case}.html?val=" + updateInstanceName;
            
     				// Success - the data is updated!
     			};
   			};	
        };
	};
	</script>
</head>
<body onload="getData();">
	<h1 class="header section1" id="edit${entityName}"></h1>
	<form>
	<fieldset>
		<legend>Details</legend>
		<table id="editTable${entityName}">
${tableCreate}
		</table>
	</fieldset>
	<button name="update" onclick="update${entityName}();">Update</button>
	</form>
</body>
</html>