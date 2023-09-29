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
import org.linuxforhealth.fhir.model.r5.type.Dosage;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationStatementStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of a medication that is being consumed by a patient. A MedicationStatement may indicate that the patient may 
 * be taking the medication now or has taken the medication in the past or will be taking the medication in the future. 
 * The source of this information can be the patient, significant other (such as a family member or spouse), or a 
 * clinician. A common scenario where this information is captured is during the history taking process during a patient 
 * visit or stay. The medication information may come from sources such as the patient's memory, from a prescription 
 * bottle, or from a list of medications the patient, clinician or other party maintains. 

The primary difference 
 * between a medicationstatement and a medicationadministration is that the medication administration has complete 
 * administration information and is based on actual administration information from the person who administered the 
 * medication. A medicationstatement is often, if not always, less specific. There is no required date/time when the 
 * medication was administered, in fact we only know that a source has reported the patient is taking this medication, 
 * where details such as time, quantity, or rate or even medication product may be incomplete or missing or less precise. 
 * As stated earlier, the Medication Statement information may come from the patient's memory, from a prescription bottle 
 * or from a list of medications the patient, clinician or other party maintains. Medication administration is more 
 * formal and is not missing detailed information.

The MedicationStatement resource was previously called 
 * MedicationStatement.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MedicationStatement extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @ReferenceTarget({ "Procedure", "MedicationStatement" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "MedicationStatementStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept indicating the current status of a MedicationStatement.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-statement-status|5.0.0"
    )
    @Required
    private final MedicationStatementStatus status;
    @Summary
    @Binding(
        bindingName = "MedicationStatementAdministrationLocation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying where the medication included in the MedicationStatement is expected to be consumed or administered.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-admin-location"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "MedicationCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying the substance or product being taken.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    @Required
    private final CodeableReference medication;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    private final Element effective;
    @Summary
    private final DateTime dateAsserted;
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization" })
    private final List<Reference> informationSource;
    private final List<Reference> derivedFrom;
    @Binding(
        bindingName = "MedicationReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying why the medication is being taken.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableReference> reason;
    private final List<Annotation> note;
    @ReferenceTarget({ "Observation", "Condition" })
    private final List<Reference> relatedClinicalInformation;
    private final Markdown renderedDosageInstruction;
    private final List<Dosage> dosage;
    @Summary
    private final Adherence adherence;

    private MedicationStatement(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        category = Collections.unmodifiableList(builder.category);
        medication = builder.medication;
        subject = builder.subject;
        encounter = builder.encounter;
        effective = builder.effective;
        dateAsserted = builder.dateAsserted;
        informationSource = Collections.unmodifiableList(builder.informationSource);
        derivedFrom = Collections.unmodifiableList(builder.derivedFrom);
        reason = Collections.unmodifiableList(builder.reason);
        note = Collections.unmodifiableList(builder.note);
        relatedClinicalInformation = Collections.unmodifiableList(builder.relatedClinicalInformation);
        renderedDosageInstruction = builder.renderedDosageInstruction;
        dosage = Collections.unmodifiableList(builder.dosage);
        adherence = builder.adherence;
    }

    /**
     * Identifiers associated with this Medication Statement that are defined by business processes and/or used to refer to 
     * it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
     * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
     * server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A larger event of which this particular MedicationStatement is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code representing the status of recording the medication statement.
     * 
     * @return
     *     An immutable object of type {@link MedicationStatementStatus} that is non-null.
     */
    public MedicationStatementStatus getStatus() {
        return status;
    }

    /**
     * Type of medication statement (for example, drug classification like ATC, where meds would be administered, legal 
     * category of the medication.).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Identifies the medication being administered. This is either a link to a resource representing the details of the 
     * medication or a simple attribute carrying a code that identifies the medication from a known list of medications.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getMedication() {
        return medication;
    }

    /**
     * The person, animal or group who is/was taking the medication.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The encounter that establishes the context for this MedicationStatement.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The interval of time during which it is being asserted that the patient is/was/will be taking the medication (or was 
     * not taking, when the MedicationStatement.adherence element is Not Taking).
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that may be null.
     */
    public Element getEffective() {
        return effective;
    }

    /**
     * The date when the Medication Statement was asserted by the information source.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDateAsserted() {
        return dateAsserted;
    }

    /**
     * The person or organization that provided the information about the taking of this medication. Note: Use derivedFrom 
     * when a MedicationStatement is derived from other resources, e.g. Claim or MedicationRequest.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInformationSource() {
        return informationSource;
    }

    /**
     * Allows linking the MedicationStatement to the underlying MedicationRequest, or to other information that supports or 
     * is used to derive the MedicationStatement.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * A concept, Condition or observation that supports why the medication is being/was taken.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Provides extra information about the Medication Statement that is not conveyed by the other attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Link to information that is relevant to a medication statement, for example, illicit drug use, gestational age, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRelatedClinicalInformation() {
        return relatedClinicalInformation;
    }

    /**
     * The full representation of the dose of the medication included in all dosage instructions. To be used when multiple 
     * dosage instructions are included to represent complex dosing such as increasing or tapering doses.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getRenderedDosageInstruction() {
        return renderedDosageInstruction;
    }

    /**
     * Indicates how the medication is/was or should be taken by the patient.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Dosage} that may be empty.
     */
    public List<Dosage> getDosage() {
        return dosage;
    }

    /**
     * Indicates whether the medication is or is not being consumed or administered.
     * 
     * @return
     *     An immutable object of type {@link Adherence} that may be null.
     */
    public Adherence getAdherence() {
        return adherence;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !partOf.isEmpty() || 
            (status != null) || 
            !category.isEmpty() || 
            (medication != null) || 
            (subject != null) || 
            (encounter != null) || 
            (effective != null) || 
            (dateAsserted != null) || 
            !informationSource.isEmpty() || 
            !derivedFrom.isEmpty() || 
            !reason.isEmpty() || 
            !note.isEmpty() || 
            !relatedClinicalInformation.isEmpty() || 
            (renderedDosageInstruction != null) || 
            !dosage.isEmpty() || 
            (adherence != null);
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
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(medication, "medication", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(effective, "effective", visitor);
                accept(dateAsserted, "dateAsserted", visitor);
                accept(informationSource, "informationSource", visitor, Reference.class);
                accept(derivedFrom, "derivedFrom", visitor, Reference.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(relatedClinicalInformation, "relatedClinicalInformation", visitor, Reference.class);
                accept(renderedDosageInstruction, "renderedDosageInstruction", visitor);
                accept(dosage, "dosage", visitor, Dosage.class);
                accept(adherence, "adherence", visitor);
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
        MedicationStatement other = (MedicationStatement) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(category, other.category) && 
            Objects.equals(medication, other.medication) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(effective, other.effective) && 
            Objects.equals(dateAsserted, other.dateAsserted) && 
            Objects.equals(informationSource, other.informationSource) && 
            Objects.equals(derivedFrom, other.derivedFrom) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(note, other.note) && 
            Objects.equals(relatedClinicalInformation, other.relatedClinicalInformation) && 
            Objects.equals(renderedDosageInstruction, other.renderedDosageInstruction) && 
            Objects.equals(dosage, other.dosage) && 
            Objects.equals(adherence, other.adherence);
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
                partOf, 
                status, 
                category, 
                medication, 
                subject, 
                encounter, 
                effective, 
                dateAsserted, 
                informationSource, 
                derivedFrom, 
                reason, 
                note, 
                relatedClinicalInformation, 
                renderedDosageInstruction, 
                dosage, 
                adherence);
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
        private List<Reference> partOf = new ArrayList<>();
        private MedicationStatementStatus status;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableReference medication;
        private Reference subject;
        private Reference encounter;
        private Element effective;
        private DateTime dateAsserted;
        private List<Reference> informationSource = new ArrayList<>();
        private List<Reference> derivedFrom = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<Reference> relatedClinicalInformation = new ArrayList<>();
        private Markdown renderedDosageInstruction;
        private List<Dosage> dosage = new ArrayList<>();
        private Adherence adherence;

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
         * Identifiers associated with this Medication Statement that are defined by business processes and/or used to refer to 
         * it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
         * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
         * server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier
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
         * Identifiers associated with this Medication Statement that are defined by business processes and/or used to refer to 
         * it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
         * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
         * server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier
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
         * A larger event of which this particular MedicationStatement is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * <li>{@link MedicationStatement}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partOf(Reference... partOf) {
            for (Reference value : partOf) {
                this.partOf.add(value);
            }
            return this;
        }

        /**
         * A larger event of which this particular MedicationStatement is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * <li>{@link MedicationStatement}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder partOf(Collection<Reference> partOf) {
            this.partOf = new ArrayList<>(partOf);
            return this;
        }

        /**
         * A code representing the status of recording the medication statement.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     recorded | entered-in-error | draft
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MedicationStatementStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Type of medication statement (for example, drug classification like ATC, where meds would be administered, legal 
         * category of the medication.).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of medication statement
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
         * Type of medication statement (for example, drug classification like ATC, where meds would be administered, legal 
         * category of the medication.).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of medication statement
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
         * Identifies the medication being administered. This is either a link to a resource representing the details of the 
         * medication or a simple attribute carrying a code that identifies the medication from a known list of medications.
         * 
         * <p>This element is required.
         * 
         * @param medication
         *     What medication was taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder medication(CodeableReference medication) {
            this.medication = medication;
            return this;
        }

        /**
         * The person, animal or group who is/was taking the medication.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     Who is/was taking the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The encounter that establishes the context for this MedicationStatement.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter associated with MedicationStatement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The interval of time during which it is being asserted that the patient is/was/will be taking the medication (or was 
         * not taking, when the MedicationStatement.adherence element is Not Taking).
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param effective
         *     The date/time or interval when the medication is/was/will be taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effective(Element effective) {
            this.effective = effective;
            return this;
        }

        /**
         * The date when the Medication Statement was asserted by the information source.
         * 
         * @param dateAsserted
         *     When the usage was asserted?
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dateAsserted(DateTime dateAsserted) {
            this.dateAsserted = dateAsserted;
            return this;
        }

        /**
         * The person or organization that provided the information about the taking of this medication. Note: Use derivedFrom 
         * when a MedicationStatement is derived from other resources, e.g. Claim or MedicationRequest.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param informationSource
         *     Person or organization that provided the information about the taking of this medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder informationSource(Reference... informationSource) {
            for (Reference value : informationSource) {
                this.informationSource.add(value);
            }
            return this;
        }

        /**
         * The person or organization that provided the information about the taking of this medication. Note: Use derivedFrom 
         * when a MedicationStatement is derived from other resources, e.g. Claim or MedicationRequest.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param informationSource
         *     Person or organization that provided the information about the taking of this medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder informationSource(Collection<Reference> informationSource) {
            this.informationSource = new ArrayList<>(informationSource);
            return this;
        }

        /**
         * Allows linking the MedicationStatement to the underlying MedicationRequest, or to other information that supports or 
         * is used to derive the MedicationStatement.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Link to information used to derive the MedicationStatement
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
         * Allows linking the MedicationStatement to the underlying MedicationRequest, or to other information that supports or 
         * is used to derive the MedicationStatement.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Link to information used to derive the MedicationStatement
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
         * A concept, Condition or observation that supports why the medication is being/was taken.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason for why the medication is being/was taken
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
         * A concept, Condition or observation that supports why the medication is being/was taken.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason for why the medication is being/was taken
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
         * Provides extra information about the Medication Statement that is not conveyed by the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Further information about the usage
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
         * Provides extra information about the Medication Statement that is not conveyed by the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Further information about the usage
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
         * Link to information that is relevant to a medication statement, for example, illicit drug use, gestational age, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Observation}</li>
         * <li>{@link Condition}</li>
         * </ul>
         * 
         * @param relatedClinicalInformation
         *     Link to information relevant to the usage of a medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatedClinicalInformation(Reference... relatedClinicalInformation) {
            for (Reference value : relatedClinicalInformation) {
                this.relatedClinicalInformation.add(value);
            }
            return this;
        }

        /**
         * Link to information that is relevant to a medication statement, for example, illicit drug use, gestational age, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Observation}</li>
         * <li>{@link Condition}</li>
         * </ul>
         * 
         * @param relatedClinicalInformation
         *     Link to information relevant to the usage of a medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatedClinicalInformation(Collection<Reference> relatedClinicalInformation) {
            this.relatedClinicalInformation = new ArrayList<>(relatedClinicalInformation);
            return this;
        }

        /**
         * The full representation of the dose of the medication included in all dosage instructions. To be used when multiple 
         * dosage instructions are included to represent complex dosing such as increasing or tapering doses.
         * 
         * @param renderedDosageInstruction
         *     Full representation of the dosage instructions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder renderedDosageInstruction(Markdown renderedDosageInstruction) {
            this.renderedDosageInstruction = renderedDosageInstruction;
            return this;
        }

        /**
         * Indicates how the medication is/was or should be taken by the patient.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosage
         *     Details of how medication is/was taken or should be taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dosage(Dosage... dosage) {
            for (Dosage value : dosage) {
                this.dosage.add(value);
            }
            return this;
        }

        /**
         * Indicates how the medication is/was or should be taken by the patient.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosage
         *     Details of how medication is/was taken or should be taken
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder dosage(Collection<Dosage> dosage) {
            this.dosage = new ArrayList<>(dosage);
            return this;
        }

        /**
         * Indicates whether the medication is or is not being consumed or administered.
         * 
         * @param adherence
         *     Indicates whether the medication is or is not being consumed or administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder adherence(Adherence adherence) {
            this.adherence = adherence;
            return this;
        }

        /**
         * Build the {@link MedicationStatement}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>medication</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link MedicationStatement}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MedicationStatement per the base specification
         */
        @Override
        public MedicationStatement build() {
            MedicationStatement medicationStatement = new MedicationStatement(this);
            if (validating) {
                validate(medicationStatement);
            }
            return medicationStatement;
        }

        protected void validate(MedicationStatement medicationStatement) {
            super.validate(medicationStatement);
            ValidationSupport.checkList(medicationStatement.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(medicationStatement.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(medicationStatement.status, "status");
            ValidationSupport.checkList(medicationStatement.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(medicationStatement.medication, "medication");
            ValidationSupport.requireNonNull(medicationStatement.subject, "subject");
            ValidationSupport.choiceElement(medicationStatement.effective, "effective", DateTime.class, Period.class, Timing.class);
            ValidationSupport.checkList(medicationStatement.informationSource, "informationSource", Reference.class);
            ValidationSupport.checkList(medicationStatement.derivedFrom, "derivedFrom", Reference.class);
            ValidationSupport.checkList(medicationStatement.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(medicationStatement.note, "note", Annotation.class);
            ValidationSupport.checkList(medicationStatement.relatedClinicalInformation, "relatedClinicalInformation", Reference.class);
            ValidationSupport.checkList(medicationStatement.dosage, "dosage", Dosage.class);
            ValidationSupport.checkReferenceType(medicationStatement.partOf, "partOf", "Procedure", "MedicationStatement");
            ValidationSupport.checkReferenceType(medicationStatement.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(medicationStatement.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(medicationStatement.informationSource, "informationSource", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization");
            ValidationSupport.checkReferenceType(medicationStatement.relatedClinicalInformation, "relatedClinicalInformation", "Observation", "Condition");
        }

        protected Builder from(MedicationStatement medicationStatement) {
            super.from(medicationStatement);
            identifier.addAll(medicationStatement.identifier);
            partOf.addAll(medicationStatement.partOf);
            status = medicationStatement.status;
            category.addAll(medicationStatement.category);
            medication = medicationStatement.medication;
            subject = medicationStatement.subject;
            encounter = medicationStatement.encounter;
            effective = medicationStatement.effective;
            dateAsserted = medicationStatement.dateAsserted;
            informationSource.addAll(medicationStatement.informationSource);
            derivedFrom.addAll(medicationStatement.derivedFrom);
            reason.addAll(medicationStatement.reason);
            note.addAll(medicationStatement.note);
            relatedClinicalInformation.addAll(medicationStatement.relatedClinicalInformation);
            renderedDosageInstruction = medicationStatement.renderedDosageInstruction;
            dosage.addAll(medicationStatement.dosage);
            adherence = medicationStatement.adherence;
            return this;
        }
    }

    /**
     * Indicates whether the medication is or is not being consumed or administered.
     */
    public static class Adherence extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "MedicationStatementAdherence",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/medication-statement-adherence"
        )
        @Required
        private final CodeableConcept code;
        @Binding(
            bindingName = "MedicationStatementStatusReason",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/reason-medication-status-codes"
        )
        private final CodeableConcept reason;

        private Adherence(Builder builder) {
            super(builder);
            code = builder.code;
            reason = builder.reason;
        }

        /**
         * Type of the adherence for the medication.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Captures the reason for the current use or adherence of a medication.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getReason() {
            return reason;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (reason != null);
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
                    accept(reason, "reason", visitor);
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
            private CodeableConcept reason;

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
             * Type of the adherence for the medication.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Type of adherence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Captures the reason for the current use or adherence of a medication.
             * 
             * @param reason
             *     Details of the reason for the current use of the medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reason(CodeableConcept reason) {
                this.reason = reason;
                return this;
            }

            /**
             * Build the {@link Adherence}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
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
                ValidationSupport.requireValueOrChildren(adherence);
            }

            protected Builder from(Adherence adherence) {
                super.from(adherence);
                code = adherence.code;
                reason = adherence.reason;
                return this;
            }
        }
    }
}
