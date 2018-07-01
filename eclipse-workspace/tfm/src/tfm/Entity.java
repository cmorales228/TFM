package tfm;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Entity {

	private String m_name;
	private HashMap<String, Property> m_properties;
	private boolean m_crud;
	private LinkedHashMap<String, Instance> m_instances;
	
	public Entity (String name) {
		m_name = name;
		m_properties = new HashMap<String, Property>();
		m_crud = false;
		m_instances = new LinkedHashMap<String, Instance>();
	}

	public String getName() {
		return m_name;
	}
	
	public void setNombre(String name) {
		m_name = name;
	}

	public void addProperty(Property property) {
		m_properties.put(property.getName(),property);
	}
	
	public void addInstance(Instance i) {
		m_instances.put(i.getName(), i);
	}
	
	public HashMap<String, Property> getProperties() {
		return this.m_properties;
	}	
	
	public void setCrud(boolean crud) {
		this.m_crud = crud;
	}
	
	public boolean getCrud() {
		return this.m_crud;
	}
	
	public HashMap<String, Instance> getInstances(){
		return this.m_instances;
	}
}
