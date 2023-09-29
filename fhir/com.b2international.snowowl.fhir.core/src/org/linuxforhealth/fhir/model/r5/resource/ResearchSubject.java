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
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A ResearchSubject is a participant or object which is the recipient of investigative activities in a research study.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ResearchSubject extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes that convey the current publication status of the research study resource.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    private final List<Progress> progress;
    @Summary
    private final Period period;
    @Summary
    @ReferenceTarget({ "ResearchStudy" })
    @Required
    private final Reference study;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Specimen", "Device", "Medication", "Substance", "BiologicallyDerivedProduct" })
    @Required
    private final Reference subject;
    private final Id assignedComparisonGroup;
    private final Id actualComparisonGroup;
    @ReferenceTarget({ "Consent" })
    private final List<Reference> consent;

    private ResearchSubject(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        progress = Collections.unmodifiableList(builder.progress);
        period = builder.period;
        study = builder.study;
        subject = builder.subject;
        assignedComparisonGroup = builder.assignedComparisonGroup;
        actualComparisonGroup = builder.actualComparisonGroup;
        consent = Collections.unmodifiableList(builder.consent);
    }

    /**
     * Identifiers assigned to this research subject for a study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The publication state of the resource (not of the subject).
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * The current state (status) of the subject and resons for status change where appropriate.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Progress} that may be empty.
     */
    public List<Progress> getProgress() {
        return progress;
    }

    /**
     * The dates the subject began and ended their participation in the study.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Reference to the study the subject is participating in.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getStudy() {
        return study;
    }

    /**
     * The record of the person, animal or other entity involved in the study.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The name of the arm in the study the subject is expected to follow as part of this study.
     * 
     * @return
     *     An immutable object of type {@link Id} that may be null.
     */
    public Id getAssignedComparisonGroup() {
        return assignedComparisonGroup;
    }

    /**
     * The name of the arm in the study the subject actually followed as part of this study.
     * 
     * @return
     *     An immutable object of type {@link Id} that may be null.
     */
    public Id getActualComparisonGroup() {
        return actualComparisonGroup;
    }

    /**
     * A record of the patient's informed agreement to participate in the study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getConsent() {
        return consent;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !progress.isEmpty() || 
            (period != null) || 
            (study != null) || 
            (subject != null) || 
            (assignedComparisonGroup != null) || 
            (actualComparisonGroup != null) || 
            !consent.isEmpty();
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
                accept(progress, "progress", visitor, Progress.class);
                accept(period, "period", visitor);
                accept(study, "study", visitor);
                accept(subject, "subject", visitor);
                accept(assignedComparisonGroup, "assignedComparisonGroup", visitor);
                accept(actualComparisonGroup, "actualComparisonGroup", visitor);
                accept(consent, "consent", visitor, Reference.class);
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
        ResearchSubject other = (ResearchSubject) obj;
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
            Objects.equals(progress, other.progress) && 
            Objects.equals(period, other.period) && 
            Objects.equals(study, other.study) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(assignedComparisonGroup, other.assignedComparisonGroup) && 
            Objects.equals(actualComparisonGroup, other.actualComparisonGroup) && 
            Objects.equals(consent, other.consent);
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
                progress, 
                period, 
                study, 
                subject, 
                assignedComparisonGroup, 
                actualComparisonGroup, 
                consent);
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
        private PublicationStatus status;
        private List<Progress> progress = new ArrayList<>();
        private Period period;
        private Reference study;
        private Reference subject;
        private Id assignedComparisonGroup;
        private Id actualComparisonGroup;
        private List<Reference> consent = new ArrayList<>();

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
         * Identifiers assigned to this research subject for a study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for research subject in a study
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
         * Identifiers assigned to this research subject for a study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for research subject in a study
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
         * The publication state of the resource (not of the subject).
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(PublicationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The current state (status) of the subject and resons for status change where appropriate.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param progress
         *     Subject status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder progress(Progress... progress) {
            for (Progress value : progress) {
                this.progress.add(value);
            }
            return this;
        }

        /**
         * The current state (status) of the subject and resons for status change where appropriate.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param progress
         *     Subject status
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder progress(Collection<Progress> progress) {
            this.progress = new ArrayList<>(progress);
            return this;
        }

        /**
         * The dates the subject began and ended their participation in the study.
         * 
         * @param period
         *     Start and end of participation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Reference to the study the subject is participating in.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link ResearchStudy}</li>
         * </ul>
         * 
         * @param study
         *     Study subject is part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder study(Reference study) {
            this.study = study;
            return this;
        }

        /**
         * The record of the person, animal or other entity involved in the study.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Specimen}</li>
         * <li>{@link Device}</li>
         * <li>{@link Medication}</li>
         * <li>{@link Substance}</li>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * </ul>
         * 
         * @param subject
         *     Who or what is part of study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The name of the arm in the study the subject is expected to follow as part of this study.
         * 
         * @param assignedComparisonGroup
         *     What path should be followed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder assignedComparisonGroup(Id assignedComparisonGroup) {
            this.assignedComparisonGroup = assignedComparisonGroup;
            return this;
        }

        /**
         * The name of the arm in the study the subject actually followed as part of this study.
         * 
         * @param actualComparisonGroup
         *     What path was followed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actualComparisonGroup(Id actualComparisonGroup) {
            this.actualComparisonGroup = actualComparisonGroup;
            return this;
        }

        /**
         * A record of the patient's informed agreement to participate in the study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Consent}</li>
         * </ul>
         * 
         * @param consent
         *     Agreement to participate in study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder consent(Reference... consent) {
            for (Reference value : consent) {
                this.consent.add(value);
            }
            return this;
        }

        /**
         * A record of the patient's informed agreement to participate in the study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Consent}</li>
         * </ul>
         * 
         * @param consent
         *     Agreement to participate in study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder consent(Collection<Reference> consent) {
            this.consent = new ArrayList<>(consent);
            return this;
        }

        /**
         * Build the {@link ResearchSubject}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>study</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ResearchSubject}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ResearchSubject per the base specification
         */
        @Override
        public ResearchSubject build() {
            ResearchSubject researchSubject = new ResearchSubject(this);
            if (validating) {
                validate(researchSubject);
            }
            return researchSubject;
        }

        protected void validate(ResearchSubject researchSubject) {
            super.validate(researchSubject);
            ValidationSupport.checkList(researchSubject.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(researchSubject.status, "status");
            ValidationSupport.checkList(researchSubject.progress, "progress", Progress.class);
            ValidationSupport.requireNonNull(researchSubject.study, "study");
            ValidationSupport.requireNonNull(researchSubject.subject, "subject");
            ValidationSupport.checkList(researchSubject.consent, "consent", Reference.class);
            ValidationSupport.checkReferenceType(researchSubject.study, "study", "ResearchStudy");
            ValidationSupport.checkReferenceType(researchSubject.subject, "subject", "Patient", "Group", "Specimen", "Device", "Medication", "Substance", "BiologicallyDerivedProduct");
            ValidationSupport.checkReferenceType(researchSubject.consent, "consent", "Consent");
        }

        protected Builder from(ResearchSubject researchSubject) {
            super.from(researchSubject);
            identifier.addAll(researchSubject.identifier);
            status = researchSubject.status;
            progress.addAll(researchSubject.progress);
            period = researchSubject.period;
            study = researchSubject.study;
            subject = researchSubject.subject;
            assignedComparisonGroup = researchSubject.assignedComparisonGroup;
            actualComparisonGroup = researchSubject.actualComparisonGroup;
            consent.addAll(researchSubject.consent);
            return this;
        }
    }

    /**
     * The current state (status) of the subject and resons for status change where appropriate.
     */
    public static class Progress extends BackboneElement {
        @Binding(
            bindingName = "ResearchSubjectStateType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Identifies the kind of state being refered to.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-subject-state-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "ResearchSubjectProgresss",
            strength = BindingStrength.Value.REQUIRED,
            description = "Indicates the progression of a study subject through a study.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-subject-state|5.0.0"
        )
        private final CodeableConcept subjectState;
        @Binding(
            bindingName = "ResearchSubjectMilestone",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Indicates the progression of a study subject through the study milestones.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-subject-milestone"
        )
        private final CodeableConcept milestone;
        @Binding(
            bindingName = "StateChangeReason",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Indicates why the state of the subject changed.",
            valueSet = "http://terminology.hl7.org/ValueSet/state-change-reason"
        )
        private final CodeableConcept reason;
        private final DateTime startDate;
        private final DateTime endDate;

        private Progress(Builder builder) {
            super(builder);
            type = builder.type;
            subjectState = builder.subjectState;
            milestone = builder.milestone;
            reason = builder.reason;
            startDate = builder.startDate;
            endDate = builder.endDate;
        }

        /**
         * Identifies the aspect of the subject's journey that the state refers to.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The current state of the subject.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getSubjectState() {
            return subjectState;
        }

        /**
         * The milestones the subject has passed through.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getMilestone() {
            return milestone;
        }

        /**
         * The reason for the state change. If coded it should follow the formal subject state model.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getReason() {
            return reason;
        }

        /**
         * The date when the new status started.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getStartDate() {
            return startDate;
        }

        /**
         * The date when the state ended.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getEndDate() {
            return endDate;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (subjectState != null) || 
                (milestone != null) || 
                (reason != null) || 
                (startDate != null) || 
                (endDate != null);
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
                    accept(type, "type", visitor);
                    accept(subjectState, "subjectState", visitor);
                    accept(milestone, "milestone", visitor);
                    accept(reason, "reason", visitor);
                    accept(startDate, "startDate", visitor);
                    accept(endDate, "endDate", visitor);
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
            Progress other = (Progress) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(subjectState, other.subjectState) && 
                Objects.equals(milestone, other.milestone) && 
                Objects.equals(reason, other.reason) && 
                Objects.equals(startDate, other.startDate) && 
                Objects.equals(endDate, other.endDate);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    subjectState, 
                    milestone, 
                    reason, 
                    startDate, 
                    endDate);
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
            private CodeableConcept type;
            private CodeableConcept subjectState;
            private CodeableConcept milestone;
            private CodeableConcept reason;
            private DateTime startDate;
            private DateTime endDate;

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
             * Identifies the aspect of the subject's journey that the state refers to.
             * 
             * @param type
             *     state | milestone
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The current state of the subject.
             * 
             * @param subjectState
             *     candidate | eligible | follow-up | ineligible | not-registered | off-study | on-study | on-study-intervention | on-
             *     study-observation | pending-on-study | potential-candidate | screening | withdrawn
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subjectState(CodeableConcept subjectState) {
                this.subjectState = subjectState;
                return this;
            }

            /**
             * The milestones the subject has passed through.
             * 
             * @param milestone
             *     SignedUp | Screened | Randomized
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder milestone(CodeableConcept milestone) {
                this.milestone = milestone;
                return this;
            }

            /**
             * The reason for the state change. If coded it should follow the formal subject state model.
             * 
             * @param reason
             *     State change reason
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reason(CodeableConcept reason) {
                this.reason = reason;
                return this;
            }

            /**
             * The date when the new status started.
             * 
             * @param startDate
             *     State change date
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder startDate(DateTime startDate) {
                this.startDate = startDate;
                return this;
            }

            /**
             * The date when the state ended.
             * 
             * @param endDate
             *     State change date
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder endDate(DateTime endDate) {
                this.endDate = endDate;
                return this;
            }

            /**
             * Build the {@link Progress}
             * 
             * @return
             *     An immutable object of type {@link Progress}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Progress per the base specification
             */
            @Override
            public Progress build() {
                Progress progress = new Progress(this);
                if (validating) {
                    validate(progress);
                }
                return progress;
            }

            protected void validate(Progress progress) {
                super.validate(progress);
                ValidationSupport.requireValueOrChildren(progress);
            }

            protected Builder from(Progress progress) {
                super.from(progress);
                type = progress.type;
                subjectState = progress.subjectState;
                milestone = progress.milestone;
                reason = progress.reason;
                startDate = progress.startDate;
                endDate = progress.endDate;
                return this;
            }
        }
    }
}
