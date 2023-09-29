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
import org.linuxforhealth.fhir.model.r5.type.Address;
import org.linuxforhealth.fhir.model.r5.type.Age;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.Availability;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Base64Binary;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactDetail;
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Count;
import org.linuxforhealth.fhir.model.r5.type.DataRequirement;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Distance;
import org.linuxforhealth.fhir.model.r5.type.Dosage;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.ExtendedContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.HumanName;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Integer64;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Oid;
import org.linuxforhealth.fhir.model.r5.type.ParameterDefinition;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.RatioRange;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.SampledData;
import org.linuxforhealth.fhir.model.r5.type.Signature;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Time;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.TriggerDefinition;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.Uuid;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.TransportIntent;
import org.linuxforhealth.fhir.model.r5.type.code.TransportPriority;
import org.linuxforhealth.fhir.model.r5.type.code.TransportStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Record of transport of item.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "transport-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/performer-role",
    expression = "performerType.exists() implies (performerType.all(memberOf('http://hl7.org/fhir/ValueSet/performer-role', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Transport",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Transport extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    private final Canonical instantiatesCanonical;
    @Summary
    private final Uri instantiatesUri;
    @Summary
    private final List<Reference> basedOn;
    @Summary
    private final Identifier groupIdentifier;
    @Summary
    @ReferenceTarget({ "Transport" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "TransportStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Status of the transport.",
        valueSet = "http://hl7.org/fhir/ValueSet/transport-status|5.0.0"
    )
    private final TransportStatus status;
    @Summary
    @Binding(
        bindingName = "TransportStatusReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes to identify the reason for current status.  These will typically be specific to a particular workflow.",
        valueSet = "http://hl7.org/fhir/ValueSet/transport-status-reason"
    )
    private final CodeableConcept statusReason;
    @Summary
    @Binding(
        bindingName = "TransportIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Distinguishes whether the transport is a proposal, plan or full order.",
        valueSet = "http://hl7.org/fhir/ValueSet/transport-intent|5.0.0"
    )
    @Required
    private final TransportIntent intent;
    @Binding(
        bindingName = "TransportPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "The priority of a transport (may affect service level applied to the transport).",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final TransportPriority priority;
    @Summary
    @Binding(
        bindingName = "TransportCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes to identify what the transport involves.  These will typically be specific to a particular workflow.",
        valueSet = "http://hl7.org/fhir/ValueSet/transport-code"
    )
    private final CodeableConcept code;
    @Summary
    private final String description;
    @Summary
    private final Reference focus;
    @Summary
    private final Reference _for;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    private final DateTime completionTime;
    private final DateTime authoredOn;
    @Summary
    private final DateTime lastModified;
    @Summary
    @ReferenceTarget({ "Device", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
    private final Reference requester;
    @Binding(
        bindingName = "TransportPerformerType",
        strength = BindingStrength.Value.PREFERRED,
        description = "The type(s) of transport performers allowed.",
        valueSet = "http://hl7.org/fhir/ValueSet/performer-role"
    )
    private final List<CodeableConcept> performerType;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "HealthcareService", "Patient", "Device", "RelatedPerson" })
    private final Reference owner;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @ReferenceTarget({ "Coverage", "ClaimResponse" })
    private final List<Reference> insurance;
    private final List<Annotation> note;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> relevantHistory;
    private final Restriction restriction;
    private final List<Input> input;
    private final List<Output> output;
    @Summary
    @ReferenceTarget({ "Location" })
    @Required
    private final Reference requestedLocation;
    @Summary
    @ReferenceTarget({ "Location" })
    @Required
    private final Reference currentLocation;
    @Binding(
        bindingName = "TransportReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Indicates why the transport is needed.  E.g. Suspended because patient admitted to hospital."
    )
    private final CodeableReference reason;
    @ReferenceTarget({ "Transport" })
    private final Reference history;

    private Transport(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = builder.instantiatesCanonical;
        instantiatesUri = builder.instantiatesUri;
        basedOn = Collections.unmodifiableList(builder.basedOn);
        groupIdentifier = builder.groupIdentifier;
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        statusReason = builder.statusReason;
        intent = builder.intent;
        priority = builder.priority;
        code = builder.code;
        description = builder.description;
        focus = builder.focus;
        _for = builder._for;
        encounter = builder.encounter;
        completionTime = builder.completionTime;
        authoredOn = builder.authoredOn;
        lastModified = builder.lastModified;
        requester = builder.requester;
        performerType = Collections.unmodifiableList(builder.performerType);
        owner = builder.owner;
        location = builder.location;
        insurance = Collections.unmodifiableList(builder.insurance);
        note = Collections.unmodifiableList(builder.note);
        relevantHistory = Collections.unmodifiableList(builder.relevantHistory);
        restriction = builder.restriction;
        input = Collections.unmodifiableList(builder.input);
        output = Collections.unmodifiableList(builder.output);
        requestedLocation = builder.requestedLocation;
        currentLocation = builder.currentLocation;
        reason = builder.reason;
        history = builder.history;
    }

    /**
     * Identifier for the transport event that is used to identify it across multiple disparate systems.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a *FHIR*-defined protocol, guideline, orderset or other definition that is adhered to in whole or 
     * in part by this Transport.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an *externally* maintained protocol, guideline, orderset or other definition that is adhered to in 
     * whole or in part by this Transport.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * BasedOn refers to a higher-level authorization that triggered the creation of the transport. It references a "request" 
     * resource such as a ServiceRequest or Transport, which is distinct from the "request" resource the Transport is seeking 
     * to fulfill. This latter resource is referenced by FocusOn. For example, based on a ServiceRequest (= BasedOn), a 
     * transport is created to fulfill a procedureRequest ( = FocusOn ) to transport a specimen to the lab.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
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
     * A larger event of which this particular event is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code specifying the state of the transport event.
     * 
     * @return
     *     An immutable object of type {@link TransportStatus} that may be null.
     */
    public TransportStatus getStatus() {
        return status;
    }

    /**
     * An explanation as to why this transport is held, failed, was refused, etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * Indicates the "level" of actionability associated with the Transport, i.e. i+R[9]Cs this a proposed transport, a 
     * planned transport, an actionable transport, etc.
     * 
     * @return
     *     An immutable object of type {@link TransportIntent} that is non-null.
     */
    public TransportIntent getIntent() {
        return intent;
    }

    /**
     * Indicates how quickly the Transport should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link TransportPriority} that may be null.
     */
    public TransportPriority getPriority() {
        return priority;
    }

    /**
     * A name or code (or both) briefly describing what the transport involves.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * A free-text description of what is to be performed.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The request being actioned or the resource being manipulated by this transport.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getFocus() {
        return focus;
    }

    /**
     * The entity who benefits from the performance of the service specified in the transport (e.g., the patient).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getFor() {
        return _for;
    }

    /**
     * The healthcare event (e.g. a patient and healthcare provider interaction) during which this transport was created.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Identifies the completion time of the event (the occurrence).
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getCompletionTime() {
        return completionTime;
    }

    /**
     * The date and time this transport was created.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * The date and time of last modification to this transport.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getLastModified() {
        return lastModified;
    }

    /**
     * The creator of the transport.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequester() {
        return requester;
    }

    /**
     * The kind of participant that should perform the transport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getPerformerType() {
        return performerType;
    }

    /**
     * Individual organization or Device currently responsible for transport execution.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOwner() {
        return owner;
    }

    /**
     * Principal physical location where this transport is performed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be relevant to the 
     * Transport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInsurance() {
        return insurance;
    }

    /**
     * Free-text information captured about the transport as it progresses.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Links to Provenance records for past versions of this Transport that identify key state transitions or updates that 
     * are likely to be relevant to a user looking at the current version of the transport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRelevantHistory() {
        return relevantHistory;
    }

    /**
     * If the Transport.focus is a request resource and the transport is seeking fulfillment (i.e. is asking for the request 
     * to be actioned), this element identifies any limitations on what parts of the referenced request should be actioned.
     * 
     * @return
     *     An immutable object of type {@link Restriction} that may be null.
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Additional information that may be needed in the execution of the transport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Input} that may be empty.
     */
    public List<Input> getInput() {
        return input;
    }

    /**
     * Outputs produced by the Transport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Output} that may be empty.
     */
    public List<Output> getOutput() {
        return output;
    }

    /**
     * The desired or final location for the transport.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getRequestedLocation() {
        return requestedLocation;
    }

    /**
     * The current location for the entity to be transported.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getCurrentLocation() {
        return currentLocation;
    }

    /**
     * A resource reference indicating why this transport needs to be performed.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getReason() {
        return reason;
    }

    /**
     * The transport event prior to this one.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getHistory() {
        return history;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (instantiatesCanonical != null) || 
            (instantiatesUri != null) || 
            !basedOn.isEmpty() || 
            (groupIdentifier != null) || 
            !partOf.isEmpty() || 
            (status != null) || 
            (statusReason != null) || 
            (intent != null) || 
            (priority != null) || 
            (code != null) || 
            (description != null) || 
            (focus != null) || 
            (_for != null) || 
            (encounter != null) || 
            (completionTime != null) || 
            (authoredOn != null) || 
            (lastModified != null) || 
            (requester != null) || 
            !performerType.isEmpty() || 
            (owner != null) || 
            (location != null) || 
            !insurance.isEmpty() || 
            !note.isEmpty() || 
            !relevantHistory.isEmpty() || 
            (restriction != null) || 
            !input.isEmpty() || 
            !output.isEmpty() || 
            (requestedLocation != null) || 
            (currentLocation != null) || 
            (reason != null) || 
            (history != null);
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
                accept(instantiatesCanonical, "instantiatesCanonical", visitor);
                accept(instantiatesUri, "instantiatesUri", visitor);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(groupIdentifier, "groupIdentifier", visitor);
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor);
                accept(intent, "intent", visitor);
                accept(priority, "priority", visitor);
                accept(code, "code", visitor);
                accept(description, "description", visitor);
                accept(focus, "focus", visitor);
                accept(_for, "for", visitor);
                accept(encounter, "encounter", visitor);
                accept(completionTime, "completionTime", visitor);
                accept(authoredOn, "authoredOn", visitor);
                accept(lastModified, "lastModified", visitor);
                accept(requester, "requester", visitor);
                accept(performerType, "performerType", visitor, CodeableConcept.class);
                accept(owner, "owner", visitor);
                accept(location, "location", visitor);
                accept(insurance, "insurance", visitor, Reference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(relevantHistory, "relevantHistory", visitor, Reference.class);
                accept(restriction, "restriction", visitor);
                accept(input, "input", visitor, Input.class);
                accept(output, "output", visitor, Output.class);
                accept(requestedLocation, "requestedLocation", visitor);
                accept(currentLocation, "currentLocation", visitor);
                accept(reason, "reason", visitor);
                accept(history, "history", visitor);
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
        Transport other = (Transport) obj;
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
            Objects.equals(groupIdentifier, other.groupIdentifier) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(code, other.code) && 
            Objects.equals(description, other.description) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(_for, other._for) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(completionTime, other.completionTime) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(lastModified, other.lastModified) && 
            Objects.equals(requester, other.requester) && 
            Objects.equals(performerType, other.performerType) && 
            Objects.equals(owner, other.owner) && 
            Objects.equals(location, other.location) && 
            Objects.equals(insurance, other.insurance) && 
            Objects.equals(note, other.note) && 
            Objects.equals(relevantHistory, other.relevantHistory) && 
            Objects.equals(restriction, other.restriction) && 
            Objects.equals(input, other.input) && 
            Objects.equals(output, other.output) && 
            Objects.equals(requestedLocation, other.requestedLocation) && 
            Objects.equals(currentLocation, other.currentLocation) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(history, other.history);
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
                groupIdentifier, 
                partOf, 
                status, 
                statusReason, 
                intent, 
                priority, 
                code, 
                description, 
                focus, 
                _for, 
                encounter, 
                completionTime, 
                authoredOn, 
                lastModified, 
                requester, 
                performerType, 
                owner, 
                location, 
                insurance, 
                note, 
                relevantHistory, 
                restriction, 
                input, 
                output, 
                requestedLocation, 
                currentLocation, 
                reason, 
                history);
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
        private Canonical instantiatesCanonical;
        private Uri instantiatesUri;
        private List<Reference> basedOn = new ArrayList<>();
        private Identifier groupIdentifier;
        private List<Reference> partOf = new ArrayList<>();
        private TransportStatus status;
        private CodeableConcept statusReason;
        private TransportIntent intent;
        private TransportPriority priority;
        private CodeableConcept code;
        private String description;
        private Reference focus;
        private Reference _for;
        private Reference encounter;
        private DateTime completionTime;
        private DateTime authoredOn;
        private DateTime lastModified;
        private Reference requester;
        private List<CodeableConcept> performerType = new ArrayList<>();
        private Reference owner;
        private Reference location;
        private List<Reference> insurance = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<Reference> relevantHistory = new ArrayList<>();
        private Restriction restriction;
        private List<Input> input = new ArrayList<>();
        private List<Output> output = new ArrayList<>();
        private Reference requestedLocation;
        private Reference currentLocation;
        private CodeableReference reason;
        private Reference history;

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
         * Identifier for the transport event that is used to identify it across multiple disparate systems.
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
         * Identifier for the transport event that is used to identify it across multiple disparate systems.
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
         * The URL pointing to a *FHIR*-defined protocol, guideline, orderset or other definition that is adhered to in whole or 
         * in part by this Transport.
         * 
         * @param instantiatesCanonical
         *     Formal definition of transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesCanonical(Canonical instantiatesCanonical) {
            this.instantiatesCanonical = instantiatesCanonical;
            return this;
        }

        /**
         * The URL pointing to an *externally* maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this Transport.
         * 
         * @param instantiatesUri
         *     Formal definition of transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesUri(Uri instantiatesUri) {
            this.instantiatesUri = instantiatesUri;
            return this;
        }

        /**
         * BasedOn refers to a higher-level authorization that triggered the creation of the transport. It references a "request" 
         * resource such as a ServiceRequest or Transport, which is distinct from the "request" resource the Transport is seeking 
         * to fulfill. This latter resource is referenced by FocusOn. For example, based on a ServiceRequest (= BasedOn), a 
         * transport is created to fulfill a procedureRequest ( = FocusOn ) to transport a specimen to the lab.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     Request fulfilled by this transport
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
         * BasedOn refers to a higher-level authorization that triggered the creation of the transport. It references a "request" 
         * resource such as a ServiceRequest or Transport, which is distinct from the "request" resource the Transport is seeking 
         * to fulfill. This latter resource is referenced by FocusOn. For example, based on a ServiceRequest (= BasedOn), a 
         * transport is created to fulfill a procedureRequest ( = FocusOn ) to transport a specimen to the lab.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     Request fulfilled by this transport
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
         * A shared identifier common to multiple independent Request instances that were activated/authorized more or less 
         * simultaneously by a single author. The presence of the same identifier on each request ties those requests together 
         * and may have business ramifications in terms of reporting of results, billing, etc. E.g. a requisition number shared 
         * by a set of lab tests ordered together, or a prescription number shared by all meds ordered at one time.
         * 
         * @param groupIdentifier
         *     Requisition or grouper id
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder groupIdentifier(Identifier groupIdentifier) {
            this.groupIdentifier = groupIdentifier;
            return this;
        }

        /**
         * A larger event of which this particular event is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Transport}</li>
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
         * A larger event of which this particular event is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Transport}</li>
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
         * A code specifying the state of the transport event.
         * 
         * @param status
         *     in-progress | completed | abandoned | cancelled | planned | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(TransportStatus status) {
            this.status = status;
            return this;
        }

        /**
         * An explanation as to why this transport is held, failed, was refused, etc.
         * 
         * @param statusReason
         *     Reason for current status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusReason(CodeableConcept statusReason) {
            this.statusReason = statusReason;
            return this;
        }

        /**
         * Indicates the "level" of actionability associated with the Transport, i.e. i+R[9]Cs this a proposed transport, a 
         * planned transport, an actionable transport, etc.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     unknown | proposal | plan | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(TransportIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Indicates how quickly the Transport should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(TransportPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * A name or code (or both) briefly describing what the transport involves.
         * 
         * @param code
         *     Transport Type
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * Convenience method for setting {@code description}.
         * 
         * @param description
         *     Human-readable explanation of transport
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #description(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder description(java.lang.String description) {
            this.description = (description == null) ? null : String.of(description);
            return this;
        }

        /**
         * A free-text description of what is to be performed.
         * 
         * @param description
         *     Human-readable explanation of transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * The request being actioned or the resource being manipulated by this transport.
         * 
         * @param focus
         *     What transport is acting on
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focus(Reference focus) {
            this.focus = focus;
            return this;
        }

        /**
         * The entity who benefits from the performance of the service specified in the transport (e.g., the patient).
         * 
         * @param _for
         *     Beneficiary of the Transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder _for(Reference _for) {
            this._for = _for;
            return this;
        }

        /**
         * The healthcare event (e.g. a patient and healthcare provider interaction) during which this transport was created.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Healthcare event during which this transport originated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Identifies the completion time of the event (the occurrence).
         * 
         * @param completionTime
         *     Completion time of the event (the occurrence)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder completionTime(DateTime completionTime) {
            this.completionTime = completionTime;
            return this;
        }

        /**
         * The date and time this transport was created.
         * 
         * @param authoredOn
         *     Transport Creation Date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * The date and time of last modification to this transport.
         * 
         * @param lastModified
         *     Transport Last Modified Date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastModified(DateTime lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        /**
         * The creator of the transport.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param requester
         *     Who is asking for transport to be done
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requester(Reference requester) {
            this.requester = requester;
            return this;
        }

        /**
         * The kind of participant that should perform the transport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performerType
         *     Requested performer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performerType(CodeableConcept... performerType) {
            for (CodeableConcept value : performerType) {
                this.performerType.add(value);
            }
            return this;
        }

        /**
         * The kind of participant that should perform the transport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performerType
         *     Requested performer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder performerType(Collection<CodeableConcept> performerType) {
            this.performerType = new ArrayList<>(performerType);
            return this;
        }

        /**
         * Individual organization or Device currently responsible for transport execution.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Device}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param owner
         *     Responsible individual
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder owner(Reference owner) {
            this.owner = owner;
            return this;
        }

        /**
         * Principal physical location where this transport is performed.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where transport occurs
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be relevant to the 
         * Transport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Coverage}</li>
         * <li>{@link ClaimResponse}</li>
         * </ul>
         * 
         * @param insurance
         *     Associated insurance coverage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurance(Reference... insurance) {
            for (Reference value : insurance) {
                this.insurance.add(value);
            }
            return this;
        }

        /**
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be relevant to the 
         * Transport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Coverage}</li>
         * <li>{@link ClaimResponse}</li>
         * </ul>
         * 
         * @param insurance
         *     Associated insurance coverage
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder insurance(Collection<Reference> insurance) {
            this.insurance = new ArrayList<>(insurance);
            return this;
        }

        /**
         * Free-text information captured about the transport as it progresses.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments made about the transport
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
         * Free-text information captured about the transport as it progresses.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments made about the transport
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
         * Links to Provenance records for past versions of this Transport that identify key state transitions or updates that 
         * are likely to be relevant to a user looking at the current version of the transport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param relevantHistory
         *     Key events in history of the Transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relevantHistory(Reference... relevantHistory) {
            for (Reference value : relevantHistory) {
                this.relevantHistory.add(value);
            }
            return this;
        }

        /**
         * Links to Provenance records for past versions of this Transport that identify key state transitions or updates that 
         * are likely to be relevant to a user looking at the current version of the transport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param relevantHistory
         *     Key events in history of the Transport
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relevantHistory(Collection<Reference> relevantHistory) {
            this.relevantHistory = new ArrayList<>(relevantHistory);
            return this;
        }

        /**
         * If the Transport.focus is a request resource and the transport is seeking fulfillment (i.e. is asking for the request 
         * to be actioned), this element identifies any limitations on what parts of the referenced request should be actioned.
         * 
         * @param restriction
         *     Constraints on fulfillment transports
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder restriction(Restriction restriction) {
            this.restriction = restriction;
            return this;
        }

        /**
         * Additional information that may be needed in the execution of the transport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param input
         *     Information used to perform transport
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
         * Additional information that may be needed in the execution of the transport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param input
         *     Information used to perform transport
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
         * Outputs produced by the Transport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param output
         *     Information produced as part of transport
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
         * Outputs produced by the Transport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param output
         *     Information produced as part of transport
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
         * The desired or final location for the transport.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param requestedLocation
         *     The desired location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requestedLocation(Reference requestedLocation) {
            this.requestedLocation = requestedLocation;
            return this;
        }

        /**
         * The current location for the entity to be transported.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param currentLocation
         *     The entity current location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder currentLocation(Reference currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        /**
         * A resource reference indicating why this transport needs to be performed.
         * 
         * @param reason
         *     Why transport is needed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(CodeableReference reason) {
            this.reason = reason;
            return this;
        }

        /**
         * The transport event prior to this one.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Transport}</li>
         * </ul>
         * 
         * @param history
         *     Parent (or preceding) transport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder history(Reference history) {
            this.history = history;
            return this;
        }

        /**
         * Build the {@link Transport}
         * 
         * <p>Required elements:
         * <ul>
         * <li>intent</li>
         * <li>requestedLocation</li>
         * <li>currentLocation</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Transport}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Transport per the base specification
         */
        @Override
        public Transport build() {
            Transport transport = new Transport(this);
            if (validating) {
                validate(transport);
            }
            return transport;
        }

        protected void validate(Transport transport) {
            super.validate(transport);
            ValidationSupport.checkList(transport.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(transport.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(transport.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(transport.intent, "intent");
            ValidationSupport.checkList(transport.performerType, "performerType", CodeableConcept.class);
            ValidationSupport.checkList(transport.insurance, "insurance", Reference.class);
            ValidationSupport.checkList(transport.note, "note", Annotation.class);
            ValidationSupport.checkList(transport.relevantHistory, "relevantHistory", Reference.class);
            ValidationSupport.checkList(transport.input, "input", Input.class);
            ValidationSupport.checkList(transport.output, "output", Output.class);
            ValidationSupport.requireNonNull(transport.requestedLocation, "requestedLocation");
            ValidationSupport.requireNonNull(transport.currentLocation, "currentLocation");
            ValidationSupport.checkReferenceType(transport.partOf, "partOf", "Transport");
            ValidationSupport.checkReferenceType(transport.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(transport.requester, "requester", "Device", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
            ValidationSupport.checkReferenceType(transport.owner, "owner", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "HealthcareService", "Patient", "Device", "RelatedPerson");
            ValidationSupport.checkReferenceType(transport.location, "location", "Location");
            ValidationSupport.checkReferenceType(transport.insurance, "insurance", "Coverage", "ClaimResponse");
            ValidationSupport.checkReferenceType(transport.relevantHistory, "relevantHistory", "Provenance");
            ValidationSupport.checkReferenceType(transport.requestedLocation, "requestedLocation", "Location");
            ValidationSupport.checkReferenceType(transport.currentLocation, "currentLocation", "Location");
            ValidationSupport.checkReferenceType(transport.history, "history", "Transport");
        }

        protected Builder from(Transport transport) {
            super.from(transport);
            identifier.addAll(transport.identifier);
            instantiatesCanonical = transport.instantiatesCanonical;
            instantiatesUri = transport.instantiatesUri;
            basedOn.addAll(transport.basedOn);
            groupIdentifier = transport.groupIdentifier;
            partOf.addAll(transport.partOf);
            status = transport.status;
            statusReason = transport.statusReason;
            intent = transport.intent;
            priority = transport.priority;
            code = transport.code;
            description = transport.description;
            focus = transport.focus;
            _for = transport._for;
            encounter = transport.encounter;
            completionTime = transport.completionTime;
            authoredOn = transport.authoredOn;
            lastModified = transport.lastModified;
            requester = transport.requester;
            performerType.addAll(transport.performerType);
            owner = transport.owner;
            location = transport.location;
            insurance.addAll(transport.insurance);
            note.addAll(transport.note);
            relevantHistory.addAll(transport.relevantHistory);
            restriction = transport.restriction;
            input.addAll(transport.input);
            output.addAll(transport.output);
            requestedLocation = transport.requestedLocation;
            currentLocation = transport.currentLocation;
            reason = transport.reason;
            history = transport.history;
            return this;
        }
    }

    /**
     * If the Transport.focus is a request resource and the transport is seeking fulfillment (i.e. is asking for the request 
     * to be actioned), this element identifies any limitations on what parts of the referenced request should be actioned.
     */
    public static class Restriction extends BackboneElement {
        private final PositiveInt repetitions;
        private final Period period;
        @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Group", "Organization" })
        private final List<Reference> recipient;

        private Restriction(Builder builder) {
            super(builder);
            repetitions = builder.repetitions;
            period = builder.period;
            recipient = Collections.unmodifiableList(builder.recipient);
        }

        /**
         * Indicates the number of times the requested action should occur.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getRepetitions() {
            return repetitions;
        }

        /**
         * Over what time-period is fulfillment sought.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        /**
         * For requests that are targeted to more than one potential recipient/target, to identify who is fulfillment is sought 
         * for.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getRecipient() {
            return recipient;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (repetitions != null) || 
                (period != null) || 
                !recipient.isEmpty();
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
                    accept(repetitions, "repetitions", visitor);
                    accept(period, "period", visitor);
                    accept(recipient, "recipient", visitor, Reference.class);
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
            Restriction other = (Restriction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(repetitions, other.repetitions) && 
                Objects.equals(period, other.period) && 
                Objects.equals(recipient, other.recipient);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    repetitions, 
                    period, 
                    recipient);
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
            private PositiveInt repetitions;
            private Period period;
            private List<Reference> recipient = new ArrayList<>();

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
             * Indicates the number of times the requested action should occur.
             * 
             * @param repetitions
             *     How many times to repeat
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder repetitions(PositiveInt repetitions) {
                this.repetitions = repetitions;
                return this;
            }

            /**
             * Over what time-period is fulfillment sought.
             * 
             * @param period
             *     When fulfillment sought
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * For requests that are targeted to more than one potential recipient/target, to identify who is fulfillment is sought 
             * for.
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
             * <li>{@link Group}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param recipient
             *     For whom is fulfillment sought?
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder recipient(Reference... recipient) {
                for (Reference value : recipient) {
                    this.recipient.add(value);
                }
                return this;
            }

            /**
             * For requests that are targeted to more than one potential recipient/target, to identify who is fulfillment is sought 
             * for.
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
             * <li>{@link Group}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param recipient
             *     For whom is fulfillment sought?
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder recipient(Collection<Reference> recipient) {
                this.recipient = new ArrayList<>(recipient);
                return this;
            }

            /**
             * Build the {@link Restriction}
             * 
             * @return
             *     An immutable object of type {@link Restriction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Restriction per the base specification
             */
            @Override
            public Restriction build() {
                Restriction restriction = new Restriction(this);
                if (validating) {
                    validate(restriction);
                }
                return restriction;
            }

            protected void validate(Restriction restriction) {
                super.validate(restriction);
                ValidationSupport.checkList(restriction.recipient, "recipient", Reference.class);
                ValidationSupport.checkReferenceType(restriction.recipient, "recipient", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Group", "Organization");
                ValidationSupport.requireValueOrChildren(restriction);
            }

            protected Builder from(Restriction restriction) {
                super.from(restriction);
                repetitions = restriction.repetitions;
                period = restriction.period;
                recipient.addAll(restriction.recipient);
                return this;
            }
        }
    }

    /**
     * Additional information that may be needed in the execution of the transport.
     */
    public static class Input extends BackboneElement {
        @Binding(
            bindingName = "TransportInputParameterType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes to identify types of input parameters.  These will typically be specific to a particular workflow.  E.g. \"Comparison source\", \"Applicable consent\", \"Concomitent Medications\", etc."
        )
        @Required
        private final CodeableConcept type;
        @Choice({ Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class })
        @Required
        private final Element value;

        private Input(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * A code or description indicating how the input is intended to be used as part of the transport execution.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The value of the input parameter as a basic type.
         * 
         * @return
         *     An immutable object of type {@link Base64Binary}, {@link Boolean}, {@link Canonical}, {@link Code}, {@link Date}, 
         *     {@link DateTime}, {@link Decimal}, {@link Id}, {@link Instant}, {@link Integer}, {@link Integer64}, {@link Markdown}, 
         *     {@link Oid}, {@link PositiveInt}, {@link String}, {@link Time}, {@link UnsignedInt}, {@link Uri}, {@link Url}, {@link 
         *     Uuid}, {@link Address}, {@link Age}, {@link Annotation}, {@link Attachment}, {@link CodeableConcept}, {@link 
         *     CodeableReference}, {@link Coding}, {@link ContactPoint}, {@link Count}, {@link Distance}, {@link Duration}, {@link 
         *     HumanName}, {@link Identifier}, {@link Money}, {@link Period}, {@link Quantity}, {@link Range}, {@link Ratio}, {@link 
         *     RatioRange}, {@link Reference}, {@link SampledData}, {@link Signature}, {@link Timing}, {@link ContactDetail}, {@link 
         *     DataRequirement}, {@link Expression}, {@link ParameterDefinition}, {@link RelatedArtifact}, {@link TriggerDefinition}, 
         *     {@link UsageContext}, {@link Availability}, {@link ExtendedContactDetail}, {@link Dosage} or {@link Meta} that is non-
         *     null.
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
            Input other = (Input) obj;
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
             * A code or description indicating how the input is intended to be used as part of the transport execution.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Label for the input
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
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
             * Convenience method for setting {@code value} with choice type Date.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.LocalDate value) {
                this.value = (value == null) ? null : Date.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Instant.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.ZonedDateTime value) {
                this.value = (value == null) ? null : Instant.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
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
             * Convenience method for setting {@code value} with choice type String.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
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
             * Convenience method for setting {@code value} with choice type Time.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Content to use in performing the transport
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
             * The value of the input parameter as a basic type.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Base64Binary}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Canonical}</li>
             * <li>{@link Code}</li>
             * <li>{@link Date}</li>
             * <li>{@link DateTime}</li>
             * <li>{@link Decimal}</li>
             * <li>{@link Id}</li>
             * <li>{@link Instant}</li>
             * <li>{@link Integer}</li>
             * <li>{@link Integer64}</li>
             * <li>{@link Markdown}</li>
             * <li>{@link Oid}</li>
             * <li>{@link PositiveInt}</li>
             * <li>{@link String}</li>
             * <li>{@link Time}</li>
             * <li>{@link UnsignedInt}</li>
             * <li>{@link Uri}</li>
             * <li>{@link Url}</li>
             * <li>{@link Uuid}</li>
             * <li>{@link Address}</li>
             * <li>{@link Age}</li>
             * <li>{@link Annotation}</li>
             * <li>{@link Attachment}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link CodeableReference}</li>
             * <li>{@link Coding}</li>
             * <li>{@link ContactPoint}</li>
             * <li>{@link Count}</li>
             * <li>{@link Distance}</li>
             * <li>{@link Duration}</li>
             * <li>{@link HumanName}</li>
             * <li>{@link Identifier}</li>
             * <li>{@link Money}</li>
             * <li>{@link Period}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Ratio}</li>
             * <li>{@link RatioRange}</li>
             * <li>{@link Reference}</li>
             * <li>{@link SampledData}</li>
             * <li>{@link Signature}</li>
             * <li>{@link Timing}</li>
             * <li>{@link ContactDetail}</li>
             * <li>{@link DataRequirement}</li>
             * <li>{@link Expression}</li>
             * <li>{@link ParameterDefinition}</li>
             * <li>{@link RelatedArtifact}</li>
             * <li>{@link TriggerDefinition}</li>
             * <li>{@link UsageContext}</li>
             * <li>{@link Availability}</li>
             * <li>{@link ExtendedContactDetail}</li>
             * <li>{@link Dosage}</li>
             * <li>{@link Meta}</li>
             * </ul>
             * 
             * @param value
             *     Content to use in performing the transport
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Input}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>value</li>
             * </ul>
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
                ValidationSupport.requireNonNull(input.type, "type");
                ValidationSupport.requireChoiceElement(input.value, "value", Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class);
                ValidationSupport.requireValueOrChildren(input);
            }

            protected Builder from(Input input) {
                super.from(input);
                type = input.type;
                value = input.value;
                return this;
            }
        }
    }

    /**
     * Outputs produced by the Transport.
     */
    public static class Output extends BackboneElement {
        @Binding(
            bindingName = "TransportOutputParameterType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes to identify types of input parameters.  These will typically be specific to a particular workflow.  E.g. \"Identified issues\", \"Preliminary results\", \"Filler order\", \"Final results\", etc."
        )
        @Required
        private final CodeableConcept type;
        @Choice({ Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class })
        @Required
        private final Element value;

        private Output(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * The name of the Output parameter.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The value of the Output parameter as a basic type.
         * 
         * @return
         *     An immutable object of type {@link Base64Binary}, {@link Boolean}, {@link Canonical}, {@link Code}, {@link Date}, 
         *     {@link DateTime}, {@link Decimal}, {@link Id}, {@link Instant}, {@link Integer}, {@link Integer64}, {@link Markdown}, 
         *     {@link Oid}, {@link PositiveInt}, {@link String}, {@link Time}, {@link UnsignedInt}, {@link Uri}, {@link Url}, {@link 
         *     Uuid}, {@link Address}, {@link Age}, {@link Annotation}, {@link Attachment}, {@link CodeableConcept}, {@link 
         *     CodeableReference}, {@link Coding}, {@link ContactPoint}, {@link Count}, {@link Distance}, {@link Duration}, {@link 
         *     HumanName}, {@link Identifier}, {@link Money}, {@link Period}, {@link Quantity}, {@link Range}, {@link Ratio}, {@link 
         *     RatioRange}, {@link Reference}, {@link SampledData}, {@link Signature}, {@link Timing}, {@link ContactDetail}, {@link 
         *     DataRequirement}, {@link Expression}, {@link ParameterDefinition}, {@link RelatedArtifact}, {@link TriggerDefinition}, 
         *     {@link UsageContext}, {@link Availability}, {@link ExtendedContactDetail}, {@link Dosage} or {@link Meta} that is non-
         *     null.
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
            Output other = (Output) obj;
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
             * The name of the Output parameter.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Label for output
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
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
             * Convenience method for setting {@code value} with choice type Date.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.LocalDate value) {
                this.value = (value == null) ? null : Date.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Instant.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.ZonedDateTime value) {
                this.value = (value == null) ? null : Instant.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
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
             * Convenience method for setting {@code value} with choice type String.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
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
             * Convenience method for setting {@code value} with choice type Time.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Result of output
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
             * The value of the Output parameter as a basic type.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Base64Binary}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Canonical}</li>
             * <li>{@link Code}</li>
             * <li>{@link Date}</li>
             * <li>{@link DateTime}</li>
             * <li>{@link Decimal}</li>
             * <li>{@link Id}</li>
             * <li>{@link Instant}</li>
             * <li>{@link Integer}</li>
             * <li>{@link Integer64}</li>
             * <li>{@link Markdown}</li>
             * <li>{@link Oid}</li>
             * <li>{@link PositiveInt}</li>
             * <li>{@link String}</li>
             * <li>{@link Time}</li>
             * <li>{@link UnsignedInt}</li>
             * <li>{@link Uri}</li>
             * <li>{@link Url}</li>
             * <li>{@link Uuid}</li>
             * <li>{@link Address}</li>
             * <li>{@link Age}</li>
             * <li>{@link Annotation}</li>
             * <li>{@link Attachment}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link CodeableReference}</li>
             * <li>{@link Coding}</li>
             * <li>{@link ContactPoint}</li>
             * <li>{@link Count}</li>
             * <li>{@link Distance}</li>
             * <li>{@link Duration}</li>
             * <li>{@link HumanName}</li>
             * <li>{@link Identifier}</li>
             * <li>{@link Money}</li>
             * <li>{@link Period}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Ratio}</li>
             * <li>{@link RatioRange}</li>
             * <li>{@link Reference}</li>
             * <li>{@link SampledData}</li>
             * <li>{@link Signature}</li>
             * <li>{@link Timing}</li>
             * <li>{@link ContactDetail}</li>
             * <li>{@link DataRequirement}</li>
             * <li>{@link Expression}</li>
             * <li>{@link ParameterDefinition}</li>
             * <li>{@link RelatedArtifact}</li>
             * <li>{@link TriggerDefinition}</li>
             * <li>{@link UsageContext}</li>
             * <li>{@link Availability}</li>
             * <li>{@link ExtendedContactDetail}</li>
             * <li>{@link Dosage}</li>
             * <li>{@link Meta}</li>
             * </ul>
             * 
             * @param value
             *     Result of output
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Output}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>value</li>
             * </ul>
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
                ValidationSupport.requireNonNull(output.type, "type");
                ValidationSupport.requireChoiceElement(output.value, "value", Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class);
                ValidationSupport.requireValueOrChildren(output);
            }

            protected Builder from(Output output) {
                super.from(output);
                type = output.type;
                value = output.value;
                return this;
            }
        }
    }
}
