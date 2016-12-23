package info.malignantshadow.api.config.processor.parser;

import info.malignantshadow.api.util.arguments.ArgumentTypes;

public class Parser {
	
	private String _src;
	private int _line, _lineIndex, _index;
	private boolean _eof;
	
	public Parser(String src) {
		_src = src;
		_line = 1;
		_index = _lineIndex = 0;
	}
	
	public int line() {
		return _line;
	}
	
	public int lineIndex() {
		return _lineIndex;
	}
	
	public int index() {
		return _index;
	}
	
	public char current() {
		if (_eof || _index >= _src.length()) {
			_eof = true;
			return 0;
		}
		
		return _src.charAt(_index);
	}
	
	public char next() {
		_index += 1;
		char c = current();
		if (c == '\n') {
			_line++;
			_lineIndex = 0;
		}
		return c;
	}
	
	public String next(int length) {
		String str = "";
		for (int i = 0; i < length; i++) {
			char n = next();
			if (n == 0)
				break;
			
			str += n;
		}
		return str;
	}
	
	private boolean expect(char c, char d) {
		return c == d;
	}
	
	public boolean expectCurrent(char c) {
		return expect(current(), c);
	}
	
	public boolean expectNext(char c) {
		return expect(next(), c);
	}
	
	public boolean expect(String next) {
		if (next == null)
			return _eof;
		
		String nextLength = next(next.length());
		return nextLength.equals(next);
	}
	
	private boolean isWhiteSpace(char c, boolean newline) {
		return (newline && c == '\n') || (newline && c == '\r') || c == '\t' || c == ' ';
	}
	
	public String skipWhiteSpace() {
		return skipWhiteSpace(true);
	}
	
	public String skipWhiteSpace(boolean newline) {
		String str = "";
		
		char c;
		while ((c = next()) > 0) {
			if (isWhiteSpace(c, newline))
				str += c;
			else
				break;
		}
		return str;
	}
	
	public char nextClean() {
		char c = current();
		if (!isWhiteSpace(c, true))
			return c;
		
		skipWhiteSpace();
		return current();
	}
	
	public String untilNewline() {
		return until('\n');
	}
	
	public String until(char until) {
		char c = current();
		String str = "" + c;
		while ((c = next()) > 0) {
			if (c == until)
				break;
			
			str += c;
		}
		return str;
	}
	
	public String untilWhitespace() {
		return untilWhitespace(true);
	}
	
	public String untilWhitespace(boolean newline) {
		char c = current();
		String str = "" + c;
		while ((c = next()) > 0) {
			if ((newline && c == '\n') || (newline && c == '\r') || c == ' ' || c == '\t')
				break;
			
			str += c;
		}
		return str;
	}
	
	public String readString(char quote, boolean newline) {
		char c = current();
		String str = "";
		while (true) {
			if (_eof)
				break;
			
			if (c == quote) {
				next();
				return str;
			}
			
			switch (c) {
				case 0:
					throw new ParserException("Unexpected end of file", this);
				case '\r':
					if (!newline)
						throw new ParserException("Unterminated string", this);
					str += "\r";
					break;
				case '\n':
					if (!newline)
						throw new ParserException("Unterminated string", this);
					str += "\n";
					break;
				case '\\':
					c = next();
					switch (c) {
						case 'b':
							str += '\b';
							break;
						case 't':
							str += '\t';
							break;
						case 'n':
							str += '\n';
							break;
						case 'r':
							str += '\r';
							break;
						case '\\':
						case '\'':
						case '"':
							str += c;
							break;
						default:
							throw new ParserException("Unsupported escape", this);
					}
					break;
				default:
					str += c;
					break;
			}
			c = next();
		}
		return str;
	}
	
	private boolean partOfNumber(char c) {
		return (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+' || c == 'E';
	}
	
	public Number readNumber() {
		String str = "" + current();
		char c;
		while ((c = next()) > 0) {
			if (partOfNumber(c))
				str += c;
			else
				break;
		}
		return ArgumentTypes.NUMBER.getValue(str);
	}
	
	//string, number, boolean, null
	public Object readLiteral(boolean newlineForStrings) {
		char curr = current();
		if (curr == '\'' || curr == '"') {
			next();
			return readString(curr, newlineForStrings);
		} else if (curr == 't') {
			if (!expect("rue"))
				throw new ParserException("expected 'rue' to complete boolean literal", this);
			next();
			return true;
		} else if (curr == 'f') {
			if (!expect("alse"))
				throw new ParserException("expected 'alse' to complete boolean literal", this);
			next();
			return false;
		} else if (curr == 'n') {
			if (!expect("ull"))
				throw new ParserException("expected 'ull' to complete null literal", this);
			next();
			return null;
		} else if (partOfNumber(curr)) {
			Number n = readNumber();
			if (n == null)
				throw new ParserException("Could not parser number", this);
			return n;
		}
		throw new ParserException("Unexpected character '" + curr + "'", this);
	}
	
}
