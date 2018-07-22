	if (document.getElementById("authentication")){
		//If there is already a user logged in
		if("loginUser" in sessionStorage){
			document.getElementById("loginResult").innerHTML = "You are now logged in as " + sessionStorage.getItem("loginUser");
			document.getElementById("authentication").innerHTML = authForm = "<form><button id=logoutButton>Log out</button></form>";
		}
		else{ //If none user is logged in
			var authForm = "<form><fieldset><table>";
			<#list credentials as prop, isSecret>
			authForm += "<tr><td><label>${prop?cap_first}:</label></td><td><input type=";
			<#if isSecret> authForm += "password";<#else>authForm += "text";</#if>
			authForm += " id=cred${prop?cap_first}></input></td></tr><p/>";
		
			</#list>
			authForm += "</table>";
			authForm += "<button id=loginButton>Log in</button>"
			authForm += "</fieldset></form>";
		
			document.getElementById("authentication").innerHTML = authForm;
		
			document.getElementById("loginResult").innerHTML = sessionStorage.getItem("loginResult");
		}
	}
	