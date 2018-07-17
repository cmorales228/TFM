package tfm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ParseInputFolder {

	//WebDSL files name
	private final String INI_FILE  = "application.ini";
	private final String DATA_FILE = "data.app";
	private final String INIT_FILE = "initialization.app";
	private final String AC_FILE   = "ac.app";

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
	private final Integer REGEX_VAR_SECRET          = 13;
	private final Integer REGEX_AC_PRINCIPAL        = 14;
	private final Integer REGEX_AC_ACCESS           = 15;
	private final Integer REGEX_AC_RULE             = 16;
	private final Integer REGEX_AC_POINTCUT         = 17;
	private final Integer REGEX_AC_PAGES            = 18;
	private final Integer REGEX_AC_END_POINTCUT     = REGEX_END_PAGE;

	private HashMap<Integer, Pattern> m_parsePatterns;
	private Entity m_currentEntity;
	private Page m_currentPage;
	private Instance m_currentInstance;
	private HashMap<String, Entity> m_entidades;
	private HashMap<String, Page> m_pages;
	private LinkedHashMap<String, Instance> m_instances;
	private String m_cssContent;
	private String m_inputFolder;
	private String m_appName;
	private AccessControl m_ac;

	public ParseInputFolder(String inputFolderName) 
	{
		m_inputFolder = inputFolderName;
		m_entidades = new HashMap<String, Entity>();
		m_pages = new HashMap<String, Page>();
		m_instances = new LinkedHashMap<String, Instance>();
		m_cssContent = "";
		m_ac = null;

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
		
		m_parsePatterns.put(REGEX_VAR_SECRET,
				Pattern.compile("^([^\"]*)\"([^\"]*)\".*"));
		
		m_parsePatterns.put(REGEX_AC_PRINCIPAL, 
				Pattern.compile("^\\s*principal\\s*is\\s*(\\w*)\\s*with\\s*credentials\\s*(.*)$"));

		m_parsePatterns.put(REGEX_AC_ACCESS,
				Pattern.compile("^(\\s*)access\\s*control\\s*rules\\s*$"));
		
		m_parsePatterns.put(REGEX_AC_RULE,
				Pattern.compile("^\\s*rule\\s(pointcut|page)\\s*(\\w*)\\s*\\(\\s*\\)\\s*[{]\\s*(\\w*)\\s*.*[}]\\s*$"));
		
		m_parsePatterns.put(REGEX_AC_POINTCUT, 
				Pattern.compile("^\\s*pointcut\\s*(\\w*)\\s*\\(\\s*\\)\\s*[{]\\s*$"));

		m_parsePatterns.put(REGEX_AC_PAGES,
				Pattern.compile("^\\s*page\\s*(\\S*)\\(\\s*(\\S*)\\s*\\)\\s*,?\\s*$"));
	
	}

	/*
	 * paraIniFile
	 * Input: none
	 * Output: none
	 * Description: read configuration file for WebDSL application
	 *              to get the application name
	 */
	void parseIniFile() {		
		System.out.print("Parsing configuration file for WebDSL...");
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
		System.out.println("\tDONE");
	}
	
	/*
	 * parseDataFile
	 * input: none
	 * output: none
	 * Description: Parse WebDSL file in which entities are described.
	 * 				Create the Entity and Property objects for them. 
	 */
	void parseDataFile() {
		System.out.print("Parsing entities for WebDSL...");
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
		System.out.println("\t\t\tDONE");
	}

	/*
	 * parsePageFile
	 * input: none
	 * output: none
	 * Description Parse root page. It may contains CRUD sentences and navigate link
	 */
	void parsePageFile() {
		
		System.out.print("Parsing root page...");
		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/" + m_appName + ".app"));
			String line = br.readLine();

			boolean pageFound = false;

			while (line != null) {

				if(pageFound) {
					parseElement(line);
					pageFound = parseEndPage(line);

					if(!pageFound) {
						m_pages.put(m_currentPage.getName(), m_currentPage);
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
		System.out.println("\t\t\t\tDONE");
	}
	
	/*
	 * parseInitializationFile
	 * input: none
	 * output: none
	 * Description: parse init sentence for WebDSL, in order to read alredy
	 * 				defined entities' instances
	 */
	void parseInitializationFile() {
		
		System.out.print("Parsing already defined instances...");
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
		System.out.println("\t\tDONE");
	}

	/*
	 * parseAccessControl
	 * input: none
	 * output: none
	 * Description: parse access control file
	 */
	void parseAccessControl() {
		
		System.out.print("Parsing access control...");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/" + AC_FILE));
			String line = br.readLine();

			boolean pointcutFlag = false;
			boolean ruleFlag = false;
			String currentPointcut = "";
			Matcher matcher = null;
			
			while (line != null) {		
				
				if (ruleFlag) {
					if (pointcutFlag) {
						matcher = m_parsePatterns.get(REGEX_AC_PAGES).matcher(line);
						if (matcher.find()) {							
							m_ac.addPointcutPage(currentPointcut, matcher.group(1));
							System.out.println("Tengo una pagina " + matcher.group(1) + " en un pointcut " + currentPointcut);
						}else {
							
							matcher = m_parsePatterns.get(REGEX_AC_END_POINTCUT).matcher(line);
							if (matcher.find()) {
								pointcutFlag = false;
								System.out.println("Find de pointcut: " + currentPointcut);
							}
						}
					}
					else {
						matcher = m_parsePatterns.get(REGEX_AC_RULE).matcher(line);
						if (matcher.find()) {
							if (matcher.group(1).equals("page")) {
								m_ac.addPageRule(matcher.group(2), matcher.group(3));
								System.out.println("tengo una rule page: " + matcher.group(2));
							}
							else {
								System.out.println("tengo un pointcut rule: " + matcher.group(2));
								m_ac.addPointcutRule(matcher.group(2), matcher.group(3));
							}	
						}	
						
						matcher = m_parsePatterns.get(REGEX_AC_POINTCUT).matcher(line);
						if (matcher.find()) {
							currentPointcut = matcher.group(1);
							System.out.println("Estoy dentro del pointcut: " + currentPointcut);
							pointcutFlag = true;
						}
						
					}
				}else {  //Not inside rules yet
					if (m_ac == null) {
						matcher = m_parsePatterns.get(REGEX_AC_PRINCIPAL).matcher(line);
						if (matcher.find()) {
							m_ac = new AccessControl(matcher.group(1), matcher.group(2), m_appName);
						}
					}
		
					if (m_ac != null) {
						matcher = m_parsePatterns.get(REGEX_AC_ACCESS).matcher(line);
						if (matcher.find()) {
							ruleFlag = true;
							
						}
					}
				}

				line = br.readLine();	
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		m_ac.processPointcuts(m_pages.keySet());
		
		System.out.println("\t\tDONE");
	}

	
	/*
	 * processInverse
	 * input: none
	 * output: none
	 * Description: define inverse properties when they are not directly set
	 *              on entity definition. Set properties as non editable
	 */
	void processInverse() {
		
		for (Entity e : m_entidades.values()) {
			for (Property p : e.getProperties().values()) {
				if (p.getEinverse() != null) {
					m_entidades.get(p.getEinverse()).getProperties().get(p.getPinverse()).setEditable(false);
					m_entidades.get(p.getEinverse()).getProperties().get(p.getPinverse()).setEinverse(e.getName());
					m_entidades.get(p.getEinverse()).getProperties().get(p.getPinverse()).setPinverse(p.getName());
				}
			}
		}
	}

	/*
	 * parseProperty
	 * input: line (String) in which a property is maybe included
	 * output: none
	 * Description: use regex to check if a property is defined in the line
	 *              and add it to the current entity.
	 */
	private void parseProperty(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_DATA_PROPERTY).matcher(line);

		if(matcher.find()) {
			Property p = createProperty(matcher);
			m_currentEntity.addProperty(p);
		}
	}

	/*
	 * parseEntity
	 * input: line (String) in which a entity is maybe included
	 * output: none
	 * Description: use regex to check if a entity begin is defined in the line.
	 *              If it is, create a new entity and set it
	 *              as the current one to add properties later.
	 */
	private boolean parseEntity(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_DATA_INICIO_ENTIDAD).matcher(line);

		if (matcher.find()) {
			Entity e = new Entity(matcher.group(1));
			m_currentEntity = e;
			return true;
		}

		return false;
	}

	/*
	 * parseEndEntity
	 * input : line (String) in which the end of the current entity is maybe defined.
	 * output: Boolean indicated if the entity end is not reached yet.
	 * Description: use regex to check if the entity end is reached. 
	 *              If it is, add entity to list of entities and remove current entity content.
	 */
	private boolean parseEndEntity(String line) {
		Matcher matcher = m_parsePatterns.get(REGEX_DATA_FIN_ENTIDAD).matcher(line);

		if (matcher.find()) {
			m_entidades.put(m_currentEntity.getName(), m_currentEntity);
			m_currentEntity = null;
			return false;
		}
		return true;
	}

	
	/*
	 * parseCrud
	 * input: line (String) in which sentence CRUD is found.
	 * output: none
	 * Description: use regex to check if CRUD sentence is defined for any entity.
	 */
	private void parseCrud(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_CRUD).matcher(line);

		if(matcher.find()) {
			String crudPage = matcher.group(1);
			m_entidades.get(crudPage).setCrud(true);
			
			m_pages.put(crudPage.toLowerCase(), null);
			m_pages.put("create" + crudPage, null);
			m_pages.put("edit" + crudPage, null);
			m_pages.put("manage" + crudPage, null);
		}

	}

	/*
	 * parseInitPage
	 * input: line (String) in which the root page init is maybe defined.
	 * output: Boolean indicating if root page is found.
	 * Description: use regex expresion to check if the line contains the initilization of root page. 
	 */
	private boolean parseInitPage(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_ROOT_PAGE).matcher(line);
		if (matcher.find()) {
			Page p = new Page(m_appName);
			m_currentPage = p;
			return true;
		}
		return false;
	}

	/*
	 * parseElement
	 * input: line (String) in which an element is maybe included
	 * output: none
	 * Description: Parse different elements in root page to know their contents.
	 * 				Elements could be a navigation link or print line.
	 */
	private void parseElement(String line) {

		//Parse navigate
		Matcher matcher = m_parsePatterns.get(REGEX_PAGE_NAVIGATE).matcher(line);
		if (matcher.find())
		{
			PageNavigate n = new PageNavigate();
			n.setPage(matcher.group(1));
			//Split the arguments
			if (matcher.group(2) !=null && matcher.group(2).length() !=0) {
				String[] arguments = matcher.group(2).split("\\s*,\\s*");	
				for (int i = 0; i<arguments.length; i++) {
					addArgumentToNavigate(arguments[i], n);
				}
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

	/*
	 * parseEndPage
	 * input: line (String) in which the root page end is maybe defined.
	 * output: Boolean indicating if root page end is not found yet.
	 * Description: use regex expresion to check if the line contains the end of root page. 
	 */
	public boolean parseEndPage(String line) {
		//Parse endPage
		Matcher matcher = m_parsePatterns.get(REGEX_END_PAGE).matcher(line);
		if (matcher.find()) {
			return false;
		}else {
			return true;
		}
	}

	/*
	 * parseInit
	 * input: line (String) in which the 'init' sentence is maybe defined.
	 * output: Boolean indicating if 'init' is found.
	 * Description: use regex expresion to check if the begin of 'init' sentence. 
	 */
	public boolean parseInit(String line) {
		
		Matcher matcher = m_parsePatterns.get(REGEX_INIT_BEGIN).matcher(line);
		if(matcher.find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * parseVar
	 * input: line (String) in which and instance is maybe defined.
	 * output: Boolean indicating if instance is found.
	 * Description: use regex expresion to check if a new instance definition if found. 
	 */
	public boolean parseVar(String line) {
		Matcher matcher = m_parsePatterns.get(REGEX_INIT_VAR_BEGIN).matcher(line);
		if(matcher.find()) {
			
			Instance i = new Instance(matcher.group(1), m_entidades.get(matcher.group(2)));
			m_currentInstance = i;
			return true;
		}
		return false;
	}

	/*
	 * parseAssign
	 * input: line (String) in which an property instance is maybe defined
	 * output: none.
	 * Description: use regex expresion to check if any property of an entity is assigned to a value. 
	 */
	public void parseAssign(String line) {

		Matcher matcher = m_parsePatterns.get(REGEX_INIT_ASSIGN).matcher(line);
		if(matcher.find()) {
			Property p = m_currentInstance.getEntity().getProperties().get(matcher.group(1));
			Matcher matcherSecret = m_parsePatterns.get(REGEX_VAR_SECRET).matcher(matcher.group(2));
			
			if (p.getType().equals("Secret") && matcherSecret.find()) {
				if (matcherSecret.group(2)!=null) {
					m_currentInstance.addValue(p, matcherSecret.group(2));
				}
			}else {
				m_currentInstance.addValue(p, matcher.group(2));	
			}
		}
	}
	
	/*
	 * addArgumentToNavigate
	 * input: argument (String) containing the WebDSL argument for a navigation link
	 *        p (PageNavigate) containing the navigation link in which the argument is added
	 * output: none
	 * Description: convert the page argument from WebDSL format to an instance name 
	 */
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
	
	/*
	 * parseEndVar
	 * input: line (String) in which an instance is maybe ended
	 * output: Boolean indicating if the instance has not finished yet.
	 * Description: use regex expresion to check if an instance is already defined. 
	 *              If so, add it the instances list.
	 */
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

	/*
	 * createProperty
	 * input: matcher(Matcher) in which property characteristics are included.
	 * output: Property which has been created.
	 * Description: assign matcher values to Property characteristics. 
	 */
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

	/*
	 * parseCssFile
	 * input: none
	 * output: none
	 * Description: read css file of WebDSL and store its contents. 
	 */
	public void parseCssFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(m_inputFolder + "/stylesheets/common_.css"));
			String line = br.readLine();

			while (line != null) {		
				m_cssContent += line;
				line = br.readLine();
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}	
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

	public HashMap<String, Page> getPages(){
		return this.m_pages;
	}
	
	public String getCss() {
		return this.m_cssContent;
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
