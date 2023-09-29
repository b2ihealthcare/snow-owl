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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Base64Binary;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Time;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.AuditEventAction;
import org.linuxforhealth.fhir.model.r5.type.code.AuditEventSeverity;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of an event relevant for purposes such as operations, privacy, security, maintenance, and performance 
 * analysis.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "auditEvent-0",
    level = "Warning",
    location = "outcome.code",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/audit-event-outcome",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/audit-event-outcome', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/AuditEvent",
    generated = true
)
@Constraint(
    id = "auditEvent-1",
    level = "Warning",
    location = "agent.type",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/participation-role-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/participation-role-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/AuditEvent",
    generated = true
)
@Constraint(
    id = "auditEvent-2",
    level = "Warning",
    location = "source.type",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/security-source-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/security-source-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/AuditEvent",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AuditEvent extends DomainResource {
    @Summary
    @Binding(
        bindingName = "AuditEventType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Type of event.",
        valueSet = "http://hl7.org/fhir/ValueSet/audit-event-type"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "AuditEventSubType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Specific type of event.",
        valueSet = "http://hl7.org/fhir/ValueSet/audit-event-sub-type"
    )
    @Required
    private final CodeableConcept code;
    @Summary
    @Binding(
        bindingName = "AuditEventAction",
        strength = BindingStrength.Value.REQUIRED,
        description = "DICOM Audit Event Action",
        valueSet = "http://hl7.org/fhir/ValueSet/audit-event-action|5.0.0"
    )
    private final AuditEventAction action;
    @Summary
    @Binding(
        bindingName = "AuditEventSeverity",
        strength = BindingStrength.Value.REQUIRED,
        description = "This is in the SysLog header, PRI. http://tools.ietf.org/html/rfc5424#appendix-A.3",
        valueSet = "http://hl7.org/fhir/ValueSet/audit-event-severity|5.0.0"
    )
    private final AuditEventSeverity severity;
    @Choice({ Period.class, DateTime.class })
    private final Element occurred;
    @Summary
    @Required
    private final Instant recorded;
    @Summary
    private final Outcome outcome;
    @Summary
    @Binding(
        bindingName = "AuditPurposeOfUse",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The authorized purposeOfUse for the activity.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-PurposeOfUse"
    )
    private final List<CodeableConcept> authorization;
    @ReferenceTarget({ "CarePlan", "DeviceRequest", "ImmunizationRecommendation", "MedicationRequest", "NutritionOrder", "ServiceRequest", "Task" })
    private final List<Reference> basedOn;
    @ReferenceTarget({ "Patient" })
    private final Reference patient;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Required
    private final List<Agent> agent;
    @Summary
    @Required
    private final Source source;
    @Summary
    private final List<Entity> entity;

    private AuditEvent(Builder builder) {
        super(builder);
        category = Collections.unmodifiableList(builder.category);
        code = builder.code;
        action = builder.action;
        severity = builder.severity;
        occurred = builder.occurred;
        recorded = builder.recorded;
        outcome = builder.outcome;
        authorization = Collections.unmodifiableList(builder.authorization);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        patient = builder.patient;
        encounter = builder.encounter;
        agent = Collections.unmodifiableList(builder.agent);
        source = builder.source;
        entity = Collections.unmodifiableList(builder.entity);
    }

    /**
     * Classification of the type of event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Describes what happened. The most specific code for the event.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * Indicator for type of action performed during the event that generated the audit.
     * 
     * @return
     *     An immutable object of type {@link AuditEventAction} that may be null.
     */
    public AuditEventAction getAction() {
        return action;
    }

    /**
     * Indicates and enables segmentation of various severity including debugging from critical.
     * 
     * @return
     *     An immutable object of type {@link AuditEventSeverity} that may be null.
     */
    public AuditEventSeverity getSeverity() {
        return severity;
    }

    /**
     * The time or period during which the activity occurred.
     * 
     * @return
     *     An immutable object of type {@link Period} or {@link DateTime} that may be null.
     */
    public Element getOccurred() {
        return occurred;
    }

    /**
     * The time when the event was recorded.
     * 
     * @return
     *     An immutable object of type {@link Instant} that is non-null.
     */
    public Instant getRecorded() {
        return recorded;
    }

    /**
     * Indicates whether the event succeeded or failed. A free text descripiton can be given in outcome.text.
     * 
     * @return
     *     An immutable object of type {@link Outcome} that may be null.
     */
    public Outcome getOutcome() {
        return outcome;
    }

    /**
     * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getAuthorization() {
        return authorization;
    }

    /**
     * Allows tracing of authorizatino for the events and tracking whether proposals/recommendations were acted upon.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The patient element is available to enable deterministic tracking of activities that involve the patient as the 
     * subject of the data used in an activity.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * This will typically be the encounter the event occurred, but some events may be initiated prior to or after the 
     * official completion of an encounter but still be tied to the context of the encounter (e.g. pre-admission lab tests).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * An actor taking an active role in the event or activity that is logged.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Agent} that is non-empty.
     */
    public List<Agent> getAgent() {
        return agent;
    }

    /**
     * The actor that is reporting the event.
     * 
     * @return
     *     An immutable object of type {@link Source} that is non-null.
     */
    public Source getSource() {
        return source;
    }

    /**
     * Specific instances of data or objects that have been accessed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Entity} that may be empty.
     */
    public List<Entity> getEntity() {
        return entity;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !category.isEmpty() || 
            (code != null) || 
            (action != null) || 
            (severity != null) || 
            (occurred != null) || 
            (recorded != null) || 
            (outcome != null) || 
            !authorization.isEmpty() || 
            !basedOn.isEmpty() || 
            (patient != null) || 
            (encounter != null) || 
            !agent.isEmpty() || 
            (source != null) || 
            !entity.isEmpty();
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
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(action, "action", visitor);
                accept(severity, "severity", visitor);
                accept(occurred, "occurred", visitor);
                accept(recorded, "recorded", visitor);
                accept(outcome, "outcome", visitor);
                accept(authorization, "authorization", visitor, CodeableConcept.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(patient, "patient", visitor);
                accept(encounter, "encounter", visitor);
                accept(agent, "agent", visitor, Agent.class);
                accept(source, "source", visitor);
                accept(entity, "entity", visitor, Entity.class);
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
        AuditEvent other = (AuditEvent) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(action, other.action) && 
            Objects.equals(severity, other.severity) && 
            Objects.equals(occurred, other.occurred) && 
            Objects.equals(recorded, other.recorded) && 
            Objects.equals(outcome, other.outcome) && 
            Objects.equals(authorization, other.authorization) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(agent, other.agent) && 
            Objects.equals(source, other.source) && 
            Objects.equals(entity, other.entity);
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
                category, 
                code, 
                action, 
                severity, 
                occurred, 
                recorded, 
                outcome, 
                authorization, 
                basedOn, 
                patient, 
                encounter, 
                agent, 
                source, 
                entity);
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
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept code;
        private AuditEventAction action;
        private AuditEventSeverity severity;
        private Element occurred;
        private Instant recorded;
        private Outcome outcome;
        private List<CodeableConcept> authorization = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private Reference patient;
        private Reference encounter;
        private List<Agent> agent = new ArrayList<>();
        private Source source;
        private List<Entity> entity = new ArrayList<>();

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
         * Classification of the type of event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type/identifier of event
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
         * Classification of the type of event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type/identifier of event
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
         * Describes what happened. The most specific code for the event.
         * 
         * <p>This element is required.
         * 
         * @param code
         *     Specific type of event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * Indicator for type of action performed during the event that generated the audit.
         * 
         * @param action
         *     Type of action performed during the event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder action(AuditEventAction action) {
            this.action = action;
            return this;
        }

        /**
         * Indicates and enables segmentation of various severity including debugging from critical.
         * 
         * @param severity
         *     emergency | alert | critical | error | warning | notice | informational | debug
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder severity(AuditEventSeverity severity) {
            this.severity = severity;
            return this;
        }

        /**
         * The time or period during which the activity occurred.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Period}</li>
         * <li>{@link DateTime}</li>
         * </ul>
         * 
         * @param occurred
         *     When the activity occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurred(Element occurred) {
            this.occurred = occurred;
            return this;
        }

        /**
         * Convenience method for setting {@code recorded}.
         * 
         * <p>This element is required.
         * 
         * @param recorded
         *     Time when the event was recorded
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #recorded(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder recorded(java.time.ZonedDateTime recorded) {
            this.recorded = (recorded == null) ? null : Instant.of(recorded);
            return this;
        }

        /**
         * The time when the event was recorded.
         * 
         * <p>This element is required.
         * 
         * @param recorded
         *     Time when the event was recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorded(Instant recorded) {
            this.recorded = recorded;
            return this;
        }

        /**
         * Indicates whether the event succeeded or failed. A free text descripiton can be given in outcome.text.
         * 
         * @param outcome
         *     Whether the event succeeded or failed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcome(Outcome outcome) {
            this.outcome = outcome;
            return this;
        }

        /**
         * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param authorization
         *     Authorization related to the event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authorization(CodeableConcept... authorization) {
            for (CodeableConcept value : authorization) {
                this.authorization.add(value);
            }
            return this;
        }

        /**
         * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param authorization
         *     Authorization related to the event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder authorization(Collection<CodeableConcept> authorization) {
            this.authorization = new ArrayList<>(authorization);
            return this;
        }

        /**
         * Allows tracing of authorizatino for the events and tracking whether proposals/recommendations were acted upon.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Workflow authorization within which this event occurred
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
         * Allows tracing of authorizatino for the events and tracking whether proposals/recommendations were acted upon.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Workflow authorization within which this event occurred
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
         * The patient element is available to enable deterministic tracking of activities that involve the patient as the 
         * subject of the data used in an activity.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     The patient is the subject of the data used/created/updated/deleted during the activity
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * This will typically be the encounter the event occurred, but some events may be initiated prior to or after the 
         * official completion of an encounter but still be tied to the context of the encounter (e.g. pre-admission lab tests).
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter within which this event occurred or which the event is tightly associated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * An actor taking an active role in the event or activity that is logged.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param agent
         *     Actor involved in the event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder agent(Agent... agent) {
            for (Agent value : agent) {
                this.agent.add(value);
            }
            return this;
        }

        /**
         * An actor taking an active role in the event or activity that is logged.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param agent
         *     Actor involved in the event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder agent(Collection<Agent> agent) {
            this.agent = new ArrayList<>(agent);
            return this;
        }

        /**
         * The actor that is reporting the event.
         * 
         * <p>This element is required.
         * 
         * @param source
         *     Audit Event Reporter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder source(Source source) {
            this.source = source;
            return this;
        }

        /**
         * Specific instances of data or objects that have been accessed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param entity
         *     Data or objects used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder entity(Entity... entity) {
            for (Entity value : entity) {
                this.entity.add(value);
            }
            return this;
        }

        /**
         * Specific instances of data or objects that have been accessed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param entity
         *     Data or objects used
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder entity(Collection<Entity> entity) {
            this.entity = new ArrayList<>(entity);
            return this;
        }

        /**
         * Build the {@link AuditEvent}
         * 
         * <p>Required elements:
         * <ul>
         * <li>code</li>
         * <li>recorded</li>
         * <li>agent</li>
         * <li>source</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link AuditEvent}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid AuditEvent per the base specification
         */
        @Override
        public AuditEvent build() {
            AuditEvent auditEvent = new AuditEvent(this);
            if (validating) {
                validate(auditEvent);
            }
            return auditEvent;
        }

        protected void validate(AuditEvent auditEvent) {
            super.validate(auditEvent);
            ValidationSupport.checkList(auditEvent.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(auditEvent.code, "code");
            ValidationSupport.choiceElement(auditEvent.occurred, "occurred", Period.class, DateTime.class);
            ValidationSupport.requireNonNull(auditEvent.recorded, "recorded");
            ValidationSupport.checkList(auditEvent.authorization, "authorization", CodeableConcept.class);
            ValidationSupport.checkList(auditEvent.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkNonEmptyList(auditEvent.agent, "agent", Agent.class);
            ValidationSupport.requireNonNull(auditEvent.source, "source");
            ValidationSupport.checkList(auditEvent.entity, "entity", Entity.class);
            ValidationSupport.checkReferenceType(auditEvent.basedOn, "basedOn", "CarePlan", "DeviceRequest", "ImmunizationRecommendation", "MedicationRequest", "NutritionOrder", "ServiceRequest", "Task");
            ValidationSupport.checkReferenceType(auditEvent.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(auditEvent.encounter, "encounter", "Encounter");
        }

        protected Builder from(AuditEvent auditEvent) {
            super.from(auditEvent);
            category.addAll(auditEvent.category);
            code = auditEvent.code;
            action = auditEvent.action;
            severity = auditEvent.severity;
            occurred = auditEvent.occurred;
            recorded = auditEvent.recorded;
            outcome = auditEvent.outcome;
            authorization.addAll(auditEvent.authorization);
            basedOn.addAll(auditEvent.basedOn);
            patient = auditEvent.patient;
            encounter = auditEvent.encounter;
            agent.addAll(auditEvent.agent);
            source = auditEvent.source;
            entity.addAll(auditEvent.entity);
            return this;
        }
    }

    /**
     * Indicates whether the event succeeded or failed. A free text descripiton can be given in outcome.text.
     */
    public static class Outcome extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "AuditEventOutcome",
            strength = BindingStrength.Value.PREFERRED,
            description = "DICOM Audit Event Outcome",
            valueSet = "http://hl7.org/fhir/ValueSet/audit-event-outcome"
        )
        @Required
        private final Coding code;
        @Summary
        @Binding(
            bindingName = "AuditEventOutcomeDetail",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A code that provides details as the exact issue.",
            valueSet = "http://hl7.org/fhir/ValueSet/audit-event-outcome-detail"
        )
        private final List<CodeableConcept> detail;

        private Outcome(Builder builder) {
            super(builder);
            code = builder.code;
            detail = Collections.unmodifiableList(builder.detail);
        }

        /**
         * Indicates whether the event succeeded or failed.
         * 
         * @return
         *     An immutable object of type {@link Coding} that is non-null.
         */
        public Coding getCode() {
            return code;
        }

        /**
         * Additional details about the error. This may be a text description of the error or a system code that identifies the 
         * error.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getDetail() {
            return detail;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                !detail.isEmpty();
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
                    accept(detail, "detail", visitor, CodeableConcept.class);
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
            Outcome other = (Outcome) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(detail, other.detail);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    detail);
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
            private Coding code;
            private List<CodeableConcept> detail = new ArrayList<>();

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
             * Indicates whether the event succeeded or failed.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Whether the event succeeded or failed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(Coding code) {
                this.code = code;
                return this;
            }

            /**
             * Additional details about the error. This may be a text description of the error or a system code that identifies the 
             * error.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Additional outcome detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detail(CodeableConcept... detail) {
                for (CodeableConcept value : detail) {
                    this.detail.add(value);
                }
                return this;
            }

            /**
             * Additional details about the error. This may be a text description of the error or a system code that identifies the 
             * error.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Additional outcome detail
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detail(Collection<CodeableConcept> detail) {
                this.detail = new ArrayList<>(detail);
                return this;
            }

            /**
             * Build the {@link Outcome}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Outcome}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Outcome per the base specification
             */
            @Override
            public Outcome build() {
                Outcome outcome = new Outcome(this);
                if (validating) {
                    validate(outcome);
                }
                return outcome;
            }

            protected void validate(Outcome outcome) {
                super.validate(outcome);
                ValidationSupport.requireNonNull(outcome.code, "code");
                ValidationSupport.checkList(outcome.detail, "detail", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(outcome);
            }

            protected Builder from(Outcome outcome) {
                super.from(outcome);
                code = outcome.code;
                detail.addAll(outcome.detail);
                return this;
            }
        }
    }

    /**
     * An actor taking an active role in the event or activity that is logged.
     */
    public static class Agent extends BackboneElement {
        @Binding(
            bindingName = "AuditAgentType",
            strength = BindingStrength.Value.PREFERRED,
            description = "The Participation type of the agent to the event.",
            valueSet = "http://hl7.org/fhir/ValueSet/participation-role-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "AuditAgentRole",
            strength = BindingStrength.Value.EXAMPLE,
            description = "What security role enabled the agent to participate in the event.",
            valueSet = "http://hl7.org/fhir/ValueSet/security-role-type"
        )
        private final List<CodeableConcept> role;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson" })
        @Required
        private final Reference who;
        @Summary
        private final Boolean requestor;
        @ReferenceTarget({ "Location" })
        private final Reference location;
        private final List<Uri> policy;
        @ReferenceTarget({ "Endpoint" })
        @Choice({ Reference.class, Uri.class, String.class })
        private final Element network;
        @Binding(
            bindingName = "AuditPurposeOfUse",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The reason the activity took place.",
            valueSet = "http://terminology.hl7.org/ValueSet/v3-PurposeOfUse"
        )
        private final List<CodeableConcept> authorization;

        private Agent(Builder builder) {
            super(builder);
            type = builder.type;
            role = Collections.unmodifiableList(builder.role);
            who = builder.who;
            requestor = builder.requestor;
            location = builder.location;
            policy = Collections.unmodifiableList(builder.policy);
            network = builder.network;
            authorization = Collections.unmodifiableList(builder.authorization);
        }

        /**
         * The Functional Role of the user when performing the event.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The structural roles of the agent indicating the agent's competency. The security role enabling the agent with respect 
         * to the activity.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getRole() {
            return role;
        }

        /**
         * Reference to who this agent is that was involved in the event.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getWho() {
            return who;
        }

        /**
         * Indicator that the user is or is not the requestor, or initiator, for the event being audited.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getRequestor() {
            return requestor;
        }

        /**
         * Where the agent location is known, the agent location when the event occurred.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getLocation() {
            return location;
        }

        /**
         * Where the policy(ies) are known that authorized the agent participation in the event. Typically, a single activity may 
         * have multiple applicable policies, such as patient consent, guarantor funding, etc. The policy would also indicate the 
         * security token used.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
         */
        public List<Uri> getPolicy() {
            return policy;
        }

        /**
         * When the event utilizes a network there should be an agent describing the local system, and an agent describing remote 
         * system, with the network interface details.
         * 
         * @return
         *     An immutable object of type {@link Reference}, {@link Uri} or {@link String} that may be null.
         */
        public Element getNetwork() {
            return network;
        }

        /**
         * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getAuthorization() {
            return authorization;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !role.isEmpty() || 
                (who != null) || 
                (requestor != null) || 
                (location != null) || 
                !policy.isEmpty() || 
                (network != null) || 
                !authorization.isEmpty();
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
                    accept(role, "role", visitor, CodeableConcept.class);
                    accept(who, "who", visitor);
                    accept(requestor, "requestor", visitor);
                    accept(location, "location", visitor);
                    accept(policy, "policy", visitor, Uri.class);
                    accept(network, "network", visitor);
                    accept(authorization, "authorization", visitor, CodeableConcept.class);
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
            Agent other = (Agent) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(role, other.role) && 
                Objects.equals(who, other.who) && 
                Objects.equals(requestor, other.requestor) && 
                Objects.equals(location, other.location) && 
                Objects.equals(policy, other.policy) && 
                Objects.equals(network, other.network) && 
                Objects.equals(authorization, other.authorization);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    role, 
                    who, 
                    requestor, 
                    location, 
                    policy, 
                    network, 
                    authorization);
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
            private List<CodeableConcept> role = new ArrayList<>();
            private Reference who;
            private Boolean requestor;
            private Reference location;
            private List<Uri> policy = new ArrayList<>();
            private Element network;
            private List<CodeableConcept> authorization = new ArrayList<>();

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
             * The Functional Role of the user when performing the event.
             * 
             * @param type
             *     How agent participated
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The structural roles of the agent indicating the agent's competency. The security role enabling the agent with respect 
             * to the activity.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param role
             *     Agent role in the event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder role(CodeableConcept... role) {
                for (CodeableConcept value : role) {
                    this.role.add(value);
                }
                return this;
            }

            /**
             * The structural roles of the agent indicating the agent's competency. The security role enabling the agent with respect 
             * to the activity.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param role
             *     Agent role in the event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder role(Collection<CodeableConcept> role) {
                this.role = new ArrayList<>(role);
                return this;
            }

            /**
             * Reference to who this agent is that was involved in the event.
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
             * </ul>
             * 
             * @param who
             *     Identifier of who
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder who(Reference who) {
                this.who = who;
                return this;
            }

            /**
             * Convenience method for setting {@code requestor}.
             * 
             * @param requestor
             *     Whether user is initiator
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #requestor(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder requestor(java.lang.Boolean requestor) {
                this.requestor = (requestor == null) ? null : Boolean.of(requestor);
                return this;
            }

            /**
             * Indicator that the user is or is not the requestor, or initiator, for the event being audited.
             * 
             * @param requestor
             *     Whether user is initiator
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder requestor(Boolean requestor) {
                this.requestor = requestor;
                return this;
            }

            /**
             * Where the agent location is known, the agent location when the event occurred.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     The agent location when the event occurred
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * Where the policy(ies) are known that authorized the agent participation in the event. Typically, a single activity may 
             * have multiple applicable policies, such as patient consent, guarantor funding, etc. The policy would also indicate the 
             * security token used.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param policy
             *     Policy that authorized the agent participation in the event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder policy(Uri... policy) {
                for (Uri value : policy) {
                    this.policy.add(value);
                }
                return this;
            }

            /**
             * Where the policy(ies) are known that authorized the agent participation in the event. Typically, a single activity may 
             * have multiple applicable policies, such as patient consent, guarantor funding, etc. The policy would also indicate the 
             * security token used.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param policy
             *     Policy that authorized the agent participation in the event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder policy(Collection<Uri> policy) {
                this.policy = new ArrayList<>(policy);
                return this;
            }

            /**
             * Convenience method for setting {@code network} with choice type String.
             * 
             * @param network
             *     This agent network location for the activity
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #network(Element)
             */
            public Builder network(java.lang.String network) {
                this.network = (network == null) ? null : String.of(network);
                return this;
            }

            /**
             * When the event utilizes a network there should be an agent describing the local system, and an agent describing remote 
             * system, with the network interface details.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Reference}</li>
             * <li>{@link Uri}</li>
             * <li>{@link String}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Endpoint}</li>
             * </ul>
             * 
             * @param network
             *     This agent network location for the activity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder network(Element network) {
                this.network = network;
                return this;
            }

            /**
             * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param authorization
             *     Allowable authorization for this agent
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder authorization(CodeableConcept... authorization) {
                for (CodeableConcept value : authorization) {
                    this.authorization.add(value);
                }
                return this;
            }

            /**
             * The authorization (e.g., PurposeOfUse) that was used during the event being recorded.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param authorization
             *     Allowable authorization for this agent
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder authorization(Collection<CodeableConcept> authorization) {
                this.authorization = new ArrayList<>(authorization);
                return this;
            }

            /**
             * Build the {@link Agent}
             * 
             * <p>Required elements:
             * <ul>
             * <li>who</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Agent}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Agent per the base specification
             */
            @Override
            public Agent build() {
                Agent agent = new Agent(this);
                if (validating) {
                    validate(agent);
                }
                return agent;
            }

            protected void validate(Agent agent) {
                super.validate(agent);
                ValidationSupport.checkList(agent.role, "role", CodeableConcept.class);
                ValidationSupport.requireNonNull(agent.who, "who");
                ValidationSupport.checkList(agent.policy, "policy", Uri.class);
                ValidationSupport.choiceElement(agent.network, "network", Reference.class, Uri.class, String.class);
                ValidationSupport.checkList(agent.authorization, "authorization", CodeableConcept.class);
                ValidationSupport.checkReferenceType(agent.who, "who", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson");
                ValidationSupport.checkReferenceType(agent.location, "location", "Location");
                ValidationSupport.checkReferenceType(agent.network, "network", "Endpoint");
                ValidationSupport.requireValueOrChildren(agent);
            }

            protected Builder from(Agent agent) {
                super.from(agent);
                type = agent.type;
                role.addAll(agent.role);
                who = agent.who;
                requestor = agent.requestor;
                location = agent.location;
                policy.addAll(agent.policy);
                network = agent.network;
                authorization.addAll(agent.authorization);
                return this;
            }
        }
    }

    /**
     * The actor that is reporting the event.
     */
    public static class Source extends BackboneElement {
        @ReferenceTarget({ "Location" })
        private final Reference site;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson" })
        @Required
        private final Reference observer;
        @Binding(
            bindingName = "AuditEventSourceType",
            strength = BindingStrength.Value.PREFERRED,
            description = "Code specifying the type of system that detected and recorded the event. Use of these codes is not required but is encouraged to maintain translation with DICOM AuditMessage schema.",
            valueSet = "http://hl7.org/fhir/ValueSet/security-source-type"
        )
        private final List<CodeableConcept> type;

        private Source(Builder builder) {
            super(builder);
            site = builder.site;
            observer = builder.observer;
            type = Collections.unmodifiableList(builder.type);
        }

        /**
         * Logical source location within the healthcare enterprise network. For example, a hospital or other provider location 
         * within a multi-entity provider group.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSite() {
            return site;
        }

        /**
         * Identifier of the source where the event was detected.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getObserver() {
            return observer;
        }

        /**
         * Code specifying the type of source where event originated.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (site != null) || 
                (observer != null) || 
                !type.isEmpty();
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
                    accept(site, "site", visitor);
                    accept(observer, "observer", visitor);
                    accept(type, "type", visitor, CodeableConcept.class);
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
            Source other = (Source) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(site, other.site) && 
                Objects.equals(observer, other.observer) && 
                Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    site, 
                    observer, 
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
            private Reference site;
            private Reference observer;
            private List<CodeableConcept> type = new ArrayList<>();

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
             * Logical source location within the healthcare enterprise network. For example, a hospital or other provider location 
             * within a multi-entity provider group.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param site
             *     Logical source location within the enterprise
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder site(Reference site) {
                this.site = site;
                return this;
            }

            /**
             * Identifier of the source where the event was detected.
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
             * </ul>
             * 
             * @param observer
             *     The identity of source detecting the event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder observer(Reference observer) {
                this.observer = observer;
                return this;
            }

            /**
             * Code specifying the type of source where event originated.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     The type of source where event originated
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
             * Code specifying the type of source where event originated.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     The type of source where event originated
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
             * Build the {@link Source}
             * 
             * <p>Required elements:
             * <ul>
             * <li>observer</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Source}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Source per the base specification
             */
            @Override
            public Source build() {
                Source source = new Source(this);
                if (validating) {
                    validate(source);
                }
                return source;
            }

            protected void validate(Source source) {
                super.validate(source);
                ValidationSupport.requireNonNull(source.observer, "observer");
                ValidationSupport.checkList(source.type, "type", CodeableConcept.class);
                ValidationSupport.checkReferenceType(source.site, "site", "Location");
                ValidationSupport.checkReferenceType(source.observer, "observer", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(source);
            }

            protected Builder from(Source source) {
                super.from(source);
                site = source.site;
                observer = source.observer;
                type.addAll(source.type);
                return this;
            }
        }
    }

    /**
     * Specific instances of data or objects that have been accessed.
     */
    public static class Entity extends BackboneElement {
        @Summary
        private final Reference what;
        @Binding(
            bindingName = "AuditEventEntityRole",
            strength = BindingStrength.Value.EXAMPLE,
            description = "DICOM Audit Event Entity Role",
            valueSet = "http://hl7.org/fhir/ValueSet/object-role"
        )
        private final CodeableConcept role;
        @Binding(
            bindingName = "SecurityLabels",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Example Security Labels from the Healthcare Privacy and Security Classification System.",
            valueSet = "http://hl7.org/fhir/ValueSet/security-label-examples"
        )
        private final List<CodeableConcept> securityLabel;
        @Summary
        private final Base64Binary query;
        private final List<Detail> detail;
        private final List<AuditEvent.Agent> agent;

        private Entity(Builder builder) {
            super(builder);
            what = builder.what;
            role = builder.role;
            securityLabel = Collections.unmodifiableList(builder.securityLabel);
            query = builder.query;
            detail = Collections.unmodifiableList(builder.detail);
            agent = Collections.unmodifiableList(builder.agent);
        }

        /**
         * Identifies a specific instance of the entity. The reference should be version specific. This is allowed to be a 
         * Parameters resource.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getWhat() {
            return what;
        }

        /**
         * Code representing the role the entity played in the event being audited.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRole() {
            return role;
        }

        /**
         * Security labels for the identified entity.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getSecurityLabel() {
            return securityLabel;
        }

        /**
         * The query parameters for a query-type entities.
         * 
         * @return
         *     An immutable object of type {@link Base64Binary} that may be null.
         */
        public Base64Binary getQuery() {
            return query;
        }

        /**
         * Tagged value pairs for conveying additional information about the entity.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Detail} that may be empty.
         */
        public List<Detail> getDetail() {
            return detail;
        }

        /**
         * The entity is attributed to an agent to express the agent's responsibility for that entity in the activity. This is 
         * most used to indicate when persistence media (the entity) are used by an agent. For example when importing data from a 
         * device, the device would be described in an entity, and the user importing data from that media would be indicated as 
         * the entity.agent.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Agent} that may be empty.
         */
        public List<AuditEvent.Agent> getAgent() {
            return agent;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (what != null) || 
                (role != null) || 
                !securityLabel.isEmpty() || 
                (query != null) || 
                !detail.isEmpty() || 
                !agent.isEmpty();
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
                    accept(what, "what", visitor);
                    accept(role, "role", visitor);
                    accept(securityLabel, "securityLabel", visitor, CodeableConcept.class);
                    accept(query, "query", visitor);
                    accept(detail, "detail", visitor, Detail.class);
                    accept(agent, "agent", visitor, AuditEvent.Agent.class);
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
            Entity other = (Entity) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(what, other.what) && 
                Objects.equals(role, other.role) && 
                Objects.equals(securityLabel, other.securityLabel) && 
                Objects.equals(query, other.query) && 
                Objects.equals(detail, other.detail) && 
                Objects.equals(agent, other.agent);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    what, 
                    role, 
                    securityLabel, 
                    query, 
                    detail, 
                    agent);
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
            private Reference what;
            private CodeableConcept role;
            private List<CodeableConcept> securityLabel = new ArrayList<>();
            private Base64Binary query;
            private List<Detail> detail = new ArrayList<>();
            private List<AuditEvent.Agent> agent = new ArrayList<>();

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
             * Identifies a specific instance of the entity. The reference should be version specific. This is allowed to be a 
             * Parameters resource.
             * 
             * @param what
             *     Specific instance of resource
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder what(Reference what) {
                this.what = what;
                return this;
            }

            /**
             * Code representing the role the entity played in the event being audited.
             * 
             * @param role
             *     What role the entity played
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder role(CodeableConcept role) {
                this.role = role;
                return this;
            }

            /**
             * Security labels for the identified entity.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param securityLabel
             *     Security labels on the entity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder securityLabel(CodeableConcept... securityLabel) {
                for (CodeableConcept value : securityLabel) {
                    this.securityLabel.add(value);
                }
                return this;
            }

            /**
             * Security labels for the identified entity.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param securityLabel
             *     Security labels on the entity
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder securityLabel(Collection<CodeableConcept> securityLabel) {
                this.securityLabel = new ArrayList<>(securityLabel);
                return this;
            }

            /**
             * The query parameters for a query-type entities.
             * 
             * @param query
             *     Query parameters
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder query(Base64Binary query) {
                this.query = query;
                return this;
            }

            /**
             * Tagged value pairs for conveying additional information about the entity.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Additional Information about the entity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detail(Detail... detail) {
                for (Detail value : detail) {
                    this.detail.add(value);
                }
                return this;
            }

            /**
             * Tagged value pairs for conveying additional information about the entity.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Additional Information about the entity
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detail(Collection<Detail> detail) {
                this.detail = new ArrayList<>(detail);
                return this;
            }

            /**
             * The entity is attributed to an agent to express the agent's responsibility for that entity in the activity. This is 
             * most used to indicate when persistence media (the entity) are used by an agent. For example when importing data from a 
             * device, the device would be described in an entity, and the user importing data from that media would be indicated as 
             * the entity.agent.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param agent
             *     Entity is attributed to this agent
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder agent(AuditEvent.Agent... agent) {
                for (AuditEvent.Agent value : agent) {
                    this.agent.add(value);
                }
                return this;
            }

            /**
             * The entity is attributed to an agent to express the agent's responsibility for that entity in the activity. This is 
             * most used to indicate when persistence media (the entity) are used by an agent. For example when importing data from a 
             * device, the device would be described in an entity, and the user importing data from that media would be indicated as 
             * the entity.agent.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param agent
             *     Entity is attributed to this agent
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder agent(Collection<AuditEvent.Agent> agent) {
                this.agent = new ArrayList<>(agent);
                return this;
            }

            /**
             * Build the {@link Entity}
             * 
             * @return
             *     An immutable object of type {@link Entity}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Entity per the base specification
             */
            @Override
            public Entity build() {
                Entity entity = new Entity(this);
                if (validating) {
                    validate(entity);
                }
                return entity;
            }

            protected void validate(Entity entity) {
                super.validate(entity);
                ValidationSupport.checkList(entity.securityLabel, "securityLabel", CodeableConcept.class);
                ValidationSupport.checkList(entity.detail, "detail", Detail.class);
                ValidationSupport.checkList(entity.agent, "agent", AuditEvent.Agent.class);
                ValidationSupport.requireValueOrChildren(entity);
            }

            protected Builder from(Entity entity) {
                super.from(entity);
                what = entity.what;
                role = entity.role;
                securityLabel.addAll(entity.securityLabel);
                query = entity.query;
                detail.addAll(entity.detail);
                agent.addAll(entity.agent);
                return this;
            }
        }

        /**
         * Tagged value pairs for conveying additional information about the entity.
         */
        public static class Detail extends BackboneElement {
            @Binding(
                bindingName = "AuditEventDetailType",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Additional detail about an entity used in an event.",
                valueSet = "http://hl7.org/fhir/ValueSet/audit-event-type"
            )
            @Required
            private final CodeableConcept type;
            @Choice({ Quantity.class, CodeableConcept.class, String.class, Boolean.class, Integer.class, Range.class, Ratio.class, Time.class, DateTime.class, Period.class, Base64Binary.class })
            @Required
            private final Element value;

            private Detail(Builder builder) {
                super(builder);
                type = builder.type;
                value = builder.value;
            }

            /**
             * The type of extra detail provided in the value.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * The value of the extra detail.
             * 
             * @return
             *     An immutable object of type {@link Quantity}, {@link CodeableConcept}, {@link String}, {@link Boolean}, {@link 
             *     Integer}, {@link Range}, {@link Ratio}, {@link Time}, {@link DateTime}, {@link Period} or {@link Base64Binary} that is 
             *     non-null.
             */
            public Element getValue() {
                return value;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (value != null);
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
                        accept(value, "value", visitor);
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
                Detail other = (Detail) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(value, other.value);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        value);
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
                private Element value;

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
                 * The type of extra detail provided in the value.
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     Name of the property
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type String.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     Property value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #value(Element)
                 */
                public Builder value(java.lang.String value) {
                    this.value = (value == null) ? null : String.of(value);
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type Boolean.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     Property value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #value(Element)
                 */
                public Builder value(java.lang.Boolean value) {
                    this.value = (value == null) ? null : Boolean.of(value);
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type Integer.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     Property value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #value(Element)
                 */
                public Builder value(java.lang.Integer value) {
                    this.value = (value == null) ? null : Integer.of(value);
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type Time.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     Property value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #value(Element)
                 */
                public Builder value(java.time.LocalTime value) {
                    this.value = (value == null) ? null : Time.of(value);
                    return this;
                }

                /**
                 * The value of the extra detail.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Quantity}</li>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link String}</li>
                 * <li>{@link Boolean}</li>
                 * <li>{@link Integer}</li>
                 * <li>{@link Range}</li>
                 * <li>{@link Ratio}</li>
                 * <li>{@link Time}</li>
                 * <li>{@link DateTime}</li>
                 * <li>{@link Period}</li>
                 * <li>{@link Base64Binary}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Property value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link Detail}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Detail}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Detail per the base specification
                 */
                @Override
                public Detail build() {
                    Detail detail = new Detail(this);
                    if (validating) {
                        validate(detail);
                    }
                    return detail;
                }

                protected void validate(Detail detail) {
                    super.validate(detail);
                    ValidationSupport.requireNonNull(detail.type, "type");
                    ValidationSupport.requireChoiceElement(detail.value, "value", Quantity.class, CodeableConcept.class, String.class, Boolean.class, Integer.class, Range.class, Ratio.class, Time.class, DateTime.class, Period.class, Base64Binary.class);
                    ValidationSupport.requireValueOrChildren(detail);
                }

                protected Builder from(Detail detail) {
                    super.from(detail);
                    type = detail.type;
                    value = detail.value;
                    return this;
                }
            }
        }
    }
}
