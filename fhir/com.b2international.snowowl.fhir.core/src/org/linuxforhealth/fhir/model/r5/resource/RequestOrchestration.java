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
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DataRequirement;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.ActionCardinalityBehavior;
import org.linuxforhealth.fhir.model.r5.type.code.ActionConditionKind;
import org.linuxforhealth.fhir.model.r5.type.code.ActionGroupingBehavior;
import org.linuxforhealth.fhir.model.r5.type.code.ActionPrecheckBehavior;
import org.linuxforhealth.fhir.model.r5.type.code.ActionRelationshipType;
import org.linuxforhealth.fhir.model.r5.type.code.ActionRequiredBehavior;
import org.linuxforhealth.fhir.model.r5.type.code.ActionSelectionBehavior;
import org.linuxforhealth.fhir.model.r5.type.code.ActivityParticipantType;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.RequestIntent;
import org.linuxforhealth.fhir.model.r5.type.code.RequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.RequestStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A set of related requests that can be used to capture intended activities that have inter-dependencies such as "give 
 * this medication after that one".
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "pld-0",
    level = "Rule",
    location = "RequestOrchestration.action.input",
    description = "Input data elements must have a requirement or a relatedData, but not both",
    expression = "requirement.exists() xor relatedData.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/RequestOrchestration"
)
@Constraint(
    id = "rqg-1",
    level = "Rule",
    location = "RequestOrchestration.action",
    description = "Must have resource or action but not both",
    expression = "resource.exists() != action.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/RequestOrchestration"
)
@Constraint(
    id = "pld-1",
    level = "Rule",
    location = "RequestOrchestration.action.output",
    description = "Output data element must have a requirement or a relatedData, but not both",
    expression = "requirement.exists() xor relatedData.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/RequestOrchestration"
)
@Constraint(
    id = "requestOrchestration-2",
    level = "Warning",
    location = "action.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/action-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/action-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/RequestOrchestration",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class RequestOrchestration extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    private final List<Reference> basedOn;
    private final List<Reference> replaces;
    @Summary
    private final Identifier groupIdentifier;
    @Summary
    @Binding(
        bindingName = "RequestStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes identifying the lifecycle stage of a request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-status|5.0.0"
    )
    @Required
    private final RequestStatus status;
    @Summary
    @Binding(
        bindingName = "RequestIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes indicating the degree of authority/intentionality associated with a request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-intent|5.0.0"
    )
    @Required
    private final RequestIntent intent;
    @Summary
    @Binding(
        bindingName = "RequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final RequestPriority priority;
    @Summary
    @Binding(
        bindingName = "RequestCode",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/action-code"
    )
    private final CodeableConcept code;
    @ReferenceTarget({ "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
    private final Reference subject;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final DateTime authoredOn;
    @ReferenceTarget({ "Device", "Practitioner", "PractitionerRole" })
    private final Reference author;
    @Binding(
        bindingName = "ActionReasonCode",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/action-reason-code"
    )
    private final List<CodeableReference> reason;
    @ReferenceTarget({ "Goal" })
    private final List<Reference> goal;
    private final List<Annotation> note;
    private final List<Action> action;

    private RequestOrchestration(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        replaces = Collections.unmodifiableList(builder.replaces);
        groupIdentifier = builder.groupIdentifier;
        status = builder.status;
        intent = builder.intent;
        priority = builder.priority;
        code = builder.code;
        subject = builder.subject;
        encounter = builder.encounter;
        authoredOn = builder.authoredOn;
        author = builder.author;
        reason = Collections.unmodifiableList(builder.reason);
        goal = Collections.unmodifiableList(builder.goal);
        note = Collections.unmodifiableList(builder.note);
        action = Collections.unmodifiableList(builder.action);
    }

    /**
     * Allows a service to provide a unique, business identifier for the request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A canonical URL referencing a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in 
     * whole or in part by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * A URL referencing an externally defined protocol, guideline, orderset or other definition that is adhered to in whole 
     * or in part by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * A plan, proposal or order that is fulfilled in whole or in part by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Completed or terminated request(s) whose function is taken by this new request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReplaces() {
        return replaces;
    }

    /**
     * A shared identifier common to multiple independent Request instances that were activated/authorized more or less 
     * simultaneously by a single author. The presence of the same identifier on each request ties those requests together 
     * and may have business ramifications in terms of reporting of results, billing, etc. E.g. a requisition number shared 
     * by a set of lab tests ordered together, or a prescription number shared by all meds ordered at one time.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
     * The current state of the request. For request orchestrations, the status reflects the status of all the requests in 
     * the orchestration.
     * 
     * @return
     *     An immutable object of type {@link RequestStatus} that is non-null.
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Indicates the level of authority/intentionality associated with the request and where the request fits into the 
     * workflow chain.
     * 
     * @return
     *     An immutable object of type {@link RequestIntent} that is non-null.
     */
    public RequestIntent getIntent() {
        return intent;
    }

    /**
     * Indicates how quickly the request should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link RequestPriority} that may be null.
     */
    public RequestPriority getPriority() {
        return priority;
    }

    /**
     * A code that identifies what the overall request orchestration is.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * The subject for which the request orchestration was created.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Describes the context of the request orchestration, if any.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Indicates when the request orchestration was created.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * Provides a reference to the author of the request orchestration.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getAuthor() {
        return author;
    }

    /**
     * Describes the reason for the request orchestration in coded or textual form.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Goals that are intended to be achieved by following the requests in this RequestOrchestration.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getGoal() {
        return goal;
    }

    /**
     * Provides a mechanism to communicate additional information about the response.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * The actions, if any, produced by the evaluation of the artifact.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Action} that may be empty.
     */
    public List<Action> getAction() {
        return action;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !instantiatesCanonical.isEmpty() || 
            !instantiatesUri.isEmpty() || 
            !basedOn.isEmpty() || 
            !replaces.isEmpty() || 
            (groupIdentifier != null) || 
            (status != null) || 
            (intent != null) || 
            (priority != null) || 
            (code != null) || 
            (subject != null) || 
            (encounter != null) || 
            (authoredOn != null) || 
            (author != null) || 
            !reason.isEmpty() || 
            !goal.isEmpty() || 
            !note.isEmpty() || 
            !action.isEmpty();
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
                accept(instantiatesCanonical, "instantiatesCanonical", visitor, Canonical.class);
                accept(instantiatesUri, "instantiatesUri", visitor, Uri.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(replaces, "replaces", visitor, Reference.class);
                accept(groupIdentifier, "groupIdentifier", visitor);
                accept(status, "status", visitor);
                accept(intent, "intent", visitor);
                accept(priority, "priority", visitor);
                accept(code, "code", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(authoredOn, "authoredOn", visitor);
                accept(author, "author", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(goal, "goal", visitor, Reference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(action, "action", visitor, Action.class);
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
        RequestOrchestration other = (RequestOrchestration) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(instantiatesCanonical, other.instantiatesCanonical) && 
            Objects.equals(instantiatesUri, other.instantiatesUri) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(replaces, other.replaces) && 
            Objects.equals(groupIdentifier, other.groupIdentifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(code, other.code) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(author, other.author) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(goal, other.goal) && 
            Objects.equals(note, other.note) && 
            Objects.equals(action, other.action);
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
                instantiatesCanonical, 
                instantiatesUri, 
                basedOn, 
                replaces, 
                groupIdentifier, 
                status, 
                intent, 
                priority, 
                code, 
                subject, 
                encounter, 
                authoredOn, 
                author, 
                reason, 
                goal, 
                note, 
                action);
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
        private List<Canonical> instantiatesCanonical = new ArrayList<>();
        private List<Uri> instantiatesUri = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private List<Reference> replaces = new ArrayList<>();
        private Identifier groupIdentifier;
        private RequestStatus status;
        private RequestIntent intent;
        private RequestPriority priority;
        private CodeableConcept code;
        private Reference subject;
        private Reference encounter;
        private DateTime authoredOn;
        private Reference author;
        private List<CodeableReference> reason = new ArrayList<>();
        private List<Reference> goal = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<Action> action = new ArrayList<>();

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
         * Allows a service to provide a unique, business identifier for the request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier
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
         * Allows a service to provide a unique, business identifier for the request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier
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
         * A canonical URL referencing a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesCanonical(Canonical... instantiatesCanonical) {
            for (Canonical value : instantiatesCanonical) {
                this.instantiatesCanonical.add(value);
            }
            return this;
        }

        /**
         * A canonical URL referencing a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesCanonical(Collection<Canonical> instantiatesCanonical) {
            this.instantiatesCanonical = new ArrayList<>(instantiatesCanonical);
            return this;
        }

        /**
         * A URL referencing an externally defined protocol, guideline, orderset or other definition that is adhered to in whole 
         * or in part by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesUri(Uri... instantiatesUri) {
            for (Uri value : instantiatesUri) {
                this.instantiatesUri.add(value);
            }
            return this;
        }

        /**
         * A URL referencing an externally defined protocol, guideline, orderset or other definition that is adhered to in whole 
         * or in part by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesUri(Collection<Uri> instantiatesUri) {
            this.instantiatesUri = new ArrayList<>(instantiatesUri);
            return this;
        }

        /**
         * A plan, proposal or order that is fulfilled in whole or in part by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     Fulfills plan, proposal, or order
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
         * A plan, proposal or order that is fulfilled in whole or in part by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     Fulfills plan, proposal, or order
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
         * Completed or terminated request(s) whose function is taken by this new request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param replaces
         *     Request(s) replaced by this request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder replaces(Reference... replaces) {
            for (Reference value : replaces) {
                this.replaces.add(value);
            }
            return this;
        }

        /**
         * Completed or terminated request(s) whose function is taken by this new request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param replaces
         *     Request(s) replaced by this request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder replaces(Collection<Reference> replaces) {
            this.replaces = new ArrayList<>(replaces);
            return this;
        }

        /**
         * A shared identifier common to multiple independent Request instances that were activated/authorized more or less 
         * simultaneously by a single author. The presence of the same identifier on each request ties those requests together 
         * and may have business ramifications in terms of reporting of results, billing, etc. E.g. a requisition number shared 
         * by a set of lab tests ordered together, or a prescription number shared by all meds ordered at one time.
         * 
         * @param groupIdentifier
         *     Composite request this is part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder groupIdentifier(Identifier groupIdentifier) {
            this.groupIdentifier = groupIdentifier;
            return this;
        }

        /**
         * The current state of the request. For request orchestrations, the status reflects the status of all the requests in 
         * the orchestration.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | on-hold | revoked | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(RequestStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the level of authority/intentionality associated with the request and where the request fits into the 
         * workflow chain.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | directive | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(RequestIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Indicates how quickly the request should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(RequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * A code that identifies what the overall request orchestration is.
         * 
         * @param code
         *     What's being requested/ordered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * The subject for which the request orchestration was created.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link Device}</li>
         * <li>{@link Group}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Location}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param subject
         *     Who the request orchestration is about
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Describes the context of the request orchestration, if any.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Created as part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Indicates when the request orchestration was created.
         * 
         * @param authoredOn
         *     When the request orchestration was authored
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * Provides a reference to the author of the request orchestration.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param author
         *     Device or practitioner that authored the request orchestration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder author(Reference author) {
            this.author = author;
            return this;
        }

        /**
         * Describes the reason for the request orchestration in coded or textual form.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the request orchestration is needed
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
         * Describes the reason for the request orchestration in coded or textual form.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the request orchestration is needed
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
         * Goals that are intended to be achieved by following the requests in this RequestOrchestration.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Goal}</li>
         * </ul>
         * 
         * @param goal
         *     What goals
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder goal(Reference... goal) {
            for (Reference value : goal) {
                this.goal.add(value);
            }
            return this;
        }

        /**
         * Goals that are intended to be achieved by following the requests in this RequestOrchestration.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Goal}</li>
         * </ul>
         * 
         * @param goal
         *     What goals
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder goal(Collection<Reference> goal) {
            this.goal = new ArrayList<>(goal);
            return this;
        }

        /**
         * Provides a mechanism to communicate additional information about the response.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional notes about the response
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
         * Provides a mechanism to communicate additional information about the response.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional notes about the response
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
         * The actions, if any, produced by the evaluation of the artifact.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param action
         *     Proposed actions, if any
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder action(Action... action) {
            for (Action value : action) {
                this.action.add(value);
            }
            return this;
        }

        /**
         * The actions, if any, produced by the evaluation of the artifact.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param action
         *     Proposed actions, if any
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder action(Collection<Action> action) {
            this.action = new ArrayList<>(action);
            return this;
        }

        /**
         * Build the {@link RequestOrchestration}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>intent</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link RequestOrchestration}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid RequestOrchestration per the base specification
         */
        @Override
        public RequestOrchestration build() {
            RequestOrchestration requestOrchestration = new RequestOrchestration(this);
            if (validating) {
                validate(requestOrchestration);
            }
            return requestOrchestration;
        }

        protected void validate(RequestOrchestration requestOrchestration) {
            super.validate(requestOrchestration);
            ValidationSupport.checkList(requestOrchestration.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(requestOrchestration.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(requestOrchestration.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(requestOrchestration.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(requestOrchestration.replaces, "replaces", Reference.class);
            ValidationSupport.requireNonNull(requestOrchestration.status, "status");
            ValidationSupport.requireNonNull(requestOrchestration.intent, "intent");
            ValidationSupport.checkList(requestOrchestration.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(requestOrchestration.goal, "goal", Reference.class);
            ValidationSupport.checkList(requestOrchestration.note, "note", Annotation.class);
            ValidationSupport.checkList(requestOrchestration.action, "action", Action.class);
            ValidationSupport.checkReferenceType(requestOrchestration.subject, "subject", "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
            ValidationSupport.checkReferenceType(requestOrchestration.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(requestOrchestration.author, "author", "Device", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(requestOrchestration.goal, "goal", "Goal");
        }

        protected Builder from(RequestOrchestration requestOrchestration) {
            super.from(requestOrchestration);
            identifier.addAll(requestOrchestration.identifier);
            instantiatesCanonical.addAll(requestOrchestration.instantiatesCanonical);
            instantiatesUri.addAll(requestOrchestration.instantiatesUri);
            basedOn.addAll(requestOrchestration.basedOn);
            replaces.addAll(requestOrchestration.replaces);
            groupIdentifier = requestOrchestration.groupIdentifier;
            status = requestOrchestration.status;
            intent = requestOrchestration.intent;
            priority = requestOrchestration.priority;
            code = requestOrchestration.code;
            subject = requestOrchestration.subject;
            encounter = requestOrchestration.encounter;
            authoredOn = requestOrchestration.authoredOn;
            author = requestOrchestration.author;
            reason.addAll(requestOrchestration.reason);
            goal.addAll(requestOrchestration.goal);
            note.addAll(requestOrchestration.note);
            action.addAll(requestOrchestration.action);
            return this;
        }
    }

    /**
     * The actions, if any, produced by the evaluation of the artifact.
     */
    public static class Action extends BackboneElement {
        private final String linkId;
        private final String prefix;
        private final String title;
        @Summary
        private final Markdown description;
        @Summary
        private final Markdown textEquivalent;
        @Binding(
            bindingName = "RequestPriority",
            strength = BindingStrength.Value.REQUIRED,
            description = "Identifies the level of importance to be assigned to actioning the request.",
            valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
        )
        private final RequestPriority priority;
        @Binding(
            bindingName = "ActionCode",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/action-code"
        )
        private final List<CodeableConcept> code;
        private final List<RelatedArtifact> documentation;
        @ReferenceTarget({ "Goal" })
        private final List<Reference> goal;
        private final List<Condition> condition;
        private final List<Input> input;
        private final List<Output> output;
        private final List<RelatedAction> relatedAction;
        @Choice({ DateTime.class, Age.class, Period.class, Duration.class, Range.class, Timing.class })
        private final Element timing;
        private final CodeableReference location;
        private final List<Participant> participant;
        @Binding(
            bindingName = "ActionType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The type of action to be performed.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "ActionGroupingBehavior",
            strength = BindingStrength.Value.REQUIRED,
            description = "Defines organization behavior of a group.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-grouping-behavior|5.0.0"
        )
        private final ActionGroupingBehavior groupingBehavior;
        @Binding(
            bindingName = "ActionSelectionBehavior",
            strength = BindingStrength.Value.REQUIRED,
            description = "Defines selection behavior of a group.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-selection-behavior|5.0.0"
        )
        private final ActionSelectionBehavior selectionBehavior;
        @Binding(
            bindingName = "ActionRequiredBehavior",
            strength = BindingStrength.Value.REQUIRED,
            description = "Defines expectations around whether an action or action group is required.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-required-behavior|5.0.0"
        )
        private final ActionRequiredBehavior requiredBehavior;
        @Binding(
            bindingName = "ActionPrecheckBehavior",
            strength = BindingStrength.Value.REQUIRED,
            description = "Defines selection frequency behavior for an action or group.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-precheck-behavior|5.0.0"
        )
        private final ActionPrecheckBehavior precheckBehavior;
        @Binding(
            bindingName = "ActionCardinalityBehavior",
            strength = BindingStrength.Value.REQUIRED,
            description = "Defines behavior for an action or a group for how many times that item may be repeated.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-cardinality-behavior|5.0.0"
        )
        private final ActionCardinalityBehavior cardinalityBehavior;
        private final Reference resource;
        @Choice({ Canonical.class, Uri.class })
        private final Element definition;
        private final Canonical transform;
        private final List<DynamicValue> dynamicValue;
        private final List<RequestOrchestration.Action> action;

        private Action(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            prefix = builder.prefix;
            title = builder.title;
            description = builder.description;
            textEquivalent = builder.textEquivalent;
            priority = builder.priority;
            code = Collections.unmodifiableList(builder.code);
            documentation = Collections.unmodifiableList(builder.documentation);
            goal = Collections.unmodifiableList(builder.goal);
            condition = Collections.unmodifiableList(builder.condition);
            input = Collections.unmodifiableList(builder.input);
            output = Collections.unmodifiableList(builder.output);
            relatedAction = Collections.unmodifiableList(builder.relatedAction);
            timing = builder.timing;
            location = builder.location;
            participant = Collections.unmodifiableList(builder.participant);
            type = builder.type;
            groupingBehavior = builder.groupingBehavior;
            selectionBehavior = builder.selectionBehavior;
            requiredBehavior = builder.requiredBehavior;
            precheckBehavior = builder.precheckBehavior;
            cardinalityBehavior = builder.cardinalityBehavior;
            resource = builder.resource;
            definition = builder.definition;
            transform = builder.transform;
            dynamicValue = Collections.unmodifiableList(builder.dynamicValue);
            action = Collections.unmodifiableList(builder.action);
        }

        /**
         * The linkId of the action from the PlanDefinition that corresponds to this action in the RequestOrchestration resource.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLinkId() {
            return linkId;
        }

        /**
         * A user-visible prefix for the action. For example a section or item numbering such as 1. or A.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * The title of the action displayed to a user.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * A short description of the action used to provide a summary to display to the user.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * A text equivalent of the action to be performed. This provides a human-interpretable description of the action when 
         * the definition is consumed by a system that might not be capable of interpreting it dynamically.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getTextEquivalent() {
            return textEquivalent;
        }

        /**
         * Indicates how quickly the action should be addressed with respect to other actions.
         * 
         * @return
         *     An immutable object of type {@link RequestPriority} that may be null.
         */
        public RequestPriority getPriority() {
            return priority;
        }

        /**
         * A code that provides meaning for the action or action group. For example, a section may have a LOINC code for a 
         * section of a documentation template.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getCode() {
            return code;
        }

        /**
         * Didactic or other informational resources associated with the action that can be provided to the CDS recipient. 
         * Information resources can include inline text commentary and links to web resources.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
         */
        public List<RelatedArtifact> getDocumentation() {
            return documentation;
        }

        /**
         * Goals that are intended to be achieved by following the requests in this action.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getGoal() {
            return goal;
        }

        /**
         * An expression that describes applicability criteria, or start/stop conditions for the action.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Condition} that may be empty.
         */
        public List<Condition> getCondition() {
            return condition;
        }

        /**
         * Defines input data requirements for the action.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Input} that may be empty.
         */
        public List<Input> getInput() {
            return input;
        }

        /**
         * Defines the outputs of the action, if any.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Output} that may be empty.
         */
        public List<Output> getOutput() {
            return output;
        }

        /**
         * A relationship to another action such as "before" or "30-60 minutes after start of".
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedAction} that may be empty.
         */
        public List<RelatedAction> getRelatedAction() {
            return relatedAction;
        }

        /**
         * An optional value describing when the action should be performed.
         * 
         * @return
         *     An immutable object of type {@link DateTime}, {@link Age}, {@link Period}, {@link Duration}, {@link Range} or {@link 
         *     Timing} that may be null.
         */
        public Element getTiming() {
            return timing;
        }

        /**
         * Identifies the facility where the action will occur; e.g. home, hospital, specific clinic, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getLocation() {
            return location;
        }

        /**
         * The participant that should perform or be responsible for this action.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Participant} that may be empty.
         */
        public List<Participant> getParticipant() {
            return participant;
        }

        /**
         * The type of action to perform (create, update, remove).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Defines the grouping behavior for the action and its children.
         * 
         * @return
         *     An immutable object of type {@link ActionGroupingBehavior} that may be null.
         */
        public ActionGroupingBehavior getGroupingBehavior() {
            return groupingBehavior;
        }

        /**
         * Defines the selection behavior for the action and its children.
         * 
         * @return
         *     An immutable object of type {@link ActionSelectionBehavior} that may be null.
         */
        public ActionSelectionBehavior getSelectionBehavior() {
            return selectionBehavior;
        }

        /**
         * Defines expectations around whether an action is required.
         * 
         * @return
         *     An immutable object of type {@link ActionRequiredBehavior} that may be null.
         */
        public ActionRequiredBehavior getRequiredBehavior() {
            return requiredBehavior;
        }

        /**
         * Defines whether the action should usually be preselected.
         * 
         * @return
         *     An immutable object of type {@link ActionPrecheckBehavior} that may be null.
         */
        public ActionPrecheckBehavior getPrecheckBehavior() {
            return precheckBehavior;
        }

        /**
         * Defines whether the action can be selected multiple times.
         * 
         * @return
         *     An immutable object of type {@link ActionCardinalityBehavior} that may be null.
         */
        public ActionCardinalityBehavior getCardinalityBehavior() {
            return cardinalityBehavior;
        }

        /**
         * The resource that is the target of the action (e.g. CommunicationRequest).
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getResource() {
            return resource;
        }

        /**
         * A reference to an ActivityDefinition that describes the action to be taken in detail, a PlanDefinition that describes 
         * a series of actions to be taken, a Questionnaire that should be filled out, a SpecimenDefinition describing a specimen 
         * to be collected, or an ObservationDefinition that specifies what observation should be captured.
         * 
         * @return
         *     An immutable object of type {@link Canonical} or {@link Uri} that may be null.
         */
        public Element getDefinition() {
            return definition;
        }

        /**
         * A reference to a StructureMap resource that defines a transform that can be executed to produce the intent resource 
         * using the ActivityDefinition instance as the input.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getTransform() {
            return transform;
        }

        /**
         * Customizations that should be applied to the statically defined resource. For example, if the dosage of a medication 
         * must be computed based on the patient's weight, a customization would be used to specify an expression that calculated 
         * the weight, and the path on the resource that would contain the result.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link DynamicValue} that may be empty.
         */
        public List<DynamicValue> getDynamicValue() {
            return dynamicValue;
        }

        /**
         * Sub actions.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Action} that may be empty.
         */
        public List<RequestOrchestration.Action> getAction() {
            return action;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (linkId != null) || 
                (prefix != null) || 
                (title != null) || 
                (description != null) || 
                (textEquivalent != null) || 
                (priority != null) || 
                !code.isEmpty() || 
                !documentation.isEmpty() || 
                !goal.isEmpty() || 
                !condition.isEmpty() || 
                !input.isEmpty() || 
                !output.isEmpty() || 
                !relatedAction.isEmpty() || 
                (timing != null) || 
                (location != null) || 
                !participant.isEmpty() || 
                (type != null) || 
                (groupingBehavior != null) || 
                (selectionBehavior != null) || 
                (requiredBehavior != null) || 
                (precheckBehavior != null) || 
                (cardinalityBehavior != null) || 
                (resource != null) || 
                (definition != null) || 
                (transform != null) || 
                !dynamicValue.isEmpty() || 
                !action.isEmpty();
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
                    accept(linkId, "linkId", visitor);
                    accept(prefix, "prefix", visitor);
                    accept(title, "title", visitor);
                    accept(description, "description", visitor);
                    accept(textEquivalent, "textEquivalent", visitor);
                    accept(priority, "priority", visitor);
                    accept(code, "code", visitor, CodeableConcept.class);
                    accept(documentation, "documentation", visitor, RelatedArtifact.class);
                    accept(goal, "goal", visitor, Reference.class);
                    accept(condition, "condition", visitor, Condition.class);
                    accept(input, "input", visitor, Input.class);
                    accept(output, "output", visitor, Output.class);
                    accept(relatedAction, "relatedAction", visitor, RelatedAction.class);
                    accept(timing, "timing", visitor);
                    accept(location, "location", visitor);
                    accept(participant, "participant", visitor, Participant.class);
                    accept(type, "type", visitor);
                    accept(groupingBehavior, "groupingBehavior", visitor);
                    accept(selectionBehavior, "selectionBehavior", visitor);
                    accept(requiredBehavior, "requiredBehavior", visitor);
                    accept(precheckBehavior, "precheckBehavior", visitor);
                    accept(cardinalityBehavior, "cardinalityBehavior", visitor);
                    accept(resource, "resource", visitor);
                    accept(definition, "definition", visitor);
                    accept(transform, "transform", visitor);
                    accept(dynamicValue, "dynamicValue", visitor, DynamicValue.class);
                    accept(action, "action", visitor, RequestOrchestration.Action.class);
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
            Action other = (Action) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(linkId, other.linkId) && 
                Objects.equals(prefix, other.prefix) && 
                Objects.equals(title, other.title) && 
                Objects.equals(description, other.description) && 
                Objects.equals(textEquivalent, other.textEquivalent) && 
                Objects.equals(priority, other.priority) && 
                Objects.equals(code, other.code) && 
                Objects.equals(documentation, other.documentation) && 
                Objects.equals(goal, other.goal) && 
                Objects.equals(condition, other.condition) && 
                Objects.equals(input, other.input) && 
                Objects.equals(output, other.output) && 
                Objects.equals(relatedAction, other.relatedAction) && 
                Objects.equals(timing, other.timing) && 
                Objects.equals(location, other.location) && 
                Objects.equals(participant, other.participant) && 
                Objects.equals(type, other.type) && 
                Objects.equals(groupingBehavior, other.groupingBehavior) && 
                Objects.equals(selectionBehavior, other.selectionBehavior) && 
                Objects.equals(requiredBehavior, other.requiredBehavior) && 
                Objects.equals(precheckBehavior, other.precheckBehavior) && 
                Objects.equals(cardinalityBehavior, other.cardinalityBehavior) && 
                Objects.equals(resource, other.resource) && 
                Objects.equals(definition, other.definition) && 
                Objects.equals(transform, other.transform) && 
                Objects.equals(dynamicValue, other.dynamicValue) && 
                Objects.equals(action, other.action);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    linkId, 
                    prefix, 
                    title, 
                    description, 
                    textEquivalent, 
                    priority, 
                    code, 
                    documentation, 
                    goal, 
                    condition, 
                    input, 
                    output, 
                    relatedAction, 
                    timing, 
                    location, 
                    participant, 
                    type, 
                    groupingBehavior, 
                    selectionBehavior, 
                    requiredBehavior, 
                    precheckBehavior, 
                    cardinalityBehavior, 
                    resource, 
                    definition, 
                    transform, 
                    dynamicValue, 
                    action);
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
            private String linkId;
            private String prefix;
            private String title;
            private Markdown description;
            private Markdown textEquivalent;
            private RequestPriority priority;
            private List<CodeableConcept> code = new ArrayList<>();
            private List<RelatedArtifact> documentation = new ArrayList<>();
            private List<Reference> goal = new ArrayList<>();
            private List<Condition> condition = new ArrayList<>();
            private List<Input> input = new ArrayList<>();
            private List<Output> output = new ArrayList<>();
            private List<RelatedAction> relatedAction = new ArrayList<>();
            private Element timing;
            private CodeableReference location;
            private List<Participant> participant = new ArrayList<>();
            private CodeableConcept type;
            private ActionGroupingBehavior groupingBehavior;
            private ActionSelectionBehavior selectionBehavior;
            private ActionRequiredBehavior requiredBehavior;
            private ActionPrecheckBehavior precheckBehavior;
            private ActionCardinalityBehavior cardinalityBehavior;
            private Reference resource;
            private Element definition;
            private Canonical transform;
            private List<DynamicValue> dynamicValue = new ArrayList<>();
            private List<RequestOrchestration.Action> action = new ArrayList<>();

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
             * Convenience method for setting {@code linkId}.
             * 
             * @param linkId
             *     Pointer to specific item from the PlanDefinition
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #linkId(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder linkId(java.lang.String linkId) {
                this.linkId = (linkId == null) ? null : String.of(linkId);
                return this;
            }

            /**
             * The linkId of the action from the PlanDefinition that corresponds to this action in the RequestOrchestration resource.
             * 
             * @param linkId
             *     Pointer to specific item from the PlanDefinition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(String linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * Convenience method for setting {@code prefix}.
             * 
             * @param prefix
             *     User-visible prefix for the action (e.g. 1. or A.)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #prefix(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder prefix(java.lang.String prefix) {
                this.prefix = (prefix == null) ? null : String.of(prefix);
                return this;
            }

            /**
             * A user-visible prefix for the action. For example a section or item numbering such as 1. or A.
             * 
             * @param prefix
             *     User-visible prefix for the action (e.g. 1. or A.)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder prefix(String prefix) {
                this.prefix = prefix;
                return this;
            }

            /**
             * Convenience method for setting {@code title}.
             * 
             * @param title
             *     User-visible title
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #title(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder title(java.lang.String title) {
                this.title = (title == null) ? null : String.of(title);
                return this;
            }

            /**
             * The title of the action displayed to a user.
             * 
             * @param title
             *     User-visible title
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * A short description of the action used to provide a summary to display to the user.
             * 
             * @param description
             *     Short description of the action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * A text equivalent of the action to be performed. This provides a human-interpretable description of the action when 
             * the definition is consumed by a system that might not be capable of interpreting it dynamically.
             * 
             * @param textEquivalent
             *     Static text equivalent of the action, used if the dynamic aspects cannot be interpreted by the receiving system
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder textEquivalent(Markdown textEquivalent) {
                this.textEquivalent = textEquivalent;
                return this;
            }

            /**
             * Indicates how quickly the action should be addressed with respect to other actions.
             * 
             * @param priority
             *     routine | urgent | asap | stat
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder priority(RequestPriority priority) {
                this.priority = priority;
                return this;
            }

            /**
             * A code that provides meaning for the action or action group. For example, a section may have a LOINC code for a 
             * section of a documentation template.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param code
             *     Code representing the meaning of the action or sub-actions
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept... code) {
                for (CodeableConcept value : code) {
                    this.code.add(value);
                }
                return this;
            }

            /**
             * A code that provides meaning for the action or action group. For example, a section may have a LOINC code for a 
             * section of a documentation template.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param code
             *     Code representing the meaning of the action or sub-actions
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder code(Collection<CodeableConcept> code) {
                this.code = new ArrayList<>(code);
                return this;
            }

            /**
             * Didactic or other informational resources associated with the action that can be provided to the CDS recipient. 
             * Information resources can include inline text commentary and links to web resources.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param documentation
             *     Supporting documentation for the intended performer of the action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder documentation(RelatedArtifact... documentation) {
                for (RelatedArtifact value : documentation) {
                    this.documentation.add(value);
                }
                return this;
            }

            /**
             * Didactic or other informational resources associated with the action that can be provided to the CDS recipient. 
             * Information resources can include inline text commentary and links to web resources.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param documentation
             *     Supporting documentation for the intended performer of the action
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder documentation(Collection<RelatedArtifact> documentation) {
                this.documentation = new ArrayList<>(documentation);
                return this;
            }

            /**
             * Goals that are intended to be achieved by following the requests in this action.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Goal}</li>
             * </ul>
             * 
             * @param goal
             *     What goals
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder goal(Reference... goal) {
                for (Reference value : goal) {
                    this.goal.add(value);
                }
                return this;
            }

            /**
             * Goals that are intended to be achieved by following the requests in this action.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Goal}</li>
             * </ul>
             * 
             * @param goal
             *     What goals
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder goal(Collection<Reference> goal) {
                this.goal = new ArrayList<>(goal);
                return this;
            }

            /**
             * An expression that describes applicability criteria, or start/stop conditions for the action.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     Whether or not the action is applicable
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder condition(Condition... condition) {
                for (Condition value : condition) {
                    this.condition.add(value);
                }
                return this;
            }

            /**
             * An expression that describes applicability criteria, or start/stop conditions for the action.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     Whether or not the action is applicable
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder condition(Collection<Condition> condition) {
                this.condition = new ArrayList<>(condition);
                return this;
            }

            /**
             * Defines input data requirements for the action.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param input
             *     Input data requirements
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder input(Input... input) {
                for (Input value : input) {
                    this.input.add(value);
                }
                return this;
            }

            /**
             * Defines input data requirements for the action.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param input
             *     Input data requirements
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder input(Collection<Input> input) {
                this.input = new ArrayList<>(input);
                return this;
            }

            /**
             * Defines the outputs of the action, if any.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param output
             *     Output data definition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder output(Output... output) {
                for (Output value : output) {
                    this.output.add(value);
                }
                return this;
            }

            /**
             * Defines the outputs of the action, if any.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param output
             *     Output data definition
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder output(Collection<Output> output) {
                this.output = new ArrayList<>(output);
                return this;
            }

            /**
             * A relationship to another action such as "before" or "30-60 minutes after start of".
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedAction
             *     Relationship to another action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatedAction(RelatedAction... relatedAction) {
                for (RelatedAction value : relatedAction) {
                    this.relatedAction.add(value);
                }
                return this;
            }

            /**
             * A relationship to another action such as "before" or "30-60 minutes after start of".
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedAction
             *     Relationship to another action
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder relatedAction(Collection<RelatedAction> relatedAction) {
                this.relatedAction = new ArrayList<>(relatedAction);
                return this;
            }

            /**
             * An optional value describing when the action should be performed.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link DateTime}</li>
             * <li>{@link Age}</li>
             * <li>{@link Period}</li>
             * <li>{@link Duration}</li>
             * <li>{@link Range}</li>
             * <li>{@link Timing}</li>
             * </ul>
             * 
             * @param timing
             *     When the action should take place
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder timing(Element timing) {
                this.timing = timing;
                return this;
            }

            /**
             * Identifies the facility where the action will occur; e.g. home, hospital, specific clinic, etc.
             * 
             * @param location
             *     Where it should happen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(CodeableReference location) {
                this.location = location;
                return this;
            }

            /**
             * The participant that should perform or be responsible for this action.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param participant
             *     Who should perform the action
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
             * The participant that should perform or be responsible for this action.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param participant
             *     Who should perform the action
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
             * The type of action to perform (create, update, remove).
             * 
             * @param type
             *     create | update | remove | fire-event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Defines the grouping behavior for the action and its children.
             * 
             * @param groupingBehavior
             *     visual-group | logical-group | sentence-group
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder groupingBehavior(ActionGroupingBehavior groupingBehavior) {
                this.groupingBehavior = groupingBehavior;
                return this;
            }

            /**
             * Defines the selection behavior for the action and its children.
             * 
             * @param selectionBehavior
             *     any | all | all-or-none | exactly-one | at-most-one | one-or-more
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder selectionBehavior(ActionSelectionBehavior selectionBehavior) {
                this.selectionBehavior = selectionBehavior;
                return this;
            }

            /**
             * Defines expectations around whether an action is required.
             * 
             * @param requiredBehavior
             *     must | could | must-unless-documented
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder requiredBehavior(ActionRequiredBehavior requiredBehavior) {
                this.requiredBehavior = requiredBehavior;
                return this;
            }

            /**
             * Defines whether the action should usually be preselected.
             * 
             * @param precheckBehavior
             *     yes | no
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder precheckBehavior(ActionPrecheckBehavior precheckBehavior) {
                this.precheckBehavior = precheckBehavior;
                return this;
            }

            /**
             * Defines whether the action can be selected multiple times.
             * 
             * @param cardinalityBehavior
             *     single | multiple
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder cardinalityBehavior(ActionCardinalityBehavior cardinalityBehavior) {
                this.cardinalityBehavior = cardinalityBehavior;
                return this;
            }

            /**
             * The resource that is the target of the action (e.g. CommunicationRequest).
             * 
             * @param resource
             *     The target of the action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder resource(Reference resource) {
                this.resource = resource;
                return this;
            }

            /**
             * A reference to an ActivityDefinition that describes the action to be taken in detail, a PlanDefinition that describes 
             * a series of actions to be taken, a Questionnaire that should be filled out, a SpecimenDefinition describing a specimen 
             * to be collected, or an ObservationDefinition that specifies what observation should be captured.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Canonical}</li>
             * <li>{@link Uri}</li>
             * </ul>
             * 
             * @param definition
             *     Description of the activity to be performed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definition(Element definition) {
                this.definition = definition;
                return this;
            }

            /**
             * A reference to a StructureMap resource that defines a transform that can be executed to produce the intent resource 
             * using the ActivityDefinition instance as the input.
             * 
             * @param transform
             *     Transform to apply the template
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder transform(Canonical transform) {
                this.transform = transform;
                return this;
            }

            /**
             * Customizations that should be applied to the statically defined resource. For example, if the dosage of a medication 
             * must be computed based on the patient's weight, a customization would be used to specify an expression that calculated 
             * the weight, and the path on the resource that would contain the result.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dynamicValue
             *     Dynamic aspects of the definition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dynamicValue(DynamicValue... dynamicValue) {
                for (DynamicValue value : dynamicValue) {
                    this.dynamicValue.add(value);
                }
                return this;
            }

            /**
             * Customizations that should be applied to the statically defined resource. For example, if the dosage of a medication 
             * must be computed based on the patient's weight, a customization would be used to specify an expression that calculated 
             * the weight, and the path on the resource that would contain the result.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dynamicValue
             *     Dynamic aspects of the definition
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder dynamicValue(Collection<DynamicValue> dynamicValue) {
                this.dynamicValue = new ArrayList<>(dynamicValue);
                return this;
            }

            /**
             * Sub actions.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param action
             *     Sub action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder action(RequestOrchestration.Action... action) {
                for (RequestOrchestration.Action value : action) {
                    this.action.add(value);
                }
                return this;
            }

            /**
             * Sub actions.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param action
             *     Sub action
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder action(Collection<RequestOrchestration.Action> action) {
                this.action = new ArrayList<>(action);
                return this;
            }

            /**
             * Build the {@link Action}
             * 
             * @return
             *     An immutable object of type {@link Action}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Action per the base specification
             */
            @Override
            public Action build() {
                Action action = new Action(this);
                if (validating) {
                    validate(action);
                }
                return action;
            }

            protected void validate(Action action) {
                super.validate(action);
                ValidationSupport.checkList(action.code, "code", CodeableConcept.class);
                ValidationSupport.checkList(action.documentation, "documentation", RelatedArtifact.class);
                ValidationSupport.checkList(action.goal, "goal", Reference.class);
                ValidationSupport.checkList(action.condition, "condition", Condition.class);
                ValidationSupport.checkList(action.input, "input", Input.class);
                ValidationSupport.checkList(action.output, "output", Output.class);
                ValidationSupport.checkList(action.relatedAction, "relatedAction", RelatedAction.class);
                ValidationSupport.choiceElement(action.timing, "timing", DateTime.class, Age.class, Period.class, Duration.class, Range.class, Timing.class);
                ValidationSupport.checkList(action.participant, "participant", Participant.class);
                ValidationSupport.choiceElement(action.definition, "definition", Canonical.class, Uri.class);
                ValidationSupport.checkList(action.dynamicValue, "dynamicValue", DynamicValue.class);
                ValidationSupport.checkList(action.action, "action", RequestOrchestration.Action.class);
                ValidationSupport.checkReferenceType(action.goal, "goal", "Goal");
                ValidationSupport.requireValueOrChildren(action);
            }

            protected Builder from(Action action) {
                super.from(action);
                linkId = action.linkId;
                prefix = action.prefix;
                title = action.title;
                description = action.description;
                textEquivalent = action.textEquivalent;
                priority = action.priority;
                code.addAll(action.code);
                documentation.addAll(action.documentation);
                goal.addAll(action.goal);
                condition.addAll(action.condition);
                input.addAll(action.input);
                output.addAll(action.output);
                relatedAction.addAll(action.relatedAction);
                timing = action.timing;
                location = action.location;
                participant.addAll(action.participant);
                type = action.type;
                groupingBehavior = action.groupingBehavior;
                selectionBehavior = action.selectionBehavior;
                requiredBehavior = action.requiredBehavior;
                precheckBehavior = action.precheckBehavior;
                cardinalityBehavior = action.cardinalityBehavior;
                resource = action.resource;
                definition = action.definition;
                transform = action.transform;
                dynamicValue.addAll(action.dynamicValue);
                this.action.addAll(action.action);
                return this;
            }
        }

        /**
         * An expression that describes applicability criteria, or start/stop conditions for the action.
         */
        public static class Condition extends BackboneElement {
            @Binding(
                bindingName = "ActionConditionKind",
                strength = BindingStrength.Value.REQUIRED,
                description = "The kind of condition for the action.",
                valueSet = "http://hl7.org/fhir/ValueSet/action-condition-kind|5.0.0"
            )
            @Required
            private final ActionConditionKind kind;
            private final Expression expression;

            private Condition(Builder builder) {
                super(builder);
                kind = builder.kind;
                expression = builder.expression;
            }

            /**
             * The kind of condition.
             * 
             * @return
             *     An immutable object of type {@link ActionConditionKind} that is non-null.
             */
            public ActionConditionKind getKind() {
                return kind;
            }

            /**
             * An expression that returns true or false, indicating whether or not the condition is satisfied.
             * 
             * @return
             *     An immutable object of type {@link Expression} that may be null.
             */
            public Expression getExpression() {
                return expression;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (kind != null) || 
                    (expression != null);
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
                        accept(kind, "kind", visitor);
                        accept(expression, "expression", visitor);
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
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(kind, other.kind) && 
                    Objects.equals(expression, other.expression);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        kind, 
                        expression);
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
                private ActionConditionKind kind;
                private Expression expression;

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
                 * The kind of condition.
                 * 
                 * <p>This element is required.
                 * 
                 * @param kind
                 *     applicability | start | stop
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder kind(ActionConditionKind kind) {
                    this.kind = kind;
                    return this;
                }

                /**
                 * An expression that returns true or false, indicating whether or not the condition is satisfied.
                 * 
                 * @param expression
                 *     Boolean-valued expression
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder expression(Expression expression) {
                    this.expression = expression;
                    return this;
                }

                /**
                 * Build the {@link Condition}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>kind</li>
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
                    ValidationSupport.requireNonNull(condition.kind, "kind");
                    ValidationSupport.requireValueOrChildren(condition);
                }

                protected Builder from(Condition condition) {
                    super.from(condition);
                    kind = condition.kind;
                    expression = condition.expression;
                    return this;
                }
            }
        }

        /**
         * Defines input data requirements for the action.
         */
        public static class Input extends BackboneElement {
            private final String title;
            private final DataRequirement requirement;
            private final Id relatedData;

            private Input(Builder builder) {
                super(builder);
                title = builder.title;
                requirement = builder.requirement;
                relatedData = builder.relatedData;
            }

            /**
             * A human-readable label for the data requirement used to label data flows in BPMN or similar diagrams. Also provides a 
             * human readable label when rendering the data requirement that conveys its purpose to human readers.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getTitle() {
                return title;
            }

            /**
             * Defines the data that is to be provided as input to the action.
             * 
             * @return
             *     An immutable object of type {@link DataRequirement} that may be null.
             */
            public DataRequirement getRequirement() {
                return requirement;
            }

            /**
             * Points to an existing input or output element that provides data to this input.
             * 
             * @return
             *     An immutable object of type {@link Id} that may be null.
             */
            public Id getRelatedData() {
                return relatedData;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (title != null) || 
                    (requirement != null) || 
                    (relatedData != null);
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
                        accept(title, "title", visitor);
                        accept(requirement, "requirement", visitor);
                        accept(relatedData, "relatedData", visitor);
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
                Input other = (Input) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(title, other.title) && 
                    Objects.equals(requirement, other.requirement) && 
                    Objects.equals(relatedData, other.relatedData);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        title, 
                        requirement, 
                        relatedData);
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
                private String title;
                private DataRequirement requirement;
                private Id relatedData;

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
                 * Convenience method for setting {@code title}.
                 * 
                 * @param title
                 *     User-visible title
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #title(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder title(java.lang.String title) {
                    this.title = (title == null) ? null : String.of(title);
                    return this;
                }

                /**
                 * A human-readable label for the data requirement used to label data flows in BPMN or similar diagrams. Also provides a 
                 * human readable label when rendering the data requirement that conveys its purpose to human readers.
                 * 
                 * @param title
                 *     User-visible title
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                /**
                 * Defines the data that is to be provided as input to the action.
                 * 
                 * @param requirement
                 *     What data is provided
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder requirement(DataRequirement requirement) {
                    this.requirement = requirement;
                    return this;
                }

                /**
                 * Points to an existing input or output element that provides data to this input.
                 * 
                 * @param relatedData
                 *     What data is provided
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder relatedData(Id relatedData) {
                    this.relatedData = relatedData;
                    return this;
                }

                /**
                 * Build the {@link Input}
                 * 
                 * @return
                 *     An immutable object of type {@link Input}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Input per the base specification
                 */
                @Override
                public Input build() {
                    Input input = new Input(this);
                    if (validating) {
                        validate(input);
                    }
                    return input;
                }

                protected void validate(Input input) {
                    super.validate(input);
                    ValidationSupport.requireValueOrChildren(input);
                }

                protected Builder from(Input input) {
                    super.from(input);
                    title = input.title;
                    requirement = input.requirement;
                    relatedData = input.relatedData;
                    return this;
                }
            }
        }

        /**
         * Defines the outputs of the action, if any.
         */
        public static class Output extends BackboneElement {
            private final String title;
            private final DataRequirement requirement;
            private final String relatedData;

            private Output(Builder builder) {
                super(builder);
                title = builder.title;
                requirement = builder.requirement;
                relatedData = builder.relatedData;
            }

            /**
             * A human-readable label for the data requirement used to label data flows in BPMN or similar diagrams. Also provides a 
             * human readable label when rendering the data requirement that conveys its purpose to human readers.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getTitle() {
                return title;
            }

            /**
             * Defines the data that results as output from the action.
             * 
             * @return
             *     An immutable object of type {@link DataRequirement} that may be null.
             */
            public DataRequirement getRequirement() {
                return requirement;
            }

            /**
             * Points to an existing input or output element that is results as output from the action.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getRelatedData() {
                return relatedData;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (title != null) || 
                    (requirement != null) || 
                    (relatedData != null);
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
                        accept(title, "title", visitor);
                        accept(requirement, "requirement", visitor);
                        accept(relatedData, "relatedData", visitor);
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
                Output other = (Output) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(title, other.title) && 
                    Objects.equals(requirement, other.requirement) && 
                    Objects.equals(relatedData, other.relatedData);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        title, 
                        requirement, 
                        relatedData);
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
                private String title;
                private DataRequirement requirement;
                private String relatedData;

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
                 * Convenience method for setting {@code title}.
                 * 
                 * @param title
                 *     User-visible title
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #title(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder title(java.lang.String title) {
                    this.title = (title == null) ? null : String.of(title);
                    return this;
                }

                /**
                 * A human-readable label for the data requirement used to label data flows in BPMN or similar diagrams. Also provides a 
                 * human readable label when rendering the data requirement that conveys its purpose to human readers.
                 * 
                 * @param title
                 *     User-visible title
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                /**
                 * Defines the data that results as output from the action.
                 * 
                 * @param requirement
                 *     What data is provided
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder requirement(DataRequirement requirement) {
                    this.requirement = requirement;
                    return this;
                }

                /**
                 * Convenience method for setting {@code relatedData}.
                 * 
                 * @param relatedData
                 *     What data is provided
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #relatedData(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder relatedData(java.lang.String relatedData) {
                    this.relatedData = (relatedData == null) ? null : String.of(relatedData);
                    return this;
                }

                /**
                 * Points to an existing input or output element that is results as output from the action.
                 * 
                 * @param relatedData
                 *     What data is provided
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder relatedData(String relatedData) {
                    this.relatedData = relatedData;
                    return this;
                }

                /**
                 * Build the {@link Output}
                 * 
                 * @return
                 *     An immutable object of type {@link Output}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Output per the base specification
                 */
                @Override
                public Output build() {
                    Output output = new Output(this);
                    if (validating) {
                        validate(output);
                    }
                    return output;
                }

                protected void validate(Output output) {
                    super.validate(output);
                    ValidationSupport.requireValueOrChildren(output);
                }

                protected Builder from(Output output) {
                    super.from(output);
                    title = output.title;
                    requirement = output.requirement;
                    relatedData = output.relatedData;
                    return this;
                }
            }
        }

        /**
         * A relationship to another action such as "before" or "30-60 minutes after start of".
         */
        public static class RelatedAction extends BackboneElement {
            @Required
            private final Id targetId;
            @Binding(
                bindingName = "ActionRelationshipType",
                strength = BindingStrength.Value.REQUIRED,
                description = "Defines the types of relationships between actions.",
                valueSet = "http://hl7.org/fhir/ValueSet/action-relationship-type|5.0.0"
            )
            @Required
            private final ActionRelationshipType relationship;
            @Binding(
                bindingName = "ActionRelationshipType",
                strength = BindingStrength.Value.REQUIRED,
                description = "Defines the types of relationships between actions.",
                valueSet = "http://hl7.org/fhir/ValueSet/action-relationship-type|5.0.0"
            )
            private final ActionRelationshipType endRelationship;
            @Choice({ Duration.class, Range.class })
            private final Element offset;

            private RelatedAction(Builder builder) {
                super(builder);
                targetId = builder.targetId;
                relationship = builder.relationship;
                endRelationship = builder.endRelationship;
                offset = builder.offset;
            }

            /**
             * The element id of the target related action.
             * 
             * @return
             *     An immutable object of type {@link Id} that is non-null.
             */
            public Id getTargetId() {
                return targetId;
            }

            /**
             * The relationship of this action to the related action.
             * 
             * @return
             *     An immutable object of type {@link ActionRelationshipType} that is non-null.
             */
            public ActionRelationshipType getRelationship() {
                return relationship;
            }

            /**
             * The relationship of the end of this action to the related action.
             * 
             * @return
             *     An immutable object of type {@link ActionRelationshipType} that may be null.
             */
            public ActionRelationshipType getEndRelationship() {
                return endRelationship;
            }

            /**
             * A duration or range of durations to apply to the relationship. For example, 30-60 minutes before.
             * 
             * @return
             *     An immutable object of type {@link Duration} or {@link Range} that may be null.
             */
            public Element getOffset() {
                return offset;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (targetId != null) || 
                    (relationship != null) || 
                    (endRelationship != null) || 
                    (offset != null);
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
                        accept(targetId, "targetId", visitor);
                        accept(relationship, "relationship", visitor);
                        accept(endRelationship, "endRelationship", visitor);
                        accept(offset, "offset", visitor);
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
                RelatedAction other = (RelatedAction) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(targetId, other.targetId) && 
                    Objects.equals(relationship, other.relationship) && 
                    Objects.equals(endRelationship, other.endRelationship) && 
                    Objects.equals(offset, other.offset);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        targetId, 
                        relationship, 
                        endRelationship, 
                        offset);
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
                private Id targetId;
                private ActionRelationshipType relationship;
                private ActionRelationshipType endRelationship;
                private Element offset;

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
                 * The element id of the target related action.
                 * 
                 * <p>This element is required.
                 * 
                 * @param targetId
                 *     What action this is related to
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder targetId(Id targetId) {
                    this.targetId = targetId;
                    return this;
                }

                /**
                 * The relationship of this action to the related action.
                 * 
                 * <p>This element is required.
                 * 
                 * @param relationship
                 *     before | before-start | before-end | concurrent | concurrent-with-start | concurrent-with-end | after | after-start | 
                 *     after-end
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder relationship(ActionRelationshipType relationship) {
                    this.relationship = relationship;
                    return this;
                }

                /**
                 * The relationship of the end of this action to the related action.
                 * 
                 * @param endRelationship
                 *     before | before-start | before-end | concurrent | concurrent-with-start | concurrent-with-end | after | after-start | 
                 *     after-end
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder endRelationship(ActionRelationshipType endRelationship) {
                    this.endRelationship = endRelationship;
                    return this;
                }

                /**
                 * A duration or range of durations to apply to the relationship. For example, 30-60 minutes before.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Duration}</li>
                 * <li>{@link Range}</li>
                 * </ul>
                 * 
                 * @param offset
                 *     Time offset for the relationship
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder offset(Element offset) {
                    this.offset = offset;
                    return this;
                }

                /**
                 * Build the {@link RelatedAction}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>targetId</li>
                 * <li>relationship</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link RelatedAction}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid RelatedAction per the base specification
                 */
                @Override
                public RelatedAction build() {
                    RelatedAction relatedAction = new RelatedAction(this);
                    if (validating) {
                        validate(relatedAction);
                    }
                    return relatedAction;
                }

                protected void validate(RelatedAction relatedAction) {
                    super.validate(relatedAction);
                    ValidationSupport.requireNonNull(relatedAction.targetId, "targetId");
                    ValidationSupport.requireNonNull(relatedAction.relationship, "relationship");
                    ValidationSupport.choiceElement(relatedAction.offset, "offset", Duration.class, Range.class);
                    ValidationSupport.requireValueOrChildren(relatedAction);
                }

                protected Builder from(RelatedAction relatedAction) {
                    super.from(relatedAction);
                    targetId = relatedAction.targetId;
                    relationship = relatedAction.relationship;
                    endRelationship = relatedAction.endRelationship;
                    offset = relatedAction.offset;
                    return this;
                }
            }
        }

        /**
         * The participant that should perform or be responsible for this action.
         */
        public static class Participant extends BackboneElement {
            @Binding(
                bindingName = "ActivityParticipantType",
                strength = BindingStrength.Value.REQUIRED,
                description = "The type of participant in the activity.",
                valueSet = "http://hl7.org/fhir/ValueSet/action-participant-type|5.0.0"
            )
            private final ActivityParticipantType type;
            private final Canonical typeCanonical;
            @ReferenceTarget({ "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
            private final Reference typeReference;
            @Binding(
                bindingName = "ActivityParticipantRole",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Defines roles played by participants for the action.",
                valueSet = "http://terminology.hl7.org/ValueSet/action-participant-role"
            )
            private final CodeableConcept role;
            @Binding(
                bindingName = "ActionParticipantFunction",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/action-participant-function"
            )
            private final CodeableConcept function;
            @ReferenceTarget({ "CapabilityStatement", "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
            @Choice({ Canonical.class, Reference.class })
            private final Element actor;

            private Participant(Builder builder) {
                super(builder);
                type = builder.type;
                typeCanonical = builder.typeCanonical;
                typeReference = builder.typeReference;
                role = builder.role;
                function = builder.function;
                actor = builder.actor;
            }

            /**
             * The type of participant in the action.
             * 
             * @return
             *     An immutable object of type {@link ActivityParticipantType} that may be null.
             */
            public ActivityParticipantType getType() {
                return type;
            }

            /**
             * The type of participant in the action.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getTypeCanonical() {
                return typeCanonical;
            }

            /**
             * The type of participant in the action.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getTypeReference() {
                return typeReference;
            }

            /**
             * The role the participant should play in performing the described action.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getRole() {
                return role;
            }

            /**
             * Indicates how the actor will be involved in the action - author, reviewer, witness, etc.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getFunction() {
                return function;
            }

            /**
             * A reference to the actual participant.
             * 
             * @return
             *     An immutable object of type {@link Canonical} or {@link Reference} that may be null.
             */
            public Element getActor() {
                return actor;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (typeCanonical != null) || 
                    (typeReference != null) || 
                    (role != null) || 
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
                        accept(type, "type", visitor);
                        accept(typeCanonical, "typeCanonical", visitor);
                        accept(typeReference, "typeReference", visitor);
                        accept(role, "role", visitor);
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
                    Objects.equals(type, other.type) && 
                    Objects.equals(typeCanonical, other.typeCanonical) && 
                    Objects.equals(typeReference, other.typeReference) && 
                    Objects.equals(role, other.role) && 
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
                        type, 
                        typeCanonical, 
                        typeReference, 
                        role, 
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
                private ActivityParticipantType type;
                private Canonical typeCanonical;
                private Reference typeReference;
                private CodeableConcept role;
                private CodeableConcept function;
                private Element actor;

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
                 * The type of participant in the action.
                 * 
                 * @param type
                 *     careteam | device | group | healthcareservice | location | organization | patient | practitioner | practitionerrole | 
                 *     relatedperson
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(ActivityParticipantType type) {
                    this.type = type;
                    return this;
                }

                /**
                 * The type of participant in the action.
                 * 
                 * @param typeCanonical
                 *     Who or what can participate
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder typeCanonical(Canonical typeCanonical) {
                    this.typeCanonical = typeCanonical;
                    return this;
                }

                /**
                 * The type of participant in the action.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link CareTeam}</li>
                 * <li>{@link Device}</li>
                 * <li>{@link DeviceDefinition}</li>
                 * <li>{@link Endpoint}</li>
                 * <li>{@link Group}</li>
                 * <li>{@link HealthcareService}</li>
                 * <li>{@link Location}</li>
                 * <li>{@link Organization}</li>
                 * <li>{@link Patient}</li>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link PractitionerRole}</li>
                 * <li>{@link RelatedPerson}</li>
                 * </ul>
                 * 
                 * @param typeReference
                 *     Who or what can participate
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder typeReference(Reference typeReference) {
                    this.typeReference = typeReference;
                    return this;
                }

                /**
                 * The role the participant should play in performing the described action.
                 * 
                 * @param role
                 *     E.g. Nurse, Surgeon, Parent, etc
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder role(CodeableConcept role) {
                    this.role = role;
                    return this;
                }

                /**
                 * Indicates how the actor will be involved in the action - author, reviewer, witness, etc.
                 * 
                 * @param function
                 *     E.g. Author, Reviewer, Witness, etc
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder function(CodeableConcept function) {
                    this.function = function;
                    return this;
                }

                /**
                 * A reference to the actual participant.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Canonical}</li>
                 * <li>{@link Reference}</li>
                 * </ul>
                 * 
                 * When of type {@link Reference}, the allowed resource types for this reference are:
                 * <ul>
                 * <li>{@link CapabilityStatement}</li>
                 * <li>{@link CareTeam}</li>
                 * <li>{@link Device}</li>
                 * <li>{@link DeviceDefinition}</li>
                 * <li>{@link Endpoint}</li>
                 * <li>{@link Group}</li>
                 * <li>{@link HealthcareService}</li>
                 * <li>{@link Location}</li>
                 * <li>{@link Organization}</li>
                 * <li>{@link Patient}</li>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link PractitionerRole}</li>
                 * <li>{@link RelatedPerson}</li>
                 * </ul>
                 * 
                 * @param actor
                 *     Who/what is participating?
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder actor(Element actor) {
                    this.actor = actor;
                    return this;
                }

                /**
                 * Build the {@link Participant}
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
                    ValidationSupport.choiceElement(participant.actor, "actor", Canonical.class, Reference.class);
                    ValidationSupport.checkReferenceType(participant.typeReference, "typeReference", "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
                    ValidationSupport.checkReferenceType(participant.actor, "actor", "CapabilityStatement", "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
                    ValidationSupport.requireValueOrChildren(participant);
                }

                protected Builder from(Participant participant) {
                    super.from(participant);
                    type = participant.type;
                    typeCanonical = participant.typeCanonical;
                    typeReference = participant.typeReference;
                    role = participant.role;
                    function = participant.function;
                    actor = participant.actor;
                    return this;
                }
            }
        }

        /**
         * Customizations that should be applied to the statically defined resource. For example, if the dosage of a medication 
         * must be computed based on the patient's weight, a customization would be used to specify an expression that calculated 
         * the weight, and the path on the resource that would contain the result.
         */
        public static class DynamicValue extends BackboneElement {
            private final String path;
            private final Expression expression;

            private DynamicValue(Builder builder) {
                super(builder);
                path = builder.path;
                expression = builder.expression;
            }

            /**
             * The path to the element to be customized. This is the path on the resource that will hold the result of the 
             * calculation defined by the expression. The specified path SHALL be a FHIRPath resolvable on the specified target type 
             * of the ActivityDefinition, and SHALL consist only of identifiers, constant indexers, and a restricted subset of 
             * functions. The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x]) to 
             * traverse multiple-cardinality sub-elements (see the [Simple FHIRPath Profile](fhirpath.html#simple) for full details).
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPath() {
                return path;
            }

            /**
             * An expression specifying the value of the customized element.
             * 
             * @return
             *     An immutable object of type {@link Expression} that may be null.
             */
            public Expression getExpression() {
                return expression;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (path != null) || 
                    (expression != null);
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
                        accept(path, "path", visitor);
                        accept(expression, "expression", visitor);
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
                DynamicValue other = (DynamicValue) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(path, other.path) && 
                    Objects.equals(expression, other.expression);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        path, 
                        expression);
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
                private String path;
                private Expression expression;

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
                 * Convenience method for setting {@code path}.
                 * 
                 * @param path
                 *     The path to the element to be set dynamically
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #path(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder path(java.lang.String path) {
                    this.path = (path == null) ? null : String.of(path);
                    return this;
                }

                /**
                 * The path to the element to be customized. This is the path on the resource that will hold the result of the 
                 * calculation defined by the expression. The specified path SHALL be a FHIRPath resolvable on the specified target type 
                 * of the ActivityDefinition, and SHALL consist only of identifiers, constant indexers, and a restricted subset of 
                 * functions. The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x]) to 
                 * traverse multiple-cardinality sub-elements (see the [Simple FHIRPath Profile](fhirpath.html#simple) for full details).
                 * 
                 * @param path
                 *     The path to the element to be set dynamically
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder path(String path) {
                    this.path = path;
                    return this;
                }

                /**
                 * An expression specifying the value of the customized element.
                 * 
                 * @param expression
                 *     An expression that provides the dynamic value for the customization
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder expression(Expression expression) {
                    this.expression = expression;
                    return this;
                }

                /**
                 * Build the {@link DynamicValue}
                 * 
                 * @return
                 *     An immutable object of type {@link DynamicValue}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid DynamicValue per the base specification
                 */
                @Override
                public DynamicValue build() {
                    DynamicValue dynamicValue = new DynamicValue(this);
                    if (validating) {
                        validate(dynamicValue);
                    }
                    return dynamicValue;
                }

                protected void validate(DynamicValue dynamicValue) {
                    super.validate(dynamicValue);
                    ValidationSupport.requireValueOrChildren(dynamicValue);
                }

                protected Builder from(DynamicValue dynamicValue) {
                    super.from(dynamicValue);
                    path = dynamicValue.path;
                    expression = dynamicValue.expression;
                    return this;
                }
            }
        }
    }
}
