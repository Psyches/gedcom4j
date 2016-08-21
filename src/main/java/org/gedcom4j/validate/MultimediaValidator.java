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

import org.gedcom4j.exception.GedcomValidationException;
import org.gedcom4j.model.AbstractCitation;
import org.gedcom4j.model.FileReference;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.GedcomVersion;
import org.gedcom4j.model.Header;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.SupportedVersion;
import org.gedcom4j.model.UserReference;

/**
 * A validator for {@link Multimedia} objects. See {@link GedcomValidator} for usage instructions.
 * 
 * @author frizbog1
 * 
 */
public class MultimediaValidator extends AbstractValidator {

    /**
     * The multimedia being validated
     */
    private final Multimedia mm;

    /**
     * The gedcom version to validate against. There are numerous differences in multimedia records between 5.5 and
     * 5.5.1.
     */
    private SupportedVersion gedcomVersion;

    /**
     * Constructor
     * 
     * @param rootValidator
     *            the root validator
     * @param multimedia
     *            the multimedia object being validated
     */
    public MultimediaValidator(GedcomValidator rootValidator, Multimedia multimedia) {
        super(rootValidator);
        if (rootValidator == null) {
            throw new GedcomValidationException("Root validator passed in to MultimediaValidator constructor was null");
        }
        mm = multimedia;
        Gedcom gedcom = rootValidator.getGedcom();
        Header header = gedcom == null ? null : gedcom.getHeader();
        GedcomVersion gv = header == null ? null : header.getGedcomVersion();
        if (gv == null || gv.getVersionNumber() == null) {
            if (isAutoRepairEnabled()) {
                gedcomVersion = SupportedVersion.V5_5_1;
                addInfo("Was not able to determine GEDCOM version - assuming 5.5.1", gedcom);
            } else {
                addError("Was not able to determine GEDCOM version - cannot validate multimedia objects", gedcom);
            }
        } else {
            gedcomVersion = gv.getVersionNumber();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        validateCommon();
        if (v551()) {
            validate551();
        } else {
            validate55();
        }
    }

    /**
     * Check a file reference
     * 
     * @param fr
     *            the file reference to check
     */
    private void checkFileReference(FileReference fr) {
        if (fr == null) {
        	addNullListError("FileReferences", "file references", mm);
            return;
        }
        checkRequiredString(fr.getFormat(), "format", fr);
        checkOptionalString(fr.getMediaType(), "media type", fr);
        checkOptionalString(fr.getTitle(), "title", fr);
        checkRequiredString(fr.getReferenceToFile(), "reference to file", fr);
    }

    /**
     * Check user references
     */
    private void checkUserReferences() {
		List<UserReference> userReferences = checkListStructure("user references", true, mm, new ListRef<UserReference>() {
			@Override
			public List<UserReference> get(boolean initializeIfNeeded) {
				return mm.getUserReferences(initializeIfNeeded);
			}
		});
		checkUserReferences(userReferences, mm);
    }

    /**
     * Check the xref field
     */
    private void checkXref() {
        // Xref is required
    	boolean isRepairEnabled = isAutoRepairEnabled();
        if (mm.getXref() == null || mm.getXref().trim().isEmpty()) {
        	addNullError(isRepairEnabled, "mandated xref", mm);
            return;
        }

        // Item should be found in map using the xref as the key
        if (getRootValidator().getGedcom().getMultimedia().get(mm.getXref()) != mm) {
            if (isRepairEnabled)
                getRootValidator().getGedcom().getMultimedia().put(mm.getXref(), mm);
            addProblemFinding(isRepairEnabled, "Multimedia object not keyed by xref in map", mm);
        }
    }

    /**
     * Check the blob
     */
    private void checkBlob() {
        // The blob object should always be instantiated, even for 5.5.1 (in
        // which case it should be an empty collection)

		checkListStructure("blob", false, mm, new ListRef<String>() {
			@Override
			public List<String> get(boolean initializeIfNeeded) {
				return mm.getBlob(initializeIfNeeded);
			}
		});
    }

    /**
     * Convenience method to determine if GEDCOM standard in use is v5.5.1
     * 
     * @return true if and only if GEDCOM standard in use is 5.5.1
     */
    private boolean v551() {
        return SupportedVersion.V5_5_1.equals(gedcomVersion);
    }

    /**
     * Validate that the multimedia object conforms to GEDCOM 5.5 rules
     */
    private void validate55() {
    	boolean isRepairEnabled = isAutoRepairEnabled();
        if (mm.getBlob() == null || mm.getBlob().isEmpty()) {
        	addNullError(isRepairEnabled, "blob", mm);
        }
        checkRequiredString(mm.getEmbeddedMediaFormat(), "embedded media format", mm);

        // Validate the citations - only allowed in 5.5.1
        if (mm.getCitations() != null && !mm.getCitations().isEmpty()) {
        	isRepairEnabled = isAutoRepairEnabled();
            if (isRepairEnabled)
                mm.getCitations(true).clear();
            addProblemFinding(isRepairEnabled, "Citations collection is populated but not allowed in gedcom v5.5", mm);
        }
    }

    /**
     * Validate that the multimedia object conforms to GEDCOM 5.5.1 rules
     */
    private void validate551() {
		List<FileReference> list = checkListStructure("file references", true, mm, new ListRef<FileReference>() {
			@Override
			public List<FileReference> get(boolean initializeIfNeeded) {
				return mm.getFileReferences(initializeIfNeeded);
			}
		});
		if (list != null) {
			for (FileReference fr : list) {
                checkFileReference(fr);
			}
		}

		// Blobs must be empty in 5.5.1
        if (mm.getBlob() != null && !mm.getBlob().isEmpty()) {
        	boolean isRepairEnabled = isAutoRepairEnabled();
            if (isRepairEnabled)
                mm.getBlob().clear();
			addProblemFinding(isRepairEnabled,
					"Embedded media object has a populated blob object, which is not allowed in GEDCOM 5.5.1", mm);
        }

        // Cannot have an embedded media format in 5.5.1
        if (mm.getEmbeddedMediaFormat() != null) {
        	boolean isRepairEnabled = isAutoRepairEnabled();
            if (isRepairEnabled)
                mm.setEmbeddedMediaFormat(null);
			addProblemFinding(isRepairEnabled,
					"Multimedia object has a format for embedded media, which is not allowed in GEDCOM 5.5.1", mm);

        }

        // Validate the citations - only allowed in 5.5.1
        if (mm.getCitations() != null) {
            for (AbstractCitation c : mm.getCitations()) {
                new CitationValidator(getRootValidator(), c).validate();
            }
        }

    }

    /**
     * Validate items that are common to both GEDCOM 5.5 and GEDCOM 5.5.1
     */
    private void validateCommon() {
        checkXref();
        checkOptionalString(mm.getRecIdNumber(), "record id number", mm);
        checkChangeDate(mm.getChangeDate(), mm);
        checkUserReferences();
        checkCitations(mm);
        if (mm.getContinuedObject() != null) {
            new MultimediaValidator(getRootValidator(), mm.getContinuedObject()).validate();
        }
        checkBlob();
        checkNotes(mm);
    }
}
