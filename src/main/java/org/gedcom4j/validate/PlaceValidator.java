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
        if (place.getPlaceName() == null) {
            addError("Place name was unspecified" + (isAutorepairEnabled() ? " and cannot be repaired" : ""));
        }
        checkPhonetic();
        checkRomanized();
        checkCitations(place);
        checkNotes(place);
        checkCustomTags(place);
    }

    private void checkPhonetic() {
		checkNameVariations("phonetic name variations", new ListRef<AbstractNameVariation>() {
			@Override
			public List<AbstractNameVariation> get(boolean initializeIfNeeded) {
				return place.getPhonetic(initializeIfNeeded);
			}
		});
    }
    
    private void checkRomanized() {
		checkNameVariations("romanized name variations", new ListRef<AbstractNameVariation>() {
			@Override
			public List<AbstractNameVariation> get(boolean initializeIfNeeded) {
				return place.getRomanized(initializeIfNeeded);
			}
		});
    }
    
    private void checkNameVariations(String name, ListRef<AbstractNameVariation> handler) {
		List<AbstractNameVariation> list = checkListStructure(name, true, place, handler);
		if (list != null) {
			for (AbstractNameVariation nv : list) {
                new NameVariationValidator(getRootValidator(), nv).validate();
			}
		}
    }
}
