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
import org.gedcom4j.model.*;

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
        new NotesValidator(getRootValidator(), f, f.getNotes()).validate();
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
        List<Individual> children = f.getChildren();
        if (children == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                f.getChildren(true).clear();
                getRootValidator().addInfo("Family's collection of children was null - repaired", f);
            } else {
                getRootValidator().addError("Family's collection of children is null", f);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<Individual>(children).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate children found and removed", f);
                }
            }
            if (children != null) {
                for (Individual i : children) {
                    if (i == null) {
                        getRootValidator().addError("Family with xref '" + f.getXref() + "' has a null entry in children collection", f);
                    }
                }
            }
        }
    }

    /**
     * Check citations.
     */
    private void checkCitations() {
        List<AbstractCitation> citations = f.getCitations();
        if (citations == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                f.getCitations(true).clear();
                addInfo("citations collection for family was null - rootValidator.autorepaired", f);
            } else {
                addError("citations collection for family is null", f);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<AbstractCitation>(citations).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate source citations found and removed", f);
                }
            }
            if (citations != null) {
                for (AbstractCitation c : citations) {
                    new CitationValidator(getRootValidator(), c).validate();
                }
            }
        }
    }

    /**
     * Check lds spouse sealings.
     */
    private void checkLdsSpouseSealings() {
        List<LdsSpouseSealing> ldsSpouseSealings = f.getLdsSpouseSealings();
        if (ldsSpouseSealings == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                f.getLdsSpouseSealings(true).clear();
                addInfo("LDS spouse sealings collection for family was null - rootValidator.autorepaired", f);
            } else {
                addError("LDS spouse sealings collection for family is null", f);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<LdsSpouseSealing>(ldsSpouseSealings).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate LDS spouse sealings found and removed", f);
                }
            }
            if (ldsSpouseSealings != null) {
                for (LdsSpouseSealing s : ldsSpouseSealings) {
                    new LdsSpouseSealingValidator(getRootValidator(), s).validate();
                }
            }
        }
    }

    /**
     * Check multimedia.
     */
    private void checkMultimedia() {
        List<Multimedia> multimedia = f.getMultimedia();
        if (multimedia == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                f.getMultimedia(true).clear();
                addInfo("Multimedia collection for family was null - rootValidator.autorepaired", f);
            } else {
                addError("Multimedia collection for family is null", f);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<Multimedia>(multimedia).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate multimedia found and removed", f);
                }
            }
            if (multimedia != null) {
                for (Multimedia m : multimedia) {
                    new MultimediaValidator(getRootValidator(), m).validate();
                }
            }
        }
    }

    /**
     * Check submitters.
     */
    private void checkSubmitters() {
        List<Submitter> submitters = f.getSubmitters();
        if (submitters == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                f.getSubmitters(true).clear();
                addInfo("Submitters collection was missing on family - repaired", f);
            } else {
                addInfo("Submitters collection is missing on family", f);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<Submitter>(submitters).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate submitters found and removed", f);
                }
            }
            if (submitters != null) {
                for (Submitter s : submitters) {
                    new SubmitterValidator(getRootValidator(), s).validate();
                }
            }
        }
    }

}
