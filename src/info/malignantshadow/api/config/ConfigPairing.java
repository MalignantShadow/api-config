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
	
}
