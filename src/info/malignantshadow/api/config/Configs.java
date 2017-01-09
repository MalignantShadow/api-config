package info.malignantshadow.api.config;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import info.malignantshadow.api.config.processor.extension.JsonConfigProcessor;
import info.malignantshadow.api.config.processor.extension.NbtConfigProcessor;
import info.malignantshadow.api.config.processor.extension.YamlConfigProcessor;
import info.malignantshadow.api.util.arguments.Argument;

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
	
	public static <T> T getComplex(T def, ConfigPairing pair, BiFunction<T, ConfigSection, T> fromSection, BiFunction<T, ConfigSequence, T> fromSequence, Argument.Type<T> fromString) {
		if (pair == null)
			return def;
		
		if (fromSection != null && pair.isSection())
			return fromSection.apply(def, pair.asSection());
		if (fromSequence != null && pair.isSequence())
			return fromSequence.apply(def, pair.asSequence());
		if (fromString != null && pair.isString()) {
			T obj = fromString.getValue(pair.asString());
			if (obj != null)
				return obj;
		}
		
		return def;
	}
	
	public static JsonConfigProcessor json() {
		return new JsonConfigProcessor();
	}
	
	public static NbtConfigProcessor nbt() {
		return new NbtConfigProcessor();
	}
	
	public static YamlConfigProcessor yaml() {
		return new YamlConfigProcessor();
	}
	
	public static YamlConfigProcessor yaml(int indentSize, int width) {
		return new YamlConfigProcessor(indentSize, width);
	}
	
	public static YamlConfigProcessor yaml(int indentSize, int width, Boolean blockStyle) {
		return new YamlConfigProcessor(indentSize, width, blockStyle);
	}
	
}
