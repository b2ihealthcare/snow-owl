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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Dosage;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationRequestIntent;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationRequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationRequestStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * An order or request for both supply of the medication and the instructions for administration of the medication to a 
 * patient. The resource is called "MedicationRequest" rather than "MedicationPrescription" or "MedicationOrder" to 
 * generalize the use across inpatient and outpatient settings, including care plans, etc., and to harmonize with 
 * workflow patterns.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "medicationRequest-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/medication-intended-performer-role",
    expression = "performerType.exists() implies (performerType.memberOf('http://hl7.org/fhir/ValueSet/medication-intended-performer-role', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/MedicationRequest",
    generated = true
)
@Constraint(
    id = "medicationRequest-1",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/medicationrequest-course-of-therapy",
    expression = "courseOfTherapyType.exists() implies (courseOfTherapyType.memberOf('http://hl7.org/fhir/ValueSet/medicationrequest-course-of-therapy', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/MedicationRequest",
    generated = true
)
@Constraint(
    id = "medicationRequest-2",
    level = "Warning",
    location = "substitution.allowed",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/v3-ActSubstanceAdminSubstitutionCode",
    expression = "$this.as(Boolean).memberOf('http://terminology.hl7.org/ValueSet/v3-ActSubstanceAdminSubstitutionCode', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/MedicationRequest",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MedicationRequest extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "CarePlan", "MedicationRequest", "ServiceRequest", "ImmunizationRecommendation" })
    private final List<Reference> basedOn;
    @ReferenceTarget({ "MedicationRequest" })
    private final Reference priorPrescription;
    @Summary
    private final Identifier groupIdentifier;
    @Summary
    @Binding(
        bindingName = "MedicationRequestStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept specifying the state of the prescribing event. Describes the lifecycle of the prescription.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-status|5.0.0"
    )
    @Required
    private final MedicationRequestStatus status;
    @Binding(
        bindingName = "MedicationRequestStatusReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Identifies the reasons for a given status.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-status-reason"
    )
    private final CodeableConcept statusReason;
    private final DateTime statusChanged;
    @Summary
    @Binding(
        bindingName = "MedicationRequestIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "The kind of medication order.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-intent|5.0.0"
    )
    @Required
    private final MedicationRequestIntent intent;
    @Binding(
        bindingName = "MedicationRequestAdministrationLocation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying where the medication is to be consumed or administered.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-admin-location"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "MedicationRequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final MedicationRequestPriority priority;
    @Summary
    private final Boolean doNotPerform;
    @Summary
    @Binding(
        bindingName = "MedicationCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying substance or product that can be ordered.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    @Required
    private final CodeableReference medication;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization" })
    private final List<Reference> informationSource;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final List<Reference> supportingInformation;
    @Summary
    private final DateTime authoredOn;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device" })
    private final Reference requester;
    @Summary
    private final Boolean reported;
    @Summary
    @Binding(
        bindingName = "MedicationRequestPerformerType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Identifies the type of individual that is desired to administer the medication.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-intended-performer-role"
    )
    private final CodeableConcept performerType;
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "DeviceDefinition", "RelatedPerson", "CareTeam", "HealthcareService" })
    private final List<Reference> performer;
    private final List<CodeableReference> device;
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final Reference recorder;
    @Binding(
        bindingName = "MedicationRequestReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept indicating why the medication was ordered.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableReference> reason;
    @Binding(
        bindingName = "MedicationRequestCourseOfTherapy",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Identifies the overall pattern of medication administratio.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationrequest-course-of-therapy"
    )
    private final CodeableConcept courseOfTherapyType;
    @ReferenceTarget({ "Coverage", "ClaimResponse" })
    private final List<Reference> insurance;
    private final List<Annotation> note;
    private final Markdown renderedDosageInstruction;
    private final Period effectiveDosePeriod;
    private final List<Dosage> dosageInstruction;
    private final DispenseRequest dispenseRequest;
    private final Substitution substitution;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> eventHistory;

    private MedicationRequest(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        priorPrescription = builder.priorPrescription;
        groupIdentifier = builder.groupIdentifier;
        status = builder.status;
        statusReason = builder.statusReason;
        statusChanged = builder.statusChanged;
        intent = builder.intent;
        category = Collections.unmodifiableList(builder.category);
        priority = builder.priority;
        doNotPerform = builder.doNotPerform;
        medication = builder.medication;
        subject = builder.subject;
        informationSource = Collections.unmodifiableList(builder.informationSource);
        encounter = builder.encounter;
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        authoredOn = builder.authoredOn;
        requester = builder.requester;
        reported = builder.reported;
        performerType = builder.performerType;
        performer = Collections.unmodifiableList(builder.performer);
        device = Collections.unmodifiableList(builder.device);
        recorder = builder.recorder;
        reason = Collections.unmodifiableList(builder.reason);
        courseOfTherapyType = builder.courseOfTherapyType;
        insurance = Collections.unmodifiableList(builder.insurance);
        note = Collections.unmodifiableList(builder.note);
        renderedDosageInstruction = builder.renderedDosageInstruction;
        effectiveDosePeriod = builder.effectiveDosePeriod;
        dosageInstruction = Collections.unmodifiableList(builder.dosageInstruction);
        dispenseRequest = builder.dispenseRequest;
        substitution = builder.substitution;
        eventHistory = Collections.unmodifiableList(builder.eventHistory);
    }

    /**
     * Identifiers associated with this medication request that are defined by business processes and/or used to refer to it 
     * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
     * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
     * to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A plan or request that is fulfilled in whole or in part by this medication request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Reference to an order/prescription that is being replaced by this MedicationRequest.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPriorPrescription() {
        return priorPrescription;
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
     * A code specifying the current state of the order. Generally, this will be active or completed state.
     * 
     * @return
     *     An immutable object of type {@link MedicationRequestStatus} that is non-null.
     */
    public MedicationRequestStatus getStatus() {
        return status;
    }

    /**
     * Captures the reason for the current state of the MedicationRequest.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * The date (and perhaps time) when the status was changed.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getStatusChanged() {
        return statusChanged;
    }

    /**
     * Whether the request is a proposal, plan, or an original order.
     * 
     * @return
     *     An immutable object of type {@link MedicationRequestIntent} that is non-null.
     */
    public MedicationRequestIntent getIntent() {
        return intent;
    }

    /**
     * An arbitrary categorization or grouping of the medication request. It could be used for indicating where meds are 
     * intended to be administered, eg. in an inpatient setting or in a patient's home, or a legal category of the medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Indicates how quickly the Medication Request should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link MedicationRequestPriority} that may be null.
     */
    public MedicationRequestPriority getPriority() {
        return priority;
    }

    /**
     * If true, indicates that the provider is asking for the patient to either stop taking or to not start taking the 
     * specified medication. For example, the patient is taking an existing medication and the provider is changing their 
     * medication. They want to create two seperate requests: one to stop using the current medication and another to start 
     * the new medication.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getDoNotPerform() {
        return doNotPerform;
    }

    /**
     * Identifies the medication being requested. This is a link to a resource that represents the medication which may be 
     * the details of the medication or simply an attribute carrying a code that identifies the medication from a known list 
     * of medications.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getMedication() {
        return medication;
    }

    /**
     * The individual or group for whom the medication has been requested.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The person or organization who provided the information about this request, if the source is someone other than the 
     * requestor. This is often used when the MedicationRequest is reported by another person.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInformationSource() {
        return informationSource;
    }

    /**
     * The Encounter during which this [x] was created or to which the creation of this record is tightly associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Information to support fulfilling (i.e. dispensing or administering) of the medication, for example, patient height 
     * and weight, a MedicationStatement for the patient).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * The date (and perhaps time) when the prescription was initially written or authored on.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * The individual, organization, or device that initiated the request and has responsibility for its activation.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequester() {
        return requester;
    }

    /**
     * Indicates if this record was captured as a secondary 'reported' record rather than as an original primary source-of-
     * truth record. It may also indicate the source of the report.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getReported() {
        return reported;
    }

    /**
     * Indicates the type of performer of the administration of the medication.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPerformerType() {
        return performerType;
    }

    /**
     * The specified desired performer of the medication treatment (e.g. the performer of the medication administration). For 
     * devices, this is the device that is intended to perform the administration of the medication. An IV Pump would be an 
     * example of a device that is performing the administration. Both the IV Pump and the practitioner that set the rate or 
     * bolus on the pump can be listed as performers.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPerformer() {
        return performer;
    }

    /**
     * The intended type of device that is to be used for the administration of the medication (for example, PCA Pump).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getDevice() {
        return device;
    }

    /**
     * The person who entered the order on behalf of another individual for example in the case of a verbal or a telephone 
     * order.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRecorder() {
        return recorder;
    }

    /**
     * The reason or the indication for ordering or not ordering the medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * The description of the overall pattern of the administration of the medication to the patient.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCourseOfTherapyType() {
        return courseOfTherapyType;
    }

    /**
     * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
     * the requested service.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInsurance() {
        return insurance;
    }

    /**
     * Extra information about the prescription that could not be conveyed by the other attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
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
     * The period over which the medication is to be taken. Where there are multiple dosageInstruction lines (for example, 
     * tapering doses), this is the earliest date and the latest end date of the dosageInstructions.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectiveDosePeriod() {
        return effectiveDosePeriod;
    }

    /**
     * Specific instructions for how the medication is to be used by the patient.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Dosage} that may be empty.
     */
    public List<Dosage> getDosageInstruction() {
        return dosageInstruction;
    }

    /**
     * Indicates the specific details for the dispense or medication supply part of a medication request (also known as a 
     * Medication Prescription or Medication Order). Note that this information is not always sent with the order. There may 
     * be in some settings (e.g. hospitals) institutional or system support for completing the dispense details in the 
     * pharmacy department.
     * 
     * @return
     *     An immutable object of type {@link DispenseRequest} that may be null.
     */
    public DispenseRequest getDispenseRequest() {
        return dispenseRequest;
    }

    /**
     * Indicates whether or not substitution can or should be part of the dispense. In some cases, substitution must happen, 
     * in other cases substitution must not happen. This block explains the prescriber's intent. If nothing is specified 
     * substitution may be done.
     * 
     * @return
     *     An immutable object of type {@link Substitution} that may be null.
     */
    public Substitution getSubstitution() {
        return substitution;
    }

    /**
     * Links to Provenance records for past versions of this resource or fulfilling request or event resources that identify 
     * key state transitions or updates that are likely to be relevant to a user looking at the current version of the 
     * resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEventHistory() {
        return eventHistory;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !basedOn.isEmpty() || 
            (priorPrescription != null) || 
            (groupIdentifier != null) || 
            (status != null) || 
            (statusReason != null) || 
            (statusChanged != null) || 
            (intent != null) || 
            !category.isEmpty() || 
            (priority != null) || 
            (doNotPerform != null) || 
            (medication != null) || 
            (subject != null) || 
            !informationSource.isEmpty() || 
            (encounter != null) || 
            !supportingInformation.isEmpty() || 
            (authoredOn != null) || 
            (requester != null) || 
            (reported != null) || 
            (performerType != null) || 
            !performer.isEmpty() || 
            !device.isEmpty() || 
            (recorder != null) || 
            !reason.isEmpty() || 
            (courseOfTherapyType != null) || 
            !insurance.isEmpty() || 
            !note.isEmpty() || 
            (renderedDosageInstruction != null) || 
            (effectiveDosePeriod != null) || 
            !dosageInstruction.isEmpty() || 
            (dispenseRequest != null) || 
            (substitution != null) || 
            !eventHistory.isEmpty();
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
                accept(priorPrescription, "priorPrescription", visitor);
                accept(groupIdentifier, "groupIdentifier", visitor);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor);
                accept(statusChanged, "statusChanged", visitor);
                accept(intent, "intent", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(priority, "priority", visitor);
                accept(doNotPerform, "doNotPerform", visitor);
                accept(medication, "medication", visitor);
                accept(subject, "subject", visitor);
                accept(informationSource, "informationSource", visitor, Reference.class);
                accept(encounter, "encounter", visitor);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(authoredOn, "authoredOn", visitor);
                accept(requester, "requester", visitor);
                accept(reported, "reported", visitor);
                accept(performerType, "performerType", visitor);
                accept(performer, "performer", visitor, Reference.class);
                accept(device, "device", visitor, CodeableReference.class);
                accept(recorder, "recorder", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(courseOfTherapyType, "courseOfTherapyType", visitor);
                accept(insurance, "insurance", visitor, Reference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(renderedDosageInstruction, "renderedDosageInstruction", visitor);
                accept(effectiveDosePeriod, "effectiveDosePeriod", visitor);
                accept(dosageInstruction, "dosageInstruction", visitor, Dosage.class);
                accept(dispenseRequest, "dispenseRequest", visitor);
                accept(substitution, "substitution", visitor);
                accept(eventHistory, "eventHistory", visitor, Reference.class);
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
        MedicationRequest other = (MedicationRequest) obj;
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
            Objects.equals(priorPrescription, other.priorPrescription) && 
            Objects.equals(groupIdentifier, other.groupIdentifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(statusChanged, other.statusChanged) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(category, other.category) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(doNotPerform, other.doNotPerform) && 
            Objects.equals(medication, other.medication) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(informationSource, other.informationSource) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(requester, other.requester) && 
            Objects.equals(reported, other.reported) && 
            Objects.equals(performerType, other.performerType) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(device, other.device) && 
            Objects.equals(recorder, other.recorder) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(courseOfTherapyType, other.courseOfTherapyType) && 
            Objects.equals(insurance, other.insurance) && 
            Objects.equals(note, other.note) && 
            Objects.equals(renderedDosageInstruction, other.renderedDosageInstruction) && 
            Objects.equals(effectiveDosePeriod, other.effectiveDosePeriod) && 
            Objects.equals(dosageInstruction, other.dosageInstruction) && 
            Objects.equals(dispenseRequest, other.dispenseRequest) && 
            Objects.equals(substitution, other.substitution) && 
            Objects.equals(eventHistory, other.eventHistory);
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
                priorPrescription, 
                groupIdentifier, 
                status, 
                statusReason, 
                statusChanged, 
                intent, 
                category, 
                priority, 
                doNotPerform, 
                medication, 
                subject, 
                informationSource, 
                encounter, 
                supportingInformation, 
                authoredOn, 
                requester, 
                reported, 
                performerType, 
                performer, 
                device, 
                recorder, 
                reason, 
                courseOfTherapyType, 
                insurance, 
                note, 
                renderedDosageInstruction, 
                effectiveDosePeriod, 
                dosageInstruction, 
                dispenseRequest, 
                substitution, 
                eventHistory);
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
        private Reference priorPrescription;
        private Identifier groupIdentifier;
        private MedicationRequestStatus status;
        private CodeableConcept statusReason;
        private DateTime statusChanged;
        private MedicationRequestIntent intent;
        private List<CodeableConcept> category = new ArrayList<>();
        private MedicationRequestPriority priority;
        private Boolean doNotPerform;
        private CodeableReference medication;
        private Reference subject;
        private List<Reference> informationSource = new ArrayList<>();
        private Reference encounter;
        private List<Reference> supportingInformation = new ArrayList<>();
        private DateTime authoredOn;
        private Reference requester;
        private Boolean reported;
        private CodeableConcept performerType;
        private List<Reference> performer = new ArrayList<>();
        private List<CodeableReference> device = new ArrayList<>();
        private Reference recorder;
        private List<CodeableReference> reason = new ArrayList<>();
        private CodeableConcept courseOfTherapyType;
        private List<Reference> insurance = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private Markdown renderedDosageInstruction;
        private Period effectiveDosePeriod;
        private List<Dosage> dosageInstruction = new ArrayList<>();
        private DispenseRequest dispenseRequest;
        private Substitution substitution;
        private List<Reference> eventHistory = new ArrayList<>();

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
         * Identifiers associated with this medication request that are defined by business processes and/or used to refer to it 
         * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
         * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
         * to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External ids for this request
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
         * Identifiers associated with this medication request that are defined by business processes and/or used to refer to it 
         * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
         * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
         * to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External ids for this request
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
         * A plan or request that is fulfilled in whole or in part by this medication request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * </ul>
         * 
         * @param basedOn
         *     A plan or request that is fulfilled in whole or in part by this medication request
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
         * A plan or request that is fulfilled in whole or in part by this medication request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * </ul>
         * 
         * @param basedOn
         *     A plan or request that is fulfilled in whole or in part by this medication request
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
         * Reference to an order/prescription that is being replaced by this MedicationRequest.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link MedicationRequest}</li>
         * </ul>
         * 
         * @param priorPrescription
         *     Reference to an order/prescription that is being replaced by this MedicationRequest
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priorPrescription(Reference priorPrescription) {
            this.priorPrescription = priorPrescription;
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
         * A code specifying the current state of the order. Generally, this will be active or completed state.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | on-hold | ended | stopped | completed | cancelled | entered-in-error | draft | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MedicationRequestStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Captures the reason for the current state of the MedicationRequest.
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
         * The date (and perhaps time) when the status was changed.
         * 
         * @param statusChanged
         *     When the status was changed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusChanged(DateTime statusChanged) {
            this.statusChanged = statusChanged;
            return this;
        }

        /**
         * Whether the request is a proposal, plan, or an original order.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(MedicationRequestIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * An arbitrary categorization or grouping of the medication request. It could be used for indicating where meds are 
         * intended to be administered, eg. in an inpatient setting or in a patient's home, or a legal category of the medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Grouping or category of medication request
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
         * An arbitrary categorization or grouping of the medication request. It could be used for indicating where meds are 
         * intended to be administered, eg. in an inpatient setting or in a patient's home, or a legal category of the medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Grouping or category of medication request
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
         * Indicates how quickly the Medication Request should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(MedicationRequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Convenience method for setting {@code doNotPerform}.
         * 
         * @param doNotPerform
         *     True if patient is to stop taking or not to start taking the medication
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
         * If true, indicates that the provider is asking for the patient to either stop taking or to not start taking the 
         * specified medication. For example, the patient is taking an existing medication and the provider is changing their 
         * medication. They want to create two seperate requests: one to stop using the current medication and another to start 
         * the new medication.
         * 
         * @param doNotPerform
         *     True if patient is to stop taking or not to start taking the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doNotPerform(Boolean doNotPerform) {
            this.doNotPerform = doNotPerform;
            return this;
        }

        /**
         * Identifies the medication being requested. This is a link to a resource that represents the medication which may be 
         * the details of the medication or simply an attribute carrying a code that identifies the medication from a known list 
         * of medications.
         * 
         * <p>This element is required.
         * 
         * @param medication
         *     Medication to be taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder medication(CodeableReference medication) {
            this.medication = medication;
            return this;
        }

        /**
         * The individual or group for whom the medication has been requested.
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
         *     Individual or group for whom the medication has been requested
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The person or organization who provided the information about this request, if the source is someone other than the 
         * requestor. This is often used when the MedicationRequest is reported by another person.
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
         *     The person or organization who provided the information about this request, if the source is someone other than the 
         *     requestor
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
         * The person or organization who provided the information about this request, if the source is someone other than the 
         * requestor. This is often used when the MedicationRequest is reported by another person.
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
         *     The person or organization who provided the information about this request, if the source is someone other than the 
         *     requestor
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
         * The Encounter during which this [x] was created or to which the creation of this record is tightly associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter created as part of encounter/admission/stay
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Information to support fulfilling (i.e. dispensing or administering) of the medication, for example, patient height 
         * and weight, a MedicationStatement for the patient).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information to support fulfilling of the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInformation(Reference... supportingInformation) {
            for (Reference value : supportingInformation) {
                this.supportingInformation.add(value);
            }
            return this;
        }

        /**
         * Information to support fulfilling (i.e. dispensing or administering) of the medication, for example, patient height 
         * and weight, a MedicationStatement for the patient).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information to support fulfilling of the medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInformation(Collection<Reference> supportingInformation) {
            this.supportingInformation = new ArrayList<>(supportingInformation);
            return this;
        }

        /**
         * The date (and perhaps time) when the prescription was initially written or authored on.
         * 
         * @param authoredOn
         *     When request was initially authored
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * The individual, organization, or device that initiated the request and has responsibility for its activation.
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
         *     Who/What requested the Request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requester(Reference requester) {
            this.requester = requester;
            return this;
        }

        /**
         * Convenience method for setting {@code reported}.
         * 
         * @param reported
         *     Reported rather than primary record
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #reported(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder reported(java.lang.Boolean reported) {
            this.reported = (reported == null) ? null : Boolean.of(reported);
            return this;
        }

        /**
         * Indicates if this record was captured as a secondary 'reported' record rather than as an original primary source-of-
         * truth record. It may also indicate the source of the report.
         * 
         * @param reported
         *     Reported rather than primary record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reported(Boolean reported) {
            this.reported = reported;
            return this;
        }

        /**
         * Indicates the type of performer of the administration of the medication.
         * 
         * @param performerType
         *     Desired kind of performer of the medication administration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performerType(CodeableConcept performerType) {
            this.performerType = performerType;
            return this;
        }

        /**
         * The specified desired performer of the medication treatment (e.g. the performer of the medication administration). For 
         * devices, this is the device that is intended to perform the administration of the medication. An IV Pump would be an 
         * example of a device that is performing the administration. Both the IV Pump and the practitioner that set the rate or 
         * bolus on the pump can be listed as performers.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link DeviceDefinition}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param performer
         *     Intended performer of administration
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
         * The specified desired performer of the medication treatment (e.g. the performer of the medication administration). For 
         * devices, this is the device that is intended to perform the administration of the medication. An IV Pump would be an 
         * example of a device that is performing the administration. Both the IV Pump and the practitioner that set the rate or 
         * bolus on the pump can be listed as performers.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link DeviceDefinition}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param performer
         *     Intended performer of administration
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
         * The intended type of device that is to be used for the administration of the medication (for example, PCA Pump).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param device
         *     Intended type of device for the administration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(CodeableReference... device) {
            for (CodeableReference value : device) {
                this.device.add(value);
            }
            return this;
        }

        /**
         * The intended type of device that is to be used for the administration of the medication (for example, PCA Pump).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param device
         *     Intended type of device for the administration
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder device(Collection<CodeableReference> device) {
            this.device = new ArrayList<>(device);
            return this;
        }

        /**
         * The person who entered the order on behalf of another individual for example in the case of a verbal or a telephone 
         * order.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param recorder
         *     Person who entered the request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorder(Reference recorder) {
            this.recorder = recorder;
            return this;
        }

        /**
         * The reason or the indication for ordering or not ordering the medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason or indication for ordering or not ordering the medication
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
         * The reason or the indication for ordering or not ordering the medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason or indication for ordering or not ordering the medication
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
         * The description of the overall pattern of the administration of the medication to the patient.
         * 
         * @param courseOfTherapyType
         *     Overall pattern of medication administration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder courseOfTherapyType(CodeableConcept courseOfTherapyType) {
            this.courseOfTherapyType = courseOfTherapyType;
            return this;
        }

        /**
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
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
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
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
         * Extra information about the prescription that could not be conveyed by the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the prescription
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
         * Extra information about the prescription that could not be conveyed by the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the prescription
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
         * The period over which the medication is to be taken. Where there are multiple dosageInstruction lines (for example, 
         * tapering doses), this is the earliest date and the latest end date of the dosageInstructions.
         * 
         * @param effectiveDosePeriod
         *     Period over which the medication is to be taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectiveDosePeriod(Period effectiveDosePeriod) {
            this.effectiveDosePeriod = effectiveDosePeriod;
            return this;
        }

        /**
         * Specific instructions for how the medication is to be used by the patient.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosageInstruction
         *     Specific instructions for how the medication should be taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dosageInstruction(Dosage... dosageInstruction) {
            for (Dosage value : dosageInstruction) {
                this.dosageInstruction.add(value);
            }
            return this;
        }

        /**
         * Specific instructions for how the medication is to be used by the patient.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosageInstruction
         *     Specific instructions for how the medication should be taken
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder dosageInstruction(Collection<Dosage> dosageInstruction) {
            this.dosageInstruction = new ArrayList<>(dosageInstruction);
            return this;
        }

        /**
         * Indicates the specific details for the dispense or medication supply part of a medication request (also known as a 
         * Medication Prescription or Medication Order). Note that this information is not always sent with the order. There may 
         * be in some settings (e.g. hospitals) institutional or system support for completing the dispense details in the 
         * pharmacy department.
         * 
         * @param dispenseRequest
         *     Medication supply authorization
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dispenseRequest(DispenseRequest dispenseRequest) {
            this.dispenseRequest = dispenseRequest;
            return this;
        }

        /**
         * Indicates whether or not substitution can or should be part of the dispense. In some cases, substitution must happen, 
         * in other cases substitution must not happen. This block explains the prescriber's intent. If nothing is specified 
         * substitution may be done.
         * 
         * @param substitution
         *     Any restrictions on medication substitution
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder substitution(Substitution substitution) {
            this.substitution = substitution;
            return this;
        }

        /**
         * Links to Provenance records for past versions of this resource or fulfilling request or event resources that identify 
         * key state transitions or updates that are likely to be relevant to a user looking at the current version of the 
         * resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param eventHistory
         *     A list of events of interest in the lifecycle
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder eventHistory(Reference... eventHistory) {
            for (Reference value : eventHistory) {
                this.eventHistory.add(value);
            }
            return this;
        }

        /**
         * Links to Provenance records for past versions of this resource or fulfilling request or event resources that identify 
         * key state transitions or updates that are likely to be relevant to a user looking at the current version of the 
         * resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param eventHistory
         *     A list of events of interest in the lifecycle
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder eventHistory(Collection<Reference> eventHistory) {
            this.eventHistory = new ArrayList<>(eventHistory);
            return this;
        }

        /**
         * Build the {@link MedicationRequest}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>intent</li>
         * <li>medication</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link MedicationRequest}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MedicationRequest per the base specification
         */
        @Override
        public MedicationRequest build() {
            MedicationRequest medicationRequest = new MedicationRequest(this);
            if (validating) {
                validate(medicationRequest);
            }
            return medicationRequest;
        }

        protected void validate(MedicationRequest medicationRequest) {
            super.validate(medicationRequest);
            ValidationSupport.checkList(medicationRequest.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(medicationRequest.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(medicationRequest.status, "status");
            ValidationSupport.requireNonNull(medicationRequest.intent, "intent");
            ValidationSupport.checkList(medicationRequest.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(medicationRequest.medication, "medication");
            ValidationSupport.requireNonNull(medicationRequest.subject, "subject");
            ValidationSupport.checkList(medicationRequest.informationSource, "informationSource", Reference.class);
            ValidationSupport.checkList(medicationRequest.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.checkList(medicationRequest.performer, "performer", Reference.class);
            ValidationSupport.checkList(medicationRequest.device, "device", CodeableReference.class);
            ValidationSupport.checkList(medicationRequest.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(medicationRequest.insurance, "insurance", Reference.class);
            ValidationSupport.checkList(medicationRequest.note, "note", Annotation.class);
            ValidationSupport.checkList(medicationRequest.dosageInstruction, "dosageInstruction", Dosage.class);
            ValidationSupport.checkList(medicationRequest.eventHistory, "eventHistory", Reference.class);
            ValidationSupport.checkReferenceType(medicationRequest.basedOn, "basedOn", "CarePlan", "MedicationRequest", "ServiceRequest", "ImmunizationRecommendation");
            ValidationSupport.checkReferenceType(medicationRequest.priorPrescription, "priorPrescription", "MedicationRequest");
            ValidationSupport.checkReferenceType(medicationRequest.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(medicationRequest.informationSource, "informationSource", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson", "Organization");
            ValidationSupport.checkReferenceType(medicationRequest.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(medicationRequest.requester, "requester", "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device");
            ValidationSupport.checkReferenceType(medicationRequest.performer, "performer", "Practitioner", "PractitionerRole", "Organization", "Patient", "DeviceDefinition", "RelatedPerson", "CareTeam", "HealthcareService");
            ValidationSupport.checkReferenceType(medicationRequest.recorder, "recorder", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(medicationRequest.insurance, "insurance", "Coverage", "ClaimResponse");
            ValidationSupport.checkReferenceType(medicationRequest.eventHistory, "eventHistory", "Provenance");
        }

        protected Builder from(MedicationRequest medicationRequest) {
            super.from(medicationRequest);
            identifier.addAll(medicationRequest.identifier);
            basedOn.addAll(medicationRequest.basedOn);
            priorPrescription = medicationRequest.priorPrescription;
            groupIdentifier = medicationRequest.groupIdentifier;
            status = medicationRequest.status;
            statusReason = medicationRequest.statusReason;
            statusChanged = medicationRequest.statusChanged;
            intent = medicationRequest.intent;
            category.addAll(medicationRequest.category);
            priority = medicationRequest.priority;
            doNotPerform = medicationRequest.doNotPerform;
            medication = medicationRequest.medication;
            subject = medicationRequest.subject;
            informationSource.addAll(medicationRequest.informationSource);
            encounter = medicationRequest.encounter;
            supportingInformation.addAll(medicationRequest.supportingInformation);
            authoredOn = medicationRequest.authoredOn;
            requester = medicationRequest.requester;
            reported = medicationRequest.reported;
            performerType = medicationRequest.performerType;
            performer.addAll(medicationRequest.performer);
            device.addAll(medicationRequest.device);
            recorder = medicationRequest.recorder;
            reason.addAll(medicationRequest.reason);
            courseOfTherapyType = medicationRequest.courseOfTherapyType;
            insurance.addAll(medicationRequest.insurance);
            note.addAll(medicationRequest.note);
            renderedDosageInstruction = medicationRequest.renderedDosageInstruction;
            effectiveDosePeriod = medicationRequest.effectiveDosePeriod;
            dosageInstruction.addAll(medicationRequest.dosageInstruction);
            dispenseRequest = medicationRequest.dispenseRequest;
            substitution = medicationRequest.substitution;
            eventHistory.addAll(medicationRequest.eventHistory);
            return this;
        }
    }

    /**
     * Indicates the specific details for the dispense or medication supply part of a medication request (also known as a 
     * Medication Prescription or Medication Order). Note that this information is not always sent with the order. There may 
     * be in some settings (e.g. hospitals) institutional or system support for completing the dispense details in the 
     * pharmacy department.
     */
    public static class DispenseRequest extends BackboneElement {
        private final InitialFill initialFill;
        private final Duration dispenseInterval;
        private final Period validityPeriod;
        private final UnsignedInt numberOfRepeatsAllowed;
        private final SimpleQuantity quantity;
        private final Duration expectedSupplyDuration;
        @ReferenceTarget({ "Organization" })
        private final Reference dispenser;
        private final List<Annotation> dispenserInstruction;
        @Binding(
            bindingName = "MedicationRequestDoseAdministrationAid",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/medication-dose-aid"
        )
        private final CodeableConcept doseAdministrationAid;

        private DispenseRequest(Builder builder) {
            super(builder);
            initialFill = builder.initialFill;
            dispenseInterval = builder.dispenseInterval;
            validityPeriod = builder.validityPeriod;
            numberOfRepeatsAllowed = builder.numberOfRepeatsAllowed;
            quantity = builder.quantity;
            expectedSupplyDuration = builder.expectedSupplyDuration;
            dispenser = builder.dispenser;
            dispenserInstruction = Collections.unmodifiableList(builder.dispenserInstruction);
            doseAdministrationAid = builder.doseAdministrationAid;
        }

        /**
         * Indicates the quantity or duration for the first dispense of the medication.
         * 
         * @return
         *     An immutable object of type {@link InitialFill} that may be null.
         */
        public InitialFill getInitialFill() {
            return initialFill;
        }

        /**
         * The minimum period of time that must occur between dispenses of the medication.
         * 
         * @return
         *     An immutable object of type {@link Duration} that may be null.
         */
        public Duration getDispenseInterval() {
            return dispenseInterval;
        }

        /**
         * This indicates the validity period of a prescription (stale dating the Prescription).
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getValidityPeriod() {
            return validityPeriod;
        }

        /**
         * An integer indicating the number of times, in addition to the original dispense, (aka refills or repeats) that the 
         * patient can receive the prescribed medication. Usage Notes: This integer does not include the original order dispense. 
         * This means that if an order indicates dispense 30 tablets plus "3 repeats", then the order can be dispensed a total of 
         * 4 times and the patient can receive a total of 120 tablets. A prescriber may explicitly say that zero refills are 
         * permitted after the initial dispense.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getNumberOfRepeatsAllowed() {
            return numberOfRepeatsAllowed;
        }

        /**
         * The amount that is to be dispensed for one fill.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getQuantity() {
            return quantity;
        }

        /**
         * Identifies the period time over which the supplied product is expected to be used, or the length of time the dispense 
         * is expected to last.
         * 
         * @return
         *     An immutable object of type {@link Duration} that may be null.
         */
        public Duration getExpectedSupplyDuration() {
            return expectedSupplyDuration;
        }

        /**
         * Indicates the intended performing Organization that will dispense the medication as specified by the prescriber.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getDispenser() {
            return dispenser;
        }

        /**
         * Provides additional information to the dispenser, for example, counselling to be provided to the patient.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
         */
        public List<Annotation> getDispenserInstruction() {
            return dispenserInstruction;
        }

        /**
         * Provides information about the type of adherence packaging to be supplied for the medication dispense.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getDoseAdministrationAid() {
            return doseAdministrationAid;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (initialFill != null) || 
                (dispenseInterval != null) || 
                (validityPeriod != null) || 
                (numberOfRepeatsAllowed != null) || 
                (quantity != null) || 
                (expectedSupplyDuration != null) || 
                (dispenser != null) || 
                !dispenserInstruction.isEmpty() || 
                (doseAdministrationAid != null);
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
                    accept(initialFill, "initialFill", visitor);
                    accept(dispenseInterval, "dispenseInterval", visitor);
                    accept(validityPeriod, "validityPeriod", visitor);
                    accept(numberOfRepeatsAllowed, "numberOfRepeatsAllowed", visitor);
                    accept(quantity, "quantity", visitor);
                    accept(expectedSupplyDuration, "expectedSupplyDuration", visitor);
                    accept(dispenser, "dispenser", visitor);
                    accept(dispenserInstruction, "dispenserInstruction", visitor, Annotation.class);
                    accept(doseAdministrationAid, "doseAdministrationAid", visitor);
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
            DispenseRequest other = (DispenseRequest) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(initialFill, other.initialFill) && 
                Objects.equals(dispenseInterval, other.dispenseInterval) && 
                Objects.equals(validityPeriod, other.validityPeriod) && 
                Objects.equals(numberOfRepeatsAllowed, other.numberOfRepeatsAllowed) && 
                Objects.equals(quantity, other.quantity) && 
                Objects.equals(expectedSupplyDuration, other.expectedSupplyDuration) && 
                Objects.equals(dispenser, other.dispenser) && 
                Objects.equals(dispenserInstruction, other.dispenserInstruction) && 
                Objects.equals(doseAdministrationAid, other.doseAdministrationAid);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    initialFill, 
                    dispenseInterval, 
                    validityPeriod, 
                    numberOfRepeatsAllowed, 
                    quantity, 
                    expectedSupplyDuration, 
                    dispenser, 
                    dispenserInstruction, 
                    doseAdministrationAid);
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
            private InitialFill initialFill;
            private Duration dispenseInterval;
            private Period validityPeriod;
            private UnsignedInt numberOfRepeatsAllowed;
            private SimpleQuantity quantity;
            private Duration expectedSupplyDuration;
            private Reference dispenser;
            private List<Annotation> dispenserInstruction = new ArrayList<>();
            private CodeableConcept doseAdministrationAid;

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
             * Indicates the quantity or duration for the first dispense of the medication.
             * 
             * @param initialFill
             *     First fill details
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder initialFill(InitialFill initialFill) {
                this.initialFill = initialFill;
                return this;
            }

            /**
             * The minimum period of time that must occur between dispenses of the medication.
             * 
             * @param dispenseInterval
             *     Minimum period of time between dispenses
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dispenseInterval(Duration dispenseInterval) {
                this.dispenseInterval = dispenseInterval;
                return this;
            }

            /**
             * This indicates the validity period of a prescription (stale dating the Prescription).
             * 
             * @param validityPeriod
             *     Time period supply is authorized for
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder validityPeriod(Period validityPeriod) {
                this.validityPeriod = validityPeriod;
                return this;
            }

            /**
             * An integer indicating the number of times, in addition to the original dispense, (aka refills or repeats) that the 
             * patient can receive the prescribed medication. Usage Notes: This integer does not include the original order dispense. 
             * This means that if an order indicates dispense 30 tablets plus "3 repeats", then the order can be dispensed a total of 
             * 4 times and the patient can receive a total of 120 tablets. A prescriber may explicitly say that zero refills are 
             * permitted after the initial dispense.
             * 
             * @param numberOfRepeatsAllowed
             *     Number of refills authorized
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder numberOfRepeatsAllowed(UnsignedInt numberOfRepeatsAllowed) {
                this.numberOfRepeatsAllowed = numberOfRepeatsAllowed;
                return this;
            }

            /**
             * The amount that is to be dispensed for one fill.
             * 
             * @param quantity
             *     Amount of medication to supply per dispense
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(SimpleQuantity quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * Identifies the period time over which the supplied product is expected to be used, or the length of time the dispense 
             * is expected to last.
             * 
             * @param expectedSupplyDuration
             *     Number of days supply per dispense
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expectedSupplyDuration(Duration expectedSupplyDuration) {
                this.expectedSupplyDuration = expectedSupplyDuration;
                return this;
            }

            /**
             * Indicates the intended performing Organization that will dispense the medication as specified by the prescriber.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param dispenser
             *     Intended performer of dispense
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dispenser(Reference dispenser) {
                this.dispenser = dispenser;
                return this;
            }

            /**
             * Provides additional information to the dispenser, for example, counselling to be provided to the patient.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dispenserInstruction
             *     Additional information for the dispenser
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dispenserInstruction(Annotation... dispenserInstruction) {
                for (Annotation value : dispenserInstruction) {
                    this.dispenserInstruction.add(value);
                }
                return this;
            }

            /**
             * Provides additional information to the dispenser, for example, counselling to be provided to the patient.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dispenserInstruction
             *     Additional information for the dispenser
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder dispenserInstruction(Collection<Annotation> dispenserInstruction) {
                this.dispenserInstruction = new ArrayList<>(dispenserInstruction);
                return this;
            }

            /**
             * Provides information about the type of adherence packaging to be supplied for the medication dispense.
             * 
             * @param doseAdministrationAid
             *     Type of adherence packaging to use for the dispense
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder doseAdministrationAid(CodeableConcept doseAdministrationAid) {
                this.doseAdministrationAid = doseAdministrationAid;
                return this;
            }

            /**
             * Build the {@link DispenseRequest}
             * 
             * @return
             *     An immutable object of type {@link DispenseRequest}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid DispenseRequest per the base specification
             */
            @Override
            public DispenseRequest build() {
                DispenseRequest dispenseRequest = new DispenseRequest(this);
                if (validating) {
                    validate(dispenseRequest);
                }
                return dispenseRequest;
            }

            protected void validate(DispenseRequest dispenseRequest) {
                super.validate(dispenseRequest);
                ValidationSupport.checkList(dispenseRequest.dispenserInstruction, "dispenserInstruction", Annotation.class);
                ValidationSupport.checkReferenceType(dispenseRequest.dispenser, "dispenser", "Organization");
                ValidationSupport.requireValueOrChildren(dispenseRequest);
            }

            protected Builder from(DispenseRequest dispenseRequest) {
                super.from(dispenseRequest);
                initialFill = dispenseRequest.initialFill;
                dispenseInterval = dispenseRequest.dispenseInterval;
                validityPeriod = dispenseRequest.validityPeriod;
                numberOfRepeatsAllowed = dispenseRequest.numberOfRepeatsAllowed;
                quantity = dispenseRequest.quantity;
                expectedSupplyDuration = dispenseRequest.expectedSupplyDuration;
                dispenser = dispenseRequest.dispenser;
                dispenserInstruction.addAll(dispenseRequest.dispenserInstruction);
                doseAdministrationAid = dispenseRequest.doseAdministrationAid;
                return this;
            }
        }

        /**
         * Indicates the quantity or duration for the first dispense of the medication.
         */
        public static class InitialFill extends BackboneElement {
            private final SimpleQuantity quantity;
            private final Duration duration;

            private InitialFill(Builder builder) {
                super(builder);
                quantity = builder.quantity;
                duration = builder.duration;
            }

            /**
             * The amount or quantity to provide as part of the first dispense.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getQuantity() {
                return quantity;
            }

            /**
             * The length of time that the first dispense is expected to last.
             * 
             * @return
             *     An immutable object of type {@link Duration} that may be null.
             */
            public Duration getDuration() {
                return duration;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (quantity != null) || 
                    (duration != null);
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
                        accept(quantity, "quantity", visitor);
                        accept(duration, "duration", visitor);
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
                InitialFill other = (InitialFill) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(duration, other.duration);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        quantity, 
                        duration);
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
                private SimpleQuantity quantity;
                private Duration duration;

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
                 * The amount or quantity to provide as part of the first dispense.
                 * 
                 * @param quantity
                 *     First fill quantity
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(SimpleQuantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * The length of time that the first dispense is expected to last.
                 * 
                 * @param duration
                 *     First fill duration
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder duration(Duration duration) {
                    this.duration = duration;
                    return this;
                }

                /**
                 * Build the {@link InitialFill}
                 * 
                 * @return
                 *     An immutable object of type {@link InitialFill}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid InitialFill per the base specification
                 */
                @Override
                public InitialFill build() {
                    InitialFill initialFill = new InitialFill(this);
                    if (validating) {
                        validate(initialFill);
                    }
                    return initialFill;
                }

                protected void validate(InitialFill initialFill) {
                    super.validate(initialFill);
                    ValidationSupport.requireValueOrChildren(initialFill);
                }

                protected Builder from(InitialFill initialFill) {
                    super.from(initialFill);
                    quantity = initialFill.quantity;
                    duration = initialFill.duration;
                    return this;
                }
            }
        }
    }

    /**
     * Indicates whether or not substitution can or should be part of the dispense. In some cases, substitution must happen, 
     * in other cases substitution must not happen. This block explains the prescriber's intent. If nothing is specified 
     * substitution may be done.
     */
    public static class Substitution extends BackboneElement {
        @Choice({ Boolean.class, CodeableConcept.class })
        @Binding(
            bindingName = "MedicationRequestSubstitution",
            strength = BindingStrength.Value.PREFERRED,
            description = "Identifies the type of substitution allowed.",
            valueSet = "http://terminology.hl7.org/ValueSet/v3-ActSubstanceAdminSubstitutionCode"
        )
        @Required
        private final Element allowed;
        @Binding(
            bindingName = "MedicationIntendedSubstitutionReason",
            strength = BindingStrength.Value.EXAMPLE,
            description = "SubstanceAdminSubstitutionReason",
            valueSet = "http://terminology.hl7.org/ValueSet/v3-SubstanceAdminSubstitutionReason"
        )
        private final CodeableConcept reason;

        private Substitution(Builder builder) {
            super(builder);
            allowed = builder.allowed;
            reason = builder.reason;
        }

        /**
         * True if the prescriber allows a different drug to be dispensed from what was prescribed.
         * 
         * @return
         *     An immutable object of type {@link Boolean} or {@link CodeableConcept} that is non-null.
         */
        public Element getAllowed() {
            return allowed;
        }

        /**
         * Indicates the reason for the substitution, or why substitution must or must not be performed.
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
                (allowed != null) || 
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
                    accept(allowed, "allowed", visitor);
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
            Substitution other = (Substitution) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(allowed, other.allowed) && 
                Objects.equals(reason, other.reason);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    allowed, 
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
            private Element allowed;
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
             * Convenience method for setting {@code allowed} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param allowed
             *     Whether substitution is allowed or not
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #allowed(Element)
             */
            public Builder allowed(java.lang.Boolean allowed) {
                this.allowed = (allowed == null) ? null : Boolean.of(allowed);
                return this;
            }

            /**
             * True if the prescriber allows a different drug to be dispensed from what was prescribed.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Boolean}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * @param allowed
             *     Whether substitution is allowed or not
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder allowed(Element allowed) {
                this.allowed = allowed;
                return this;
            }

            /**
             * Indicates the reason for the substitution, or why substitution must or must not be performed.
             * 
             * @param reason
             *     Why should (not) substitution be made
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reason(CodeableConcept reason) {
                this.reason = reason;
                return this;
            }

            /**
             * Build the {@link Substitution}
             * 
             * <p>Required elements:
             * <ul>
             * <li>allowed</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Substitution}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Substitution per the base specification
             */
            @Override
            public Substitution build() {
                Substitution substitution = new Substitution(this);
                if (validating) {
                    validate(substitution);
                }
                return substitution;
            }

            protected void validate(Substitution substitution) {
                super.validate(substitution);
                ValidationSupport.requireChoiceElement(substitution.allowed, "allowed", Boolean.class, CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(substitution);
            }

            protected Builder from(Substitution substitution) {
                super.from(substitution);
                allowed = substitution.allowed;
                reason = substitution.reason;
                return this;
            }
        }
    }
}
