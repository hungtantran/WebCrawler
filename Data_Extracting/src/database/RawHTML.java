package database;

import java.io.Serializable;

public class RawHTML implements Serializable {
    // Constants
    //
    // ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Properties
    //
	// ---------------------------------------------------------------------------------

    private Integer id;
    private String html;

    // Getters/setters
    //
	// ----------------------------------------------------------------------------

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    // Object overrides
    //
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
        return this.html;
    }

    // Custom methods
    public boolean isValid() {
        return this.id != null && this.html != null;
    }
}
