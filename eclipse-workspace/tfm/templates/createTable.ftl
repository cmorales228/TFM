			<tr><td>${propName?cap_first}: </td>
			<td><#if isLinkEntity??> <select name="input${propName}" id="input${propName}"></select>
			<#elseif isBoolean??> <input type="radio" name="input${propName}" id="input${propName}" value="True"><br><input type="radio" name="input${propName}" id="input${propName}" value="False">
			<#elseif isSecret??> <input type="password" id="input${propName}"/>
			<#else><input type="text" id="input${propName}"/></#if></td>
			<#if isSetorList><td><p id=add${propName}></p><p><button onclick="add${entityName}Property();">Add</button></p></td></#if></tr>				