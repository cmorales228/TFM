<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="utf-8">
<script>

	var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

    function getData() {
		
		var urlLocation = location.protocol + "//" + location.host;
		
		document.querySelector("#create${entityName}").href = urlLocation + "/create${entityName?cap_first}.html";
                       
    	var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
		};
			
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};

		req.onsuccess=function(evt){
		
			var active = req.result;
            var data = active.transaction(['${entityName}'], 'readonly');
            var objectStore = data.objectStore("${entityName}");
        	var allObjectsStore = objectStore.getAll();    
        
        	allObjectsStore.onerror = function(event) {
           		console.log("error while getting " + requestValue + " from ${entityName}");
  				// Handle errors!
			};
			allObjectsStore.onsuccess = function(event) {
        		output = "";
        		allObjectsStore.result.forEach(function (object${entityName}){
        			output += "<p><a href='" + urlLocation + "/${entityName?lower_case}.html?val=" + object${entityName}.instanceName + "'>" + object${entityName}.name +"</a> ";
					output += "<a href='" + urlLocation + "/edit${entityName?lower_case}.html?val=" + object${entityName}.instanceName + "'>edit</a></p>";
					output += "<button onclick='deleteData(\"" + object${entityName}.instanceName + "\");'>remove</button>\n";
        		});
        		document.querySelector("#manage${entityName}").innerHTML = output;
        	};		
		};
	};
	
	function deleteData(deleteEntityName){
	
		console.log("deletedata from ${entityName} with name " + deleteEntityName);
		var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("ONUPGRADEDNEEDED: ${appName}.read${entityName}: YOU MUST NOT SEE THIS MESSAGE");
		};
			
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};

		req.onsuccess=function(evt){
			var active = req.result;
            var data = active.transaction(['${entityName}'], 'readwrite');
            var objectStore = data.objectStore("${entityName}");
            var request = objectStore.delete(deleteEntityName);
            
            request.onerror = function (evt){
            	console.error("Error while removing " + deleteEntityName + " from database");
            };
            request.onsuccess = function (evt){
            	<#list propToDelete as delEntity, delProp>
            	var data = active.transaction(['${delEntity}'], 'readwrite');
            	var objectStoreDel = data.objectStore("${delEntity}");
            	var requestDel = objectStoreDel.getAll();
            	requestDel.onerror = function (evt){
            		console.error("Error while getting data from ${delEntity}")
            	};
            	requestDel.onsuccess = function (evt){
            		requestDel.result.forEach(function(result${delEntity}){
            			console.log("profesor: " + result${delEntity}.instanceName);
            			if(result${delEntity}.${delProp} && result${delEntity}.${delProp}.split(", ").includes(deleteEntityName)){
            				
            				var items = result${delEntity}.${delProp}.split(", ");
							var itemPosition = items.indexOf(deleteEntityName);
							items.splice(itemPosition, 1);
							result${delEntity}.${delProp} = items.join(", ");
							
							var requestUpdate = objectStoreDel.put(result${delEntity});
            			}
            		});
            	};
            		
            	</#list>
            	console.log("Element " + deleteEntityName + " removed from ${entityName}");
            	sessionStorage.clear();
            	getData();
            };
		};
	};
	</script>
</head>
<body onload="getData();">
	<h1 class="header section1" id=${entityName}>Manage ${entityName}</h1>
	<a id=create${entityName}>create</a>
	<p id="manage${entityName}">
	</p>
</body>
</html>