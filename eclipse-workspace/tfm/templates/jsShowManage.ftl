	
	var urlLocation = location.protocol + "//" + location.host;
	//Add create link		
	document.querySelector("#create${entityName}").href = urlLocation + "/create${entityName?cap_first}.html";    
	
	var output = "";
	//For each value, add view, edit and remove
    values.forEach(function (value){
    	output += "<p><a href='" + urlLocation + "/${entityName?lower_case}.html?val=" + value.instanceName + "'>" + value.name +"</a> ";
		output += "<a href='" + urlLocation + "/edit${entityName?cap_first}.html?val=" + value.instanceName + "'>edit</a></p>";
		output += "<button id=deleteInstance"+ value.instanceName + " onclick='sessionStorage.setItem(\"deleteInstance\", \"" + value.instanceName + "\");return false;'>remove</button>\n";
        });
   	document.querySelector("#manage${entityName}").innerHTML = output;