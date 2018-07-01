<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body id="${entityName}">
	<h1 class="header section1">m_var.name</h1>
	<fieldset>
		<legend>Details</legend>
		<table>
			<#list properties as property><tr>
				<td><label>${property?cap_first}: </label></td>
				<td><p id="${property?cap_first}"></p><script>document.getElementById("${property?cap_first}:").innerHTML = m_var.name</script></td>
			</tr></#list>
		</table>
	</fieldset>
</body>
<script type="text/javascript" src="js/index.js"></script>
</html>

			