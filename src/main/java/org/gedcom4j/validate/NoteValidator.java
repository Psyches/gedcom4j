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
import org.gedcom4j.model.Note;

/**
 * Validator for a single {@link Note}
 * 
 * @author frizbog
 * 
 */
class NoteValidator extends AbstractValidator {

    /**
     * The note being validated
     */
    private final Note n;

    /**
     * The note's ordinal location in whatever collection it's in
     */
    private final int i;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the main gedcom validator that holds all the findings
     * @param i
     *            the note's ordinal location in whatever collection it's in
     * @param n
     *            the note being validated
     */
    public NoteValidator(GedcomValidator rootValidator, int i, Note n) {
        super(rootValidator);
        this.i = i;
        this.n = n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {

        if (Options.isCollectionInitializationEnabled() && n.getLines() == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                n.getLines(true).clear();
                addInfo("Lines of text collection on note was null - autorepaired");
            } else {
                addError("Lines of text collection on note is null", n);
                return;
            }
        }

        if (n.getXref() == null && (n.getLines() == null || n.getLines().isEmpty())) {
            addError("Note " + i + " without xref has no lines", n);
        }

        checkOptionalString(n.getRecIdNumber(), "automated record id", n);
        List<AbstractCitation> citations = n.getCitations();
        if (citations == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                n.getCitations(true).clear();
                addInfo("Source citations collection on note was null - autorepaired");
            } else {
                addError("Source citations collection on note is null", n);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<AbstractCitation>(citations).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate citations found and removed", n);
                }
            }
            if (citations != null) {
                for (AbstractCitation c : citations) {
                    new CitationValidator(getRootValidator(), c).validate();
                }
            }
        }
        if (n.getUserReferences() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                n.getUserReferences(true).clear();
                addInfo("User references collection on note was null - autorepaired");
            } else {
                addError("User references collection on note is null", n);
            }
        } else {
            checkUserReferences(n.getUserReferences(), n);
        }
        checkChangeDate(n.getChangeDate(), n);
    }

}
