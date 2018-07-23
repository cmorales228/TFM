package tfm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class GenerateOutput {

	private Configuration m_cfg;
	private String m_outputFolder;
	private ParseInputFolder m_inputData;
	private String m_appName;
	private String m_authentication;
	private LinkedHashMap<String, String> m_eventListener;
	private LinkedHashMap<String, String> m_jsFunctions;
	private LinkedHashMap<String, String> m_jsEVFunctions;
	
	public GenerateOutput(String outputFolder, ParseInputFolder inputData) {
		
		m_outputFolder = outputFolder;
		m_inputData = inputData;
		m_appName = inputData.getAppName();
		m_eventListener = new LinkedHashMap<>();
		m_authentication = null;
		m_jsFunctions= new LinkedHashMap<>();
		m_jsEVFunctions= new LinkedHashMap<>();
		
		try {
			
			m_cfg = new Configuration (Configuration.VERSION_2_3_23);
			m_cfg.setDirectoryForTemplateLoading(new File("templates"));
			m_cfg.setDefaultEncoding("UTF-8");
			m_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * generateOuptutConfig
	 * input: none
	 * output: none
	 * Description: generate configuration file for Apache Cordova 
	 */
	public void generateOutputConfig() {
		
		System.out.print("\nGenerating configuration for Apache Cordova...");
		
		Map<String, Object> map = new HashMap<>();
		map.put("appName", m_appName);

		processTemplateToFile("config.ftl", map, m_outputFolder + "/config.xml");
		System.out.println("\tDONE");
	}
	
	/*
	 * generateRootFile
	 * input: none
	 * output: none
	 * Description: generate root page for Apache Cordova with input data
	 */
	public void generateRootFile() {
		
		System.out.print("Generating root page...");

		Page rootPage = m_inputData.getPages().get(m_appName);
			
		Map<String, Object> map = new HashMap<>();
	
		map.put("pageId", "root");
		map.put("body", generateBodyPage(rootPage));
		map.put("appName", m_appName);
		
		processTemplateToFile("page.ftl", map, m_outputFolder + "/www/" + m_appName + ".html");
		
		System.out.println("\t\t\t\tDONE");
	}
	

	/*
	 * generatePages
	 * input: none
	 * output: none
	 * Description: generate CRUD pages for each defined entity 
	 */
	public void generatePages() {
			
		System.out.print("Generating crud pages...");
		//Generate CRUD
		for(Entity ent: m_inputData.getEntities().values()){
			if (ent.getCrud()) {
				
				//Map <String, Object> map = new HashMap<>();
				Map <String, Object> map = generateEntityMap(ent);
				
				//Generate View
				m_jsFunctions.put("showTable"+ent.getName(), processTemplateToString("jsShowTable.ftl", map));
				processTemplateToFile("viewEntity.ftl", map, m_outputFolder + "/www/" + ent.getName().toLowerCase() + ".html");	
			
				//Generate Create
				m_jsFunctions.put("showForm"+ent.getName(), processTemplateToString("jsShowForm.ftl", map));
				m_eventListener.put("save"+ent.getName(), "create"+ent.getName());
				m_jsEVFunctions.put("create"+ent.getName(), processTemplateToString("jsCreateEntity.ftl", map));
				processTemplateToFile("createEntity.ftl", map, m_outputFolder + "/www/create" + ent.getName() + ".html");
				
				//GenerateEdit
				m_eventListener.put("update"+ent.getName(), "update"+ent.getName());
				m_jsEVFunctions.put("update"+ent.getName(), processTemplateToString("jsEditEntity.ftl", map));
				processTemplateToFile("editEntity.ftl", map, m_outputFolder + "/www/edit" + ent.getName() + ".html");
			
				//GenerateManage
				m_jsFunctions.put("showManage"+ent.getName(), processTemplateToString("jsShowManage.ftl", map));
				m_jsEVFunctions.put("deleteInstance"+ent.getName(), processTemplateToString("jsDeleteInstance.ftl", map));
				processTemplateToFile("manageEntity.ftl", map, m_outputFolder + "/www/manage" + ent.getName() + ".html");
			}
		}
		System.out.println("\t\t\tDONE");
	}
	
	/*
	 * generateJavaScript
	 * input: none
	 * output: none
	 * Description: generates almost all javascript functions needed. 
	 *              It is separated in two files: index.js and functions.js 
	 */
	public void generateJavaScript() {
		
		System.out.print("Generating JavaScript files...");
		
		HashMap <String, Object> map = new HashMap<>();
		
		map.put("appName", m_appName);
		map.put("eventListener", m_eventListener);
		map.put("evFunctions", m_jsEVFunctions);
		map.put("functions", m_jsFunctions.keySet());
		map.put("authentication", m_authentication);
		
		//Set allLinkedEntities for crud entities.
		HashMap<String, LinkedList<String>> allLinkedEntities = new HashMap<>();
		for (Entity ent: m_inputData.getEntities().values()) {
			if (ent.getCrud()) {
				allLinkedEntities.put(ent.getName(), new LinkedList<>());
				for (Property p: ent.getProperties().values()) {
					if(m_inputData.getEntities().keySet().contains(p.getType())){
						allLinkedEntities.get(ent.getName()).add(p.getType());
					}
				}
			}
		}
		map.put("crudEntities", allLinkedEntities);	
		
		//Set editable linked entities.
		HashMap<String, LinkedList<String>> editableLinkedEntities = new HashMap<>();
		for (Entity ent: m_inputData.getEntities().values()) {
			for(Property p: ent.getProperties().values()) {
				if (p.getEditable() ==true && m_inputData.getEntities().keySet().contains(p.getType())) {
					if (!editableLinkedEntities.containsKey(ent.getName())) {
						editableLinkedEntities.put(ent.getName(), new LinkedList<>());
					}
					editableLinkedEntities.get(ent.getName()).add(p.getType());
				}	
			}
		}	
		map.put("editableLinkedEntities", editableLinkedEntities);
		
		//Generate stores for indexedDB and already defined instances
		LinkedList<String> objects= new LinkedList<String>();
		for(Entity ent: m_inputData.getEntities().values()) {	
			objects.add(generateStores(ent));
		}		
		map.put("objects", objects);
		
		//Set access control rules
		if (m_inputData.getAC()!=null) {
			map.put("rules", m_inputData.getAC().getPageRules());
		}
		
		generateJSfunctions();
		processTemplateToFile("jsIndex.ftl", map, m_outputFolder + "/www/js/index.js");
		
		System.out.println("\t\t\tDONE");
	}
	
	/*
	 * generateJSfunctions
	 * input: none
	 * output: none
	 * Description: generate functions for Javascript which does not depend on indexedDB
	 */
	private void generateJSfunctions() {
	
		HashMap <String, Object> map = new HashMap<>();
		map.put("functions", m_jsFunctions);
		
		//Add function argument for JavaScript 
		HashMap<String, LinkedList<String>> argFunctions = new HashMap<>(); 
		LinkedList<String> argShowTable = new LinkedList<>();
		argShowTable.add("value");
		argShowTable.add("instancesValue");
		LinkedList<String> argShowForm = new LinkedList<>();
		argShowForm.add("values");
		argShowForm.add("instancesMap");
		argShowForm.add("defaultValue");
		LinkedList<String> argShowManage = new LinkedList<>();
		argShowManage.add("values");
		
		for (String entityName: m_inputData.getEntities().keySet()) {
			argFunctions.put("showTable"+entityName, argShowTable);
			argFunctions.put("showForm"+entityName, argShowForm);
			argFunctions.put("showManage"+entityName, argShowManage);
		}
		
		map.put("argFunctions", argFunctions);
		processTemplateToFile("jsFunction.ftl", map, m_outputFolder + "/www/js/functions.js");
	}
	
	/*
	 * 
	 */
	private String generateStores(Entity ent) {
		
		LinkedList<String> data = new LinkedList<String>();
		Map<String, Object> map = new HashMap<>();
		
		for (String instName : ent.getInstances().keySet()) {
			map.put("nameInstance", instName);
			map.put("properties", ent.getInstances().get(instName).getValues());
			
			//Add secretProperties --> <PropertyName, IsSecret>
			HashMap<String, Boolean> secretProperties = new HashMap<>();
			for (Property p : ent.getProperties().values()) {
				secretProperties.put(p.getName(), p.getType().equals("Secret"));
			}
			map.put("secretProperties", secretProperties);
			
			data.add(processTemplateToString("createData.ftl", map));
		}
		
		map.put("entity", ent.getName());
		map.put("data", data);
		
		return processTemplateToString("createStores.ftl", map);
	}
	
	/*
	 * generateCssFile
	 * input: none
	 * output: none
	 * Description: generate CSS file from input data 
	 */
	public void generateCssFile() {
		
		System.out.print("Generate Css File...");
		FileWriter cssFile = null;
		PrintWriter pw = null;
		try {
			cssFile = new FileWriter(m_outputFolder + "/www/css/index.css");
		    pw = new PrintWriter(cssFile);
		    pw.print(m_inputData.getCss());
			cssFile.close();
		 } catch (Exception e) {
		    e.printStackTrace();
		 } 
		System.out.println("\t\t\t\tDONE");
		
	}
	
	/*
	 * generateEntityMap
	 * input: ent (Entity) for which CRUD pages will be created
	 * output: Map<String, String> which contains all data for the output CRUD templates
	 * Description: add all needed data to map from input data 
	 */
	private Map<String, Object> generateEntityMap(Entity ent) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("entityName", ent.getName());
		map.put("appName", m_appName);
		
		//Add properties --> <PropertyName, IsList>
		//Add secretProperties --> <PropertyName, IsSecret>
		//Add inverseNonEditable --> <Inverse PropertyName, isEditable>
		LinkedHashMap<String, Boolean> properties = new LinkedHashMap<>();
		LinkedHashMap<String, Boolean> secretProperties = new LinkedHashMap<>();
		LinkedHashMap<String, String> inverseEditable = new LinkedHashMap<>();
		LinkedHashMap<String, String> inverseNonEditable = new LinkedHashMap<>();
		for (Property p : ent.getProperties().values()) {
			
			if(p.getEditable()) {
				properties.put(p.getName(), p.isSetOrList());
				secretProperties.put(p.getName(), p.getType().equals("Secret"));
				if (p.getEinverse()!=null && !m_inputData.getEntities().get(p.getEinverse()).getProperties().get(p.getPinverse()).isSetOrList()){
					inverseEditable.put(p.getEinverse(), p.getPinverse());
				}
			}else if(p.getEinverse()!=null){
				inverseNonEditable.put(p.getEinverse(), p.getPinverse());
			}
		}
		
		map.put("properties", properties);
		map.put("secretProperties", secretProperties);
		map.put("inverseEditable", inverseEditable);
		map.put("inverseNonEditable", inverseNonEditable);
		
		LinkedHashMap<String, String> linkEntitiesOrNull = new LinkedHashMap<>();
		LinkedHashMap<String, String> linkEntities = new LinkedHashMap<>();
		for (Property p : ent.getProperties().values()) {
			linkEntities.put(p.getName(), p.getType());
			if (m_inputData.getEntities().keySet().contains(p.getType())) {
				linkEntitiesOrNull.put(p.getName(), p.getType());
			}
			else {
				linkEntitiesOrNull.put(p.getName(), null);
			}
		}
		map.put("linkEntitiesOrNull", linkEntitiesOrNull);
		map.put("linkEntities", linkEntities);
		
		//CRUD
		//propToDelete
		LinkedHashMap<String, Boolean> crudEntities = new LinkedHashMap<>();
		LinkedHashMap<String, String> propToDelete = new LinkedHashMap<>();
		for (Entity e : m_inputData.getEntities().values())
		{
			crudEntities.put(e.getName(), e.getCrud());
			for (Property p : e.getProperties().values()) {
				if (p.getType().equals(ent.getName())) {
					propToDelete.put(e.getName(), p.getName());
				}
			}
		}
		map.put("crudEntities", crudEntities);
		map.put("propToDelete", propToDelete);
		
		map.put("tableCreate", generateCreateTable(ent));
		
		return map;
	}
	
	/*
	 * generateAC
	 */
	public void generateAC() {

		Map <String, Object> map = new HashMap<>();
		map.put("appName", m_appName);
		LinkedHashMap<String, Boolean> credentials = new LinkedHashMap<>();
		String principal = m_inputData.getAC().getPrincipal();

		for (String cred : m_inputData.getAC().getCredentials()) {
			credentials.put(cred, m_inputData.getEntities().get(principal).getProperties().get(cred).getType().equals("Secret"));
		}

		map.put("credentials", credentials);
		m_authentication = processTemplateToString("jsAuthentication.ftl", map);
		map.put("principal", m_inputData.getAC().getPrincipal());
		
		m_jsEVFunctions.put("login", processTemplateToString("jsELlogin.ftl", map));
		m_eventListener.put("loginButton", "login");
		m_jsEVFunctions.put("logout", processTemplateToString("jsELlogout.ftl", map));
		m_eventListener.put("logoutButton", "logout");
		
		processTemplateToFile("accessDenied.ftl", map,  m_outputFolder + "/www/accessDenied.html");
	}
	
	/*
	 * generateBodyPage
	 * input: page (Page) to be converted
	 * output: String which contains the HTML body of the page
	 * Description: get page elements and convert them to HTML elements. 
	 */
	private String generateBodyPage(Page page) {
		
		String body = "";
		
		Map<String, Object> map = new HashMap<>();
		String template;
		
		for(PageElement e: page.getElements()) {
				
			switch(e.getType()) {
				case NAVIGATE:
					template = "navigate.ftl";
					map.put("page", (PageNavigate)e);
					map.put("arguments", ((PageNavigate)e).getArguments());
					break;
				case OUTPUT:
					template = "output.ftl";
					map.put("outputElement", generateOutString((PageOutput)e));
					break;
				case FORM:
					template = "navigate.ftl";
					break;
				case TEMPLATE:
					template = "authentication.ftl";
					break;
				default:
					template = "navigate.ftl";
					break;
			}
			body += processTemplateToString(template, map);
			map.clear();
		
		}

		return body;
	}
	
	/*
	 * generateCreateTable
	 * input: ent (Entity) for which the form is created
	 * output: String with the generated form for HTML
	 * Description: according to entity properties, create a form to create and edit objects. 
	 */
	private String generateCreateTable(Entity ent) {
		Map<String, Object> map = new HashMap<>();
		String table = "";

		for (Property p : ent.getProperties().values()) {
			if(p.getEditable()) {
				map.put("entityName", ent.getName());
				map.put("propName", p.getName());
			
				if (m_inputData.getEntities().keySet().contains(p.getType())) {
					map.put("isLinkEntity", true);
				}
				if (p.getType().equals("Bool")) {
					map.put("isBoolean", true);
				}else if (p.getType().equals("Secret")) {
					map.put("isSecret", true);
				}
				map.put("isSetorList",p.isSetOrList());
				table += processTemplateToString("createTable.ftl", map) + "\n";
				map.clear();
			}
		}
		return table;
	}
	
	/*
	 * generateOutString
	 * input: o (PageOutput) to be converted
	 * output: String in which the output is converted to HTML
	 * Description: get output strings from PageOutput and convert it to HTML. 
	 */
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
	
	/*
	 * processTemplateToFile
	 * input: tName (String) indicates the template Name
	 *        map (Map<String, Object>) with the data neede for the template.
	 *        file (String) indicate the file name in the the template output will be set.
	 * output: none
	 * Description: process template with map data. Set the output to a file. 
	 */
	private void processTemplateToFile(String tName, Map<String, Object> map, String file) {
		try {
			
			Template template = m_cfg.getTemplate(tName);
			Writer writer = new FileWriter (new File(file));
			template.process(map, writer);
			
		}catch(TemplateException t) {
			t.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * processTemplateToFile
	 * input: tName (String) indicates the template Name
	 *        map (Map<String, Object>) with the data neede for the template.
	 * output: String with the content of the processed template
	 * Description: process template with map data.  
	 */
	private String processTemplateToString(String tName, Map<String, Object> map) {
		Writer writer = new StringWriter();
		
		try {
			Template template = m_cfg.getTemplate(tName);
			template.process(map, writer);
			
		}catch (TemplateException t) {
			t.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}	
}
