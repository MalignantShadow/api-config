package info.malignantshadow.api.config;

public class ConfigProcessorException extends RuntimeException {
	
	private static final long serialVersionUID = -6314320042158379731L;
	
	private ConfigProcessor _processor;
	
	public ConfigProcessorException(String message, ConfigProcessor processor) {
		super(message);
		_processor = processor;
	}
	
	public ConfigProcessor getProcessor() {
		return _processor;
	}
	
}
