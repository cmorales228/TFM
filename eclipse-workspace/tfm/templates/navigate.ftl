		<a href='${page.getPage()}.html<#if arguments?has_content><#list arguments as arg>?val=${arg?replace("\"","")}<#sep>?</#list></#if>' id="${page.getPage()}" class="navigate">${page.getText()}</a>