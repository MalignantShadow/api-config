package info.malignantshadow.api.config;

import java.util.List;
import java.util.Map;

public class Configs {
	
	public static boolean testKey(String key) {
		return key != null;
	}
	
	public static void checkKey(String key) {
		if (!testKey(key))
			throw new IllegalArgumentException("key cannot be null");
	}
	
	public static boolean testValue(Object value) {
		return value == null || value instanceof String || value instanceof Boolean || value instanceof Number ||
			value instanceof ConfigSection || value instanceof ConfigSequence;
	}
	
	public static void checkValue(Object value) {
		if (!testValue(value))
			throw new IllegalArgumentException("Illegal value type. Must be null or an instance of String, Number, Boolean, ConfigSection or ConfigSequence");
	}
	
	public static boolean testPath(String... path) {
		return path != null && path.length > 0;
	}
	
	public static void checkPath(String... path) {
		if (!testPath(path))
			throw new IllegalArgumentException("path must have a length of 1 or more");
	}
	
	public static Object getValue(Object object) {
		if (Configs.testValue(object))
			return object;
		else if (object instanceof List)
			return ConfigSequence.fromList((List<?>) object);
		else if (object instanceof Map)
			return ConfigSection.fromMap((Map<?, ?>) object);
		
		return null;
	}
	
}
