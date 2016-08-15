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
import org.gedcom4j.model.PersonalNameVariation;

/**
 * Validator for {@link PersonalNameVariation} objects
 * 
 * @author frizbog1
 */
class PersonalNameVariationValidator extends NameVariationValidator {

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root {@link GedcomValidator} that contains the findings and the settings
     * @param pnv
     *            the personal name variation to be validated
     */
    public PersonalNameVariationValidator(GedcomValidator rootValidator, PersonalNameVariation pnv) {
        super(rootValidator, pnv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        if (nv == null) {
            return;
        }
        if (!(nv instanceof PersonalNameVariation)) {
            addError("Name variation on person is not a PersonalNameVariation");
            return;
        }
        PersonalNameVariation pnv = (PersonalNameVariation) nv;
        List<AbstractCitation> citations = pnv.getCitations();
        if (citations == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                pnv.getCitations(true).clear();
                addInfo("citations collection for personal name was null - autorepaired", pnv);
            } else {
                addError("citations collection for personal name is null", pnv);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<AbstractCitation>(citations).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate citations found and removed", pnv);
                }
            }

            for (AbstractCitation c : citations) {
                new CitationValidator(getRootValidator(), c).validate();
            }
        }
        checkOptionalString(pnv.getGivenName(), "given name", pnv);
        checkOptionalString(pnv.getNickname(), "nickname", pnv);
        checkOptionalString(pnv.getPrefix(), "prefix", pnv);
        checkOptionalString(pnv.getSuffix(), "suffix", pnv);
        checkOptionalString(pnv.getSurname(), "surname", pnv);
        checkOptionalString(pnv.getSurnamePrefix(), "surname prefix", pnv);
        new NotesValidator(getRootValidator(), pnv, pnv.getNotes()).validate();
    }
}
