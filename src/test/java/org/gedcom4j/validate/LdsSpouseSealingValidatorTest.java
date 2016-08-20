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

import org.gedcom4j.model.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link LdsSpouseSealingValidator}
 * 
 * @author frizbog1
 */
@SuppressWarnings("PMD.SingularField")
public class LdsSpouseSealingValidatorTest extends AbstractValidatorTestCase {
    /**
     * The father in the family
     */
	private Individual dad;

    /**
     * The mother in the family
     */
    private Individual mom;

    /**
     * The child in the family
     */
    private Individual jr;

    /**
     * The family being validated
     */
    private Family f;

    /**
     * {@inheritDoc}
     */
    @Override
    @Before
    public void setUp() throws Exception {
        gedcom = TestHelper.getMinimalGedcom();
        super.setUp();
        rootValidator.setAutoRepairEnabled(false);

        dad = new Individual();
        dad.setXref("@I00001@");
        gedcom.getIndividuals().put(dad.getXref(), dad);

        mom = new Individual();
        mom.setXref("@I00002@");
        gedcom.getIndividuals().put(mom.getXref(), mom);

        jr = new Individual();
        jr.setXref("@I00003@");
        gedcom.getIndividuals().put(jr.getXref(), jr);

        f = new Family();
        f.setXref("@F0001@");
        f.setHusband(dad);
        f.setWife(mom);
        f.getChildren(true).add(jr);
        gedcom.getFamilies().put(f.getXref(), f);

        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when citations are messed up
     */
    @Test
    public void testCitations() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);

        s.getCitations(true).clear();
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when custom tags are messed up
     */
    @Test
    public void testCustomTags() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);

        s.getCustomTags(true).clear();
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when citations are messed up
     */
    @Test
    public void testDate() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);
        s.setDate(new StringWithCustomTags((String) null));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "date", "no value");

        s.setDate(new StringWithCustomTags(""));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "date", "no value");

        s.setDate(new StringWithCustomTags("              "));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "date", "no value");

        s.setDate(new StringWithCustomTags("Frying Pan"));
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when notes are messed up
     */
    @Test
    public void testNotes() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);

        s.getNotes(true).clear();
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when list of sealings is null
     */
    @Test
    public void testNullList() {
        f.getLdsSpouseSealings(true).clear();
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when citations are messed up
     */
    @Test
    public void testPlace() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);
        s.setPlace(new StringWithCustomTags((String) null));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "place", "no value");

        s.setPlace(new StringWithCustomTags(""));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "place", "no value");

        s.setPlace(new StringWithCustomTags("              "));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "place", "no value");

        s.setPlace(new StringWithCustomTags("Frying Pan"));
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when citations are messed up
     */
    @Test
    public void testStatus() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);
        s.setStatus(new StringWithCustomTags((String) null));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "status", "no value");

        s.setStatus(new StringWithCustomTags(""));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "status", "no value");

        s.setStatus(new StringWithCustomTags("              "));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "status", "no value");

        s.setStatus(new StringWithCustomTags("Frying Pan"));
        rootValidator.validate();
        assertNoIssues();
    }

    /**
     * Test when citations are messed up
     */
    @Test
    public void testTemple() {
        LdsSpouseSealing s = new LdsSpouseSealing();
        f.getLdsSpouseSealings(true).add(s);
        s.setTemple(new StringWithCustomTags((String) null));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "temple", "no value");

        s.setTemple(new StringWithCustomTags(""));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "temple", "no value");

        s.setTemple(new StringWithCustomTags("              "));
        rootValidator.validate();
        assertFindingsContain(Severity.ERROR, "temple", "no value");

        s.setTemple(new StringWithCustomTags("Frying Pan"));
        rootValidator.validate();
        assertNoIssues();
    }
}
