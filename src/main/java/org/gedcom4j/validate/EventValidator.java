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

import org.gedcom4j.model.AbstractCitation;
import org.gedcom4j.model.AbstractEvent;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.StringWithCustomTags;

/**
 * Validator for events
 * 
 * @author frizbog1
 */
public class EventValidator extends AbstractValidator {

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
        new NotesValidator(getRootValidator(), e).validate();
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
		List<AbstractCitation> list = validateRepairStructure("Citations", "Citations", true, e,
				new ListRef<AbstractCitation>() {
					@Override
					public List<AbstractCitation> get(boolean initializeIfNeeded) {
						return e.getCitations(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (AbstractCitation c : list) {
				new CitationValidator(getRootValidator(), c).validate();
			}
		}
    }

    /**
	 * Check the multimedia
	 */
	private void checkMultimedia() {
		List<Multimedia> mm = validateRepairStructure("Multimedia", "Multimedia", true, e,
				new ListRef<Multimedia>() {
					@Override
					public List<Multimedia> get(boolean initializeIfNeeded) {
						return e.getMultimedia(initializeIfNeeded);
					}
				});
		if (mm != null) {
			for (Multimedia m : mm) {
	            new MultimediaValidator(getRootValidator(), m).validate();
			}
		}
	}

	/**
	 * Check the emails
	 */
	private void checkEmails() {
		checkValidRepairStringList("Emails", "Email address", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return e.getEmails(initializeIfNeeded);
			}
		});
	}

	/**
	 * Check the fax numbers
	 */
	private void checkFaxNumbers() {
		checkValidRepairStringList("Faxes", "Fax number", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return e.getFaxNumbers(initializeIfNeeded);
			}
		});
	}

	/**
	 * Check the phone numbers
	 */
	private void checkPhoneNumbers() {
		checkValidRepairStringList("Phones", "Phone number", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return e.getEmails(initializeIfNeeded);
			}
		});
	
		if (e.getPlace() != null) {
	        new PlaceValidator(getRootValidator(), e.getPlace()).validate();
	    }
	}

	/**
	 * Check the www urls
	 */
	private void checkWwwUrls() {
		checkValidRepairStringList("URLs", "www url", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return e.getWwwUrls(initializeIfNeeded);
			}
		});
	}

	/**
     * Check list of required strings with custom tags and maybe repair
     */
    private List<StringWithCustomTags> checkValidRepairStringList(String v, String n, ListRef<StringWithCustomTags> r) {
		List<StringWithCustomTags> list = validateRepairStructure(v, n, true, e, r);
		if (list != null) {
			for (StringWithCustomTags swct : list) {
				checkRequiredString(swct, n, e);
			}
		}
		return list;
    }
}
