package com.group_finity.mascot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ini4j.Wini;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.group_finity.mascot.exception.ConfigurationException;

import java.nio.file.Files;

public class ConfigurationFactory {
	
	private static final String DEFAULT_SHIMEJI = "shime";
	
	private final Path configurationFolder;
	private final Path logFolder;
	private String shimejiName;
	private XmlLanguages shimejiLang;
	private Path shimejiFolder;
	private Path shimejiImgFolder;
	
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
		String shimejiLangStr =  wini.get("shimeji", "language", String.class);
		shimejiLang = XmlLanguages.valueOf(shimejiLangStr);
		shimejiLang = shimejiLang == null ? XmlLanguages.JPN : shimejiLang;
		shimejiFolder = configurationFolder.resolve(shimejiName);
		if(!Files.exists(shimejiFolder)) {
			throw new RuntimeException("Missing shimeji folder");
		}
		shimejiImgFolder = shimejiFolder.resolve("img");
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
		Configuration configuration = new LocalizedConfiguration(shimejiImgFolder, shimejiLang);
		
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
			Path behaviourFile = shimejiFolder.resolve("conf/behaviors.xml");
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
			Path behaviourFile = shimejiFolder.resolve("conf/actions.xml");
			try {
				return new FileInputStream(behaviourFile.toAbsolutePath().toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
