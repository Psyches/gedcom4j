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

/**
 * Represents a family event. Corresponds to the FAMILY_EVENT_STRUCTURE from the GEDCOM standard along with the two
 * child elements of the wife and husband ages.
 * 
 * @author frizbog1
 * 
 */
public class FamilyEvent extends AbstractEvent {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -5964078401105991388L;

    /**
     * Age of husband at time of event
     */
    private StringWithCustomTags husbandAge;

    /**
     * The type of event. See FAMILY_EVENT_STRUCTURE in the GEDCOM standard for more info.
     */
    private FamilyEventType type;

    /**
     * Age of wife at time of event
     */
    private StringWithCustomTags wifeAge;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FamilyEvent)) {
            return false;
        }
        FamilyEvent other = (FamilyEvent) obj;
        if (husbandAge == null) {
            if (other.husbandAge != null) {
                return false;
            }
        } else if (!husbandAge.equals(other.husbandAge)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (wifeAge == null) {
            if (other.wifeAge != null) {
                return false;
            }
        } else if (!wifeAge.equals(other.wifeAge)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the husband's age.
     *
     * @return the husband's age
     */
    public StringWithCustomTags getHusbandAge() {
        return husbandAge;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public FamilyEventType getType() {
        return type;
    }

    /**
     * Gets the wife's age.
     *
     * @return the wife's age
     */
    public StringWithCustomTags getWifeAge() {
        return wifeAge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (husbandAge == null ? 0 : husbandAge.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        result = prime * result + (wifeAge == null ? 0 : wifeAge.hashCode());
        return result;
    }

    /**
     * Sets the husband's age.
     *
     * @param husbandAge
     *            the new husband's age
     */
    public void setHusbandAge(StringWithCustomTags husbandAge) {
        this.husbandAge = husbandAge;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(FamilyEventType type) {
        this.type = type;
    }

    /**
     * Sets the wife's age.
     *
     * @param wifeAge
     *            the new wife's age
     */
    public void setWifeAge(StringWithCustomTags wifeAge) {
        this.wifeAge = wifeAge;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FamilyEvent [");
        if (husbandAge != null) {
            builder.append("husbandAge=");
            builder.append(husbandAge);
            builder.append(", ");
        }
        if (type != null) {
            builder.append("type=");
            builder.append(type);
            builder.append(", ");
        }
        if (wifeAge != null) {
            builder.append("wifeAge=");
            builder.append(wifeAge);
            builder.append(", ");
        }
        if (address != null) {
            builder.append("address=");
            builder.append(address);
            builder.append(", ");
        }
        if (age != null) {
            builder.append("age=");
            builder.append(age);
            builder.append(", ");
        }
        if (cause != null) {
            builder.append("cause=");
            builder.append(cause);
            builder.append(", ");
        }
        if (citations != null) {
            builder.append("citations=");
            builder.append(citations);
            builder.append(", ");
        }
        if (date != null) {
            builder.append("date=");
            builder.append(date);
            builder.append(", ");
        }
        if (description != null) {
            builder.append("description=");
            builder.append(description);
            builder.append(", ");
        }
        if (emails != null) {
            builder.append("emails=");
            builder.append(emails);
            builder.append(", ");
        }
        if (faxNumbers != null) {
            builder.append("faxNumbers=");
            builder.append(faxNumbers);
            builder.append(", ");
        }
        if (multimedia != null) {
            builder.append("multimedia=");
            builder.append(multimedia);
            builder.append(", ");
        }
        if (getNotes() != null) {
            builder.append("notes=");
            builder.append(getNotes());
            builder.append(", ");
        }
        if (phoneNumbers != null) {
            builder.append("phoneNumbers=");
            builder.append(phoneNumbers);
            builder.append(", ");
        }
        if (place != null) {
            builder.append("place=");
            builder.append(place);
            builder.append(", ");
        }
        if (religiousAffiliation != null) {
            builder.append("religiousAffiliation=");
            builder.append(religiousAffiliation);
            builder.append(", ");
        }
        if (respAgency != null) {
            builder.append("respAgency=");
            builder.append(respAgency);
            builder.append(", ");
        }
        if (restrictionNotice != null) {
            builder.append("restrictionNotice=");
            builder.append(restrictionNotice);
            builder.append(", ");
        }
        if (subType != null) {
            builder.append("subType=");
            builder.append(subType);
            builder.append(", ");
        }
        if (wwwUrls != null) {
            builder.append("wwwUrls=");
            builder.append(wwwUrls);
            builder.append(", ");
        }
        if (yNull != null) {
            builder.append("yNull=");
            builder.append(yNull);
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
