package info.malignantshadow.api.config.processor.extension;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.processor.TextFileConfigProcessor;

public class YamlConfigProcessor extends TextFileConfigProcessor {
	
	private Yaml _yaml;
	
	private static DumperOptions createDumperOptions(int indentSize, int width, FlowStyle style) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(style);
		options.setWidth(width);
		options.setIndent(indentSize);
		options.setLineBreak(LineBreak.UNIX);
		
		return options;
	}
	
	public YamlConfigProcessor() {
		this(2, 100);
	}
	
	public YamlConfigProcessor(int indentSize, int width) {
		this(indentSize, width, true);
	}
	
	public YamlConfigProcessor(int indentSize, int width, Boolean blockStyle) {
		this(createDumperOptions(indentSize, width, blockStyle == null ? FlowStyle.AUTO : blockStyle ? FlowStyle.BLOCK : FlowStyle.FLOW));
	}
	
	public YamlConfigProcessor(DumperOptions options) {
		_yaml = new Yaml(options);
	}
	
	public Yaml getYaml() {
		return _yaml;
	}
	
	@Override
	public ConfigSection getDocument(String source) {
		return getDocument(_yaml.load(source));
	}
	
	private ConfigSection getDocument(Object o) {
		if (!(o instanceof Map))
			return null;
		
		Map<?, ?> map = (Map<?, ?>) o;
		ConfigSection config = new ConfigSection();
		config.setAll(map);
		return config;
	}
	
	@Override
	public boolean putDocument(ConfigSection document, FileWriter writer) {
		if (document == null)
			throw new IllegalArgumentException("document cannot be null");
		
		_yaml.dump(document.toMap(), writer);
		try {
			writer.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public String putDocument(ConfigSection document) {
		if (document == null)
			throw new IllegalArgumentException("document cannot be null");
		
		return _yaml.dump(document.toMap());
	}
	
}
