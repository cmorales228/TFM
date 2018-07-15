package tfm;

import java.util.LinkedHashMap;
import tfm.FormElement.FORM_ELEMENT_TYPE;

public class PageForm implements PageElement {

	private SUBMIT_TYPE m_submitType;
	private String submitText;
	private String action;
	private LinkedHashMap<FORM_ELEMENT_TYPE, FormElement> m_formElements;
	
	private enum SUBMIT_TYPE {
		LINK, BUTTON
	}
	
	public PageForm() {
		m_formElements = new LinkedHashMap<FORM_ELEMENT_TYPE, FormElement>();
	}
	
	public ELEMENT_TYPES getType() {
		return ELEMENT_TYPES.FORM;
	}

}
