package database;

import java.io.Serializable;

public class ExtractedText implements Serializable {
    // Constants
    //
    // ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Properties
    //
	// ---------------------------------------------------------------------------------

    private Integer id;
    private String extractedText;

    // Getters/setters
    //
	// ----------------------------------------------------------------------------

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
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
        return this.extractedText;
    }

    // Custom methods
    public boolean isValid() {
        return this.extractedText != null;
    }
}
