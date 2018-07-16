module ac

principal is Profesor with credentials name, password

access control rules
	
	rule page root(){true}

	pointcut viewPages(){
		page alumno(*),
		page profesor(*),
		page asignatura(*)
	}
	
	pointcut otherPages(){
		page create*(),
		page edit*(*),
		page manage*()		
	}
	
	rule pointcut viewPages(){true}
	
	rule pointcut otherPages(){loggedIn()}
	