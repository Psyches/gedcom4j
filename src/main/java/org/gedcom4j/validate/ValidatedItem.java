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

import org.gedcom4j.model.ModelElement;

/**
 * ValidatedItem allows an arbitrary object to appear in a Finding on the Gedcom
 * model tree. The class therefore simulates a model element as closely as
 * possible (but perhaps not perfectly).
 * 
 * @author Mark A Sikes
 */
public class ValidatedItem implements ModelElement {

	private final Object item;

	/**
	 * @param theItem
	 *            the relevant object.
	 */
	public ValidatedItem(Object theItem) {
		item = theItem;
	}

	/**
	 * @return the underlying object reference for this wrapper model element.
	 */
	public Object getItem() {
		return item;
	}
}
