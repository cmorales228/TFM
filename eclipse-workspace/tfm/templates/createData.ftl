 
    				{ instanceName : "${nameInstance}", <#list properties as propName, value>${propName}:<#list secretProperties as secretProp, isSecret><#if secretProp == propName><#if isSecret>encryptPass("${value}")<#elseif value?contains("\"")>${value}<#else>"${value}"</#if></#if></#list><#sep>, 
    				</#list>}