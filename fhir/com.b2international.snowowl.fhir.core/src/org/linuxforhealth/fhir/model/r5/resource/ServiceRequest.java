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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ServiceRequestIntent;
import org.linuxforhealth.fhir.model.r5.type.code.ServiceRequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.ServiceRequestStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of a request for service such as diagnostic investigations, treatments, or operations to be performed.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "bdystr-1",
    level = "Rule",
    location = "(base)",
    description = "bodyStructure SHALL only be present if bodySite is not present",
    expression = "bodySite.exists() implies bodyStructure.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/ServiceRequest"
)
@Constraint(
    id = "prr-1",
    level = "Rule",
    location = "(base)",
    description = "orderDetail SHALL only be present if code is present",
    expression = "orderDetail.empty() or code.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ServiceRequest"
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ServiceRequest extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    @Summary
    @ReferenceTarget({ "CarePlan", "ServiceRequest", "MedicationRequest" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> replaces;
    @Summary
    private final Identifier requisition;
    @Summary
    @Binding(
        bindingName = "ServiceRequestStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of a service order.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-status|5.0.0"
    )
    @Required
    private final ServiceRequestStatus status;
    @Summary
    @Binding(
        bindingName = "ServiceRequestIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "The kind of service request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-intent|5.0.0"
    )
    @Required
    private final ServiceRequestIntent intent;
    @Summary
    @Binding(
        bindingName = "ServiceRequestCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Classification of the requested service.",
        valueSet = "http://hl7.org/fhir/ValueSet/servicerequest-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "ServiceRequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final ServiceRequestPriority priority;
    @Summary
    private final Boolean doNotPerform;
    @Summary
    @Binding(
        bindingName = "ServiceRequestCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes for tests or services that can be carried out by a designated individual, organization or healthcare service.  For laboratory, LOINC is  [preferred](terminologies.html#preferred) and a valueset using LOINC Order codes is available [here](valueset-diagnostic-requests.html).",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-code"
    )
    private final CodeableReference code;
    @Summary
    private final List<OrderDetail> orderDetail;
    @Summary
    @Choice({ Quantity.class, Ratio.class, Range.class })
    private final Element quantity;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Location", "Device" })
    @Required
    private final Reference subject;
    @Summary
    private final List<Reference> focus;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    private final Element occurrence;
    @Summary
    @Choice({ Boolean.class, CodeableConcept.class })
    @Binding(
        bindingName = "ProcedureAsNeededReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying the pre-condition that should hold prior to performing a procedure.  For example \"pain\", \"on flare-up\", etc.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-as-needed-reason"
    )
    private final Element asNeeded;
    @Summary
    private final DateTime authoredOn;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device" })
    private final Reference requester;
    @Summary
    @Binding(
        bindingName = "ServiceRequestParticipantRole",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Indicates specific responsibility of an individual within the care team, such as \"Primary physician\", \"Team coordinator\", \"Caregiver\", etc.",
        valueSet = "http://hl7.org/fhir/ValueSet/participant-role"
    )
    private final CodeableConcept performerType;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "HealthcareService", "Patient", "Device", "RelatedPerson" })
    private final List<Reference> performer;
    @Summary
    @Binding(
        bindingName = "ServiceRequestLocation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A location type where services are delivered.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ServiceDeliveryLocationRoleType"
    )
    private final List<CodeableReference> location;
    @Summary
    @Binding(
        bindingName = "ServiceRequestReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Condition/Problem/Diagnosis Codes",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-reason"
    )
    private final List<CodeableReference> reason;
    @ReferenceTarget({ "Coverage", "ClaimResponse" })
    private final List<Reference> insurance;
    private final List<CodeableReference> supportingInfo;
    @Summary
    @ReferenceTarget({ "Specimen" })
    private final List<Reference> specimen;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final List<CodeableConcept> bodySite;
    @Summary
    @ReferenceTarget({ "BodyStructure" })
    private final Reference bodyStructure;
    private final List<Annotation> note;
    private final List<PatientInstruction> patientInstruction;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> relevantHistory;

    private ServiceRequest(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        replaces = Collections.unmodifiableList(builder.replaces);
        requisition = builder.requisition;
        status = builder.status;
        intent = builder.intent;
        category = Collections.unmodifiableList(builder.category);
        priority = builder.priority;
        doNotPerform = builder.doNotPerform;
        code = builder.code;
        orderDetail = Collections.unmodifiableList(builder.orderDetail);
        quantity = builder.quantity;
        subject = builder.subject;
        focus = Collections.unmodifiableList(builder.focus);
        encounter = builder.encounter;
        occurrence = builder.occurrence;
        asNeeded = builder.asNeeded;
        authoredOn = builder.authoredOn;
        requester = builder.requester;
        performerType = builder.performerType;
        performer = Collections.unmodifiableList(builder.performer);
        location = Collections.unmodifiableList(builder.location);
        reason = Collections.unmodifiableList(builder.reason);
        insurance = Collections.unmodifiableList(builder.insurance);
        supportingInfo = Collections.unmodifiableList(builder.supportingInfo);
        specimen = Collections.unmodifiableList(builder.specimen);
        bodySite = Collections.unmodifiableList(builder.bodySite);
        bodyStructure = builder.bodyStructure;
        note = Collections.unmodifiableList(builder.note);
        patientInstruction = Collections.unmodifiableList(builder.patientInstruction);
        relevantHistory = Collections.unmodifiableList(builder.relevantHistory);
    }

    /**
     * Identifiers assigned to this order instance by the orderer and/or the receiver and/or order fulfiller.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
     * part by this ServiceRequest.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
     * whole or in part by this ServiceRequest.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * Plan/proposal/order fulfilled by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The request takes the place of the referenced completed or terminated request(s).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReplaces() {
        return replaces;
    }

    /**
     * A shared identifier common to all service requests that were authorized more or less simultaneously by a single 
     * author, representing the composite or group identifier.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getRequisition() {
        return requisition;
    }

    /**
     * The status of the order.
     * 
     * @return
     *     An immutable object of type {@link ServiceRequestStatus} that is non-null.
     */
    public ServiceRequestStatus getStatus() {
        return status;
    }

    /**
     * Whether the request is a proposal, plan, an original order or a reflex order.
     * 
     * @return
     *     An immutable object of type {@link ServiceRequestIntent} that is non-null.
     */
    public ServiceRequestIntent getIntent() {
        return intent;
    }

    /**
     * A code that classifies the service for searching, sorting and display purposes (e.g. "Surgical Procedure").
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Indicates how quickly the ServiceRequest should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link ServiceRequestPriority} that may be null.
     */
    public ServiceRequestPriority getPriority() {
        return priority;
    }

    /**
     * Set this to true if the record is saying that the service/procedure should NOT be performed.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getDoNotPerform() {
        return doNotPerform;
    }

    /**
     * A code or reference that identifies a particular service (i.e., procedure, diagnostic investigation, or panel of 
     * investigations) that have been requested.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getCode() {
        return code;
    }

    /**
     * Additional details and instructions about the how the services are to be delivered. For example, and order for a 
     * urinary catheter may have an order detail for an external or indwelling catheter, or an order for a bandage may 
     * require additional instructions specifying how the bandage should be applied.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link OrderDetail} that may be empty.
     */
    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    /**
     * An amount of service being requested which can be a quantity ( for example $1,500 home modification), a ratio ( for 
     * example, 20 half day visits per month), or a range (2.0 to 1.8 Gy per fraction).
     * 
     * @return
     *     An immutable object of type {@link Quantity}, {@link Ratio} or {@link Range} that may be null.
     */
    public Element getQuantity() {
        return quantity;
    }

    /**
     * On whom or what the service is to be performed. This is usually a human patient, but can also be requested on animals, 
     * groups of humans or animals, devices such as dialysis machines, or even locations (typically for environmental scans).
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The actual focus of a service request when it is not the subject of record representing something or someone 
     * associated with the subject such as a spouse, parent, fetus, or donor. The focus of a service request could also be an 
     * existing condition, an intervention, the subject's diet, another service request on the subject, or a body structure 
     * such as tumor or implanted device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getFocus() {
        return focus;
    }

    /**
     * An encounter that provides additional information about the healthcare context in which this request is made.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The date/time at which the requested service should occur.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * If a CodeableConcept is present, it indicates the pre-condition for performing the service. For example "pain", "on 
     * flare-up", etc.
     * 
     * @return
     *     An immutable object of type {@link Boolean} or {@link CodeableConcept} that may be null.
     */
    public Element getAsNeeded() {
        return asNeeded;
    }

    /**
     * When the request transitioned to being actionable.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * The individual who initiated the request and has responsibility for its activation.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequester() {
        return requester;
    }

    /**
     * Desired type of performer for doing the requested service.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPerformerType() {
        return performerType;
    }

    /**
     * The desired performer for doing the requested service. For example, the surgeon, dermatopathologist, endoscopist, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPerformer() {
        return performer;
    }

    /**
     * The preferred location(s) where the procedure should actually happen in coded or free text form. E.g. at home or 
     * nursing day care center.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getLocation() {
        return location;
    }

    /**
     * An explanation or justification for why this service is being requested in coded or textual form. This is often for 
     * billing purposes. May relate to the resources referred to in `supportingInfo`.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be needed for delivering 
     * the requested service.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInsurance() {
        return insurance;
    }

    /**
     * Additional clinical information about the patient or specimen that may influence the services or their 
     * interpretations. This information includes diagnosis, clinical findings and other observations. In laboratory ordering 
     * these are typically referred to as "ask at order entry questions (AOEs)". This includes observations explicitly 
     * requested by the producer (filler) to provide context or supporting information needed to complete the order. For 
     * example, reporting the amount of inspired oxygen for blood gas measurements.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getSupportingInfo() {
        return supportingInfo;
    }

    /**
     * One or more specimens that the laboratory procedure will use.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSpecimen() {
        return specimen;
    }

    /**
     * Anatomic location where the procedure should be performed. This is the target site.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getBodySite() {
        return bodySite;
    }

    /**
     * Anatomic location where the procedure should be performed. This is the target site.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getBodyStructure() {
        return bodyStructure;
    }

    /**
     * Any other notes and comments made about the service request. For example, internal billing notes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Instructions in terms that are understood by the patient or consumer.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link PatientInstruction} that may be empty.
     */
    public List<PatientInstruction> getPatientInstruction() {
        return patientInstruction;
    }

    /**
     * Key events in the history of the request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRelevantHistory() {
        return relevantHistory;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !instantiatesCanonical.isEmpty() || 
            !instantiatesUri.isEmpty() || 
            !basedOn.isEmpty() || 
            !replaces.isEmpty() || 
            (requisition != null) || 
            (status != null) || 
            (intent != null) || 
            !category.isEmpty() || 
            (priority != null) || 
            (doNotPerform != null) || 
            (code != null) || 
            !orderDetail.isEmpty() || 
            (quantity != null) || 
            (subject != null) || 
            !focus.isEmpty() || 
            (encounter != null) || 
            (occurrence != null) || 
            (asNeeded != null) || 
            (authoredOn != null) || 
            (requester != null) || 
            (performerType != null) || 
            !performer.isEmpty() || 
            !location.isEmpty() || 
            !reason.isEmpty() || 
            !insurance.isEmpty() || 
            !supportingInfo.isEmpty() || 
            !specimen.isEmpty() || 
            !bodySite.isEmpty() || 
            (bodyStructure != null) || 
            !note.isEmpty() || 
            !patientInstruction.isEmpty() || 
            !relevantHistory.isEmpty();
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
                accept(requisition, "requisition", visitor);
                accept(status, "status", visitor);
                accept(intent, "intent", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(priority, "priority", visitor);
                accept(doNotPerform, "doNotPerform", visitor);
                accept(code, "code", visitor);
                accept(orderDetail, "orderDetail", visitor, OrderDetail.class);
                accept(quantity, "quantity", visitor);
                accept(subject, "subject", visitor);
                accept(focus, "focus", visitor, Reference.class);
                accept(encounter, "encounter", visitor);
                accept(occurrence, "occurrence", visitor);
                accept(asNeeded, "asNeeded", visitor);
                accept(authoredOn, "authoredOn", visitor);
                accept(requester, "requester", visitor);
                accept(performerType, "performerType", visitor);
                accept(performer, "performer", visitor, Reference.class);
                accept(location, "location", visitor, CodeableReference.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(insurance, "insurance", visitor, Reference.class);
                accept(supportingInfo, "supportingInfo", visitor, CodeableReference.class);
                accept(specimen, "specimen", visitor, Reference.class);
                accept(bodySite, "bodySite", visitor, CodeableConcept.class);
                accept(bodyStructure, "bodyStructure", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(patientInstruction, "patientInstruction", visitor, PatientInstruction.class);
                accept(relevantHistory, "relevantHistory", visitor, Reference.class);
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
        ServiceRequest other = (ServiceRequest) obj;
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
            Objects.equals(requisition, other.requisition) && 
            Objects.equals(status, other.status) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(category, other.category) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(doNotPerform, other.doNotPerform) && 
            Objects.equals(code, other.code) && 
            Objects.equals(orderDetail, other.orderDetail) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(asNeeded, other.asNeeded) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(requester, other.requester) && 
            Objects.equals(performerType, other.performerType) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(location, other.location) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(insurance, other.insurance) && 
            Objects.equals(supportingInfo, other.supportingInfo) && 
            Objects.equals(specimen, other.specimen) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(bodyStructure, other.bodyStructure) && 
            Objects.equals(note, other.note) && 
            Objects.equals(patientInstruction, other.patientInstruction) && 
            Objects.equals(relevantHistory, other.relevantHistory);
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
                requisition, 
                status, 
                intent, 
                category, 
                priority, 
                doNotPerform, 
                code, 
                orderDetail, 
                quantity, 
                subject, 
                focus, 
                encounter, 
                occurrence, 
                asNeeded, 
                authoredOn, 
                requester, 
                performerType, 
                performer, 
                location, 
                reason, 
                insurance, 
                supportingInfo, 
                specimen, 
                bodySite, 
                bodyStructure, 
                note, 
                patientInstruction, 
                relevantHistory);
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
        private Identifier requisition;
        private ServiceRequestStatus status;
        private ServiceRequestIntent intent;
        private List<CodeableConcept> category = new ArrayList<>();
        private ServiceRequestPriority priority;
        private Boolean doNotPerform;
        private CodeableReference code;
        private List<OrderDetail> orderDetail = new ArrayList<>();
        private Element quantity;
        private Reference subject;
        private List<Reference> focus = new ArrayList<>();
        private Reference encounter;
        private Element occurrence;
        private Element asNeeded;
        private DateTime authoredOn;
        private Reference requester;
        private CodeableConcept performerType;
        private List<Reference> performer = new ArrayList<>();
        private List<CodeableReference> location = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private List<Reference> insurance = new ArrayList<>();
        private List<CodeableReference> supportingInfo = new ArrayList<>();
        private List<Reference> specimen = new ArrayList<>();
        private List<CodeableConcept> bodySite = new ArrayList<>();
        private Reference bodyStructure;
        private List<Annotation> note = new ArrayList<>();
        private List<PatientInstruction> patientInstruction = new ArrayList<>();
        private List<Reference> relevantHistory = new ArrayList<>();

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
         * Identifiers assigned to this order instance by the orderer and/or the receiver and/or order fulfiller.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers assigned to this order
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
         * Identifiers assigned to this order instance by the orderer and/or the receiver and/or order fulfiller.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers assigned to this order
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
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this ServiceRequest.
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
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this ServiceRequest.
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
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this ServiceRequest.
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
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this ServiceRequest.
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
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link MedicationRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     What request fulfills
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
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link MedicationRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     What request fulfills
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
         * The request takes the place of the referenced completed or terminated request(s).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param replaces
         *     What request replaces
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
         * The request takes the place of the referenced completed or terminated request(s).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param replaces
         *     What request replaces
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
         * A shared identifier common to all service requests that were authorized more or less simultaneously by a single 
         * author, representing the composite or group identifier.
         * 
         * @param requisition
         *     Composite Request ID
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requisition(Identifier requisition) {
            this.requisition = requisition;
            return this;
        }

        /**
         * The status of the order.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | on-hold | revoked | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ServiceRequestStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Whether the request is a proposal, plan, an original order or a reflex order.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | directive | order +
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(ServiceRequestIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * A code that classifies the service for searching, sorting and display purposes (e.g. "Surgical Procedure").
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of service
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
         * A code that classifies the service for searching, sorting and display purposes (e.g. "Surgical Procedure").
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of service
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
         * Indicates how quickly the ServiceRequest should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(ServiceRequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Convenience method for setting {@code doNotPerform}.
         * 
         * @param doNotPerform
         *     True if service/procedure should not be performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #doNotPerform(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder doNotPerform(java.lang.Boolean doNotPerform) {
            this.doNotPerform = (doNotPerform == null) ? null : Boolean.of(doNotPerform);
            return this;
        }

        /**
         * Set this to true if the record is saying that the service/procedure should NOT be performed.
         * 
         * @param doNotPerform
         *     True if service/procedure should not be performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doNotPerform(Boolean doNotPerform) {
            this.doNotPerform = doNotPerform;
            return this;
        }

        /**
         * A code or reference that identifies a particular service (i.e., procedure, diagnostic investigation, or panel of 
         * investigations) that have been requested.
         * 
         * @param code
         *     What is being requested/ordered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableReference code) {
            this.code = code;
            return this;
        }

        /**
         * Additional details and instructions about the how the services are to be delivered. For example, and order for a 
         * urinary catheter may have an order detail for an external or indwelling catheter, or an order for a bandage may 
         * require additional instructions specifying how the bandage should be applied.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param orderDetail
         *     Additional order information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder orderDetail(OrderDetail... orderDetail) {
            for (OrderDetail value : orderDetail) {
                this.orderDetail.add(value);
            }
            return this;
        }

        /**
         * Additional details and instructions about the how the services are to be delivered. For example, and order for a 
         * urinary catheter may have an order detail for an external or indwelling catheter, or an order for a bandage may 
         * require additional instructions specifying how the bandage should be applied.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param orderDetail
         *     Additional order information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder orderDetail(Collection<OrderDetail> orderDetail) {
            this.orderDetail = new ArrayList<>(orderDetail);
            return this;
        }

        /**
         * An amount of service being requested which can be a quantity ( for example $1,500 home modification), a ratio ( for 
         * example, 20 half day visits per month), or a range (2.0 to 1.8 Gy per fraction).
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Quantity}</li>
         * <li>{@link Ratio}</li>
         * <li>{@link Range}</li>
         * </ul>
         * 
         * @param quantity
         *     Service amount
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder quantity(Element quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * On whom or what the service is to be performed. This is usually a human patient, but can also be requested on animals, 
         * groups of humans or animals, devices such as dialysis machines, or even locations (typically for environmental scans).
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Location}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param subject
         *     Individual or Entity the service is ordered for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The actual focus of a service request when it is not the subject of record representing something or someone 
         * associated with the subject such as a spouse, parent, fetus, or donor. The focus of a service request could also be an 
         * existing condition, an intervention, the subject's diet, another service request on the subject, or a body structure 
         * such as tumor or implanted device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     What the service request is about, when it is not about the subject of record
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
         * The actual focus of a service request when it is not the subject of record representing something or someone 
         * associated with the subject such as a spouse, parent, fetus, or donor. The focus of a service request could also be an 
         * existing condition, an intervention, the subject's diet, another service request on the subject, or a body structure 
         * such as tumor or implanted device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     What the service request is about, when it is not about the subject of record
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
         * An encounter that provides additional information about the healthcare context in which this request is made.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter in which the request was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The date/time at which the requested service should occur.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurrence
         *     When service should occur
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * Convenience method for setting {@code asNeeded} with choice type Boolean.
         * 
         * @param asNeeded
         *     Preconditions for service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #asNeeded(Element)
         */
        public Builder asNeeded(java.lang.Boolean asNeeded) {
            this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
            return this;
        }

        /**
         * If a CodeableConcept is present, it indicates the pre-condition for performing the service. For example "pain", "on 
         * flare-up", etc.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Boolean}</li>
         * <li>{@link CodeableConcept}</li>
         * </ul>
         * 
         * @param asNeeded
         *     Preconditions for service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder asNeeded(Element asNeeded) {
            this.asNeeded = asNeeded;
            return this;
        }

        /**
         * When the request transitioned to being actionable.
         * 
         * @param authoredOn
         *     Date request signed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * The individual who initiated the request and has responsibility for its activation.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param requester
         *     Who/what is requesting service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requester(Reference requester) {
            this.requester = requester;
            return this;
        }

        /**
         * Desired type of performer for doing the requested service.
         * 
         * @param performerType
         *     Performer role
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performerType(CodeableConcept performerType) {
            this.performerType = performerType;
            return this;
        }

        /**
         * The desired performer for doing the requested service. For example, the surgeon, dermatopathologist, endoscopist, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
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
         * @param performer
         *     Requested performer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(Reference... performer) {
            for (Reference value : performer) {
                this.performer.add(value);
            }
            return this;
        }

        /**
         * The desired performer for doing the requested service. For example, the surgeon, dermatopathologist, endoscopist, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
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
         * @param performer
         *     Requested performer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder performer(Collection<Reference> performer) {
            this.performer = new ArrayList<>(performer);
            return this;
        }

        /**
         * The preferred location(s) where the procedure should actually happen in coded or free text form. E.g. at home or 
         * nursing day care center.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     Requested location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(CodeableReference... location) {
            for (CodeableReference value : location) {
                this.location.add(value);
            }
            return this;
        }

        /**
         * The preferred location(s) where the procedure should actually happen in coded or free text form. E.g. at home or 
         * nursing day care center.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     Requested location
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder location(Collection<CodeableReference> location) {
            this.location = new ArrayList<>(location);
            return this;
        }

        /**
         * An explanation or justification for why this service is being requested in coded or textual form. This is often for 
         * billing purposes. May relate to the resources referred to in `supportingInfo`.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Explanation/Justification for procedure or service
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
         * An explanation or justification for why this service is being requested in coded or textual form. This is often for 
         * billing purposes. May relate to the resources referred to in `supportingInfo`.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Explanation/Justification for procedure or service
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
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be needed for delivering 
         * the requested service.
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
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be needed for delivering 
         * the requested service.
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
         * Additional clinical information about the patient or specimen that may influence the services or their 
         * interpretations. This information includes diagnosis, clinical findings and other observations. In laboratory ordering 
         * these are typically referred to as "ask at order entry questions (AOEs)". This includes observations explicitly 
         * requested by the producer (filler) to provide context or supporting information needed to complete the order. For 
         * example, reporting the amount of inspired oxygen for blood gas measurements.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Additional clinical information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInfo(CodeableReference... supportingInfo) {
            for (CodeableReference value : supportingInfo) {
                this.supportingInfo.add(value);
            }
            return this;
        }

        /**
         * Additional clinical information about the patient or specimen that may influence the services or their 
         * interpretations. This information includes diagnosis, clinical findings and other observations. In laboratory ordering 
         * these are typically referred to as "ask at order entry questions (AOEs)". This includes observations explicitly 
         * requested by the producer (filler) to provide context or supporting information needed to complete the order. For 
         * example, reporting the amount of inspired oxygen for blood gas measurements.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Additional clinical information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInfo(Collection<CodeableReference> supportingInfo) {
            this.supportingInfo = new ArrayList<>(supportingInfo);
            return this;
        }

        /**
         * One or more specimens that the laboratory procedure will use.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param specimen
         *     Procedure Samples
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specimen(Reference... specimen) {
            for (Reference value : specimen) {
                this.specimen.add(value);
            }
            return this;
        }

        /**
         * One or more specimens that the laboratory procedure will use.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param specimen
         *     Procedure Samples
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specimen(Collection<Reference> specimen) {
            this.specimen = new ArrayList<>(specimen);
            return this;
        }

        /**
         * Anatomic location where the procedure should be performed. This is the target site.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Coded location on Body
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
         * Anatomic location where the procedure should be performed. This is the target site.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Coded location on Body
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
         * Anatomic location where the procedure should be performed. This is the target site.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link BodyStructure}</li>
         * </ul>
         * 
         * @param bodyStructure
         *     BodyStructure-based location on the body
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodyStructure(Reference bodyStructure) {
            this.bodyStructure = bodyStructure;
            return this;
        }

        /**
         * Any other notes and comments made about the service request. For example, internal billing notes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments
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
         * Any other notes and comments made about the service request. For example, internal billing notes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments
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
         * Instructions in terms that are understood by the patient or consumer.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientInstruction
         *     Patient or consumer-oriented instructions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patientInstruction(PatientInstruction... patientInstruction) {
            for (PatientInstruction value : patientInstruction) {
                this.patientInstruction.add(value);
            }
            return this;
        }

        /**
         * Instructions in terms that are understood by the patient or consumer.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientInstruction
         *     Patient or consumer-oriented instructions
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder patientInstruction(Collection<PatientInstruction> patientInstruction) {
            this.patientInstruction = new ArrayList<>(patientInstruction);
            return this;
        }

        /**
         * Key events in the history of the request.
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
         *     Request provenance
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
         * Key events in the history of the request.
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
         *     Request provenance
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
         * Build the {@link ServiceRequest}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>intent</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ServiceRequest}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ServiceRequest per the base specification
         */
        @Override
        public ServiceRequest build() {
            ServiceRequest serviceRequest = new ServiceRequest(this);
            if (validating) {
                validate(serviceRequest);
            }
            return serviceRequest;
        }

        protected void validate(ServiceRequest serviceRequest) {
            super.validate(serviceRequest);
            ValidationSupport.checkList(serviceRequest.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(serviceRequest.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(serviceRequest.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(serviceRequest.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(serviceRequest.replaces, "replaces", Reference.class);
            ValidationSupport.requireNonNull(serviceRequest.status, "status");
            ValidationSupport.requireNonNull(serviceRequest.intent, "intent");
            ValidationSupport.checkList(serviceRequest.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(serviceRequest.orderDetail, "orderDetail", OrderDetail.class);
            ValidationSupport.choiceElement(serviceRequest.quantity, "quantity", Quantity.class, Ratio.class, Range.class);
            ValidationSupport.requireNonNull(serviceRequest.subject, "subject");
            ValidationSupport.checkList(serviceRequest.focus, "focus", Reference.class);
            ValidationSupport.choiceElement(serviceRequest.occurrence, "occurrence", DateTime.class, Period.class, Timing.class);
            ValidationSupport.choiceElement(serviceRequest.asNeeded, "asNeeded", Boolean.class, CodeableConcept.class);
            ValidationSupport.checkList(serviceRequest.performer, "performer", Reference.class);
            ValidationSupport.checkList(serviceRequest.location, "location", CodeableReference.class);
            ValidationSupport.checkList(serviceRequest.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(serviceRequest.insurance, "insurance", Reference.class);
            ValidationSupport.checkList(serviceRequest.supportingInfo, "supportingInfo", CodeableReference.class);
            ValidationSupport.checkList(serviceRequest.specimen, "specimen", Reference.class);
            ValidationSupport.checkList(serviceRequest.bodySite, "bodySite", CodeableConcept.class);
            ValidationSupport.checkList(serviceRequest.note, "note", Annotation.class);
            ValidationSupport.checkList(serviceRequest.patientInstruction, "patientInstruction", PatientInstruction.class);
            ValidationSupport.checkList(serviceRequest.relevantHistory, "relevantHistory", Reference.class);
            ValidationSupport.checkReferenceType(serviceRequest.basedOn, "basedOn", "CarePlan", "ServiceRequest", "MedicationRequest");
            ValidationSupport.checkReferenceType(serviceRequest.replaces, "replaces", "ServiceRequest");
            ValidationSupport.checkReferenceType(serviceRequest.subject, "subject", "Patient", "Group", "Location", "Device");
            ValidationSupport.checkReferenceType(serviceRequest.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(serviceRequest.requester, "requester", "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device");
            ValidationSupport.checkReferenceType(serviceRequest.performer, "performer", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "HealthcareService", "Patient", "Device", "RelatedPerson");
            ValidationSupport.checkReferenceType(serviceRequest.insurance, "insurance", "Coverage", "ClaimResponse");
            ValidationSupport.checkReferenceType(serviceRequest.specimen, "specimen", "Specimen");
            ValidationSupport.checkReferenceType(serviceRequest.bodyStructure, "bodyStructure", "BodyStructure");
            ValidationSupport.checkReferenceType(serviceRequest.relevantHistory, "relevantHistory", "Provenance");
        }

        protected Builder from(ServiceRequest serviceRequest) {
            super.from(serviceRequest);
            identifier.addAll(serviceRequest.identifier);
            instantiatesCanonical.addAll(serviceRequest.instantiatesCanonical);
            instantiatesUri.addAll(serviceRequest.instantiatesUri);
            basedOn.addAll(serviceRequest.basedOn);
            replaces.addAll(serviceRequest.replaces);
            requisition = serviceRequest.requisition;
            status = serviceRequest.status;
            intent = serviceRequest.intent;
            category.addAll(serviceRequest.category);
            priority = serviceRequest.priority;
            doNotPerform = serviceRequest.doNotPerform;
            code = serviceRequest.code;
            orderDetail.addAll(serviceRequest.orderDetail);
            quantity = serviceRequest.quantity;
            subject = serviceRequest.subject;
            focus.addAll(serviceRequest.focus);
            encounter = serviceRequest.encounter;
            occurrence = serviceRequest.occurrence;
            asNeeded = serviceRequest.asNeeded;
            authoredOn = serviceRequest.authoredOn;
            requester = serviceRequest.requester;
            performerType = serviceRequest.performerType;
            performer.addAll(serviceRequest.performer);
            location.addAll(serviceRequest.location);
            reason.addAll(serviceRequest.reason);
            insurance.addAll(serviceRequest.insurance);
            supportingInfo.addAll(serviceRequest.supportingInfo);
            specimen.addAll(serviceRequest.specimen);
            bodySite.addAll(serviceRequest.bodySite);
            bodyStructure = serviceRequest.bodyStructure;
            note.addAll(serviceRequest.note);
            patientInstruction.addAll(serviceRequest.patientInstruction);
            relevantHistory.addAll(serviceRequest.relevantHistory);
            return this;
        }
    }

    /**
     * Additional details and instructions about the how the services are to be delivered. For example, and order for a 
     * urinary catheter may have an order detail for an external or indwelling catheter, or an order for a bandage may 
     * require additional instructions specifying how the bandage should be applied.
     */
    public static class OrderDetail extends BackboneElement {
        private final CodeableReference parameterFocus;
        @Summary
        @Required
        private final List<Parameter> parameter;

        private OrderDetail(Builder builder) {
            super(builder);
            parameterFocus = builder.parameterFocus;
            parameter = Collections.unmodifiableList(builder.parameter);
        }

        /**
         * Indicates the context of the order details by reference.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getParameterFocus() {
            return parameterFocus;
        }

        /**
         * The parameter details for the service being requested.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Parameter} that is non-empty.
         */
        public List<Parameter> getParameter() {
            return parameter;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (parameterFocus != null) || 
                !parameter.isEmpty();
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
                    accept(parameterFocus, "parameterFocus", visitor);
                    accept(parameter, "parameter", visitor, Parameter.class);
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
            OrderDetail other = (OrderDetail) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(parameterFocus, other.parameterFocus) && 
                Objects.equals(parameter, other.parameter);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    parameterFocus, 
                    parameter);
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
            private CodeableReference parameterFocus;
            private List<Parameter> parameter = new ArrayList<>();

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
             * Indicates the context of the order details by reference.
             * 
             * @param parameterFocus
             *     The context of the order details by reference
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder parameterFocus(CodeableReference parameterFocus) {
                this.parameterFocus = parameterFocus;
                return this;
            }

            /**
             * The parameter details for the service being requested.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param parameter
             *     The parameter details for the service being requested
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder parameter(Parameter... parameter) {
                for (Parameter value : parameter) {
                    this.parameter.add(value);
                }
                return this;
            }

            /**
             * The parameter details for the service being requested.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param parameter
             *     The parameter details for the service being requested
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder parameter(Collection<Parameter> parameter) {
                this.parameter = new ArrayList<>(parameter);
                return this;
            }

            /**
             * Build the {@link OrderDetail}
             * 
             * <p>Required elements:
             * <ul>
             * <li>parameter</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link OrderDetail}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid OrderDetail per the base specification
             */
            @Override
            public OrderDetail build() {
                OrderDetail orderDetail = new OrderDetail(this);
                if (validating) {
                    validate(orderDetail);
                }
                return orderDetail;
            }

            protected void validate(OrderDetail orderDetail) {
                super.validate(orderDetail);
                ValidationSupport.checkNonEmptyList(orderDetail.parameter, "parameter", Parameter.class);
                ValidationSupport.requireValueOrChildren(orderDetail);
            }

            protected Builder from(OrderDetail orderDetail) {
                super.from(orderDetail);
                parameterFocus = orderDetail.parameterFocus;
                parameter.addAll(orderDetail.parameter);
                return this;
            }
        }

        /**
         * The parameter details for the service being requested.
         */
        public static class Parameter extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "ServiceRequestOrderDetailParameterCode",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for order detail parameters.",
                valueSet = "http://hl7.org/fhir/ValueSet/servicerequest-orderdetail-parameter-code"
            )
            @Required
            private final CodeableConcept code;
            @Summary
            @Choice({ Quantity.class, Ratio.class, Range.class, Boolean.class, CodeableConcept.class, String.class, Period.class })
            @Required
            private final Element value;

            private Parameter(Builder builder) {
                super(builder);
                code = builder.code;
                value = builder.value;
            }

            /**
             * A value representing the additional detail or instructions for the order (e.g., catheter insertion, body elevation, 
             * descriptive device configuration and/or setting instructions).
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * Indicates a value for the order detail.
             * 
             * @return
             *     An immutable object of type {@link Quantity}, {@link Ratio}, {@link Range}, {@link Boolean}, {@link CodeableConcept}, 
             *     {@link String} or {@link Period} that is non-null.
             */
            public Element getValue() {
                return value;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (code != null) || 
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
                        accept(code, "code", visitor);
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
                Parameter other = (Parameter) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(value, other.value);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        code, 
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
                private CodeableConcept code;
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
                 * A value representing the additional detail or instructions for the order (e.g., catheter insertion, body elevation, 
                 * descriptive device configuration and/or setting instructions).
                 * 
                 * <p>This element is required.
                 * 
                 * @param code
                 *     The detail of the order being requested
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CodeableConcept code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type Boolean.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The value for the order detail
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
                 * Convenience method for setting {@code value} with choice type String.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The value for the order detail
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
                 * Indicates a value for the order detail.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Quantity}</li>
                 * <li>{@link Ratio}</li>
                 * <li>{@link Range}</li>
                 * <li>{@link Boolean}</li>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link String}</li>
                 * <li>{@link Period}</li>
                 * </ul>
                 * 
                 * @param value
                 *     The value for the order detail
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link Parameter}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>code</li>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Parameter}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Parameter per the base specification
                 */
                @Override
                public Parameter build() {
                    Parameter parameter = new Parameter(this);
                    if (validating) {
                        validate(parameter);
                    }
                    return parameter;
                }

                protected void validate(Parameter parameter) {
                    super.validate(parameter);
                    ValidationSupport.requireNonNull(parameter.code, "code");
                    ValidationSupport.requireChoiceElement(parameter.value, "value", Quantity.class, Ratio.class, Range.class, Boolean.class, CodeableConcept.class, String.class, Period.class);
                    ValidationSupport.requireValueOrChildren(parameter);
                }

                protected Builder from(Parameter parameter) {
                    super.from(parameter);
                    code = parameter.code;
                    value = parameter.value;
                    return this;
                }
            }
        }
    }

    /**
     * Instructions in terms that are understood by the patient or consumer.
     */
    public static class PatientInstruction extends BackboneElement {
        @Summary
        @ReferenceTarget({ "DocumentReference" })
        @Choice({ Markdown.class, Reference.class })
        private final Element instruction;

        private PatientInstruction(Builder builder) {
            super(builder);
            instruction = builder.instruction;
        }

        /**
         * Instructions in terms that are understood by the patient or consumer.
         * 
         * @return
         *     An immutable object of type {@link Markdown} or {@link Reference} that may be null.
         */
        public Element getInstruction() {
            return instruction;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (instruction != null);
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
                    accept(instruction, "instruction", visitor);
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
            PatientInstruction other = (PatientInstruction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(instruction, other.instruction);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    instruction);
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
            private Element instruction;

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
             * Instructions in terms that are understood by the patient or consumer.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Markdown}</li>
             * <li>{@link Reference}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * </ul>
             * 
             * @param instruction
             *     Patient or consumer-oriented instructions
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instruction(Element instruction) {
                this.instruction = instruction;
                return this;
            }

            /**
             * Build the {@link PatientInstruction}
             * 
             * @return
             *     An immutable object of type {@link PatientInstruction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid PatientInstruction per the base specification
             */
            @Override
            public PatientInstruction build() {
                PatientInstruction patientInstruction = new PatientInstruction(this);
                if (validating) {
                    validate(patientInstruction);
                }
                return patientInstruction;
            }

            protected void validate(PatientInstruction patientInstruction) {
                super.validate(patientInstruction);
                ValidationSupport.choiceElement(patientInstruction.instruction, "instruction", Markdown.class, Reference.class);
                ValidationSupport.checkReferenceType(patientInstruction.instruction, "instruction", "DocumentReference");
                ValidationSupport.requireValueOrChildren(patientInstruction);
            }

            protected Builder from(PatientInstruction patientInstruction) {
                super.from(patientInstruction);
                instruction = patientInstruction.instruction;
                return this;
            }
        }
    }
}
