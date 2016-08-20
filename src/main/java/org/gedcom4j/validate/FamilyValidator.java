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
import org.gedcom4j.model.AbstractEvent;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.LdsSpouseSealing;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.Submitter;

/**
 * Validator for {@link Family} objects
 * 
 * @author frizbog1
 */
class FamilyValidator extends AbstractValidator {

    /**
     * The family being validated
     */
    private final Family f;

    /**
     * Validator for {@link Family}
     * 
     * @param gedcomValidator
     *            the {@link GedcomValidator} that holds all the findings and settings
     * @param f
     *            the family being validated
     */
    public FamilyValidator(GedcomValidator gedcomValidator, Family f) {
        super(gedcomValidator);
        this.f = f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        checkOptionalString(f.getAutomatedRecordId(), "Automated record id", f);
        checkChangeDate(f.getChangeDate(), f);
        checkChildren();
        checkCitations();
        checkCustomTags(f);
        if (f.getEvents() != null) {
            for (AbstractEvent ev : f.getEvents()) {
                new EventValidator(getRootValidator(), ev).validate();
            }
        }
        if (f.getHusband() != null) {
            new IndividualValidator(getRootValidator(), f.getHusband()).validate();
        }
        if (f.getWife() != null) {
            new IndividualValidator(getRootValidator(), f.getWife()).validate();
        }
        checkLdsSpouseSealings();
        checkMultimedia();
        new NotesValidator(getRootValidator(), f).validate();
        checkOptionalString(f.getNumChildren(), "number of children", f);
        checkOptionalString(f.getRecFileNumber(), "record file number", f);
        checkOptionalString(f.getRestrictionNotice(), "restriction notice", f);
        checkSubmitters();
        checkUserReferences(f.getUserReferences(), f);
    }

    /**
     * Check children.
     */
    private void checkChildren() {
		// Structure validate, repair, and dedup children on family collection
		List<Individual> list = validateRepairStructure("Children", "children", true, f,
				new ListRef<Individual>() {
					@Override
					public List<Individual> get(boolean initializeIfNeeded) {
						return f.getChildren(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (Individual i : list) {
                if (i == null) {
                    getRootValidator().addError("Family with xref '" + f.getXref() + "' has a null entry in children collection", f);
                }
			}
		}
    }

    /**
     * Check citations.
     */
    private void checkCitations() {
		// Structure validate, repair, and dedup citations on family collection
		List<AbstractCitation> list = validateRepairStructure("Citations", "Citations", true, f,
				new ListRef<AbstractCitation>() {
					@Override
					public List<AbstractCitation> get(boolean initializeIfNeeded) {
						return f.getCitations(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (AbstractCitation c : list) {
				new CitationValidator(getRootValidator(), c).validate();
			}
		}
    }

    /**
     * Check lds spouse sealings.
     */
    private void checkLdsSpouseSealings() {
		// Structure validate, repair, and dedup citations on event collection
		List<LdsSpouseSealing> list = validateRepairStructure("SpouseSealings", "spouse sealings", true, f,
				new ListRef<LdsSpouseSealing>() {
					@Override
					public List<LdsSpouseSealing> get(boolean initializeIfNeeded) {
						return f.getLdsSpouseSealings(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (LdsSpouseSealing l : list) {
				new LdsSpouseSealingValidator(getRootValidator(), l).validate();
			}
		}
    }

    /**
     * Check multimedia.
     */
    private void checkMultimedia() {
		List<Multimedia> list = validateRepairStructure("Multimedia", "Multimedia", true, f,
				new ListRef<Multimedia>() {
					@Override
					public List<Multimedia> get(boolean initializeIfNeeded) {
						return f.getMultimedia(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (Multimedia l : list) {
				new MultimediaValidator(getRootValidator(), l).validate();
			}
		}
    }

    /**
     * Check submitters.
     */
    private void checkSubmitters() {
		List<Submitter> list = validateRepairStructure("Submitters", "Submitter", true, f,
				new ListRef<Submitter>() {
					@Override
					public List<Submitter> get(boolean initializeIfNeeded) {
						return f.getSubmitters(initializeIfNeeded);
					}
				});
		if (list != null) {
			for (Submitter l : list) {
				new SubmitterValidator(getRootValidator(), l).validate();
			}
		}
    }
}
