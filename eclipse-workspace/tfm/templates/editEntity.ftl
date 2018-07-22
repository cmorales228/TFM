<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="utf-8">
	<script src="./js/index.js" type="module"></script>
	<script>
	//Function to temporarily add a value to a property
	//		No access to indexedDB
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
	
	
	//Function to temporarily remove a value from a linked property
	//		No access to indexedDB
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
	</script>
</head>
<body>
	<h1 class="header section1" id="edit${entityName}"></h1>
	<form>
	<fieldset>
		<legend>Details</legend>
		<table id="editTable${entityName}">
${tableCreate}
		</table>
	</fieldset>
	<button name="update" id=update${entityName} onclick="return false;">Update</button>
	</form>
</body>
</html>