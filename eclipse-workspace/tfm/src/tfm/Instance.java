package tfm;

import java.util.HashMap;

public class Instance {
	
	private String m_name;
	private Entity m_entity;
	HashMap<String, String> m_values;
	
	public Instance() {
		
	}
	
	public Instance(String name, Entity entity) {
		m_name = name;
		m_entity = entity;
		m_values = new HashMap<String, String>();
	}
	
	public String getName() {
		return m_name;
	}
	
	public Entity getEntity() {
		return m_entity;
	}
	
	public String getValue(Property property) {
		return m_values.get(property.getName());
	}
	
	public HashMap<String, String> getValues(){
		return this.m_values;
	}
	
	public void setName(String name) {
		this.m_name = name;
	}
	
	public void setEntidad(Entity entidad) {
		this.m_entity = entidad;
	}
	
	public void addValue(Property property, String value) {
		
		String newValue = (value.replace("[", "")).replace("]","");
		
		if (property.isSetOrList() && m_values.containsKey(property.getName())) {
			String lastValue = m_values.get(property.getName());
			m_values.put(property.getName(), lastValue + ", " + newValue);
		}
		else {
			m_values.put(property.getName(), newValue);
		}
	}
	
	public boolean hasValue(String property) {
		return m_values.containsKey(property);
	}
	
}
