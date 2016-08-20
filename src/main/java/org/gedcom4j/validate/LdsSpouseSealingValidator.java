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

import org.gedcom4j.model.LdsSpouseSealing;

/**
 * Validator for {@link LdsSpouseSealing} objects
 * 
 * @author frizbog1
 * 
 */
class LdsSpouseSealingValidator extends AbstractValidator {

    /**
     * The sealing being validated
     */
    private final LdsSpouseSealing spouseSealing;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            root {@link GedcomValidator} that contains findings and settings
     * @param s
     *            the sealing being validated
     */
    public LdsSpouseSealingValidator(GedcomValidator rootValidator, LdsSpouseSealing s) {
        super(rootValidator);
        this.spouseSealing = s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        if (spouseSealing == null) {
            addError("LDS Spouse Sealing is null and cannot be validated");
            return;
        }
        checkCitations(spouseSealing);
        checkCustomTags(spouseSealing);
        checkOptionalString(spouseSealing.getDate(), "date", spouseSealing);
        new NotesValidator(getRootValidator(), spouseSealing).validate();
        checkOptionalString(spouseSealing.getPlace(), "place", spouseSealing);
        checkOptionalString(spouseSealing.getStatus(), "status", spouseSealing);
        checkOptionalString(spouseSealing.getTemple(), "temple", spouseSealing);
    }
}
