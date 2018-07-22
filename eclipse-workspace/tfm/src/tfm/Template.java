package tfm;

public class Template implements PageElement{

	private String m_name;
	
	public Template(String name) {
		this.m_name = name;
	}
	
	@Override
	public ELEMENT_TYPES getType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPES.TEMPLATE;
	}
	
	public String getName() {
		return this.m_name;
	}

}
