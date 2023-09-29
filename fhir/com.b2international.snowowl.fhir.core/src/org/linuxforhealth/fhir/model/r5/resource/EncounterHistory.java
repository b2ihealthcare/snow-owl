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
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.EncounterStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of significant events/milestones key data throughout the history of an Encounter, often tracked for specific 
 * purposes such as billing.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "encounterHistory-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v3-ActEncounterCode",
    expression = "class.exists() and class.memberOf('http://terminology.hl7.org/ValueSet/v3-ActEncounterCode', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/EncounterHistory",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class EncounterHistory extends DomainResource {
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "EncounterStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Current state of the encounter.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-status|5.0.0"
    )
    @Required
    private final EncounterStatus status;
    @Summary
    @Binding(
        bindingName = "EncounterClass",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Classification of the encounter.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ActEncounterCode"
    )
    @Required
    private final CodeableConcept clazz;
    @Summary
    @Binding(
        bindingName = "EncounterType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A specific code indicating type of service provided",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    @Binding(
        bindingName = "EncounterServiceType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Broad categorization of the service that is to be provided.",
        valueSet = "http://hl7.org/fhir/ValueSet/service-type"
    )
    private final List<CodeableReference> serviceType;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    private final Reference subject;
    @Binding(
        bindingName = "SubjectStatus",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Current status of the subject  within the encounter.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-subject-status"
    )
    private final CodeableConcept subjectStatus;
    private final Period actualPeriod;
    private final DateTime plannedStartDate;
    private final DateTime plannedEndDate;
    private final Duration length;
    private final List<Location> location;

    private EncounterHistory(Builder builder) {
        super(builder);
        encounter = builder.encounter;
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        clazz = builder.clazz;
        type = Collections.unmodifiableList(builder.type);
        serviceType = Collections.unmodifiableList(builder.serviceType);
        subject = builder.subject;
        subjectStatus = builder.subjectStatus;
        actualPeriod = builder.actualPeriod;
        plannedStartDate = builder.plannedStartDate;
        plannedEndDate = builder.plannedEndDate;
        length = builder.length;
        location = Collections.unmodifiableList(builder.location);
    }

    /**
     * The Encounter associated with this set of historic values.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Identifier(s) by which this encounter is known.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * planned | in-progress | on-hold | discharged | completed | cancelled | discontinued | entered-in-error | unknown.
     * 
     * @return
     *     An immutable object of type {@link EncounterStatus} that is non-null.
     */
    public EncounterStatus getStatus() {
        return status;
    }

    /**
     * Concepts representing classification of patient encounter such as ambulatory (outpatient), inpatient, emergency, home 
     * health or others due to local variations.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getClazz() {
        return clazz;
    }

    /**
     * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * Broad categorization of the service that is to be provided (e.g. cardiology).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getServiceType() {
        return serviceType;
    }

    /**
     * The patient or group related to this encounter. In some use-cases the patient MAY not be present, such as a case 
     * meeting about a patient between several practitioners or a careteam.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The subjectStatus value can be used to track the patient's status within the encounter. It details whether the patient 
     * has arrived or departed, has been triaged or is currently in a waiting status.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSubjectStatus() {
        return subjectStatus;
    }

    /**
     * The start and end time associated with this set of values associated with the encounter, may be different to the 
     * planned times for various reasons.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getActualPeriod() {
        return actualPeriod;
    }

    /**
     * The planned start date/time (or admission date) of the encounter.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPlannedStartDate() {
        return plannedStartDate;
    }

    /**
     * The planned end date/time (or discharge date) of the encounter.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    /**
     * Actual quantity of time the encounter lasted. This excludes the time during leaves of absence.When missing it is the 
     * time in between the start and end values.
     * 
     * @return
     *     An immutable object of type {@link Duration} that may be null.
     */
    public Duration getLength() {
        return length;
    }

    /**
     * The location of the patient at this point in the encounter, the multiple cardinality permits de-normalizing the levels 
     * of the location hierarchy, such as site/ward/room/bed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Location} that may be empty.
     */
    public List<Location> getLocation() {
        return location;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (encounter != null) || 
            !identifier.isEmpty() || 
            (status != null) || 
            (clazz != null) || 
            !type.isEmpty() || 
            !serviceType.isEmpty() || 
            (subject != null) || 
            (subjectStatus != null) || 
            (actualPeriod != null) || 
            (plannedStartDate != null) || 
            (plannedEndDate != null) || 
            (length != null) || 
            !location.isEmpty();
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
                accept(encounter, "encounter", visitor);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(status, "status", visitor);
                accept(clazz, "class", visitor);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(serviceType, "serviceType", visitor, CodeableReference.class);
                accept(subject, "subject", visitor);
                accept(subjectStatus, "subjectStatus", visitor);
                accept(actualPeriod, "actualPeriod", visitor);
                accept(plannedStartDate, "plannedStartDate", visitor);
                accept(plannedEndDate, "plannedEndDate", visitor);
                accept(length, "length", visitor);
                accept(location, "location", visitor, Location.class);
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
        EncounterHistory other = (EncounterHistory) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(clazz, other.clazz) && 
            Objects.equals(type, other.type) && 
            Objects.equals(serviceType, other.serviceType) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(subjectStatus, other.subjectStatus) && 
            Objects.equals(actualPeriod, other.actualPeriod) && 
            Objects.equals(plannedStartDate, other.plannedStartDate) && 
            Objects.equals(plannedEndDate, other.plannedEndDate) && 
            Objects.equals(length, other.length) && 
            Objects.equals(location, other.location);
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
                encounter, 
                identifier, 
                status, 
                clazz, 
                type, 
                serviceType, 
                subject, 
                subjectStatus, 
                actualPeriod, 
                plannedStartDate, 
                plannedEndDate, 
                length, 
                location);
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
        private Reference encounter;
        private List<Identifier> identifier = new ArrayList<>();
        private EncounterStatus status;
        private CodeableConcept clazz;
        private List<CodeableConcept> type = new ArrayList<>();
        private List<CodeableReference> serviceType = new ArrayList<>();
        private Reference subject;
        private CodeableConcept subjectStatus;
        private Period actualPeriod;
        private DateTime plannedStartDate;
        private DateTime plannedEndDate;
        private Duration length;
        private List<Location> location = new ArrayList<>();

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
         * The Encounter associated with this set of historic values.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The Encounter associated with this set of historic values
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Identifier(s) by which this encounter is known.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier(s) by which this encounter is known
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
         * Identifier(s) by which this encounter is known.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier(s) by which this encounter is known
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
         * planned | in-progress | on-hold | discharged | completed | cancelled | discontinued | entered-in-error | unknown.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     planned | in-progress | on-hold | discharged | completed | cancelled | discontinued | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(EncounterStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Concepts representing classification of patient encounter such as ambulatory (outpatient), inpatient, emergency, home 
         * health or others due to local variations.
         * 
         * <p>This element is required.
         * 
         * @param clazz
         *     Classification of patient encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clazz(CodeableConcept clazz) {
            this.clazz = clazz;
            return this;
        }

        /**
         * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Specific type of encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept... type) {
            for (CodeableConcept value : type) {
                this.type.add(value);
            }
            return this;
        }

        /**
         * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Specific type of encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder type(Collection<CodeableConcept> type) {
            this.type = new ArrayList<>(type);
            return this;
        }

        /**
         * Broad categorization of the service that is to be provided (e.g. cardiology).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     Specific type of service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder serviceType(CodeableReference... serviceType) {
            for (CodeableReference value : serviceType) {
                this.serviceType.add(value);
            }
            return this;
        }

        /**
         * Broad categorization of the service that is to be provided (e.g. cardiology).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     Specific type of service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder serviceType(Collection<CodeableReference> serviceType) {
            this.serviceType = new ArrayList<>(serviceType);
            return this;
        }

        /**
         * The patient or group related to this encounter. In some use-cases the patient MAY not be present, such as a case 
         * meeting about a patient between several practitioners or a careteam.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     The patient or group related to this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The subjectStatus value can be used to track the patient's status within the encounter. It details whether the patient 
         * has arrived or departed, has been triaged or is currently in a waiting status.
         * 
         * @param subjectStatus
         *     The current status of the subject in relation to the Encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subjectStatus(CodeableConcept subjectStatus) {
            this.subjectStatus = subjectStatus;
            return this;
        }

        /**
         * The start and end time associated with this set of values associated with the encounter, may be different to the 
         * planned times for various reasons.
         * 
         * @param actualPeriod
         *     The actual start and end time associated with this set of values associated with the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actualPeriod(Period actualPeriod) {
            this.actualPeriod = actualPeriod;
            return this;
        }

        /**
         * The planned start date/time (or admission date) of the encounter.
         * 
         * @param plannedStartDate
         *     The planned start date/time (or admission date) of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder plannedStartDate(DateTime plannedStartDate) {
            this.plannedStartDate = plannedStartDate;
            return this;
        }

        /**
         * The planned end date/time (or discharge date) of the encounter.
         * 
         * @param plannedEndDate
         *     The planned end date/time (or discharge date) of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder plannedEndDate(DateTime plannedEndDate) {
            this.plannedEndDate = plannedEndDate;
            return this;
        }

        /**
         * Actual quantity of time the encounter lasted. This excludes the time during leaves of absence.When missing it is the 
         * time in between the start and end values.
         * 
         * @param length
         *     Actual quantity of time the encounter lasted (less time absent)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder length(Duration length) {
            this.length = length;
            return this;
        }

        /**
         * The location of the patient at this point in the encounter, the multiple cardinality permits de-normalizing the levels 
         * of the location hierarchy, such as site/ward/room/bed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     Location of the patient at this point in the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Location... location) {
            for (Location value : location) {
                this.location.add(value);
            }
            return this;
        }

        /**
         * The location of the patient at this point in the encounter, the multiple cardinality permits de-normalizing the levels 
         * of the location hierarchy, such as site/ward/room/bed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     Location of the patient at this point in the encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder location(Collection<Location> location) {
            this.location = new ArrayList<>(location);
            return this;
        }

        /**
         * Build the {@link EncounterHistory}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>class</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link EncounterHistory}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid EncounterHistory per the base specification
         */
        @Override
        public EncounterHistory build() {
            EncounterHistory encounterHistory = new EncounterHistory(this);
            if (validating) {
                validate(encounterHistory);
            }
            return encounterHistory;
        }

        protected void validate(EncounterHistory encounterHistory) {
            super.validate(encounterHistory);
            ValidationSupport.checkList(encounterHistory.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(encounterHistory.status, "status");
            ValidationSupport.requireNonNull(encounterHistory.clazz, "class");
            ValidationSupport.checkList(encounterHistory.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(encounterHistory.serviceType, "serviceType", CodeableReference.class);
            ValidationSupport.checkList(encounterHistory.location, "location", Location.class);
            ValidationSupport.checkReferenceType(encounterHistory.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(encounterHistory.subject, "subject", "Patient", "Group");
        }

        protected Builder from(EncounterHistory encounterHistory) {
            super.from(encounterHistory);
            encounter = encounterHistory.encounter;
            identifier.addAll(encounterHistory.identifier);
            status = encounterHistory.status;
            clazz = encounterHistory.clazz;
            type.addAll(encounterHistory.type);
            serviceType.addAll(encounterHistory.serviceType);
            subject = encounterHistory.subject;
            subjectStatus = encounterHistory.subjectStatus;
            actualPeriod = encounterHistory.actualPeriod;
            plannedStartDate = encounterHistory.plannedStartDate;
            plannedEndDate = encounterHistory.plannedEndDate;
            length = encounterHistory.length;
            location.addAll(encounterHistory.location);
            return this;
        }
    }

    /**
     * The location of the patient at this point in the encounter, the multiple cardinality permits de-normalizing the levels 
     * of the location hierarchy, such as site/ward/room/bed.
     */
    public static class Location extends BackboneElement {
        @ReferenceTarget({ "Location" })
        @Required
        private final Reference location;
        @Binding(
            bindingName = "LocationForm",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Physical form of the location.",
            valueSet = "http://hl7.org/fhir/ValueSet/location-form"
        )
        private final CodeableConcept form;

        private Location(Builder builder) {
            super(builder);
            location = builder.location;
            form = builder.form;
        }

        /**
         * The location where the encounter takes place.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getLocation() {
            return location;
        }

        /**
         * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either 
         * messaging or query.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getForm() {
            return form;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (location != null) || 
                (form != null);
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
                    accept(location, "location", visitor);
                    accept(form, "form", visitor);
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
            Location other = (Location) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(location, other.location) && 
                Objects.equals(form, other.form);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    location, 
                    form);
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
            private Reference location;
            private CodeableConcept form;

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
             * The location where the encounter takes place.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     Location the encounter takes place
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either 
             * messaging or query.
             * 
             * @param form
             *     The physical type of the location (usually the level in the location hierarchy - bed, room, ward, virtual etc.)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder form(CodeableConcept form) {
                this.form = form;
                return this;
            }

            /**
             * Build the {@link Location}
             * 
             * <p>Required elements:
             * <ul>
             * <li>location</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Location}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Location per the base specification
             */
            @Override
            public Location build() {
                Location location = new Location(this);
                if (validating) {
                    validate(location);
                }
                return location;
            }

            protected void validate(Location location) {
                super.validate(location);
                ValidationSupport.requireNonNull(location.location, "location");
                ValidationSupport.checkReferenceType(location.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(location);
            }

            protected Builder from(Location location) {
                super.from(location);
                this.location = location.location;
                form = location.form;
                return this;
            }
        }
    }
}
