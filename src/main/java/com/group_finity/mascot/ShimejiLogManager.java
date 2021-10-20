package com.group_finity.mascot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;

public class ShimejiLogManager {

	private final Path logFolder;

	public ShimejiLogManager(Path logFolder) {
		this.logFolder = logFolder;
	}
	
	public void init() {
		try {
			if(!Files.isDirectory(logFolder)) {
				Files.createDirectories(logFolder);
			}
			LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
		
		} catch (SecurityException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
