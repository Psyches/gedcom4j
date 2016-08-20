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

import org.gedcom4j.model.EventRecorded;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.RepositoryCitation;
import org.gedcom4j.model.Source;
import org.gedcom4j.model.SourceCallNumber;
import org.gedcom4j.model.SourceData;

/**
 * A validator for {@link Source} objects. See {@link GedcomValidator} for usage
 * information.
 * 
 * @author frizbog1
 * 
 */
public class SourceValidator extends AbstractValidator {

	/**
	 * The source being validated
	 */
	private final Source source;

	/**
	 * Constructor
	 * 
	 * @param rootValidator
	 *            the root validator
	 * @param source
	 *            the source being validated
	 */
	public SourceValidator(GedcomValidator rootValidator, Source source) {
		super(rootValidator);
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validate() {
		if (source == null) {
			addError("Source is null and cannot be validated");
			return;
		}
		checkXref(source);
		checkChangeDate(source.getChangeDate(), source);
		final SourceData sd = source.getData();
		if (sd != null) {
			new NotesValidator(getRootValidator(), sd).validate();
			checkOptionalString(sd.getRespAgency(), "responsible agency", sd);

			// Structure validate, repair, and dedup the recorded events collection
			List<EventRecorded> erList = checkListStructure("recorded event", true, sd, new ListRef<EventRecorded>() {
				@Override
				public List<EventRecorded> get(boolean initializeIfNeeded) {
					return sd.getEventsRecorded(initializeIfNeeded);
				}
			});

			if (erList != null) {
				for (EventRecorded er : erList) {
					checkOptionalString(er.getDatePeriod(), "date period", er);
					checkOptionalString(er.getEventType(), "event type", er);
					checkOptionalString(er.getJurisdiction(), "jurisdiction", er);
				}
			}
		}

		// Structure validate, repair, and dedup the multimedia collection
		List<Multimedia> mmList = checkListStructure("multimedia", true, source, new ListRef<Multimedia>() {
			@Override
			public List<Multimedia> get(boolean initializeIfNeeded) {
				return source.getMultimedia(initializeIfNeeded);
			}
		});

		if (mmList != null) {
			for (Multimedia mm : mmList) {
				new MultimediaValidator(getRootValidator(), mm).validate();
			}
		}
		
		new NotesValidator(getRootValidator(), source).validate();
		
		checkStringList(source.getOriginatorsAuthors(), "originators/authors", false);
		checkStringList(source.getPublicationFacts(), "publication facts", false);
		checkOptionalString(source.getRecIdNumber(), "automated record id", source);
		checkStringList(source.getSourceText(), "source text", true);
		checkOptionalString(source.getSourceFiledBy(), "source filed by", source);
		checkStringList(source.getTitle(), "title", true);
		checkUserReferences(source.getUserReferences(), source);

		RepositoryCitation c = source.getRepositoryCitation();
		if (c != null) {
			new NotesValidator(getRootValidator(), c).validate();
			checkRequiredString(c.getRepositoryXref(), "repository xref", c);
			checkCallNumbers(c);
		}
	}

	/**
	 * Check the call numbers on a RepositoryCitation object
	 * 
	 * @param citation
	 *            the citation to check the call numbers on
	 */
	private void checkCallNumbers(final RepositoryCitation citation) {
		List<SourceCallNumber> scnList = checkListStructure("source call number", true, citation, new ListRef<SourceCallNumber>() {
			@Override
			public List<SourceCallNumber> get(boolean initializeIfNeeded) {
				return citation.getCallNumbers(initializeIfNeeded);
			}
		});

		if (scnList != null) {
			for (SourceCallNumber scn : scnList) {
				checkOptionalString(scn.getCallNumber(), "call number", scn);
				if (scn.getCallNumber() == null) {
					if (scn.getMediaType() != null) {
						addError("You cannot specify media type without a call number in a SourceCallNumber structure", scn);
					}
				} else {
					checkOptionalString(scn.getMediaType(), "media type", scn);
				}
			}
		}
	}
}
