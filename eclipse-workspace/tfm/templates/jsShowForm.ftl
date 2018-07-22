	
	//set elements which are already in an inverse property 
	var elements = [];
	<#list inverseEditable as invEnt, invProp>var instancesInverse${invEnt} = instancesMap["${invEnt}"];
	
	if (!("inverse${invEnt}${invProp?cap_first}" in sessionStorage)){
		values.forEach(function(value){
		<#list linkEntitiesOrNull as linkProp, linkEnt><#if linkEnt??><#if invEnt == linkEnt>
			if(value.${linkProp}.length>0 && (defaultValue == null || value.${linkProp}!=defaultValue.${linkProp})){elements.push(value.${linkProp})};
				</#if></#if></#list>
		});
		if (elements.length>0){
			sessionStorage.setItem("inverse${invEnt}${invProp?cap_first}", elements.join(", "));
		}
	}
	</#list>
	
	//Set default value in the form -- for 'edit'
	if (defaultValue != null){
		if(!("currentInstance${entityName}" in sessionStorage)){
			sessionStorage.setItem("currentInstance${entityName}", defaultValue.name);
		}
			
		<#list properties as prop, isSet>
		<#if isSet>
		if (!("${entityName}${prop?cap_first}" in sessionStorage) && defaultValue.${prop} != null){
			sessionStorage.setItem("${entityName}${prop?cap_first}", defaultValue.${prop});
		}<#else>
		if(!("selected${entityName}${prop?cap_first}" in sessionStorage)){
			sessionStorage.setItem("selected${entityName}${prop?cap_first}", defaultValue.${prop});
		}
		</#if>
		</#list>
	}
	
	//Set header title
	if (document.querySelector("#edit${entityName}")){
		document.querySelector("#edit${entityName}").innerHTML = "Edit " + sessionStorage.getItem("currentInstance${entityName}");
	}
	
	<#list linkEntitiesOrNull as linkProp, linkEntity>
	<#if linkEntity??>
	
	if (instancesMap["${linkEntity}"]){
		//Generate 'select' element for linked entites.
		var instancesLink${linkEntity} = instancesMap["${linkEntity}"];
		
		var selectInput ="";
		selectInput += "<option value></option>";
		
		//Generate a list of boxes for already selected object for list properties.
		var alreadySelected = "";
		instancesLink${linkEntity}.forEach(function (instanceLink${linkEntity}) {	
			sessionStorage.setItem( instanceLink${linkEntity}.instanceName, instanceLink${linkEntity}.name);
			if(!("${entityName}${linkProp?cap_first}" in sessionStorage) || (sessionStorage.getItem("${entityName}${linkProp?cap_first}")).split(", ").indexOf(instanceLink${linkEntity}.instanceName)<0){
			<#list inverseEditable as invEnt, invProp><#if invEnt == linkEntity>
				console.log("estoy aqui dentro");
				if(!("inverse${invEnt}${invProp?cap_first}" in sessionStorage) || sessionStorage.getItem("inverse${invEnt}${invProp?cap_first}").split(", ").indexOf(instanceLink${linkEntity}.instanceName)<0){	
				</#if></#list>
				selectInput += "<option value=" + instanceLink${linkEntity}.instanceName;
				if (("selected${entityName}${linkProp?cap_first}" in sessionStorage) && sessionStorage.getItem("selected${entityName}${linkProp?cap_first}") == instanceLink${linkEntity}.instanceName)
					{selectInput += " selected";} 
				selectInput +=">" + instanceLink${linkEntity}.name + "</option>\n";
				<#list inverseEditable as invEnt, invProp><#if invEnt == linkEntity>}</#if></#list>	
			}
			<#list properties as propName, isSet><#if isSet><#if propName == linkProp>
			else{ //Add item to the list
				alreadySelected += "<fieldset id=${linkProp}><label>" + instanceLink${linkEntity}.name + "</label><button onclick='remove${entityName}Property(\""+instanceLink${linkEntity}.instanceName+"\")'>x</button></fieldset>";	
			}</#if></#if></#list>		   
		});
					
		<#if inverseNonEditable?has_content><#list inverseNonEditable as invEnt, invProp><#if invEnt == linkEntity> <#else>
		document.querySelector("#input${linkProp}").innerHTML = selectInput;</#if></#list>
		<#else>
		document.querySelector("#input${linkProp}").innerHTML = selectInput;
		</#if>
	}	
	<#else>
	if ("selected${entityName}${linkProp?cap_first}" in sessionStorage){
		document.querySelector("#input${linkProp}").value = sessionStorage.getItem("selected${entityName}${linkProp?cap_first}");
	} 	
	</#if></#list>		
	
	//Add already selected items for list properties
	<#list properties as prop, isSet>
	<#if isSet>
	if("${entityName}${prop?cap_first}" in sessionStorage){
		var object${prop?cap_first}Added = sessionStorage.getItem("${entityName}${prop?cap_first}");
		var output = "<p>";
		object${prop?cap_first}Added.split(", ").forEach(function(object){
			if(sessionStorage.getItem(object)){
				output += "<fieldset id=${prop}><label>" + sessionStorage.getItem(object) + "</label><button onclick='remove${entityName}Property(\""+object+"\")'>x</button></fieldset>";
			}else{
				output += "<fieldset id=${prop}><label>" + object + "</label><button onclick='remove${entityName}Property(\""+object+"\")'>x</button></fieldset>";					
			}
		});
		output +="</p>";
		document.querySelector("#add${prop}").innerHTML = output;
	}
	</#if>
	</#list>