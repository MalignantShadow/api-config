package info.malignantshadow.api.config.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.processor.parser.Parser;
import info.malignantshadow.api.config.processor.parser.ParserException;

public abstract class TextFileConfigProcessor extends FileConfigProcessor {
	
	protected static void expected(Parser parser, char found, String expected) {
		throw new ParserException("Unexpected character '" + found + "', expected '" + expected + "'", parser);
	}
	
	protected static void unexpectedEof(Parser parser) {
		throw new ParserException("Unexpected EOF", parser);
	}
	
	public static String getIndentString(int indentSize, int indent) {
		String indentFull = "";
		for (int i = 0; i < indentSize; i++)
			indentFull += " ";
		
		String s = "";
		for (int i = 0; i < indent; i++)
			s += indentFull;
		
		return s;
	}
	
	public abstract ConfigSection getDocument(String source);
	
	@Override
	public ConfigSection getDocument(InputStream stream) {
		if (stream == null)
			return null;
		
		Scanner scanner = new Scanner(stream);
		scanner.useDelimiter("\\A");
		String source = scanner.next();
		scanner.close(); // 'stream' closed
		
		return getDocument(source);
	}
	
	public boolean putDocument(ConfigSection document, File file) {
		if (file == null)
			return false;
		
		try {
			return putDocument(document, new FileWriter(file));
		} catch (IOException e) {
			return false;
		}
	}
	
	public abstract boolean putDocument(ConfigSection section, FileWriter writer);
	
}
