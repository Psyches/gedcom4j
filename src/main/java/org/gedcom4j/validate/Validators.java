/*
 * Copyright (c) 2016 Mark A. Sikes
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gedcom4j.exception.GedcomValidationException;

/**
 * @author Mark A Sikes
 */
final public class Validators {
	private static final Map<String, Validator> REGISTERED_VALIDATORS = new HashMap<String, Validator>();
	private static final List<Validator> CONFIGURED_VALIDATORS = new ArrayList<Validator>();
	
	/**
	 * Register a validator instance by name.
	 * 
	 * @param theName the name of the validator
	 * @param theValidator the validator instance.
	 * 
	 * @return the old validator if one was replaced with this new registration.
	 */
	public static Validator registerValidator(String theName, Validator theValidator) {
		Validator old = REGISTERED_VALIDATORS.put(theName, theValidator);
		CONFIGURED_VALIDATORS.remove(old);
		return old;
	}

	/**
	 * @return the entire, read-only map of registered validators.
	 */
	public static Map<String, Validator> getRegisteredValidators() {
		return Collections.unmodifiableMap(REGISTERED_VALIDATORS);
	}
	
	/**
	 * @param theName the name of the validator.
	 * @return the registered validator instance.
	 */
	public static Validator getRegisteredValidator(String theName) {
		return REGISTERED_VALIDATORS.get(theName);
	}

	/**
	 * @return the read-only list of configured validators.
	 */
	public static List<Validator> getConfiguredValidators() {
		return Collections.unmodifiableList(CONFIGURED_VALIDATORS);
	}

	/**
	 * @param theNames the validator names to configure.
	 */
	public static void addConfiguredValidators(String... theNames) {
		for (String name : theNames) {
			Validator v = getRegisteredValidator(name);
			if (v == null)
				throw new GedcomValidationException("No such validator exists: " + name);
			CONFIGURED_VALIDATORS.add(v);
		}
	}

	private Validators() {
	}
}
