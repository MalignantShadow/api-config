package info.malignantshadow.api.config;

public class TypedConfigSequence extends ConfigSequence {
	
	private Class<?> _type;
	
	public TypedConfigSequence(Class<?> type) {
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");
		
		if (type != String.class &&
			!Number.class.isAssignableFrom(type) &&
			type != Boolean.class &&
			!ConfigSection.class.isAssignableFrom(type) &&
			!ConfigSequence.class.isAssignableFrom(type))
			throw new IllegalArgumentException(
				"The given type must be a config-writable type: Number (or subclass), String, Boolean, ConfigSection (or subclass), ConfigSequence (or subclass)");
		_type = type;
	}
	
	public Class<?> getType() {
		return _type;
	}
	
	public boolean canAdd(Object o) {
		return o == null || _type.isAssignableFrom(o.getClass());
	}
	
	@Override
	public TypedConfigSequence add(int index, Object value) {
		if (!canAdd(value))
			throw new IllegalArgumentException("Non-allowed type: " + value.getClass().getSimpleName());
		
		return (TypedConfigSequence) super.add(index, value);
	}
}
