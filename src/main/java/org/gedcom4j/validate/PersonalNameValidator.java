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
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.PersonalNameVariation;

/**
 * Validator for {@link PersonalName} objects
 * 
 * @author frizbog1
 */
class PersonalNameValidator extends AbstractValidator {

    /**
     * The personal name being validated
     */
    private final PersonalName pn;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root {@link GedcomValidator} that contains all the findings and options
     * @param pn
     *            the personal name being validated
     */
    public PersonalNameValidator(GedcomValidator rootValidator, PersonalName pn) {
        super(rootValidator);
        this.pn = pn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        if (pn == null) {
            addError("Personal name was null - cannot validate");
            return;
        }
        checkRequiredString(pn.getBasic(), "basic name", pn);
        checkOptionalString(pn.getGivenName(), "given name", pn);
        checkOptionalString(pn.getNickname(), "nickname", pn);
        checkOptionalString(pn.getPrefix(), "prefix", pn);
        checkOptionalString(pn.getSuffix(), "suffix", pn);
        checkOptionalString(pn.getSurname(), "surname", pn);
        checkOptionalString(pn.getSurnamePrefix(), "surname prefix", pn);
        checkCustomTags(pn);
        checkCitations();
        new NotesValidator(getRootValidator(), pn).validate();
        checkPhonetic();
        checkRomanized();
    }

    /**
     * Check the citations
     */
    private void checkCitations() {
    	// TODO: dedup citations here
		List<AbstractCitation> list = validateRepairStructure("Citations", "Citations", false, pn,
				new ListRef<AbstractCitation>() {
					@Override
					public List<AbstractCitation> get(boolean initializeIfNeeded) {
						return pn.getCitations(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (AbstractCitation c : list) {
				new CitationValidator(getRootValidator(), c).validate();
			}
		}
    }
    
    private void checkPhonetic() {
		checkNameVariations("Phonetics", "phonetic name variations", new ListRef<PersonalNameVariation>() {
			@Override
			public List<PersonalNameVariation> get(boolean initializeIfNeeded) {
				return pn.getPhonetic(initializeIfNeeded);
			}
		});
    }
    
    private void checkRomanized() {
		checkNameVariations("Romanized", "romanized name variations", new ListRef<PersonalNameVariation>() {
			@Override
			public List<PersonalNameVariation> get(boolean initializeIfNeeded) {
				return pn.getRomanized(initializeIfNeeded);
			}
		});
    }
    
    private void checkNameVariations(String v, String n, ListRef<PersonalNameVariation> handler) {
		List<PersonalNameVariation> list = validateRepairStructure(v, n, true, pn, handler);
		if (list != null) {
			for (PersonalNameVariation nv : list) {
                new PersonalNameVariationValidator(getRootValidator(), nv).validate();
			}
		}
    }
}
