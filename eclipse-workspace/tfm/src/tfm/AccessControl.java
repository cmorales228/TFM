package tfm;

import java.util.LinkedList;
import java.util.Set;
import java.util.HashMap;

public class AccessControl {
	
	private String m_principal;
	private LinkedList<String> m_credentials;
	private String m_appName;
	private HashMap<String, String> m_pageRules;
	private HashMap<String, String> m_pointcutRules;
	private HashMap<String, LinkedList<String>> m_pointcutPages;

	
	public AccessControl (String principal, String credentials, String appName) {
		m_principal = principal;

		m_credentials = new LinkedList<>();
		for (String cred : credentials.split("\\s*,\\s*")) {
			m_credentials.add(cred);
		}
		
		m_appName = appName;
		
		m_pageRules = new HashMap<>();
		m_pointcutRules = new HashMap<>();
		m_pointcutPages = new HashMap<>();
	}
	
	public void addPageRule(String page, String rule) {
		if (page.equals("root")) {
			page = m_appName;
		}
		m_pageRules.put(page, rule);
	}

	public void addPointcutRule(String pointcut, String rule) {
		m_pointcutRules.put(pointcut, rule);
	}
	
	public void addPointcutPage(String pointcut, String page) {
		if (!m_pointcutPages.containsKey(pointcut)) {
			m_pointcutPages.put(pointcut, new LinkedList<>());
		}
		
		if (page !=null) {
			m_pointcutPages.get(pointcut).add(page);
		}
	}
	
	public String getPrincipal() {
		return this.m_principal;
	}
	
	public LinkedList<String> getCredentials(){
		return this.m_credentials;
	}
	
	public HashMap<String, String> getPageRules(){
		return this.m_pageRules;
	}

	public HashMap<String, String> getPointcutRules(){
		return this.m_pointcutRules;
	}
	
	public HashMap<String, LinkedList<String>> getPointcutPages(){
		return this.m_pointcutPages;
	}
	
	public void processPointcuts(Set<String> pages) {
		
		for(String pointcut : m_pointcutPages.keySet()) {
			for(String ppage : m_pointcutPages.get(pointcut)) {
				for (String page: pages) {
					if(ppage.startsWith("*") && page.endsWith(ppage.replace("*", ""))) {
						this.m_pageRules.put(page, m_pointcutRules.get(pointcut));
					}else if (ppage.endsWith("*") && page.startsWith(ppage.replace("*", ""))){
						this.m_pageRules.put(page, m_pointcutRules.get(pointcut));
					}else if(ppage.equals(page)) {
						this.m_pageRules.put(ppage, m_pointcutRules.get(pointcut));
					}
				}
			}
		}
	}

}
