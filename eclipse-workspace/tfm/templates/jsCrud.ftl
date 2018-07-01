function _manage${entity}(currentInstance){
	m_var = currentInstance;
}

function _edit${entity}(currentInstance){
	m_var = currentInstance;
	}
	
function _create${entity}(){
	}
	
function _${entity?lower_case}(currentVar){
	//m_var = document.getElementById("asignatura");
	console.log(currentVar);
	m_var = instances[currentVar];
	console.log(m_var.name);
	//m_var = currentInstance;
	//window.location.href = "asignatura.html";
	}
	
document.getElementById("${entity?lower_case}").addEventListener("click", _${entity?lower_case});
	
	

	
