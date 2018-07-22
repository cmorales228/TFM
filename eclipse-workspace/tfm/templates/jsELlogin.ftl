	<#list credentials as prop, isSecret><#if isSecret>
	var cred${prop?cap_first} = encryptPass(document.getElementById("cred${prop?cap_first}").value);<#else>
	var cred${prop?cap_first} = document.getElementById("cred${prop?cap_first}").value;</#if></#list>		

	await getAllFromIndexedDB("${principal}");
	
	var result${principal} = requestObjects;
    var loggedIn = false;
      				
	result${principal}.forEach(function(result){
		//Check all possibilities to log in
    	if (<#list credentials as prop, isSecret> cred${prop?cap_first} == result.${prop} <#sep>&& </#list>){
    		sessionStorage.setItem("loginUser", result.name);		
    		loggedIn = true;
    	}
    });
    
    //Set an error if not logged in  				
    if (!loggedIn){
    	sessionStorage.setItem("loginResult", "<#list credentials as prop, isSecret>${prop?cap_first}<#sep> or </#list> are not correct.");
    }