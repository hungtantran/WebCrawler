package database;

import java.io.Serializable;

public class LinkType implements Serializable {
	// Constants
	// ----------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Properties
	// ---------------------------------------------------------------------------------

	private Integer id;
	private String type;

	// Getters/setters
	// ----------------------------------------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// Object overrides
	// ---------------------------------------------------------------------------

	/**
	 */
	@Override
	public boolean equals(Object other) {
		// TODO implement this
		return false;
	}

	/**
	 */
	@Override
	public int hashCode() {
		// TODO implement this
		return 0;
	}

	/**
	 */
	@Override
	public String toString() {
		// TODO implement this
		return this.type;
	}
	
	// Custom methods
	public boolean isValid() {
		return this.type != null;
	}
}
