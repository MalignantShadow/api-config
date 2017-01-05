package info.malignantshadow.api.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import info.malignantshadow.api.util.AttachableData;
import info.malignantshadow.api.util.ListUtil;

public class ConfigSection extends AttachableData implements Iterable<ConfigPairing> {
	
	private List<ConfigPairing> _pairs;
	
	public ConfigSection() {
		_pairs = new ArrayList<ConfigPairing>();
	}
	
	public void clear() {
		_pairs.clear();
	}
	
	public int size() {
		return _pairs.size();
	}
	
	public void add(ConfigPairing pair) {
		if (pair == null)
			throw new IllegalArgumentException("pair cannot be null");
		
		int index = ListUtil.indexOf(_pairs, p -> p.getKey().equals(pair.getKey()));
		if (index == -1)
			_pairs.add(pair);
		else
			_pairs.set(index, pair);
	}
	
	public ConfigSection set(Object value, String key, String... path) {
		get(true, key, path).set(value);
		return this;
	}
	
	public boolean isSet(String key) {
		return ListUtil.contains(_pairs, p -> p != null && p.getKey().equals(key));
	}
	
	public boolean isSet(String key, String... path) {
		return get(key, path) == null;
	}
	
	public ConfigSection setIfMissing(Object value, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null) {
			pair = createPairing(key, path);
			pair.set(value);
		}
		return this;
	}
	
	public ConfigPairing get(String key, String... path) {
		return get(false, key, path);
	}
	
	public ConfigPairing get(boolean create, String key, String... path) {
		Configs.checkKey(key);
		for (ConfigPairing p : _pairs) {
			if (!p.getKey().equals(key))
				continue;
			
			if (path == null || path.length == 0)
				return p;
			
			String newKey = path[0];
			String[] newPath = new String[path.length - 1];
			if (newPath.length > 0)
				System.arraycopy(path, 1, newPath, 0, newPath.length);
			
			// The pair has the given key, but a further path is given and it isn't a section
			if (!p.isSection() && create)
				p.set(new ConfigSection());
			
			return p.asSection().get(create, newKey, newPath);
			
		}
		
		if (create)
			return createPairing(key, path);
		
		return null;
	}
	
	public Number getNumber(String key, String... path) {
		return getNumber(0, key, path);
	}
	
	public Number getNumber(Number def, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null)
			return def;
		
		if (pair.isNumber())
			return pair.asNumber();
		
		return def;
	}
	
	public String getString(String key, String... path) {
		return getStringWithDefault("", key, path);
	}
	
	public String getStringWithDefault(String def, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null)
			return def;
		
		if (pair.isString())
			return pair.asString();
		
		return def;
	}
	
	public boolean getBoolean(String key, String... path) {
		return getBoolean(false, key, path);
	}
	
	public boolean getBoolean(boolean def, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null)
			return def;
		
		if (pair.isBoolean())
			return pair.asBoolean();
		
		return def;
	}
	
	public ConfigSection getSection(String key, String... path) {
		return getSection(new ConfigSection(), key, path);
	}
	
	public ConfigSection getSection(ConfigSection def, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null)
			return def;
		
		if (pair.isSection())
			return pair.asSection();
		
		return def;
	}
	
	public ConfigSequence getSequence(String key, String... path) {
		return getSequence(new ConfigSequence(), key, path);
	}
	
	public ConfigSequence getSequence(ConfigSequence def, String key, String... path) {
		ConfigPairing pair = get(key, path);
		if (pair == null)
			return def;
		
		if (pair.isSequence())
			return pair.asSequence();
		
		return def;
	}
	
	//called only if they key doesn't exist, so it is safe to add a new one with that key
	private ConfigPairing createPairing(String key, String... path) {
		ConfigPairing pair = new ConfigPairing(key, null);
		_pairs.add(pair);
		if (path.length == 0)
			return pair;
		
		String newKey = path[0];
		String[] newPath = new String[path.length - 1];
		if (newPath.length > 0)
			System.arraycopy(path, 1, newPath, 0, newPath.length);
		
		pair.set(new ConfigSection());
		return pair.asSection().createPairing(newKey, newPath);
	}
	
	public static ConfigSection fromMap(Map<?, ?> map) {
		ConfigSection section = new ConfigSection();
		section.setAll(map);
		return section;
	}
	
	public ConfigSection setAll(Map<?, ?> map) {
		for (Map.Entry<?, ?> e : map.entrySet()) {
			Object k = e.getKey();
			String key = null;
			if (k instanceof String)
				key = (String) k;
			else if (k != null)
				key = k.toString();
			
			if (key == null)
				continue;
			
			Object obj = Configs.getValue(e.getValue());
			if (obj == null)
				continue;
			
			set(obj, key);
		}
		return this;
	}
	
	public <T> T parseAs(Function<ConfigSection, T> parser) {
		if (parser == null)
			return null;
		
		return parser.apply(this);
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>(_pairs.size());
		for (ConfigPairing p : _pairs) {
			if (p.isSection())
				map.put(p.getKey(), p.asSection().toMap());
			else if (p.isSequence())
				map.put(p.getKey(), p.asSequence().toList());
			else
				map.put(p.getKey(), p.get());
		}
		return map;
	}
	
	@Override
	public Iterator<ConfigPairing> iterator() {
		return _pairs.iterator();
	}
	
}
