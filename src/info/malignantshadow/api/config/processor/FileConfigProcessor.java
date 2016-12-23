package info.malignantshadow.api.config.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import info.malignantshadow.api.config.ConfigProcessor;
import info.malignantshadow.api.config.ConfigSection;

public abstract class FileConfigProcessor implements ConfigProcessor {
	
	public ConfigSection getDocument(File file) {
		if (file == null || file.isDirectory() || !file.exists())
			return null;
		
		try {
			return getDocument(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
