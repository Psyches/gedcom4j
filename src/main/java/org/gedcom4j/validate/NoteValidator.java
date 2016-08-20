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

import org.gedcom4j.model.Note;
import org.gedcom4j.model.UserReference;

/**
 * Validator for a single {@link Note}
 * 
 * @author frizbog
 * 
 */
public class NoteValidator extends AbstractValidator {

    /**
     * The note being validated
     */
    private final Note note;

    /**
     * The note's ordinal location in whatever collection it's in
     */
    private final int location;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the main gedcom validator that holds all the findings
     * @param theLocation
     *            the note's ordinal location in whatever collection it's in
     * @param theNote
     *            the note being validated
     */
    public NoteValidator(GedcomValidator rootValidator, int theLocation, Note theNote) {
        super(rootValidator);
        location = theLocation;
        note = theNote;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        checkOptionalString(note.getRecIdNumber(), "automated record id", note);
        checkChangeDate(note.getChangeDate(), note);
        checkLines();
        checkCitations(note);
        checkUserReferences();
    }
    
    /**
     * Check the note text lines. 
     */
    private void checkLines() {
        List<String> list = checkListStructure("text lines in a note", false, note, new ListRef<String>() {
			@Override
			public List<String> get(boolean initializeIfNeeded) {
				return note.getLines(initializeIfNeeded);
			}
		});

        if (note.getXref() == null && (list == null || list.isEmpty())) {
            addError("Note " + location + " without xref has no lines", note);
        }
    }
    
    /**
     * Check user references
     */
    private void checkUserReferences() {
		List<UserReference> userReferences = checkListStructure("user references", true, note, new ListRef<UserReference>() {
			@Override
			public List<UserReference> get(boolean initializeIfNeeded) {
				return note.getUserReferences(initializeIfNeeded);
			}
		});
		checkUserReferences(userReferences, note);
    }
}
