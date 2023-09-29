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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.VirtualServiceDetail;
import org.linuxforhealth.fhir.model.r5.type.code.AppointmentStatus;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ParticipationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A booking of a healthcare event among patient(s), practitioner(s), related person(s) and/or device(s) for a specific 
 * date/time. This may result in one or more Encounter(s).
 * 
 * <p>Maturity level: FMM3 (Trial Use)
 */
@Maturity(
    level = 3,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "app-1",
    level = "Rule",
    location = "Appointment.participant",
    description = "Either the type or actor on the participant SHALL be specified",
    expression = "type.exists() or actor.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-2",
    level = "Rule",
    location = "(base)",
    description = "Either start and end are specified, or neither",
    expression = "start.exists() = end.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-3",
    level = "Rule",
    location = "(base)",
    description = "Only proposed or cancelled appointments can be missing start/end dates",
    expression = "(start.exists() and end.exists()) or (status in ('proposed' | 'cancelled' | 'waitlist'))",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-4",
    level = "Rule",
    location = "(base)",
    description = "Cancellation reason is only used for appointments that have been cancelled, or noshow",
    expression = "cancellationReason.exists() implies (status='noshow' or status='cancelled')",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-5",
    level = "Rule",
    location = "(base)",
    description = "The start must be less than or equal to the end",
    expression = "start.exists() implies start <= end",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-6",
    level = "Warning",
    location = "(base)",
    description = "An appointment may have an originatingAppointment or recurrenceTemplate, but not both",
    expression = "originatingAppointment.exists().not() or recurrenceTemplate.exists().not()",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "app-7",
    level = "Rule",
    location = "(base)",
    description = "Cancellation date is only used for appointments that have been cancelled, or noshow",
    expression = "cancellationDate.exists() implies (status='noshow' or status='cancelled')",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment"
)
@Constraint(
    id = "appointment-8",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/EncounterClass",
    expression = "class.exists() implies (class.all(memberOf('http://terminology.hl7.org/ValueSet/EncounterClass', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Constraint(
    id = "appointment-9",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/c80-practice-codes",
    expression = "specialty.exists() implies (specialty.all(memberOf('http://hl7.org/fhir/ValueSet/c80-practice-codes', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Constraint(
    id = "appointment-10",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/v2-0276",
    expression = "appointmentType.exists() implies (appointmentType.memberOf('http://terminology.hl7.org/ValueSet/v2-0276', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Constraint(
    id = "appointment-11",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-reason",
    expression = "reason.exists() implies (reason.all(memberOf('http://hl7.org/fhir/ValueSet/encounter-reason', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Constraint(
    id = "appointment-12",
    level = "Warning",
    location = "participant.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/encounter-participant-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-participant-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Constraint(
    id = "appointment-13",
    level = "Warning",
    location = "recurrenceTemplate.recurrenceType",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/appointment-recurrrence-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/appointment-recurrrence-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Appointment",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Appointment extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "AppointmentStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The free/busy status of an appointment.",
        valueSet = "http://hl7.org/fhir/ValueSet/appointmentstatus|5.0.0"
    )
    @Required
    private final AppointmentStatus status;
    @Summary
    @Binding(
        bindingName = "cancellation-reason",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/appointment-cancellation-reason"
    )
    private final CodeableConcept cancellationReason;
    @Summary
    @Binding(
        bindingName = "EncounterClass",
        strength = BindingStrength.Value.PREFERRED,
        description = "Classification of the encounter.",
        valueSet = "http://terminology.hl7.org/ValueSet/EncounterClass"
    )
    private final List<CodeableConcept> clazz;
    @Summary
    @Binding(
        bindingName = "service-category",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/service-category"
    )
    private final List<CodeableConcept> serviceCategory;
    @Summary
    @Binding(
        bindingName = "service-type",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/service-type"
    )
    private final List<CodeableReference> serviceType;
    @Summary
    @Binding(
        bindingName = "specialty",
        strength = BindingStrength.Value.PREFERRED,
        valueSet = "http://hl7.org/fhir/ValueSet/c80-practice-codes"
    )
    private final List<CodeableConcept> specialty;
    @Summary
    @Binding(
        bindingName = "appointment-type",
        strength = BindingStrength.Value.PREFERRED,
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0276"
    )
    private final CodeableConcept appointmentType;
    @Summary
    @Binding(
        bindingName = "ApptReason",
        strength = BindingStrength.Value.PREFERRED,
        description = "The Reason for the appointment to take place.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-reason"
    )
    private final List<CodeableReference> reason;
    @Binding(
        bindingName = "Priority",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Indicates the urgency of the appointment.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ActPriority"
    )
    private final CodeableConcept priority;
    private final String description;
    @ReferenceTarget({ "Appointment" })
    private final List<Reference> replaces;
    private final List<VirtualServiceDetail> virtualService;
    private final List<Reference> supportingInformation;
    @ReferenceTarget({ "Appointment" })
    private final Reference previousAppointment;
    @ReferenceTarget({ "Appointment" })
    private final Reference originatingAppointment;
    @Summary
    private final Instant start;
    @Summary
    private final Instant end;
    private final PositiveInt minutesDuration;
    private final List<Period> requestedPeriod;
    @ReferenceTarget({ "Slot" })
    private final List<Reference> slot;
    @ReferenceTarget({ "Account" })
    private final List<Reference> account;
    private final DateTime created;
    private final DateTime cancellationDate;
    private final List<Annotation> note;
    private final List<CodeableReference> patientInstruction;
    @ReferenceTarget({ "CarePlan", "DeviceRequest", "MedicationRequest", "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    private final Reference subject;
    @Required
    private final List<Participant> participant;
    private final PositiveInt recurrenceId;
    private final Boolean occurrenceChanged;
    private final List<RecurrenceTemplate> recurrenceTemplate;

    private Appointment(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        cancellationReason = builder.cancellationReason;
        clazz = Collections.unmodifiableList(builder.clazz);
        serviceCategory = Collections.unmodifiableList(builder.serviceCategory);
        serviceType = Collections.unmodifiableList(builder.serviceType);
        specialty = Collections.unmodifiableList(builder.specialty);
        appointmentType = builder.appointmentType;
        reason = Collections.unmodifiableList(builder.reason);
        priority = builder.priority;
        description = builder.description;
        replaces = Collections.unmodifiableList(builder.replaces);
        virtualService = Collections.unmodifiableList(builder.virtualService);
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        previousAppointment = builder.previousAppointment;
        originatingAppointment = builder.originatingAppointment;
        start = builder.start;
        end = builder.end;
        minutesDuration = builder.minutesDuration;
        requestedPeriod = Collections.unmodifiableList(builder.requestedPeriod);
        slot = Collections.unmodifiableList(builder.slot);
        account = Collections.unmodifiableList(builder.account);
        created = builder.created;
        cancellationDate = builder.cancellationDate;
        note = Collections.unmodifiableList(builder.note);
        patientInstruction = Collections.unmodifiableList(builder.patientInstruction);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        subject = builder.subject;
        participant = Collections.unmodifiableList(builder.participant);
        recurrenceId = builder.recurrenceId;
        occurrenceChanged = builder.occurrenceChanged;
        recurrenceTemplate = Collections.unmodifiableList(builder.recurrenceTemplate);
    }

    /**
     * This records identifiers associated with this appointment concern that are defined by business processes and/or used 
     * to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in 
     * written / printed documentation).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The overall status of the Appointment. Each of the participants has their own participation status which indicates 
     * their involvement in the process, however this status indicates the shared status.
     * 
     * @return
     *     An immutable object of type {@link AppointmentStatus} that is non-null.
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * The coded reason for the appointment being cancelled. This is often used in reporting/billing/futher processing to 
     * determine if further actions are required, or specific fees apply.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Concepts representing classification of patient encounter such as ambulatory (outpatient), inpatient, emergency, home 
     * health or others due to local variations.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getClazz() {
        return clazz;
    }

    /**
     * A broad categorization of the service that is to be performed during this appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getServiceCategory() {
        return serviceCategory;
    }

    /**
     * The specific service that is to be performed during this appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getServiceType() {
        return serviceType;
    }

    /**
     * The specialty of a practitioner that would be required to perform the service requested in this appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSpecialty() {
        return specialty;
    }

    /**
     * The style of appointment or patient that has been booked in the slot (not service type).
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getAppointmentType() {
        return appointmentType;
    }

    /**
     * The reason that this appointment is being scheduled. This is more clinical than administrative. This can be coded, or 
     * as specified using information from another resource. When the patient arrives and the encounter begins it may be used 
     * as the admission diagnosis. The indication will typically be a Condition (with other resources referenced in the 
     * evidence.detail), or a Procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * The priority of the appointment. Can be used to make informed decisions if needing to re-prioritize appointments. (The 
     * iCal Standard specifies 0 as undefined, 1 as highest, 9 as lowest priority).
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPriority() {
        return priority;
    }

    /**
     * The brief description of the appointment as would be shown on a subject line in a meeting request, or appointment 
     * list. Detailed or expanded information should be put in the note field.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Appointment replaced by this Appointment in cases where there is a cancellation, the details of the cancellation can 
     * be found in the cancellationReason property (on the referenced resource).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReplaces() {
        return replaces;
    }

    /**
     * Connection details of a virtual service (e.g. conference call).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link VirtualServiceDetail} that may be empty.
     */
    public List<VirtualServiceDetail> getVirtualService() {
        return virtualService;
    }

    /**
     * Additional information to support the appointment provided when making the appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * The previous appointment in a series of related appointments.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPreviousAppointment() {
        return previousAppointment;
    }

    /**
     * The originating appointment in a recurring set of related appointments.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOriginatingAppointment() {
        return originatingAppointment;
    }

    /**
     * Date/Time that the appointment is to take place.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getStart() {
        return start;
    }

    /**
     * Date/Time that the appointment is to conclude.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getEnd() {
        return end;
    }

    /**
     * Number of minutes that the appointment is to take. This can be less than the duration between the start and end times. 
     * For example, where the actual time of appointment is only an estimate or if a 30 minute appointment is being 
     * requested, but any time would work. Also, if there is, for example, a planned 15 minute break in the middle of a long 
     * appointment, the duration may be 15 minutes less than the difference between the start and end.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getMinutesDuration() {
        return minutesDuration;
    }

    /**
     * A set of date ranges (potentially including times) that the appointment is preferred to be scheduled within.

The 
     * duration (usually in minutes) could also be provided to indicate the length of the appointment to fill and populate 
     * the start/end times for the actual allocated time. However, in other situations the duration may be calculated by the 
     * scheduling system.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Period} that may be empty.
     */
    public List<Period> getRequestedPeriod() {
        return requestedPeriod;
    }

    /**
     * The slots from the participants' schedules that will be filled by the appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSlot() {
        return slot;
    }

    /**
     * The set of accounts that is expected to be used for billing the activities that result from this Appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAccount() {
        return account;
    }

    /**
     * The date that this appointment was initially created. This could be different to the meta.lastModified value on the 
     * initial entry, as this could have been before the resource was created on the FHIR server, and should remain unchanged 
     * over the lifespan of the appointment.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getCreated() {
        return created;
    }

    /**
     * The date/time describing when the appointment was cancelled.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getCancellationDate() {
        return cancellationDate;
    }

    /**
     * Additional notes/comments about the appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * While Appointment.note contains information for internal use, Appointment.patientInstructions is used to capture 
     * patient facing information about the Appointment (e.g. please bring your referral or fast from 8pm night before).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getPatientInstruction() {
        return patientInstruction;
    }

    /**
     * The request this appointment is allocated to assess (e.g. incoming referral or procedure request).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The patient or group associated with the appointment, if they are to be present (usually) then they should also be 
     * included in the participant backbone element.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * List of participants involved in the appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Participant} that is non-empty.
     */
    public List<Participant> getParticipant() {
        return participant;
    }

    /**
     * The sequence number that identifies a specific appointment in a recurring pattern.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getRecurrenceId() {
        return recurrenceId;
    }

    /**
     * This appointment varies from the recurring pattern.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getOccurrenceChanged() {
        return occurrenceChanged;
    }

    /**
     * The details of the recurrence pattern or template that is used to generate recurring appointments.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RecurrenceTemplate} that may be empty.
     */
    public List<RecurrenceTemplate> getRecurrenceTemplate() {
        return recurrenceTemplate;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (cancellationReason != null) || 
            !clazz.isEmpty() || 
            !serviceCategory.isEmpty() || 
            !serviceType.isEmpty() || 
            !specialty.isEmpty() || 
            (appointmentType != null) || 
            !reason.isEmpty() || 
            (priority != null) || 
            (description != null) || 
            !replaces.isEmpty() || 
            !virtualService.isEmpty() || 
            !supportingInformation.isEmpty() || 
            (previousAppointment != null) || 
            (originatingAppointment != null) || 
            (start != null) || 
            (end != null) || 
            (minutesDuration != null) || 
            !requestedPeriod.isEmpty() || 
            !slot.isEmpty() || 
            !account.isEmpty() || 
            (created != null) || 
            (cancellationDate != null) || 
            !note.isEmpty() || 
            !patientInstruction.isEmpty() || 
            !basedOn.isEmpty() || 
            (subject != null) || 
            !participant.isEmpty() || 
            (recurrenceId != null) || 
            (occurrenceChanged != null) || 
            !recurrenceTemplate.isEmpty();
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
                accept(cancellationReason, "cancellationReason", visitor);
                accept(clazz, "class", visitor, CodeableConcept.class);
                accept(serviceCategory, "serviceCategory", visitor, CodeableConcept.class);
                accept(serviceType, "serviceType", visitor, CodeableReference.class);
                accept(specialty, "specialty", visitor, CodeableConcept.class);
                accept(appointmentType, "appointmentType", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(priority, "priority", visitor);
                accept(description, "description", visitor);
                accept(replaces, "replaces", visitor, Reference.class);
                accept(virtualService, "virtualService", visitor, VirtualServiceDetail.class);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(previousAppointment, "previousAppointment", visitor);
                accept(originatingAppointment, "originatingAppointment", visitor);
                accept(start, "start", visitor);
                accept(end, "end", visitor);
                accept(minutesDuration, "minutesDuration", visitor);
                accept(requestedPeriod, "requestedPeriod", visitor, Period.class);
                accept(slot, "slot", visitor, Reference.class);
                accept(account, "account", visitor, Reference.class);
                accept(created, "created", visitor);
                accept(cancellationDate, "cancellationDate", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(patientInstruction, "patientInstruction", visitor, CodeableReference.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(subject, "subject", visitor);
                accept(participant, "participant", visitor, Participant.class);
                accept(recurrenceId, "recurrenceId", visitor);
                accept(occurrenceChanged, "occurrenceChanged", visitor);
                accept(recurrenceTemplate, "recurrenceTemplate", visitor, RecurrenceTemplate.class);
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
        Appointment other = (Appointment) obj;
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
            Objects.equals(cancellationReason, other.cancellationReason) && 
            Objects.equals(clazz, other.clazz) && 
            Objects.equals(serviceCategory, other.serviceCategory) && 
            Objects.equals(serviceType, other.serviceType) && 
            Objects.equals(specialty, other.specialty) && 
            Objects.equals(appointmentType, other.appointmentType) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(description, other.description) && 
            Objects.equals(replaces, other.replaces) && 
            Objects.equals(virtualService, other.virtualService) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(previousAppointment, other.previousAppointment) && 
            Objects.equals(originatingAppointment, other.originatingAppointment) && 
            Objects.equals(start, other.start) && 
            Objects.equals(end, other.end) && 
            Objects.equals(minutesDuration, other.minutesDuration) && 
            Objects.equals(requestedPeriod, other.requestedPeriod) && 
            Objects.equals(slot, other.slot) && 
            Objects.equals(account, other.account) && 
            Objects.equals(created, other.created) && 
            Objects.equals(cancellationDate, other.cancellationDate) && 
            Objects.equals(note, other.note) && 
            Objects.equals(patientInstruction, other.patientInstruction) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(participant, other.participant) && 
            Objects.equals(recurrenceId, other.recurrenceId) && 
            Objects.equals(occurrenceChanged, other.occurrenceChanged) && 
            Objects.equals(recurrenceTemplate, other.recurrenceTemplate);
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
                cancellationReason, 
                clazz, 
                serviceCategory, 
                serviceType, 
                specialty, 
                appointmentType, 
                reason, 
                priority, 
                description, 
                replaces, 
                virtualService, 
                supportingInformation, 
                previousAppointment, 
                originatingAppointment, 
                start, 
                end, 
                minutesDuration, 
                requestedPeriod, 
                slot, 
                account, 
                created, 
                cancellationDate, 
                note, 
                patientInstruction, 
                basedOn, 
                subject, 
                participant, 
                recurrenceId, 
                occurrenceChanged, 
                recurrenceTemplate);
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
        private AppointmentStatus status;
        private CodeableConcept cancellationReason;
        private List<CodeableConcept> clazz = new ArrayList<>();
        private List<CodeableConcept> serviceCategory = new ArrayList<>();
        private List<CodeableReference> serviceType = new ArrayList<>();
        private List<CodeableConcept> specialty = new ArrayList<>();
        private CodeableConcept appointmentType;
        private List<CodeableReference> reason = new ArrayList<>();
        private CodeableConcept priority;
        private String description;
        private List<Reference> replaces = new ArrayList<>();
        private List<VirtualServiceDetail> virtualService = new ArrayList<>();
        private List<Reference> supportingInformation = new ArrayList<>();
        private Reference previousAppointment;
        private Reference originatingAppointment;
        private Instant start;
        private Instant end;
        private PositiveInt minutesDuration;
        private List<Period> requestedPeriod = new ArrayList<>();
        private List<Reference> slot = new ArrayList<>();
        private List<Reference> account = new ArrayList<>();
        private DateTime created;
        private DateTime cancellationDate;
        private List<Annotation> note = new ArrayList<>();
        private List<CodeableReference> patientInstruction = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private Reference subject;
        private List<Participant> participant = new ArrayList<>();
        private PositiveInt recurrenceId;
        private Boolean occurrenceChanged;
        private List<RecurrenceTemplate> recurrenceTemplate = new ArrayList<>();

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
         * This records identifiers associated with this appointment concern that are defined by business processes and/or used 
         * to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in 
         * written / printed documentation).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this item
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
         * This records identifiers associated with this appointment concern that are defined by business processes and/or used 
         * to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in 
         * written / printed documentation).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this item
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
         * The overall status of the Appointment. Each of the participants has their own participation status which indicates 
         * their involvement in the process, however this status indicates the shared status.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     proposed | pending | booked | arrived | fulfilled | cancelled | noshow | entered-in-error | checked-in | waitlist
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The coded reason for the appointment being cancelled. This is often used in reporting/billing/futher processing to 
         * determine if further actions are required, or specific fees apply.
         * 
         * @param cancellationReason
         *     The coded reason for the appointment being cancelled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder cancellationReason(CodeableConcept cancellationReason) {
            this.cancellationReason = cancellationReason;
            return this;
        }

        /**
         * Concepts representing classification of patient encounter such as ambulatory (outpatient), inpatient, emergency, home 
         * health or others due to local variations.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param clazz
         *     Classification when becoming an encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clazz(CodeableConcept... clazz) {
            for (CodeableConcept value : clazz) {
                this.clazz.add(value);
            }
            return this;
        }

        /**
         * Concepts representing classification of patient encounter such as ambulatory (outpatient), inpatient, emergency, home 
         * health or others due to local variations.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param clazz
         *     Classification when becoming an encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder clazz(Collection<CodeableConcept> clazz) {
            this.clazz = new ArrayList<>(clazz);
            return this;
        }

        /**
         * A broad categorization of the service that is to be performed during this appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceCategory
         *     A broad categorization of the service that is to be performed during this appointment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder serviceCategory(CodeableConcept... serviceCategory) {
            for (CodeableConcept value : serviceCategory) {
                this.serviceCategory.add(value);
            }
            return this;
        }

        /**
         * A broad categorization of the service that is to be performed during this appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceCategory
         *     A broad categorization of the service that is to be performed during this appointment
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder serviceCategory(Collection<CodeableConcept> serviceCategory) {
            this.serviceCategory = new ArrayList<>(serviceCategory);
            return this;
        }

        /**
         * The specific service that is to be performed during this appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     The specific service that is to be performed during this appointment
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
         * The specific service that is to be performed during this appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     The specific service that is to be performed during this appointment
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
         * The specialty of a practitioner that would be required to perform the service requested in this appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     The specialty of a practitioner that would be required to perform the service requested in this appointment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specialty(CodeableConcept... specialty) {
            for (CodeableConcept value : specialty) {
                this.specialty.add(value);
            }
            return this;
        }

        /**
         * The specialty of a practitioner that would be required to perform the service requested in this appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     The specialty of a practitioner that would be required to perform the service requested in this appointment
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specialty(Collection<CodeableConcept> specialty) {
            this.specialty = new ArrayList<>(specialty);
            return this;
        }

        /**
         * The style of appointment or patient that has been booked in the slot (not service type).
         * 
         * @param appointmentType
         *     The style of appointment or patient that has been booked in the slot (not service type)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder appointmentType(CodeableConcept appointmentType) {
            this.appointmentType = appointmentType;
            return this;
        }

        /**
         * The reason that this appointment is being scheduled. This is more clinical than administrative. This can be coded, or 
         * as specified using information from another resource. When the patient arrives and the encounter begins it may be used 
         * as the admission diagnosis. The indication will typically be a Condition (with other resources referenced in the 
         * evidence.detail), or a Procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason this appointment is scheduled
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
         * The reason that this appointment is being scheduled. This is more clinical than administrative. This can be coded, or 
         * as specified using information from another resource. When the patient arrives and the encounter begins it may be used 
         * as the admission diagnosis. The indication will typically be a Condition (with other resources referenced in the 
         * evidence.detail), or a Procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason this appointment is scheduled
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
         * The priority of the appointment. Can be used to make informed decisions if needing to re-prioritize appointments. (The 
         * iCal Standard specifies 0 as undefined, 1 as highest, 9 as lowest priority).
         * 
         * @param priority
         *     Used to make informed decisions if needing to re-prioritize
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(CodeableConcept priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Convenience method for setting {@code description}.
         * 
         * @param description
         *     Shown on a subject line in a meeting request, or appointment list
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
         * The brief description of the appointment as would be shown on a subject line in a meeting request, or appointment 
         * list. Detailed or expanded information should be put in the note field.
         * 
         * @param description
         *     Shown on a subject line in a meeting request, or appointment list
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Appointment replaced by this Appointment in cases where there is a cancellation, the details of the cancellation can 
         * be found in the cancellationReason property (on the referenced resource).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param replaces
         *     Appointment replaced by this Appointment
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
         * Appointment replaced by this Appointment in cases where there is a cancellation, the details of the cancellation can 
         * be found in the cancellationReason property (on the referenced resource).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param replaces
         *     Appointment replaced by this Appointment
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
         * Connection details of a virtual service (e.g. conference call).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param virtualService
         *     Connection details of a virtual service (e.g. conference call)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder virtualService(VirtualServiceDetail... virtualService) {
            for (VirtualServiceDetail value : virtualService) {
                this.virtualService.add(value);
            }
            return this;
        }

        /**
         * Connection details of a virtual service (e.g. conference call).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param virtualService
         *     Connection details of a virtual service (e.g. conference call)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder virtualService(Collection<VirtualServiceDetail> virtualService) {
            this.virtualService = new ArrayList<>(virtualService);
            return this;
        }

        /**
         * Additional information to support the appointment provided when making the appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information to support the appointment
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
         * Additional information to support the appointment provided when making the appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information to support the appointment
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
         * The previous appointment in a series of related appointments.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param previousAppointment
         *     The previous appointment in a series
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder previousAppointment(Reference previousAppointment) {
            this.previousAppointment = previousAppointment;
            return this;
        }

        /**
         * The originating appointment in a recurring set of related appointments.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param originatingAppointment
         *     The originating appointment in a recurring set of appointments
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder originatingAppointment(Reference originatingAppointment) {
            this.originatingAppointment = originatingAppointment;
            return this;
        }

        /**
         * Convenience method for setting {@code start}.
         * 
         * @param start
         *     When appointment is to take place
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #start(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder start(java.time.ZonedDateTime start) {
            this.start = (start == null) ? null : Instant.of(start);
            return this;
        }

        /**
         * Date/Time that the appointment is to take place.
         * 
         * @param start
         *     When appointment is to take place
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder start(Instant start) {
            this.start = start;
            return this;
        }

        /**
         * Convenience method for setting {@code end}.
         * 
         * @param end
         *     When appointment is to conclude
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #end(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder end(java.time.ZonedDateTime end) {
            this.end = (end == null) ? null : Instant.of(end);
            return this;
        }

        /**
         * Date/Time that the appointment is to conclude.
         * 
         * @param end
         *     When appointment is to conclude
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder end(Instant end) {
            this.end = end;
            return this;
        }

        /**
         * Number of minutes that the appointment is to take. This can be less than the duration between the start and end times. 
         * For example, where the actual time of appointment is only an estimate or if a 30 minute appointment is being 
         * requested, but any time would work. Also, if there is, for example, a planned 15 minute break in the middle of a long 
         * appointment, the duration may be 15 minutes less than the difference between the start and end.
         * 
         * @param minutesDuration
         *     Can be less than start/end (e.g. estimate)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder minutesDuration(PositiveInt minutesDuration) {
            this.minutesDuration = minutesDuration;
            return this;
        }

        /**
         * A set of date ranges (potentially including times) that the appointment is preferred to be scheduled within.

The 
         * duration (usually in minutes) could also be provided to indicate the length of the appointment to fill and populate 
         * the start/end times for the actual allocated time. However, in other situations the duration may be calculated by the 
         * scheduling system.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param requestedPeriod
         *     Potential date/time interval(s) requested to allocate the appointment within
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requestedPeriod(Period... requestedPeriod) {
            for (Period value : requestedPeriod) {
                this.requestedPeriod.add(value);
            }
            return this;
        }

        /**
         * A set of date ranges (potentially including times) that the appointment is preferred to be scheduled within.

The 
         * duration (usually in minutes) could also be provided to indicate the length of the appointment to fill and populate 
         * the start/end times for the actual allocated time. However, in other situations the duration may be calculated by the 
         * scheduling system.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param requestedPeriod
         *     Potential date/time interval(s) requested to allocate the appointment within
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder requestedPeriod(Collection<Period> requestedPeriod) {
            this.requestedPeriod = new ArrayList<>(requestedPeriod);
            return this;
        }

        /**
         * The slots from the participants' schedules that will be filled by the appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Slot}</li>
         * </ul>
         * 
         * @param slot
         *     The slots that this appointment is filling
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder slot(Reference... slot) {
            for (Reference value : slot) {
                this.slot.add(value);
            }
            return this;
        }

        /**
         * The slots from the participants' schedules that will be filled by the appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Slot}</li>
         * </ul>
         * 
         * @param slot
         *     The slots that this appointment is filling
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder slot(Collection<Reference> slot) {
            this.slot = new ArrayList<>(slot);
            return this;
        }

        /**
         * The set of accounts that is expected to be used for billing the activities that result from this Appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Account}</li>
         * </ul>
         * 
         * @param account
         *     The set of accounts that may be used for billing for this Appointment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder account(Reference... account) {
            for (Reference value : account) {
                this.account.add(value);
            }
            return this;
        }

        /**
         * The set of accounts that is expected to be used for billing the activities that result from this Appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Account}</li>
         * </ul>
         * 
         * @param account
         *     The set of accounts that may be used for billing for this Appointment
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder account(Collection<Reference> account) {
            this.account = new ArrayList<>(account);
            return this;
        }

        /**
         * The date that this appointment was initially created. This could be different to the meta.lastModified value on the 
         * initial entry, as this could have been before the resource was created on the FHIR server, and should remain unchanged 
         * over the lifespan of the appointment.
         * 
         * @param created
         *     The date that this appointment was initially created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder created(DateTime created) {
            this.created = created;
            return this;
        }

        /**
         * The date/time describing when the appointment was cancelled.
         * 
         * @param cancellationDate
         *     When the appointment was cancelled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder cancellationDate(DateTime cancellationDate) {
            this.cancellationDate = cancellationDate;
            return this;
        }

        /**
         * Additional notes/comments about the appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional comments
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
         * Additional notes/comments about the appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional comments
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
         * While Appointment.note contains information for internal use, Appointment.patientInstructions is used to capture 
         * patient facing information about the Appointment (e.g. please bring your referral or fast from 8pm night before).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientInstruction
         *     Detailed information and instructions for the patient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patientInstruction(CodeableReference... patientInstruction) {
            for (CodeableReference value : patientInstruction) {
                this.patientInstruction.add(value);
            }
            return this;
        }

        /**
         * While Appointment.note contains information for internal use, Appointment.patientInstructions is used to capture 
         * patient facing information about the Appointment (e.g. please bring your referral or fast from 8pm night before).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientInstruction
         *     Detailed information and instructions for the patient
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder patientInstruction(Collection<CodeableReference> patientInstruction) {
            this.patientInstruction = new ArrayList<>(patientInstruction);
            return this;
        }

        /**
         * The request this appointment is allocated to assess (e.g. incoming referral or procedure request).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     The request this appointment is allocated to assess
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
         * The request this appointment is allocated to assess (e.g. incoming referral or procedure request).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     The request this appointment is allocated to assess
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
         * The patient or group associated with the appointment, if they are to be present (usually) then they should also be 
         * included in the participant backbone element.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     The patient or group associated with the appointment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * List of participants involved in the appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param participant
         *     Participants involved in appointment
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
         * List of participants involved in the appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param participant
         *     Participants involved in appointment
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
         * The sequence number that identifies a specific appointment in a recurring pattern.
         * 
         * @param recurrenceId
         *     The sequence number in the recurrence
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recurrenceId(PositiveInt recurrenceId) {
            this.recurrenceId = recurrenceId;
            return this;
        }

        /**
         * Convenience method for setting {@code occurrenceChanged}.
         * 
         * @param occurrenceChanged
         *     Indicates that this appointment varies from a recurrence pattern
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #occurrenceChanged(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder occurrenceChanged(java.lang.Boolean occurrenceChanged) {
            this.occurrenceChanged = (occurrenceChanged == null) ? null : Boolean.of(occurrenceChanged);
            return this;
        }

        /**
         * This appointment varies from the recurring pattern.
         * 
         * @param occurrenceChanged
         *     Indicates that this appointment varies from a recurrence pattern
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrenceChanged(Boolean occurrenceChanged) {
            this.occurrenceChanged = occurrenceChanged;
            return this;
        }

        /**
         * The details of the recurrence pattern or template that is used to generate recurring appointments.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param recurrenceTemplate
         *     Details of the recurrence pattern/template used to generate occurrences
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recurrenceTemplate(RecurrenceTemplate... recurrenceTemplate) {
            for (RecurrenceTemplate value : recurrenceTemplate) {
                this.recurrenceTemplate.add(value);
            }
            return this;
        }

        /**
         * The details of the recurrence pattern or template that is used to generate recurring appointments.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param recurrenceTemplate
         *     Details of the recurrence pattern/template used to generate occurrences
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder recurrenceTemplate(Collection<RecurrenceTemplate> recurrenceTemplate) {
            this.recurrenceTemplate = new ArrayList<>(recurrenceTemplate);
            return this;
        }

        /**
         * Build the {@link Appointment}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>participant</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Appointment}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Appointment per the base specification
         */
        @Override
        public Appointment build() {
            Appointment appointment = new Appointment(this);
            if (validating) {
                validate(appointment);
            }
            return appointment;
        }

        protected void validate(Appointment appointment) {
            super.validate(appointment);
            ValidationSupport.checkList(appointment.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(appointment.status, "status");
            ValidationSupport.checkList(appointment.clazz, "class", CodeableConcept.class);
            ValidationSupport.checkList(appointment.serviceCategory, "serviceCategory", CodeableConcept.class);
            ValidationSupport.checkList(appointment.serviceType, "serviceType", CodeableReference.class);
            ValidationSupport.checkList(appointment.specialty, "specialty", CodeableConcept.class);
            ValidationSupport.checkList(appointment.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(appointment.replaces, "replaces", Reference.class);
            ValidationSupport.checkList(appointment.virtualService, "virtualService", VirtualServiceDetail.class);
            ValidationSupport.checkList(appointment.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.checkList(appointment.requestedPeriod, "requestedPeriod", Period.class);
            ValidationSupport.checkList(appointment.slot, "slot", Reference.class);
            ValidationSupport.checkList(appointment.account, "account", Reference.class);
            ValidationSupport.checkList(appointment.note, "note", Annotation.class);
            ValidationSupport.checkList(appointment.patientInstruction, "patientInstruction", CodeableReference.class);
            ValidationSupport.checkList(appointment.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkNonEmptyList(appointment.participant, "participant", Participant.class);
            ValidationSupport.checkList(appointment.recurrenceTemplate, "recurrenceTemplate", RecurrenceTemplate.class);
            ValidationSupport.checkReferenceType(appointment.replaces, "replaces", "Appointment");
            ValidationSupport.checkReferenceType(appointment.previousAppointment, "previousAppointment", "Appointment");
            ValidationSupport.checkReferenceType(appointment.originatingAppointment, "originatingAppointment", "Appointment");
            ValidationSupport.checkReferenceType(appointment.slot, "slot", "Slot");
            ValidationSupport.checkReferenceType(appointment.account, "account", "Account");
            ValidationSupport.checkReferenceType(appointment.basedOn, "basedOn", "CarePlan", "DeviceRequest", "MedicationRequest", "ServiceRequest");
            ValidationSupport.checkReferenceType(appointment.subject, "subject", "Patient", "Group");
        }

        protected Builder from(Appointment appointment) {
            super.from(appointment);
            identifier.addAll(appointment.identifier);
            status = appointment.status;
            cancellationReason = appointment.cancellationReason;
            clazz.addAll(appointment.clazz);
            serviceCategory.addAll(appointment.serviceCategory);
            serviceType.addAll(appointment.serviceType);
            specialty.addAll(appointment.specialty);
            appointmentType = appointment.appointmentType;
            reason.addAll(appointment.reason);
            priority = appointment.priority;
            description = appointment.description;
            replaces.addAll(appointment.replaces);
            virtualService.addAll(appointment.virtualService);
            supportingInformation.addAll(appointment.supportingInformation);
            previousAppointment = appointment.previousAppointment;
            originatingAppointment = appointment.originatingAppointment;
            start = appointment.start;
            end = appointment.end;
            minutesDuration = appointment.minutesDuration;
            requestedPeriod.addAll(appointment.requestedPeriod);
            slot.addAll(appointment.slot);
            account.addAll(appointment.account);
            created = appointment.created;
            cancellationDate = appointment.cancellationDate;
            note.addAll(appointment.note);
            patientInstruction.addAll(appointment.patientInstruction);
            basedOn.addAll(appointment.basedOn);
            subject = appointment.subject;
            participant.addAll(appointment.participant);
            recurrenceId = appointment.recurrenceId;
            occurrenceChanged = appointment.occurrenceChanged;
            recurrenceTemplate.addAll(appointment.recurrenceTemplate);
            return this;
        }
    }

    /**
     * List of participants involved in the appointment.
     */
    public static class Participant extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "ParticipantType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Role of participant in encounter.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-participant-type"
        )
        private final List<CodeableConcept> type;
        private final Period period;
        @Summary
        @ReferenceTarget({ "Patient", "Group", "Practitioner", "PractitionerRole", "CareTeam", "RelatedPerson", "Device", "HealthcareService", "Location" })
        private final Reference actor;
        @Summary
        private final Boolean required;
        @Summary
        @Binding(
            bindingName = "ParticipationStatus",
            strength = BindingStrength.Value.REQUIRED,
            description = "The Participation status of an appointment.",
            valueSet = "http://hl7.org/fhir/ValueSet/participationstatus|5.0.0"
        )
        @Required
        private final ParticipationStatus status;

        private Participant(Builder builder) {
            super(builder);
            type = Collections.unmodifiableList(builder.type);
            period = builder.period;
            actor = builder.actor;
            required = builder.required;
            status = builder.status;
        }

        /**
         * Role of participant in the appointment.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * Participation period of the actor.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        /**
         * The individual, device, location, or service participating in the appointment.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getActor() {
            return actor;
        }

        /**
         * Whether this participant is required to be present at the meeting. If false, the participant is optional.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getRequired() {
            return required;
        }

        /**
         * Participation status of the actor.
         * 
         * @return
         *     An immutable object of type {@link ParticipationStatus} that is non-null.
         */
        public ParticipationStatus getStatus() {
            return status;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !type.isEmpty() || 
                (period != null) || 
                (actor != null) || 
                (required != null) || 
                (status != null);
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
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(period, "period", visitor);
                    accept(actor, "actor", visitor);
                    accept(required, "required", visitor);
                    accept(status, "status", visitor);
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
                Objects.equals(period, other.period) && 
                Objects.equals(actor, other.actor) && 
                Objects.equals(required, other.required) && 
                Objects.equals(status, other.status);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    period, 
                    actor, 
                    required, 
                    status);
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
            private List<CodeableConcept> type = new ArrayList<>();
            private Period period;
            private Reference actor;
            private Boolean required;
            private ParticipationStatus status;

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
             * Role of participant in the appointment.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Role of participant in the appointment
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
             * Role of participant in the appointment.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Role of participant in the appointment
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
             * Participation period of the actor.
             * 
             * @param period
             *     Participation period of the actor
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * The individual, device, location, or service participating in the appointment.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Group}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link CareTeam}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Device}</li>
             * <li>{@link HealthcareService}</li>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param actor
             *     The individual, device, location, or service participating in the appointment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
                this.actor = actor;
                return this;
            }

            /**
             * Convenience method for setting {@code required}.
             * 
             * @param required
             *     The participant is required to attend (optional when false)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #required(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder required(java.lang.Boolean required) {
                this.required = (required == null) ? null : Boolean.of(required);
                return this;
            }

            /**
             * Whether this participant is required to be present at the meeting. If false, the participant is optional.
             * 
             * @param required
             *     The participant is required to attend (optional when false)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder required(Boolean required) {
                this.required = required;
                return this;
            }

            /**
             * Participation status of the actor.
             * 
             * <p>This element is required.
             * 
             * @param status
             *     accepted | declined | tentative | needs-action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder status(ParticipationStatus status) {
                this.status = status;
                return this;
            }

            /**
             * Build the {@link Participant}
             * 
             * <p>Required elements:
             * <ul>
             * <li>status</li>
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
                ValidationSupport.checkList(participant.type, "type", CodeableConcept.class);
                ValidationSupport.requireNonNull(participant.status, "status");
                ValidationSupport.checkReferenceType(participant.actor, "actor", "Patient", "Group", "Practitioner", "PractitionerRole", "CareTeam", "RelatedPerson", "Device", "HealthcareService", "Location");
                ValidationSupport.requireValueOrChildren(participant);
            }

            protected Builder from(Participant participant) {
                super.from(participant);
                type.addAll(participant.type);
                period = participant.period;
                actor = participant.actor;
                required = participant.required;
                status = participant.status;
                return this;
            }
        }
    }

    /**
     * The details of the recurrence pattern or template that is used to generate recurring appointments.
     */
    public static class RecurrenceTemplate extends BackboneElement {
        @Binding(
            bindingName = "IANATimezone",
            strength = BindingStrength.Value.REQUIRED,
            description = "IANA Timezones (BCP 175)",
            valueSet = "http://hl7.org/fhir/ValueSet/timezones|5.0.0"
        )
        private final CodeableConcept timezone;
        @Binding(
            bindingName = "AppointmentRecurrenceType",
            strength = BindingStrength.Value.PREFERRED,
            description = "IANA Timezones (BCP 175)",
            valueSet = "http://hl7.org/fhir/ValueSet/appointment-recurrrence-type"
        )
        @Required
        private final CodeableConcept recurrenceType;
        private final Date lastOccurrenceDate;
        private final PositiveInt occurrenceCount;
        private final List<Date> occurrenceDate;
        private final WeeklyTemplate weeklyTemplate;
        private final MonthlyTemplate monthlyTemplate;
        private final YearlyTemplate yearlyTemplate;
        private final List<Date> excludingDate;
        private final List<PositiveInt> excludingRecurrenceId;

        private RecurrenceTemplate(Builder builder) {
            super(builder);
            timezone = builder.timezone;
            recurrenceType = builder.recurrenceType;
            lastOccurrenceDate = builder.lastOccurrenceDate;
            occurrenceCount = builder.occurrenceCount;
            occurrenceDate = Collections.unmodifiableList(builder.occurrenceDate);
            weeklyTemplate = builder.weeklyTemplate;
            monthlyTemplate = builder.monthlyTemplate;
            yearlyTemplate = builder.yearlyTemplate;
            excludingDate = Collections.unmodifiableList(builder.excludingDate);
            excludingRecurrenceId = Collections.unmodifiableList(builder.excludingRecurrenceId);
        }

        /**
         * The timezone of the recurring appointment occurrences.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getTimezone() {
            return timezone;
        }

        /**
         * How often the appointment series should recur.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getRecurrenceType() {
            return recurrenceType;
        }

        /**
         * Recurring appointments will not occur after this date.
         * 
         * @return
         *     An immutable object of type {@link Date} that may be null.
         */
        public Date getLastOccurrenceDate() {
            return lastOccurrenceDate;
        }

        /**
         * How many appointments are planned in the recurrence.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getOccurrenceCount() {
            return occurrenceCount;
        }

        /**
         * The list of specific dates that will have appointments generated.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Date} that may be empty.
         */
        public List<Date> getOccurrenceDate() {
            return occurrenceDate;
        }

        /**
         * Information about weekly recurring appointments.
         * 
         * @return
         *     An immutable object of type {@link WeeklyTemplate} that may be null.
         */
        public WeeklyTemplate getWeeklyTemplate() {
            return weeklyTemplate;
        }

        /**
         * Information about monthly recurring appointments.
         * 
         * @return
         *     An immutable object of type {@link MonthlyTemplate} that may be null.
         */
        public MonthlyTemplate getMonthlyTemplate() {
            return monthlyTemplate;
        }

        /**
         * Information about yearly recurring appointments.
         * 
         * @return
         *     An immutable object of type {@link YearlyTemplate} that may be null.
         */
        public YearlyTemplate getYearlyTemplate() {
            return yearlyTemplate;
        }

        /**
         * Any dates, such as holidays, that should be excluded from the recurrence.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Date} that may be empty.
         */
        public List<Date> getExcludingDate() {
            return excludingDate;
        }

        /**
         * Any dates, such as holidays, that should be excluded from the recurrence.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getExcludingRecurrenceId() {
            return excludingRecurrenceId;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (timezone != null) || 
                (recurrenceType != null) || 
                (lastOccurrenceDate != null) || 
                (occurrenceCount != null) || 
                !occurrenceDate.isEmpty() || 
                (weeklyTemplate != null) || 
                (monthlyTemplate != null) || 
                (yearlyTemplate != null) || 
                !excludingDate.isEmpty() || 
                !excludingRecurrenceId.isEmpty();
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
                    accept(timezone, "timezone", visitor);
                    accept(recurrenceType, "recurrenceType", visitor);
                    accept(lastOccurrenceDate, "lastOccurrenceDate", visitor);
                    accept(occurrenceCount, "occurrenceCount", visitor);
                    accept(occurrenceDate, "occurrenceDate", visitor, Date.class);
                    accept(weeklyTemplate, "weeklyTemplate", visitor);
                    accept(monthlyTemplate, "monthlyTemplate", visitor);
                    accept(yearlyTemplate, "yearlyTemplate", visitor);
                    accept(excludingDate, "excludingDate", visitor, Date.class);
                    accept(excludingRecurrenceId, "excludingRecurrenceId", visitor, PositiveInt.class);
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
            RecurrenceTemplate other = (RecurrenceTemplate) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(timezone, other.timezone) && 
                Objects.equals(recurrenceType, other.recurrenceType) && 
                Objects.equals(lastOccurrenceDate, other.lastOccurrenceDate) && 
                Objects.equals(occurrenceCount, other.occurrenceCount) && 
                Objects.equals(occurrenceDate, other.occurrenceDate) && 
                Objects.equals(weeklyTemplate, other.weeklyTemplate) && 
                Objects.equals(monthlyTemplate, other.monthlyTemplate) && 
                Objects.equals(yearlyTemplate, other.yearlyTemplate) && 
                Objects.equals(excludingDate, other.excludingDate) && 
                Objects.equals(excludingRecurrenceId, other.excludingRecurrenceId);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    timezone, 
                    recurrenceType, 
                    lastOccurrenceDate, 
                    occurrenceCount, 
                    occurrenceDate, 
                    weeklyTemplate, 
                    monthlyTemplate, 
                    yearlyTemplate, 
                    excludingDate, 
                    excludingRecurrenceId);
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
            private CodeableConcept timezone;
            private CodeableConcept recurrenceType;
            private Date lastOccurrenceDate;
            private PositiveInt occurrenceCount;
            private List<Date> occurrenceDate = new ArrayList<>();
            private WeeklyTemplate weeklyTemplate;
            private MonthlyTemplate monthlyTemplate;
            private YearlyTemplate yearlyTemplate;
            private List<Date> excludingDate = new ArrayList<>();
            private List<PositiveInt> excludingRecurrenceId = new ArrayList<>();

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
             * The timezone of the recurring appointment occurrences.
             * 
             * @param timezone
             *     The timezone of the occurrences
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder timezone(CodeableConcept timezone) {
                this.timezone = timezone;
                return this;
            }

            /**
             * How often the appointment series should recur.
             * 
             * <p>This element is required.
             * 
             * @param recurrenceType
             *     The frequency of the recurrence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder recurrenceType(CodeableConcept recurrenceType) {
                this.recurrenceType = recurrenceType;
                return this;
            }

            /**
             * Convenience method for setting {@code lastOccurrenceDate}.
             * 
             * @param lastOccurrenceDate
             *     The date when the recurrence should end
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #lastOccurrenceDate(org.linuxforhealth.fhir.model.type.Date)
             */
            public Builder lastOccurrenceDate(java.time.LocalDate lastOccurrenceDate) {
                this.lastOccurrenceDate = (lastOccurrenceDate == null) ? null : Date.of(lastOccurrenceDate);
                return this;
            }

            /**
             * Recurring appointments will not occur after this date.
             * 
             * @param lastOccurrenceDate
             *     The date when the recurrence should end
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder lastOccurrenceDate(Date lastOccurrenceDate) {
                this.lastOccurrenceDate = lastOccurrenceDate;
                return this;
            }

            /**
             * How many appointments are planned in the recurrence.
             * 
             * @param occurrenceCount
             *     The number of planned occurrences
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder occurrenceCount(PositiveInt occurrenceCount) {
                this.occurrenceCount = occurrenceCount;
                return this;
            }

            /**
             * Convenience method for setting {@code occurrenceDate}.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param occurrenceDate
             *     Specific dates for a recurring set of appointments (no template)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #occurrenceDate(org.linuxforhealth.fhir.model.type.Date)
             */
            public Builder occurrenceDate(java.time.LocalDate... occurrenceDate) {
                for (java.time.LocalDate value : occurrenceDate) {
                    this.occurrenceDate.add((value == null) ? null : Date.of(value));
                }
                return this;
            }

            /**
             * The list of specific dates that will have appointments generated.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param occurrenceDate
             *     Specific dates for a recurring set of appointments (no template)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder occurrenceDate(Date... occurrenceDate) {
                for (Date value : occurrenceDate) {
                    this.occurrenceDate.add(value);
                }
                return this;
            }

            /**
             * The list of specific dates that will have appointments generated.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param occurrenceDate
             *     Specific dates for a recurring set of appointments (no template)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder occurrenceDate(Collection<Date> occurrenceDate) {
                this.occurrenceDate = new ArrayList<>(occurrenceDate);
                return this;
            }

            /**
             * Information about weekly recurring appointments.
             * 
             * @param weeklyTemplate
             *     Information about weekly recurring appointments
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder weeklyTemplate(WeeklyTemplate weeklyTemplate) {
                this.weeklyTemplate = weeklyTemplate;
                return this;
            }

            /**
             * Information about monthly recurring appointments.
             * 
             * @param monthlyTemplate
             *     Information about monthly recurring appointments
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder monthlyTemplate(MonthlyTemplate monthlyTemplate) {
                this.monthlyTemplate = monthlyTemplate;
                return this;
            }

            /**
             * Information about yearly recurring appointments.
             * 
             * @param yearlyTemplate
             *     Information about yearly recurring appointments
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder yearlyTemplate(YearlyTemplate yearlyTemplate) {
                this.yearlyTemplate = yearlyTemplate;
                return this;
            }

            /**
             * Convenience method for setting {@code excludingDate}.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param excludingDate
             *     Any dates that should be excluded from the series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #excludingDate(org.linuxforhealth.fhir.model.type.Date)
             */
            public Builder excludingDate(java.time.LocalDate... excludingDate) {
                for (java.time.LocalDate value : excludingDate) {
                    this.excludingDate.add((value == null) ? null : Date.of(value));
                }
                return this;
            }

            /**
             * Any dates, such as holidays, that should be excluded from the recurrence.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param excludingDate
             *     Any dates that should be excluded from the series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder excludingDate(Date... excludingDate) {
                for (Date value : excludingDate) {
                    this.excludingDate.add(value);
                }
                return this;
            }

            /**
             * Any dates, such as holidays, that should be excluded from the recurrence.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param excludingDate
             *     Any dates that should be excluded from the series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder excludingDate(Collection<Date> excludingDate) {
                this.excludingDate = new ArrayList<>(excludingDate);
                return this;
            }

            /**
             * Any dates, such as holidays, that should be excluded from the recurrence.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param excludingRecurrenceId
             *     Any recurrence IDs that should be excluded from the recurrence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder excludingRecurrenceId(PositiveInt... excludingRecurrenceId) {
                for (PositiveInt value : excludingRecurrenceId) {
                    this.excludingRecurrenceId.add(value);
                }
                return this;
            }

            /**
             * Any dates, such as holidays, that should be excluded from the recurrence.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param excludingRecurrenceId
             *     Any recurrence IDs that should be excluded from the recurrence
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder excludingRecurrenceId(Collection<PositiveInt> excludingRecurrenceId) {
                this.excludingRecurrenceId = new ArrayList<>(excludingRecurrenceId);
                return this;
            }

            /**
             * Build the {@link RecurrenceTemplate}
             * 
             * <p>Required elements:
             * <ul>
             * <li>recurrenceType</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link RecurrenceTemplate}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid RecurrenceTemplate per the base specification
             */
            @Override
            public RecurrenceTemplate build() {
                RecurrenceTemplate recurrenceTemplate = new RecurrenceTemplate(this);
                if (validating) {
                    validate(recurrenceTemplate);
                }
                return recurrenceTemplate;
            }

            protected void validate(RecurrenceTemplate recurrenceTemplate) {
                super.validate(recurrenceTemplate);
                ValidationSupport.requireNonNull(recurrenceTemplate.recurrenceType, "recurrenceType");
                ValidationSupport.checkList(recurrenceTemplate.occurrenceDate, "occurrenceDate", Date.class);
                ValidationSupport.checkList(recurrenceTemplate.excludingDate, "excludingDate", Date.class);
                ValidationSupport.checkList(recurrenceTemplate.excludingRecurrenceId, "excludingRecurrenceId", PositiveInt.class);
                ValidationSupport.requireValueOrChildren(recurrenceTemplate);
            }

            protected Builder from(RecurrenceTemplate recurrenceTemplate) {
                super.from(recurrenceTemplate);
                timezone = recurrenceTemplate.timezone;
                recurrenceType = recurrenceTemplate.recurrenceType;
                lastOccurrenceDate = recurrenceTemplate.lastOccurrenceDate;
                occurrenceCount = recurrenceTemplate.occurrenceCount;
                occurrenceDate.addAll(recurrenceTemplate.occurrenceDate);
                weeklyTemplate = recurrenceTemplate.weeklyTemplate;
                monthlyTemplate = recurrenceTemplate.monthlyTemplate;
                yearlyTemplate = recurrenceTemplate.yearlyTemplate;
                excludingDate.addAll(recurrenceTemplate.excludingDate);
                excludingRecurrenceId.addAll(recurrenceTemplate.excludingRecurrenceId);
                return this;
            }
        }

        /**
         * Information about weekly recurring appointments.
         */
        public static class WeeklyTemplate extends BackboneElement {
            private final Boolean monday;
            private final Boolean tuesday;
            private final Boolean wednesday;
            private final Boolean thursday;
            private final Boolean friday;
            private final Boolean saturday;
            private final Boolean sunday;
            private final PositiveInt weekInterval;

            private WeeklyTemplate(Builder builder) {
                super(builder);
                monday = builder.monday;
                tuesday = builder.tuesday;
                wednesday = builder.wednesday;
                thursday = builder.thursday;
                friday = builder.friday;
                saturday = builder.saturday;
                sunday = builder.sunday;
                weekInterval = builder.weekInterval;
            }

            /**
             * Indicates that recurring appointments should occur on Mondays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getMonday() {
                return monday;
            }

            /**
             * Indicates that recurring appointments should occur on Tuesdays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getTuesday() {
                return tuesday;
            }

            /**
             * Indicates that recurring appointments should occur on Wednesdays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getWednesday() {
                return wednesday;
            }

            /**
             * Indicates that recurring appointments should occur on Thursdays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getThursday() {
                return thursday;
            }

            /**
             * Indicates that recurring appointments should occur on Fridays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getFriday() {
                return friday;
            }

            /**
             * Indicates that recurring appointments should occur on Saturdays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getSaturday() {
                return saturday;
            }

            /**
             * Indicates that recurring appointments should occur on Sundays.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getSunday() {
                return sunday;
            }

            /**
             * The interval defines if the recurrence is every nth week. The default is every week, so it is expected that this value 
             * will be 2 or more.e.g. For recurring every second week this interval would be 2, or every third week the interval 
             * would be 3.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that may be null.
             */
            public PositiveInt getWeekInterval() {
                return weekInterval;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (monday != null) || 
                    (tuesday != null) || 
                    (wednesday != null) || 
                    (thursday != null) || 
                    (friday != null) || 
                    (saturday != null) || 
                    (sunday != null) || 
                    (weekInterval != null);
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
                        accept(monday, "monday", visitor);
                        accept(tuesday, "tuesday", visitor);
                        accept(wednesday, "wednesday", visitor);
                        accept(thursday, "thursday", visitor);
                        accept(friday, "friday", visitor);
                        accept(saturday, "saturday", visitor);
                        accept(sunday, "sunday", visitor);
                        accept(weekInterval, "weekInterval", visitor);
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
                WeeklyTemplate other = (WeeklyTemplate) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(monday, other.monday) && 
                    Objects.equals(tuesday, other.tuesday) && 
                    Objects.equals(wednesday, other.wednesday) && 
                    Objects.equals(thursday, other.thursday) && 
                    Objects.equals(friday, other.friday) && 
                    Objects.equals(saturday, other.saturday) && 
                    Objects.equals(sunday, other.sunday) && 
                    Objects.equals(weekInterval, other.weekInterval);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        monday, 
                        tuesday, 
                        wednesday, 
                        thursday, 
                        friday, 
                        saturday, 
                        sunday, 
                        weekInterval);
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
                private Boolean monday;
                private Boolean tuesday;
                private Boolean wednesday;
                private Boolean thursday;
                private Boolean friday;
                private Boolean saturday;
                private Boolean sunday;
                private PositiveInt weekInterval;

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
                 * Convenience method for setting {@code monday}.
                 * 
                 * @param monday
                 *     Recurs on Mondays
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #monday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder monday(java.lang.Boolean monday) {
                    this.monday = (monday == null) ? null : Boolean.of(monday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Mondays.
                 * 
                 * @param monday
                 *     Recurs on Mondays
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder monday(Boolean monday) {
                    this.monday = monday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code tuesday}.
                 * 
                 * @param tuesday
                 *     Recurs on Tuesday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #tuesday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder tuesday(java.lang.Boolean tuesday) {
                    this.tuesday = (tuesday == null) ? null : Boolean.of(tuesday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Tuesdays.
                 * 
                 * @param tuesday
                 *     Recurs on Tuesday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder tuesday(Boolean tuesday) {
                    this.tuesday = tuesday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code wednesday}.
                 * 
                 * @param wednesday
                 *     Recurs on Wednesday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #wednesday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder wednesday(java.lang.Boolean wednesday) {
                    this.wednesday = (wednesday == null) ? null : Boolean.of(wednesday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Wednesdays.
                 * 
                 * @param wednesday
                 *     Recurs on Wednesday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder wednesday(Boolean wednesday) {
                    this.wednesday = wednesday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code thursday}.
                 * 
                 * @param thursday
                 *     Recurs on Thursday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #thursday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder thursday(java.lang.Boolean thursday) {
                    this.thursday = (thursday == null) ? null : Boolean.of(thursday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Thursdays.
                 * 
                 * @param thursday
                 *     Recurs on Thursday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder thursday(Boolean thursday) {
                    this.thursday = thursday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code friday}.
                 * 
                 * @param friday
                 *     Recurs on Friday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #friday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder friday(java.lang.Boolean friday) {
                    this.friday = (friday == null) ? null : Boolean.of(friday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Fridays.
                 * 
                 * @param friday
                 *     Recurs on Friday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder friday(Boolean friday) {
                    this.friday = friday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code saturday}.
                 * 
                 * @param saturday
                 *     Recurs on Saturday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #saturday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder saturday(java.lang.Boolean saturday) {
                    this.saturday = (saturday == null) ? null : Boolean.of(saturday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Saturdays.
                 * 
                 * @param saturday
                 *     Recurs on Saturday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder saturday(Boolean saturday) {
                    this.saturday = saturday;
                    return this;
                }

                /**
                 * Convenience method for setting {@code sunday}.
                 * 
                 * @param sunday
                 *     Recurs on Sunday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #sunday(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder sunday(java.lang.Boolean sunday) {
                    this.sunday = (sunday == null) ? null : Boolean.of(sunday);
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur on Sundays.
                 * 
                 * @param sunday
                 *     Recurs on Sunday
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder sunday(Boolean sunday) {
                    this.sunday = sunday;
                    return this;
                }

                /**
                 * The interval defines if the recurrence is every nth week. The default is every week, so it is expected that this value 
                 * will be 2 or more.e.g. For recurring every second week this interval would be 2, or every third week the interval 
                 * would be 3.
                 * 
                 * @param weekInterval
                 *     Recurs every nth week
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder weekInterval(PositiveInt weekInterval) {
                    this.weekInterval = weekInterval;
                    return this;
                }

                /**
                 * Build the {@link WeeklyTemplate}
                 * 
                 * @return
                 *     An immutable object of type {@link WeeklyTemplate}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid WeeklyTemplate per the base specification
                 */
                @Override
                public WeeklyTemplate build() {
                    WeeklyTemplate weeklyTemplate = new WeeklyTemplate(this);
                    if (validating) {
                        validate(weeklyTemplate);
                    }
                    return weeklyTemplate;
                }

                protected void validate(WeeklyTemplate weeklyTemplate) {
                    super.validate(weeklyTemplate);
                    ValidationSupport.requireValueOrChildren(weeklyTemplate);
                }

                protected Builder from(WeeklyTemplate weeklyTemplate) {
                    super.from(weeklyTemplate);
                    monday = weeklyTemplate.monday;
                    tuesday = weeklyTemplate.tuesday;
                    wednesday = weeklyTemplate.wednesday;
                    thursday = weeklyTemplate.thursday;
                    friday = weeklyTemplate.friday;
                    saturday = weeklyTemplate.saturday;
                    sunday = weeklyTemplate.sunday;
                    weekInterval = weeklyTemplate.weekInterval;
                    return this;
                }
            }
        }

        /**
         * Information about monthly recurring appointments.
         */
        public static class MonthlyTemplate extends BackboneElement {
            private final PositiveInt dayOfMonth;
            @Binding(
                bindingName = "WeekOfMonth",
                strength = BindingStrength.Value.REQUIRED,
                description = "The set of weeks in a month.",
                valueSet = "http://hl7.org/fhir/ValueSet/week-of-month|5.0.0"
            )
            private final Coding nthWeekOfMonth;
            @Binding(
                bindingName = "DaysOfWeek",
                strength = BindingStrength.Value.REQUIRED,
                description = "The days of the week.",
                valueSet = "http://hl7.org/fhir/ValueSet/days-of-week|5.0.0"
            )
            private final Coding dayOfWeek;
            @Required
            private final PositiveInt monthInterval;

            private MonthlyTemplate(Builder builder) {
                super(builder);
                dayOfMonth = builder.dayOfMonth;
                nthWeekOfMonth = builder.nthWeekOfMonth;
                dayOfWeek = builder.dayOfWeek;
                monthInterval = builder.monthInterval;
            }

            /**
             * Indicates that appointments in the series of recurring appointments should occur on a specific day of the month.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that may be null.
             */
            public PositiveInt getDayOfMonth() {
                return dayOfMonth;
            }

            /**
             * Indicates which week within a month the appointments in the series of recurring appointments should occur on.
             * 
             * @return
             *     An immutable object of type {@link Coding} that may be null.
             */
            public Coding getNthWeekOfMonth() {
                return nthWeekOfMonth;
            }

            /**
             * Indicates which day of the week the recurring appointments should occur each nth week.
             * 
             * @return
             *     An immutable object of type {@link Coding} that may be null.
             */
            public Coding getDayOfWeek() {
                return dayOfWeek;
            }

            /**
             * Indicates that recurring appointments should occur every nth month.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that is non-null.
             */
            public PositiveInt getMonthInterval() {
                return monthInterval;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (dayOfMonth != null) || 
                    (nthWeekOfMonth != null) || 
                    (dayOfWeek != null) || 
                    (monthInterval != null);
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
                        accept(dayOfMonth, "dayOfMonth", visitor);
                        accept(nthWeekOfMonth, "nthWeekOfMonth", visitor);
                        accept(dayOfWeek, "dayOfWeek", visitor);
                        accept(monthInterval, "monthInterval", visitor);
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
                MonthlyTemplate other = (MonthlyTemplate) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(dayOfMonth, other.dayOfMonth) && 
                    Objects.equals(nthWeekOfMonth, other.nthWeekOfMonth) && 
                    Objects.equals(dayOfWeek, other.dayOfWeek) && 
                    Objects.equals(monthInterval, other.monthInterval);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        dayOfMonth, 
                        nthWeekOfMonth, 
                        dayOfWeek, 
                        monthInterval);
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
                private PositiveInt dayOfMonth;
                private Coding nthWeekOfMonth;
                private Coding dayOfWeek;
                private PositiveInt monthInterval;

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
                 * Indicates that appointments in the series of recurring appointments should occur on a specific day of the month.
                 * 
                 * @param dayOfMonth
                 *     Recurs on a specific day of the month
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder dayOfMonth(PositiveInt dayOfMonth) {
                    this.dayOfMonth = dayOfMonth;
                    return this;
                }

                /**
                 * Indicates which week within a month the appointments in the series of recurring appointments should occur on.
                 * 
                 * @param nthWeekOfMonth
                 *     Indicates which week of the month the appointment should occur
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder nthWeekOfMonth(Coding nthWeekOfMonth) {
                    this.nthWeekOfMonth = nthWeekOfMonth;
                    return this;
                }

                /**
                 * Indicates which day of the week the recurring appointments should occur each nth week.
                 * 
                 * @param dayOfWeek
                 *     Indicates which day of the week the appointment should occur
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder dayOfWeek(Coding dayOfWeek) {
                    this.dayOfWeek = dayOfWeek;
                    return this;
                }

                /**
                 * Indicates that recurring appointments should occur every nth month.
                 * 
                 * <p>This element is required.
                 * 
                 * @param monthInterval
                 *     Recurs every nth month
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder monthInterval(PositiveInt monthInterval) {
                    this.monthInterval = monthInterval;
                    return this;
                }

                /**
                 * Build the {@link MonthlyTemplate}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>monthInterval</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link MonthlyTemplate}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid MonthlyTemplate per the base specification
                 */
                @Override
                public MonthlyTemplate build() {
                    MonthlyTemplate monthlyTemplate = new MonthlyTemplate(this);
                    if (validating) {
                        validate(monthlyTemplate);
                    }
                    return monthlyTemplate;
                }

                protected void validate(MonthlyTemplate monthlyTemplate) {
                    super.validate(monthlyTemplate);
                    ValidationSupport.requireNonNull(monthlyTemplate.monthInterval, "monthInterval");
                    ValidationSupport.checkValueSetBinding(monthlyTemplate.nthWeekOfMonth, "nthWeekOfMonth", "http://hl7.org/fhir/ValueSet/week-of-month", "http://hl7.org/fhir/week-of-month", "first", "second", "third", "fourth", "last");
                    ValidationSupport.checkValueSetBinding(monthlyTemplate.dayOfWeek, "dayOfWeek", "http://hl7.org/fhir/ValueSet/days-of-week", "http://hl7.org/fhir/days-of-week", "mon", "tue", "wed", "thu", "fri", "sat", "sun");
                    ValidationSupport.requireValueOrChildren(monthlyTemplate);
                }

                protected Builder from(MonthlyTemplate monthlyTemplate) {
                    super.from(monthlyTemplate);
                    dayOfMonth = monthlyTemplate.dayOfMonth;
                    nthWeekOfMonth = monthlyTemplate.nthWeekOfMonth;
                    dayOfWeek = monthlyTemplate.dayOfWeek;
                    monthInterval = monthlyTemplate.monthInterval;
                    return this;
                }
            }
        }

        /**
         * Information about yearly recurring appointments.
         */
        public static class YearlyTemplate extends BackboneElement {
            @Required
            private final PositiveInt yearInterval;

            private YearlyTemplate(Builder builder) {
                super(builder);
                yearInterval = builder.yearInterval;
            }

            /**
             * Appointment recurs every nth year.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that is non-null.
             */
            public PositiveInt getYearInterval() {
                return yearInterval;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (yearInterval != null);
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
                        accept(yearInterval, "yearInterval", visitor);
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
                YearlyTemplate other = (YearlyTemplate) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(yearInterval, other.yearInterval);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        yearInterval);
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
                private PositiveInt yearInterval;

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
                 * Appointment recurs every nth year.
                 * 
                 * <p>This element is required.
                 * 
                 * @param yearInterval
                 *     Recurs every nth year
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder yearInterval(PositiveInt yearInterval) {
                    this.yearInterval = yearInterval;
                    return this;
                }

                /**
                 * Build the {@link YearlyTemplate}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>yearInterval</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link YearlyTemplate}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid YearlyTemplate per the base specification
                 */
                @Override
                public YearlyTemplate build() {
                    YearlyTemplate yearlyTemplate = new YearlyTemplate(this);
                    if (validating) {
                        validate(yearlyTemplate);
                    }
                    return yearlyTemplate;
                }

                protected void validate(YearlyTemplate yearlyTemplate) {
                    super.validate(yearlyTemplate);
                    ValidationSupport.requireNonNull(yearlyTemplate.yearInterval, "yearInterval");
                    ValidationSupport.requireValueOrChildren(yearlyTemplate);
                }

                protected Builder from(YearlyTemplate yearlyTemplate) {
                    super.from(yearlyTemplate);
                    yearInterval = yearlyTemplate.yearInterval;
                    return this;
                }
            }
        }
    }
}
