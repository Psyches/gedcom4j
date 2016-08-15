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
package org.gedcom4j.validate;

import java.util.List;

import org.gedcom4j.Options;
import org.gedcom4j.model.AbstractCitation;
import org.gedcom4j.model.AbstractEvent;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.StringWithCustomTags;

/**
 * Validator for events
 * 
 * @author frizbog1
 */
class EventValidator extends AbstractValidator {

    /**
     * The event being validated
     */
    private final AbstractEvent e;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root {@link GedcomValidator} that contains the findings and the settings
     * @param e
     *            the event beign validated
     */
    public EventValidator(GedcomValidator rootValidator, AbstractEvent e) {
        super(rootValidator);
        this.e = e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    protected void validate() {
        if (e == null) {
            addError("Event is null and cannot be validated or autorepaired");
            return;
        }
        if (e.getAddress() != null) {
            new AddressValidator(getRootValidator(), e.getAddress()).validate();
        }
        checkOptionalString(e.getAge(), "age", e);
        checkOptionalString(e.getCause(), "cause", e);
        checkCitations();
        checkCustomTags(e);
        checkOptionalString(e.getDate(), "date", e);
        if (e.getDescription() != null && e.getDescription().trim().length() != 0) {
            getRootValidator().addError("Event has description, which is non-standard. Remove this value, or move it (perhaps to a Note).", e);
        }
        checkEmails();
        checkFaxNumbers();
        checkMultimedia();
        new NotesValidator(getRootValidator(), e, e.getNotes()).validate();
        checkPhoneNumbers();
        checkOptionalString(e.getReligiousAffiliation(), "religious affiliation", e);
        checkOptionalString(e.getRespAgency(), "responsible agency", e);
        checkOptionalString(e.getRestrictionNotice(), "restriction notice", e);
        checkOptionalString(e.getSubType(), "subtype", e);
        checkWwwUrls();

    }

    /**
     * Check the citations
     */
    private void checkCitations() {
        List<AbstractCitation> citations = e.getCitations();
        if (citations == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getCitations(true).clear();
                getRootValidator().addInfo("Event had null list of citations - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of citations", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<AbstractCitation>(citations).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate source citations found and removed", e);
                }
            }
            if (citations != null) {
                for (AbstractCitation c : citations) {
                    new CitationValidator(getRootValidator(), c).validate();
                }
            }
        }
    }

    /**
     * Check the emails
     */
    private void checkEmails() {
        List<StringWithCustomTags> emails = e.getEmails();
        if (emails == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getEmails(true).clear();
                getRootValidator().addInfo("Event had null list of emails - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of emails", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<StringWithCustomTags>(emails).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate emails found and removed", e);
                }
            }
            if (emails != null) {
                for (StringWithCustomTags swct : emails) {
                    checkRequiredString(swct, "email", e);
                }
            }
        }
    }

    /**
     * Check the fax numbers
     */
    private void checkFaxNumbers() {
        List<StringWithCustomTags> faxNumbers = e.getFaxNumbers();
        if (faxNumbers == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getFaxNumbers(true).clear();
                getRootValidator().addInfo("Event had null list of fax numbers - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of fax numbers", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<StringWithCustomTags>(faxNumbers).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate fax numbers found and removed", e);
                }
            }
            if (faxNumbers != null) {
                for (StringWithCustomTags swct : faxNumbers) {
                    checkRequiredString(swct, "fax number", e);
                }
            }
        }
    }

    /**
     * Check the multimedia
     */
    private void checkMultimedia() {
        List<Multimedia> multimedia = e.getMultimedia();
        if (multimedia == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getMultimedia(true).clear();
                getRootValidator().addInfo("Event had null list of multimedia - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of multimedia", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<Multimedia>(multimedia).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate multimedia found and removed", e);
                }
            }
            if (multimedia != null) {
                for (Multimedia m : multimedia) {
                    new MultimediaValidator(getRootValidator(), m).validate();
                }
            }
        }
    }

    /**
     * Check the phone numbers
     */
    private void checkPhoneNumbers() {
        List<StringWithCustomTags> phoneNumbers = e.getPhoneNumbers();
        if (phoneNumbers == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getPhoneNumbers(true).clear();
                getRootValidator().addInfo("Event had null list of phone numbers - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of phone numbers", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<StringWithCustomTags>(phoneNumbers).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate phone numbers found and removed", e);
                }
            }
            if (phoneNumbers != null) {
                for (StringWithCustomTags swct : phoneNumbers) {
                    checkRequiredString(swct, "phone number", e);
                }
            }
        }
        if (e.getPlace() != null) {
            new PlaceValidator(getRootValidator(), e.getPlace()).validate();
        }
    }

    /**
     * Check the www urls
     */
    private void checkWwwUrls() {
        List<StringWithCustomTags> wwwUrls = e.getWwwUrls();
        if (wwwUrls == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                e.getWwwUrls(true).clear();
                getRootValidator().addInfo("Event had null list of www urls - repaired", e);
            } else {
                getRootValidator().addError("Event has null list of www url", e);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<StringWithCustomTags>(wwwUrls).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate web URLs found and removed", e);
                }
            }
            if (wwwUrls != null) {
                for (StringWithCustomTags swct : wwwUrls) {
                    checkRequiredString(swct, "www url", e);
                }
            }
        }
    }
}
