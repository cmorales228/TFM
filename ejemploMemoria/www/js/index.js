/*
 * Carolina Morales Aguayo
 */

const DB_NAME = "EjemploMemoria";
const DB_VERSION = 1;

openDB();

function setCurrentVar(currentVar){
		console.log("var " + currentVar);

		var m_var = [{name : currentVar}];
		var database = window.db.myDB;
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
						//Create Asignatura
		var storeAsignatura = database.createObjectStore('Asignatura', {keyPath: 'name'});

		storeAsignatura.createIndex('coordinador', 'coordinador', {unique : false}); 
		storeAsignatura.createIndex('code', 'code', {unique : false}); 
		storeAsignatura.createIndex('name', 'name', {unique : false}); 
	
		storeAsignatura.transaction.oncomplete = function (event) {
  
        	var AsignaturaStore = database.transaction('Asignatura', 'readwrite').objectStore('Asignatura');
        
      		var AsignaturaData = [  
    		{ instanceName : "asig1", coordinador:prof1, name:"Asignatura1"},  
    		{ instanceName : "asig2", coordinador:prof2, name:"Asignatura2"}];
        
        	AsignaturaData.forEach(function (Asignatura) {
           		AsignaturaStore.add(Asignatura);
        	});
    	};
    	//End Asignatura
    
		//Create Profesor
		var storeProfesor = database.createObjectStore('Profesor', {keyPath: 'name'});

		storeProfesor.createIndex('password', 'password', {unique : false}); 
		storeProfesor.createIndex('asignatura', 'asignatura', {unique : false}); 
		storeProfesor.createIndex('alumnos', 'alumnos', {unique : false}); 
		storeProfesor.createIndex('name', 'name', {unique : false}); 
	
		storeProfesor.transaction.oncomplete = function (event) {
  
        	var ProfesorStore = database.transaction('Profesor', 'readwrite').objectStore('Profesor');
        
      		var ProfesorData = [  
    		{ instanceName : "prof1", password:"Profesor1", asignatura:asig1, alumnos:alumn1, name:"Profesor1"},  
    		{ instanceName : "prof2", password:"Profesor2", asignatura:asig2, name:"Profesor2"}];
        
        	ProfesorData.forEach(function (Profesor) {
           		ProfesorStore.add(Profesor);
        	});
    	};
    	//End Profesor
    
		//Create Alumno
		var storeAlumno = database.createObjectStore('Alumno', {keyPath: 'name'});

		storeAlumno.createIndex('name', 'name', {unique : false}); 
		storeAlumno.createIndex('asignaturas', 'asignaturas', {unique : false}); 
		storeAlumno.createIndex('tutor', 'tutor', {unique : false}); 
	
		storeAlumno.transaction.oncomplete = function (event) {
  
        	var AlumnoStore = database.transaction('Alumno', 'readwrite').objectStore('Alumno');
        
      		var AlumnoData = [  
    		{ instanceName : "alumn1", name:"Alumno1", asignaturas:[asig1, asig2], tutor:prof1},  
    		{ instanceName : "alumn2", name:"Alumno2", asignaturas:[asig1, asig2], tutor:prof1}];
        
        	AlumnoData.forEach(function (Alumno) {
           		AlumnoStore.add(Alumno);
        	});
    	};
    	//End Alumno
    

			}
		else{
			console.log("La base de datos ya existe4");
		}
	};
};
