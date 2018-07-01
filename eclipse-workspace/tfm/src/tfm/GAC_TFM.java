package tfm;

public class GAC_TFM {
	
	private static ConfigurationFile m_config;
	private static ParseInputFolder m_input;
	private static GenerateOutput m_output;

	public static void main(String[] args) {
		
		//Read the configuration file
		m_config = new ConfigurationFile();
		
		//Parse input folder
		parseInput();	
		
		//generate output
		generateOutput();
	}
	
	private static void parseInput() {
		
		m_input = new ParseInputFolder(m_config.getInputFolder());
		m_input.parseIniFile();
		m_input.parseDataFile();
		m_input.parseInitializationFile();
		m_input.parsePageFile(); //parse the root page
		m_input.processInverse();
		//m_input.print();
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void generateOutput() {
		
		m_output = new GenerateOutput(m_config.getOutputFolder());
		
		m_output.generateOutputConfig(m_input.getAppName());
		m_output.generateRootFile(m_input.getPages().get(m_input.getAppName()), m_input.getAppName());
		m_output.generateDataFile(m_input.getEntities(),m_input.getInstances());
		m_output.generatePages(m_input.getPages(), m_input.getEntities());
		
		m_output.generateJavaScript();
		
	}
		

}
