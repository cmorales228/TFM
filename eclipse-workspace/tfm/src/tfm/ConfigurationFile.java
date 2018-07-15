package tfm;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class ConfigurationFile {

	public final static String configFileName = "config/config.xml";
	String m_inputFolder;
	String m_outputFolder;
	String m_eclipseWorkspace;

	public ConfigurationFile() {
		try {
			File configFile = new File(configFileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			Document document = documentBuilder.parse(configFile);
			document.getDocumentElement().normalize();
			m_inputFolder = document.getElementsByTagName("inputFolder").item(0).getTextContent();
			m_outputFolder = document.getElementsByTagName("outputFolder").item(0).getTextContent();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getInputFolder() {
		return m_inputFolder;
	}

	String getOutputFolder() {
		return m_outputFolder;
	}		
}



