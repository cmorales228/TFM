package tfm;

public class GAC_TFM {
	
	private static ConfigurationFile m_config;
	private static ParseInputFolder m_input;
	private static GenerateOutput m_output;

	//Main Class
	public static void main(String[] args) {
		
		System.out.println("\n\t\tGAC-TFM starts\n");
		//Read the configuration file
		m_config = new ConfigurationFile();
		
		//Parse input folder
		parseInput();	
		
		//generate output
		generateOutput();
		System.out.println("\n\t\tGAC-TFM ends\n");
	}
	
	private static void parseInput() {
		
		//Create parse input data
		m_input = new ParseInputFolder(m_config.getInputFolder());
		
		m_input.parseIniFile();  //parse configuration file for WebDSL
		m_input.parseDataFile();  //parse entity data
		m_input.parseInitializationFile(); //parse instances 
		m_input.parsePageFile(); //parse the root page
		m_input.parseCssFile(); // parse the css file
		m_input.parseAccessControl(); //parse access control
		m_input.processInverse();  //process the inverse properties
		//m_input.print();
		
	}
	
	private static void generateOutput() {
		
		//create generate output class
		m_output = new GenerateOutput(m_config.getOutputFolder(), m_input);
		
		m_output.generateOutputConfig(); //generate configuration for Apache Cordova
		m_output.generateRootFile(); //generate root file
		m_output.generatePages(); //generate root and CRUD pages
		m_output.generateCssFile(); //generate CSS file
	}
}
