/*Carolina Morales Aguayo*/
import {encryptPass, <#list functions as function>${function}<#sep>, </#list>} from './functions.js';

<#if rules??>
//Set access control before loading pages.
var rules = {
<#list rules as page, rule>	"${page}":"${rule}"<#sep>,
</#list>
};

//Check the rule
var pathName = window.location.pathname.substring(1, window.location.pathname.length-5);
var rule = rules[pathName];
if (rule == "false"){
	window.location.href = window.location.protocol + '//' + window.location.host + "/accessDenied.html";
}
else if (rule == "loggedIn" && !("loginUser" in sessionStorage)){
	window.location.href = window.location.protocol + '//' + window.location.host + "/accessDenied.html";
}  	
</#if>

window.onload = function(){

	//Objects for database management
	var db;
	var requestObject;
	var requestObjects;

	function startDB() { //Open indexedDB
		
		var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;                        
        var req = indexedDB.open("${appName}", 1);

		req.onupgradeneeded = function(evt){
			console.log("openDB.onupgradedneeded");
			var db = evt.target.result;
			
			db.onerror = function(){
				console.log(database.errorCode);	
			};
	
			var transaction = evt.target.transaction;
			//If successfull, create stores and already defined objects
<#list objects as obj>${obj}</#list>
			loadPage();
		};

		req.onsuccess=function(evt){
			db = this.result;
			console.log("DB opened!");
			loadPage();
		};
	
		req.onerror = function(evt){
			console.error("openDB:", evt.target.errorCode);
		};
	};
	
	//Function to select the store to access
	function getObjectStore(store_name, mode) {
   		var tx = db.transaction(store_name, mode);
   		return tx.objectStore(store_name);
	};
	
	//Function to get a single object of the indexedDB
	function getSingleFromIndexedDB(store, requestedValue){
    	return new Promise(resolve => {

	    	var objectStore = getObjectStore(store, 'readonly')
    	    var request = objectStore.get(requestedValue);
           	
	    	request.onerror = function(event) {
        		console.log("error while getting " + defaultValue + " from " + store);
  				// Handle errors!
  				resolved(false);
			};
		
			request.onsuccess = function(event) {
				requestObject = request.result;
				resolve(true);
  			};
  		});       	
	};
	
	//Function to return all objects in the same store
	function getAllFromIndexedDB(store){
		return new Promise(resolve => {
			var objectStore = getObjectStore(store, 'readonly');
		
			var request = objectStore.getAll();
		
			request.onerror = function(event) {
				console.log("error while getting data from " + store);
				resolve(false);
			};
			request.onsuccess = function(event) {
				requestObjects = request.result;
				resolve(true);
  			};
  		});
	};
	
	//function to insert (add or update) a single object in indexedDB
	function insertItemToIndexedDB(storeName, item, newItem){
		
		var store = getObjectStore (storeName, 'readwrite');
		var request;
		
		if (newItem){
			request = store.add(item);
		}else{
			request = store.put(item);
		}
		
		request.onsucess = function(evt){
			console.log("Item " + item.name + " inserted into indexeddb");
		};
		request.onerror = function(evt){
			console.log("Error while inserting " + item.name + " into " + storeName);
		};
	};
		
	//function to delete an object from indexedDB
	function deleteItemFromIndexedDB(storeName, item){
		
		var store = getObjectStore (storeName, 'readwrite');
		var request = store.delete(item);
		
		request.onsucess = function(evt){
			console.log("Item " + item.name + " deleted from indexeddb");
		};
		request.onerror = function(evt){
			console.log("Error while deleting " + item.name + " from " + storeName);
		};
	};
	
	//function to delete all items in sessionStorage except login ones
	function deleteSessionStorage(){
		//Delete all session storage except AC items
		for (var key in sessionStorage){	
			if (!key.includes("login")){
   				sessionStorage.removeItem(key);
  			}
  		}
	};
	
	//Add all functions for CRUD entities which need indexedDB access
	<#list evFunctions as function, content>
	async function ${function}(){
		${content}
	};
	
	</#list>
	
	//similar to "window.onload", but for specific pages.
	async function loadPage(){
	
		//View pages
		
		if (<#list crudEntities as entity, linkEntities>location.href.includes("/${entity?lower_case}.html")){
			//Get argument from URL
       		var argURL = location.search.substr(1);
			var linkInstancesValue = [];
		
			//Get object to view from IndexedDB
			await getSingleFromIndexedDB("${entity}", argURL.split("=")[1]);
			var instanceValue = requestObject;
			<#list linkEntities as linkEntity>
			//Get data from ${linkEntity} which is linked to ${entity}
			await getAllFromIndexedDB("${linkEntity}");
			for (var i = 0; i < requestObjects.length; i++){
				linkInstancesValue.push(requestObjects[i]);
			}
			</#list>
			//Show the selected object
			showTable${entity}(instanceValue, linkInstancesValue);
		
		} <#sep>else if( </#list>
		//Manage pages
		else if(<#list crudEntities as entity, linkEntities>location.href.includes("/manage${entity?cap_first}.html")){
			//Get all objects from ${entity} store
			var values = [];
			
			await getAllFromIndexedDB("${entity}");
			for (var i = 0; i < requestObjects.length; i++){
				values.push(requestObjects[i]);
			}
			
			showManage${entity}(values);
			
			//Set a different event Listener for each 'remove' button.
			for (var i = 0; i < values.length; i++){
				document.getElementById("deleteInstance"+values[i].instanceName).addEventListener("click", function(){
					deleteInstance${entity}();	
				});
			}
		
		} <#sep>else if( </#list>
		//Create-Edit pages
		else if(<#list crudEntities as entity, linkEntities>location.href.includes("${entity?cap_first}.html")){
			
			var linkInstancesMap = new Object();
			var linkInstancesValues = [];
			var values = []
			var instanceValue = null;
			
			//Get selected object if edit.
			if (location.href.includes("/edit${entity?cap_first}.html")){
				var argURL = location.search.substr(1);
				await getSingleFromIndexedDB("${entity}", argURL.split("=")[1]);
				instanceValue = requestObject;
			}
			
			//Get all objects from ${entity}, used for inverse purposes.
			await getAllFromIndexedDB("${entity}");
			for (var i = 0; i < requestObjects.length; i++){
				values.push(requestObjects[i]);
			}
			<#list editableLinkedEntities as ent, editEntities><#if ent == entity><#list editEntities as editEntity>
			//get all objects from ${editEntity} which is linked to ${ent}
			await getAllFromIndexedDB("${editEntity}");
			for (var i = 0; i < requestObjects.length; i++){
				linkInstancesValues.push(requestObjects[i]);
			}
			linkInstancesMap["${editEntity}"] = linkInstancesValues;
			linkInstancesValues = [];
			</#list></#if></#list>
			showForm${entity}(values, linkInstancesMap, instanceValue);
		
		} <#sep>else if( </#list>
			
	}
	
	function startEventListener(){
		
		//Add all event listeners
		<#list eventListener as button, function>
		if(document.getElementById("${button}")){
			document.getElementById("${button}").addEventListener("click", function(){
				${function}();	
			});
    	}
    	</#list>
	};
	
	//Authentication element
	<#if authentication??>
${authentication}</#if>
	
	startDB();
	startEventListener();
}