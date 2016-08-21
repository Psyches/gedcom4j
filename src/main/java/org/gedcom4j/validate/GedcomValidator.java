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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gedcom4j.model.Family;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.HasNotes;
import org.gedcom4j.model.Header;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.Note;
import org.gedcom4j.model.Repository;
import org.gedcom4j.model.Source;
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.Submission;
import org.gedcom4j.model.Submitter;
import org.gedcom4j.model.Trailer;

/**
 * <p>
 * A class to validate the contents of a {@link Gedcom} structure. It is used
 * primarily for those users who wish to create and write GEDCOM files, and is
 * of little importance or use to those who wish only to read/parse GEDCOM files
 * and use their data. Validation is performed automatically prior to writing a
 * GEDCOM file by default (although this can be disabled), and there is support
 * for automatically repairing ("autorepair") issues found.
 * </p>
 * <p>
 * <b>Note that the validation framework is a work in progress and as such, is
 * incompletely implemented at this time.</b>
 * </p>
 * <p>
 * General usage is as follows:
 * </p>
 * <ol>
 * <li>Instantiate a {@link GedcomValidator}, passing the {@link Gedcom}
 * structure to be validated as the argument to the constructor</li>
 * <li>If desired, turn off automatic repairs during validation by setting
 * {@link GedcomValidator#autorepairEnabled} to <tt>false</tt>.
 * <li>Call the {@link GedcomValidator#validate()} method.</li>
 * <li>Inspect the {@link GedcomValidator#findings} list, which contains
 * {@link GedcomValidationFinding} objects describing the problems that were
 * found. These will include errors that were fixed by autorepair (with severity
 * of INFO), and those that could not be autorepaired (with severity of ERROR or
 * WARNING).</li>
 * </ol>
 * <p>
 * Note again that by default, validation is performed automatically by the
 * {@link org.gedcom4j.writer.GedcomWriter} class when writing a GEDCOM file
 * out.
 * </p>
 * 
 * <h2>Autorepair</h2>
 * <p>
 * The validation framework, by default and unless disabled, will attempt to
 * automatically repair ("autorepair") problems it finds in the object graph, so
 * that if written as a GEDCOM file, the file written will conform to the GEDCOM
 * spec, as well as to help the developer avoid NullPointerExceptions due to
 * certain items not being instantiated (if they have so selected in the
 * {@link Options} class.
 * </p>
 * 
 * 
 * @author frizbog1
 */
/**
 * @author Mark A Sikes
 *
 */
@SuppressWarnings({ "PMD.GodClass", "PMD.TooManyMethods" })
public class GedcomValidator extends AbstractValidator {

	/**
	 * Will the most simple, obvious, non-destructive errors be automatically
	 * fixed? This includes things like creating empty collections where one is
	 * expected but only a null reference exists.
	 */
	private boolean autoRepairEnabled = true;

	/**
	 * The findings from validation
	 */
	private final List<GedcomValidationFinding> findings = new ArrayList<GedcomValidationFinding>();

	/**
	 * The gedcom structure being validated
	 */
	private final Gedcom gedcom;

	/**
	 * Constructor
	 * 
	 * @param gedcom
	 *            the gedcom structure being validated
	 */
	public GedcomValidator(Gedcom gedcom) {
		super(null);
		this.gedcom = gedcom;
	}

	/**
	 * @return the gedcom
	 */
	public Gedcom getGedcom() {
		return gedcom;
	}

	/**
	 * Get the findings
	 * 
	 * @return the findings
	 */
	public List<GedcomValidationFinding> getFindings() {
		return findings;
	}

	/**
	 * Are there any errors in the findings (so far)?
	 * 
	 * @return true if there exists at least one finding with severity ERROR
	 */
	public boolean hasErrors() {
		for (GedcomValidationFinding finding : getRootValidator().findings) {
			if (finding.getSeverity() == Severity.ERROR) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Are there any INFO level items in the findings (so far)?
	 * 
	 * @return true if there exists at least one finding with severity INFO
	 */
	public boolean hasInfo() {
		for (GedcomValidationFinding finding : getRootValidator().findings) {
			if (finding.getSeverity() == Severity.INFO) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Are there any warnings in the findings (so far)?
	 * 
	 * @return true if there exists at least one finding with severity WARNING
	 */
	public boolean hasWarnings() {
		for (GedcomValidationFinding finding : getRootValidator().findings) {
			if (finding.getSeverity() == Severity.WARNING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if auto-repair is enabled.
	 */
	@Override
	protected boolean isAutoRepairEnabled() {
		return autoRepairEnabled;
	}
	
	/**
	 * Set the auto-repair state
	 * 
	 * @param autorepair
	 *            the autorepair to set
	 */
	public void setAutoRepairEnabled(boolean autorepair) {
		autoRepairEnabled = autorepair;
	}

	/**
	 * Validate the gedcom file
	 */
	@Override
	public void validate() {
		findings.clear();
		if (getGedcom() == null) {
			addError("gedcom structure is null");
			return;
		}
		validateSubmitters();
		validateHeader();
		validateIndividuals();
		validateFamilies();
		validateRepositories();
		validateMultimedia();
		validateNotes();
		validateSources();
		validateSubmission(getGedcom().getSubmission());
		validateTrailer();
		checkNotes(new GedcomNotes());
	}

	/**
	 * Validate the submission substructure under the root gedcom
	 * 
	 * @param s
	 *            the submission substructure to be validated
	 */
	void validateSubmission(Submission s) {
		if (s == null) {
			addError("Submission record on root gedcom is null", getGedcom());
			return;
		}
		checkXref(s);
		checkOptionalString(s.getAncestorsCount(), "Ancestor count", s);
		checkOptionalString(s.getDescendantsCount(), "Descendant count", s);
		checkOptionalString(s.getNameOfFamilyFile(), "Name of family file", s);
		checkOptionalString(s.getOrdinanceProcessFlag(), "Ordinance process flag", s);
		checkOptionalString(s.getRecIdNumber(), "Automated record id", s);
		checkOptionalString(s.getTempleCode(), "Temple code", s);
	}

	/**
	 * Validate the families map
	 */
	private void validateFamilies() {
		boolean isRepairEnabled = isAutoRepairEnabled();
		for (Entry<String, Family> e : getGedcom().getFamilies().entrySet()) {
			if (e.getKey() == null) {
				if (isRepairEnabled) {
					addError("Family in map but has null key - cannot repair", e.getValue());
				} else {
					addError("Family in map but has null key", e.getValue());
				}
				continue;
			}
			Family f = e.getValue();
			if (!e.getKey().equals(f.getXref())) {
				if (isRepairEnabled) {
					addError("Family in map not keyed by its xref - cannot repair",
							new ValidatedItem(f.getXref()));
				} else {
					addError("Family in map not keyed by its xref", new ValidatedItem(f.getXref()));
				}
				continue;
			}
			new FamilyValidator(this, f).validate();
		}
	}

	/**
	 * Validate the {@link Gedcom#header} object
	 */
	private void validateHeader() {
		if (getGedcom().getHeader() == null) {
			if (isAutoRepairEnabled()) {
				getGedcom().setHeader(new Header());
				addInfo("Header was null - autorepaired");
			} else {
				addError("GEDCOM Header is null");
				return;
			}
		}
		new HeaderValidator(getRootValidator(), getGedcom().getHeader()).validate();
	}

	/**
	 * Validate the {@link Gedcom#individuals} collection
	 */
	private void validateIndividuals() {
		for (Entry<String, Individual> e : getGedcom().getIndividuals().entrySet()) {
			if (e.getKey() == null) {
				addError("Entry in individuals collection has null key", new ValidatedItem(e));
				return;
			}
			if (e.getValue() == null) {
				addError("Entry in individuals collection has null value", new ValidatedItem(e));
				return;
			}
			if (!e.getKey().equals(e.getValue().getXref())) {
				addError("Entry in individuals collection is not keyed by the individual's xref", new ValidatedItem(e));
				return;
			}
			new IndividualValidator(getRootValidator(), e.getValue()).validate();
		}
	}

	/**
	 * Validate the collection of {@link Multimedia} objects
	 */
	private void validateMultimedia() {
		if (getGedcom().getMultimedia() != null) {
			for (Multimedia m : getGedcom().getMultimedia().values()) {
				MultimediaValidator mv = new MultimediaValidator(this, m);
				mv.validate();
			}
		}
	}

	/**
	 * Validates the notes
	 */
	private void validateNotes() {
		int i = 0;
		for (Note n : getGedcom().getNotes().values()) {
			i++;
			new NoteValidator(getRootValidator(), i, n).validate();
		}
	}

	/**
	 * Validate the repositories collection
	 */
	private void validateRepositories() {
		for (Entry<String, Repository> e : getGedcom().getRepositories().entrySet()) {
			if (e.getKey() == null) {
				addError("Entry in repositories collection has null key", new ValidatedItem(e));
				return;
			}
			if (e.getValue() == null) {
				addError("Entry in repositories collection has null value", new ValidatedItem(e));
				return;
			}
			if (!e.getKey().equals(e.getValue().getXref())) {
				addError("Entry in repositories collection is not keyed by the Repository's xref", new ValidatedItem(e));
				return;
			}
			new RepositoryValidator(getRootValidator(), e.getValue()).validate();
		}

	}

	/**
	 * Validate the {@link Gedcom#sources} collection
	 */
	private void validateSources() {
		for (Entry<String, Source> e : getGedcom().getSources().entrySet()) {
			if (e.getKey() == null) {
				addError("Entry in sources collection has null key", new ValidatedItem(e));
				return;
			}
			if (e.getValue() == null) {
				addError("Entry in sources collection has null value", new ValidatedItem(e));
				return;
			}
			if (!e.getKey().equals(e.getValue().getXref())) {
				addError("Entry in sources collection is not keyed by the individual's xref", new ValidatedItem(e));
				return;
			}
			new SourceValidator(getRootValidator(), e.getValue()).validate();
		}
	}

	/**
	 * Validate the submitters collection
	 */
	private void validateSubmitters() {
		if (getGedcom().getSubmitters().isEmpty()) {
			if (isAutoRepairEnabled()) {
				Submitter s = new Submitter();
				s.setXref("@SUBM0000@");
				s.setName(new StringWithCustomTags("UNSPECIFIED"));
				getGedcom().getSubmitters().put(s.getXref(), s);
				addInfo("Submitters collection was empty - repaired", getGedcom());
			} else {
				addError("Submitters collection is empty", getGedcom());
			}
		}
		for (Submitter s : getGedcom().getSubmitters().values()) {
			new SubmitterValidator(getRootValidator(), s).validate();
		}
	}

	/**
	 * Validate the trailer
	 */
	private void validateTrailer() {
		if (getGedcom().getTrailer() == null) {
			if (isAutoRepairEnabled()) {
				getGedcom().setTrailer(new Trailer());
				getRootValidator().addInfo("Gedcom had no trailer - repaired", getGedcom());
			} else {
				getRootValidator().addError("Gedcom has no trailer", getGedcom());
			}
		}
	}
	
	/**
	 * Wrapper class for Gedcom Map of notes into List of notes.
	 */
	class GedcomNotes implements HasNotes {
		@Override
		public List<Note> getNotes() {
			Map<String, Note> notes = gedcom.getNotes();
			return notes == null ? null : new ArrayList<Note>(notes.values());
		}

		@Override
		public List<Note> getNotes(boolean initializeIfNeeded) {
			List<Note> notes = getNotes();
			if (initializeIfNeeded && notes == null)
				addError("Cannot repair notes on the gedcom object itself", gedcom);
			return getNotes();
		}
	}
}
