package com.matoski.adbm.pojo;

import com.google.gson.annotations.Expose;

/**
 * A generic model that is used to hold the state of an object, and it's name.
 * 
 * <pre>
 * new Model("name");
 * new Model("name", true);
 * </pre>
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class Model {

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Model)) {
			return false;
		}
		Model other = (Model) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/** The name. */
	@Expose
	private String name;

	/** The selected. */
	@Expose
	private boolean selected;

	/**
	 * Instantiates a new model.
	 * 
	 * @param name
	 *            the name
	 */
	public Model(String name) {
		this.name = name;
		this.selected = false;
	}

	/**
	 * Instantiates a new model.
	 * 
	 * @param name
	 *            The name of the item
	 * @param selected
	 *            Is the item in the selected
	 */
	public Model(String name, boolean selected) {
		this.name = name;
		this.selected = selected;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selected.
	 * 
	 * @param selected
	 *            the new selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
