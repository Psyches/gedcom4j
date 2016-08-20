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
import org.gedcom4j.model.CitationWithSource;
import org.gedcom4j.model.CitationWithoutSource;
import org.gedcom4j.model.Note;

/**
 * A validator for source citations - both {@link CitationWithoutSource} and {@link CitationWithSource}. See
 * {@link GedcomValidator} for usage information.
 * 
 * @author frizbog1
 * 
 */
class CitationValidator extends AbstractValidator {

    /**
     * The citation being validated
     */
    private final AbstractCitation citation;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root validator with the collection of findings
     * @param citation
     *            the citation being validated
     */
    public CitationValidator(GedcomValidator rootValidator, AbstractCitation citation) {
        super(rootValidator);
        this.citation = citation;
    }

    @Override
    protected void validate() {
        if (citation == null) {
            addError("Citation is null");
            return;
        }
        if (citation instanceof CitationWithSource) {
            CitationWithSource cws = (CitationWithSource) citation;
            if (cws.getSource() == null) {
                addError("CitationWithSource requires a non-null source reference", cws);
            }
            checkOptionalString(cws.getWhereInSource(), "where within source", cws);
            checkOptionalString(cws.getEventCited(), "event type cited from", cws);
            if (cws.getEventCited() == null) {
                if (cws.getRoleInEvent() != null) {
                    addError("CitationWithSource has role in event but a null event");
                }
            } else {
                checkOptionalString(cws.getRoleInEvent(), "role in event", cws);
            }
            checkOptionalString(cws.getCertainty(), "certainty/quality", cws);
        } else if (citation instanceof CitationWithoutSource) {
            final CitationWithoutSource cwns = (CitationWithoutSource) citation;
            checkStringList(cwns.getDescription(), "description on a citation without a source", true);
			// Structure validate, repair, and dedup text from source collection
			List<List<String>> texts = validateRepairStructure("Citations", "Texts from source (list of lists)", true,
					cwns, new ListRef<List<String>>() {
						@Override
						public List<List<String>> get(boolean initializeIfNeeded) {
							return cwns.getTextFromSource(initializeIfNeeded);
						}
					});
            if (texts != null) {
                for (List<String> text : texts) {
                    if (text == null) {
                        addError("Text from source collection (the list of lists) in CitationWithoutSource contains a null", citation);
                    } else {
                        checkStringList(text, "one of the sublists in the textFromSource collection on a source citation", true);
                    }
                }
            }
        } else {
            throw new IllegalStateException("AbstractCitation references must be either CitationWithSource or CitationWithoutSource instances");
        }
		// Structure validate and repair notes WITHOUT dedup
		List<Note> list = validateRepairStructure("Notes", "Note", false, citation, new ListRef<Note>() {
			@Override
			public List<Note> get(boolean initializeIfNeeded) {
				return citation.getNotes(initializeIfNeeded);
			}
		});
        if (list != null)
        	new NotesValidator(getRootValidator(), citation).validate();
    }
}
