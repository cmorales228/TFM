package tfm;

import java.util.Map;

public class Instancia {
	
	private String m_nombre;
	private Entity m_entidad;
	Map<Property, String> m_valores;
	
	public Instancia() {
		
	}
	
	public Instancia(String nombre, Entity entidad) {
		m_nombre = nombre;
		m_entidad = entidad;
	}
	
	public String getNombre() {
		return m_nombre;
	}
	
	public Entity getEntidad() {
		return m_entidad;
	}
	
	public String getValue(Property property) {
		return m_valores.get(property);
	}
	
	public void setNombre(String nombre) {
		m_nombre = nombre;
	}
	
	public void setEntidad(Entity entidad) {
		m_entidad = entidad;
	}
	
	public void addValue(Property property, String value) {
		m_valores.put(property, value);
	}
	
}
