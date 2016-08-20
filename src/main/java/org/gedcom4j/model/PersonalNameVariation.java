/*
 * Copyright (c) 2009-2016 Matthew R. Harrah
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.gedcom4j.model;

import java.util.ArrayList;
import java.util.List;

import org.gedcom4j.Options;

/**
 * A variation on a personal name - either a romanized version or a phonetic version. Introduced with GEDCOM 5.5.1.
 * 
 * @author frizbog
 */
public class PersonalNameVariation extends AbstractNameVariation implements HasNotes, HasCitations {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1262477720634081355L;

    /**
     * The citations for this object
     */
    private List<AbstractCitation> citations = getCitations(Options.isCollectionInitializationEnabled());

    /**
     * The given (aka "Christian" or "first") names
     */
    private StringWithCustomTags givenName;

    /**
     * Nickname
     */
    private StringWithCustomTags nickname;

    /**
     * Notes about this object
     */
    private List<Note> notes = getNotes(Options.isCollectionInitializationEnabled());

    /**
     * The prefix for the name
     */
    private StringWithCustomTags prefix;

    /**
     * The suffix
     */
    private StringWithCustomTags suffix;

    /**
     * The surname (aka "family" or "last" name)
     */
    private StringWithCustomTags surname;

    /**
     * Surname prefix
     */
    private StringWithCustomTags surnamePrefix;

    /**
     * Determine if this object is equal to another
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     *            the other object we are comparing this one to
     * @return true if and only if the other object is equal to this one
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PersonalNameVariation other = (PersonalNameVariation) obj;
        if (citations == null) {
            if (other.citations != null) {
                return false;
            }
        } else if (!citations.equals(other.citations)) {
            return false;
        }
        if (givenName == null) {
            if (other.givenName != null) {
                return false;
            }
        } else if (!givenName.equals(other.givenName)) {
            return false;
        }
        if (nickname == null) {
            if (other.nickname != null) {
                return false;
            }
        } else if (!nickname.equals(other.nickname)) {
            return false;
        }
        if (notes == null) {
            if (other.notes != null) {
                return false;
            }
        } else if (!notes.equals(other.notes)) {
            return false;
        }
        if (prefix == null) {
            if (other.prefix != null) {
                return false;
            }
        } else if (!prefix.equals(other.prefix)) {
            return false;
        }
        if (suffix == null) {
            if (other.suffix != null) {
                return false;
            }
        } else if (!suffix.equals(other.suffix)) {
            return false;
        }
        if (surname == null) {
            if (other.surname != null) {
                return false;
            }
        } else if (!surname.equals(other.surname)) {
            return false;
        }
        if (surnamePrefix == null) {
            if (other.surnamePrefix != null) {
                return false;
            }
        } else if (!surnamePrefix.equals(other.surnamePrefix)) {
            return false;
        }
        if (variation == null) {
            if (other.variation != null) {
                return false;
            }
        } else if (!variation.equals(other.variation)) {
            return false;
        }
        if (variationType == null) {
            if (other.variationType != null) {
                return false;
            }
        } else if (!variationType.equals(other.variationType)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the citations.
     *
     * @return the citations
     */
    public List<AbstractCitation> getCitations() {
        return citations;
    }

    /**
     * Get the citations
     * 
     * @param initializeIfNeeded
     *            initialize the collection if needed?
     * 
     * @return the citations
     */
    public List<AbstractCitation> getCitations(boolean initializeIfNeeded) {
        if (initializeIfNeeded && citations == null) {
            citations = new ArrayList<AbstractCitation>(0);
        }
        return citations;
    }

    /**
     * Gets the given name.
     *
     * @return the given name
     */
    public StringWithCustomTags getGivenName() {
        return givenName;
    }

    /**
     * Gets the nickname.
     *
     * @return the nickname
     */
    public StringWithCustomTags getNickname() {
        return nickname;
    }

    /**
     * Gets the notes.
     *
     * @return the notes
     */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Get the notes
     * 
     * @param initializeIfNeeded
     *            initialize the collection if needed?
     * 
     * @return the notes
     */
    public List<Note> getNotes(boolean initializeIfNeeded) {
        if (initializeIfNeeded && notes == null) {
            notes = new ArrayList<Note>(0);
        }
        return notes;
    }

	/**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public StringWithCustomTags getPrefix() {
        return prefix;
    }

    /**
     * Gets the suffix.
     *
     * @return the suffix
     */
    public StringWithCustomTags getSuffix() {
        return suffix;
    }

    /**
     * Gets the surname.
     *
     * @return the surname
     */
    public StringWithCustomTags getSurname() {
        return surname;
    }

    /**
     * Gets the surname prefix.
     *
     * @return the surname prefix
     */
    public StringWithCustomTags getSurnamePrefix() {
        return surnamePrefix;
    }

    /**
     * Calculate a hashcode for this object
     * 
     * @see java.lang.Object#hashCode()
     * @return a hashcode for this object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (citations == null ? 0 : citations.hashCode());
        result = prime * result + (givenName == null ? 0 : givenName.hashCode());
        result = prime * result + (nickname == null ? 0 : nickname.hashCode());
        result = prime * result + (notes == null ? 0 : notes.hashCode());
        result = prime * result + (prefix == null ? 0 : prefix.hashCode());
        result = prime * result + (suffix == null ? 0 : suffix.hashCode());
        result = prime * result + (surname == null ? 0 : surname.hashCode());
        result = prime * result + (surnamePrefix == null ? 0 : surnamePrefix.hashCode());
        result = prime * result + (variation == null ? 0 : variation.hashCode());
        result = prime * result + (variationType == null ? 0 : variationType.hashCode());
        return result;
    }

    /**
     * Sets the given name.
     *
     * @param givenName
     *            the new given name
     */
    public void setGivenName(StringWithCustomTags givenName) {
        this.givenName = givenName;
    }

    /**
     * Sets the nickname.
     *
     * @param nickname
     *            the new nickname
     */
    public void setNickname(StringWithCustomTags nickname) {
        this.nickname = nickname;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix
     *            the new prefix
     */
    public void setPrefix(StringWithCustomTags prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the suffix.
     *
     * @param suffix
     *            the new suffix
     */
    public void setSuffix(StringWithCustomTags suffix) {
        this.suffix = suffix;
    }

    /**
     * Sets the surname.
     *
     * @param surname
     *            the new surname
     */
    public void setSurname(StringWithCustomTags surname) {
        this.surname = surname;
    }

    /**
     * Sets the surname prefix.
     *
     * @param surnamePrefix
     *            the new surname prefix
     */
    public void setSurnamePrefix(StringWithCustomTags surnamePrefix) {
        this.surnamePrefix = surnamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);
        builder.append("PersonalNameVariation [");
        if (citations != null) {
            builder.append("citations=");
            builder.append(citations);
            builder.append(", ");
        }
        if (givenName != null) {
            builder.append("givenName=");
            builder.append(givenName);
            builder.append(", ");
        }
        if (nickname != null) {
            builder.append("nickname=");
            builder.append(nickname);
            builder.append(", ");
        }
        if (notes != null) {
            builder.append("notes=");
            builder.append(notes);
            builder.append(", ");
        }
        if (prefix != null) {
            builder.append("prefix=");
            builder.append(prefix);
            builder.append(", ");
        }
        if (suffix != null) {
            builder.append("suffix=");
            builder.append(suffix);
            builder.append(", ");
        }
        if (surname != null) {
            builder.append("surname=");
            builder.append(surname);
            builder.append(", ");
        }
        if (surnamePrefix != null) {
            builder.append("surnamePrefix=");
            builder.append(surnamePrefix);
            builder.append(", ");
        }
        if (variation != null) {
            builder.append("variation=");
            builder.append(variation);
            builder.append(", ");
        }
        if (variationType != null) {
            builder.append("variationType=");
            builder.append(variationType);
            builder.append(", ");
        }
        if (getCustomTags() != null) {
            builder.append("customTags=");
            builder.append(getCustomTags());
        }
        builder.append("]");
        return builder.toString();
    }

}
