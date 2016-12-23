package info.malignantshadow.api.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import info.malignantshadow.api.util.AttachableData;

public class ConfigSequence extends AttachableData implements Iterable<Object> {
	
	private List<Object> _seq;
	
	public ConfigSequence() {
		_seq = new ArrayList<Object>();
	}
	
	public void add(int index, Object value) {
		Configs.checkValue(value);
		_seq.add(index, value);
	}
	
	public void add(Object value) {
		Configs.checkValue(value);
		_seq.add(value);
	}
	
	public Object get(int index) {
		if (index < 0 || index >= _seq.size())
			return null;
		
		return _seq.get(index);
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
	public void forEach(Consumer<Object> consumer) {
		_seq.forEach(consumer);
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
}
