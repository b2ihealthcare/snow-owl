/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ImagingSelection2DGraphicType;
import org.linuxforhealth.fhir.model.r5.type.code.ImagingSelection3DGraphicType;
import org.linuxforhealth.fhir.model.r5.type.code.ImagingSelectionStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A selection of DICOM SOP instances and/or frames within a single Study and Series. This might include additional 
 * specifics such as an image region, an Observation UID or a Segmentation Number, allowing linkage to an Observation 
 * Resource or transferring this information along with the ImagingStudy Resource.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "imagingSelection-0",
    level = "Warning",
    location = "performer.function",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/series-performer-function",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/series-performer-function', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingSelection",
    generated = true
)
@Constraint(
    id = "imagingSelection-1",
    level = "Warning",
    location = "instance.sopClass",
    description = "SHALL, if possible, contain a code from value set http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1",
    expression = "$this.memberOf('http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingSelection",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ImagingSelection extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "ImagingSelectionStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the ImagingSelection.",
        valueSet = "http://hl7.org/fhir/ValueSet/imagingselection-status|5.0.0"
    )
    @Required
    private final ImagingSelectionStatus status;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Device", "Location", "Organization", "Procedure", "Practitioner", "Medication", "Substance", "Specimen" })
    private final Reference subject;
    @Summary
    private final Instant issued;
    @Summary
    private final List<Performer> performer;
    @Summary
    @ReferenceTarget({ "CarePlan", "ServiceRequest", "Appointment", "AppointmentResponse", "Task" })
    private final List<Reference> basedOn;
    @Summary
    @Binding(
        bindingName = "ImagingSelectionCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Key Object Selection Document Title.",
        valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_7010.html"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "ImagingSelectionCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Key Object Selection Document Title.",
        valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_7010.html"
    )
    @Required
    private final CodeableConcept code;
    @Summary
    private final Id studyUid;
    @Summary
    @ReferenceTarget({ "ImagingStudy", "DocumentReference" })
    private final List<Reference> derivedFrom;
    @Summary
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;
    @Summary
    private final Id seriesUid;
    @Summary
    private final UnsignedInt seriesNumber;
    @Summary
    private final Id frameOfReferenceUid;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final CodeableReference bodySite;
    @Summary
    @ReferenceTarget({ "ImagingSelection" })
    private final List<Reference> focus;
    @Summary
    private final List<Instance> instance;

    private ImagingSelection(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        subject = builder.subject;
        issued = builder.issued;
        performer = Collections.unmodifiableList(builder.performer);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        category = Collections.unmodifiableList(builder.category);
        code = builder.code;
        studyUid = builder.studyUid;
        derivedFrom = Collections.unmodifiableList(builder.derivedFrom);
        endpoint = Collections.unmodifiableList(builder.endpoint);
        seriesUid = builder.seriesUid;
        seriesNumber = builder.seriesNumber;
        frameOfReferenceUid = builder.frameOfReferenceUid;
        bodySite = builder.bodySite;
        focus = Collections.unmodifiableList(builder.focus);
        instance = Collections.unmodifiableList(builder.instance);
    }

    /**
     * A unique identifier assigned to this imaging selection.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The current state of the ImagingSelection resource. This is not the status of any ImagingStudy, ServiceRequest, or 
     * Task resources associated with the ImagingSelection.
     * 
     * @return
     *     An immutable object of type {@link ImagingSelectionStatus} that is non-null.
     */
    public ImagingSelectionStatus getStatus() {
        return status;
    }

    /**
     * The patient, or group of patients, location, device, organization, procedure or practitioner this imaging selection is 
     * about and into whose or what record the imaging selection is placed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The date and time this imaging selection was created.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getIssued() {
        return issued;
    }

    /**
     * Selector of the instances – human or machine.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * A list of the diagnostic requests that resulted in this imaging selection being performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Classifies the imaging selection.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Reason for referencing the selected content.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * The Study Instance UID for the DICOM Study from which the images were selected.
     * 
     * @return
     *     An immutable object of type {@link Id} that may be null.
     */
    public Id getStudyUid() {
        return studyUid;
    }

    /**
     * The imaging study from which the imaging selection is made.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * The network service providing retrieval access to the selected images, frames, etc. See implementation notes for 
     * information about using DICOM endpoints.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEndpoint() {
        return endpoint;
    }

    /**
     * The Series Instance UID for the DICOM Series from which the images were selected.
     * 
     * @return
     *     An immutable object of type {@link Id} that may be null.
     */
    public Id getSeriesUid() {
        return seriesUid;
    }

    /**
     * The Series Number for the DICOM Series from which the images were selected.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getSeriesNumber() {
        return seriesNumber;
    }

    /**
     * The Frame of Reference UID identifying the coordinate system that conveys spatial and/or temporal information for the 
     * selected images or frames.
     * 
     * @return
     *     An immutable object of type {@link Id} that may be null.
     */
    public Id getFrameOfReferenceUid() {
        return frameOfReferenceUid;
    }

    /**
     * The anatomic structures examined. See DICOM Part 16 Annex L (http://dicom.nema.
     * org/medical/dicom/current/output/chtml/part16/chapter_L.html) for DICOM to SNOMED-CT mappings.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getBodySite() {
        return bodySite;
    }

    /**
     * The actual focus of an observation when it is not the patient of record representing something or someone associated 
     * with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record. The 
     * focus of an observation could also be an existing condition, an intervention, the subject's diet, another observation 
     * of the subject, or a body structure such as tumor or implanted device. An example use case would be using the 
     * Observation resource to capture whether the mother is trained to change her child's tracheostomy tube. In this 
     * example, the child is the patient of record and the mother is the focus.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getFocus() {
        return focus;
    }

    /**
     * Each imaging selection includes one or more selected DICOM SOP instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Instance} that may be empty.
     */
    public List<Instance> getInstance() {
        return instance;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (subject != null) || 
            (issued != null) || 
            !performer.isEmpty() || 
            !basedOn.isEmpty() || 
            !category.isEmpty() || 
            (code != null) || 
            (studyUid != null) || 
            !derivedFrom.isEmpty() || 
            !endpoint.isEmpty() || 
            (seriesUid != null) || 
            (seriesNumber != null) || 
            (frameOfReferenceUid != null) || 
            (bodySite != null) || 
            !focus.isEmpty() || 
            !instance.isEmpty();
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(meta, "meta", visitor);
                accept(implicitRules, "implicitRules", visitor);
                accept(language, "language", visitor);
                accept(text, "text", visitor);
                accept(contained, "contained", visitor, Resource.class);
                accept(extension, "extension", visitor, Extension.class);
                accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(status, "status", visitor);
                accept(subject, "subject", visitor);
                accept(issued, "issued", visitor);
                accept(performer, "performer", visitor, Performer.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(studyUid, "studyUid", visitor);
                accept(derivedFrom, "derivedFrom", visitor, Reference.class);
                accept(endpoint, "endpoint", visitor, Reference.class);
                accept(seriesUid, "seriesUid", visitor);
                accept(seriesNumber, "seriesNumber", visitor);
                accept(frameOfReferenceUid, "frameOfReferenceUid", visitor);
                accept(bodySite, "bodySite", visitor);
                accept(focus, "focus", visitor, Reference.class);
                accept(instance, "instance", visitor, Instance.class);
            }
            visitor.visitEnd(elementName, elementIndex, this);
            visitor.postVisit(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImagingSelection other = (ImagingSelection) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(issued, other.issued) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(studyUid, other.studyUid) && 
            Objects.equals(derivedFrom, other.derivedFrom) && 
            Objects.equals(endpoint, other.endpoint) && 
            Objects.equals(seriesUid, other.seriesUid) && 
            Objects.equals(seriesNumber, other.seriesNumber) && 
            Objects.equals(frameOfReferenceUid, other.frameOfReferenceUid) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(instance, other.instance);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                meta, 
                implicitRules, 
                language, 
                text, 
                contained, 
                extension, 
                modifierExtension, 
                identifier, 
                status, 
                subject, 
                issued, 
                performer, 
                basedOn, 
                category, 
                code, 
                studyUid, 
                derivedFrom, 
                endpoint, 
                seriesUid, 
                seriesNumber, 
                frameOfReferenceUid, 
                bodySite, 
                focus, 
                instance);
            hashCode = result;
        }
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends DomainResource.Builder {
        private List<Identifier> identifier = new ArrayList<>();
        private ImagingSelectionStatus status;
        private Reference subject;
        private Instant issued;
        private List<Performer> performer = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept code;
        private Id studyUid;
        private List<Reference> derivedFrom = new ArrayList<>();
        private List<Reference> endpoint = new ArrayList<>();
        private Id seriesUid;
        private UnsignedInt seriesNumber;
        private Id frameOfReferenceUid;
        private CodeableReference bodySite;
        private List<Reference> focus = new ArrayList<>();
        private List<Instance> instance = new ArrayList<>();

        private Builder() {
            super();
        }

        /**
         * The logical id of the resource, as used in the URL for the resource. Once assigned, this value never changes.
         * 
         * @param id
         *     Logical id of this artifact
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        /**
         * The metadata about the resource. This is content that is maintained by the infrastructure. Changes to the content 
         * might not always be associated with version changes to the resource.
         * 
         * @param meta
         *     Metadata about the resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder meta(Meta meta) {
            return (Builder) super.meta(meta);
        }

        /**
         * A reference to a set of rules that were followed when the resource was constructed, and which must be understood when 
         * processing the content. Often, this is a reference to an implementation guide that defines the special rules along 
         * with other profiles etc.
         * 
         * @param implicitRules
         *     A set of rules under which this content was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder implicitRules(Uri implicitRules) {
            return (Builder) super.implicitRules(implicitRules);
        }

        /**
         * The base language in which the resource is written.
         * 
         * @param language
         *     Language of the resource content
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder language(Code language) {
            return (Builder) super.language(language);
        }

        /**
         * A human-readable narrative that contains a summary of the resource and can be used to represent the content of the 
         * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
         * detail to make it "clinically safe" for a human to just read the narrative. Resource definitions may define what 
         * content should be represented in the narrative to ensure clinical safety.
         * 
         * @param text
         *     Text summary of the resource, for human interpretation
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder text(Narrative text) {
            return (Builder) super.text(text);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder contained(Resource... contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder contained(Collection<Resource> contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder modifierExtension(Extension... modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder modifierExtension(Collection<Extension> modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * A unique identifier assigned to this imaging selection.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for Imaging Selection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier... identifier) {
            for (Identifier value : identifier) {
                this.identifier.add(value);
            }
            return this;
        }

        /**
         * A unique identifier assigned to this imaging selection.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for Imaging Selection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder identifier(Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * The current state of the ImagingSelection resource. This is not the status of any ImagingStudy, ServiceRequest, or 
         * Task resources associated with the ImagingSelection.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     available | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ImagingSelectionStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The patient, or group of patients, location, device, organization, procedure or practitioner this imaging selection is 
         * about and into whose or what record the imaging selection is placed.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Device}</li>
         * <li>{@link Location}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link Medication}</li>
         * <li>{@link Substance}</li>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param subject
         *     Subject of the selected instances
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Convenience method for setting {@code issued}.
         * 
         * @param issued
         *     Date / Time when this imaging selection was created
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #issued(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder issued(java.time.ZonedDateTime issued) {
            this.issued = (issued == null) ? null : Instant.of(issued);
            return this;
        }

        /**
         * The date and time this imaging selection was created.
         * 
         * @param issued
         *     Date / Time when this imaging selection was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder issued(Instant issued) {
            this.issued = issued;
            return this;
        }

        /**
         * Selector of the instances – human or machine.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Selector of the instances (human or machine)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(Performer... performer) {
            for (Performer value : performer) {
                this.performer.add(value);
            }
            return this;
        }

        /**
         * Selector of the instances – human or machine.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Selector of the instances (human or machine)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder performer(Collection<Performer> performer) {
            this.performer = new ArrayList<>(performer);
            return this;
        }

        /**
         * A list of the diagnostic requests that resulted in this imaging selection being performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Associated request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder basedOn(Reference... basedOn) {
            for (Reference value : basedOn) {
                this.basedOn.add(value);
            }
            return this;
        }

        /**
         * A list of the diagnostic requests that resulted in this imaging selection being performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Associated request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder basedOn(Collection<Reference> basedOn) {
            this.basedOn = new ArrayList<>(basedOn);
            return this;
        }

        /**
         * Classifies the imaging selection.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classifies the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(CodeableConcept... category) {
            for (CodeableConcept value : category) {
                this.category.add(value);
            }
            return this;
        }

        /**
         * Classifies the imaging selection.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classifies the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder category(Collection<CodeableConcept> category) {
            this.category = new ArrayList<>(category);
            return this;
        }

        /**
         * Reason for referencing the selected content.
         * 
         * <p>This element is required.
         * 
         * @param code
         *     Imaging Selection purpose text or code
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * The Study Instance UID for the DICOM Study from which the images were selected.
         * 
         * @param studyUid
         *     DICOM Study Instance UID
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder studyUid(Id studyUid) {
            this.studyUid = studyUid;
            return this;
        }

        /**
         * The imaging study from which the imaging selection is made.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ImagingStudy}</li>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param derivedFrom
         *     The imaging study from which the imaging selection is derived
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFrom(Reference... derivedFrom) {
            for (Reference value : derivedFrom) {
                this.derivedFrom.add(value);
            }
            return this;
        }

        /**
         * The imaging study from which the imaging selection is made.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ImagingStudy}</li>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param derivedFrom
         *     The imaging study from which the imaging selection is derived
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFrom(Collection<Reference> derivedFrom) {
            this.derivedFrom = new ArrayList<>(derivedFrom);
            return this;
        }

        /**
         * The network service providing retrieval access to the selected images, frames, etc. See implementation notes for 
         * information about using DICOM endpoints.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     The network service providing retrieval for the images referenced in the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder endpoint(Reference... endpoint) {
            for (Reference value : endpoint) {
                this.endpoint.add(value);
            }
            return this;
        }

        /**
         * The network service providing retrieval access to the selected images, frames, etc. See implementation notes for 
         * information about using DICOM endpoints.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     The network service providing retrieval for the images referenced in the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder endpoint(Collection<Reference> endpoint) {
            this.endpoint = new ArrayList<>(endpoint);
            return this;
        }

        /**
         * The Series Instance UID for the DICOM Series from which the images were selected.
         * 
         * @param seriesUid
         *     DICOM Series Instance UID
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder seriesUid(Id seriesUid) {
            this.seriesUid = seriesUid;
            return this;
        }

        /**
         * The Series Number for the DICOM Series from which the images were selected.
         * 
         * @param seriesNumber
         *     DICOM Series Number
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder seriesNumber(UnsignedInt seriesNumber) {
            this.seriesNumber = seriesNumber;
            return this;
        }

        /**
         * The Frame of Reference UID identifying the coordinate system that conveys spatial and/or temporal information for the 
         * selected images or frames.
         * 
         * @param frameOfReferenceUid
         *     The Frame of Reference UID for the selected images
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder frameOfReferenceUid(Id frameOfReferenceUid) {
            this.frameOfReferenceUid = frameOfReferenceUid;
            return this;
        }

        /**
         * The anatomic structures examined. See DICOM Part 16 Annex L (http://dicom.nema.
         * org/medical/dicom/current/output/chtml/part16/chapter_L.html) for DICOM to SNOMED-CT mappings.
         * 
         * @param bodySite
         *     Body part examined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableReference bodySite) {
            this.bodySite = bodySite;
            return this;
        }

        /**
         * The actual focus of an observation when it is not the patient of record representing something or someone associated 
         * with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record. The 
         * focus of an observation could also be an existing condition, an intervention, the subject's diet, another observation 
         * of the subject, or a body structure such as tumor or implanted device. An example use case would be using the 
         * Observation resource to capture whether the mother is trained to change her child's tracheostomy tube. In this 
         * example, the child is the patient of record and the mother is the focus.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ImagingSelection}</li>
         * </ul>
         * 
         * @param focus
         *     Related resource that is the focus for the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focus(Reference... focus) {
            for (Reference value : focus) {
                this.focus.add(value);
            }
            return this;
        }

        /**
         * The actual focus of an observation when it is not the patient of record representing something or someone associated 
         * with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record. The 
         * focus of an observation could also be an existing condition, an intervention, the subject's diet, another observation 
         * of the subject, or a body structure such as tumor or implanted device. An example use case would be using the 
         * Observation resource to capture whether the mother is trained to change her child's tracheostomy tube. In this 
         * example, the child is the patient of record and the mother is the focus.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ImagingSelection}</li>
         * </ul>
         * 
         * @param focus
         *     Related resource that is the focus for the imaging selection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder focus(Collection<Reference> focus) {
            this.focus = new ArrayList<>(focus);
            return this;
        }

        /**
         * Each imaging selection includes one or more selected DICOM SOP instances.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instance
         *     The selected instances
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instance(Instance... instance) {
            for (Instance value : instance) {
                this.instance.add(value);
            }
            return this;
        }

        /**
         * Each imaging selection includes one or more selected DICOM SOP instances.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instance
         *     The selected instances
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instance(Collection<Instance> instance) {
            this.instance = new ArrayList<>(instance);
            return this;
        }

        /**
         * Build the {@link ImagingSelection}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>code</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ImagingSelection}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ImagingSelection per the base specification
         */
        @Override
        public ImagingSelection build() {
            ImagingSelection imagingSelection = new ImagingSelection(this);
            if (validating) {
                validate(imagingSelection);
            }
            return imagingSelection;
        }

        protected void validate(ImagingSelection imagingSelection) {
            super.validate(imagingSelection);
            ValidationSupport.checkList(imagingSelection.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(imagingSelection.status, "status");
            ValidationSupport.checkList(imagingSelection.performer, "performer", Performer.class);
            ValidationSupport.checkList(imagingSelection.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(imagingSelection.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(imagingSelection.code, "code");
            ValidationSupport.checkList(imagingSelection.derivedFrom, "derivedFrom", Reference.class);
            ValidationSupport.checkList(imagingSelection.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkList(imagingSelection.focus, "focus", Reference.class);
            ValidationSupport.checkList(imagingSelection.instance, "instance", Instance.class);
            ValidationSupport.checkReferenceType(imagingSelection.subject, "subject", "Patient", "Group", "Device", "Location", "Organization", "Procedure", "Practitioner", "Medication", "Substance", "Specimen");
            ValidationSupport.checkReferenceType(imagingSelection.basedOn, "basedOn", "CarePlan", "ServiceRequest", "Appointment", "AppointmentResponse", "Task");
            ValidationSupport.checkReferenceType(imagingSelection.derivedFrom, "derivedFrom", "ImagingStudy", "DocumentReference");
            ValidationSupport.checkReferenceType(imagingSelection.endpoint, "endpoint", "Endpoint");
            ValidationSupport.checkReferenceType(imagingSelection.focus, "focus", "ImagingSelection");
        }

        protected Builder from(ImagingSelection imagingSelection) {
            super.from(imagingSelection);
            identifier.addAll(imagingSelection.identifier);
            status = imagingSelection.status;
            subject = imagingSelection.subject;
            issued = imagingSelection.issued;
            performer.addAll(imagingSelection.performer);
            basedOn.addAll(imagingSelection.basedOn);
            category.addAll(imagingSelection.category);
            code = imagingSelection.code;
            studyUid = imagingSelection.studyUid;
            derivedFrom.addAll(imagingSelection.derivedFrom);
            endpoint.addAll(imagingSelection.endpoint);
            seriesUid = imagingSelection.seriesUid;
            seriesNumber = imagingSelection.seriesNumber;
            frameOfReferenceUid = imagingSelection.frameOfReferenceUid;
            bodySite = imagingSelection.bodySite;
            focus.addAll(imagingSelection.focus);
            instance.addAll(imagingSelection.instance);
            return this;
        }
    }

    /**
     * Selector of the instances – human or machine.
     */
    public static class Performer extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "EventPerformerFunction",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The type of involvement of the performer.",
            valueSet = "http://hl7.org/fhir/ValueSet/series-performer-function"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Device", "Organization", "CareTeam", "Patient", "RelatedPerson", "HealthcareService" })
        private final Reference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Distinguishes the type of involvement of the performer.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Author – human or machine.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getActor() {
            return actor;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (function != null) || 
                (actor != null);
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(function, "function", visitor);
                    accept(actor, "actor", visitor);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Performer other = (Performer) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(function, other.function) && 
                Objects.equals(actor, other.actor);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    function, 
                    actor);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private CodeableConcept function;
            private Reference actor;

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * Distinguishes the type of involvement of the performer.
             * 
             * @param function
             *     Type of performer
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Author – human or machine.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Device}</li>
             * <li>{@link Organization}</li>
             * <li>{@link CareTeam}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link HealthcareService}</li>
             * </ul>
             * 
             * @param actor
             *     Author (human or machine)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
                this.actor = actor;
                return this;
            }

            /**
             * Build the {@link Performer}
             * 
             * @return
             *     An immutable object of type {@link Performer}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Performer per the base specification
             */
            @Override
            public Performer build() {
                Performer performer = new Performer(this);
                if (validating) {
                    validate(performer);
                }
                return performer;
            }

            protected void validate(Performer performer) {
                super.validate(performer);
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Device", "Organization", "CareTeam", "Patient", "RelatedPerson", "HealthcareService");
                ValidationSupport.requireValueOrChildren(performer);
            }

            protected Builder from(Performer performer) {
                super.from(performer);
                function = performer.function;
                actor = performer.actor;
                return this;
            }
        }
    }

    /**
     * Each imaging selection includes one or more selected DICOM SOP instances.
     */
    public static class Instance extends BackboneElement {
        @Summary
        @Required
        private final Id uid;
        @Summary
        private final UnsignedInt number;
        @Binding(
            bindingName = "sopClass",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "DICOM SOP Classes.",
            valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1"
        )
        private final Coding sopClass;
        private final List<String> subset;
        private final List<ImageRegion2D> imageRegion2D;
        private final List<ImageRegion3D> imageRegion3D;

        private Instance(Builder builder) {
            super(builder);
            uid = builder.uid;
            number = builder.number;
            sopClass = builder.sopClass;
            subset = Collections.unmodifiableList(builder.subset);
            imageRegion2D = Collections.unmodifiableList(builder.imageRegion2D);
            imageRegion3D = Collections.unmodifiableList(builder.imageRegion3D);
        }

        /**
         * The SOP Instance UID for the selected DICOM instance.
         * 
         * @return
         *     An immutable object of type {@link Id} that is non-null.
         */
        public Id getUid() {
            return uid;
        }

        /**
         * The Instance Number for the selected DICOM instance.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getNumber() {
            return number;
        }

        /**
         * The SOP Class UID for the selected DICOM instance.
         * 
         * @return
         *     An immutable object of type {@link Coding} that may be null.
         */
        public Coding getSopClass() {
            return sopClass;
        }

        /**
         * Selected subset of the SOP Instance. The content and format of the subset item is determined by the SOP Class of the 
         * selected instance.
 May be one of:
 - A list of frame numbers selected from a multiframe SOP Instance.
 - A list of 
         * Content Item Observation UID values selected from a DICOM SR or other structured document SOP Instance.
 - A list of 
         * segment numbers selected from a segmentation SOP Instance.
 - A list of Region of Interest (ROI) numbers selected from 
         * a radiotherapy structure set SOP Instance.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
         */
        public List<String> getSubset() {
            return subset;
        }

        /**
         * Each imaging selection instance or frame list might includes an image region, specified by a region type and a set of 
         * 2D coordinates.
 If the parent imagingSelection.instance contains a subset element of type frame, the image region 
         * applies to all frames in the subset list.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link ImageRegion2D} that may be empty.
         */
        public List<ImageRegion2D> getImageRegion2D() {
            return imageRegion2D;
        }

        /**
         * Each imaging selection might includes a 3D image region, specified by a region type and a set of 3D coordinates.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link ImageRegion3D} that may be empty.
         */
        public List<ImageRegion3D> getImageRegion3D() {
            return imageRegion3D;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (uid != null) || 
                (number != null) || 
                (sopClass != null) || 
                !subset.isEmpty() || 
                !imageRegion2D.isEmpty() || 
                !imageRegion3D.isEmpty();
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(uid, "uid", visitor);
                    accept(number, "number", visitor);
                    accept(sopClass, "sopClass", visitor);
                    accept(subset, "subset", visitor, String.class);
                    accept(imageRegion2D, "imageRegion2D", visitor, ImageRegion2D.class);
                    accept(imageRegion3D, "imageRegion3D", visitor, ImageRegion3D.class);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Instance other = (Instance) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(uid, other.uid) && 
                Objects.equals(number, other.number) && 
                Objects.equals(sopClass, other.sopClass) && 
                Objects.equals(subset, other.subset) && 
                Objects.equals(imageRegion2D, other.imageRegion2D) && 
                Objects.equals(imageRegion3D, other.imageRegion3D);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    uid, 
                    number, 
                    sopClass, 
                    subset, 
                    imageRegion2D, 
                    imageRegion3D);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private Id uid;
            private UnsignedInt number;
            private Coding sopClass;
            private List<String> subset = new ArrayList<>();
            private List<ImageRegion2D> imageRegion2D = new ArrayList<>();
            private List<ImageRegion3D> imageRegion3D = new ArrayList<>();

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The SOP Instance UID for the selected DICOM instance.
             * 
             * <p>This element is required.
             * 
             * @param uid
             *     DICOM SOP Instance UID
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder uid(Id uid) {
                this.uid = uid;
                return this;
            }

            /**
             * The Instance Number for the selected DICOM instance.
             * 
             * @param number
             *     DICOM Instance Number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder number(UnsignedInt number) {
                this.number = number;
                return this;
            }

            /**
             * The SOP Class UID for the selected DICOM instance.
             * 
             * @param sopClass
             *     DICOM SOP Class UID
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder sopClass(Coding sopClass) {
                this.sopClass = sopClass;
                return this;
            }

            /**
             * Convenience method for setting {@code subset}.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param subset
             *     The selected subset of the SOP Instance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #subset(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder subset(java.lang.String... subset) {
                for (java.lang.String value : subset) {
                    this.subset.add((value == null) ? null : String.of(value));
                }
                return this;
            }

            /**
             * Selected subset of the SOP Instance. The content and format of the subset item is determined by the SOP Class of the 
             * selected instance.
 May be one of:
 - A list of frame numbers selected from a multiframe SOP Instance.
 - A list of 
             * Content Item Observation UID values selected from a DICOM SR or other structured document SOP Instance.
 - A list of 
             * segment numbers selected from a segmentation SOP Instance.
 - A list of Region of Interest (ROI) numbers selected from 
             * a radiotherapy structure set SOP Instance.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param subset
             *     The selected subset of the SOP Instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subset(String... subset) {
                for (String value : subset) {
                    this.subset.add(value);
                }
                return this;
            }

            /**
             * Selected subset of the SOP Instance. The content and format of the subset item is determined by the SOP Class of the 
             * selected instance.
 May be one of:
 - A list of frame numbers selected from a multiframe SOP Instance.
 - A list of 
             * Content Item Observation UID values selected from a DICOM SR or other structured document SOP Instance.
 - A list of 
             * segment numbers selected from a segmentation SOP Instance.
 - A list of Region of Interest (ROI) numbers selected from 
             * a radiotherapy structure set SOP Instance.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param subset
             *     The selected subset of the SOP Instance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder subset(Collection<String> subset) {
                this.subset = new ArrayList<>(subset);
                return this;
            }

            /**
             * Each imaging selection instance or frame list might includes an image region, specified by a region type and a set of 
             * 2D coordinates.
 If the parent imagingSelection.instance contains a subset element of type frame, the image region 
             * applies to all frames in the subset list.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param imageRegion2D
             *     A specific 2D region in a DICOM image / frame
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder imageRegion2D(ImageRegion2D... imageRegion2D) {
                for (ImageRegion2D value : imageRegion2D) {
                    this.imageRegion2D.add(value);
                }
                return this;
            }

            /**
             * Each imaging selection instance or frame list might includes an image region, specified by a region type and a set of 
             * 2D coordinates.
 If the parent imagingSelection.instance contains a subset element of type frame, the image region 
             * applies to all frames in the subset list.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param imageRegion2D
             *     A specific 2D region in a DICOM image / frame
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder imageRegion2D(Collection<ImageRegion2D> imageRegion2D) {
                this.imageRegion2D = new ArrayList<>(imageRegion2D);
                return this;
            }

            /**
             * Each imaging selection might includes a 3D image region, specified by a region type and a set of 3D coordinates.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param imageRegion3D
             *     A specific 3D region in a DICOM frame of reference
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder imageRegion3D(ImageRegion3D... imageRegion3D) {
                for (ImageRegion3D value : imageRegion3D) {
                    this.imageRegion3D.add(value);
                }
                return this;
            }

            /**
             * Each imaging selection might includes a 3D image region, specified by a region type and a set of 3D coordinates.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param imageRegion3D
             *     A specific 3D region in a DICOM frame of reference
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder imageRegion3D(Collection<ImageRegion3D> imageRegion3D) {
                this.imageRegion3D = new ArrayList<>(imageRegion3D);
                return this;
            }

            /**
             * Build the {@link Instance}
             * 
             * <p>Required elements:
             * <ul>
             * <li>uid</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Instance}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Instance per the base specification
             */
            @Override
            public Instance build() {
                Instance instance = new Instance(this);
                if (validating) {
                    validate(instance);
                }
                return instance;
            }

            protected void validate(Instance instance) {
                super.validate(instance);
                ValidationSupport.requireNonNull(instance.uid, "uid");
                ValidationSupport.checkList(instance.subset, "subset", String.class);
                ValidationSupport.checkList(instance.imageRegion2D, "imageRegion2D", ImageRegion2D.class);
                ValidationSupport.checkList(instance.imageRegion3D, "imageRegion3D", ImageRegion3D.class);
                ValidationSupport.requireValueOrChildren(instance);
            }

            protected Builder from(Instance instance) {
                super.from(instance);
                uid = instance.uid;
                number = instance.number;
                sopClass = instance.sopClass;
                subset.addAll(instance.subset);
                imageRegion2D.addAll(instance.imageRegion2D);
                imageRegion3D.addAll(instance.imageRegion3D);
                return this;
            }
        }

        /**
         * Each imaging selection instance or frame list might includes an image region, specified by a region type and a set of 
         * 2D coordinates.
 If the parent imagingSelection.instance contains a subset element of type frame, the image region 
         * applies to all frames in the subset list.
         */
        public static class ImageRegion2D extends BackboneElement {
            @Binding(
                bindingName = "ImagingSelection2DGraphicType",
                strength = BindingStrength.Value.REQUIRED,
                description = "The type of image region.",
                valueSet = "http://hl7.org/fhir/ValueSet/imagingselection-2dgraphictype|5.0.0"
            )
            @Required
            private final ImagingSelection2DGraphicType regionType;
            @Required
            private final List<Decimal> coordinate;

            private ImageRegion2D(Builder builder) {
                super(builder);
                regionType = builder.regionType;
                coordinate = Collections.unmodifiableList(builder.coordinate);
            }

            /**
             * Specifies the type of image region.
             * 
             * @return
             *     An immutable object of type {@link ImagingSelection2DGraphicType} that is non-null.
             */
            public ImagingSelection2DGraphicType getRegionType() {
                return regionType;
            }

            /**
             * The coordinates describing the image region. Encoded as a set of (column, row) pairs that denote positions in the 
             * selected image / frames specified with sub-pixel resolution.
 The origin at the TLHC of the TLHC pixel is 0.0\0.0, the 
             * BRHC of the TLHC pixel is 1.0\1.0, and the BRHC of the BRHC pixel is the number of columns\rows in the image / frames. 
             * The values must be within the range 0\0 to the number of columns\rows in the image / frames.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Decimal} that is non-empty.
             */
            public List<Decimal> getCoordinate() {
                return coordinate;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (regionType != null) || 
                    !coordinate.isEmpty();
            }

            @Override
            public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
                if (visitor.preVisit(this)) {
                    visitor.visitStart(elementName, elementIndex, this);
                    if (visitor.visit(elementName, elementIndex, this)) {
                        // visit children
                        accept(id, "id", visitor);
                        accept(extension, "extension", visitor, Extension.class);
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(regionType, "regionType", visitor);
                        accept(coordinate, "coordinate", visitor, Decimal.class);
                    }
                    visitor.visitEnd(elementName, elementIndex, this);
                    visitor.postVisit(this);
                }
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                ImageRegion2D other = (ImageRegion2D) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(regionType, other.regionType) && 
                    Objects.equals(coordinate, other.coordinate);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        regionType, 
                        coordinate);
                    hashCode = result;
                }
                return result;
            }

            @Override
            public Builder toBuilder() {
                return new Builder().from(this);
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder extends BackboneElement.Builder {
                private ImagingSelection2DGraphicType regionType;
                private List<Decimal> coordinate = new ArrayList<>();

                private Builder() {
                    super();
                }

                /**
                 * Unique id for the element within a resource (for internal references). This may be any string value that does not 
                 * contain spaces.
                 * 
                 * @param id
                 *     Unique id for inter-element referencing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder id(java.lang.String id) {
                    return (Builder) super.id(id);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder extension(Extension... extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder extension(Collection<Extension> extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder modifierExtension(Extension... modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder modifierExtension(Collection<Extension> modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * Specifies the type of image region.
                 * 
                 * <p>This element is required.
                 * 
                 * @param regionType
                 *     point | polyline | interpolated | circle | ellipse
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder regionType(ImagingSelection2DGraphicType regionType) {
                    this.regionType = regionType;
                    return this;
                }

                /**
                 * The coordinates describing the image region. Encoded as a set of (column, row) pairs that denote positions in the 
                 * selected image / frames specified with sub-pixel resolution.
 The origin at the TLHC of the TLHC pixel is 0.0\0.0, the 
                 * BRHC of the TLHC pixel is 1.0\1.0, and the BRHC of the BRHC pixel is the number of columns\rows in the image / frames. 
                 * The values must be within the range 0\0 to the number of columns\rows in the image / frames.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param coordinate
                 *     Specifies the coordinates that define the image region
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder coordinate(Decimal... coordinate) {
                    for (Decimal value : coordinate) {
                        this.coordinate.add(value);
                    }
                    return this;
                }

                /**
                 * The coordinates describing the image region. Encoded as a set of (column, row) pairs that denote positions in the 
                 * selected image / frames specified with sub-pixel resolution.
 The origin at the TLHC of the TLHC pixel is 0.0\0.0, the 
                 * BRHC of the TLHC pixel is 1.0\1.0, and the BRHC of the BRHC pixel is the number of columns\rows in the image / frames. 
                 * The values must be within the range 0\0 to the number of columns\rows in the image / frames.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param coordinate
                 *     Specifies the coordinates that define the image region
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder coordinate(Collection<Decimal> coordinate) {
                    this.coordinate = new ArrayList<>(coordinate);
                    return this;
                }

                /**
                 * Build the {@link ImageRegion2D}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>regionType</li>
                 * <li>coordinate</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link ImageRegion2D}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid ImageRegion2D per the base specification
                 */
                @Override
                public ImageRegion2D build() {
                    ImageRegion2D imageRegion2D = new ImageRegion2D(this);
                    if (validating) {
                        validate(imageRegion2D);
                    }
                    return imageRegion2D;
                }

                protected void validate(ImageRegion2D imageRegion2D) {
                    super.validate(imageRegion2D);
                    ValidationSupport.requireNonNull(imageRegion2D.regionType, "regionType");
                    ValidationSupport.checkNonEmptyList(imageRegion2D.coordinate, "coordinate", Decimal.class);
                    ValidationSupport.requireValueOrChildren(imageRegion2D);
                }

                protected Builder from(ImageRegion2D imageRegion2D) {
                    super.from(imageRegion2D);
                    regionType = imageRegion2D.regionType;
                    coordinate.addAll(imageRegion2D.coordinate);
                    return this;
                }
            }
        }

        /**
         * Each imaging selection might includes a 3D image region, specified by a region type and a set of 3D coordinates.
         */
        public static class ImageRegion3D extends BackboneElement {
            @Binding(
                bindingName = "ImagingSelection3DGraphicType",
                strength = BindingStrength.Value.REQUIRED,
                description = "The type of image region.",
                valueSet = "http://hl7.org/fhir/ValueSet/imagingselection-3dgraphictype|5.0.0"
            )
            @Required
            private final ImagingSelection3DGraphicType regionType;
            @Required
            private final List<Decimal> coordinate;

            private ImageRegion3D(Builder builder) {
                super(builder);
                regionType = builder.regionType;
                coordinate = Collections.unmodifiableList(builder.coordinate);
            }

            /**
             * Specifies the type of image region.
             * 
             * @return
             *     An immutable object of type {@link ImagingSelection3DGraphicType} that is non-null.
             */
            public ImagingSelection3DGraphicType getRegionType() {
                return regionType;
            }

            /**
             * The coordinates describing the image region. Encoded as an ordered set of (x,y,z) triplets (in mm and may be negative) 
             * that define a region of interest in the patient-relative Reference Coordinate System defined by ImagingSelection.
             * frameOfReferenceUid element.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Decimal} that is non-empty.
             */
            public List<Decimal> getCoordinate() {
                return coordinate;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (regionType != null) || 
                    !coordinate.isEmpty();
            }

            @Override
            public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
                if (visitor.preVisit(this)) {
                    visitor.visitStart(elementName, elementIndex, this);
                    if (visitor.visit(elementName, elementIndex, this)) {
                        // visit children
                        accept(id, "id", visitor);
                        accept(extension, "extension", visitor, Extension.class);
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(regionType, "regionType", visitor);
                        accept(coordinate, "coordinate", visitor, Decimal.class);
                    }
                    visitor.visitEnd(elementName, elementIndex, this);
                    visitor.postVisit(this);
                }
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                ImageRegion3D other = (ImageRegion3D) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(regionType, other.regionType) && 
                    Objects.equals(coordinate, other.coordinate);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        regionType, 
                        coordinate);
                    hashCode = result;
                }
                return result;
            }

            @Override
            public Builder toBuilder() {
                return new Builder().from(this);
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder extends BackboneElement.Builder {
                private ImagingSelection3DGraphicType regionType;
                private List<Decimal> coordinate = new ArrayList<>();

                private Builder() {
                    super();
                }

                /**
                 * Unique id for the element within a resource (for internal references). This may be any string value that does not 
                 * contain spaces.
                 * 
                 * @param id
                 *     Unique id for inter-element referencing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder id(java.lang.String id) {
                    return (Builder) super.id(id);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder extension(Extension... extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder extension(Collection<Extension> extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder modifierExtension(Extension... modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder modifierExtension(Collection<Extension> modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * Specifies the type of image region.
                 * 
                 * <p>This element is required.
                 * 
                 * @param regionType
                 *     point | multipoint | polyline | polygon | ellipse | ellipsoid
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder regionType(ImagingSelection3DGraphicType regionType) {
                    this.regionType = regionType;
                    return this;
                }

                /**
                 * The coordinates describing the image region. Encoded as an ordered set of (x,y,z) triplets (in mm and may be negative) 
                 * that define a region of interest in the patient-relative Reference Coordinate System defined by ImagingSelection.
                 * frameOfReferenceUid element.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param coordinate
                 *     Specifies the coordinates that define the image region
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder coordinate(Decimal... coordinate) {
                    for (Decimal value : coordinate) {
                        this.coordinate.add(value);
                    }
                    return this;
                }

                /**
                 * The coordinates describing the image region. Encoded as an ordered set of (x,y,z) triplets (in mm and may be negative) 
                 * that define a region of interest in the patient-relative Reference Coordinate System defined by ImagingSelection.
                 * frameOfReferenceUid element.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param coordinate
                 *     Specifies the coordinates that define the image region
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder coordinate(Collection<Decimal> coordinate) {
                    this.coordinate = new ArrayList<>(coordinate);
                    return this;
                }

                /**
                 * Build the {@link ImageRegion3D}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>regionType</li>
                 * <li>coordinate</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link ImageRegion3D}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid ImageRegion3D per the base specification
                 */
                @Override
                public ImageRegion3D build() {
                    ImageRegion3D imageRegion3D = new ImageRegion3D(this);
                    if (validating) {
                        validate(imageRegion3D);
                    }
                    return imageRegion3D;
                }

                protected void validate(ImageRegion3D imageRegion3D) {
                    super.validate(imageRegion3D);
                    ValidationSupport.requireNonNull(imageRegion3D.regionType, "regionType");
                    ValidationSupport.checkNonEmptyList(imageRegion3D.coordinate, "coordinate", Decimal.class);
                    ValidationSupport.requireValueOrChildren(imageRegion3D);
                }

                protected Builder from(ImageRegion3D imageRegion3D) {
                    super.from(imageRegion3D);
                    regionType = imageRegion3D.regionType;
                    coordinate.addAll(imageRegion3D.coordinate);
                    return this;
                }
            }
        }
    }
}
