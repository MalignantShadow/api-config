package info.malignantshadow.api.config;

public class ConfigPairing {
	
	private String _key;
	private Object _value;
	
	public ConfigPairing(String key, Object value) {
		Configs.checkKey(key);
		_key = key;
		set(value);
	}
	
	public String getKey() {
		return _key;
	}
	
	public Object get() {
		return _value;
	}
	
	public ConfigPairing get(String key, String... path) {
		if (!isSection())
			return null;
		
		return asSection().get(key, path);
	}
	
	public Object set(Object value) {
		Configs.checkValue(value);
		Object old = _value;
		_value = value;
		return old;
	}
	
	public boolean hasValue() {
		return _value != null;
	}
	
	public boolean isSection() {
		return _value instanceof ConfigSection;
	}
	
	public ConfigSection asSection() {
		return (ConfigSection) _value;
	}
	
	public boolean isSequence() {
		return _value instanceof ConfigSequence;
	}
	
	public ConfigSequence asSequence() {
		return (ConfigSequence) _value;
	}
	
	public boolean isBoolean() {
		return _value instanceof Boolean;
	}
	
	public Boolean asBoolean() {
		return (Boolean) _value;
	}
	
	public boolean isNumber() {
		return _value instanceof Number;
	}
	
	public Number asNumber() {
		return (Number) _value;
	}
	
	public byte asByte() {
		return asNumber().byteValue();
	}
	
	public short asShort() {
		return asNumber().shortValue();
	}
	
	public int asInt() {
		return asNumber().intValue();
	}
	
	public float asFloat() {
		return asNumber().floatValue();
	}
	
	public long asLong() {
		return asNumber().longValue();
	}
	
	public double asDouble() {
		return asNumber().doubleValue();
	}
	
	public boolean isString() {
		return _value instanceof String;
	}
	
	public String asString() {
		if (isString())
			return (String) _value;
		else if (_value == null)
			return "null";
		
		return _value.toString();
	}
	
	public void conserveMemory() {
		_value = conserveMemory(_value);
	}
	
	public static Object conserveMemory(Object value) {
		if (value == null)
			return null;
		;
		
		if (value instanceof String) {
			String original = (String) value;
			try {
				//coerce value to double if possible, then continue to next if statement
				return Double.parseDouble(original);
			} catch (NumberFormatException e) {
				return original;
			}
		}
		
		//don't do this for do this for floats or bytes, no work is needed if thats the case
		if (value instanceof Number && !(value instanceof Float || value instanceof Byte)) {
			Number original = (Number) value;
			double d = original.doubleValue();
			long l = original.longValue();
			
			//go down the line of primitive number types, finding
			//the value that can use the lowest possible number of bytes, use that one
			
			if (d == l) { //whole number
				int i = original.intValue();
				if (l == i) {
					short s = original.shortValue();
					if (i == s) {
						byte b = original.byteValue();
						if (b == s)
							return b;
						else
							return s;
					} else {
						return i;
					}
				} else {
					return l;
				}
			} else { //decimal number (possibly float)
				float f = original.floatValue();
				//in the rare case they are actually identical, use the float version
				//e.g. one third (1/3) is different for both (.3333333 != .333333333333), but .5 is the same
				if (d == f)
					return f;
				else
					return d;
			}
		}
		
		return value;
	}
	
}
