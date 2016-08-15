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
import org.gedcom4j.exception.GedcomValidationException;
import org.gedcom4j.model.*;

/**
 * A validator for {@link Multimedia} objects. See {@link GedcomValidator} for usage instructions.
 * 
 * @author frizbog1
 * 
 */
class MultimediaValidator extends AbstractValidator {

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
        if (rootValidator.getGedcom() == null || rootValidator.getGedcom().getHeader() == null || rootValidator.getGedcom().getHeader().getGedcomVersion() == null
                || rootValidator.getGedcom().getHeader().getGedcomVersion().getVersionNumber() == null) {
            if (rootValidator.isAutorepairEnabled()) {
                gedcomVersion = SupportedVersion.V5_5_1;
                rootValidator.addInfo("Was not able to determine GEDCOM version - assuming 5.5.1", rootValidator.getGedcom());
            } else {
                rootValidator.addError("Was not able to determine GEDCOM version - cannot validate multimedia objects", rootValidator.getGedcom());
            }
        } else {
            gedcomVersion = rootValidator.getGedcom().getHeader().getGedcomVersion().getVersionNumber();
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
            if (getRootValidator().isAutorepairEnabled()) {
                addError("Null file reference in list of file references in multimedia object - cannot repair", mm);
            } else {
                addError("Null file reference in list of file references in multimedia object", mm);
            }
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
        List<UserReference> userReferences = mm.getUserReferences();
        if (Options.isCollectionInitializationEnabled() && userReferences == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getUserReferences(true).clear();
                getRootValidator().addInfo("List of user references on multimedia object was null - repaired", mm);
            } else {
                getRootValidator().addError("List of user references on multimedia object is null", mm);
                return;
            }
        }
        if (getRootValidator().isAutorepairEnabled()) {
            int dups = new DuplicateEliminator<UserReference>(userReferences).process();
            if (dups > 0) {
                getRootValidator().addInfo(dups + " duplicate user references found and removed", mm);
            }
        }
        if (userReferences != null) {
            for (UserReference u : userReferences) {
                checkCustomTags(u);
                if (u.getReferenceNum() == null) {
                    if (getRootValidator().isAutorepairEnabled()) {
                        addError("User reference is has a null or blank reference number - cannot repair", u);
                    } else {
                        addError("User reference is has a null or blank reference number", u);
                    }
                    continue;
                }
            }
        }
    }

    /**
     * Check the xref field
     */
    private void checkXref() {
        // Xref is required
        if (mm.getXref() == null || mm.getXref().trim().length() == 0) {
            if (getRootValidator().isAutorepairEnabled()) {
                addError("Multimedia object must have xref - cannot autorepair", mm);
            } else {
                addError("Multimedia object must have xref", mm);
            }
            return;
        }

        // Item should be found in map using the xref as the key
        if (getRootValidator().getGedcom().getMultimedia().get(mm.getXref()) != mm) {
            if (getRootValidator().isAutorepairEnabled()) {
                getRootValidator().getGedcom().getMultimedia().put(mm.getXref(), mm);
                getRootValidator().addInfo("Multimedia object not keyed by xref in map - repaired", mm);
            } else {
                getRootValidator().addError("Multimedia object not keyed by xref in map", mm);
            }
            return;
        }
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
        if (mm.getBlob() == null || mm.getBlob().isEmpty()) {
            if (getRootValidator().isAutorepairEnabled()) {
                addError("Embedded media object has an empty blob object - cannot repair", mm);
            } else {
                addError("Embedded media object has an empty blob object", mm);
            }
        }
        checkRequiredString(mm.getEmbeddedMediaFormat(), "embedded media format", mm);

        // Validate the citations - only allowed in 5.5.1
        if (mm.getCitations() != null && !mm.getCitations().isEmpty()) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getCitations(true).clear();
                getRootValidator().addInfo("Citations collection was populated, but not allowed in " + "v5.5 of gedcom - repaired (cleared)", mm);
            } else {
                getRootValidator().addError("Citations collection is populated, but not allowed in " + "v5.5 of gedcom", mm);
            }
        }
    }

    /**
     * Validate that the multimedia object conforms to GEDCOM 5.5.1 rules
     */
    private void validate551() {

        // File references
        if (Options.isCollectionInitializationEnabled() && mm.getFileReferences() == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getFileReferences(true).clear();
                getRootValidator().addInfo("Multimedia object did not have list of file references - repaired", mm);
            } else {
                getRootValidator().addError("Multimedia object does not have list of file references", mm);
                return;
            }
        }
        if (mm.getFileReferences() != null) {
            for (FileReference fr : mm.getFileReferences()) {
                checkFileReference(fr);
            }
        }

        // Blobs must be empty in 5.5.1
        if (mm.getBlob() != null && !mm.getBlob().isEmpty()) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getBlob().clear();
                addInfo("Embedded media object had a populated blob object, " + "which is not allowed in GEDCOM 5.5.1 - repaired (cleared)", mm);
            } else {
                addError("Embedded media object has a populated blob object, which is not allowed in GEDCOM 5.5.1", mm);
            }
        }

        // Cannot have an embedded media format in 5.5.1
        if (mm.getEmbeddedMediaFormat() != null) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.setEmbeddedMediaFormat(null);
                getRootValidator().addInfo("Multimedia object had a format for embedded media, " + "which is not allowed in GEDCOM 5.5.1 - repaired (cleared)", mm);
            } else {
                getRootValidator().addError("Multimedia object has a format for embedded media, " + "which is not allowed in GEDCOM 5.5.1", mm);
            }

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
        if (Options.isCollectionInitializationEnabled() && mm.getCitations() == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getCitations(true).clear();
                addInfo("citations collection for multimedia object was null - rootValidator.autorepaired", mm);
            } else {
                addError("citations collection for multimedia object is null", mm);
            }
        }
        if (mm.getContinuedObject() != null) {
            new MultimediaValidator(getRootValidator(), mm.getContinuedObject()).validate();
        }
        // The blob object should always be instantiated, even for 5.5.1 (in
        // which case it should be an empty collection)
        if (Options.isCollectionInitializationEnabled() && mm.getBlob() == null) {
            if (getRootValidator().isAutorepairEnabled()) {
                mm.getBlob(true).clear();
                getRootValidator().addInfo("Embedded blob was null - repaired", mm);
            } else {
                getRootValidator().addError("Embedded blob is null", mm);
            }
        }

        new NotesValidator(getRootValidator(), mm, mm.getNotes()).validate();
    }

}
