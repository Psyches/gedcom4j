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
    private final AbstractEvent event;

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
        event = e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        if (event == null) {
            addError("Event is null and cannot be validated or autorepaired");
            return;
        }
        if (event.getAddress() != null) {
            new AddressValidator(getRootValidator(), event.getAddress()).validate();
        }
        checkOptionalString(event.getAge(), "age", event);
        checkOptionalString(event.getCause(), "cause", event);
        checkOptionalString(event.getDate(), "date", event);
        if (event.getDescription() != null && event.getDescription().trim().length() != 0) {
            addError("Event has description, which is non-standard. Remove this value, or move it (perhaps to a Note).", event);
        }
        checkEmails();
        checkFaxNumbers();
        checkMultimedia();
        checkPhoneNumbers();
        checkOptionalString(event.getReligiousAffiliation(), "religious affiliation", event);
        checkOptionalString(event.getRespAgency(), "responsible agency", event);
        checkOptionalString(event.getRestrictionNotice(), "restriction notice", event);
        checkOptionalString(event.getSubType(), "subtype", event);
        checkWwwUrls();
        checkCitations(event);
        checkNotes(event);
        checkCustomTags(event);
    }

    /**
	 * Check the multimedia
	 */
	private void checkMultimedia() {
		List<Multimedia> mm = checkListStructure("Multimedia", true, event, new ListRef<Multimedia>() {
			@Override
			public List<Multimedia> get(boolean initializeIfNeeded) {
				return event.getMultimedia(initializeIfNeeded);
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
		checkStringListStructure("Email address", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return event.getEmails(initializeIfNeeded);
			}
		});
	}

	/**
	 * Check the fax numbers
	 */
	private void checkFaxNumbers() {
		checkStringListStructure("Fax number", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return event.getFaxNumbers(initializeIfNeeded);
			}
		});
	}

	/**
	 * Check the phone numbers
	 */
	private void checkPhoneNumbers() {
		checkStringListStructure("Phone number", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return event.getEmails(initializeIfNeeded);
			}
		});
	
		if (event.getPlace() != null) {
	        new PlaceValidator(getRootValidator(), event.getPlace()).validate();
	    }
	}

	/**
	 * Check the www urls
	 */
	private void checkWwwUrls() {
		checkStringListStructure("www url", new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return event.getWwwUrls(initializeIfNeeded);
			}
		});
	}

	/**
     * Check list of required strings with custom tags and maybe repair
     * TODO push to AbstractValidator
     */
    private List<StringWithCustomTags> checkStringListStructure(String name, ListRef<StringWithCustomTags> r) {
		List<StringWithCustomTags> list = checkListStructure(name, true, event, r);
		if (list != null) {
			for (StringWithCustomTags swct : list) {
				checkRequiredString(swct, name, event);
			}
		}
		return list;
    }
}
