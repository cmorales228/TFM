package tfm;

import java.util.LinkedHashMap;

public class PageOutput implements PageElement {

	private LinkedHashMap<String, Boolean> m_outputElements;
	
	public PageOutput() {
		m_outputElements = new LinkedHashMap<String, Boolean>();
	}
	
	public ELEMENT_TYPES getType() {
		return ELEMENT_TYPES.OUTPUT;
	}
	
	public void addOutputElement(String outputString, Boolean literal) {
		m_outputElements.put(outputString, literal);
	}
	
	public LinkedHashMap<String, Boolean> getOutputElements(){
		return m_outputElements; 
	}
}
