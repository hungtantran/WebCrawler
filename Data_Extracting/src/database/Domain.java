package database;

public class Domain {
    // Constants
    //
    // ----------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    // Properties
    //
	// ---------------------------------------------------------------------------------

    Integer m_id;
    String m_domain;

    // Getters/setters
    //
	// ----------------------------------------------------------------------------

    public Integer getId() {
        return m_id;
    }

    public void setId(Integer id) {
        this.m_id = id;
    }

    public String getDomain() {
        return m_domain;
    }

    public void setDomain(String domain) {
        this.m_domain = domain;
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
        return this.m_domain;
    }

    // Custom methods
    public boolean isValid() {
        return this.m_id != null && this.m_domain != null;
    }
}
