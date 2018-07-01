package tfm;

import java.util.LinkedList;

public class PageNavigate implements PageElement {

	private String m_page;
	private LinkedList<String> m_arguments;
	private String m_text;
	
	public PageNavigate() {
		m_arguments = new LinkedList<String>();
	}
	
	public ELEMENT_TYPES getType() {
		return ELEMENT_TYPES.NAVIGATE;
	}
	
	public String getPage() {
		return this.m_page;
	}
	
	public LinkedList<String> getArguments(){
		return this.m_arguments;
	}
	
	public String getText() {
		return this.m_text;
	}
	
	public void setPage(String page) {
		this.m_page = page;
	}
	
	public void addArgument(String argument) {

		this.m_arguments.add(argument);
	}
	
	public void setText(String text) {
		this.m_text = text;
	}
}
