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
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.Submitter;

/**
 * Validate a {@link Submitter} object. See {@link GedcomValidator} for usage information.
 * 
 * @author frizbog1
 * 
 */
class SubmitterValidator extends AbstractValidator {

    /**
     * The submitter being validated
     */
    private final Submitter submitter;
	private int maxLanguagePrefs = 3; // TODO: use extended Options to get a configured default (via Properties?)

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root validator containing among other things the findings collection
     * @param submitter
     *            the submitter being validated
     */
    public SubmitterValidator(GedcomValidator rootValidator, Submitter submitter) {
        super(rootValidator);
        this.submitter = submitter;
    }

    /**
	 * @return the maxLanguagePrefs
	 */
	public int getMaxLanguagePrefs() {
		return maxLanguagePrefs;
	}

	/**
	 * @param maxLanguagePrefs the maxLanguagePrefs to set
	 */
	public void setMaxLanguagePrefs(int theMaxLanguagePrefs) {
		maxLanguagePrefs = theMaxLanguagePrefs;
	}

    @Override
    protected void validate() {
        if (submitter == null) {
            addError("Submitter being validated is null");
            return;
        }
        checkXref(submitter);
        checkRequiredString(submitter.getName(), "name", submitter);
        checkLanguagePreferences();
        checkOptionalString(submitter.getRecIdNumber(), "record id number", submitter);
        checkOptionalString(submitter.getRegFileNumber(), "submitter registered rfn", submitter);
        if (submitter.getAddress() != null) {
            new AddressValidator(getRootValidator(), submitter.getAddress()).validate();
        }
        checkNotes(submitter);
    }

    /**
     * Check the language preferences
     */
    private void checkLanguagePreferences() {
        List<StringWithCustomTags> languagePref = submitter.getLanguagePref();
        if (isAutoRepairEnabled()) {
            eliminateDuplicatesWithInfo("language preferences", submitter, languagePref);
        }

        if (submitter.getLanguagePref(Options.isCollectionInitializationEnabled()) != null) {
            if (languagePref.size() > maxLanguagePrefs) {
                addError("Submitter exceeds limit on language preferences (" + maxLanguagePrefs + ")", submitter);
            }
            for (StringWithCustomTags s : languagePref) {
                checkRequiredString(s, "language pref", submitter);
            }
        }
    }
}
