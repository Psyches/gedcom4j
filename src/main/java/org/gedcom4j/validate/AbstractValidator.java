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

import java.util.HashSet;
import java.util.List;

import org.gedcom4j.Options;
import org.gedcom4j.model.AbstractCitation;
import org.gedcom4j.model.ChangeDate;
import org.gedcom4j.model.HasCitations;
import org.gedcom4j.model.HasCustomTags;
import org.gedcom4j.model.HasNotes;
import org.gedcom4j.model.HasXref;
import org.gedcom4j.model.Note;
import org.gedcom4j.model.StringTree;
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.UserReference;
import org.gedcom4j.model.ModelElement;

/**
 * A base class for all validators
 * 
 * @author frizbog1
 * 
 */
@SuppressWarnings({ "PMD.GodClass", "PMD.TooManyMethods" })
public abstract class AbstractValidator {

	/**
	 * The root validator - the one that holds the collection of findings among
	 * other things. Must be declared specifically a {@link GedcomValidator} and
	 * not an {@link AbstractValidator}
	 */
	private final GedcomValidator rootValidator;

	/**
	 * @param theRootValidator the root GedcomValidator of the entire Gedcom tree.
	 */
	protected AbstractValidator(GedcomValidator theRootValidator) {
		rootValidator = (theRootValidator == null && this instanceof GedcomValidator) ? (GedcomValidator) this
				: theRootValidator;
	}

	/**
	 * Get the root validator instance
	 * 
	 * @return the root validator.
	 */
	protected GedcomValidator getRootValidator() {
		return rootValidator;
	}

	/**
	 * @return true iff auto-repair is enabled on the root validator.
	 */
	protected boolean isAutorepairEnabled() {
		return getRootValidator().isAutorepairEnabled();
	}
	
	/**
	 * Add a new finding of severity ERROR
	 * 
	 * @param description
	 *            the description of the error
	 */
	protected void addError(String description) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.ERROR, null));
	}

	/**
	 * Add a new finding of severity ERROR
	 * 
	 * @param description
	 *            the description of the error
	 * @param o
	 *            the object in error
	 */
	protected void addError(String description, ModelElement o) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.ERROR, o));
	}

	/**
	 * Add a new finding of severity INFO
	 * 
	 * @param description
	 *            the description of the finding
	 */
	protected void addInfo(String description) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.INFO, null));
	}

	/**
	 * Add a new finding of severity INFO
	 * 
	 * @param description
	 *            the description of the finding
	 * @param o
	 *            the object in error
	 */
	protected void addInfo(String description, ModelElement o) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.INFO, o));
	}

	/**
	 * Add a new finding of severity WARNING
	 * 
	 * @param description
	 *            the description of the finding
	 */
	protected void addWarning(String description) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.WARNING, null));
	}

	/**
	 * Add a new finding of severity WARNING
	 * 
	 * @param description
	 *            the description of the finding
	 * @param o
	 *            the object in error
	 */
	protected void addWarning(String description, ModelElement o) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(description, Severity.WARNING, o));
	}

	/**
	 * Add a new problem to the Findings.
	 * @param repaired true iff the problem was repaired.
	 * @param message the message to add.
	 * @param objectContainingProblem the object with the problem.
	 */
	protected void addProblemFinding(boolean repaired, String message, ModelElement objectContainingProblem) {
		getRootValidator().getFindings().add(new GedcomValidationFinding(message + (repaired ? " - repaired" : ""),
				repaired ? Severity.INFO : Severity.ERROR, objectContainingProblem));
	}
	/**
	 * Add a standard error for a null entry from a model object reference
	 * 
	 * @param repairRequested true if repair was requested (repair cannot be done)
	 * @param theName the friendly referenced type name
	 * @param o the model object (the referencer)
	 */
	protected void addNullError(boolean repairRequested, String theName, ModelElement o) {
		addError(String.format("%s is null on %s%s", theName, o.getClass().getSimpleName(),
				repairRequested ? " - cannot repair" : ""), o);
	}
	/**
	 * Add a standard error for a null list from a model object reference
	 * 
	 * @param theValidator the Validator/repairer name
	 * @param theName the friendly list type name
	 * @param element the model object element
	 */
	protected void addNullListError(String theValidator, String theName, ModelElement element) {
		addNullError(isAutorepairEnabled(), "List of " + theName, element);
	}
	
	/**
	 * Is the string supplied non-null, and has something other than whitespace
	 * in it?
	 * 
	 * @param s
	 *            the strings
	 * @return true if the string supplied non-null, and has something other
	 *         than whitespace in it
	 */
	protected static boolean isSpecified(String s) {
		if (s != null) {
			for (int i = 0; i < s.length(); i++) {
				if (!Character.isWhitespace(s.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Process the list, eliminating duplicates
	 * 
	 * @param <T> the list element type.
	 * @param items the list of items to eliminate duplicates in.
	 * @return the number of items removed.
	 */
	protected static <T> int eliminateDuplicates(List<T> items) {
		int result = 0;
		if (items != null && !items.isEmpty()) {
			HashSet<T> unique = new HashSet<>();
			for (int i = 0; i < items.size();) {
				T item = items.get(i);
				if (unique.contains(item)) {
					result++;
					items.remove(i);
				} else {
					i++;
					unique.add(item);
				}
			}
		}
		return result;
	}

	/**
	 * Eliminate any duplicates in a list on an element, and give an info-level report if any were found
	 * 
	 * @param <T> the list element type.
	 * @param name the kind of the list of items duplicates are being removed on.
	 * @param element the list owner model element.
	 * @param items the list to remove duplicates on.
	 */
	protected <T> void eliminateDuplicatesWithInfo(String name, ModelElement element, List<T> items) {
	    int dups = eliminateDuplicates(items);
	    if (dups > 0) {
	        addInfo(dups + " duplicates in " + name + " found and removed", element);
	    }
	}

	/**
	 * checkListStructure checks lists of model sub-objects on an object. Those
	 * lists must be non-null but may be empty. If auto-repair is enabled, a
	 * null list will be converted to an empty list. If de-duplication is
	 * requested and auto-repair is enabled, the list itself is de-duplicated
	 * (exact duplicate entries are removed).
	 * 
	 * @param <E>
	 *            the type of the model element containing a list.
	 * @param <T>
	 *            the list element type.
	 * @param theName
	 *            the name of the list to validate and maybe repair
	 * @param handleDups
	 *            true for duplicate removal on a non-empty list.
	 * @param theElement
	 *            the outer element that owns the list
	 * @param theHandler
	 *            the handler for the list attached to the outer element
	 * 
	 * @return the resulting list which may be null, or newly created and/or
	 *         de-duped depending on arguments
	 */
	protected <E extends ModelElement, T> List<T> checkListStructure(String theName,
			boolean handleDups, E theElement, ListRef<T> theHandler) {
		boolean isRepairEnabled = isAutorepairEnabled();
		List<T> list = theHandler.get(false);
		String qualifiedName = String.format("List of %s on %s", theName, theElement.getClass().getSimpleName());
		if (list == null && Options.isCollectionInitializationEnabled()) {
			if (isRepairEnabled) {
				list = theHandler.get(true); // list was null, now will not be
				addInfo(qualifiedName + " was null - auto-repaired", theElement);
			} else {
				addError(qualifiedName + " is null", theElement);
			}
		} else {
			if (isRepairEnabled && handleDups) {
				eliminateDuplicatesWithInfo(qualifiedName, theElement, list);
			}
		}
		return list;
	}

	/**
	 * Check a change date structure
	 * 
	 * @param changeDate
	 *            the change date structure to check
	 * @param objectWithChangeDate
	 *            the object with the change date
	 */
	protected void checkChangeDate(ChangeDate changeDate, ModelElement objectWithChangeDate) {
		if (changeDate == null) {
			// Change dates are always optional
			return;
		}
		checkRequiredString(changeDate.getDate(), "change date", objectWithChangeDate);
		checkOptionalString(changeDate.getTime(), "change time", objectWithChangeDate);
		checkNotes(changeDate);
	}

    /**
     * Check the notes.
     * 
     * @param notes the model element that has the Notes list.
     */
    protected void checkNotes(final HasNotes notes) {
		List<Note> list = checkListStructure("Notes", true, notes, new ListRef<Note>() {
			@Override
			public List<Note> get(boolean initializeIfNeeded) {
				return notes.getNotes(initializeIfNeeded);
			}
		});
		if (list != null) {
			int i = 0;
			for (Note n : list) {
				i++;
				new NoteValidator(getRootValidator(), i, n).validate();
			}
		}
    }
    
    /**
     * Check the citations.
     * 
     * @param citations the model element that has the Citations list.
     */
    protected void checkCitations(final HasCitations citations) {
		List<AbstractCitation> list = checkListStructure("Citations", true, citations, new ListRef<AbstractCitation>() {
			@Override
			public List<AbstractCitation> get(boolean initializeIfNeeded) {
				return citations.getCitations(initializeIfNeeded);
			}
		});
		if (list != null) {
			for (AbstractCitation c : list) {
				new CitationValidator(getRootValidator(), c).validate();
			}
		}
    }
    
	/**
	 * Check custom tags on an object implementing HasCustomTags. If autorepair
	 * is on, it will reflectively fix this.
	 * 
	 * @param element
	 *            the object being validated
	 */
	protected void checkCustomTags(HasCustomTags element) {
		List<StringTree> customTags = element.getCustomTags();
		if (customTags == null && Options.isCollectionInitializationEnabled()) {
			if (isAutorepairEnabled()) {
				element.getCustomTags(true);
				getRootValidator().addInfo("Custom tag collection was null - repaired", element);
			} else {
				getRootValidator().addError("Custom tag collection is null - must be at least an empty collection", element);
			}
		}
	}

	/**
	 * Checks that the value for an optional tag is either null, or greater than
	 * zero characters long after trimming.
	 * 
	 * @param optionalString
	 *            the field that is required
	 * @param fieldDescription
	 *            the human-readable name of the field
	 * @param objectContainingField
	 *            the object containing the field being checked
	 */
	protected void checkOptionalString(String optionalString, String fieldDescription,
			ModelElement objectContainingField) {
		if (optionalString != null && !isSpecified(optionalString)) {
			addError(fieldDescription + " on " + objectContainingField.getClass().getSimpleName()
					+ " is specified, but has a blank value", objectContainingField);
		}
	}

	/**
	 * Checks that the value for an optional tag is either null, or greater than
	 * zero characters long after trimming
	 * 
	 * @param optionalString
	 *            the field that is required
	 * @param fieldDescription
	 *            the human-readable name of the field
	 * @param objectContainingField
	 *            the object containing the field being checked
	 */
	protected void checkOptionalString(StringWithCustomTags optionalString, String fieldDescription,
			ModelElement objectContainingField) {
		if (optionalString != null && optionalString.getValue() != null && !isSpecified(optionalString.getValue())) {
			addError(fieldDescription + " on " + objectContainingField.getClass().getSimpleName()
					+ " is specified, but has a blank value", objectContainingField);
		}
		checkStringWithCustomTags(optionalString, fieldDescription);
	}

	/**
	 * Checks that a required string field is specified
	 * 
	 * @param requiredString
	 *            the field that is required
	 * @param fieldDescription
	 *            the human-readable name of the field
	 * @param objectContainingField
	 *            the object containing the field being checked
	 */
	protected void checkRequiredString(String requiredString, String fieldDescription,
			ModelElement objectContainingField) {
		if (!isSpecified(requiredString)) {
			addError(fieldDescription + " on " + objectContainingField.getClass().getSimpleName()
					+ " is required, but is either null or blank", objectContainingField);
		}
	}

	/**
	 * Checks that a required string field is specified
	 * 
	 * @param requiredString
	 *            the field that is required
	 * @param fieldDescription
	 *            the human-readable name of the field
	 * @param objectContainingField
	 *            the object containing the field being checked
	 */
	protected void checkRequiredString(StringWithCustomTags requiredString, String fieldDescription,
			ModelElement objectContainingField) {
		if (requiredString == null || requiredString.getValue() == null
				|| requiredString.getValue().trim().length() == 0) {
			addError(fieldDescription + " on " + objectContainingField.getClass().getSimpleName()
					+ " is required, but is either null or blank", objectContainingField);
		}
		checkStringWithCustomTags(requiredString, fieldDescription);
	}

	/**
	 * Check list of required strings with custom tags and maybe repair
	 * 
	 * @param fieldDescription
	 *            the human-readable name of the field
	 * @param objectContainingField
	 *            the object containing the field being checked
	 * @param stringList
	 *            the list of StringWithCustomTags
	 * 
	 * @return the resulting list of StringWithCustomTags
	 */
	protected List<StringWithCustomTags> checkStringListStructure(String fieldDescription,
			ModelElement objectContainingField, ListRef<StringWithCustomTags> stringList) {
		List<StringWithCustomTags> list = checkListStructure(fieldDescription, true, objectContainingField, stringList);
		if (list != null) {
			for (StringWithCustomTags swct : list) {
				checkRequiredString(swct, fieldDescription, objectContainingField);
			}
		}
		return list;
	}

	/**
	 * Check a string list (List&lt;String&gt;) on an object. All strings in the
	 * list must be non-null and non-blank when trimmed.
	 * 
	 * @param stringList
	 *            the stringlist being validated
	 * @param description
	 *            a description of the string list
	 * @param blanksAllowed
	 *            are blank strings allowed in the string list?
	 */
	protected void checkStringList(List<String> stringList, String description, boolean blanksAllowed) {
		int i = 0;
		if (stringList != null) {
			while (i < stringList.size()) {
				String a = stringList.get(i);
				if (a == null) {
					if (isAutorepairEnabled()) {
						addInfo("String list (" + description + ") contains null entry - removed",
								new ValidatedItem(stringList));
						stringList.remove(i);
						continue;
					}
					addError("String list (" + description + ") contains null entry", new ValidatedItem(stringList));
				} else if (!blanksAllowed && !isSpecified(a)) {
					if (isAutorepairEnabled()) {
						addInfo("String list (" + description
								+ ") contains blank entry where none are allowed - removed",
								new ValidatedItem(stringList));
						stringList.remove(i);
						continue;
					}
					addError("String list (" + description + ") contains blank entry where none are allowed",
							new ValidatedItem(stringList));
				}
				i++;
			}
		}
	}

	/**
	 * Check a tagged string list (List&lt;StringWithCustomTags&gt;) on an
	 * object. All strings in the list must be non-null and non-blank when
	 * trimmed.
	 * 
	 * @param stringList
	 *            the stringlist being validated
	 * @param description
	 *            a description of the string list
	 * @param blanksAllowed
	 *            are blank strings allowed in the string list?
	 */
	protected void checkStringTagList(List<StringWithCustomTags> stringList, String description,
			boolean blanksAllowed) {
		int i = 0;
		boolean isRepairEnabled = isAutorepairEnabled();
		if (isRepairEnabled) {
			eliminateDuplicatesWithInfo("tagged strings", new ValidatedItem(stringList), stringList);
		}

		if (stringList != null) {
			while (i < stringList.size()) {
				StringWithCustomTags a = stringList.get(i);
				if (a == null || a.getValue() == null) {
					if (isRepairEnabled) {
						addInfo("String list (" + description + ") contains null entry - removed",
								new ValidatedItem(stringList));
						stringList.remove(i);
						continue;
					}
					addError("String list (" + description + ") contains null entry", new ValidatedItem(stringList));
				} else if (!blanksAllowed && a.getValue().trim().length() == 0) {
					if (isRepairEnabled) {
						addInfo("String list (" + description
								+ ") contains blank entry where none are allowed - removed",
								new ValidatedItem(stringList));
						stringList.remove(i);
						continue;
					}
					addError("String list (" + description + ") contains blank entry where none are allowed",
							new ValidatedItem(stringList));
				}
				i++;
			}
		}
	}

	/**
	 * Check a collection of user references
	 * 
	 * @param userReferences
	 *            the collection of user references
	 * @param objectWithUserReferences
	 *            the object that contains the collection of user references
	 */
	protected void checkUserReferences(List<UserReference> userReferences, ModelElement objectWithUserReferences) {
		if (userReferences != null) {
			for (UserReference userReference : userReferences) {
				if (userReference == null) {
					addNullError(false, "user reference in list", objectWithUserReferences);
				} else {
	                checkCustomTags(userReference);
					checkRequiredString(userReference.getReferenceNum(), "reference number", userReference);
					checkOptionalString(userReference.getType(), "reference type", userReference);
				}
			}
		}
	}

	/**
	 * @param objectContainingXref
	 *            an object that has a Xref and implements HasXref to allow a
	 *            generic way of getting it
	 */
	protected void checkXref(HasXref objectContainingXref) {
		checkXref(objectContainingXref, "xref");
	}

	/**
	 * Check the xref on an object, using the default field name of
	 * <tt>xref</tt> for the xref field
	 * 
	 * @param objectContainingXref
	 *            the object containing the xref field
	 * @param xrefFieldName
	 *            the name of the field for Findings reporting
	 */
	protected void checkXref(HasXref objectContainingXref, String xrefFieldName) {
		String xref = objectContainingXref.getXref();
		checkRequiredString(xref, xrefFieldName, objectContainingXref);
		if (xref != null) {
			String context = objectContainingXref.getClass().getSimpleName();
			if (xref.length() < 3) {
				addError("xref on " + context + " is too short to be a valid xref", objectContainingXref);
			} else if (xref.charAt(0) != '@') {
				addError("xref on " + context + " doesn't start with an at-sign (@)", objectContainingXref);
			}
			if (!xref.endsWith("@")) {
				addError("xref on " + context + " doesn't end with an at-sign (@)", objectContainingXref);
			}
		}
	}

	/**
	 * Validate the gedcom file
	 */
	protected abstract void validate();

	/**
	 * Check a string with custom tags to make sure the custom tags collection
	 * is defined whenever there is a value in the string part.
	 * 
	 * @param swct
	 *            the string with custom tags
	 * @param fieldDescription
	 *            a description of the field's contents
	 */
	private void checkStringWithCustomTags(StringWithCustomTags swct, String fieldDescription) {
		if (swct == null) {
			return;
		}
		if (swct.getValue() == null || !isSpecified(swct.getValue())) {
			addError("A string with custom tags object (" + fieldDescription + ") was defined with no value", swct);
		}
		checkCustomTags(swct);
	}
}
