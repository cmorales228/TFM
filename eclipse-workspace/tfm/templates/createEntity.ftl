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
			
			var active = req.result;
			
			<#if inverseEditable?has_content>
			
			var data${entityName} = active.transaction(['${entityName}'], 'readonly');
			var ${entityName}Store = data${entityName}.objectStore("${entityName}");
			var request${entityName} = ${entityName}Store.getAll();
			request${entityName}.onerror = function (event) {
				console.log("Error while gettting data form ${entityName}");
			}
			request${entityName}.onsuccess = function(event){
				var elements = [];
				<#list inverseEditable as invEnt, invProp>
				if (!("inverse${invEnt}${invProp?cap_first}" in sessionStorage)){
					request${entityName}.result.forEach(function(object${entityName}){
						<#list linkEntitiesOrNull as linkProp, linkEnt><#if linkEnt??><#if invEnt == linkEnt>
						if(object${entityName}.${linkProp}.length>0){elements.push(object${entityName}.${linkProp})};
						</#if></#if></#list>
					});
					if (elements.length>0){
						sessionStorage.setItem("inverse${invEnt}${invProp?cap_first}", elements.join(", "));
					}
				}</#list>
			}
			</#if>
			
			<#list linkEntitiesOrNull as linkProp, linkEntity>
			<#if linkEntity??>
  			// ${entityName} has a property of type ${linkEntity}, so request for it
  			var linkData${linkEntity} = active.transaction(['${linkEntity}'], 'readonly');
           	var link${linkEntity}Store = linkData${linkEntity}.objectStore("${linkEntity}");
	          	var requestLink${linkEntity} = link${linkEntity}Store.getAll();
    	   	requestLink${linkEntity}.onerror = function(event) {
        	  		console.log("error while getting data from ${linkEntity}");
  				// Handle errors!
			};
			requestLink${linkEntity}.onsuccess = function(event) {
				selectInput = "";
				selectInput += "<option selected value></option>";
				requestLink${linkEntity}.result.forEach(function (valueLink${linkEntity}) {	
					sessionStorage.setItem( valueLink${linkEntity}.instanceName, valueLink${linkEntity}.name);
					if(!("${entityName}${linkProp?cap_first}" in sessionStorage) || (sessionStorage.getItem("${entityName}${linkProp?cap_first}")).split(", ").indexOf(valueLink${linkEntity}.instanceName)<0){
					<#list inverseEditable as invEnt, invProp><#if invEnt == linkEntity>
						if(("inverse${invEnt}${invProp?cap_first}" in sessionStorage) && sessionStorage.getItem("inverse${invEnt}${invProp?cap_first}").split(", ").indexOf(valueLink${linkEntity}.instanceName)<0){	
					</#if></#list>
						selectInput += "<option value=" + valueLink${linkEntity}.instanceName;
						if (("selected${entityName}${linkProp?cap_first}" in sessionStorage) && sessionStorage.getItem("selected${entityName}${linkProp?cap_first}") == valueLink${linkEntity}.instanceName)
						{selectInput += " selected";}
						selectInput +=">" + valueLink${linkEntity}.name + "</option>\n";
					<#list inverseEditable as invEnt, invProp><#if invEnt == linkEntity>}</#if></#list>	
					}
				   
				});
				<#if inverseNonEditable?has_content><#list inverseNonEditable as invEnt, invProp><#if invEnt == linkEntity> <#else>
				document.querySelector("#input${linkProp}").innerHTML = selectInput;</#if></#list>
				<#else>
				document.querySelector("#input${linkProp}").innerHTML = selectInput;
				</#if>
			};
			<#else>
			if ("selected${entityName}${linkProp?cap_first}" in sessionStorage){
				document.querySelector("#input${linkProp}").value = sessionStorage.getItem("selected${entityName}${linkProp?cap_first}");
			} 
			</#if>
			
			</#list>
			<#list properties as prop, isSet>
			<#if isSet>
			if("${entityName}${prop?cap_first}" in sessionStorage){
			var object${prop?cap_first}Added = sessionStorage.getItem("${entityName}${prop?cap_first}");
			var output = "<p>";
			object${prop?cap_first}Added.split(", ").forEach(function(object){
				if(sessionStorage.getItem(object)){
					output += "<fieldset id=${prop}><label>" + sessionStorage.getItem(object) + "</label><button onclick='remove${entityName}${prop?cap_first}(\""+object+"\")'>x</button></fieldset>";
				}else{
					output += "<fieldset id=${prop}><label>" + object + "</label><button onclick='remove${entityName}${prop?cap_first}(\""+object+"\")'>x</button></fieldset>";					
				}
			});
			output +="</p>";
				document.querySelector("#add${prop}").innerHTML = output;
			}
			</#if>
			</#list>
		};           	
	};
	
	function add${entityName}Property(){
		<#list properties as prop, isSet>
		<#if isSet>
		var sel = document.querySelector("#input${prop}");
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
		if (oldItems == null){
			newItems = newItem
		}
		else{
			newItems = oldItems + ", " + newItem;
		}
		sessionStorage.setItem("${entityName}${prop?cap_first}", newItems);
		<#else>
		sessionStorage.setItem("selected${entityName}${prop?cap_first}", document.querySelector("#input${prop}").value);
		</#if>
		</#list>
	}
	
	function remove${entityName}Property(item){
		
		<#list properties as prop, isSet>
		<#if isSet>
		var oldItems = sessionStorage.getItem("${entityName}${prop?cap_first}").split(", ");
		var itemPosition = oldItems.indexOf(item);
		oldItems.splice(itemPosition, 1);
				
		if(oldItems.length > 0){
			sessionStorage.setItem("${entityName}${prop?cap_first}", oldItems.join(", "));
		}else{
			sessionStorage.removeItem("${entityName}${prop?cap_first}");
		}
		<#else>
		sessionStorage.setItem("selected${entityName}${prop?cap_first}", document.querySelector("#input${prop}").value);
		</#if></#list>
	}
	
	
	function create${entityName}() {
		var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
		};
			
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};

		req.onsuccess=function(evt){
		
		var newInstanceName = "${entityName}" + (new Date()).getTime();
		var newData = {instanceName: newInstanceName};
		
		<#list secretProperties as prop, isSecret>
		
		if("${entityName}${prop?cap_first}" in sessionStorage){
			newData['${prop}'] = sessionStorage.getItem("${entityName}${prop?cap_first}");
			sessionStorage.removeItem("${entityName}${prop?cap_first}");
		}else{
			<#if isSecret>
			var s = document.querySelector("#input${prop}").value;
			var h = 0, l = s.length, i = 0;
  			if ( l > 0 )
    			while (i < l)
      				h = (h << 5) - h + s.charCodeAt(i++) | 0;
			newData['${prop}'] = h.toString();
			<#else>
			newData['${prop}'] = document.querySelector("#input${prop}").value;
			</#if>
		}
		</#list>
			
		var active = req.result;
        var data = active.transaction(['${entityName}'], 'readwrite');
        var objectStore = data.objectStore("${entityName}");
        objectStore.add(newData);
            
        sessionStorage.clear();
        location.href = location.protocol + '//' + location.host + "/${entityName?lower_case}.html?val=" + newInstanceName;
          		
       };
	};
	</script>
</head>
<body onload="getData();">
	<h1 class="header section1" id=${entityName}>Create ${entityName?cap_first}</h1>
	<form>
	<fieldset>
		<legend>Details</legend>
		<table id="create${entityName}">
${tableCreate}
		</table>
	</fieldset>
	<button name="save${entityName}" onclick="create${entityName}();">Save</button>
	</form>
</body>
</html>