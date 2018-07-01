module data

  entity Asignatura{
  	name :: String
  	code :: Int
  	coordinador -> Profesor
  }
  
  entity Profesor{
  	name :: String
  	password :: Secret
  	asignatura -> Asignatura (inverse = Asignatura.coordinador)
  	alumnos -> List<Alumno> 
  }
  
  entity Alumno{
  	name :: String
  	tutor -> Profesor (inverse = Profesor.alumnos)
  	asignaturas -> List<Asignatura> 
  }
