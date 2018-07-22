/*Carolina Morales Aguayo*/

export function encryptPass(value){
		var h = 0, l = value.length, i = 0;
			if ( l > 0 )
			while (i < l)
  				h = (h << 5) - h + value.charCodeAt(i++) | 0;
		return h.toString();
	};

<#list functions as function, content>
export function ${function}(<#list argFunctions as func, args><#if function == func><#list args as arg>${arg}<#sep>, </#list></#if></#list>){
	${content}
};

</#list>