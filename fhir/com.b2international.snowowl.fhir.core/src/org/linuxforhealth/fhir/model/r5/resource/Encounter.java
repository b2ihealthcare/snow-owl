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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.VirtualServiceDetail;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.EncounterLocationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.EncounterStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * An interaction between a patient and healthcare provider(s) for the purpose of providing healthcare service(s) or 
 * assessing the health status of a patient. Encounter is primarily used to record information about the actual 
 * activities that occurred, where Appointment is used to record planned activities.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "enc-1",
    level = "Rule",
    location = "Encounter.participant",
    description = "A type must be provided when no explicit actor is specified",
    expression = "actor.exists() or type.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter"
)
@Constraint(
    id = "enc-2",
    level = "Rule",
    location = "Encounter.participant",
    description = "A type cannot be provided for a patient or group participant",
    expression = "actor.exists(resolve() is Patient or resolve() is Group) implies type.exists().not()",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter"
)
@Constraint(
    id = "encounter-3",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/encounter-class",
    expression = "class.exists() implies (class.all(memberOf('http://terminology.hl7.org/ValueSet/encounter-class', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-4",
    level = "Warning",
    location = "participant.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/encounter-participant-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-participant-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-5",
    level = "Warning",
    location = "reason.value",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-reason",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-reason', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-6",
    level = "Warning",
    location = "diagnosis.use",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-diagnosis-use",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-diagnosis-use', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-7",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-special-arrangements",
    expression = "specialArrangement.exists() implies (specialArrangement.all(memberOf('http://hl7.org/fhir/ValueSet/encounter-special-arrangements', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-8",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-special-courtesy",
    expression = "specialCourtesy.exists() implies (specialCourtesy.all(memberOf('http://hl7.org/fhir/ValueSet/encounter-special-courtesy', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Constraint(
    id = "encounter-9",
    level = "Warning",
    location = "admission.admitSource",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-admit-source",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-admit-source', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Encounter",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Encounter extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "EncounterStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Current state of the encounter.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-status|5.0.0"
    )
    @Required
    private final EncounterStatus status;
    @Summary
    @Binding(
        bindingName = "EncounterClass",
        strength = BindingStrength.Value.PREFERRED,
        description = "Classification of the encounter.",
        valueSet = "http://terminology.hl7.org/ValueSet/encounter-class"
    )
    private final List<CodeableConcept> clazz;
    @Binding(
        bindingName = "Priority",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Indicates the urgency of the encounter.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ActPriority"
    )
    private final CodeableConcept priority;
    @Summary
    @Binding(
        bindingName = "EncounterType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A specific code indicating type of service provided",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    @Binding(
        bindingName = "EncounterServiceType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Broad categorization of the service that is to be provided.",
        valueSet = "http://hl7.org/fhir/ValueSet/service-type"
    )
    private final List<CodeableReference> serviceType;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    private final Reference subject;
    @Binding(
        bindingName = "SubjectStatus",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Current status of the subject  within the encounter.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-subject-status"
    )
    private final CodeableConcept subjectStatus;
    @Summary
    @ReferenceTarget({ "EpisodeOfCare" })
    private final List<Reference> episodeOfCare;
    @ReferenceTarget({ "CarePlan", "DeviceRequest", "MedicationRequest", "ServiceRequest" })
    private final List<Reference> basedOn;
    @ReferenceTarget({ "CareTeam" })
    private final List<Reference> careTeam;
    @ReferenceTarget({ "Encounter" })
    private final Reference partOf;
    @ReferenceTarget({ "Organization" })
    private final Reference serviceProvider;
    @Summary
    private final List<Participant> participant;
    @Summary
    @ReferenceTarget({ "Appointment" })
    private final List<Reference> appointment;
    private final List<VirtualServiceDetail> virtualService;
    private final Period actualPeriod;
    private final DateTime plannedStartDate;
    private final DateTime plannedEndDate;
    private final Duration length;
    @Summary
    private final List<Reason> reason;
    @Summary
    private final List<Diagnosis> diagnosis;
    @ReferenceTarget({ "Account" })
    private final List<Reference> account;
    @Binding(
        bindingName = "PatientDiet",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Medical, cultural or ethical food preferences to help with catering requirements.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-diet"
    )
    private final List<CodeableConcept> dietPreference;
    @Binding(
        bindingName = "Arrangements",
        strength = BindingStrength.Value.PREFERRED,
        description = "Special arrangements.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-special-arrangements"
    )
    private final List<CodeableConcept> specialArrangement;
    @Binding(
        bindingName = "Courtesies",
        strength = BindingStrength.Value.PREFERRED,
        description = "Special courtesies.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-special-courtesy"
    )
    private final List<CodeableConcept> specialCourtesy;
    private final Admission admission;
    private final List<Location> location;

    private Encounter(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        clazz = Collections.unmodifiableList(builder.clazz);
        priority = builder.priority;
        type = Collections.unmodifiableList(builder.type);
        serviceType = Collections.unmodifiableList(builder.serviceType);
        subject = builder.subject;
        subjectStatus = builder.subjectStatus;
        episodeOfCare = Collections.unmodifiableList(builder.episodeOfCare);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        careTeam = Collections.unmodifiableList(builder.careTeam);
        partOf = builder.partOf;
        serviceProvider = builder.serviceProvider;
        participant = Collections.unmodifiableList(builder.participant);
        appointment = Collections.unmodifiableList(builder.appointment);
        virtualService = Collections.unmodifiableList(builder.virtualService);
        actualPeriod = builder.actualPeriod;
        plannedStartDate = builder.plannedStartDate;
        plannedEndDate = builder.plannedEndDate;
        length = builder.length;
        reason = Collections.unmodifiableList(builder.reason);
        diagnosis = Collections.unmodifiableList(builder.diagnosis);
        account = Collections.unmodifiableList(builder.account);
        dietPreference = Collections.unmodifiableList(builder.dietPreference);
        specialArrangement = Collections.unmodifiableList(builder.specialArrangement);
        specialCourtesy = Collections.unmodifiableList(builder.specialCourtesy);
        admission = builder.admission;
        location = Collections.unmodifiableList(builder.location);
    }

    /**
     * Identifier(s) by which this encounter is known.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The current state of the encounter (not the state of the patient within the encounter - that is subjectState).
     * 
     * @return
     *     An immutable object of type {@link EncounterStatus} that is non-null.
     */
    public EncounterStatus getStatus() {
        return status;
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
     * Indicates the urgency of the encounter.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPriority() {
        return priority;
    }

    /**
     * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * Broad categorization of the service that is to be provided (e.g. cardiology).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getServiceType() {
        return serviceType;
    }

    /**
     * The patient or group related to this encounter. In some use-cases the patient MAY not be present, such as a case 
     * meeting about a patient between several practitioners or a careteam.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The subjectStatus value can be used to track the patient's status within the encounter. It details whether the patient 
     * has arrived or departed, has been triaged or is currently in a waiting status.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSubjectStatus() {
        return subjectStatus;
    }

    /**
     * Where a specific encounter should be classified as a part of a specific episode(s) of care this field should be used. 
     * This association can facilitate grouping of related encounters together for a specific purpose, such as government 
     * reporting, issue tracking, association via a common problem. The association is recorded on the encounter as these are 
     * typically created after the episode of care and grouped on entry rather than editing the episode of care to append 
     * another encounter to it (the episode of care could span years).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEpisodeOfCare() {
        return episodeOfCare;
    }

    /**
     * The request this encounter satisfies (e.g. incoming referral or procedure request).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The group(s) of individuals, organizations that are allocated to participate in this encounter. The participants 
     * backbone will record the actuals of when these individuals participated during the encounter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getCareTeam() {
        return careTeam;
    }

    /**
     * Another Encounter of which this encounter is a part of (administratively or in time).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPartOf() {
        return partOf;
    }

    /**
     * The organization that is primarily responsible for this Encounter's services. This MAY be the same as the organization 
     * on the Patient record, however it could be different, such as if the actor performing the services was from an 
     * external organization (which may be billed seperately) for an external consultation. Refer to the colonoscopy example 
     * on the Encounter examples tab.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getServiceProvider() {
        return serviceProvider;
    }

    /**
     * The list of people responsible for providing the service.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Participant} that may be empty.
     */
    public List<Participant> getParticipant() {
        return participant;
    }

    /**
     * The appointment that scheduled this encounter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAppointment() {
        return appointment;
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
     * The actual start and end time of the encounter.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getActualPeriod() {
        return actualPeriod;
    }

    /**
     * The planned start date/time (or admission date) of the encounter.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPlannedStartDate() {
        return plannedStartDate;
    }

    /**
     * The planned end date/time (or discharge date) of the encounter.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    /**
     * Actual quantity of time the encounter lasted. This excludes the time during leaves of absence.When missing it is the 
     * time in between the start and end values.
     * 
     * @return
     *     An immutable object of type {@link Duration} that may be null.
     */
    public Duration getLength() {
        return length;
    }

    /**
     * The list of medical reasons that are expected to be addressed during the episode of care.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reason} that may be empty.
     */
    public List<Reason> getReason() {
        return reason;
    }

    /**
     * The list of diagnosis relevant to this encounter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Diagnosis} that may be empty.
     */
    public List<Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    /**
     * The set of accounts that may be used for billing for this Encounter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAccount() {
        return account;
    }

    /**
     * Diet preferences reported by the patient.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getDietPreference() {
        return dietPreference;
    }

    /**
     * Any special requests that have been made for this encounter, such as the provision of specific equipment or other 
     * things.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSpecialArrangement() {
        return specialArrangement;
    }

    /**
     * Special courtesies that may be provided to the patient during the encounter (VIP, board member, professional courtesy).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSpecialCourtesy() {
        return specialCourtesy;
    }

    /**
     * Details about the stay during which a healthcare service is provided.This does not describe the event of admitting 
     * the patient, but rather any information that is relevant from the time of admittance until the time of discharge.
     * 
     * @return
     *     An immutable object of type {@link Admission} that may be null.
     */
    public Admission getAdmission() {
        return admission;
    }

    /**
     * List of locations where the patient has been during this encounter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Location} that may be empty.
     */
    public List<Location> getLocation() {
        return location;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !clazz.isEmpty() || 
            (priority != null) || 
            !type.isEmpty() || 
            !serviceType.isEmpty() || 
            (subject != null) || 
            (subjectStatus != null) || 
            !episodeOfCare.isEmpty() || 
            !basedOn.isEmpty() || 
            !careTeam.isEmpty() || 
            (partOf != null) || 
            (serviceProvider != null) || 
            !participant.isEmpty() || 
            !appointment.isEmpty() || 
            !virtualService.isEmpty() || 
            (actualPeriod != null) || 
            (plannedStartDate != null) || 
            (plannedEndDate != null) || 
            (length != null) || 
            !reason.isEmpty() || 
            !diagnosis.isEmpty() || 
            !account.isEmpty() || 
            !dietPreference.isEmpty() || 
            !specialArrangement.isEmpty() || 
            !specialCourtesy.isEmpty() || 
            (admission != null) || 
            !location.isEmpty();
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
                accept(clazz, "class", visitor, CodeableConcept.class);
                accept(priority, "priority", visitor);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(serviceType, "serviceType", visitor, CodeableReference.class);
                accept(subject, "subject", visitor);
                accept(subjectStatus, "subjectStatus", visitor);
                accept(episodeOfCare, "episodeOfCare", visitor, Reference.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(careTeam, "careTeam", visitor, Reference.class);
                accept(partOf, "partOf", visitor);
                accept(serviceProvider, "serviceProvider", visitor);
                accept(participant, "participant", visitor, Participant.class);
                accept(appointment, "appointment", visitor, Reference.class);
                accept(virtualService, "virtualService", visitor, VirtualServiceDetail.class);
                accept(actualPeriod, "actualPeriod", visitor);
                accept(plannedStartDate, "plannedStartDate", visitor);
                accept(plannedEndDate, "plannedEndDate", visitor);
                accept(length, "length", visitor);
                accept(reason, "reason", visitor, Reason.class);
                accept(diagnosis, "diagnosis", visitor, Diagnosis.class);
                accept(account, "account", visitor, Reference.class);
                accept(dietPreference, "dietPreference", visitor, CodeableConcept.class);
                accept(specialArrangement, "specialArrangement", visitor, CodeableConcept.class);
                accept(specialCourtesy, "specialCourtesy", visitor, CodeableConcept.class);
                accept(admission, "admission", visitor);
                accept(location, "location", visitor, Location.class);
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
        Encounter other = (Encounter) obj;
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
            Objects.equals(clazz, other.clazz) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(type, other.type) && 
            Objects.equals(serviceType, other.serviceType) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(subjectStatus, other.subjectStatus) && 
            Objects.equals(episodeOfCare, other.episodeOfCare) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(careTeam, other.careTeam) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(serviceProvider, other.serviceProvider) && 
            Objects.equals(participant, other.participant) && 
            Objects.equals(appointment, other.appointment) && 
            Objects.equals(virtualService, other.virtualService) && 
            Objects.equals(actualPeriod, other.actualPeriod) && 
            Objects.equals(plannedStartDate, other.plannedStartDate) && 
            Objects.equals(plannedEndDate, other.plannedEndDate) && 
            Objects.equals(length, other.length) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(diagnosis, other.diagnosis) && 
            Objects.equals(account, other.account) && 
            Objects.equals(dietPreference, other.dietPreference) && 
            Objects.equals(specialArrangement, other.specialArrangement) && 
            Objects.equals(specialCourtesy, other.specialCourtesy) && 
            Objects.equals(admission, other.admission) && 
            Objects.equals(location, other.location);
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
                clazz, 
                priority, 
                type, 
                serviceType, 
                subject, 
                subjectStatus, 
                episodeOfCare, 
                basedOn, 
                careTeam, 
                partOf, 
                serviceProvider, 
                participant, 
                appointment, 
                virtualService, 
                actualPeriod, 
                plannedStartDate, 
                plannedEndDate, 
                length, 
                reason, 
                diagnosis, 
                account, 
                dietPreference, 
                specialArrangement, 
                specialCourtesy, 
                admission, 
                location);
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
        private EncounterStatus status;
        private List<CodeableConcept> clazz = new ArrayList<>();
        private CodeableConcept priority;
        private List<CodeableConcept> type = new ArrayList<>();
        private List<CodeableReference> serviceType = new ArrayList<>();
        private Reference subject;
        private CodeableConcept subjectStatus;
        private List<Reference> episodeOfCare = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private List<Reference> careTeam = new ArrayList<>();
        private Reference partOf;
        private Reference serviceProvider;
        private List<Participant> participant = new ArrayList<>();
        private List<Reference> appointment = new ArrayList<>();
        private List<VirtualServiceDetail> virtualService = new ArrayList<>();
        private Period actualPeriod;
        private DateTime plannedStartDate;
        private DateTime plannedEndDate;
        private Duration length;
        private List<Reason> reason = new ArrayList<>();
        private List<Diagnosis> diagnosis = new ArrayList<>();
        private List<Reference> account = new ArrayList<>();
        private List<CodeableConcept> dietPreference = new ArrayList<>();
        private List<CodeableConcept> specialArrangement = new ArrayList<>();
        private List<CodeableConcept> specialCourtesy = new ArrayList<>();
        private Admission admission;
        private List<Location> location = new ArrayList<>();

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
         * Identifier(s) by which this encounter is known.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier(s) by which this encounter is known
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
         * Identifier(s) by which this encounter is known.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier(s) by which this encounter is known
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
         * The current state of the encounter (not the state of the patient within the encounter - that is subjectState).
         * 
         * <p>This element is required.
         * 
         * @param status
         *     planned | in-progress | on-hold | discharged | completed | cancelled | discontinued | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(EncounterStatus status) {
            this.status = status;
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
         *     Classification of patient encounter context - e.g. Inpatient, outpatient
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
         *     Classification of patient encounter context - e.g. Inpatient, outpatient
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
         * Indicates the urgency of the encounter.
         * 
         * @param priority
         *     Indicates the urgency of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(CodeableConcept priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Specific type of encounter (e.g. e-mail consultation, surgical day-care, ...)
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
         * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Specific type of encounter (e.g. e-mail consultation, surgical day-care, ...)
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
         * Broad categorization of the service that is to be provided (e.g. cardiology).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     Specific type of service
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
         * Broad categorization of the service that is to be provided (e.g. cardiology).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceType
         *     Specific type of service
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
         * The patient or group related to this encounter. In some use-cases the patient MAY not be present, such as a case 
         * meeting about a patient between several practitioners or a careteam.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     The patient or group related to this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The subjectStatus value can be used to track the patient's status within the encounter. It details whether the patient 
         * has arrived or departed, has been triaged or is currently in a waiting status.
         * 
         * @param subjectStatus
         *     The current status of the subject in relation to the Encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subjectStatus(CodeableConcept subjectStatus) {
            this.subjectStatus = subjectStatus;
            return this;
        }

        /**
         * Where a specific encounter should be classified as a part of a specific episode(s) of care this field should be used. 
         * This association can facilitate grouping of related encounters together for a specific purpose, such as government 
         * reporting, issue tracking, association via a common problem. The association is recorded on the encounter as these are 
         * typically created after the episode of care and grouped on entry rather than editing the episode of care to append 
         * another encounter to it (the episode of care could span years).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link EpisodeOfCare}</li>
         * </ul>
         * 
         * @param episodeOfCare
         *     Episode(s) of care that this encounter should be recorded against
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder episodeOfCare(Reference... episodeOfCare) {
            for (Reference value : episodeOfCare) {
                this.episodeOfCare.add(value);
            }
            return this;
        }

        /**
         * Where a specific encounter should be classified as a part of a specific episode(s) of care this field should be used. 
         * This association can facilitate grouping of related encounters together for a specific purpose, such as government 
         * reporting, issue tracking, association via a common problem. The association is recorded on the encounter as these are 
         * typically created after the episode of care and grouped on entry rather than editing the episode of care to append 
         * another encounter to it (the episode of care could span years).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link EpisodeOfCare}</li>
         * </ul>
         * 
         * @param episodeOfCare
         *     Episode(s) of care that this encounter should be recorded against
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder episodeOfCare(Collection<Reference> episodeOfCare) {
            this.episodeOfCare = new ArrayList<>(episodeOfCare);
            return this;
        }

        /**
         * The request this encounter satisfies (e.g. incoming referral or procedure request).
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
         *     The request that initiated this encounter
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
         * The request this encounter satisfies (e.g. incoming referral or procedure request).
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
         *     The request that initiated this encounter
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
         * The group(s) of individuals, organizations that are allocated to participate in this encounter. The participants 
         * backbone will record the actuals of when these individuals participated during the encounter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     The group(s) that are allocated to participate in this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder careTeam(Reference... careTeam) {
            for (Reference value : careTeam) {
                this.careTeam.add(value);
            }
            return this;
        }

        /**
         * The group(s) of individuals, organizations that are allocated to participate in this encounter. The participants 
         * backbone will record the actuals of when these individuals participated during the encounter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     The group(s) that are allocated to participate in this encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder careTeam(Collection<Reference> careTeam) {
            this.careTeam = new ArrayList<>(careTeam);
            return this;
        }

        /**
         * Another Encounter of which this encounter is a part of (administratively or in time).
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param partOf
         *     Another Encounter this encounter is part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partOf(Reference partOf) {
            this.partOf = partOf;
            return this;
        }

        /**
         * The organization that is primarily responsible for this Encounter's services. This MAY be the same as the organization 
         * on the Patient record, however it could be different, such as if the actor performing the services was from an 
         * external organization (which may be billed seperately) for an external consultation. Refer to the colonoscopy example 
         * on the Encounter examples tab.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param serviceProvider
         *     The organization (facility) responsible for this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder serviceProvider(Reference serviceProvider) {
            this.serviceProvider = serviceProvider;
            return this;
        }

        /**
         * The list of people responsible for providing the service.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     List of participants involved in the encounter
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
         * The list of people responsible for providing the service.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     List of participants involved in the encounter
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
         * The appointment that scheduled this encounter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param appointment
         *     The appointment that scheduled this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder appointment(Reference... appointment) {
            for (Reference value : appointment) {
                this.appointment.add(value);
            }
            return this;
        }

        /**
         * The appointment that scheduled this encounter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param appointment
         *     The appointment that scheduled this encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder appointment(Collection<Reference> appointment) {
            this.appointment = new ArrayList<>(appointment);
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
         * The actual start and end time of the encounter.
         * 
         * @param actualPeriod
         *     The actual start and end time of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actualPeriod(Period actualPeriod) {
            this.actualPeriod = actualPeriod;
            return this;
        }

        /**
         * The planned start date/time (or admission date) of the encounter.
         * 
         * @param plannedStartDate
         *     The planned start date/time (or admission date) of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder plannedStartDate(DateTime plannedStartDate) {
            this.plannedStartDate = plannedStartDate;
            return this;
        }

        /**
         * The planned end date/time (or discharge date) of the encounter.
         * 
         * @param plannedEndDate
         *     The planned end date/time (or discharge date) of the encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder plannedEndDate(DateTime plannedEndDate) {
            this.plannedEndDate = plannedEndDate;
            return this;
        }

        /**
         * Actual quantity of time the encounter lasted. This excludes the time during leaves of absence.When missing it is the 
         * time in between the start and end values.
         * 
         * @param length
         *     Actual quantity of time the encounter lasted (less time absent)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder length(Duration length) {
            this.length = length;
            return this;
        }

        /**
         * The list of medical reasons that are expected to be addressed during the episode of care.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The list of medical reasons that are expected to be addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(Reason... reason) {
            for (Reason value : reason) {
                this.reason.add(value);
            }
            return this;
        }

        /**
         * The list of medical reasons that are expected to be addressed during the episode of care.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The list of medical reasons that are expected to be addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reason(Collection<Reason> reason) {
            this.reason = new ArrayList<>(reason);
            return this;
        }

        /**
         * The list of diagnosis relevant to this encounter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of diagnosis relevant to this encounter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder diagnosis(Diagnosis... diagnosis) {
            for (Diagnosis value : diagnosis) {
                this.diagnosis.add(value);
            }
            return this;
        }

        /**
         * The list of diagnosis relevant to this encounter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of diagnosis relevant to this encounter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder diagnosis(Collection<Diagnosis> diagnosis) {
            this.diagnosis = new ArrayList<>(diagnosis);
            return this;
        }

        /**
         * The set of accounts that may be used for billing for this Encounter.
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
         *     The set of accounts that may be used for billing for this Encounter
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
         * The set of accounts that may be used for billing for this Encounter.
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
         *     The set of accounts that may be used for billing for this Encounter
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
         * Diet preferences reported by the patient.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dietPreference
         *     Diet preferences reported by the patient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dietPreference(CodeableConcept... dietPreference) {
            for (CodeableConcept value : dietPreference) {
                this.dietPreference.add(value);
            }
            return this;
        }

        /**
         * Diet preferences reported by the patient.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dietPreference
         *     Diet preferences reported by the patient
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder dietPreference(Collection<CodeableConcept> dietPreference) {
            this.dietPreference = new ArrayList<>(dietPreference);
            return this;
        }

        /**
         * Any special requests that have been made for this encounter, such as the provision of specific equipment or other 
         * things.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialArrangement
         *     Wheelchair, translator, stretcher, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specialArrangement(CodeableConcept... specialArrangement) {
            for (CodeableConcept value : specialArrangement) {
                this.specialArrangement.add(value);
            }
            return this;
        }

        /**
         * Any special requests that have been made for this encounter, such as the provision of specific equipment or other 
         * things.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialArrangement
         *     Wheelchair, translator, stretcher, etc
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specialArrangement(Collection<CodeableConcept> specialArrangement) {
            this.specialArrangement = new ArrayList<>(specialArrangement);
            return this;
        }

        /**
         * Special courtesies that may be provided to the patient during the encounter (VIP, board member, professional courtesy).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialCourtesy
         *     Special courtesies (VIP, board member)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specialCourtesy(CodeableConcept... specialCourtesy) {
            for (CodeableConcept value : specialCourtesy) {
                this.specialCourtesy.add(value);
            }
            return this;
        }

        /**
         * Special courtesies that may be provided to the patient during the encounter (VIP, board member, professional courtesy).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialCourtesy
         *     Special courtesies (VIP, board member)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specialCourtesy(Collection<CodeableConcept> specialCourtesy) {
            this.specialCourtesy = new ArrayList<>(specialCourtesy);
            return this;
        }

        /**
         * Details about the stay during which a healthcare service is provided.This does not describe the event of admitting 
         * the patient, but rather any information that is relevant from the time of admittance until the time of discharge.
         * 
         * @param admission
         *     Details about the admission to a healthcare service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder admission(Admission admission) {
            this.admission = admission;
            return this;
        }

        /**
         * List of locations where the patient has been during this encounter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     List of locations where the patient has been
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Location... location) {
            for (Location value : location) {
                this.location.add(value);
            }
            return this;
        }

        /**
         * List of locations where the patient has been during this encounter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param location
         *     List of locations where the patient has been
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder location(Collection<Location> location) {
            this.location = new ArrayList<>(location);
            return this;
        }

        /**
         * Build the {@link Encounter}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Encounter}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Encounter per the base specification
         */
        @Override
        public Encounter build() {
            Encounter encounter = new Encounter(this);
            if (validating) {
                validate(encounter);
            }
            return encounter;
        }

        protected void validate(Encounter encounter) {
            super.validate(encounter);
            ValidationSupport.checkList(encounter.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(encounter.status, "status");
            ValidationSupport.checkList(encounter.clazz, "class", CodeableConcept.class);
            ValidationSupport.checkList(encounter.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(encounter.serviceType, "serviceType", CodeableReference.class);
            ValidationSupport.checkList(encounter.episodeOfCare, "episodeOfCare", Reference.class);
            ValidationSupport.checkList(encounter.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(encounter.careTeam, "careTeam", Reference.class);
            ValidationSupport.checkList(encounter.participant, "participant", Participant.class);
            ValidationSupport.checkList(encounter.appointment, "appointment", Reference.class);
            ValidationSupport.checkList(encounter.virtualService, "virtualService", VirtualServiceDetail.class);
            ValidationSupport.checkList(encounter.reason, "reason", Reason.class);
            ValidationSupport.checkList(encounter.diagnosis, "diagnosis", Diagnosis.class);
            ValidationSupport.checkList(encounter.account, "account", Reference.class);
            ValidationSupport.checkList(encounter.dietPreference, "dietPreference", CodeableConcept.class);
            ValidationSupport.checkList(encounter.specialArrangement, "specialArrangement", CodeableConcept.class);
            ValidationSupport.checkList(encounter.specialCourtesy, "specialCourtesy", CodeableConcept.class);
            ValidationSupport.checkList(encounter.location, "location", Location.class);
            ValidationSupport.checkReferenceType(encounter.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(encounter.episodeOfCare, "episodeOfCare", "EpisodeOfCare");
            ValidationSupport.checkReferenceType(encounter.basedOn, "basedOn", "CarePlan", "DeviceRequest", "MedicationRequest", "ServiceRequest");
            ValidationSupport.checkReferenceType(encounter.careTeam, "careTeam", "CareTeam");
            ValidationSupport.checkReferenceType(encounter.partOf, "partOf", "Encounter");
            ValidationSupport.checkReferenceType(encounter.serviceProvider, "serviceProvider", "Organization");
            ValidationSupport.checkReferenceType(encounter.appointment, "appointment", "Appointment");
            ValidationSupport.checkReferenceType(encounter.account, "account", "Account");
        }

        protected Builder from(Encounter encounter) {
            super.from(encounter);
            identifier.addAll(encounter.identifier);
            status = encounter.status;
            clazz.addAll(encounter.clazz);
            priority = encounter.priority;
            type.addAll(encounter.type);
            serviceType.addAll(encounter.serviceType);
            subject = encounter.subject;
            subjectStatus = encounter.subjectStatus;
            episodeOfCare.addAll(encounter.episodeOfCare);
            basedOn.addAll(encounter.basedOn);
            careTeam.addAll(encounter.careTeam);
            partOf = encounter.partOf;
            serviceProvider = encounter.serviceProvider;
            participant.addAll(encounter.participant);
            appointment.addAll(encounter.appointment);
            virtualService.addAll(encounter.virtualService);
            actualPeriod = encounter.actualPeriod;
            plannedStartDate = encounter.plannedStartDate;
            plannedEndDate = encounter.plannedEndDate;
            length = encounter.length;
            reason.addAll(encounter.reason);
            diagnosis.addAll(encounter.diagnosis);
            account.addAll(encounter.account);
            dietPreference.addAll(encounter.dietPreference);
            specialArrangement.addAll(encounter.specialArrangement);
            specialCourtesy.addAll(encounter.specialCourtesy);
            admission = encounter.admission;
            location.addAll(encounter.location);
            return this;
        }
    }

    /**
     * The list of people responsible for providing the service.
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
        @ReferenceTarget({ "Patient", "Group", "RelatedPerson", "Practitioner", "PractitionerRole", "Device", "HealthcareService" })
        private final Reference actor;

        private Participant(Builder builder) {
            super(builder);
            type = Collections.unmodifiableList(builder.type);
            period = builder.period;
            actor = builder.actor;
        }

        /**
         * Role of participant in encounter.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * The period of time that the specified participant participated in the encounter. These can overlap or be sub-sets of 
         * the overall encounter's period.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        /**
         * Person involved in the encounter, the patient/group is also included here to indicate that the patient was actually 
         * participating in the encounter. Not including the patient here covers use cases such as a case meeting between 
         * practitioners about a patient - non contact times.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getActor() {
            return actor;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !type.isEmpty() || 
                (period != null) || 
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
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(period, "period", visitor);
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
                Objects.equals(period, other.period) && 
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
                    period, 
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
            private List<CodeableConcept> type = new ArrayList<>();
            private Period period;
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
             * Role of participant in encounter.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Role of participant in encounter
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
             * Role of participant in encounter.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Role of participant in encounter
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
             * The period of time that the specified participant participated in the encounter. These can overlap or be sub-sets of 
             * the overall encounter's period.
             * 
             * @param period
             *     Period of time during the encounter that the participant participated
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Person involved in the encounter, the patient/group is also included here to indicate that the patient was actually 
             * participating in the encounter. Not including the patient here covers use cases such as a case meeting between 
             * practitioners about a patient - non contact times.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Group}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Device}</li>
             * <li>{@link HealthcareService}</li>
             * </ul>
             * 
             * @param actor
             *     The individual, device, or service participating in the encounter
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
                ValidationSupport.checkReferenceType(participant.actor, "actor", "Patient", "Group", "RelatedPerson", "Practitioner", "PractitionerRole", "Device", "HealthcareService");
                ValidationSupport.requireValueOrChildren(participant);
            }

            protected Builder from(Participant participant) {
                super.from(participant);
                type.addAll(participant.type);
                period = participant.period;
                actor = participant.actor;
                return this;
            }
        }
    }

    /**
     * The list of medical reasons that are expected to be addressed during the episode of care.
     */
    public static class Reason extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "reason-use",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-reason-use"
        )
        private final List<CodeableConcept> use;
        @Summary
        @Binding(
            bindingName = "EncounterReason",
            strength = BindingStrength.Value.PREFERRED,
            description = "Reason why the encounter takes place.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-reason"
        )
        private final List<CodeableReference> value;

        private Reason(Builder builder) {
            super(builder);
            use = Collections.unmodifiableList(builder.use);
            value = Collections.unmodifiableList(builder.value);
        }

        /**
         * What the reason value should be used as e.g. Chief Complaint, Health Concern, Health Maintenance (including screening).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getUse() {
            return use;
        }

        /**
         * Reason the encounter takes place, expressed as a code or a reference to another resource. For admissions, this can be 
         * used for a coded admission diagnosis.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !use.isEmpty() || 
                !value.isEmpty();
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
                    accept(use, "use", visitor, CodeableConcept.class);
                    accept(value, "value", visitor, CodeableReference.class);
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
            Reason other = (Reason) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(use, other.use) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    use, 
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
            private List<CodeableConcept> use = new ArrayList<>();
            private List<CodeableReference> value = new ArrayList<>();

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
             * What the reason value should be used as e.g. Chief Complaint, Health Concern, Health Maintenance (including screening).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param use
             *     What the reason value should be used for/as
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder use(CodeableConcept... use) {
                for (CodeableConcept value : use) {
                    this.use.add(value);
                }
                return this;
            }

            /**
             * What the reason value should be used as e.g. Chief Complaint, Health Concern, Health Maintenance (including screening).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param use
             *     What the reason value should be used for/as
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder use(Collection<CodeableConcept> use) {
                this.use = new ArrayList<>(use);
                return this;
            }

            /**
             * Reason the encounter takes place, expressed as a code or a reference to another resource. For admissions, this can be 
             * used for a coded admission diagnosis.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param value
             *     Reason the encounter takes place (core or reference)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(CodeableReference... value) {
                for (CodeableReference _value : value) {
                    this.value.add(_value);
                }
                return this;
            }

            /**
             * Reason the encounter takes place, expressed as a code or a reference to another resource. For admissions, this can be 
             * used for a coded admission diagnosis.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param value
             *     Reason the encounter takes place (core or reference)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder value(Collection<CodeableReference> value) {
                this.value = new ArrayList<>(value);
                return this;
            }

            /**
             * Build the {@link Reason}
             * 
             * @return
             *     An immutable object of type {@link Reason}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Reason per the base specification
             */
            @Override
            public Reason build() {
                Reason reason = new Reason(this);
                if (validating) {
                    validate(reason);
                }
                return reason;
            }

            protected void validate(Reason reason) {
                super.validate(reason);
                ValidationSupport.checkList(reason.use, "use", CodeableConcept.class);
                ValidationSupport.checkList(reason.value, "value", CodeableReference.class);
                ValidationSupport.requireValueOrChildren(reason);
            }

            protected Builder from(Reason reason) {
                super.from(reason);
                use.addAll(reason.use);
                value.addAll(reason.value);
                return this;
            }
        }
    }

    /**
     * The list of diagnosis relevant to this encounter.
     */
    public static class Diagnosis extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "condition-code",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
        )
        private final List<CodeableReference> condition;
        @Binding(
            bindingName = "DiagnosisUse",
            strength = BindingStrength.Value.PREFERRED,
            description = "The type of diagnosis this condition represents.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-diagnosis-use"
        )
        private final List<CodeableConcept> use;

        private Diagnosis(Builder builder) {
            super(builder);
            condition = Collections.unmodifiableList(builder.condition);
            use = Collections.unmodifiableList(builder.use);
        }

        /**
         * The coded diagnosis or a reference to a Condition (with other resources referenced in the evidence.detail), the use 
         * property will indicate the purpose of this specific diagnosis.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getCondition() {
            return condition;
        }

        /**
         * Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getUse() {
            return use;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !condition.isEmpty() || 
                !use.isEmpty();
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
                    accept(condition, "condition", visitor, CodeableReference.class);
                    accept(use, "use", visitor, CodeableConcept.class);
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
            Diagnosis other = (Diagnosis) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(condition, other.condition) && 
                Objects.equals(use, other.use);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    condition, 
                    use);
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
            private List<CodeableReference> condition = new ArrayList<>();
            private List<CodeableConcept> use = new ArrayList<>();

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
             * The coded diagnosis or a reference to a Condition (with other resources referenced in the evidence.detail), the use 
             * property will indicate the purpose of this specific diagnosis.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     The diagnosis relevant to the encounter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder condition(CodeableReference... condition) {
                for (CodeableReference value : condition) {
                    this.condition.add(value);
                }
                return this;
            }

            /**
             * The coded diagnosis or a reference to a Condition (with other resources referenced in the evidence.detail), the use 
             * property will indicate the purpose of this specific diagnosis.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     The diagnosis relevant to the encounter
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder condition(Collection<CodeableReference> condition) {
                this.condition = new ArrayList<>(condition);
                return this;
            }

            /**
             * Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param use
             *     Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder use(CodeableConcept... use) {
                for (CodeableConcept value : use) {
                    this.use.add(value);
                }
                return this;
            }

            /**
             * Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param use
             *     Role that this diagnosis has within the encounter (e.g. admission, billing, discharge …)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder use(Collection<CodeableConcept> use) {
                this.use = new ArrayList<>(use);
                return this;
            }

            /**
             * Build the {@link Diagnosis}
             * 
             * @return
             *     An immutable object of type {@link Diagnosis}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Diagnosis per the base specification
             */
            @Override
            public Diagnosis build() {
                Diagnosis diagnosis = new Diagnosis(this);
                if (validating) {
                    validate(diagnosis);
                }
                return diagnosis;
            }

            protected void validate(Diagnosis diagnosis) {
                super.validate(diagnosis);
                ValidationSupport.checkList(diagnosis.condition, "condition", CodeableReference.class);
                ValidationSupport.checkList(diagnosis.use, "use", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(diagnosis);
            }

            protected Builder from(Diagnosis diagnosis) {
                super.from(diagnosis);
                condition.addAll(diagnosis.condition);
                use.addAll(diagnosis.use);
                return this;
            }
        }
    }

    /**
     * Details about the stay during which a healthcare service is provided.This does not describe the event of admitting 
     * the patient, but rather any information that is relevant from the time of admittance until the time of discharge.
     */
    public static class Admission extends BackboneElement {
        private final Identifier preAdmissionIdentifier;
        @ReferenceTarget({ "Location", "Organization" })
        private final Reference origin;
        @Binding(
            bindingName = "AdmitSource",
            strength = BindingStrength.Value.PREFERRED,
            description = "From where the patient was admitted.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-admit-source"
        )
        private final CodeableConcept admitSource;
        @Binding(
            bindingName = "ReAdmissionType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The reason for re-admission of this admission encounter.",
            valueSet = "http://terminology.hl7.org/ValueSet/v2-0092"
        )
        private final CodeableConcept reAdmission;
        @ReferenceTarget({ "Location", "Organization" })
        private final Reference destination;
        @Binding(
            bindingName = "DischargeDisp",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Discharge Disposition.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-discharge-disposition"
        )
        private final CodeableConcept dischargeDisposition;

        private Admission(Builder builder) {
            super(builder);
            preAdmissionIdentifier = builder.preAdmissionIdentifier;
            origin = builder.origin;
            admitSource = builder.admitSource;
            reAdmission = builder.reAdmission;
            destination = builder.destination;
            dischargeDisposition = builder.dischargeDisposition;
        }

        /**
         * Pre-admission identifier.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that may be null.
         */
        public Identifier getPreAdmissionIdentifier() {
            return preAdmissionIdentifier;
        }

        /**
         * The location/organization from which the patient came before admission.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getOrigin() {
            return origin;
        }

        /**
         * From where patient was admitted (physician referral, transfer).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getAdmitSource() {
            return admitSource;
        }

        /**
         * Indicates that this encounter is directly related to a prior admission, often because the conditions addressed in the 
         * prior admission were not fully addressed.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getReAdmission() {
            return reAdmission;
        }

        /**
         * Location/organization to which the patient is discharged.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getDestination() {
            return destination;
        }

        /**
         * Category or kind of location after discharge.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getDischargeDisposition() {
            return dischargeDisposition;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (preAdmissionIdentifier != null) || 
                (origin != null) || 
                (admitSource != null) || 
                (reAdmission != null) || 
                (destination != null) || 
                (dischargeDisposition != null);
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
                    accept(preAdmissionIdentifier, "preAdmissionIdentifier", visitor);
                    accept(origin, "origin", visitor);
                    accept(admitSource, "admitSource", visitor);
                    accept(reAdmission, "reAdmission", visitor);
                    accept(destination, "destination", visitor);
                    accept(dischargeDisposition, "dischargeDisposition", visitor);
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
            Admission other = (Admission) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(preAdmissionIdentifier, other.preAdmissionIdentifier) && 
                Objects.equals(origin, other.origin) && 
                Objects.equals(admitSource, other.admitSource) && 
                Objects.equals(reAdmission, other.reAdmission) && 
                Objects.equals(destination, other.destination) && 
                Objects.equals(dischargeDisposition, other.dischargeDisposition);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    preAdmissionIdentifier, 
                    origin, 
                    admitSource, 
                    reAdmission, 
                    destination, 
                    dischargeDisposition);
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
            private Identifier preAdmissionIdentifier;
            private Reference origin;
            private CodeableConcept admitSource;
            private CodeableConcept reAdmission;
            private Reference destination;
            private CodeableConcept dischargeDisposition;

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
             * Pre-admission identifier.
             * 
             * @param preAdmissionIdentifier
             *     Pre-admission identifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder preAdmissionIdentifier(Identifier preAdmissionIdentifier) {
                this.preAdmissionIdentifier = preAdmissionIdentifier;
                return this;
            }

            /**
             * The location/organization from which the patient came before admission.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param origin
             *     The location/organization from which the patient came before admission
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder origin(Reference origin) {
                this.origin = origin;
                return this;
            }

            /**
             * From where patient was admitted (physician referral, transfer).
             * 
             * @param admitSource
             *     From where patient was admitted (physician referral, transfer)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder admitSource(CodeableConcept admitSource) {
                this.admitSource = admitSource;
                return this;
            }

            /**
             * Indicates that this encounter is directly related to a prior admission, often because the conditions addressed in the 
             * prior admission were not fully addressed.
             * 
             * @param reAdmission
             *     Indicates that the patient is being re-admitted
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reAdmission(CodeableConcept reAdmission) {
                this.reAdmission = reAdmission;
                return this;
            }

            /**
             * Location/organization to which the patient is discharged.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param destination
             *     Location/organization to which the patient is discharged
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder destination(Reference destination) {
                this.destination = destination;
                return this;
            }

            /**
             * Category or kind of location after discharge.
             * 
             * @param dischargeDisposition
             *     Category or kind of location after discharge
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dischargeDisposition(CodeableConcept dischargeDisposition) {
                this.dischargeDisposition = dischargeDisposition;
                return this;
            }

            /**
             * Build the {@link Admission}
             * 
             * @return
             *     An immutable object of type {@link Admission}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Admission per the base specification
             */
            @Override
            public Admission build() {
                Admission admission = new Admission(this);
                if (validating) {
                    validate(admission);
                }
                return admission;
            }

            protected void validate(Admission admission) {
                super.validate(admission);
                ValidationSupport.checkReferenceType(admission.origin, "origin", "Location", "Organization");
                ValidationSupport.checkReferenceType(admission.destination, "destination", "Location", "Organization");
                ValidationSupport.requireValueOrChildren(admission);
            }

            protected Builder from(Admission admission) {
                super.from(admission);
                preAdmissionIdentifier = admission.preAdmissionIdentifier;
                origin = admission.origin;
                admitSource = admission.admitSource;
                reAdmission = admission.reAdmission;
                destination = admission.destination;
                dischargeDisposition = admission.dischargeDisposition;
                return this;
            }
        }
    }

    /**
     * List of locations where the patient has been during this encounter.
     */
    public static class Location extends BackboneElement {
        @ReferenceTarget({ "Location" })
        @Required
        private final Reference location;
        @Binding(
            bindingName = "EncounterLocationStatus",
            strength = BindingStrength.Value.REQUIRED,
            description = "The status of the location.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-location-status|5.0.0"
        )
        private final EncounterLocationStatus status;
        @Binding(
            bindingName = "LocationForm",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Physical form of the location.",
            valueSet = "http://hl7.org/fhir/ValueSet/location-form"
        )
        private final CodeableConcept form;
        private final Period period;

        private Location(Builder builder) {
            super(builder);
            location = builder.location;
            status = builder.status;
            form = builder.form;
            period = builder.period;
        }

        /**
         * The location where the encounter takes place.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getLocation() {
            return location;
        }

        /**
         * The status of the participants' presence at the specified location during the period specified. If the participant is 
         * no longer at the location, then the period will have an end date/time.
         * 
         * @return
         *     An immutable object of type {@link EncounterLocationStatus} that may be null.
         */
        public EncounterLocationStatus getStatus() {
            return status;
        }

        /**
         * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either 
         * messaging or query.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getForm() {
            return form;
        }

        /**
         * Time period during which the patient was present at the location.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (location != null) || 
                (status != null) || 
                (form != null) || 
                (period != null);
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
                    accept(location, "location", visitor);
                    accept(status, "status", visitor);
                    accept(form, "form", visitor);
                    accept(period, "period", visitor);
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
            Location other = (Location) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(location, other.location) && 
                Objects.equals(status, other.status) && 
                Objects.equals(form, other.form) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    location, 
                    status, 
                    form, 
                    period);
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
            private Reference location;
            private EncounterLocationStatus status;
            private CodeableConcept form;
            private Period period;

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
             * The location where the encounter takes place.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     Location the encounter takes place
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * The status of the participants' presence at the specified location during the period specified. If the participant is 
             * no longer at the location, then the period will have an end date/time.
             * 
             * @param status
             *     planned | active | reserved | completed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder status(EncounterLocationStatus status) {
                this.status = status;
                return this;
            }

            /**
             * This will be used to specify the required levels (bed/ward/room/etc.) desired to be recorded to simplify either 
             * messaging or query.
             * 
             * @param form
             *     The physical type of the location (usually the level in the location hierarchy - bed, room, ward, virtual etc.)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder form(CodeableConcept form) {
                this.form = form;
                return this;
            }

            /**
             * Time period during which the patient was present at the location.
             * 
             * @param period
             *     Time period during which the patient was present at the location
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link Location}
             * 
             * <p>Required elements:
             * <ul>
             * <li>location</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Location}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Location per the base specification
             */
            @Override
            public Location build() {
                Location location = new Location(this);
                if (validating) {
                    validate(location);
                }
                return location;
            }

            protected void validate(Location location) {
                super.validate(location);
                ValidationSupport.requireNonNull(location.location, "location");
                ValidationSupport.checkReferenceType(location.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(location);
            }

            protected Builder from(Location location) {
                super.from(location);
                this.location = location.location;
                status = location.status;
                form = location.form;
                period = location.period;
                return this;
            }
        }
    }
}
