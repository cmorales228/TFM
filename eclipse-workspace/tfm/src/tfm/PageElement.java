package tfm;

public interface PageElement {
	
	public enum ELEMENT_TYPES{
		NAVIGATE, OUTPUT, FORM, NONE
	}

	public ELEMENT_TYPES m_type = ELEMENT_TYPES.NONE;
	
	public ELEMENT_TYPES getType();
	
}
