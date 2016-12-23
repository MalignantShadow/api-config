package info.malignantshadow.api.config;

import java.io.InputStream;

public interface ConfigProcessor {
	
	public ConfigSection getDocument(InputStream stream);
	
}
