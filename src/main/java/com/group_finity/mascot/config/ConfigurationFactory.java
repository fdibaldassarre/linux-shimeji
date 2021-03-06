package com.group_finity.mascot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ini4j.Wini;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.group_finity.mascot.exception.ConfigurationException;

public class ConfigurationFactory {
	
	private static final String DEFAULT_SHIMEJI = "shime";
	
	private static final String [] ACTION_XML_NAME = {"actions.xml", "Actions.xml"};
	private static final String [] BEHAVIOUR_XML_NAME = {"behaviors.xml", "Behaviors.xml", "Behavior.xml", "behavior.xml"};
	
	private final Path configurationFolder;
	private final Path logFolder;
	private String shimejiName;
	private XmlLanguages shimejiLang;
	private Path shimejiFolder;
	private Path shimejiImgFolder;
	private int maxMascotCount;
	
	public ConfigurationFactory() {
		String homeFolder = System.getProperty("user.home");
		configurationFolder = Paths.get(homeFolder, ".config", "shimeji");
		logFolder = configurationFolder.resolve("log/");
	}
	
	public void init() {
		Path configFile = configurationFolder.resolve("config.ini");
		try {
			loadConfig(configFile);
		} catch(Exception e) {
			loadDefault();
			saveConfig(configFile);
		} 
	}
	
	public Path getLogFolder() {
		return logFolder;
	}
	
	private void loadConfig(Path configFile) {
		File file = configFile.toFile();
		Wini wini;
		try {
			wini = new Wini(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		shimejiName = wini.get("shimeji", "name", String.class);
		if(DEFAULT_SHIMEJI.equals(shimejiName)) {
			loadDefault();
			return;
		}
		// Language
		String shimejiLangStr =  wini.get("shimeji", "language", String.class);
		shimejiLang = XmlLanguages.valueOf(shimejiLangStr);
		shimejiLang = shimejiLang == null ? XmlLanguages.JPN : shimejiLang;
		// Other options
		Integer maxCountOption = wini.get("shimeji", "maxCount", Integer.class);
		maxMascotCount = maxCountOption != null ? maxCountOption: 999;
		// Folder
		shimejiFolder = configurationFolder.resolve(shimejiName);
		if(!Files.exists(shimejiFolder)) {
			throw new RuntimeException("Missing shimeji folder");
		}
		shimejiImgFolder = shimejiFolder.resolve("img/Shimeji");
		if(!Files.exists(shimejiImgFolder)) {
			shimejiImgFolder = shimejiFolder.resolve("img/shimeji");
			if(!Files.exists(shimejiImgFolder)) {
				shimejiImgFolder = shimejiFolder.resolve("img");
			}
		}
	}
	
	private void loadDefault() {
		shimejiName = DEFAULT_SHIMEJI;
		shimejiFolder = null;
		shimejiLang = XmlLanguages.JPN;
		shimejiImgFolder = null;
	}
	
	private void saveConfig(Path configFile) {
		try {
			File file = configFile.toFile();
			if(!file.exists()) {
				Files.createDirectories(file.getParentFile().toPath());
				file.createNewFile();
			} else {
				// Never overwrite existing files
				return ;
			}
			Wini wini = new Wini(file);
			wini.put("shimeji", "name", shimejiName);
			wini.put("shimeji", "language", shimejiLang.toString());
			
			wini.store();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public Configuration load() throws ConfigurationException {
		Configuration configuration = new LocalizedConfiguration(shimejiImgFolder, shimejiLang, maxMascotCount );
		
		loadBehaviour(configuration);
		loadActions(configuration);
		
		configuration.validate();
				
		return configuration;
	}

	private void loadActions(Configuration configuration) {
		InputStream inputStream = getActionsFile();
		load(configuration, inputStream);
	}

	private void loadBehaviour(Configuration configuration) {
		InputStream inputStream = getBehaviourFile();
		load(configuration, inputStream);
		
	}
	
	private void load(Configuration configuration, InputStream inputStream) {
		Document actions;
		try {
			actions = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		try {
			configuration.load(new Entry(actions.getDocumentElement(), configuration.getLanguage()));
		} catch (IOException | ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private InputStream getBehaviourFile() {
		if(shimejiFolder == null) {
			return ConfigurationFactory.class.getResourceAsStream("/shime/conf/Behavior.xml");
		} else {
			Path behaviourFile = null;
			for(String name: BEHAVIOUR_XML_NAME) {
				behaviourFile = shimejiFolder.resolve(String.format("conf/%s", name));
				if(Files.exists(behaviourFile)) {
					break;
				}
			}
			try {
				return new FileInputStream(behaviourFile.toAbsolutePath().toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private InputStream getActionsFile() {
		if(shimejiFolder == null) {
			return ConfigurationFactory.class.getResourceAsStream("/shime/conf/Actions.xml");
		} else {
			Path actionFile = null;
			for(String name: ACTION_XML_NAME) {
				actionFile = shimejiFolder.resolve(String.format("conf/%s", name));
				if(Files.exists(actionFile)) {
					break;
				}
			}
			try {
				return new FileInputStream(actionFile.toAbsolutePath().toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
