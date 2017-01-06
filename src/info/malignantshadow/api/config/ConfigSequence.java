package info.malignantshadow.api.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.malignantshadow.api.util.AttachableData;

public class ConfigSequence extends AttachableData implements Iterable<Object> {
	
	private List<Object> _seq;
	private boolean _needsConservation;
	
	public ConfigSequence() {
		super();
		_seq = new ArrayList<Object>();
		_needsConservation = true;
	}
	
	public ConfigSequence add(int index, Object value) {
		Configs.checkValue(value);
		_seq.add(index, value);
		_needsConservation = true;
		return this;
	}
	
	public ConfigSequence add(Object value) {
		Configs.checkValue(value);
		_seq.add(value);
		_needsConservation = true;
		return this;
	}
	
	public Object get(int index) {
		if (index < 0 || index >= _seq.size())
			return null;
		
		return _seq.get(index);
	}
	
	public Number getNumber(int index) {
		return getNumber(0, index);
	}
	
	public Number getNumber(Number def, int index) {
		Object o = get(index);
		if (o == null)
			return def;
		
		if (o instanceof Number)
			return (Number) o;
		
		return def;
	}
	
	public String getString(int index) {
		return getString("", index);
	}
	
	public String getString(String def, int index) {
		Object o = get(index);
		if (o == null)
			return def;
		
		if (o instanceof String || o instanceof Number || o instanceof Boolean)
			return o.toString();
		
		return def;
	}
	
	public boolean getBoolean(int index) {
		return getBoolean(false, index);
	}
	
	public boolean getBoolean(boolean def, int index) {
		Object o = get(index);
		if (o == null)
			return def;
		
		if (o instanceof Boolean)
			return (Boolean) o;
		
		return def;
	}
	
	public ConfigSection getSection(int index) {
		return getSection(new ConfigSection(), index);
	}
	
	public ConfigSection getSection(ConfigSection def, int index) {
		Object o = get(index);
		if (o == null)
			return def;
		
		if (o instanceof ConfigSection)
			return (ConfigSection) o;
		
		return def;
	}
	
	public ConfigSequence getSequence(int index) {
		return getSequence(new ConfigSequence(), index);
	}
	
	public ConfigSequence getSequence(ConfigSequence def, int index) {
		Object o = get(index);
		if (o == null)
			return def;
		
		if (o instanceof ConfigSequence)
			return (ConfigSequence) o;
		
		return def;
	}
	
	public int size() {
		return _seq.size();
	}
	
	public Object set(int index, Object value) {
		Configs.checkValue(value);
		return _seq.set(index, value);
	}
	
	public Object remove(int index) {
		return _seq.remove(index);
	}
	
	public boolean remove(Object value) {
		return _seq.remove(value);
	}
	
	@Override
	public Iterator<Object> iterator() {
		return _seq.iterator();
	}
	
	public void addAll(List<?> list) {
		if (list == null || list.isEmpty())
			return;
		
		for (Object o : list) {
			Object object = Configs.getValue(o);
			if (object != null)
				add(object);
		}
	}
	
	public static ConfigSequence fromList(List<?> list) {
		ConfigSequence seq = new ConfigSequence();
		seq.addAll(list);
		return seq;
	}
	
	public boolean isTyped() {
		return getType() != null;
	}
	
	public Class<?> getType() {
		Class<?> clazz = null;
		for (Object o : _seq) {
			if (o == null)
				continue;
			
			Class<?> c = null;
			if (o instanceof ConfigSection)
				c = ConfigSection.class;
			else if (o instanceof ConfigSequence)
				c = ConfigSequence.class;
			else if (o instanceof Boolean)
				c = Boolean.class;
			else if (o instanceof Number)
				c = Number.class;
			else if (o instanceof String)
				c = String.class;
			
			if (clazz == null) {
				clazz = c;
				continue;
			}
			
			if (clazz != c)
				return null;
		}
		
		return clazz;
	}
	
	public List<Object> toList() {
		List<Object> list = new ArrayList<Object>(_seq.size());
		for (Object o : _seq) {
			if (o instanceof ConfigSection)
				list.add(((ConfigSection) o).toMap());
			else if (o instanceof ConfigSequence)
				list.add(((ConfigSequence) o).toList());
			else
				list.add(o);
		}
		
		return list;
	}
	
	public ConfigSequence conserveMemory() {
		if (!_needsConservation)
			return this;
		
		List<Object> newSeq = new ArrayList<Object>();
		for (Object o : _seq) {
			if (o == null)
				continue;
			
			if (o instanceof ConfigSection)
				newSeq.add(((ConfigSection) o).conserveMemory());
			else if (o instanceof ConfigSequence)
				newSeq.add(((ConfigSequence) o).conserveMemory());
			else
				newSeq.add(ConfigPairing.conserveMemory(o));
		}
		_seq = newSeq;
		_needsConservation = false;
		return this;
	}
}
