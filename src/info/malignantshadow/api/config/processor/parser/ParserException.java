package info.malignantshadow.api.config.processor.parser;

public class ParserException extends RuntimeException {
	
	private static final long serialVersionUID = -1858657099307068952L;
	
	private Parser _parser;
	
	public ParserException(String message, Parser parser) {
		super(message);
		_parser = parser;
	}
	
	public Parser getParser() {
		return _parser;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage() + String.format(" at character %d (%d:%d)", _parser.index(), _parser.line(), _parser.lineIndex());
	}
	
}
