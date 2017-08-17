package database;

import java.io.Serializable;

public class NaiveBayesParam implements Serializable {
    // Constants
    // ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Properties
    // ---------------------------------------------------------------------------------

    private Integer word;
    private Integer label;
    private Integer count;

    // Getters/setters
    // ----------------------------------------------------------------------------

    public Integer getWord() {
        return word;
    }

    public void setWord(Integer word) {
        this.word = word;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
        return this.word + " " + this.label + " " + this.count;
    }

    // Custom methods
    public boolean isValid() {
        return this.word != null && this.label != null && this.count != null;
    }
}
