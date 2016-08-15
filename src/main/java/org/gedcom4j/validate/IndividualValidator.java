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
 * A validator for an {@link Individual}. See {@link GedcomValidator} for usage information.
 * 
 * @author frizbog1
 * 
 */
class IndividualValidator extends AbstractValidator {

    /**
     * The individual being validated
     */
    private final Individual individual;

    /**
     * Constructor
     * 
     * @param gedcomValidator
     *            the root validator
     * @param individual
     *            the individual being validated
     */
    public IndividualValidator(GedcomValidator gedcomValidator, Individual individual) {
        super(gedcomValidator);
        this.individual = individual;
    }

    @Override
    protected void validate() {
        if (individual == null) {
            addError("Individual is null");
            return;
        }
        checkXref(individual);
        List<PersonalName> names = individual.getNames();
        if (names == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getNames(true).clear();
                getRootValidator().addInfo("Individual " + individual.getXref() + " had no list of names - repaired", individual);
            } else {
                getRootValidator().addError("Individual " + individual.getXref() + " has no list of names", individual);
            }
        } else {
            if (getRootValidator().isAutorepairEnabled()) {
                int dups = new DuplicateEliminator<PersonalName>(names).process();
                if (dups > 0) {
                    getRootValidator().addInfo(dups + " duplicate names found and removed", individual);
                }
            }
            if (names != null) {
                for (PersonalName pn : names) {
                    new PersonalNameValidator(getRootValidator(), pn).validate();
                }
            }
        }
        if (getRootValidator().isAutorepairEnabled()) {
            int dups = new DuplicateEliminator<FamilyChild>(individual.getFamiliesWhereChild()).process();
            if (dups > 0) {
                getRootValidator().addInfo(dups + " duplicate families (where individual was a child) found and removed", individual);
            }
        }
        if (getRootValidator().isAutorepairEnabled()) {
            int dups = new DuplicateEliminator<FamilySpouse>(individual.getFamiliesWhereSpouse()).process();
            if (dups > 0) {
                getRootValidator().addInfo(dups + " duplicate families (where individual was a spouse) found and removed", individual);
            }
        }

        checkAliases();
        checkAssociations();
        checkCitations();
        checkIndividualAttributes();
        checkSubmitters();
        checkIndividualEvents();
    }

    /**
     * Validate the {@link Individual#aliases} collection
     * 
     */
    private void checkAliases() {
        if (individual.getAliases() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getAliases(true).clear();
                addInfo("aliases collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("aliases collection for individual is null", individual);
            }
        } else {
            checkStringTagList(individual.getAliases(), "aliases on individual", false);
        }
    }

    /**
     * Validate the {@link Individual#associations} collection
     */
    private void checkAssociations() {
        if (individual.getAssociations() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getAssociations(true).clear();
                addInfo("associations collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("associations collection for individual is null", individual);
            }
        } else {
            if (individual.getAssociations() != null) {
                for (Association a : individual.getAssociations()) {
                    if (a == null) {
                        addError("associations collection for individual contains null entry", individual);
                    } else {
                        checkRequiredString(a.getAssociatedEntityType(), "associated entity type", a);
                        checkXref(a, "associatedEntityXref");
                    }
                }
            }
        }

    }

    /**
     * Validate the {@link Individual#citations} collection
     */
    private void checkCitations() {
        if (individual.getCitations() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getCitations(true).clear();
                addInfo("citations collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("citations collection for individual is null", individual);
            }
        } else {
            if (individual.getCitations() != null) {
                for (AbstractCitation c : individual.getCitations()) {
                    new CitationValidator(getRootValidator(), c).validate();
                }
            }
        }
    }

    /**
     * Validate the {@link Individual#attributes} collection
     */
    private void checkIndividualAttributes() {
        if (individual.getAttributes() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getAttributes(true).clear();
                addInfo("attributes collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("attributes collection for individual is null", individual);
            }
        } else {
            if (individual.getAttributes() != null) {
                for (IndividualAttribute a : individual.getAttributes()) {
                    if (a.getType() == null) {
                        addError("Individual attribute requires a type", a);
                    }
                }
            }
        }
    }

    /**
     * Validate the {@link Individual#events} collection
     */
    private void checkIndividualEvents() {
        if (individual.getEvents() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getEvents(true).clear();
                addInfo("events collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("events collection for individual is null", individual);
            }
        } else {
            if (individual.getEvents() != null) {
                for (IndividualEvent a : individual.getEvents()) {
                    if (a.getType() == null) {
                        addError("Individual event requires a type", a);
                    }
                    new EventValidator(getRootValidator(), a).validate();
                }
            }
        }
    }

    /**
     * Validate the two submitters collections: {@link Individual#ancestorInterest} and
     * {@link Individual#descendantInterest}
     */
    private void checkSubmitters() {
        if (individual.getAncestorInterest() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getAncestorInterest(true).clear();
                addInfo("ancestorInterest collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("ancestorInterest collection for individual is null", individual);
            }
        } else {
            if (individual.getAncestorInterest() != null) {
                for (Submitter submitter : individual.getAncestorInterest()) {
                    new SubmitterValidator(getRootValidator(), submitter).validate();
                }
            }
        }
        if (individual.getDescendantInterest() == null && Options.isCollectionInitializationEnabled()) {
            if (getRootValidator().isAutorepairEnabled()) {
                individual.getDescendantInterest(true).clear();
                addInfo("descendantInterest collection for individual was null - rootValidator.autorepaired", individual);
            } else {
                addError("descendantInterest collection for individual is null", individual);
            }
        } else {
            if (individual.getDescendantInterest() != null) {
                for (Submitter submitter : individual.getDescendantInterest()) {
                    new SubmitterValidator(getRootValidator(), submitter).validate();
                }
            }
        }

    }

}
