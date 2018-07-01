package tfm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class GenerateOutput {

	private Configuration m_cfg;
	private String m_outputFolder;
	private HashMap<String, Object> m_javaScript;
	
	public GenerateOutput(String outputFolder) {
		
		m_outputFolder = outputFolder;
		m_javaScript = new HashMap<String, Object>();
		
		try {
			
			m_cfg = new Configuration (Configuration.VERSION_2_3_23);
			m_cfg.setDirectoryForTemplateLoading(new File("templates"));
			m_cfg.setDefaultEncoding("UTF-8");
			m_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateOutputConfig(String appName) {
		try {
		
			Map<String, Object> map = new HashMap<>();
			map.put("appName", appName);
			m_javaScript.put("appName", appName);
		
			Template template = m_cfg.getTemplate("config.ftl");
		
			Writer file = new FileWriter (new File(m_outputFolder + "/config.xml"));
			template.process(map, file);

		}catch(TemplateException t) {
			t.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateRootFile(Page rootPage, String appName) {

		try {
			Map<String, Object> map = new HashMap<>();
			Template template = m_cfg.getTemplate("page.ftl");
	
			map.put("pageId", "root");
			map.put("body", generateBodyPage(rootPage));

			Writer file = new FileWriter (new File(m_outputFolder + "/www/" + appName + ".html"));
			template.process(map, file);
	
		}catch(TemplateException t) {
			t.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateDataFile(HashMap<String, Entity> entities, HashMap<String, Instance> instances) {
		
		Map <String, Object> map = new HashMap<>();	
		
		try {
			Template template;
			
			LinkedList<String> objects= new LinkedList<String>();
			for(Entity ent: entities.values()) {
			
				LinkedList<String> data = new LinkedList<String>();
				for (String instName : ent.getInstances().keySet()) {
					template = m_cfg.getTemplate("createData.ftl");
					map.put("nameInstance", instName);
					map.put("properties", ent.getInstances().get(instName).getValues());
					Writer writer = new StringWriter();
					template.process(map, writer);
					data.add(writer.toString());
				}
			
				map.put("entity", ent.getName());
				map.put("properties", ent.getProperties().keySet());
				map.put("data", data);
				template = m_cfg.getTemplate("createStores.ftl");
				Writer writer = new StringWriter();
				template.process(map, writer);
				objects.add(writer.toString());
			}
			
			m_javaScript.put("objects", objects);
			
		}catch(TemplateException t) {
			t.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void generatePages(HashMap <String, Page> inputPages, HashMap <String, Entity> inputEntities) {
		
		String functions = "";
		//TODO: generar paginas 'normales'
		
		//Generate CRUD
		for(Entity ent: inputEntities.values()){
			if (ent.getCrud()) {
				generateView(ent);
				//generateCreate(e);
				//generateManage(e);
				//generateEdit(e);
				try {
				HashMap <String, Object> map = new HashMap<String, Object>();
				Template template = m_cfg.getTemplate("jsCrud.ftl");
				map.put("entity", Character.toUpperCase(ent.getName().charAt(0)) + ent.getName().substring(1));
				Writer writer = new StringWriter();
				template.process(map, writer);
				functions = functions + writer;
				}catch(TemplateException t) {
					t.printStackTrace();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		m_javaScript.put("functions", functions);
	}
	
	public void generateView(Entity ent) {
		Map <String, Object> map = new HashMap<>();
		
		try {
			Template template = m_cfg.getTemplate("viewEntity.ftl");
			map.put("entityName", ent.getName());
			map.put("properties", ent.getProperties().keySet());
			Writer writer = new FileWriter (new File(m_outputFolder + "/www/" + ent.getName().toLowerCase() + ".html"));
			template.process(map, writer);
			
		}catch(TemplateException t) {
			t.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateJavaScript() {
		try {
			Template template = m_cfg.getTemplate("jsIndex.ftl");
			Writer writer = new FileWriter(new File(m_outputFolder + "/www/js/index.js"));
			template.process(m_javaScript, writer);
		}catch(TemplateException t) {
			t.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String generateBodyPage(Page page) {
		
		String body = "";
		
		try {
			Map<String, Object> map = new HashMap<>();
			Template template;
			for(PageElement e: page.getElements()) {
				
				switch(e.getType()) {
					case NAVIGATE:
						template = m_cfg.getTemplate("navigate.ftl");
						map.put("page", (PageNavigate)e);
						map.put("arguments", ((PageNavigate)e).getArguments());
						break;
					case OUTPUT:
						template = m_cfg.getTemplate("output.ftl");
						map.put("outputElement", generateOutString((PageOutput)e));
						break;
					case FORM:
						template = m_cfg.getTemplate("navigate.ftl");
						break;
					default:

						template = m_cfg.getTemplate("navigate.ftl");
						break;
				}

				Writer templateResult = new StringWriter();
				template.process(map, templateResult); 
				body = body + templateResult.toString();
				map.clear();
			
			}
		}catch (TemplateException t) {
			t.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
	private String generateOutString(PageOutput o) {
		
		String outString = "";
		
		for (String s : o.getOutputElements().keySet()) {
			if (o.getOutputElements().get(s)) {
				outString = outString + s;
			}
			else {
				outString = outString + '"' + s + '"'; 
			}
		}
		return outString;
	}
	
	
}
