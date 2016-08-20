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
import org.gedcom4j.model.AbstractNameVariation;
import org.gedcom4j.model.Place;

/**
 * Validator for {@link Place} objects
 * 
 * @author frizbog1
 * 
 */
public class PlaceValidator extends AbstractValidator {

    /**
     * The place being validated
     */
    private final Place place;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the gedcom validator that holds all the findings
     * @param place
     *            the {@link Place} begin validated
     */
    public PlaceValidator(GedcomValidator rootValidator, Place place) {
        super(rootValidator);
        this.place = place;
    }

    @Override
    protected void validate() {
        if (place == null) {
            addError("Place is null and cannot be validated or repaired");
            return;
        }
        checkOptionalString(place.getLatitude(), "latitude", place);
        checkOptionalString(place.getLongitude(), "longitude", place);
        checkOptionalString(place.getPlaceFormat(), "place format", place);
        checkCustomTags(place);
        checkCitations();
        checkPhonetic();
        checkRomanized();
        new NotesValidator(getRootValidator(), place).validate();
        if (place.getPlaceName() == null) {
            addError("Place name was unspecified" + (getRootValidator().isAutoRepairEnabled() ? " and cannot be repaired" : ""));
        }
    }

    private void checkCitations() {
		List<AbstractCitation> list = validateRepairStructure("Citations", "citations", true, place,
				new ListRef<AbstractCitation>() {
					@Override
					public List<AbstractCitation> get(boolean initializeIfNeeded) {
						return place.getCitations(initializeIfNeeded);
					}
				});

		if (list != null) {
			for (AbstractCitation c : list) {
				new CitationValidator(getRootValidator(), c).validate();
			}
		}
	}
    
    private void checkPhonetic() {
		checkNameVariations("Phonetics", "phonetic name variations", new ListRef<AbstractNameVariation>() {
			@Override
			public List<AbstractNameVariation> get(boolean initializeIfNeeded) {
				return place.getPhonetic(initializeIfNeeded);
			}
		});
    }
    
    private void checkRomanized() {
		checkNameVariations("Romanized", "romanized name variations", new ListRef<AbstractNameVariation>() {
			@Override
			public List<AbstractNameVariation> get(boolean initializeIfNeeded) {
				return place.getRomanized(initializeIfNeeded);
			}
		});
    }
    
    private void checkNameVariations(String v, String n, ListRef<AbstractNameVariation> handler) {
		List<AbstractNameVariation> list = validateRepairStructure(v, n, true, place, handler);
		if (list != null) {
			for (AbstractNameVariation nv : list) {
                new NameVariationValidator(getRootValidator(), nv).validate();
			}
		}
    }
}
