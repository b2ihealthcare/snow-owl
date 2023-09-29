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
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
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
import org.linuxforhealth.fhir.model.r5.type.code.AdverseEventActuality;
import org.linuxforhealth.fhir.model.r5.type.code.AdverseEventStatus;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * An event (i.e. any change to current patient status) that may be related to unintended effects on a patient or 
 * research participant. The unintended effects may require additional monitoring, treatment, hospitalization, or may 
 * result in death. The AdverseEvent resource also extends to potential or avoided events that could have had such 
 * effects. There are two major domains where the AdverseEvent resource is expected to be used. One is in clinical care 
 * reported adverse events and the other is in reporting adverse events in clinical research trial management. Adverse 
 * events can be reported by healthcare providers, patients, caregivers or by medical products manufacturers. Given the 
 * differences between these two concepts, we recommend consulting the domain specific implementation guides when 
 * implementing the AdverseEvent Resource. The implementation guides include specific extensions, value sets and 
 * constraints.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AdverseEvent extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "AdverseEventStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes identifying the lifecycle stage of an event.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-status|5.0.0"
    )
    @Required
    private final AdverseEventStatus status;
    @Summary
    @Binding(
        bindingName = "AdverseEventActuality",
        strength = BindingStrength.Value.REQUIRED,
        description = "Overall nature of the adverse event, e.g. real or potential.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-actuality|5.0.0"
    )
    @Required
    private final AdverseEventActuality actuality;
    @Summary
    @Binding(
        bindingName = "AdverseEventCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Overall categorization of the event, e.g. product-related or situational.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "AdverseEventType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Detailed type of event.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-type"
    )
    private final CodeableConcept code;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Practitioner", "RelatedPerson", "ResearchSubject" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    private final Element occurrence;
    @Summary
    private final DateTime detected;
    @Summary
    private final DateTime recordedDate;
    @Summary
    @ReferenceTarget({ "Condition", "Observation" })
    private final List<Reference> resultingEffect;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Summary
    @Binding(
        bindingName = "AdverseEventSeriousness",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Overall seriousness of this event for the patient.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-seriousness"
    )
    private final CodeableConcept seriousness;
    @Summary
    @Binding(
        bindingName = "AdverseEventOutcome",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes describing the type of outcome from the adverse event.",
        valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-outcome"
    )
    private final List<CodeableConcept> outcome;
    @Summary
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "ResearchSubject" })
    private final Reference recorder;
    @Summary
    private final List<Participant> participant;
    @Summary
    @ReferenceTarget({ "ResearchStudy" })
    private final List<Reference> study;
    private final Boolean expectedInResearchStudy;
    @Summary
    private final List<SuspectEntity> suspectEntity;
    @Summary
    private final List<ContributingFactor> contributingFactor;
    @Summary
    private final List<PreventiveAction> preventiveAction;
    @Summary
    private final List<MitigatingAction> mitigatingAction;
    @Summary
    private final List<SupportingInfo> supportingInfo;
    @Summary
    private final List<Annotation> note;

    private AdverseEvent(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        actuality = builder.actuality;
        category = Collections.unmodifiableList(builder.category);
        code = builder.code;
        subject = builder.subject;
        encounter = builder.encounter;
        occurrence = builder.occurrence;
        detected = builder.detected;
        recordedDate = builder.recordedDate;
        resultingEffect = Collections.unmodifiableList(builder.resultingEffect);
        location = builder.location;
        seriousness = builder.seriousness;
        outcome = Collections.unmodifiableList(builder.outcome);
        recorder = builder.recorder;
        participant = Collections.unmodifiableList(builder.participant);
        study = Collections.unmodifiableList(builder.study);
        expectedInResearchStudy = builder.expectedInResearchStudy;
        suspectEntity = Collections.unmodifiableList(builder.suspectEntity);
        contributingFactor = Collections.unmodifiableList(builder.contributingFactor);
        preventiveAction = Collections.unmodifiableList(builder.preventiveAction);
        mitigatingAction = Collections.unmodifiableList(builder.mitigatingAction);
        supportingInfo = Collections.unmodifiableList(builder.supportingInfo);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Business identifiers assigned to this adverse event by the performer or other systems which remain constant as the 
     * resource is updated and propagates from server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The current state of the adverse event or potential adverse event.
     * 
     * @return
     *     An immutable object of type {@link AdverseEventStatus} that is non-null.
     */
    public AdverseEventStatus getStatus() {
        return status;
    }

    /**
     * Whether the event actually happened or was a near miss. Note that this is independent of whether anyone was affected 
     * or harmed or how severely.
     * 
     * @return
     *     An immutable object of type {@link AdverseEventActuality} that is non-null.
     */
    public AdverseEventActuality getActuality() {
        return actuality;
    }

    /**
     * The overall type of event, intended for search and filtering purposes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Specific event that occurred or that was averted, such as patient fall, wrong organ removed, or wrong blood transfused.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * This subject or group impacted by the event.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The Encounter associated with the start of the AdverseEvent.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The date (and perhaps time) when the adverse event occurred.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * Estimated or actual date the AdverseEvent began, in the opinion of the reporter.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDetected() {
        return detected;
    }

    /**
     * The date on which the existence of the AdverseEvent was first recorded.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getRecordedDate() {
        return recordedDate;
    }

    /**
     * Information about the condition that occurred as a result of the adverse event, such as hives due to the exposure to a 
     * substance (for example, a drug or a chemical) or a broken leg as a result of the fall.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getResultingEffect() {
        return resultingEffect;
    }

    /**
     * The information about where the adverse event occurred.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Assessment whether this event, or averted event, was of clinical importance.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSeriousness() {
        return seriousness;
    }

    /**
     * Describes the type of outcome from the adverse event, such as resolved, recovering, ongoing, resolved-with-sequelae, 
     * or fatal.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getOutcome() {
        return outcome;
    }

    /**
     * Information on who recorded the adverse event. May be the patient or a practitioner.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRecorder() {
        return recorder;
    }

    /**
     * Indicates who or what participated in the adverse event and how they were involved.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Participant} that may be empty.
     */
    public List<Participant> getParticipant() {
        return participant;
    }

    /**
     * The research study that the subject is enrolled in.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getStudy() {
        return study;
    }

    /**
     * Considered likely or probable or anticipated in the research study. Whether the reported event matches any of the 
     * outcomes for the patient that are considered by the study as known or likely.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExpectedInResearchStudy() {
        return expectedInResearchStudy;
    }

    /**
     * Describes the entity that is suspected to have caused the adverse event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link SuspectEntity} that may be empty.
     */
    public List<SuspectEntity> getSuspectEntity() {
        return suspectEntity;
    }

    /**
     * The contributing factors suspected to have increased the probability or severity of the adverse event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContributingFactor} that may be empty.
     */
    public List<ContributingFactor> getContributingFactor() {
        return contributingFactor;
    }

    /**
     * Preventive actions that contributed to avoiding the adverse event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link PreventiveAction} that may be empty.
     */
    public List<PreventiveAction> getPreventiveAction() {
        return preventiveAction;
    }

    /**
     * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link MitigatingAction} that may be empty.
     */
    public List<MitigatingAction> getMitigatingAction() {
        return mitigatingAction;
    }

    /**
     * Supporting information relevant to the event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link SupportingInfo} that may be empty.
     */
    public List<SupportingInfo> getSupportingInfo() {
        return supportingInfo;
    }

    /**
     * Comments made about the adverse event by the performer, subject or other participants.
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
            (status != null) || 
            (actuality != null) || 
            !category.isEmpty() || 
            (code != null) || 
            (subject != null) || 
            (encounter != null) || 
            (occurrence != null) || 
            (detected != null) || 
            (recordedDate != null) || 
            !resultingEffect.isEmpty() || 
            (location != null) || 
            (seriousness != null) || 
            !outcome.isEmpty() || 
            (recorder != null) || 
            !participant.isEmpty() || 
            !study.isEmpty() || 
            (expectedInResearchStudy != null) || 
            !suspectEntity.isEmpty() || 
            !contributingFactor.isEmpty() || 
            !preventiveAction.isEmpty() || 
            !mitigatingAction.isEmpty() || 
            !supportingInfo.isEmpty() || 
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
                accept(status, "status", visitor);
                accept(actuality, "actuality", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(occurrence, "occurrence", visitor);
                accept(detected, "detected", visitor);
                accept(recordedDate, "recordedDate", visitor);
                accept(resultingEffect, "resultingEffect", visitor, Reference.class);
                accept(location, "location", visitor);
                accept(seriousness, "seriousness", visitor);
                accept(outcome, "outcome", visitor, CodeableConcept.class);
                accept(recorder, "recorder", visitor);
                accept(participant, "participant", visitor, Participant.class);
                accept(study, "study", visitor, Reference.class);
                accept(expectedInResearchStudy, "expectedInResearchStudy", visitor);
                accept(suspectEntity, "suspectEntity", visitor, SuspectEntity.class);
                accept(contributingFactor, "contributingFactor", visitor, ContributingFactor.class);
                accept(preventiveAction, "preventiveAction", visitor, PreventiveAction.class);
                accept(mitigatingAction, "mitigatingAction", visitor, MitigatingAction.class);
                accept(supportingInfo, "supportingInfo", visitor, SupportingInfo.class);
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
        AdverseEvent other = (AdverseEvent) obj;
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
            Objects.equals(actuality, other.actuality) && 
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(detected, other.detected) && 
            Objects.equals(recordedDate, other.recordedDate) && 
            Objects.equals(resultingEffect, other.resultingEffect) && 
            Objects.equals(location, other.location) && 
            Objects.equals(seriousness, other.seriousness) && 
            Objects.equals(outcome, other.outcome) && 
            Objects.equals(recorder, other.recorder) && 
            Objects.equals(participant, other.participant) && 
            Objects.equals(study, other.study) && 
            Objects.equals(expectedInResearchStudy, other.expectedInResearchStudy) && 
            Objects.equals(suspectEntity, other.suspectEntity) && 
            Objects.equals(contributingFactor, other.contributingFactor) && 
            Objects.equals(preventiveAction, other.preventiveAction) && 
            Objects.equals(mitigatingAction, other.mitigatingAction) && 
            Objects.equals(supportingInfo, other.supportingInfo) && 
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
                status, 
                actuality, 
                category, 
                code, 
                subject, 
                encounter, 
                occurrence, 
                detected, 
                recordedDate, 
                resultingEffect, 
                location, 
                seriousness, 
                outcome, 
                recorder, 
                participant, 
                study, 
                expectedInResearchStudy, 
                suspectEntity, 
                contributingFactor, 
                preventiveAction, 
                mitigatingAction, 
                supportingInfo, 
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
        private AdverseEventStatus status;
        private AdverseEventActuality actuality;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept code;
        private Reference subject;
        private Reference encounter;
        private Element occurrence;
        private DateTime detected;
        private DateTime recordedDate;
        private List<Reference> resultingEffect = new ArrayList<>();
        private Reference location;
        private CodeableConcept seriousness;
        private List<CodeableConcept> outcome = new ArrayList<>();
        private Reference recorder;
        private List<Participant> participant = new ArrayList<>();
        private List<Reference> study = new ArrayList<>();
        private Boolean expectedInResearchStudy;
        private List<SuspectEntity> suspectEntity = new ArrayList<>();
        private List<ContributingFactor> contributingFactor = new ArrayList<>();
        private List<PreventiveAction> preventiveAction = new ArrayList<>();
        private List<MitigatingAction> mitigatingAction = new ArrayList<>();
        private List<SupportingInfo> supportingInfo = new ArrayList<>();
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
         * Business identifiers assigned to this adverse event by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the event
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
         * Business identifiers assigned to this adverse event by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the event
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
         * The current state of the adverse event or potential adverse event.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     in-progress | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(AdverseEventStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Whether the event actually happened or was a near miss. Note that this is independent of whether anyone was affected 
         * or harmed or how severely.
         * 
         * <p>This element is required.
         * 
         * @param actuality
         *     actual | potential
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actuality(AdverseEventActuality actuality) {
            this.actuality = actuality;
            return this;
        }

        /**
         * The overall type of event, intended for search and filtering purposes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     wrong-patient | procedure-mishap | medication-mishap | device | unsafe-physical-environment | hospital-aquired-
         *     infection | wrong-body-site
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
         * The overall type of event, intended for search and filtering purposes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     wrong-patient | procedure-mishap | medication-mishap | device | unsafe-physical-environment | hospital-aquired-
         *     infection | wrong-body-site
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
         * Specific event that occurred or that was averted, such as patient fall, wrong organ removed, or wrong blood transfused.
         * 
         * @param code
         *     Event or incident that occurred or was averted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * This subject or group impacted by the event.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link ResearchSubject}</li>
         * </ul>
         * 
         * @param subject
         *     Subject impacted by event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The Encounter associated with the start of the AdverseEvent.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The Encounter associated with the start of the AdverseEvent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The date (and perhaps time) when the adverse event occurred.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurrence
         *     When the event occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * Estimated or actual date the AdverseEvent began, in the opinion of the reporter.
         * 
         * @param detected
         *     When the event was detected
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder detected(DateTime detected) {
            this.detected = detected;
            return this;
        }

        /**
         * The date on which the existence of the AdverseEvent was first recorded.
         * 
         * @param recordedDate
         *     When the event was recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recordedDate(DateTime recordedDate) {
            this.recordedDate = recordedDate;
            return this;
        }

        /**
         * Information about the condition that occurred as a result of the adverse event, such as hives due to the exposure to a 
         * substance (for example, a drug or a chemical) or a broken leg as a result of the fall.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Condition}</li>
         * <li>{@link Observation}</li>
         * </ul>
         * 
         * @param resultingEffect
         *     Effect on the subject due to this event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder resultingEffect(Reference... resultingEffect) {
            for (Reference value : resultingEffect) {
                this.resultingEffect.add(value);
            }
            return this;
        }

        /**
         * Information about the condition that occurred as a result of the adverse event, such as hives due to the exposure to a 
         * substance (for example, a drug or a chemical) or a broken leg as a result of the fall.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Condition}</li>
         * <li>{@link Observation}</li>
         * </ul>
         * 
         * @param resultingEffect
         *     Effect on the subject due to this event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder resultingEffect(Collection<Reference> resultingEffect) {
            this.resultingEffect = new ArrayList<>(resultingEffect);
            return this;
        }

        /**
         * The information about where the adverse event occurred.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Location where adverse event occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Assessment whether this event, or averted event, was of clinical importance.
         * 
         * @param seriousness
         *     Seriousness or gravity of the event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder seriousness(CodeableConcept seriousness) {
            this.seriousness = seriousness;
            return this;
        }

        /**
         * Describes the type of outcome from the adverse event, such as resolved, recovering, ongoing, resolved-with-sequelae, 
         * or fatal.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param outcome
         *     Type of outcome from the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcome(CodeableConcept... outcome) {
            for (CodeableConcept value : outcome) {
                this.outcome.add(value);
            }
            return this;
        }

        /**
         * Describes the type of outcome from the adverse event, such as resolved, recovering, ongoing, resolved-with-sequelae, 
         * or fatal.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param outcome
         *     Type of outcome from the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder outcome(Collection<CodeableConcept> outcome) {
            this.outcome = new ArrayList<>(outcome);
            return this;
        }

        /**
         * Information on who recorded the adverse event. May be the patient or a practitioner.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link ResearchSubject}</li>
         * </ul>
         * 
         * @param recorder
         *     Who recorded the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorder(Reference recorder) {
            this.recorder = recorder;
            return this;
        }

        /**
         * Indicates who or what participated in the adverse event and how they were involved.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who was involved in the adverse event or the potential adverse event and what they did
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
         * Indicates who or what participated in the adverse event and how they were involved.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who was involved in the adverse event or the potential adverse event and what they did
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
         * The research study that the subject is enrolled in.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ResearchStudy}</li>
         * </ul>
         * 
         * @param study
         *     Research study that the subject is enrolled in
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder study(Reference... study) {
            for (Reference value : study) {
                this.study.add(value);
            }
            return this;
        }

        /**
         * The research study that the subject is enrolled in.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ResearchStudy}</li>
         * </ul>
         * 
         * @param study
         *     Research study that the subject is enrolled in
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder study(Collection<Reference> study) {
            this.study = new ArrayList<>(study);
            return this;
        }

        /**
         * Convenience method for setting {@code expectedInResearchStudy}.
         * 
         * @param expectedInResearchStudy
         *     Considered likely or probable or anticipated in the research study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #expectedInResearchStudy(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder expectedInResearchStudy(java.lang.Boolean expectedInResearchStudy) {
            this.expectedInResearchStudy = (expectedInResearchStudy == null) ? null : Boolean.of(expectedInResearchStudy);
            return this;
        }

        /**
         * Considered likely or probable or anticipated in the research study. Whether the reported event matches any of the 
         * outcomes for the patient that are considered by the study as known or likely.
         * 
         * @param expectedInResearchStudy
         *     Considered likely or probable or anticipated in the research study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expectedInResearchStudy(Boolean expectedInResearchStudy) {
            this.expectedInResearchStudy = expectedInResearchStudy;
            return this;
        }

        /**
         * Describes the entity that is suspected to have caused the adverse event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param suspectEntity
         *     The suspected agent causing the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder suspectEntity(SuspectEntity... suspectEntity) {
            for (SuspectEntity value : suspectEntity) {
                this.suspectEntity.add(value);
            }
            return this;
        }

        /**
         * Describes the entity that is suspected to have caused the adverse event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param suspectEntity
         *     The suspected agent causing the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder suspectEntity(Collection<SuspectEntity> suspectEntity) {
            this.suspectEntity = new ArrayList<>(suspectEntity);
            return this;
        }

        /**
         * The contributing factors suspected to have increased the probability or severity of the adverse event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contributingFactor
         *     Contributing factors suspected to have increased the probability or severity of the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contributingFactor(ContributingFactor... contributingFactor) {
            for (ContributingFactor value : contributingFactor) {
                this.contributingFactor.add(value);
            }
            return this;
        }

        /**
         * The contributing factors suspected to have increased the probability or severity of the adverse event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contributingFactor
         *     Contributing factors suspected to have increased the probability or severity of the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contributingFactor(Collection<ContributingFactor> contributingFactor) {
            this.contributingFactor = new ArrayList<>(contributingFactor);
            return this;
        }

        /**
         * Preventive actions that contributed to avoiding the adverse event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param preventiveAction
         *     Preventive actions that contributed to avoiding the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preventiveAction(PreventiveAction... preventiveAction) {
            for (PreventiveAction value : preventiveAction) {
                this.preventiveAction.add(value);
            }
            return this;
        }

        /**
         * Preventive actions that contributed to avoiding the adverse event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param preventiveAction
         *     Preventive actions that contributed to avoiding the adverse event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder preventiveAction(Collection<PreventiveAction> preventiveAction) {
            this.preventiveAction = new ArrayList<>(preventiveAction);
            return this;
        }

        /**
         * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param mitigatingAction
         *     Ameliorating actions taken after the adverse event occured in order to reduce the extent of harm
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder mitigatingAction(MitigatingAction... mitigatingAction) {
            for (MitigatingAction value : mitigatingAction) {
                this.mitigatingAction.add(value);
            }
            return this;
        }

        /**
         * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param mitigatingAction
         *     Ameliorating actions taken after the adverse event occured in order to reduce the extent of harm
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder mitigatingAction(Collection<MitigatingAction> mitigatingAction) {
            this.mitigatingAction = new ArrayList<>(mitigatingAction);
            return this;
        }

        /**
         * Supporting information relevant to the event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Supporting information relevant to the event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInfo(SupportingInfo... supportingInfo) {
            for (SupportingInfo value : supportingInfo) {
                this.supportingInfo.add(value);
            }
            return this;
        }

        /**
         * Supporting information relevant to the event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Supporting information relevant to the event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInfo(Collection<SupportingInfo> supportingInfo) {
            this.supportingInfo = new ArrayList<>(supportingInfo);
            return this;
        }

        /**
         * Comments made about the adverse event by the performer, subject or other participants.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comment on adverse event
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
         * Comments made about the adverse event by the performer, subject or other participants.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comment on adverse event
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
         * Build the {@link AdverseEvent}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>actuality</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link AdverseEvent}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid AdverseEvent per the base specification
         */
        @Override
        public AdverseEvent build() {
            AdverseEvent adverseEvent = new AdverseEvent(this);
            if (validating) {
                validate(adverseEvent);
            }
            return adverseEvent;
        }

        protected void validate(AdverseEvent adverseEvent) {
            super.validate(adverseEvent);
            ValidationSupport.checkList(adverseEvent.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(adverseEvent.status, "status");
            ValidationSupport.requireNonNull(adverseEvent.actuality, "actuality");
            ValidationSupport.checkList(adverseEvent.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(adverseEvent.subject, "subject");
            ValidationSupport.choiceElement(adverseEvent.occurrence, "occurrence", DateTime.class, Period.class, Timing.class);
            ValidationSupport.checkList(adverseEvent.resultingEffect, "resultingEffect", Reference.class);
            ValidationSupport.checkList(adverseEvent.outcome, "outcome", CodeableConcept.class);
            ValidationSupport.checkList(adverseEvent.participant, "participant", Participant.class);
            ValidationSupport.checkList(adverseEvent.study, "study", Reference.class);
            ValidationSupport.checkList(adverseEvent.suspectEntity, "suspectEntity", SuspectEntity.class);
            ValidationSupport.checkList(adverseEvent.contributingFactor, "contributingFactor", ContributingFactor.class);
            ValidationSupport.checkList(adverseEvent.preventiveAction, "preventiveAction", PreventiveAction.class);
            ValidationSupport.checkList(adverseEvent.mitigatingAction, "mitigatingAction", MitigatingAction.class);
            ValidationSupport.checkList(adverseEvent.supportingInfo, "supportingInfo", SupportingInfo.class);
            ValidationSupport.checkList(adverseEvent.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(adverseEvent.subject, "subject", "Patient", "Group", "Practitioner", "RelatedPerson", "ResearchSubject");
            ValidationSupport.checkReferenceType(adverseEvent.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(adverseEvent.resultingEffect, "resultingEffect", "Condition", "Observation");
            ValidationSupport.checkReferenceType(adverseEvent.location, "location", "Location");
            ValidationSupport.checkReferenceType(adverseEvent.recorder, "recorder", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "ResearchSubject");
            ValidationSupport.checkReferenceType(adverseEvent.study, "study", "ResearchStudy");
        }

        protected Builder from(AdverseEvent adverseEvent) {
            super.from(adverseEvent);
            identifier.addAll(adverseEvent.identifier);
            status = adverseEvent.status;
            actuality = adverseEvent.actuality;
            category.addAll(adverseEvent.category);
            code = adverseEvent.code;
            subject = adverseEvent.subject;
            encounter = adverseEvent.encounter;
            occurrence = adverseEvent.occurrence;
            detected = adverseEvent.detected;
            recordedDate = adverseEvent.recordedDate;
            resultingEffect.addAll(adverseEvent.resultingEffect);
            location = adverseEvent.location;
            seriousness = adverseEvent.seriousness;
            outcome.addAll(adverseEvent.outcome);
            recorder = adverseEvent.recorder;
            participant.addAll(adverseEvent.participant);
            study.addAll(adverseEvent.study);
            expectedInResearchStudy = adverseEvent.expectedInResearchStudy;
            suspectEntity.addAll(adverseEvent.suspectEntity);
            contributingFactor.addAll(adverseEvent.contributingFactor);
            preventiveAction.addAll(adverseEvent.preventiveAction);
            mitigatingAction.addAll(adverseEvent.mitigatingAction);
            supportingInfo.addAll(adverseEvent.supportingInfo);
            note.addAll(adverseEvent.note);
            return this;
        }
    }

    /**
     * Indicates who or what participated in the adverse event and how they were involved.
     */
    public static class Participant extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "AdverseEventParticipantFunction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the type of involvement of the actor in the adverse event.",
            valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-participant-function"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson", "ResearchSubject" })
        @Required
        private final Reference actor;

        private Participant(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Distinguishes the type of involvement of the actor in the adverse event, such as contributor or informant.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Indicates who or what participated in the event.
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
             * Distinguishes the type of involvement of the actor in the adverse event, such as contributor or informant.
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
             * Indicates who or what participated in the event.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link CareTeam}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Device}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link ResearchSubject}</li>
             * </ul>
             * 
             * @param actor
             *     Who was involved in the adverse event or the potential adverse event
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
                ValidationSupport.checkReferenceType(participant.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson", "ResearchSubject");
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
     * Describes the entity that is suspected to have caused the adverse event.
     */
    public static class SuspectEntity extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Immunization", "Procedure", "Substance", "Medication", "MedicationAdministration", "MedicationStatement", "Device", "BiologicallyDerivedProduct", "ResearchStudy" })
        @Choice({ CodeableConcept.class, Reference.class })
        @Required
        private final Element instance;
        @Summary
        private final Causality causality;

        private SuspectEntity(Builder builder) {
            super(builder);
            instance = builder.instance;
            causality = builder.causality;
        }

        /**
         * Identifies the actual instance of what caused the adverse event. May be a substance, medication, medication 
         * administration, medication statement or a device.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} or {@link Reference} that is non-null.
         */
        public Element getInstance() {
            return instance;
        }

        /**
         * Information on the possible cause of the event.
         * 
         * @return
         *     An immutable object of type {@link Causality} that may be null.
         */
        public Causality getCausality() {
            return causality;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (instance != null) || 
                (causality != null);
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
                    accept(instance, "instance", visitor);
                    accept(causality, "causality", visitor);
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
            SuspectEntity other = (SuspectEntity) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(instance, other.instance) && 
                Objects.equals(causality, other.causality);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    instance, 
                    causality);
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
            private Element instance;
            private Causality causality;

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
             * Identifies the actual instance of what caused the adverse event. May be a substance, medication, medication 
             * administration, medication statement or a device.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Reference}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Immunization}</li>
             * <li>{@link Procedure}</li>
             * <li>{@link Substance}</li>
             * <li>{@link Medication}</li>
             * <li>{@link MedicationAdministration}</li>
             * <li>{@link MedicationStatement}</li>
             * <li>{@link Device}</li>
             * <li>{@link BiologicallyDerivedProduct}</li>
             * <li>{@link ResearchStudy}</li>
             * </ul>
             * 
             * @param instance
             *     Refers to the specific entity that caused the adverse event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instance(Element instance) {
                this.instance = instance;
                return this;
            }

            /**
             * Information on the possible cause of the event.
             * 
             * @param causality
             *     Information on the possible cause of the event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder causality(Causality causality) {
                this.causality = causality;
                return this;
            }

            /**
             * Build the {@link SuspectEntity}
             * 
             * <p>Required elements:
             * <ul>
             * <li>instance</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link SuspectEntity}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid SuspectEntity per the base specification
             */
            @Override
            public SuspectEntity build() {
                SuspectEntity suspectEntity = new SuspectEntity(this);
                if (validating) {
                    validate(suspectEntity);
                }
                return suspectEntity;
            }

            protected void validate(SuspectEntity suspectEntity) {
                super.validate(suspectEntity);
                ValidationSupport.requireChoiceElement(suspectEntity.instance, "instance", CodeableConcept.class, Reference.class);
                ValidationSupport.checkReferenceType(suspectEntity.instance, "instance", "Immunization", "Procedure", "Substance", "Medication", "MedicationAdministration", "MedicationStatement", "Device", "BiologicallyDerivedProduct", "ResearchStudy");
                ValidationSupport.requireValueOrChildren(suspectEntity);
            }

            protected Builder from(SuspectEntity suspectEntity) {
                super.from(suspectEntity);
                instance = suspectEntity.instance;
                causality = suspectEntity.causality;
                return this;
            }
        }

        /**
         * Information on the possible cause of the event.
         */
        public static class Causality extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "AdverseEventCausalityMethod",
                strength = BindingStrength.Value.EXAMPLE,
                description = "TODO.",
                valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-causality-method"
            )
            private final CodeableConcept assessmentMethod;
            @Summary
            @Binding(
                bindingName = "AdverseEventCausalityAssessment",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for the assessment of whether the entity caused the event.",
                valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-causality-assess"
            )
            private final CodeableConcept entityRelatedness;
            @Summary
            @ReferenceTarget({ "Practitioner", "PractitionerRole", "Patient", "RelatedPerson", "ResearchSubject" })
            private final Reference author;

            private Causality(Builder builder) {
                super(builder);
                assessmentMethod = builder.assessmentMethod;
                entityRelatedness = builder.entityRelatedness;
                author = builder.author;
            }

            /**
             * The method of evaluating the relatedness of the suspected entity to the event.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getAssessmentMethod() {
                return assessmentMethod;
            }

            /**
             * The result of the assessment regarding the relatedness of the suspected entity to the event.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getEntityRelatedness() {
                return entityRelatedness;
            }

            /**
             * The author of the information on the possible cause of the event.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getAuthor() {
                return author;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (assessmentMethod != null) || 
                    (entityRelatedness != null) || 
                    (author != null);
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
                        accept(assessmentMethod, "assessmentMethod", visitor);
                        accept(entityRelatedness, "entityRelatedness", visitor);
                        accept(author, "author", visitor);
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
                Causality other = (Causality) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(assessmentMethod, other.assessmentMethod) && 
                    Objects.equals(entityRelatedness, other.entityRelatedness) && 
                    Objects.equals(author, other.author);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        assessmentMethod, 
                        entityRelatedness, 
                        author);
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
                private CodeableConcept assessmentMethod;
                private CodeableConcept entityRelatedness;
                private Reference author;

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
                 * The method of evaluating the relatedness of the suspected entity to the event.
                 * 
                 * @param assessmentMethod
                 *     Method of evaluating the relatedness of the suspected entity to the event
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder assessmentMethod(CodeableConcept assessmentMethod) {
                    this.assessmentMethod = assessmentMethod;
                    return this;
                }

                /**
                 * The result of the assessment regarding the relatedness of the suspected entity to the event.
                 * 
                 * @param entityRelatedness
                 *     Result of the assessment regarding the relatedness of the suspected entity to the event
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder entityRelatedness(CodeableConcept entityRelatedness) {
                    this.entityRelatedness = entityRelatedness;
                    return this;
                }

                /**
                 * The author of the information on the possible cause of the event.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link PractitionerRole}</li>
                 * <li>{@link Patient}</li>
                 * <li>{@link RelatedPerson}</li>
                 * <li>{@link ResearchSubject}</li>
                 * </ul>
                 * 
                 * @param author
                 *     Author of the information on the possible cause of the event
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder author(Reference author) {
                    this.author = author;
                    return this;
                }

                /**
                 * Build the {@link Causality}
                 * 
                 * @return
                 *     An immutable object of type {@link Causality}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Causality per the base specification
                 */
                @Override
                public Causality build() {
                    Causality causality = new Causality(this);
                    if (validating) {
                        validate(causality);
                    }
                    return causality;
                }

                protected void validate(Causality causality) {
                    super.validate(causality);
                    ValidationSupport.checkReferenceType(causality.author, "author", "Practitioner", "PractitionerRole", "Patient", "RelatedPerson", "ResearchSubject");
                    ValidationSupport.requireValueOrChildren(causality);
                }

                protected Builder from(Causality causality) {
                    super.from(causality);
                    assessmentMethod = causality.assessmentMethod;
                    entityRelatedness = causality.entityRelatedness;
                    author = causality.author;
                    return this;
                }
            }
        }
    }

    /**
     * The contributing factors suspected to have increased the probability or severity of the adverse event.
     */
    public static class ContributingFactor extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Condition", "Observation", "AllergyIntolerance", "FamilyMemberHistory", "Immunization", "Procedure", "Device", "DeviceUsage", "DocumentReference", "MedicationAdministration", "MedicationStatement" })
        @Choice({ Reference.class, CodeableConcept.class })
        @Binding(
            bindingName = "AdverseEventContributingFactor",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the contributing factors suspected to have increased the probability or severity of the adverse event.",
            valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-contributing-factor"
        )
        @Required
        private final Element item;

        private ContributingFactor(Builder builder) {
            super(builder);
            item = builder.item;
        }

        /**
         * The item that is suspected to have increased the probability or severity of the adverse event.
         * 
         * @return
         *     An immutable object of type {@link Reference} or {@link CodeableConcept} that is non-null.
         */
        public Element getItem() {
            return item;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (item != null);
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
                    accept(item, "item", visitor);
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
            ContributingFactor other = (ContributingFactor) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    item);
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
            private Element item;

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
             * The item that is suspected to have increased the probability or severity of the adverse event.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Reference}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Condition}</li>
             * <li>{@link Observation}</li>
             * <li>{@link AllergyIntolerance}</li>
             * <li>{@link FamilyMemberHistory}</li>
             * <li>{@link Immunization}</li>
             * <li>{@link Procedure}</li>
             * <li>{@link Device}</li>
             * <li>{@link DeviceUsage}</li>
             * <li>{@link DocumentReference}</li>
             * <li>{@link MedicationAdministration}</li>
             * <li>{@link MedicationStatement}</li>
             * </ul>
             * 
             * @param item
             *     Item suspected to have increased the probability or severity of the adverse event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder item(Element item) {
                this.item = item;
                return this;
            }

            /**
             * Build the {@link ContributingFactor}
             * 
             * <p>Required elements:
             * <ul>
             * <li>item</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ContributingFactor}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ContributingFactor per the base specification
             */
            @Override
            public ContributingFactor build() {
                ContributingFactor contributingFactor = new ContributingFactor(this);
                if (validating) {
                    validate(contributingFactor);
                }
                return contributingFactor;
            }

            protected void validate(ContributingFactor contributingFactor) {
                super.validate(contributingFactor);
                ValidationSupport.requireChoiceElement(contributingFactor.item, "item", Reference.class, CodeableConcept.class);
                ValidationSupport.checkReferenceType(contributingFactor.item, "item", "Condition", "Observation", "AllergyIntolerance", "FamilyMemberHistory", "Immunization", "Procedure", "Device", "DeviceUsage", "DocumentReference", "MedicationAdministration", "MedicationStatement");
                ValidationSupport.requireValueOrChildren(contributingFactor);
            }

            protected Builder from(ContributingFactor contributingFactor) {
                super.from(contributingFactor);
                item = contributingFactor.item;
                return this;
            }
        }
    }

    /**
     * Preventive actions that contributed to avoiding the adverse event.
     */
    public static class PreventiveAction extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Immunization", "Procedure", "DocumentReference", "MedicationAdministration", "MedicationRequest" })
        @Choice({ Reference.class, CodeableConcept.class })
        @Binding(
            bindingName = "AdverseEventPreventiveAction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the preventive actions that contributed to avoiding the adverse event.",
            valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-preventive-action"
        )
        @Required
        private final Element item;

        private PreventiveAction(Builder builder) {
            super(builder);
            item = builder.item;
        }

        /**
         * The action that contributed to avoiding the adverse event.
         * 
         * @return
         *     An immutable object of type {@link Reference} or {@link CodeableConcept} that is non-null.
         */
        public Element getItem() {
            return item;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (item != null);
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
                    accept(item, "item", visitor);
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
            PreventiveAction other = (PreventiveAction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    item);
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
            private Element item;

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
             * The action that contributed to avoiding the adverse event.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Reference}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Immunization}</li>
             * <li>{@link Procedure}</li>
             * <li>{@link DocumentReference}</li>
             * <li>{@link MedicationAdministration}</li>
             * <li>{@link MedicationRequest}</li>
             * </ul>
             * 
             * @param item
             *     Action that contributed to avoiding the adverse event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder item(Element item) {
                this.item = item;
                return this;
            }

            /**
             * Build the {@link PreventiveAction}
             * 
             * <p>Required elements:
             * <ul>
             * <li>item</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link PreventiveAction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid PreventiveAction per the base specification
             */
            @Override
            public PreventiveAction build() {
                PreventiveAction preventiveAction = new PreventiveAction(this);
                if (validating) {
                    validate(preventiveAction);
                }
                return preventiveAction;
            }

            protected void validate(PreventiveAction preventiveAction) {
                super.validate(preventiveAction);
                ValidationSupport.requireChoiceElement(preventiveAction.item, "item", Reference.class, CodeableConcept.class);
                ValidationSupport.checkReferenceType(preventiveAction.item, "item", "Immunization", "Procedure", "DocumentReference", "MedicationAdministration", "MedicationRequest");
                ValidationSupport.requireValueOrChildren(preventiveAction);
            }

            protected Builder from(PreventiveAction preventiveAction) {
                super.from(preventiveAction);
                item = preventiveAction.item;
                return this;
            }
        }
    }

    /**
     * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
     */
    public static class MitigatingAction extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Procedure", "DocumentReference", "MedicationAdministration", "MedicationRequest" })
        @Choice({ Reference.class, CodeableConcept.class })
        @Binding(
            bindingName = "AdverseEventMitigatingAction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the ameliorating actions taken after the adverse event occured in order to reduce the extent of harm.",
            valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-mitigating-action"
        )
        @Required
        private final Element item;

        private MitigatingAction(Builder builder) {
            super(builder);
            item = builder.item;
        }

        /**
         * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
         * 
         * @return
         *     An immutable object of type {@link Reference} or {@link CodeableConcept} that is non-null.
         */
        public Element getItem() {
            return item;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (item != null);
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
                    accept(item, "item", visitor);
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
            MitigatingAction other = (MitigatingAction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    item);
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
            private Element item;

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
             * The ameliorating action taken after the adverse event occured in order to reduce the extent of harm.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Reference}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Procedure}</li>
             * <li>{@link DocumentReference}</li>
             * <li>{@link MedicationAdministration}</li>
             * <li>{@link MedicationRequest}</li>
             * </ul>
             * 
             * @param item
             *     Ameliorating action taken after the adverse event occured in order to reduce the extent of harm
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder item(Element item) {
                this.item = item;
                return this;
            }

            /**
             * Build the {@link MitigatingAction}
             * 
             * <p>Required elements:
             * <ul>
             * <li>item</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link MitigatingAction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid MitigatingAction per the base specification
             */
            @Override
            public MitigatingAction build() {
                MitigatingAction mitigatingAction = new MitigatingAction(this);
                if (validating) {
                    validate(mitigatingAction);
                }
                return mitigatingAction;
            }

            protected void validate(MitigatingAction mitigatingAction) {
                super.validate(mitigatingAction);
                ValidationSupport.requireChoiceElement(mitigatingAction.item, "item", Reference.class, CodeableConcept.class);
                ValidationSupport.checkReferenceType(mitigatingAction.item, "item", "Procedure", "DocumentReference", "MedicationAdministration", "MedicationRequest");
                ValidationSupport.requireValueOrChildren(mitigatingAction);
            }

            protected Builder from(MitigatingAction mitigatingAction) {
                super.from(mitigatingAction);
                item = mitigatingAction.item;
                return this;
            }
        }
    }

    /**
     * Supporting information relevant to the event.
     */
    public static class SupportingInfo extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Condition", "Observation", "AllergyIntolerance", "FamilyMemberHistory", "Immunization", "Procedure", "DocumentReference", "MedicationAdministration", "MedicationStatement", "QuestionnaireResponse" })
        @Choice({ Reference.class, CodeableConcept.class })
        @Binding(
            bindingName = "AdverseEventSupportingInfo",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing the supporting information relevant to the event.",
            valueSet = "http://hl7.org/fhir/ValueSet/adverse-event-supporting-info"
        )
        @Required
        private final Element item;

        private SupportingInfo(Builder builder) {
            super(builder);
            item = builder.item;
        }

        /**
         * Relevant past history for the subject. In a clinical care context, an example being a patient had an adverse event 
         * following a pencillin administration and the patient had a previously documented penicillin allergy. In a clinical 
         * trials context, an example is a bunion or rash that was present prior to the study. Additionally, the supporting item 
         * can be a document that is relevant to this instance of the adverse event that is not part of the subject's medical 
         * history. For example, a clinical note, staff list, or material safety data sheet (MSDS). Supporting information is not 
         * a contributing factor, preventive action, or mitigating action.
         * 
         * @return
         *     An immutable object of type {@link Reference} or {@link CodeableConcept} that is non-null.
         */
        public Element getItem() {
            return item;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (item != null);
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
                    accept(item, "item", visitor);
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
            SupportingInfo other = (SupportingInfo) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    item);
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
            private Element item;

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
             * Relevant past history for the subject. In a clinical care context, an example being a patient had an adverse event 
             * following a pencillin administration and the patient had a previously documented penicillin allergy. In a clinical 
             * trials context, an example is a bunion or rash that was present prior to the study. Additionally, the supporting item 
             * can be a document that is relevant to this instance of the adverse event that is not part of the subject's medical 
             * history. For example, a clinical note, staff list, or material safety data sheet (MSDS). Supporting information is not 
             * a contributing factor, preventive action, or mitigating action.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Reference}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Condition}</li>
             * <li>{@link Observation}</li>
             * <li>{@link AllergyIntolerance}</li>
             * <li>{@link FamilyMemberHistory}</li>
             * <li>{@link Immunization}</li>
             * <li>{@link Procedure}</li>
             * <li>{@link DocumentReference}</li>
             * <li>{@link MedicationAdministration}</li>
             * <li>{@link MedicationStatement}</li>
             * <li>{@link QuestionnaireResponse}</li>
             * </ul>
             * 
             * @param item
             *     Subject medical history or document relevant to this adverse event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder item(Element item) {
                this.item = item;
                return this;
            }

            /**
             * Build the {@link SupportingInfo}
             * 
             * <p>Required elements:
             * <ul>
             * <li>item</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link SupportingInfo}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid SupportingInfo per the base specification
             */
            @Override
            public SupportingInfo build() {
                SupportingInfo supportingInfo = new SupportingInfo(this);
                if (validating) {
                    validate(supportingInfo);
                }
                return supportingInfo;
            }

            protected void validate(SupportingInfo supportingInfo) {
                super.validate(supportingInfo);
                ValidationSupport.requireChoiceElement(supportingInfo.item, "item", Reference.class, CodeableConcept.class);
                ValidationSupport.checkReferenceType(supportingInfo.item, "item", "Condition", "Observation", "AllergyIntolerance", "FamilyMemberHistory", "Immunization", "Procedure", "DocumentReference", "MedicationAdministration", "MedicationStatement", "QuestionnaireResponse");
                ValidationSupport.requireValueOrChildren(supportingInfo);
            }

            protected Builder from(SupportingInfo supportingInfo) {
                super.from(supportingInfo);
                item = supportingInfo.item;
                return this;
            }
        }
    }
}
