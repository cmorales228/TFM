application EjemploMemoria

imports data 
imports initialization

derive crud Asignatura
derive crud Profesor
derive crud Alumno
  
page root(){ 
	
 	navigate asignatura((from Asignatura)[0]) {"viewAsignatura"}
    " "
    navigate manageAsignatura() {"manage asignatura"}
    " "
    navigate editAsignatura((from Asignatura)[0]) {"edit asignatura"}
    " "
    navigate createAsignatura() {"create asignatura"}
}