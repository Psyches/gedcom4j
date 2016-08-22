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

import org.gedcom4j.model.ModelElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link GedcomValidationFinding}
 * 
 * @author frizbog1
 * 
 */
public class GedcomValidationFindingTest implements ModelElement {

	/**
     * Test for {@link GedcomValidationFinding#GedcomValidationFinding(String, Severity, ModelElement)} - nulls as parameters
     */
    @Test
    public void testGedcomValidationFinding1() {
        GedcomValidationFinding gvf = new GedcomValidationFinding(null, null, this);
        Assert.assertNotNull(gvf);
        Assert.assertNull(gvf.getProblemDescription());
        Assert.assertNull(gvf.getSeverity());
        Assert.assertSame(this, gvf.getItemWithProblem());
    }

    /**
     * Test for {@link GedcomValidationFinding#GedcomValidationFinding(String, Severity, ModelElement)} - values for
     * parameters
     */
    @Test
    public void testGedcomValidationFinding2() {
        GedcomValidationFinding gvf = new GedcomValidationFinding("testing 1 2 3", Severity.ERROR, this);
        Assert.assertNotNull(gvf);
        Assert.assertEquals("testing 1 2 3", gvf.getProblemDescription());
        Assert.assertEquals(Severity.ERROR, gvf.getSeverity());
        Assert.assertSame(this, gvf.getItemWithProblem());
    }

    /**
     * Test for {@link GedcomValidationFinding#toString()}
     */
    @Test
    public void testToString() {
        GedcomValidationFinding gvf = new GedcomValidationFinding("testing 1 2 3", Severity.ERROR, this /*Integer.valueOf(4)*/);
        Assert.assertEquals("ERROR: testing 1 2 3 (uh yeah...four)", gvf.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     * until ValidatedItem enforces wider semantics for problem location determination 
     */
    @Override
    public String toString() {
    	return "uh yeah...four";
    }
}
