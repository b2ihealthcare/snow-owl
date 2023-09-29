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
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceUsageStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of a device being used by a patient where the record is the result of a report from the patient or a 
 * clinician.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceUsage extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    @Binding(
        bindingName = "DeviceUsageStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept indicating the current status of the Device Usage.",
        valueSet = "http://hl7.org/fhir/ValueSet/deviceusage-status|5.0.0"
    )
    @Required
    private final DeviceUsageStatus status;
    private final List<CodeableConcept> category;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;
    @Summary
    @ReferenceTarget({ "ServiceRequest", "Procedure", "Claim", "Observation", "QuestionnaireResponse", "DocumentReference" })
    private final List<Reference> derivedFrom;
    @Summary
    @ReferenceTarget({ "Encounter", "EpisodeOfCare" })
    private final Reference context;
    @Summary
    @Choice({ Timing.class, Period.class, DateTime.class })
    private final Element timing;
    @Summary
    private final DateTime dateAsserted;
    @Binding(
        bindingName = "DeviceUsageStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes representing the usage status of the device.",
        valueSet = "http://hl7.org/fhir/ValueSet/deviceusage-status|5.0.0"
    )
    private final CodeableConcept usageStatus;
    private final List<CodeableConcept> usageReason;
    private final Adherence adherence;
    @Summary
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization" })
    private final Reference informationSource;
    @Summary
    @Required
    private final CodeableReference device;
    @Summary
    private final List<CodeableReference> reason;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final CodeableReference bodySite;
    private final List<Annotation> note;

    private DeviceUsage(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        status = builder.status;
        category = Collections.unmodifiableList(builder.category);
        patient = builder.patient;
        derivedFrom = Collections.unmodifiableList(builder.derivedFrom);
        context = builder.context;
        timing = builder.timing;
        dateAsserted = builder.dateAsserted;
        usageStatus = builder.usageStatus;
        usageReason = Collections.unmodifiableList(builder.usageReason);
        adherence = builder.adherence;
        informationSource = builder.informationSource;
        device = builder.device;
        reason = Collections.unmodifiableList(builder.reason);
        bodySite = builder.bodySite;
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * An external identifier for this statement such as an IRI.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A plan, proposal or order that is fulfilled in whole or in part by this DeviceUsage.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A code representing the patient or other source's judgment about the state of the device used that this statement is 
     * about. Generally this will be active or completed.
     * 
     * @return
     *     An immutable object of type {@link DeviceUsageStatus} that is non-null.
     */
    public DeviceUsageStatus getStatus() {
        return status;
    }

    /**
     * This attribute indicates a category for the statement - The device statement may be made in an inpatient or outpatient 
     * settting (inpatient | outpatient | community | patientspecified).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * The patient who used the device.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * Allows linking the DeviceUsage to the underlying Request, or to other information that supports or is used to derive 
     * the DeviceUsage.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * The encounter or episode of care that establishes the context for this device use statement.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getContext() {
        return context;
    }

    /**
     * How often the device was used.
     * 
     * @return
     *     An immutable object of type {@link Timing}, {@link Period} or {@link DateTime} that may be null.
     */
    public Element getTiming() {
        return timing;
    }

    /**
     * The time at which the statement was recorded by informationSource.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDateAsserted() {
        return dateAsserted;
    }

    /**
     * The status of the device usage, for example always, sometimes, never. This is not the same as the status of the 
     * statement.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getUsageStatus() {
        return usageStatus;
    }

    /**
     * The reason for asserting the usage status - for example forgot, lost, stolen, broken.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getUsageReason() {
        return usageReason;
    }

    /**
     * This indicates how or if the device is being used.
     * 
     * @return
     *     An immutable object of type {@link Adherence} that may be null.
     */
    public Adherence getAdherence() {
        return adherence;
    }

    /**
     * Who reported the device was being used by the patient.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getInformationSource() {
        return informationSource;
    }

    /**
     * Code or Reference to device used.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getDevice() {
        return device;
    }

    /**
     * Reason or justification for the use of the device. A coded concept, or another resource whose existence justifies this 
     * DeviceUsage.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Indicates the anotomic location on the subject's body where the device was used ( i.e. the target).
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getBodySite() {
        return bodySite;
    }

    /**
     * Details about the device statement that were not represented at all or sufficiently in one of the attributes provided 
     * in a class. These may include for example a comment, an instruction, or a note associated with the statement.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !basedOn.isEmpty() || 
            (status != null) || 
            !category.isEmpty() || 
            (patient != null) || 
            !derivedFrom.isEmpty() || 
            (context != null) || 
            (timing != null) || 
            (dateAsserted != null) || 
            (usageStatus != null) || 
            !usageReason.isEmpty() || 
            (adherence != null) || 
            (informationSource != null) || 
            (device != null) || 
            !reason.isEmpty() || 
            (bodySite != null) || 
            !note.isEmpty();
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
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(patient, "patient", visitor);
                accept(derivedFrom, "derivedFrom", visitor, Reference.class);
                accept(context, "context", visitor);
                accept(timing, "timing", visitor);
                accept(dateAsserted, "dateAsserted", visitor);
                accept(usageStatus, "usageStatus", visitor);
                accept(usageReason, "usageReason", visitor, CodeableConcept.class);
                accept(adherence, "adherence", visitor);
                accept(informationSource, "informationSource", visitor);
                accept(device, "device", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(bodySite, "bodySite", visitor);
                accept(note, "note", visitor, Annotation.class);
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
        DeviceUsage other = (DeviceUsage) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(status, other.status) && 
            Objects.equals(category, other.category) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(derivedFrom, other.derivedFrom) && 
            Objects.equals(context, other.context) && 
            Objects.equals(timing, other.timing) && 
            Objects.equals(dateAsserted, other.dateAsserted) && 
            Objects.equals(usageStatus, other.usageStatus) && 
            Objects.equals(usageReason, other.usageReason) && 
            Objects.equals(adherence, other.adherence) && 
            Objects.equals(informationSource, other.informationSource) && 
            Objects.equals(device, other.device) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(note, other.note);
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
                basedOn, 
                status, 
                category, 
                patient, 
                derivedFrom, 
                context, 
                timing, 
                dateAsserted, 
                usageStatus, 
                usageReason, 
                adherence, 
                informationSource, 
                device, 
                reason, 
                bodySite, 
                note);
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
        private List<Reference> basedOn = new ArrayList<>();
        private DeviceUsageStatus status;
        private List<CodeableConcept> category = new ArrayList<>();
        private Reference patient;
        private List<Reference> derivedFrom = new ArrayList<>();
        private Reference context;
        private Element timing;
        private DateTime dateAsserted;
        private CodeableConcept usageStatus;
        private List<CodeableConcept> usageReason = new ArrayList<>();
        private Adherence adherence;
        private Reference informationSource;
        private CodeableReference device;
        private List<CodeableReference> reason = new ArrayList<>();
        private CodeableReference bodySite;
        private List<Annotation> note = new ArrayList<>();

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
         * An external identifier for this statement such as an IRI.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier for this record
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
         * An external identifier for this statement such as an IRI.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier for this record
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
         * A plan, proposal or order that is fulfilled in whole or in part by this DeviceUsage.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfills plan, proposal or order
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
         * A plan, proposal or order that is fulfilled in whole or in part by this DeviceUsage.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfills plan, proposal or order
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
         * A code representing the patient or other source's judgment about the state of the device used that this statement is 
         * about. Generally this will be active or completed.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | completed | not-done | entered-in-error +
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(DeviceUsageStatus status) {
            this.status = status;
            return this;
        }

        /**
         * This attribute indicates a category for the statement - The device statement may be made in an inpatient or outpatient 
         * settting (inpatient | outpatient | community | patientspecified).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     The category of the statement - classifying how the statement is made
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
         * This attribute indicates a category for the statement - The device statement may be made in an inpatient or outpatient 
         * settting (inpatient | outpatient | community | patientspecified).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     The category of the statement - classifying how the statement is made
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
         * The patient who used the device.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     Patient using device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * Allows linking the DeviceUsage to the underlying Request, or to other information that supports or is used to derive 
         * the DeviceUsage.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link Claim}</li>
         * <li>{@link Observation}</li>
         * <li>{@link QuestionnaireResponse}</li>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param derivedFrom
         *     Supporting information
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
         * Allows linking the DeviceUsage to the underlying Request, or to other information that supports or is used to derive 
         * the DeviceUsage.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link Claim}</li>
         * <li>{@link Observation}</li>
         * <li>{@link QuestionnaireResponse}</li>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param derivedFrom
         *     Supporting information
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
         * The encounter or episode of care that establishes the context for this device use statement.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * <li>{@link EpisodeOfCare}</li>
         * </ul>
         * 
         * @param context
         *     The encounter or episode of care that establishes the context for this device use statement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder context(Reference context) {
            this.context = context;
            return this;
        }

        /**
         * How often the device was used.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Timing}</li>
         * <li>{@link Period}</li>
         * <li>{@link DateTime}</li>
         * </ul>
         * 
         * @param timing
         *     How often the device was used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder timing(Element timing) {
            this.timing = timing;
            return this;
        }

        /**
         * The time at which the statement was recorded by informationSource.
         * 
         * @param dateAsserted
         *     When the statement was made (and recorded)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dateAsserted(DateTime dateAsserted) {
            this.dateAsserted = dateAsserted;
            return this;
        }

        /**
         * The status of the device usage, for example always, sometimes, never. This is not the same as the status of the 
         * statement.
         * 
         * @param usageStatus
         *     The status of the device usage, for example always, sometimes, never. This is not the same as the status of the 
         *     statement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usageStatus(CodeableConcept usageStatus) {
            this.usageStatus = usageStatus;
            return this;
        }

        /**
         * The reason for asserting the usage status - for example forgot, lost, stolen, broken.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param usageReason
         *     The reason for asserting the usage status - for example forgot, lost, stolen, broken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usageReason(CodeableConcept... usageReason) {
            for (CodeableConcept value : usageReason) {
                this.usageReason.add(value);
            }
            return this;
        }

        /**
         * The reason for asserting the usage status - for example forgot, lost, stolen, broken.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param usageReason
         *     The reason for asserting the usage status - for example forgot, lost, stolen, broken
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder usageReason(Collection<CodeableConcept> usageReason) {
            this.usageReason = new ArrayList<>(usageReason);
            return this;
        }

        /**
         * This indicates how or if the device is being used.
         * 
         * @param adherence
         *     How device is being used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder adherence(Adherence adherence) {
            this.adherence = adherence;
            return this;
        }

        /**
         * Who reported the device was being used by the patient.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param informationSource
         *     Who made the statement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder informationSource(Reference informationSource) {
            this.informationSource = informationSource;
            return this;
        }

        /**
         * Code or Reference to device used.
         * 
         * <p>This element is required.
         * 
         * @param device
         *     Code or Reference to device used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(CodeableReference device) {
            this.device = device;
            return this;
        }

        /**
         * Reason or justification for the use of the device. A coded concept, or another resource whose existence justifies this 
         * DeviceUsage.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why device was used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(CodeableReference... reason) {
            for (CodeableReference value : reason) {
                this.reason.add(value);
            }
            return this;
        }

        /**
         * Reason or justification for the use of the device. A coded concept, or another resource whose existence justifies this 
         * DeviceUsage.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why device was used
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reason(Collection<CodeableReference> reason) {
            this.reason = new ArrayList<>(reason);
            return this;
        }

        /**
         * Indicates the anotomic location on the subject's body where the device was used ( i.e. the target).
         * 
         * @param bodySite
         *     Target body site
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableReference bodySite) {
            this.bodySite = bodySite;
            return this;
        }

        /**
         * Details about the device statement that were not represented at all or sufficiently in one of the attributes provided 
         * in a class. These may include for example a comment, an instruction, or a note associated with the statement.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Addition details (comments, instructions)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder note(Annotation... note) {
            for (Annotation value : note) {
                this.note.add(value);
            }
            return this;
        }

        /**
         * Details about the device statement that were not represented at all or sufficiently in one of the attributes provided 
         * in a class. These may include for example a comment, an instruction, or a note associated with the statement.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Addition details (comments, instructions)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder note(Collection<Annotation> note) {
            this.note = new ArrayList<>(note);
            return this;
        }

        /**
         * Build the {@link DeviceUsage}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>patient</li>
         * <li>device</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DeviceUsage}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceUsage per the base specification
         */
        @Override
        public DeviceUsage build() {
            DeviceUsage deviceUsage = new DeviceUsage(this);
            if (validating) {
                validate(deviceUsage);
            }
            return deviceUsage;
        }

        protected void validate(DeviceUsage deviceUsage) {
            super.validate(deviceUsage);
            ValidationSupport.checkList(deviceUsage.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(deviceUsage.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(deviceUsage.status, "status");
            ValidationSupport.checkList(deviceUsage.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(deviceUsage.patient, "patient");
            ValidationSupport.checkList(deviceUsage.derivedFrom, "derivedFrom", Reference.class);
            ValidationSupport.choiceElement(deviceUsage.timing, "timing", Timing.class, Period.class, DateTime.class);
            ValidationSupport.checkList(deviceUsage.usageReason, "usageReason", CodeableConcept.class);
            ValidationSupport.requireNonNull(deviceUsage.device, "device");
            ValidationSupport.checkList(deviceUsage.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(deviceUsage.note, "note", Annotation.class);
            ValidationSupport.checkValueSetBinding(deviceUsage.usageStatus, "usageStatus", "http://hl7.org/fhir/ValueSet/deviceusage-status", "http://hl7.org/fhir/deviceusage-status", "active", "completed", "not-done", "entered-in-error", "intended", "stopped", "on-hold");
            ValidationSupport.checkReferenceType(deviceUsage.basedOn, "basedOn", "ServiceRequest");
            ValidationSupport.checkReferenceType(deviceUsage.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(deviceUsage.derivedFrom, "derivedFrom", "ServiceRequest", "Procedure", "Claim", "Observation", "QuestionnaireResponse", "DocumentReference");
            ValidationSupport.checkReferenceType(deviceUsage.context, "context", "Encounter", "EpisodeOfCare");
            ValidationSupport.checkReferenceType(deviceUsage.informationSource, "informationSource", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization");
        }

        protected Builder from(DeviceUsage deviceUsage) {
            super.from(deviceUsage);
            identifier.addAll(deviceUsage.identifier);
            basedOn.addAll(deviceUsage.basedOn);
            status = deviceUsage.status;
            category.addAll(deviceUsage.category);
            patient = deviceUsage.patient;
            derivedFrom.addAll(deviceUsage.derivedFrom);
            context = deviceUsage.context;
            timing = deviceUsage.timing;
            dateAsserted = deviceUsage.dateAsserted;
            usageStatus = deviceUsage.usageStatus;
            usageReason.addAll(deviceUsage.usageReason);
            adherence = deviceUsage.adherence;
            informationSource = deviceUsage.informationSource;
            device = deviceUsage.device;
            reason.addAll(deviceUsage.reason);
            bodySite = deviceUsage.bodySite;
            note.addAll(deviceUsage.note);
            return this;
        }
    }

    /**
     * This indicates how or if the device is being used.
     */
    public static class Adherence extends BackboneElement {
        @Binding(
            bindingName = "DeviceUsageAdherenceCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes for adherence",
            valueSet = "http://hl7.org/fhir/ValueSet/deviceusage-adherence-code"
        )
        @Required
        private final CodeableConcept code;
        @Binding(
            bindingName = "DeviceUsageAdherenceReason",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes for adherence reason",
            valueSet = "http://hl7.org/fhir/ValueSet/deviceusage-adherence-reason"
        )
        @Required
        private final List<CodeableConcept> reason;

        private Adherence(Builder builder) {
            super(builder);
            code = builder.code;
            reason = Collections.unmodifiableList(builder.reason);
        }

        /**
         * Type of adherence.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Reason for adherence type.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that is non-empty.
         */
        public List<CodeableConcept> getReason() {
            return reason;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                !reason.isEmpty();
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
                    accept(code, "code", visitor);
                    accept(reason, "reason", visitor, CodeableConcept.class);
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
            Adherence other = (Adherence) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(reason, other.reason);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    reason);
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
            private CodeableConcept code;
            private List<CodeableConcept> reason = new ArrayList<>();

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
             * Type of adherence.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     always | never | sometimes
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Reason for adherence type.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param reason
             *     lost | stolen | prescribed | broken | burned | forgot
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reason(CodeableConcept... reason) {
                for (CodeableConcept value : reason) {
                    this.reason.add(value);
                }
                return this;
            }

            /**
             * Reason for adherence type.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param reason
             *     lost | stolen | prescribed | broken | burned | forgot
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder reason(Collection<CodeableConcept> reason) {
                this.reason = new ArrayList<>(reason);
                return this;
            }

            /**
             * Build the {@link Adherence}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * <li>reason</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Adherence}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Adherence per the base specification
             */
            @Override
            public Adherence build() {
                Adherence adherence = new Adherence(this);
                if (validating) {
                    validate(adherence);
                }
                return adherence;
            }

            protected void validate(Adherence adherence) {
                super.validate(adherence);
                ValidationSupport.requireNonNull(adherence.code, "code");
                ValidationSupport.checkNonEmptyList(adherence.reason, "reason", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(adherence);
            }

            protected Builder from(Adherence adherence) {
                super.from(adherence);
                code = adherence.code;
                reason.addAll(adherence.reason);
                return this;
            }
        }
    }
}
