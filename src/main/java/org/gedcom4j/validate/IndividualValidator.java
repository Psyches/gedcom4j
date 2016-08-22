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

import org.gedcom4j.model.Association;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualAttribute;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.Submitter;

/**
 * A validator for an {@link Individual}. See {@link GedcomValidator} for usage information.
 * 
 * @author frizbog1
 * 
 */
public class IndividualValidator extends AbstractValidator {

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
		List<PersonalName> list = checkListStructure("names", true, individual, new ListRef<PersonalName>() {
			@Override
			public List<PersonalName> get(boolean initializeIfNeeded) {
				return individual.getNames(initializeIfNeeded);
			}
		});
		if (list != null) {
            for (PersonalName pn : list) {
                new PersonalNameValidator(getRootValidator(), pn).validate();
            }
		}
		
		boolean isRepairEnabled = isAutorepairEnabled();
        if (isRepairEnabled) {
        	eliminateDuplicatesWithInfo("families (where individual was a child)", individual, individual.getFamiliesWhereChild());
        	eliminateDuplicatesWithInfo("families (where individual was a spouse)", individual, individual.getFamiliesWhereSpouse());
        }

        checkAliases();
        checkAssociations();
        checkCitations(individual);
        checkIndividualAttributes();
        checkSubmitters();
        checkIndividualEvents();
    }

    /**
     * Validate the {@link Individual#aliases} collection
     * 
     */
    private void checkAliases() {
		List<StringWithCustomTags> list = checkListStructure("aliases", true, individual, new ListRef<StringWithCustomTags>() {
			@Override
			public List<StringWithCustomTags> get(boolean initializeIfNeeded) {
				return individual.getAliases(initializeIfNeeded);
			}
		});
		if (list != null) {
			checkStringTagList(list, "aliases", false);
		}
    }

    /**
     * Validate the {@link Individual#associations} collection
     */
    private void checkAssociations() {
		List<Association> list = checkListStructure("associations", true, individual, new ListRef<Association>() {
			@Override
			public List<Association> get(boolean initializeIfNeeded) {
				return individual.getAssociations(initializeIfNeeded);
			}
		});
		if (list != null) {
            for (Association a : list) {
                if (a == null) {
                    addError("associations collection for individual contains null entry", individual);
                } else {
                    checkRequiredString(a.getAssociatedEntityType(), "associated entity type", a);
                    checkXref(a, "association xref");
                }
            }
		}
    }

    /**
     * Validate the {@link Individual#attributes} collection
     */
    private void checkIndividualAttributes() {
		List<IndividualAttribute> list = checkListStructure("attributes", true, individual, new ListRef<IndividualAttribute>() {
			@Override
			public List<IndividualAttribute> get(boolean initializeIfNeeded) {
				return individual.getAttributes(initializeIfNeeded);
			}
		});
		if (list != null) {
			for (IndividualAttribute l : list) {
                if (l.getType() == null) {
                    addError("Individual attribute requires a type", l);
                }
			}
		}
    }

    /**
     * Validate the {@link Individual#events} collection
     */
    private void checkIndividualEvents() {
		List<IndividualEvent> list = checkListStructure("events", true, individual, new ListRef<IndividualEvent>() {
			@Override
			public List<IndividualEvent> get(boolean initializeIfNeeded) {
				return individual.getEvents(initializeIfNeeded);
			}
		});
		if (list != null) {
			for (IndividualEvent l : list) {
                if (l.getType() == null) {
                    addError("Individual event requires a type", l);
                }
                new EventValidator(getRootValidator(), l).validate();
			}
		}
    }

    /**
	 * Validate the two submitters collections: {@link Individual#ancestorInterest} and
	 * {@link Individual#descendantInterest}
	 */
	private void checkSubmitters() {
		checkSubmitters("ancestor interest", new ListRef<Submitter>() {
			@Override
			public List<Submitter> get(boolean initializeIfNeeded) {
				return individual.getAncestorInterest(initializeIfNeeded);
			}
		});
		
		checkSubmitters("descendant interest", new ListRef<Submitter>() {
			@Override
			public List<Submitter> get(boolean initializeIfNeeded) {
				return individual.getDescendantInterest(initializeIfNeeded);
			}
		});
	}

	/**
     * @param whichName ancestor or descendant interest list name
     * @param whichList ancestor or descendant interest list reference
     */
    private void checkSubmitters(String whichName, ListRef<Submitter> whichList) {
    	List<Submitter> list = checkListStructure(whichName, true, individual, whichList);
		if (list != null) {
			for (Submitter s : list) {
				new SubmitterValidator(getRootValidator(), s).validate();
			}
		}
    }
}
