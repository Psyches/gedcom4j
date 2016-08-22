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

/**
 * A class that holds information about something wrong with the gedcom data validated by {@link GedcomValidator}.
 * 
 * @author frizbog1
 * 
 */
public class GedcomValidationFinding {
    /**
     * The object that had a problem with it
     */
    private final ModelElement itemWithProblem;

    /**
     * A description of the problem
     */
    private final String problemDescription;

    /**
     * How severe is the problem
     */
    private final Severity severity;

    /**
     * Constructor
     * 
     * @param description
     *            a description of the problem
     * @param findingSeverity
     *            the severity of the problem
     * @param obj
     *            the item that has the problem (if applicable)
     */
    GedcomValidationFinding(String description, Severity findingSeverity, ModelElement obj) {
        problemDescription = description;
        severity = findingSeverity;
        itemWithProblem = obj;
    }

    /**
     * Get the itemWithProblem
     * 
     * @return the itemWithProblem
     */
    public ModelElement getItemWithProblem() {
        return itemWithProblem;
    }

    /**
     * Get the problemDescription
     * 
     * @return the problemDescription
     */
    public String getProblemDescription() {
        return problemDescription;
    }

    /**
     * Get the severity
     * 
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(severity).append(": ").append(problemDescription);
        if (itemWithProblem != null) {
            sb.append(" (").append(itemWithProblem).append(")");
        }
        return sb.toString();
    }
}
