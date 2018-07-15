					
					//Add entity ${entityName}, property ${prop?cap_first}
					outerTable += "\n\t<tr>\n\t\t<td>${prop?cap_first}:</td>\n\t\t<td>";
					<#if typeIsEntity>
					outerTable+="<select>";
					requestLink${pType}.result.forEach(function(instance${pType}){
						outerTable +="<option value='" + instance${pType}.instanceName + "' ";
						if(instance${pType}.instanceName == result${entityName}.${prop}){ outerTable+="selected"; }
						outerTable += ">" + instance${pType}.name + "</option>";
					});
					outerTable+="</select>";
					<#elseif pType == "Secret">
					outerTable += "<input type='password' name='${prop}' value='result${entityName}.${prop}'><br>\n";
					<#else>
					outerTable +="<input type='text' name='${prop}' value='" + result${entityName}.${prop} + "'><br>\n";
					</#if>
					outerTable += "</td>\n\t</tr>";
					
					