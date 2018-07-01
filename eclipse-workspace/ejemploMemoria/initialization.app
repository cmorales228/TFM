module initialization

init{
  	var asig1 := Asignatura{
  		name := "Asignatura1"
  	};
  	var asig2 := Asignatura{
  		name := "Asignatura2"
  	};
  	
  	var prof1 := Profesor{
  		name := "Profesor1"
  		password := "Profesor1"
  		asignatura := asig1
  	};
  	
  	var prof2 := Profesor{
  		name := "Profesor2"
  		password := "Profesor2"
  		asignatura := asig2
  	};
  	
  	var alumn1 := Alumno{
  		name := "Alumno1"
  		tutor := prof1
  		asignaturas := [asig1, asig2]
  	};
  	
  	var alumn2 := Alumno{
  		name := "Alumno2"
  		tutor := prof1
  		asignaturas := [asig1, asig2]
  	};
  	
  	asig1.save();
  	prof1.save();
  }