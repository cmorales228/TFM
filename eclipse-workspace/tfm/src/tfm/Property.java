package tfm;

public class Property {

	private String m_type = "";
	private String m_name = "";
	private boolean m_isSetOrList = false;
	private String m_pinverse = null;
	private String m_einverse = null;
	private Boolean m_editable = true;
	
	public Property() {
		
	}

	public String getType() {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}
	
	public boolean isSetOrList() {
		return m_isSetOrList;
	}
	
	public String getPinverse() {
		return m_pinverse;
	}
	
	public String getEinverse() {
		return m_einverse;
	}
	
	public Boolean getEditable() {
		return m_editable;
	}
	
	public void setType(String type) {
		this.m_type = type;
	}
	
	public void setName(String name) {
		this.m_name = name;
	}
	
	public void setIsSetOrList(boolean isSetOrList) {
		this.m_isSetOrList = isSetOrList;
	}
	
	public void setPinverse(String pinverse) {
		this.m_pinverse = pinverse;
	}

	public void setEinverse(String einverse) {
		this.m_einverse = einverse;
	}
	
	public void setEditable(Boolean editable) {
		this.m_editable = editable;
	}
}
