package database;

import java.io.Serializable;

public class LinkCategory implements Serializable {
	// Constants
	// ----------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Properties
	// ---------------------------------------------------------------------------------

	private Integer id;
	private Integer typeId;

	// Getters/setters
	// ----------------------------------------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
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
		return this.typeId.toString();
	}
	
	// Custom methods
	public boolean isValid() {
		return this.id != null && this.typeId != null;
	}
}
