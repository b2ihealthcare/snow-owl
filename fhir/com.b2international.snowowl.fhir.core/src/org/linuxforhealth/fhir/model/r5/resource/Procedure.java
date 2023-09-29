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
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
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
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ProcedureStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * An action that is or was performed on or for a patient, practitioner, device, organization, or location. For example, 
 * this can be a physical intervention on a patient like an operation, or less invasive like long term services, 
 * counseling, or hypnotherapy. This can be a quality or safety inspection for a location, organization, or device. This 
 * can be an accreditation procedure on a practitioner for licensing.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "prc-1",
    level = "Rule",
    location = "Procedure.performer",
    description = "Procedure.performer.onBehalfOf can only be populated when performer.actor isn't Practitioner or PractitionerRole",
    expression = "onBehalfOf.exists() and actor.resolve().exists() implies actor.resolve().where($this is Practitioner or $this is PractitionerRole).empty()",
    source = "http://hl7.org/fhir/StructureDefinition/Procedure"
)
@Constraint(
    id = "procedure-2",
    level = "Warning",
    location = "focalDevice.action",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/device-action",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/device-action', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Procedure",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Procedure extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    @Summary
    @ReferenceTarget({ "CarePlan", "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "Procedure", "Observation", "MedicationAdministration" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "ProcedureStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A code specifying the state of the procedure.",
        valueSet = "http://hl7.org/fhir/ValueSet/event-status|5.0.0"
    )
    @Required
    private final ProcedureStatus status;
    @Summary
    @Binding(
        bindingName = "ProcedureNegationReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code that identifies the reason a procedure was not performed.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-not-performed-reason"
    )
    private final CodeableConcept statusReason;
    @Summary
    @Binding(
        bindingName = "ProcedureCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code that classifies a procedure for searching, sorting and display purposes.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "ProcedureCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code to identify a specific procedure .",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-code"
    )
    private final CodeableConcept code;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Device", "Practitioner", "Organization", "Location" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "RelatedPerson", "Practitioner", "Organization", "CareTeam", "PractitionerRole", "Specimen" })
    private final Reference focus;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class, String.class, Age.class, Range.class, Timing.class })
    private final Element occurrence;
    @Summary
    private final DateTime recorded;
    @Summary
    @ReferenceTarget({ "Patient", "RelatedPerson", "Practitioner", "PractitionerRole" })
    private final Reference recorder;
    @Summary
    @ReferenceTarget({ "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization" })
    @Choice({ Boolean.class, Reference.class })
    private final Element reported;
    @Summary
    private final List<Performer> performer;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Summary
    @Binding(
        bindingName = "ProcedureReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code that identifies the reason a procedure is  required.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-reason"
    )
    private final List<CodeableReference> reason;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final List<CodeableConcept> bodySite;
    @Summary
    @Binding(
        bindingName = "ProcedureOutcome",
        strength = BindingStrength.Value.EXAMPLE,
        description = "An outcome of a procedure - whether it was resolved or otherwise.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-outcome"
    )
    private final CodeableConcept outcome;
    @ReferenceTarget({ "DiagnosticReport", "DocumentReference", "Composition" })
    private final List<Reference> report;
    @Binding(
        bindingName = "ProcedureComplication",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes describing complications that resulted from a procedure.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableReference> complication;
    @Binding(
        bindingName = "ProcedureFollowUp",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Specific follow up required for a procedure e.g. removal of sutures.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-followup"
    )
    private final List<CodeableConcept> followUp;
    private final List<Annotation> note;
    private final List<FocalDevice> focalDevice;
    @Binding(
        bindingName = "ProcedureUsed",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes describing items used during a procedure.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-type"
    )
    private final List<CodeableReference> used;
    private final List<Reference> supportingInfo;

    private Procedure(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        statusReason = builder.statusReason;
        category = Collections.unmodifiableList(builder.category);
        code = builder.code;
        subject = builder.subject;
        focus = builder.focus;
        encounter = builder.encounter;
        occurrence = builder.occurrence;
        recorded = builder.recorded;
        recorder = builder.recorder;
        reported = builder.reported;
        performer = Collections.unmodifiableList(builder.performer);
        location = builder.location;
        reason = Collections.unmodifiableList(builder.reason);
        bodySite = Collections.unmodifiableList(builder.bodySite);
        outcome = builder.outcome;
        report = Collections.unmodifiableList(builder.report);
        complication = Collections.unmodifiableList(builder.complication);
        followUp = Collections.unmodifiableList(builder.followUp);
        note = Collections.unmodifiableList(builder.note);
        focalDevice = Collections.unmodifiableList(builder.focalDevice);
        used = Collections.unmodifiableList(builder.used);
        supportingInfo = Collections.unmodifiableList(builder.supportingInfo);
    }

    /**
     * Business identifiers assigned to this procedure by the performer or other systems which remain constant as the 
     * resource is updated and is propagated from server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a FHIR-defined protocol, guideline, order set or other definition that is adhered to in whole or 
     * in part by this Procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol, guideline, order set or other definition that is adhered to in 
     * whole or in part by this Procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * A reference to a resource that contains details of the request for this procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A larger event of which this particular procedure is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code specifying the state of the procedure. Generally, this will be the in-progress or completed state.
     * 
     * @return
     *     An immutable object of type {@link ProcedureStatus} that is non-null.
     */
    public ProcedureStatus getStatus() {
        return status;
    }

    /**
     * Captures the reason for the current state of the procedure.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * A code that classifies the procedure for searching, sorting and display purposes (e.g. "Surgical Procedure").
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * The specific procedure that is performed. Use text if the exact nature of the procedure cannot be coded (e.g. 
     * "Laparoscopic Appendectomy").
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * On whom or on what the procedure was performed. This is usually an individual human, but can also be performed on 
     * animals, groups of humans or animals, organizations or practitioners (for licensing), locations or devices (for safety 
     * inspections or regulatory authorizations). If the actual focus of the procedure is different from the subject, the 
     * focus element specifies the actual focus of the procedure.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Who is the target of the procedure when it is not the subject of record only. If focus is not present, then subject is 
     * the focus. If focus is present and the subject is one of the targets of the procedure, include subject as a focus as 
     * well. If focus is present and the subject is not included in focus, it implies that the procedure was only targeted on 
     * the focus. For example, when a caregiver is given education for a patient, the caregiver would be the focus and the 
     * procedure record is associated with the subject (e.g. patient). For example, use focus when recording the target of 
     * the education, training, or counseling is the parent or relative of a patient.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getFocus() {
        return focus;
    }

    /**
     * The Encounter during which this Procedure was created or performed or to which the creation of this record is tightly 
     * associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Estimated or actual date, date-time, period, or age when the procedure did occur or is occurring. Allows a period to 
     * support complex procedures that span more than one date, and also allows for the length of the procedure to be 
     * captured.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period}, {@link String}, {@link Age}, {@link Range} or {@link 
     *     Timing} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * The date the occurrence of the procedure was first captured in the record regardless of Procedure.status (potentially 
     * after the occurrence of the event).
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getRecorded() {
        return recorded;
    }

    /**
     * Individual who recorded the record and takes responsibility for its content.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRecorder() {
        return recorder;
    }

    /**
     * Indicates if this record was captured as a secondary 'reported' record rather than as an original primary source-of-
     * truth record. It may also indicate the source of the report.
     * 
     * @return
     *     An immutable object of type {@link Boolean} or {@link Reference} that may be null.
     */
    public Element getReported() {
        return reported;
    }

    /**
     * Indicates who or what performed the procedure and how they were involved.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * The location where the procedure actually happened. E.g. a newborn at home, a tracheostomy at a restaurant.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * The coded reason or reference why the procedure was performed. This may be a coded entity of some type, be present as 
     * text, or be a reference to one of several resources that justify the procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Detailed and structured anatomical location information. Multiple locations are allowed - e.g. multiple punch biopsies 
     * of a lesion.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getBodySite() {
        return bodySite;
    }

    /**
     * The outcome of the procedure - did it resolve the reasons for the procedure being performed?
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getOutcome() {
        return outcome;
    }

    /**
     * This could be a histology result, pathology report, surgical report, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReport() {
        return report;
    }

    /**
     * Any complications that occurred during the procedure, or in the immediate post-performance period. These are generally 
     * tracked separately from the notes, which will typically describe the procedure itself rather than any 'post procedure' 
     * issues.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getComplication() {
        return complication;
    }

    /**
     * If the procedure required specific follow up - e.g. removal of sutures. The follow up may be represented as a simple 
     * note or could potentially be more complex, in which case the CarePlan resource can be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getFollowUp() {
        return followUp;
    }

    /**
     * Any other notes and comments about the procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement, fitting a prosthesis, 
     * attaching a wound-vac, etc.) as a focal portion of the Procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link FocalDevice} that may be empty.
     */
    public List<FocalDevice> getFocalDevice() {
        return focalDevice;
    }

    /**
     * Identifies medications, devices and any other substance used as part of the procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getUsed() {
        return used;
    }

    /**
     * Other resources from the patient record that may be relevant to the procedure. The information from these resources 
     * was either used to create the instance or is provided to help with its interpretation. This extension should not be 
     * used if more specific inline elements or extensions are available.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInfo() {
        return supportingInfo;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !instantiatesCanonical.isEmpty() || 
            !instantiatesUri.isEmpty() || 
            !basedOn.isEmpty() || 
            !partOf.isEmpty() || 
            (status != null) || 
            (statusReason != null) || 
            !category.isEmpty() || 
            (code != null) || 
            (subject != null) || 
            (focus != null) || 
            (encounter != null) || 
            (occurrence != null) || 
            (recorded != null) || 
            (recorder != null) || 
            (reported != null) || 
            !performer.isEmpty() || 
            (location != null) || 
            !reason.isEmpty() || 
            !bodySite.isEmpty() || 
            (outcome != null) || 
            !report.isEmpty() || 
            !complication.isEmpty() || 
            !followUp.isEmpty() || 
            !note.isEmpty() || 
            !focalDevice.isEmpty() || 
            !used.isEmpty() || 
            !supportingInfo.isEmpty();
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
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(subject, "subject", visitor);
                accept(focus, "focus", visitor);
                accept(encounter, "encounter", visitor);
                accept(occurrence, "occurrence", visitor);
                accept(recorded, "recorded", visitor);
                accept(recorder, "recorder", visitor);
                accept(reported, "reported", visitor);
                accept(performer, "performer", visitor, Performer.class);
                accept(location, "location", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(bodySite, "bodySite", visitor, CodeableConcept.class);
                accept(outcome, "outcome", visitor);
                accept(report, "report", visitor, Reference.class);
                accept(complication, "complication", visitor, CodeableReference.class);
                accept(followUp, "followUp", visitor, CodeableConcept.class);
                accept(note, "note", visitor, Annotation.class);
                accept(focalDevice, "focalDevice", visitor, FocalDevice.class);
                accept(used, "used", visitor, CodeableReference.class);
                accept(supportingInfo, "supportingInfo", visitor, Reference.class);
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
        Procedure other = (Procedure) obj;
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
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(recorded, other.recorded) && 
            Objects.equals(recorder, other.recorder) && 
            Objects.equals(reported, other.reported) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(location, other.location) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(outcome, other.outcome) && 
            Objects.equals(report, other.report) && 
            Objects.equals(complication, other.complication) && 
            Objects.equals(followUp, other.followUp) && 
            Objects.equals(note, other.note) && 
            Objects.equals(focalDevice, other.focalDevice) && 
            Objects.equals(used, other.used) && 
            Objects.equals(supportingInfo, other.supportingInfo);
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
                partOf, 
                status, 
                statusReason, 
                category, 
                code, 
                subject, 
                focus, 
                encounter, 
                occurrence, 
                recorded, 
                recorder, 
                reported, 
                performer, 
                location, 
                reason, 
                bodySite, 
                outcome, 
                report, 
                complication, 
                followUp, 
                note, 
                focalDevice, 
                used, 
                supportingInfo);
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
        private List<Reference> partOf = new ArrayList<>();
        private ProcedureStatus status;
        private CodeableConcept statusReason;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept code;
        private Reference subject;
        private Reference focus;
        private Reference encounter;
        private Element occurrence;
        private DateTime recorded;
        private Reference recorder;
        private Element reported;
        private List<Performer> performer = new ArrayList<>();
        private Reference location;
        private List<CodeableReference> reason = new ArrayList<>();
        private List<CodeableConcept> bodySite = new ArrayList<>();
        private CodeableConcept outcome;
        private List<Reference> report = new ArrayList<>();
        private List<CodeableReference> complication = new ArrayList<>();
        private List<CodeableConcept> followUp = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<FocalDevice> focalDevice = new ArrayList<>();
        private List<CodeableReference> used = new ArrayList<>();
        private List<Reference> supportingInfo = new ArrayList<>();

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
         * Business identifiers assigned to this procedure by the performer or other systems which remain constant as the 
         * resource is updated and is propagated from server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Identifiers for this procedure
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
         * Business identifiers assigned to this procedure by the performer or other systems which remain constant as the 
         * resource is updated and is propagated from server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Identifiers for this procedure
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
         * The URL pointing to a FHIR-defined protocol, guideline, order set or other definition that is adhered to in whole or 
         * in part by this Procedure.
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
         * The URL pointing to a FHIR-defined protocol, guideline, order set or other definition that is adhered to in whole or 
         * in part by this Procedure.
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
         * The URL pointing to an externally maintained protocol, guideline, order set or other definition that is adhered to in 
         * whole or in part by this Procedure.
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
         * The URL pointing to an externally maintained protocol, guideline, order set or other definition that is adhered to in 
         * whole or in part by this Procedure.
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
         * A reference to a resource that contains details of the request for this procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     A request for this procedure
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
         * A reference to a resource that contains details of the request for this procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     A request for this procedure
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
         * A larger event of which this particular procedure is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * <li>{@link Observation}</li>
         * <li>{@link MedicationAdministration}</li>
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
         * A larger event of which this particular procedure is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * <li>{@link Observation}</li>
         * <li>{@link MedicationAdministration}</li>
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
         * A code specifying the state of the procedure. Generally, this will be the in-progress or completed state.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ProcedureStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Captures the reason for the current state of the procedure.
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
         * A code that classifies the procedure for searching, sorting and display purposes (e.g. "Surgical Procedure").
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of the procedure
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
         * A code that classifies the procedure for searching, sorting and display purposes (e.g. "Surgical Procedure").
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of the procedure
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
         * The specific procedure that is performed. Use text if the exact nature of the procedure cannot be coded (e.g. 
         * "Laparoscopic Appendectomy").
         * 
         * @param code
         *     Identification of the procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * On whom or on what the procedure was performed. This is usually an individual human, but can also be performed on 
         * animals, groups of humans or animals, organizations or practitioners (for licensing), locations or devices (for safety 
         * inspections or regulatory authorizations). If the actual focus of the procedure is different from the subject, the 
         * focus element specifies the actual focus of the procedure.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Device}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param subject
         *     Individual or entity the procedure was performed on
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Who is the target of the procedure when it is not the subject of record only. If focus is not present, then subject is 
         * the focus. If focus is present and the subject is one of the targets of the procedure, include subject as a focus as 
         * well. If focus is present and the subject is not included in focus, it implies that the procedure was only targeted on 
         * the focus. For example, when a caregiver is given education for a patient, the caregiver would be the focus and the 
         * procedure record is associated with the subject (e.g. patient). For example, use focus when recording the target of 
         * the education, training, or counseling is the parent or relative of a patient.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param focus
         *     Who is the target of the procedure when it is not the subject of record only
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focus(Reference focus) {
            this.focus = focus;
            return this;
        }

        /**
         * The Encounter during which this Procedure was created or performed or to which the creation of this record is tightly 
         * associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The Encounter during which this Procedure was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Convenience method for setting {@code occurrence} with choice type String.
         * 
         * @param occurrence
         *     When the procedure occurred or is occurring
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #occurrence(Element)
         */
        public Builder occurrence(java.lang.String occurrence) {
            this.occurrence = (occurrence == null) ? null : String.of(occurrence);
            return this;
        }

        /**
         * Estimated or actual date, date-time, period, or age when the procedure did occur or is occurring. Allows a period to 
         * support complex procedures that span more than one date, and also allows for the length of the procedure to be 
         * captured.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link String}</li>
         * <li>{@link Age}</li>
         * <li>{@link Range}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurrence
         *     When the procedure occurred or is occurring
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * The date the occurrence of the procedure was first captured in the record regardless of Procedure.status (potentially 
         * after the occurrence of the event).
         * 
         * @param recorded
         *     When the procedure was first captured in the subject's record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorded(DateTime recorded) {
            this.recorded = recorded;
            return this;
        }

        /**
         * Individual who recorded the record and takes responsibility for its content.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param recorder
         *     Who recorded the procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorder(Reference recorder) {
            this.recorder = recorder;
            return this;
        }

        /**
         * Convenience method for setting {@code reported} with choice type Boolean.
         * 
         * @param reported
         *     Reported rather than primary record
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #reported(Element)
         */
        public Builder reported(java.lang.Boolean reported) {
            this.reported = (reported == null) ? null : Boolean.of(reported);
            return this;
        }

        /**
         * Indicates if this record was captured as a secondary 'reported' record rather than as an original primary source-of-
         * truth record. It may also indicate the source of the report.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Boolean}</li>
         * <li>{@link Reference}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param reported
         *     Reported rather than primary record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reported(Element reported) {
            this.reported = reported;
            return this;
        }

        /**
         * Indicates who or what performed the procedure and how they were involved.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed the procedure and what they did
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(Performer... performer) {
            for (Performer value : performer) {
                this.performer.add(value);
            }
            return this;
        }

        /**
         * Indicates who or what performed the procedure and how they were involved.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed the procedure and what they did
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder performer(Collection<Performer> performer) {
            this.performer = new ArrayList<>(performer);
            return this;
        }

        /**
         * The location where the procedure actually happened. E.g. a newborn at home, a tracheostomy at a restaurant.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where the procedure happened
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * The coded reason or reference why the procedure was performed. This may be a coded entity of some type, be present as 
         * text, or be a reference to one of several resources that justify the procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The justification that the procedure was performed
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
         * The coded reason or reference why the procedure was performed. This may be a coded entity of some type, be present as 
         * text, or be a reference to one of several resources that justify the procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The justification that the procedure was performed
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
         * Detailed and structured anatomical location information. Multiple locations are allowed - e.g. multiple punch biopsies 
         * of a lesion.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Target body sites
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
         * Detailed and structured anatomical location information. Multiple locations are allowed - e.g. multiple punch biopsies 
         * of a lesion.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Target body sites
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
         * The outcome of the procedure - did it resolve the reasons for the procedure being performed?
         * 
         * @param outcome
         *     The result of procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcome(CodeableConcept outcome) {
            this.outcome = outcome;
            return this;
        }

        /**
         * This could be a histology result, pathology report, surgical report, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DiagnosticReport}</li>
         * <li>{@link DocumentReference}</li>
         * <li>{@link Composition}</li>
         * </ul>
         * 
         * @param report
         *     Any report resulting from the procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder report(Reference... report) {
            for (Reference value : report) {
                this.report.add(value);
            }
            return this;
        }

        /**
         * This could be a histology result, pathology report, surgical report, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DiagnosticReport}</li>
         * <li>{@link DocumentReference}</li>
         * <li>{@link Composition}</li>
         * </ul>
         * 
         * @param report
         *     Any report resulting from the procedure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder report(Collection<Reference> report) {
            this.report = new ArrayList<>(report);
            return this;
        }

        /**
         * Any complications that occurred during the procedure, or in the immediate post-performance period. These are generally 
         * tracked separately from the notes, which will typically describe the procedure itself rather than any 'post procedure' 
         * issues.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param complication
         *     Complication following the procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder complication(CodeableReference... complication) {
            for (CodeableReference value : complication) {
                this.complication.add(value);
            }
            return this;
        }

        /**
         * Any complications that occurred during the procedure, or in the immediate post-performance period. These are generally 
         * tracked separately from the notes, which will typically describe the procedure itself rather than any 'post procedure' 
         * issues.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param complication
         *     Complication following the procedure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder complication(Collection<CodeableReference> complication) {
            this.complication = new ArrayList<>(complication);
            return this;
        }

        /**
         * If the procedure required specific follow up - e.g. removal of sutures. The follow up may be represented as a simple 
         * note or could potentially be more complex, in which case the CarePlan resource can be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param followUp
         *     Instructions for follow up
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder followUp(CodeableConcept... followUp) {
            for (CodeableConcept value : followUp) {
                this.followUp.add(value);
            }
            return this;
        }

        /**
         * If the procedure required specific follow up - e.g. removal of sutures. The follow up may be represented as a simple 
         * note or could potentially be more complex, in which case the CarePlan resource can be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param followUp
         *     Instructions for follow up
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder followUp(Collection<CodeableConcept> followUp) {
            this.followUp = new ArrayList<>(followUp);
            return this;
        }

        /**
         * Any other notes and comments about the procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional information about the procedure
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
         * Any other notes and comments about the procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional information about the procedure
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
         * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement, fitting a prosthesis, 
         * attaching a wound-vac, etc.) as a focal portion of the Procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focalDevice
         *     Manipulated, implanted, or removed device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focalDevice(FocalDevice... focalDevice) {
            for (FocalDevice value : focalDevice) {
                this.focalDevice.add(value);
            }
            return this;
        }

        /**
         * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement, fitting a prosthesis, 
         * attaching a wound-vac, etc.) as a focal portion of the Procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focalDevice
         *     Manipulated, implanted, or removed device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder focalDevice(Collection<FocalDevice> focalDevice) {
            this.focalDevice = new ArrayList<>(focalDevice);
            return this;
        }

        /**
         * Identifies medications, devices and any other substance used as part of the procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param used
         *     Items used during procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder used(CodeableReference... used) {
            for (CodeableReference value : used) {
                this.used.add(value);
            }
            return this;
        }

        /**
         * Identifies medications, devices and any other substance used as part of the procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param used
         *     Items used during procedure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder used(Collection<CodeableReference> used) {
            this.used = new ArrayList<>(used);
            return this;
        }

        /**
         * Other resources from the patient record that may be relevant to the procedure. The information from these resources 
         * was either used to create the instance or is provided to help with its interpretation. This extension should not be 
         * used if more specific inline elements or extensions are available.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Extra information relevant to the procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInfo(Reference... supportingInfo) {
            for (Reference value : supportingInfo) {
                this.supportingInfo.add(value);
            }
            return this;
        }

        /**
         * Other resources from the patient record that may be relevant to the procedure. The information from these resources 
         * was either used to create the instance or is provided to help with its interpretation. This extension should not be 
         * used if more specific inline elements or extensions are available.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Extra information relevant to the procedure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInfo(Collection<Reference> supportingInfo) {
            this.supportingInfo = new ArrayList<>(supportingInfo);
            return this;
        }

        /**
         * Build the {@link Procedure}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Procedure}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Procedure per the base specification
         */
        @Override
        public Procedure build() {
            Procedure procedure = new Procedure(this);
            if (validating) {
                validate(procedure);
            }
            return procedure;
        }

        protected void validate(Procedure procedure) {
            super.validate(procedure);
            ValidationSupport.checkList(procedure.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(procedure.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(procedure.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(procedure.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(procedure.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(procedure.status, "status");
            ValidationSupport.checkList(procedure.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(procedure.subject, "subject");
            ValidationSupport.choiceElement(procedure.occurrence, "occurrence", DateTime.class, Period.class, String.class, Age.class, Range.class, Timing.class);
            ValidationSupport.choiceElement(procedure.reported, "reported", Boolean.class, Reference.class);
            ValidationSupport.checkList(procedure.performer, "performer", Performer.class);
            ValidationSupport.checkList(procedure.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(procedure.bodySite, "bodySite", CodeableConcept.class);
            ValidationSupport.checkList(procedure.report, "report", Reference.class);
            ValidationSupport.checkList(procedure.complication, "complication", CodeableReference.class);
            ValidationSupport.checkList(procedure.followUp, "followUp", CodeableConcept.class);
            ValidationSupport.checkList(procedure.note, "note", Annotation.class);
            ValidationSupport.checkList(procedure.focalDevice, "focalDevice", FocalDevice.class);
            ValidationSupport.checkList(procedure.used, "used", CodeableReference.class);
            ValidationSupport.checkList(procedure.supportingInfo, "supportingInfo", Reference.class);
            ValidationSupport.checkReferenceType(procedure.basedOn, "basedOn", "CarePlan", "ServiceRequest");
            ValidationSupport.checkReferenceType(procedure.partOf, "partOf", "Procedure", "Observation", "MedicationAdministration");
            ValidationSupport.checkReferenceType(procedure.subject, "subject", "Patient", "Group", "Device", "Practitioner", "Organization", "Location");
            ValidationSupport.checkReferenceType(procedure.focus, "focus", "Patient", "Group", "RelatedPerson", "Practitioner", "Organization", "CareTeam", "PractitionerRole", "Specimen");
            ValidationSupport.checkReferenceType(procedure.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(procedure.recorder, "recorder", "Patient", "RelatedPerson", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(procedure.reported, "reported", "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(procedure.location, "location", "Location");
            ValidationSupport.checkReferenceType(procedure.report, "report", "DiagnosticReport", "DocumentReference", "Composition");
        }

        protected Builder from(Procedure procedure) {
            super.from(procedure);
            identifier.addAll(procedure.identifier);
            instantiatesCanonical.addAll(procedure.instantiatesCanonical);
            instantiatesUri.addAll(procedure.instantiatesUri);
            basedOn.addAll(procedure.basedOn);
            partOf.addAll(procedure.partOf);
            status = procedure.status;
            statusReason = procedure.statusReason;
            category.addAll(procedure.category);
            code = procedure.code;
            subject = procedure.subject;
            focus = procedure.focus;
            encounter = procedure.encounter;
            occurrence = procedure.occurrence;
            recorded = procedure.recorded;
            recorder = procedure.recorder;
            reported = procedure.reported;
            performer.addAll(procedure.performer);
            location = procedure.location;
            reason.addAll(procedure.reason);
            bodySite.addAll(procedure.bodySite);
            outcome = procedure.outcome;
            report.addAll(procedure.report);
            complication.addAll(procedure.complication);
            followUp.addAll(procedure.followUp);
            note.addAll(procedure.note);
            focalDevice.addAll(procedure.focalDevice);
            used.addAll(procedure.used);
            supportingInfo.addAll(procedure.supportingInfo);
            return this;
        }
    }

    /**
     * Indicates who or what performed the procedure and how they were involved.
     */
    public static class Performer extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "ProcedurePerformerRole",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A code that identifies the role of a performer of the procedure.",
            valueSet = "http://hl7.org/fhir/ValueSet/performer-role"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device", "CareTeam", "HealthcareService" })
        @Required
        private final Reference actor;
        @ReferenceTarget({ "Organization" })
        private final Reference onBehalfOf;
        private final Period period;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
            onBehalfOf = builder.onBehalfOf;
            period = builder.period;
        }

        /**
         * Distinguishes the type of involvement of the performer in the procedure. For example, surgeon, anaesthetist, 
         * endoscopist.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Indicates who or what performed the procedure.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getActor() {
            return actor;
        }

        /**
         * The Organization the Patient, RelatedPerson, Device, CareTeam, and HealthcareService was acting on behalf of.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getOnBehalfOf() {
            return onBehalfOf;
        }

        /**
         * Time period during which the performer performed the procedure.
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
                (function != null) || 
                (actor != null) || 
                (onBehalfOf != null) || 
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
                    accept(function, "function", visitor);
                    accept(actor, "actor", visitor);
                    accept(onBehalfOf, "onBehalfOf", visitor);
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
            Performer other = (Performer) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(function, other.function) && 
                Objects.equals(actor, other.actor) && 
                Objects.equals(onBehalfOf, other.onBehalfOf) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    function, 
                    actor, 
                    onBehalfOf, 
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
            private CodeableConcept function;
            private Reference actor;
            private Reference onBehalfOf;
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
             * Distinguishes the type of involvement of the performer in the procedure. For example, surgeon, anaesthetist, 
             * endoscopist.
             * 
             * @param function
             *     Type of performance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Indicates who or what performed the procedure.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Device}</li>
             * <li>{@link CareTeam}</li>
             * <li>{@link HealthcareService}</li>
             * </ul>
             * 
             * @param actor
             *     Who performed the procedure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
                this.actor = actor;
                return this;
            }

            /**
             * The Organization the Patient, RelatedPerson, Device, CareTeam, and HealthcareService was acting on behalf of.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param onBehalfOf
             *     Organization the device or practitioner was acting for
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder onBehalfOf(Reference onBehalfOf) {
                this.onBehalfOf = onBehalfOf;
                return this;
            }

            /**
             * Time period during which the performer performed the procedure.
             * 
             * @param period
             *     When the performer performed the procedure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link Performer}
             * 
             * <p>Required elements:
             * <ul>
             * <li>actor</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Performer}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Performer per the base specification
             */
            @Override
            public Performer build() {
                Performer performer = new Performer(this);
                if (validating) {
                    validate(performer);
                }
                return performer;
            }

            protected void validate(Performer performer) {
                super.validate(performer);
                ValidationSupport.requireNonNull(performer.actor, "actor");
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device", "CareTeam", "HealthcareService");
                ValidationSupport.checkReferenceType(performer.onBehalfOf, "onBehalfOf", "Organization");
                ValidationSupport.requireValueOrChildren(performer);
            }

            protected Builder from(Performer performer) {
                super.from(performer);
                function = performer.function;
                actor = performer.actor;
                onBehalfOf = performer.onBehalfOf;
                period = performer.period;
                return this;
            }
        }
    }

    /**
     * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement, fitting a prosthesis, 
     * attaching a wound-vac, etc.) as a focal portion of the Procedure.
     */
    public static class FocalDevice extends BackboneElement {
        @Binding(
            bindingName = "DeviceActionKind",
            strength = BindingStrength.Value.PREFERRED,
            description = "A kind of change that happened to the device during the procedure.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-action"
        )
        private final CodeableConcept action;
        @ReferenceTarget({ "Device" })
        @Required
        private final Reference manipulated;

        private FocalDevice(Builder builder) {
            super(builder);
            action = builder.action;
            manipulated = builder.manipulated;
        }

        /**
         * The kind of change that happened to the device during the procedure.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getAction() {
            return action;
        }

        /**
         * The device that was manipulated (changed) during the procedure.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getManipulated() {
            return manipulated;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (action != null) || 
                (manipulated != null);
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
                    accept(action, "action", visitor);
                    accept(manipulated, "manipulated", visitor);
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
            FocalDevice other = (FocalDevice) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(action, other.action) && 
                Objects.equals(manipulated, other.manipulated);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    action, 
                    manipulated);
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
            private CodeableConcept action;
            private Reference manipulated;

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
             * The kind of change that happened to the device during the procedure.
             * 
             * @param action
             *     Kind of change to device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder action(CodeableConcept action) {
                this.action = action;
                return this;
            }

            /**
             * The device that was manipulated (changed) during the procedure.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Device}</li>
             * </ul>
             * 
             * @param manipulated
             *     Device that was changed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder manipulated(Reference manipulated) {
                this.manipulated = manipulated;
                return this;
            }

            /**
             * Build the {@link FocalDevice}
             * 
             * <p>Required elements:
             * <ul>
             * <li>manipulated</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link FocalDevice}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid FocalDevice per the base specification
             */
            @Override
            public FocalDevice build() {
                FocalDevice focalDevice = new FocalDevice(this);
                if (validating) {
                    validate(focalDevice);
                }
                return focalDevice;
            }

            protected void validate(FocalDevice focalDevice) {
                super.validate(focalDevice);
                ValidationSupport.requireNonNull(focalDevice.manipulated, "manipulated");
                ValidationSupport.checkReferenceType(focalDevice.manipulated, "manipulated", "Device");
                ValidationSupport.requireValueOrChildren(focalDevice);
            }

            protected Builder from(FocalDevice focalDevice) {
                super.from(focalDevice);
                action = focalDevice.action;
                manipulated = focalDevice.manipulated;
                return this;
            }
        }
    }
}
