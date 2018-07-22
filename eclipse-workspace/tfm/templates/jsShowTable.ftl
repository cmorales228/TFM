	//Add head title
	document.querySelector("#${entityName}").innerHTML = value.name;
	
	var outerTable = "";
<#list linkEntitiesOrNull as linkProp, linkEnt>
	//Add entity ${entityName}, property ${linkProp?cap_first}	
	outerTable += "\n\t<tr>\n\t\t<td>${linkProp?cap_first}:</td>\n\t\t<td><ul>";
	if (value.${linkProp}!=null){
		value.${linkProp}.split(", ").forEach(function (linkValueItem){
		
		if (value.${linkProp}.includes(", ")){outerTable +="<li>";} //Add a list for listed properties
		<#if linkEnt??>
		instancesValue.forEach(function (instance){//If property is an entity
			if (linkValueItem == instance.instanceName){
				<#list crudEntities as crudEntity, isCrud><#if crudEntity == linkEnt> 
				<#if isCrud>outerTable += "<a href='${linkEnt?lower_case}.html?val=" + linkValueItem + "'> " + instance.name + "</a>\n";
				<#else>outerTable += instance.name;</#if></#if></#list>}
			});
	
		<#else><#list secretProperties as secretProp, isSecret><#if secretProp == linkProp><#if isSecret>
			outerTable += "*********";
		<#else>
			outerTable += linkValueItem;
		</#if></#if></#list>
		</#if>
		if (value.${linkProp}.includes(", ")){outerTable +="</li>";}
    	});		
	}else{ //For non editable properties (related to inverse)
		<#if linkEnt??><#list inverseNonEditable as invEnt, invProp><#if invEnt == linkEnt>
		instancesValue.forEach(function(result${invEnt}){ 
			if (result${invEnt}.${invProp} && result${invEnt}.${invProp}.split(", ").includes(value.instanceName)){
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
