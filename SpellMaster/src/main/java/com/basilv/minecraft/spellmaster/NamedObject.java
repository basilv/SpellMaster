package com.basilv.minecraft.spellmaster;

/**
 * An object identified by a name.
 * @author Basil
 */
public class NamedObject {

	private String name;

	public NamedObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NamedObject)) {
			return false;
		}
		// Different subclasses are not equal to each other
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		NamedObject other = (NamedObject) obj;
		return this.name.equals(other.getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
}
