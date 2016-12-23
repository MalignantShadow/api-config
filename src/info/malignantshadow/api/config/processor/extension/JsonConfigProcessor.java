package info.malignantshadow.api.config.processor.extension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import info.malignantshadow.api.config.ConfigPairing;
import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.ConfigSequence;
import info.malignantshadow.api.config.processor.TextFileConfigProcessor;
import info.malignantshadow.api.config.processor.parser.Parser;
import info.malignantshadow.api.config.processor.parser.ParserException;

public class JsonConfigProcessor extends TextFileConfigProcessor {
	
	public static final int DEF_INDENT_SIZE = 4;
	
	@Override
	public ConfigSection getDocument(String source) {
		Parser parser = new Parser(source);
		char c = parser.current();
		if (c != '{')
			expected(parser, c, "{");
		return getObject(parser);
	}
	
	private ConfigSection getObject(Parser parser) {
		ConfigSection object = new ConfigSection();
		parser.skipWhiteSpace();
		char c = parser.current();
		
		while (true) {
			if (c == '}') {
				parser.next();
				return object;
			}
			
			if (c != '\'' && c != '"')
				expected(parser, c, "beginning quote character");
			
			parser.next();
			String key = parser.readString(c, false);
			c = parser.nextClean();
			if (c != ':')
				expected(parser, c, ":");
			
			parser.skipWhiteSpace();
			Object value = getValue(parser);
			object.set(value, key);
			c = parser.nextClean();
			if (c == 0)
				unexpectedEof(parser);
			
			if (c == ',') {
				parser.skipWhiteSpace();
				c = parser.current();
				continue;
			} else if (c == '}') {
				parser.next();
				return object;
			}
			
			expected(parser, c, ",' or '}");
		}
	}
	
	private ConfigSequence getArray(Parser parser) {
		ConfigSequence array = new ConfigSequence();
		parser.skipWhiteSpace();
		char c = parser.current();
		
		while (true) {
			if (c == ']') {
				parser.next();
				return array;
			}
			
			Object value = getValue(parser);
			array.add(value);
			c = parser.nextClean();
			if (c == 0)
				unexpectedEof(parser);
			
			if (c == ',') {
				parser.skipWhiteSpace();
				c = parser.current();
				continue;
			} else if (c == ']') {
				parser.next();
				return array;
			}
			
			throw new ParserException("Unexpected character '" + c + "', expected ',' or ']'", parser);
		}
	}
	
	private Object getValue(Parser parser) {
		char c = parser.current();
		if (c == '{') {
			parser.next();
			return getObject(parser);
		} else if (c == '[') {
			parser.next();
			return getArray(parser);
		} else
			return parser.readLiteral(false);
	}
	
	@Override
	public boolean putDocument(ConfigSection section, File file) {
		return putDocument(section, file, DEF_INDENT_SIZE);
	}
	
	public boolean putDocument(ConfigSection section, File file, int indentSize) {
		return putDocument(section, file, indentSize, 0);
		
	}
	
	public boolean putDocument(ConfigSection section, File file, int indentSize, int indent) {
		if (section == null || file == null)
			return false;
		
		try {
			return putDocument(section, new FileWriter(file), indentSize, indent);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean putDocument(ConfigSection section, FileWriter writer) {
		return putDocument(section, writer, DEF_INDENT_SIZE);
	}
	
	public boolean putDocument(ConfigSection section, FileWriter writer, int indentSize) {
		return putDocument(section, writer, indentSize, 0);
	}
	
	public boolean putDocument(ConfigSection section, FileWriter writer, int indentSize, int indent) {
		if (section == null || writer == null)
			return false;
		
		try {
			writer.write(putDocument(section, indentSize, indent));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String putDocument(ConfigSection section) {
		return putDocument(section, DEF_INDENT_SIZE);
	}
	
	public String putDocument(ConfigSection section, int indentSize) {
		return putDocument(section, indentSize, 0);
	}
	
	public String putDocument(ConfigSection section, int indentSize, int indent) {
		String str = "{";
		int index = 0;
		for (ConfigPairing p : section) {
			if (indentSize > 0)
				str += "\n" + getIndentString(indentSize, indent + 1);
			str += putString(p.getKey()) + ":";
			if (indentSize > 0)
				str += " ";
			str += putValue(p.get(), indentSize, indent + 1);
			if (index < section.size() - 1)
				str += ",";
			index++;
		}
		if (indentSize > 0)
			str += "\n" + getIndentString(indentSize, indent);
		
		return str + "}";
	}
	
	public String putArray(ConfigSequence seq, int indentSize, int indent) {
		String str = "[";
		for (int i = 0; i < seq.size(); i++) {
			if (indentSize > 0)
				str += "\n" + getIndentString(indentSize, indent + 1);
			str += putValue(seq.get(i), indentSize, indent + 1);
			if (i < seq.size() - 1)
				str += ",";
		}
		if (indentSize > 0)
			str += "\n" + getIndentString(indentSize, indent);
		
		return str + "]";
	}
	
	public String putString(String string) {
		if (string == null)
			return null;
		else if (string.isEmpty())
			return "\"\"";
		
		String str = "\"";
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\n')
				str += "\\n";
			else if (c == '\r')
				str += "\\r";
			else if (c == '\t')
				str += "\\t";
			else if (c == '\b')
				str += "\\b";
			else if (c == '\\')
				str += "\\";// \
			else if (c == '\'')
				str += "\\'"; // \'
			else if (c == '"')
				str += "\\\""; // \"
			else
				str += c;
		}
		return str + "\"";
	}
	
	public String putValue(Object value, int indentSize, int indent) {
		if (value == null)
			return "null";
		
		if (value instanceof String)
			return putString((String) value);
		else if (value instanceof ConfigSection)
			return putDocument((ConfigSection) value, indentSize, indent);
		else if (value instanceof ConfigSequence)
			return putArray((ConfigSequence) value, indentSize, indent);
		else
			return value.toString();
	}
	
}
