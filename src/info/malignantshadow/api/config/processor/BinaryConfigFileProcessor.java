package info.malignantshadow.api.config.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import info.malignantshadow.api.config.ConfigSection;

public abstract class BinaryConfigFileProcessor extends FileConfigProcessor {
	
	public boolean putDocument(ConfigSection document, File file) {
		if (file == null)
			return false;
		
		try {
			return putDocument(document, new FileOutputStream(file));
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean putDocument(ConfigSection section, FileOutputStream stream) {
		return putDocument(section, (OutputStream) stream);
	}
	
	public abstract boolean putDocument(ConfigSection section, OutputStream stream);
	
}
