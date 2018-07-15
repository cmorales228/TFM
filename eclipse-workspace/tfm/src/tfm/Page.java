package tfm;

import java.util.LinkedList;

public class Page {
	
	String m_name;
	LinkedList <PageElement> m_elements;
	
	public Page() {
		
	}
	
	public Page (String name) {
		m_name = name;
		m_elements = new LinkedList<PageElement>();
	}
	
	public void addElement(PageElement e) {
		m_elements.add(e);
	}
	
	public String getName() {
		return m_name;
	}

	public LinkedList <PageElement> getElements(){
		return m_elements;
	}
}
