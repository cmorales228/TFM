application EjemploMemoria

imports data 
imports initialization
imports ac

derive crud Asignatura
derive crud Profesor
derive crud Alumno
  
page root(){ 
 	navigate alumno((from Alumno)[0]) {"viewAlumno"}
    " "
    navigate manageAlumno() {"manage alumno"}
    " "
    navigate editAlumno((from Alumno)[0]) {"edit alumno"}
    " "
    navigate createAlumno() {"create alumno"}
    " "
	authentication()
}