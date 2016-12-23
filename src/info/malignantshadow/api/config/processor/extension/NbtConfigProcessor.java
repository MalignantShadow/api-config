package info.malignantshadow.api.config.processor.extension;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import info.malignantshadow.api.config.ConfigPairing;
import info.malignantshadow.api.config.ConfigProcessorException;
import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.ConfigSequence;
import info.malignantshadow.api.config.processor.BinaryConfigFileProcessor;
import info.malignantshadow.api.config.processor.TextFileConfigProcessor;

public class NbtConfigProcessor extends BinaryConfigFileProcessor {
	
	public static final byte TAG_END = 0;
	public static final byte TAG_BYTE = 1;
	public static final byte TAG_SHORT = 2;
	public static final byte TAG_INT = 3;
	public static final byte TAG_LONG = 4;
	public static final byte TAG_FLOAT = 5;
	public static final byte TAG_DOUBLE = 6;
	public static final byte TAG_BYTE_ARRAY = 7;
	public static final byte TAG_STRING = 8;
	public static final byte TAG_LIST = 9;
	public static final byte TAG_COMPOUND = 10;
	public static final byte TAG_INT_ARRAY = 11;
	
	public static final String CHARSET = "UTF-8";
	public static final int INDENT_SIZE = 2;
	
	public static final String D_LIST_TYPE = "NBT.TagListType";
	public static final String N_TAG_BYTE = "TAG_Byte";
	public static final String N_TAG_SHORT = "TAG_Short";
	public static final String N_TAG_INT = "TAG_Int";
	public static final String N_TAG_LONG = "TAG_Long";
	public static final String N_TAG_FLOAT = "TAG_Float";
	public static final String N_TAG_DOUBLE = "TAG_Double";
	public static final String N_TAG_BYTE_ARRAY = "TAG_ByteArray";
	public static final String N_TAG_STRING = "TAG_String";
	public static final String N_TAG_LIST = "TAG_List";
	public static final String N_TAG_COMPOUND = "TAG_Compound";
	public static final String N_TAG_INT_ARRAY = "TAG_Int_Array";
	public static final String N_TAG_UNKNOWN = "UnknownTag";
	
	public static String formatNamedTag(Object tagType, String name) {
		return getTagTypeName(tagType) + (name != null ? "('" + name + "')" : "");
	}
	
	public String getIndentString(int indent) {
		return TextFileConfigProcessor.getIndentString(INDENT_SIZE, indent);
	}
	
	public static byte getListType(ConfigSequence seq) {
		Object data = seq.getData(D_LIST_TYPE);
		
		//if this sequence was read from a file/stream (and not created on the fly) then this will be a byte
		//representing its type
		//it will be either TAG_LIST, TAG_BYTE_ARRAY, or TAG_INT_ARRAY
		if (data != null && data instanceof Byte)
			return (Byte) data;
		else {
			//otherwise, determine it's type by class
			Class<?> clazz = seq.getType();
			if (clazz == Byte.class)
				return TAG_BYTE_ARRAY;
			else if (clazz == Integer.class)
				return TAG_INT_ARRAY;
			else
				return TAG_LIST;
			
		}
	}
	
	public static byte getTypeOf(Object value) {
		if (value == null)
			return -1;
		
		if (value instanceof Byte)
			return TAG_BYTE;
		if (value instanceof Short)
			return TAG_SHORT;
		if (value instanceof Integer)
			return TAG_INT;
		if (value instanceof Long)
			return TAG_LONG;
		if (value instanceof Float)
			return TAG_FLOAT;
		if (value instanceof Double)
			return TAG_DOUBLE;
		if (value instanceof String)
			return TAG_STRING;
		
		//TAG_LIST, TAG_BYTE_ARRAY, TAG_INT_ARRAY
		if (value instanceof ConfigSequence)
			return getListType((ConfigSequence) value);
		if (value instanceof ConfigSection)
			return TAG_COMPOUND;
		
		return -1;
	}
	
	public static String getTagTypeName(Object value) {
		byte type = getTypeOf(value);
		
		if (type == TAG_BYTE)
			return N_TAG_BYTE;
		if (type == TAG_SHORT)
			return N_TAG_SHORT;
		if (type == TAG_INT)
			return N_TAG_INT;
		if (type == TAG_LONG)
			return N_TAG_LONG;
		if (type == TAG_FLOAT)
			return N_TAG_FLOAT;
		if (type == TAG_DOUBLE)
			return N_TAG_DOUBLE;
		if (type == TAG_BYTE_ARRAY)
			return N_TAG_BYTE_ARRAY;
		if (type == TAG_STRING)
			return N_TAG_STRING;
		if (type == TAG_LIST)
			return N_TAG_LIST;
		if (type == TAG_COMPOUND)
			return N_TAG_COMPOUND;
		if (type == TAG_INT_ARRAY)
			return N_TAG_INT_ARRAY;
		
		return N_TAG_UNKNOWN;
	}
	
	@Override
	public ConfigSection getDocument(File file) {
		return getDocument(file, false);
	}
	
	public ConfigSection getDocument(File file, boolean compressed) {
		if (file == null || !file.exists())
			return null;
		
		if (!compressed)
			return super.getDocument(file);
		
		try {
			GZIPInputStream stream = new GZIPInputStream(new FileInputStream(file));
			return getDocument(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public ConfigSection getDocument(InputStream stream) {
		if (stream == null)
			return null;
		
		try {
			DataInputStream data = (stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream));
			byte b = data.readByte();
			if (b != TAG_COMPOUND)
				throw new ConfigProcessorException("NBT expects that the first tag is TAG_Compound", this);
			
			// Note: the root section isn't actually emitted, it's required to retain the TAG_Compound's name
			// Likewise, when emitting, the given section must have at least one ConfigPairing; only the first pair where .isSection()
			// is true will be emitted.
			ConfigSection root = new ConfigSection();
			root.add(readNamedTag(TAG_COMPOUND, data));
			data.close();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String readString(DataInputStream data) throws IOException {
		short length = data.readShort();
		byte[] bytes = new byte[length];
		data.readFully(bytes);
		
		return new String(bytes, CHARSET);
	}
	
	//start after '10' is read, we know to read a compound
	//the name is already read
	private ConfigSection readCompound(DataInputStream data) throws IOException {
		ConfigSection section = new ConfigSection();
		ConfigPairing p;
		while ((p = readNamedTag(data)) != null)
			section.add(p);
		return section;
	}
	
	//start after '9' is read, we know to read a list
	//the name is already read
	private ConfigSequence readList(DataInputStream data) throws IOException {
		return readList(TAG_LIST, data.readByte(), data);
	}
	
	private ConfigSequence readList(byte listType, byte type, DataInputStream data) throws IOException {
		ConfigSequence seq = new ConfigSequence();
		seq.setData(D_LIST_TYPE, listType);
		int length = data.readInt();
		for (int i = 0; i < length; i++)
			seq.add(readPayload(type, data));
		
		return seq;
	}
	
	private ConfigPairing readNamedTag(DataInputStream data) throws IOException {
		return readNamedTag(data.readByte(), data);
	}
	
	private ConfigPairing readNamedTag(byte type, DataInputStream data) throws IOException {
		if (type == TAG_END)
			return null;
		
		String name = readString(data);
		Object value = readPayload(type, data);
		return new ConfigPairing(name, value);
	}
	
	private Object readPayload(byte type, DataInputStream data) throws IOException {
		if (type == TAG_END)
			throw new ConfigProcessorException("Unexpected TAG_End", this);
		else if (type == TAG_BYTE)
			return data.readByte();
		else if (type == TAG_SHORT)
			return data.readShort();
		else if (type == TAG_INT)
			return data.readInt();
		else if (type == TAG_LONG)
			return data.readLong();
		else if (type == TAG_FLOAT)
			return data.readFloat();
		else if (type == TAG_DOUBLE)
			return data.readDouble();
		else if (type == TAG_BYTE_ARRAY)
			return readList(type, TAG_BYTE, data);
		else if (type == TAG_STRING)
			return readString(data);
		else if (type == TAG_LIST)
			return readList(data);
		else if (type == TAG_COMPOUND)
			return readCompound(data);
		else if (type == TAG_INT_ARRAY)
			return readList(type, TAG_INT, data);
		else
			throw new ConfigProcessorException("Unknown TagType '" + type + "'", this);
	}
	
	@Override
	public boolean putDocument(ConfigSection section, OutputStream stream) {
		if (section == null || stream == null)
			return false;
		
		DataOutputStream data = (stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream));
		//TODO: write NBT
		
		return true;
	}
	
	public String putDocument(ConfigSection section) {
		return putDocument("", section, 0);
	}
	
	public String putDocument(String name, ConfigSection section, int indent) {
		if (section == null)
			return null;
		
		int entries = section.size();
		String indentStr = getIndentString(indent);
		String str = indentStr + formatNamedTag(section, name) + ": " + entries + " entr" + (entries == 1 ? "y" : "ies");
		str += "\n" + indentStr + "{";
		for (ConfigPairing p : section)
			str += "\n" + putPair(p, indent + 1);
		
		return str + "\n" + indentStr + "}";
	}
	
	public String putPair(ConfigPairing pair, int indent) {
		if (pair == null)
			return null;
		
		if (pair.isSection())
			return putDocument(pair.getKey(), pair.asSection(), indent);
		if (pair.isSequence())
			return putList(pair.getKey(), pair.asSequence(), indent);
		
		return putTag(indent, pair.getKey(), pair.get());
	}
	
	public String putTag(int indent, String name, Object o) {
		return getIndentString(indent) + formatNamedTag(o, name) + ": " + (o instanceof String ? "'" + o + "'" : o);
	}
	
	public String putList(String name, ConfigSequence seq, int indent) {
		if (seq == null)
			return null;
		
		int entries = seq.size();
		String indentStr = getIndentString(indent);
		String str = indentStr + formatNamedTag(seq, name) + ": " + entries + " entr" + (entries == 1 ? "y" : "ies");
		str += "\n" + indentStr + "{";
		for (Object o : seq) {
			str += "\n";
			if (o instanceof ConfigSection)
				str += putDocument("", ((ConfigSection) o), indent + 1);
			else if (o instanceof ConfigSequence)
				str += putList("", ((ConfigSequence) o), indent + 1);
			else
				str += putTag(indent, null, o);
		}
		
		return str + "\n" + indentStr + "}";
	}
	
}
