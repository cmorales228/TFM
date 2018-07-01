/*
 * Carolina Morales Aguayo
 */

const DB_NAME = "${appName}";
const DB_VERSION = 1;

openDB();

function setCurrentVar(currentVar){
		console.log("var " + currentVar);

		var m_var = [{name : currentVar}];
		var transaction = database.transaction(["current"], "readwrite");
		transaction.onerror = function(event){
			console.log(db.errorCode);
		}

		var objectStore =  transaction.objectStore("current");
		var request = objectStore.add(m_var);
		request.onsuccess = function(event){
			console.log("valor introducido");
		}	
}
	
function openDB() {

	console.log("openDB...");
	var req = indexedDB.open(DB_NAME, DB_VERSION);
	var database;
	req.onsuccess=function(evt){
		database=req.result;
		console.log("DB opened!");
	};
	
	req.onerror = function(evt){
		console.error("openDB:", evt.target.errorCode);
	};
	req.onupgradeneeded = function(evt){
		console.log("openDB.onupgradedneeded");
		database = evt.target.result;
		database.onerror = function(){
			console.log(database.errorCode);	
		};
	
		if (!database.objectStoreNames.contains('current')){
			console.log("creando todas los almacenes");
			var storeCurrent = database.createObjectStore('current', {keyPath: 'name'});
				<#list objects as obj>${obj}</#list>
			}
		else{
			console.log("La base de datos ya existe4");
		}
	};
};
