package tfm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

public class ParseInputFolder {

	//WebDSL files name
	private final String INI_FILE = "application.ini";
	private final String DATA_FILE = "data.app";
	private final String INIT_FILE = "initialization.app";

	//Parse data file
	private final Integer REGEX_DATA_INICIO_ENTIDAD =  0;
	private final Integer REGEX_DATA_PROPERTY       =  1;
	private final Integer REGEX_DATA_FIN_ENTIDAD    =  2;
	private final Integer REGEX_CRUD                =  3;
	private final Integer REGEX_ROOT_PAGE           =  4;
	private final Integer REGEX_PAGE_NAVIGATE       =  5;
	private final Integer REGEX_PAGE_OUTPUT         =  6;
	private final Integer REGEX_OUT_LINE            =  7;
	private final Integer REGEX_END_PAGE            =  8;
	private final Integer REGEX_INIT_BEGIN          =  9;
	private final Integer REGEX_INIT_VAR_BEGIN      = 10;
	private final Integer REGEX_INIT_ASSIGN         = 11;
	private final Integer REGEX_INIT_VAR_END        = 12;

	private HashMap<Integer, Pattern> m_parsePatterns;
	private Entity m_currentEntity;
	private Page m_currentPage;
	private Instance m_currentInstance;
	private HashMap<String, Entity> m_entidades;
	private HashMap<String, Page> m_pages;
	private HashMap<String, Instance> m_instances;
	private String m_inputFolder;
	private String m_appName;

	public ParseInputFolder(String inputFolderName) 
	{
		m_inputFolder = inputFolderName;
		m_entidades = new HashMap<String, Entity>();
		m_pages = new HashMap<String, Page>();
		m_instances = new HashMap<String, Instance>();

		initialisePatterns();
	}

	private void initialisePatterns() {

		m_parsePatterns = new HashMap<Integer, Pattern>();

		m_parsePatterns.put(REGEX_DATA_INICIO_ENTIDAD, 
				Pattern.compile("^\\s*entity\\s*(\\w+)\\s*[{]"));

		m_parsePatterns.put(REGEX_DATA_PROPERTY, 
				Pattern.compile("^\\s*(\\w+)\\s*(->|::){1}\\s*(\\w*)\\s*(<(\\w+)>\\s)?(\\(\\s*inverse\\s*=\\s*(\\w+)\\.(\\w+)\\s*\\)\\s*)?"));

		m_parsePatterns.put(REGEX_DATA_FIN_ENTIDAD, 
				Pattern.compile("^.*}\\s*"));

		m_parsePatterns.put(REGEX_CRUD, 
				Pattern.compile("^\\s*(?i)derive\\s*(?i)crud\\s*(\\w*)\\s*$"));

		m_parsePatterns.put(REGEX_ROOT_PAGE, 
				Pattern.compile("^\\s*(define)?\\s*page\\s*root\\s*\\(\\s*\\)\\s*[{]\\s*"));

		m_parsePatterns.put(REGEX_PAGE_NAVIGATE,
				Pattern.compile("\\s*navigate\\s*(\\w*)\\s*\\((.*)\\)\\s*\\{\\s*(\"|')(.*)(\"|')\\}\\s*"));

		m_parsePatterns.put(REGEX_PAGE_OUTPUT,
				Pattern.compile("\\s*output\\s*\\(\\s*([\\w\\.]*)\\s*\\)\\s*"));

		m_parsePatterns.put(REGEX_OUT_LINE,
				Pattern.compile("^\\s*\"(.*)\"\\s*$"));

		m_parsePatterns.put(REGEX_END_PAGE, 
				Pattern.compile("^\\s*[}]\\s*$"));

		m_parsePatterns.put(REGEX_INIT_BEGIN,
				Pattern.compile("\\S*init\\s*\\s*[{]\\s*"));

		m_parsePatterns.put(REGEX_INIT_VAR_BEGIN,
				Pattern.compile("^\\s*var\\s*(\\w*)\\s*:=\\s*(\\w*)\\s*[{]\\s*$"));

		m_parsePatterns.put(REGEX_INIT_ASSIGN,
				Pattern.compile("^\\s*(\\w*)\\s*:=\\s*(.*)\\s*"));
		
		m_parsePatterns.put(REGEX_INIT_VAR_END,
				Pattern.compile("\\s*[}]\\s*;\\s*$"));
	}

	void parseIniFile() {		

		try {	

			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/" + INI_FILE));
			String line = br.readLine();
			boolean appNameFound = false;
			String configAppName = "appname";
			while (line != null && !appNameFound) {
				if (line.contains(configAppName)){
					appNameFound = true;
					m_appName = line.substring(configAppName.length()+1);
				}
				line = br.readLine();	
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	void parseDataFile() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + '/' + DATA_FILE));
			String line = br.readLine();

			boolean entityFound = false;
			while (line != null) {
				if (entityFound){
					parseProperty(line);
					entityFound = parseEndEntity(line);
				}else {
					entityFound = parseEntity(line);
				}
				line = br.readLine();	
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	void parsePageFile() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/" + m_appName + ".app"));
			String line = br.readLine();

			boolean pageFound = false;
			//boolean formFound = false;

			while (line != null) {

				if(pageFound) {
					parseElement(line);
					pageFound = parseEndPage(line);

					if(!pageFound) {
						m_pages.put(m_currentPage.getName(), m_currentPage);
						
						//m_currentPage = null;
					}

				}else {
					parseCrud(line);
					pageFound = parseInitPage(line);
				}

				line = br.readLine();	
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	void parseInitializationFile() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/" + INIT_FILE));
			String line = br.readLine();
			boolean initFound = false;
			boolean varFound = false;

			while (line != null) {		
				if (initFound) {

					if (varFound) {
						parseAssign(line);
						varFound = parseEndVar(line);
					}
					else {
						varFound = parseVar(line);
					}
					
					initFound = parseEndPage(line);

				}else {
					initFound = parseInit(line);
				}

				line = br.readLine();	
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	void processInverse() {
	
		for (Entity i : m_entidades.values()) {
			for (Property p : i.getProperties().values()) {
				String eInverse = p.getEinverse();
				if (eInverse != null) {
					for (Instance ins : i.getInstances().values()) {
						if (ins.hasValue(p.getName())) {
							for(Instance invIns : m_entidades.get(eInverse).getInstances().values()) {
								if (invIns.getName().equals(ins.getValue(p))){
									invIns.addValue(
											((m_entidades.get(eInverse)).getProperties()).get(p.getPinverse()), ins.getName());
								}
							}
						}else {
							for (Instance invIns : m_entidades.get(eInverse).getInstances().values()) {
								if (invIns.hasValue(p.getPinverse())){
									ins.addValue(p, invIns.getName());
								}
							}
						}
					}
				}
			}
		}	
	}
	
	
	private void parseProperty(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_DATA_PROPERTY).matcher(line);

		if(matcher.find()) {
			Property p = createProperty(matcher);
			m_currentEntity.addProperty(p);
		}
	}

	private boolean parseEntity(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_DATA_INICIO_ENTIDAD).matcher(line);

		if (matcher.find()) {
			Entity e = new Entity(matcher.group(1));
			m_currentEntity = e;
			return true;
		}

		return false;
	}

	private boolean parseEndEntity(String line) {
		Matcher matcher = m_parsePatterns.get(REGEX_DATA_FIN_ENTIDAD).matcher(line);

		if (matcher.find()) {
			m_entidades.put(m_currentEntity.getName(), m_currentEntity);
			m_currentEntity = null;
			return false;
		}
		return true;
	}
	
	public HashMap<String, Page> getPages(){
		return this.m_pages;
	}
	
	private void parseCrud(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_CRUD).matcher(line);

		if(matcher.find()) {
			String crudPage = matcher.group(1);
			m_entidades.get(crudPage).setCrud(true);		

			/*
			m_pages.put("create" + crudPage, null); //create
			m_pages.put("manage" + crudPage, null); //manage
			m_pages.put("view" + crudPage, null); //edit
			m_pages.put(crudPage.toLowerCase(), null); //view
			*/
		}

	}

	private boolean parseInitPage(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_ROOT_PAGE).matcher(line);
		if (matcher.find()) {
			Page p = new Page(m_appName);
			m_currentPage = p;
			return true;
		}
		return false;
	}

	private void parseElement(String line) {

		//Parse navigate
		Matcher matcher = m_parsePatterns.get(REGEX_PAGE_NAVIGATE).matcher(line);
		if (matcher.find())
		{
			PageNavigate n = new PageNavigate();
			n.setPage(matcher.group(1));
			//Split the arguments
			if (matcher.group(2) !=null) {
				String[] arguments = matcher.group(2).split("\\s*,\\s*");	
				for (int i = 0; i<arguments.length; i++)
					addArgumentToNavigate(arguments[i], n);
			}
			n.setText(matcher.group(4));

			m_currentPage.addElement(n);
		}

		//Parse output
		matcher = m_parsePatterns.get(REGEX_PAGE_OUTPUT).matcher(line);
		if(matcher.find()) {
			PageOutput o = new PageOutput();

			String[] outputs = matcher.group(1).split("\"");
			for (int i = 0; i< outputs.length; i++) {
				matcher = m_parsePatterns.get(REGEX_PAGE_OUTPUT).matcher(outputs[i]);
				if (matcher.find()) {
					o.addOutputElement(matcher.group(1), false);
				}
				else {
					o.addOutputElement(outputs[i], true);
				}
			}

			m_currentPage.addElement(o);
		}

		//Parse out line
		matcher = m_parsePatterns.get(REGEX_OUT_LINE).matcher(line);
		if(matcher.find()) {
			PageOutput o = new PageOutput();

			String[] outputs = matcher.group(1).split("\"");
			for (int i = 0; i< outputs.length; i++) {
				o.addOutputElement(matcher.group(1), true);
			}

			m_currentPage.addElement(o);
		}
	}

	public boolean parseEndPage(String line) {
		//Parse endPage
		Matcher matcher = m_parsePatterns.get(REGEX_END_PAGE).matcher(line);
		if (matcher.find()) {
			return false;
		}else {
			return true;
		}
	}

	public boolean parseInit(String line) {
		
		Matcher matcher = m_parsePatterns.get(REGEX_INIT_BEGIN).matcher(line);
		if(matcher.find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean parseVar(String line) {
		Matcher matcher = m_parsePatterns.get(REGEX_INIT_VAR_BEGIN).matcher(line);
		if(matcher.find()) {
			Instance i = new Instance(matcher.group(1), m_entidades.get(matcher.group(2)));
			m_currentInstance = i;
			return true;
		}
		return false;
	}

	public void parseAssign(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_INIT_ASSIGN).matcher(line);
		if(matcher.find()) {
			Property p = m_currentInstance.getEntity().getProperties().get(matcher.group(1));
			m_currentInstance.addValue(p, matcher.group(2));	
		}
	}
	
	private void addArgumentToNavigate(String argument, PageNavigate p) {
		
		String arg = argument;
		for (String entityName : m_entidades.keySet()) {
			if(argument.contains(entityName) && argument.equals(arg)) {
				for(Instance i : m_instances.values()) {	
					if((i.getEntity().getName()).equals(entityName)) {
						arg = i.getName();
					}
				}
			}
		}
		p.addArgument(arg);
	}
	
	private boolean parseEndVar(String line) {
		Matcher matcher = m_parsePatterns.get(REGEX_INIT_VAR_END).matcher(line);
		if (matcher.find()) {
			m_instances.put(m_currentInstance.getName(), m_currentInstance);
			m_entidades.get(m_currentInstance.getEntity().getName()).addInstance(m_currentInstance);
			m_currentInstance = null;
			return false;
		}
		return true;
	}

	private Property createProperty(Matcher matcher) {

		Property p = new Property();
		p.setName(matcher.group(1)); 

		if(matcher.group(5)!=null) 
		{
			p.setIsSetOrList(true);
			p.setType(matcher.group(5));
		}
		else {
			p.setType(matcher.group(3));
		}
		p.setEinverse(matcher.group(7));
		p.setPinverse(matcher.group(8));

		return p;
	}

	public String getAppName()
	{
		return m_appName;
	}
	
	public HashMap<String, Instance> getInstances(){
		return this.m_instances;
	}
	
	public HashMap<String, Entity> getEntities() {
		return this.m_entidades;
	}

	public void print() {
		System.out.println("Nombre de la aplicacion: " + m_appName);

		for (String key : m_entidades.keySet()) {
			System.out.println("Entity: " + key);
			for (Property p : m_entidades.get(key).getProperties().values()) {
				System.out.println("\t" + p.getType() + " : " + p.getName());
			}
		}

		for (String key : m_pages.keySet()) {
			System.out.println("Page: " + key);
		}
		
		for (String key : m_instances.keySet()) {
			System.out.println("Instance " + m_instances.get(key).getName() + " de " + m_instances.get(key).getEntity().getName());
		}
	}
}
