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
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Age;
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
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a 
 * level of concern.
 * 
 * <p>Maturity level: FMM5 (Trial Use)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "con-1",
    level = "Rule",
    location = "Condition.stage",
    description = "Stage SHALL have summary or assessment",
    expression = "summary.exists() or assessment.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Condition"
)
@Constraint(
    id = "con-2",
    level = "Warning",
    location = "(base)",
    description = "If category is problems list item, the clinicalStatus should not be unknown",
    expression = "category.coding.where(system='http://terminology.hl7.org/CodeSystem/condition-category' and code='problem-list-item').exists() implies clinicalStatus.coding.where(system='http://terminology.hl7.org/CodeSystem/condition-clinical' and code='unknown').exists().not()",
    source = "http://hl7.org/fhir/StructureDefinition/Condition"
)
@Constraint(
    id = "con-3",
    level = "Rule",
    location = "(base)",
    description = "If condition is abated, then clinicalStatus must be either inactive, resolved, or remission.",
    expression = "abatement.exists() implies (clinicalStatus.coding.where(system='http://terminology.hl7.org/CodeSystem/condition-clinical' and (code='inactive' or code='resolved' or code='remission')).exists())",
    source = "http://hl7.org/fhir/StructureDefinition/Condition"
)
@Constraint(
    id = "condition-4",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/condition-category",
    expression = "category.exists() implies (category.all(memberOf('http://hl7.org/fhir/ValueSet/condition-category', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Condition",
    generated = true
)
@Constraint(
    id = "condition-5",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/condition-severity",
    expression = "severity.exists() implies (severity.memberOf('http://hl7.org/fhir/ValueSet/condition-severity', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/Condition",
    generated = true
)
@Constraint(
    id = "condition-6",
    level = "Warning",
    location = "participant.function",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/participation-role-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/participation-role-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Condition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Condition extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "ConditionClinicalStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The clinical status of the condition or diagnosis.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-clinical|5.0.0"
    )
    @Required
    private final CodeableConcept clinicalStatus;
    @Summary
    @Binding(
        bindingName = "ConditionVerificationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The verification status to support or decline the clinical status of the condition or diagnosis.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-ver-status|5.0.0"
    )
    private final CodeableConcept verificationStatus;
    @Binding(
        bindingName = "ConditionCategory",
        strength = BindingStrength.Value.PREFERRED,
        description = "A category assigned to the condition.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-category"
    )
    private final List<CodeableConcept> category;
    @Binding(
        bindingName = "ConditionSeverity",
        strength = BindingStrength.Value.PREFERRED,
        description = "A subjective assessment of the severity of the condition as evaluated by the clinician.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-severity"
    )
    private final CodeableConcept severity;
    @Summary
    @Binding(
        bindingName = "ConditionKind",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Identification of the condition or diagnosis.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final CodeableConcept code;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final List<CodeableConcept> bodySite;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Age.class, Period.class, Range.class, String.class })
    private final Element onset;
    @Choice({ DateTime.class, Age.class, Period.class, Range.class, String.class })
    private final Element abatement;
    @Summary
    private final DateTime recordedDate;
    @Summary
    private final List<Participant> participant;
    private final List<Stage> stage;
    @Summary
    @Binding(
        bindingName = "ManifestationOrSymptom",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/clinical-findings"
    )
    private final List<CodeableReference> evidence;
    private final List<Annotation> note;

    private Condition(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        clinicalStatus = builder.clinicalStatus;
        verificationStatus = builder.verificationStatus;
        category = Collections.unmodifiableList(builder.category);
        severity = builder.severity;
        code = builder.code;
        bodySite = Collections.unmodifiableList(builder.bodySite);
        subject = builder.subject;
        encounter = builder.encounter;
        onset = builder.onset;
        abatement = builder.abatement;
        recordedDate = builder.recordedDate;
        participant = Collections.unmodifiableList(builder.participant);
        stage = Collections.unmodifiableList(builder.stage);
        evidence = Collections.unmodifiableList(builder.evidence);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Business identifiers assigned to this condition by the performer or other systems which remain constant as the 
     * resource is updated and propagates from server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The clinical status of the condition.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getClinicalStatus() {
        return clinicalStatus;
    }

    /**
     * The verification status to support the clinical status of the condition. The verification status pertains to the 
     * condition, itself, not to any specific condition attribute.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getVerificationStatus() {
        return verificationStatus;
    }

    /**
     * A category assigned to the condition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * A subjective assessment of the severity of the condition as evaluated by the clinician.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSeverity() {
        return severity;
    }

    /**
     * Identification of the condition, problem or diagnosis.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * The anatomical location where this condition manifests itself.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getBodySite() {
        return bodySite;
    }

    /**
     * Indicates the patient or group who the condition record is associated with.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The Encounter during which this Condition was created or to which the creation of this record is tightly associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Age}, {@link Period}, {@link Range} or {@link String} that may be 
     *     null.
     */
    public Element getOnset() {
        return onset;
    }

    /**
     * The date or estimated date that the condition resolved or went into remission. This is called "abatement" because of 
     * the many overloaded connotations associated with "remission" or "resolution" - Some conditions, such as chronic 
     * conditions, are never really resolved, but they can abate.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Age}, {@link Period}, {@link Range} or {@link String} that may be 
     *     null.
     */
    public Element getAbatement() {
        return abatement;
    }

    /**
     * The recordedDate represents when this particular Condition record was created in the system, which is often a system-
     * generated date.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getRecordedDate() {
        return recordedDate;
    }

    /**
     * Indicates who or what participated in the activities related to the condition and how they were involved.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Participant} that may be empty.
     */
    public List<Participant> getParticipant() {
        return participant;
    }

    /**
     * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
     * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Stage} that may be empty.
     */
    public List<Stage> getStage() {
        return stage;
    }

    /**
     * Supporting evidence / manifestations that are the basis of the Condition's verification status, such as evidence that 
     * confirmed or refuted the condition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getEvidence() {
        return evidence;
    }

    /**
     * Additional information about the Condition. This is a general notes/comments entry for description of the Condition, 
     * its diagnosis and prognosis.
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
            (clinicalStatus != null) || 
            (verificationStatus != null) || 
            !category.isEmpty() || 
            (severity != null) || 
            (code != null) || 
            !bodySite.isEmpty() || 
            (subject != null) || 
            (encounter != null) || 
            (onset != null) || 
            (abatement != null) || 
            (recordedDate != null) || 
            !participant.isEmpty() || 
            !stage.isEmpty() || 
            !evidence.isEmpty() || 
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
                accept(clinicalStatus, "clinicalStatus", visitor);
                accept(verificationStatus, "verificationStatus", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(severity, "severity", visitor);
                accept(code, "code", visitor);
                accept(bodySite, "bodySite", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(onset, "onset", visitor);
                accept(abatement, "abatement", visitor);
                accept(recordedDate, "recordedDate", visitor);
                accept(participant, "participant", visitor, Participant.class);
                accept(stage, "stage", visitor, Stage.class);
                accept(evidence, "evidence", visitor, CodeableReference.class);
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
        Condition other = (Condition) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(clinicalStatus, other.clinicalStatus) && 
            Objects.equals(verificationStatus, other.verificationStatus) && 
            Objects.equals(category, other.category) && 
            Objects.equals(severity, other.severity) && 
            Objects.equals(code, other.code) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(onset, other.onset) && 
            Objects.equals(abatement, other.abatement) && 
            Objects.equals(recordedDate, other.recordedDate) && 
            Objects.equals(participant, other.participant) && 
            Objects.equals(stage, other.stage) && 
            Objects.equals(evidence, other.evidence) && 
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
                clinicalStatus, 
                verificationStatus, 
                category, 
                severity, 
                code, 
                bodySite, 
                subject, 
                encounter, 
                onset, 
                abatement, 
                recordedDate, 
                participant, 
                stage, 
                evidence, 
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
        private CodeableConcept clinicalStatus;
        private CodeableConcept verificationStatus;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept severity;
        private CodeableConcept code;
        private List<CodeableConcept> bodySite = new ArrayList<>();
        private Reference subject;
        private Reference encounter;
        private Element onset;
        private Element abatement;
        private DateTime recordedDate;
        private List<Participant> participant = new ArrayList<>();
        private List<Stage> stage = new ArrayList<>();
        private List<CodeableReference> evidence = new ArrayList<>();
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
         * Business identifiers assigned to this condition by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this condition
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
         * Business identifiers assigned to this condition by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this condition
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
         * The clinical status of the condition.
         * 
         * <p>This element is required.
         * 
         * @param clinicalStatus
         *     active | recurrence | relapse | inactive | remission | resolved | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clinicalStatus(CodeableConcept clinicalStatus) {
            this.clinicalStatus = clinicalStatus;
            return this;
        }

        /**
         * The verification status to support the clinical status of the condition. The verification status pertains to the 
         * condition, itself, not to any specific condition attribute.
         * 
         * @param verificationStatus
         *     unconfirmed | provisional | differential | confirmed | refuted | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder verificationStatus(CodeableConcept verificationStatus) {
            this.verificationStatus = verificationStatus;
            return this;
        }

        /**
         * A category assigned to the condition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     problem-list-item | encounter-diagnosis
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
         * A category assigned to the condition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     problem-list-item | encounter-diagnosis
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
         * A subjective assessment of the severity of the condition as evaluated by the clinician.
         * 
         * @param severity
         *     Subjective severity of condition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder severity(CodeableConcept severity) {
            this.severity = severity;
            return this;
        }

        /**
         * Identification of the condition, problem or diagnosis.
         * 
         * @param code
         *     Identification of the condition, problem or diagnosis
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * The anatomical location where this condition manifests itself.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Anatomical location, if relevant
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableConcept... bodySite) {
            for (CodeableConcept value : bodySite) {
                this.bodySite.add(value);
            }
            return this;
        }

        /**
         * The anatomical location where this condition manifests itself.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Anatomical location, if relevant
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder bodySite(Collection<CodeableConcept> bodySite) {
            this.bodySite = new ArrayList<>(bodySite);
            return this;
        }

        /**
         * Indicates the patient or group who the condition record is associated with.
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
         *     Who has the condition?
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The Encounter during which this Condition was created or to which the creation of this record is tightly associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The Encounter during which this Condition was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Convenience method for setting {@code onset} with choice type String.
         * 
         * @param onset
         *     Estimated or actual date, date-time, or age
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #onset(Element)
         */
        public Builder onset(java.lang.String onset) {
            this.onset = (onset == null) ? null : String.of(onset);
            return this;
        }

        /**
         * Estimated or actual date or date-time the condition began, in the opinion of the clinician.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Age}</li>
         * <li>{@link Period}</li>
         * <li>{@link Range}</li>
         * <li>{@link String}</li>
         * </ul>
         * 
         * @param onset
         *     Estimated or actual date, date-time, or age
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder onset(Element onset) {
            this.onset = onset;
            return this;
        }

        /**
         * Convenience method for setting {@code abatement} with choice type String.
         * 
         * @param abatement
         *     When in resolution/remission
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #abatement(Element)
         */
        public Builder abatement(java.lang.String abatement) {
            this.abatement = (abatement == null) ? null : String.of(abatement);
            return this;
        }

        /**
         * The date or estimated date that the condition resolved or went into remission. This is called "abatement" because of 
         * the many overloaded connotations associated with "remission" or "resolution" - Some conditions, such as chronic 
         * conditions, are never really resolved, but they can abate.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Age}</li>
         * <li>{@link Period}</li>
         * <li>{@link Range}</li>
         * <li>{@link String}</li>
         * </ul>
         * 
         * @param abatement
         *     When in resolution/remission
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder abatement(Element abatement) {
            this.abatement = abatement;
            return this;
        }

        /**
         * The recordedDate represents when this particular Condition record was created in the system, which is often a system-
         * generated date.
         * 
         * @param recordedDate
         *     Date condition was first recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recordedDate(DateTime recordedDate) {
            this.recordedDate = recordedDate;
            return this;
        }

        /**
         * Indicates who or what participated in the activities related to the condition and how they were involved.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who or what participated in the activities related to the condition and how they were involved
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder participant(Participant... participant) {
            for (Participant value : participant) {
                this.participant.add(value);
            }
            return this;
        }

        /**
         * Indicates who or what participated in the activities related to the condition and how they were involved.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who or what participated in the activities related to the condition and how they were involved
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder participant(Collection<Participant> participant) {
            this.participant = new ArrayList<>(participant);
            return this;
        }

        /**
         * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
         * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param stage
         *     Stage/grade, usually assessed formally
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder stage(Stage... stage) {
            for (Stage value : stage) {
                this.stage.add(value);
            }
            return this;
        }

        /**
         * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
         * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param stage
         *     Stage/grade, usually assessed formally
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder stage(Collection<Stage> stage) {
            this.stage = new ArrayList<>(stage);
            return this;
        }

        /**
         * Supporting evidence / manifestations that are the basis of the Condition's verification status, such as evidence that 
         * confirmed or refuted the condition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param evidence
         *     Supporting evidence for the verification status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder evidence(CodeableReference... evidence) {
            for (CodeableReference value : evidence) {
                this.evidence.add(value);
            }
            return this;
        }

        /**
         * Supporting evidence / manifestations that are the basis of the Condition's verification status, such as evidence that 
         * confirmed or refuted the condition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param evidence
         *     Supporting evidence for the verification status
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder evidence(Collection<CodeableReference> evidence) {
            this.evidence = new ArrayList<>(evidence);
            return this;
        }

        /**
         * Additional information about the Condition. This is a general notes/comments entry for description of the Condition, 
         * its diagnosis and prognosis.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional information about the Condition
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
         * Additional information about the Condition. This is a general notes/comments entry for description of the Condition, 
         * its diagnosis and prognosis.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional information about the Condition
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
         * Build the {@link Condition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>clinicalStatus</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Condition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Condition per the base specification
         */
        @Override
        public Condition build() {
            Condition condition = new Condition(this);
            if (validating) {
                validate(condition);
            }
            return condition;
        }

        protected void validate(Condition condition) {
            super.validate(condition);
            ValidationSupport.checkList(condition.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(condition.clinicalStatus, "clinicalStatus");
            ValidationSupport.checkList(condition.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(condition.bodySite, "bodySite", CodeableConcept.class);
            ValidationSupport.requireNonNull(condition.subject, "subject");
            ValidationSupport.choiceElement(condition.onset, "onset", DateTime.class, Age.class, Period.class, Range.class, String.class);
            ValidationSupport.choiceElement(condition.abatement, "abatement", DateTime.class, Age.class, Period.class, Range.class, String.class);
            ValidationSupport.checkList(condition.participant, "participant", Participant.class);
            ValidationSupport.checkList(condition.stage, "stage", Stage.class);
            ValidationSupport.checkList(condition.evidence, "evidence", CodeableReference.class);
            ValidationSupport.checkList(condition.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(condition.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(condition.encounter, "encounter", "Encounter");
        }

        protected Builder from(Condition condition) {
            super.from(condition);
            identifier.addAll(condition.identifier);
            clinicalStatus = condition.clinicalStatus;
            verificationStatus = condition.verificationStatus;
            category.addAll(condition.category);
            severity = condition.severity;
            code = condition.code;
            bodySite.addAll(condition.bodySite);
            subject = condition.subject;
            encounter = condition.encounter;
            onset = condition.onset;
            abatement = condition.abatement;
            recordedDate = condition.recordedDate;
            participant.addAll(condition.participant);
            stage.addAll(condition.stage);
            evidence.addAll(condition.evidence);
            note.addAll(condition.note);
            return this;
        }
    }

    /**
     * Indicates who or what participated in the activities related to the condition and how they were involved.
     */
    public static class Participant extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "ConditionParticipantFunction",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/participation-role-type"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Patient", "RelatedPerson", "Device", "Organization", "CareTeam" })
        @Required
        private final Reference actor;

        private Participant(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Distinguishes the type of involvement of the actor in the activities related to the condition.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Indicates who or what participated in the activities related to the condition.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
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
            Participant other = (Participant) obj;
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
             * Distinguishes the type of involvement of the actor in the activities related to the condition.
             * 
             * @param function
             *     Type of involvement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Indicates who or what participated in the activities related to the condition.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Device}</li>
             * <li>{@link Organization}</li>
             * <li>{@link CareTeam}</li>
             * </ul>
             * 
             * @param actor
             *     Who or what participated in the activities related to the condition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
                this.actor = actor;
                return this;
            }

            /**
             * Build the {@link Participant}
             * 
             * <p>Required elements:
             * <ul>
             * <li>actor</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Participant}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Participant per the base specification
             */
            @Override
            public Participant build() {
                Participant participant = new Participant(this);
                if (validating) {
                    validate(participant);
                }
                return participant;
            }

            protected void validate(Participant participant) {
                super.validate(participant);
                ValidationSupport.requireNonNull(participant.actor, "actor");
                ValidationSupport.checkReferenceType(participant.actor, "actor", "Practitioner", "PractitionerRole", "Patient", "RelatedPerson", "Device", "Organization", "CareTeam");
                ValidationSupport.requireValueOrChildren(participant);
            }

            protected Builder from(Participant participant) {
                super.from(participant);
                function = participant.function;
                actor = participant.actor;
                return this;
            }
        }
    }

    /**
     * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
     * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
     */
    public static class Stage extends BackboneElement {
        @Binding(
            bindingName = "ConditionStage",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing condition stages (e.g. Cancer stages).",
            valueSet = "http://hl7.org/fhir/ValueSet/condition-stage"
        )
        private final CodeableConcept summary;
        @ReferenceTarget({ "ClinicalImpression", "DiagnosticReport", "Observation" })
        private final List<Reference> assessment;
        @Binding(
            bindingName = "ConditionStageType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the kind of condition staging (e.g. clinical or pathological).",
            valueSet = "http://hl7.org/fhir/ValueSet/condition-stage-type"
        )
        private final CodeableConcept type;

        private Stage(Builder builder) {
            super(builder);
            summary = builder.summary;
            assessment = Collections.unmodifiableList(builder.assessment);
            type = builder.type;
        }

        /**
         * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
         * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getSummary() {
            return summary;
        }

        /**
         * Reference to a formal record of the evidence on which the staging assessment is based.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getAssessment() {
            return assessment;
        }

        /**
         * The kind of staging, such as pathological or clinical staging.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (summary != null) || 
                !assessment.isEmpty() || 
                (type != null);
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
                    accept(summary, "summary", visitor);
                    accept(assessment, "assessment", visitor, Reference.class);
                    accept(type, "type", visitor);
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
            Stage other = (Stage) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(summary, other.summary) && 
                Objects.equals(assessment, other.assessment) && 
                Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    summary, 
                    assessment, 
                    type);
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
            private CodeableConcept summary;
            private List<Reference> assessment = new ArrayList<>();
            private CodeableConcept type;

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
             * A simple summary of the stage such as "Stage 3" or "Early Onset". The determination of the stage is disease-specific, 
             * such as cancer, retinopathy of prematurity, kidney diseases, Alzheimer's, or Parkinson disease.
             * 
             * @param summary
             *     Simple summary (disease specific)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder summary(CodeableConcept summary) {
                this.summary = summary;
                return this;
            }

            /**
             * Reference to a formal record of the evidence on which the staging assessment is based.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link ClinicalImpression}</li>
             * <li>{@link DiagnosticReport}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param assessment
             *     Formal record of assessment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder assessment(Reference... assessment) {
                for (Reference value : assessment) {
                    this.assessment.add(value);
                }
                return this;
            }

            /**
             * Reference to a formal record of the evidence on which the staging assessment is based.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link ClinicalImpression}</li>
             * <li>{@link DiagnosticReport}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param assessment
             *     Formal record of assessment
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder assessment(Collection<Reference> assessment) {
                this.assessment = new ArrayList<>(assessment);
                return this;
            }

            /**
             * The kind of staging, such as pathological or clinical staging.
             * 
             * @param type
             *     Kind of staging
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Build the {@link Stage}
             * 
             * @return
             *     An immutable object of type {@link Stage}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Stage per the base specification
             */
            @Override
            public Stage build() {
                Stage stage = new Stage(this);
                if (validating) {
                    validate(stage);
                }
                return stage;
            }

            protected void validate(Stage stage) {
                super.validate(stage);
                ValidationSupport.checkList(stage.assessment, "assessment", Reference.class);
                ValidationSupport.checkReferenceType(stage.assessment, "assessment", "ClinicalImpression", "DiagnosticReport", "Observation");
                ValidationSupport.requireValueOrChildren(stage);
            }

            protected Builder from(Stage stage) {
                super.from(stage);
                summary = stage.summary;
                assessment.addAll(stage.assessment);
                type = stage.type;
                return this;
            }
        }
    }
}
