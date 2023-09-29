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
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationAdministrationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Describes the event of a patient consuming or otherwise being administered a medication. This may be as simple as 
 * swallowing a tablet or it may be a long running infusion. Related resources tie this event to the authorizing 
 * prescription, and the specific encounter between patient and health care practitioner.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "mad-1",
    level = "Rule",
    location = "MedicationAdministration.dosage",
    description = "If dosage attribute is present then SHALL have at least one of dosage.text or dosage.dose or dosage.rate[x]",
    expression = "(dose.exists() or rate.exists() or text.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/MedicationAdministration"
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MedicationAdministration extends DomainResource {
    private final List<Identifier> identifier;
    @ReferenceTarget({ "CarePlan" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "MedicationAdministration", "Procedure", "MedicationDispense" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "MedicationAdministrationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A set of codes indicating the current status of a MedicationAdministration.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-admin-status|5.0.0"
    )
    @Required
    private final MedicationAdministrationStatus status;
    @Binding(
        bindingName = "MedicationAdministrationNegationReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A set of codes indicating the reason why the MedicationAdministration is negated.",
        valueSet = "http://hl7.org/fhir/ValueSet/reason-medication-not-given-codes"
    )
    private final List<CodeableConcept> statusReason;
    @Binding(
        bindingName = "MedicationAdministrationLocation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept describing where the medication administered is expected to occur.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-admin-location"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "MedicationCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes identifying substance or product that can be administered.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    @Required
    private final CodeableReference medication;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final List<Reference> supportingInformation;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    @Required
    private final Element occurence;
    @Summary
    private final DateTime recorded;
    private final Boolean isSubPotent;
    @Binding(
        bindingName = "MedicationAdministrationSubPotentReason",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/administration-subpotent-reason"
    )
    private final List<CodeableConcept> subPotentReason;
    @Summary
    private final List<Performer> performer;
    @Binding(
        bindingName = "MedicationAdministrationReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A set of codes indicating the reason why the MedicationAdministration was made.",
        valueSet = "http://hl7.org/fhir/ValueSet/reason-medication-given-codes"
    )
    private final List<CodeableReference> reason;
    @ReferenceTarget({ "MedicationRequest" })
    private final Reference request;
    private final List<CodeableReference> device;
    private final List<Annotation> note;
    private final Dosage dosage;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> eventHistory;

    private MedicationAdministration(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        statusReason = Collections.unmodifiableList(builder.statusReason);
        category = Collections.unmodifiableList(builder.category);
        medication = builder.medication;
        subject = builder.subject;
        encounter = builder.encounter;
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        occurence = builder.occurence;
        recorded = builder.recorded;
        isSubPotent = builder.isSubPotent;
        subPotentReason = Collections.unmodifiableList(builder.subPotentReason);
        performer = Collections.unmodifiableList(builder.performer);
        reason = Collections.unmodifiableList(builder.reason);
        request = builder.request;
        device = Collections.unmodifiableList(builder.device);
        note = Collections.unmodifiableList(builder.note);
        dosage = builder.dosage;
        eventHistory = Collections.unmodifiableList(builder.eventHistory);
    }

    /**
     * Identifiers associated with this Medication Administration that are defined by business processes and/or used to refer 
     * to it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
     * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
     * server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A plan that is fulfilled in whole or in part by this MedicationAdministration.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
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
     * Will generally be set to show that the administration has been completed. For some long running administrations such 
     * as infusions, it is possible for an administration to be started but not completed or it may be paused while some 
     * other process is under way.
     * 
     * @return
     *     An immutable object of type {@link MedicationAdministrationStatus} that is non-null.
     */
    public MedicationAdministrationStatus getStatus() {
        return status;
    }

    /**
     * A code indicating why the administration was not performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getStatusReason() {
        return statusReason;
    }

    /**
     * The type of medication administration (for example, drug classification like ATC, where meds would be administered, 
     * legal category of the medication).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Identifies the medication that was administered. This is either a link to a resource representing the details of the 
     * medication or a simple attribute carrying a code that identifies the medication from a known list of medications.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getMedication() {
        return medication;
    }

    /**
     * The person or animal or group receiving the medication.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The visit, admission, or other contact between patient and health care provider during which the medication 
     * administration was performed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Additional information (for example, patient height and weight) that supports the administration of the medication. 
     * This attribute can be used to provide documentation of specific characteristics of the patient present at the time of 
     * administration. For example, if the dose says "give "x" if the heartrate exceeds "y"", then the heart rate can be 
     * included using this attribute.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * A specific date/time or interval of time during which the administration took place (or did not take place). For many 
     * administrations, such as swallowing a tablet the use of dateTime is more appropriate.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that is non-null.
     */
    public Element getOccurence() {
        return occurence;
    }

    /**
     * The date the occurrence of the MedicationAdministration was first captured in the record - potentially significantly 
     * after the occurrence of the event.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getRecorded() {
        return recorded;
    }

    /**
     * An indication that the full dose was not administered.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getIsSubPotent() {
        return isSubPotent;
    }

    /**
     * The reason or reasons why the full dose was not administered.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSubPotentReason() {
        return subPotentReason;
    }

    /**
     * The performer of the medication treatment. For devices this is the device that performed the administration of the 
     * medication. An IV Pump would be an example of a device that is performing the administration. Both the IV Pump and the 
     * practitioner that set the rate or bolus on the pump can be listed as performers.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * A code, Condition or observation that supports why the medication was administered.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * The original request, instruction or authority to perform the administration.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequest() {
        return request;
    }

    /**
     * The device that is to be used for the administration of the medication (for example, PCA Pump).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getDevice() {
        return device;
    }

    /**
     * Extra information about the medication administration that is not conveyed by the other attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Describes the medication dosage information details e.g. dose, rate, site, route, etc.
     * 
     * @return
     *     An immutable object of type {@link Dosage} that may be null.
     */
    public Dosage getDosage() {
        return dosage;
    }

    /**
     * A summary of the events of interest that have occurred, such as when the administration was verified.
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
            !partOf.isEmpty() || 
            (status != null) || 
            !statusReason.isEmpty() || 
            !category.isEmpty() || 
            (medication != null) || 
            (subject != null) || 
            (encounter != null) || 
            !supportingInformation.isEmpty() || 
            (occurence != null) || 
            (recorded != null) || 
            (isSubPotent != null) || 
            !subPotentReason.isEmpty() || 
            !performer.isEmpty() || 
            !reason.isEmpty() || 
            (request != null) || 
            !device.isEmpty() || 
            !note.isEmpty() || 
            (dosage != null) || 
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
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor, CodeableConcept.class);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(medication, "medication", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(occurence, "occurence", visitor);
                accept(recorded, "recorded", visitor);
                accept(isSubPotent, "isSubPotent", visitor);
                accept(subPotentReason, "subPotentReason", visitor, CodeableConcept.class);
                accept(performer, "performer", visitor, Performer.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(request, "request", visitor);
                accept(device, "device", visitor, CodeableReference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(dosage, "dosage", visitor);
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
        MedicationAdministration other = (MedicationAdministration) obj;
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
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(category, other.category) && 
            Objects.equals(medication, other.medication) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(occurence, other.occurence) && 
            Objects.equals(recorded, other.recorded) && 
            Objects.equals(isSubPotent, other.isSubPotent) && 
            Objects.equals(subPotentReason, other.subPotentReason) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(request, other.request) && 
            Objects.equals(device, other.device) && 
            Objects.equals(note, other.note) && 
            Objects.equals(dosage, other.dosage) && 
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
                partOf, 
                status, 
                statusReason, 
                category, 
                medication, 
                subject, 
                encounter, 
                supportingInformation, 
                occurence, 
                recorded, 
                isSubPotent, 
                subPotentReason, 
                performer, 
                reason, 
                request, 
                device, 
                note, 
                dosage, 
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
        private List<Reference> partOf = new ArrayList<>();
        private MedicationAdministrationStatus status;
        private List<CodeableConcept> statusReason = new ArrayList<>();
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableReference medication;
        private Reference subject;
        private Reference encounter;
        private List<Reference> supportingInformation = new ArrayList<>();
        private Element occurence;
        private DateTime recorded;
        private Boolean isSubPotent;
        private List<CodeableConcept> subPotentReason = new ArrayList<>();
        private List<Performer> performer = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private Reference request;
        private List<CodeableReference> device = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private Dosage dosage;
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
         * Identifiers associated with this Medication Administration that are defined by business processes and/or used to refer 
         * to it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
         * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
         * server to server.
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
         * Identifiers associated with this Medication Administration that are defined by business processes and/or used to refer 
         * to it when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to 
         * this resource by the performer or other systems and remain constant as the resource is updated and propagates from 
         * server to server.
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
         * A plan that is fulfilled in whole or in part by this MedicationAdministration.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param basedOn
         *     Plan this is fulfilled by this administration
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
         * A plan that is fulfilled in whole or in part by this MedicationAdministration.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param basedOn
         *     Plan this is fulfilled by this administration
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
         * A larger event of which this particular event is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link MedicationAdministration}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link MedicationDispense}</li>
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
         * <li>{@link MedicationAdministration}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link MedicationDispense}</li>
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
         * Will generally be set to show that the administration has been completed. For some long running administrations such 
         * as infusions, it is possible for an administration to be started but not completed or it may be paused while some 
         * other process is under way.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     in-progress | not-done | on-hold | completed | entered-in-error | stopped | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MedicationAdministrationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * A code indicating why the administration was not performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     Reason administration not performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusReason(CodeableConcept... statusReason) {
            for (CodeableConcept value : statusReason) {
                this.statusReason.add(value);
            }
            return this;
        }

        /**
         * A code indicating why the administration was not performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     Reason administration not performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder statusReason(Collection<CodeableConcept> statusReason) {
            this.statusReason = new ArrayList<>(statusReason);
            return this;
        }

        /**
         * The type of medication administration (for example, drug classification like ATC, where meds would be administered, 
         * legal category of the medication).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of medication administration
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
         * The type of medication administration (for example, drug classification like ATC, where meds would be administered, 
         * legal category of the medication).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of medication administration
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
         * Identifies the medication that was administered. This is either a link to a resource representing the details of the 
         * medication or a simple attribute carrying a code that identifies the medication from a known list of medications.
         * 
         * <p>This element is required.
         * 
         * @param medication
         *     What was administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder medication(CodeableReference medication) {
            this.medication = medication;
            return this;
        }

        /**
         * The person or animal or group receiving the medication.
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
         *     Who received medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The visit, admission, or other contact between patient and health care provider during which the medication 
         * administration was performed.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter administered as part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Additional information (for example, patient height and weight) that supports the administration of the medication. 
         * This attribute can be used to provide documentation of specific characteristics of the patient present at the time of 
         * administration. For example, if the dose says "give "x" if the heartrate exceeds "y"", then the heart rate can be 
         * included using this attribute.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information to support administration
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
         * Additional information (for example, patient height and weight) that supports the administration of the medication. 
         * This attribute can be used to provide documentation of specific characteristics of the patient present at the time of 
         * administration. For example, if the dose says "give "x" if the heartrate exceeds "y"", then the heart rate can be 
         * included using this attribute.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information to support administration
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
         * A specific date/time or interval of time during which the administration took place (or did not take place). For many 
         * administrations, such as swallowing a tablet the use of dateTime is more appropriate.
         * 
         * <p>This element is required.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurence
         *     Specific date/time or interval of time during which the administration took place (or did not take place)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurence(Element occurence) {
            this.occurence = occurence;
            return this;
        }

        /**
         * The date the occurrence of the MedicationAdministration was first captured in the record - potentially significantly 
         * after the occurrence of the event.
         * 
         * @param recorded
         *     When the MedicationAdministration was first captured in the subject's record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorded(DateTime recorded) {
            this.recorded = recorded;
            return this;
        }

        /**
         * Convenience method for setting {@code isSubPotent}.
         * 
         * @param isSubPotent
         *     Full dose was not administered
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #isSubPotent(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder isSubPotent(java.lang.Boolean isSubPotent) {
            this.isSubPotent = (isSubPotent == null) ? null : Boolean.of(isSubPotent);
            return this;
        }

        /**
         * An indication that the full dose was not administered.
         * 
         * @param isSubPotent
         *     Full dose was not administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder isSubPotent(Boolean isSubPotent) {
            this.isSubPotent = isSubPotent;
            return this;
        }

        /**
         * The reason or reasons why the full dose was not administered.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subPotentReason
         *     Reason full dose was not administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subPotentReason(CodeableConcept... subPotentReason) {
            for (CodeableConcept value : subPotentReason) {
                this.subPotentReason.add(value);
            }
            return this;
        }

        /**
         * The reason or reasons why the full dose was not administered.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subPotentReason
         *     Reason full dose was not administered
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subPotentReason(Collection<CodeableConcept> subPotentReason) {
            this.subPotentReason = new ArrayList<>(subPotentReason);
            return this;
        }

        /**
         * The performer of the medication treatment. For devices this is the device that performed the administration of the 
         * medication. An IV Pump would be an example of a device that is performing the administration. Both the IV Pump and the 
         * practitioner that set the rate or bolus on the pump can be listed as performers.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who or what performed the medication administration and what type of performance they did
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
         * The performer of the medication treatment. For devices this is the device that performed the administration of the 
         * medication. An IV Pump would be an example of a device that is performing the administration. Both the IV Pump and the 
         * practitioner that set the rate or bolus on the pump can be listed as performers.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who or what performed the medication administration and what type of performance they did
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
         * A code, Condition or observation that supports why the medication was administered.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Concept, condition or observation that supports why the medication was administered
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
         * A code, Condition or observation that supports why the medication was administered.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Concept, condition or observation that supports why the medication was administered
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
         * The original request, instruction or authority to perform the administration.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link MedicationRequest}</li>
         * </ul>
         * 
         * @param request
         *     Request administration performed against
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder request(Reference request) {
            this.request = request;
            return this;
        }

        /**
         * The device that is to be used for the administration of the medication (for example, PCA Pump).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param device
         *     Device used to administer
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
         * The device that is to be used for the administration of the medication (for example, PCA Pump).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param device
         *     Device used to administer
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
         * Extra information about the medication administration that is not conveyed by the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the administration
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
         * Extra information about the medication administration that is not conveyed by the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the administration
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
         * Describes the medication dosage information details e.g. dose, rate, site, route, etc.
         * 
         * @param dosage
         *     Details of how medication was taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dosage(Dosage dosage) {
            this.dosage = dosage;
            return this;
        }

        /**
         * A summary of the events of interest that have occurred, such as when the administration was verified.
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
         * A summary of the events of interest that have occurred, such as when the administration was verified.
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
         * Build the {@link MedicationAdministration}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>medication</li>
         * <li>subject</li>
         * <li>occurence</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link MedicationAdministration}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MedicationAdministration per the base specification
         */
        @Override
        public MedicationAdministration build() {
            MedicationAdministration medicationAdministration = new MedicationAdministration(this);
            if (validating) {
                validate(medicationAdministration);
            }
            return medicationAdministration;
        }

        protected void validate(MedicationAdministration medicationAdministration) {
            super.validate(medicationAdministration);
            ValidationSupport.checkList(medicationAdministration.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(medicationAdministration.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(medicationAdministration.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(medicationAdministration.status, "status");
            ValidationSupport.checkList(medicationAdministration.statusReason, "statusReason", CodeableConcept.class);
            ValidationSupport.checkList(medicationAdministration.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(medicationAdministration.medication, "medication");
            ValidationSupport.requireNonNull(medicationAdministration.subject, "subject");
            ValidationSupport.checkList(medicationAdministration.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.requireChoiceElement(medicationAdministration.occurence, "occurence", DateTime.class, Period.class, Timing.class);
            ValidationSupport.checkList(medicationAdministration.subPotentReason, "subPotentReason", CodeableConcept.class);
            ValidationSupport.checkList(medicationAdministration.performer, "performer", Performer.class);
            ValidationSupport.checkList(medicationAdministration.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(medicationAdministration.device, "device", CodeableReference.class);
            ValidationSupport.checkList(medicationAdministration.note, "note", Annotation.class);
            ValidationSupport.checkList(medicationAdministration.eventHistory, "eventHistory", Reference.class);
            ValidationSupport.checkReferenceType(medicationAdministration.basedOn, "basedOn", "CarePlan");
            ValidationSupport.checkReferenceType(medicationAdministration.partOf, "partOf", "MedicationAdministration", "Procedure", "MedicationDispense");
            ValidationSupport.checkReferenceType(medicationAdministration.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(medicationAdministration.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(medicationAdministration.request, "request", "MedicationRequest");
            ValidationSupport.checkReferenceType(medicationAdministration.eventHistory, "eventHistory", "Provenance");
        }

        protected Builder from(MedicationAdministration medicationAdministration) {
            super.from(medicationAdministration);
            identifier.addAll(medicationAdministration.identifier);
            basedOn.addAll(medicationAdministration.basedOn);
            partOf.addAll(medicationAdministration.partOf);
            status = medicationAdministration.status;
            statusReason.addAll(medicationAdministration.statusReason);
            category.addAll(medicationAdministration.category);
            medication = medicationAdministration.medication;
            subject = medicationAdministration.subject;
            encounter = medicationAdministration.encounter;
            supportingInformation.addAll(medicationAdministration.supportingInformation);
            occurence = medicationAdministration.occurence;
            recorded = medicationAdministration.recorded;
            isSubPotent = medicationAdministration.isSubPotent;
            subPotentReason.addAll(medicationAdministration.subPotentReason);
            performer.addAll(medicationAdministration.performer);
            reason.addAll(medicationAdministration.reason);
            request = medicationAdministration.request;
            device.addAll(medicationAdministration.device);
            note.addAll(medicationAdministration.note);
            dosage = medicationAdministration.dosage;
            eventHistory.addAll(medicationAdministration.eventHistory);
            return this;
        }
    }

    /**
     * The performer of the medication treatment. For devices this is the device that performed the administration of the 
     * medication. An IV Pump would be an example of a device that is performing the administration. Both the IV Pump and the 
     * practitioner that set the rate or bolus on the pump can be listed as performers.
     */
    public static class Performer extends BackboneElement {
        @Binding(
            bindingName = "MedicationAdministrationPerformerFunction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A code describing the role an individual played in administering the medication.",
            valueSet = "http://hl7.org/fhir/ValueSet/med-admin-perform-function"
        )
        private final CodeableConcept function;
        @Summary
        @Required
        private final CodeableReference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Distinguishes the type of involvement of the performer in the medication administration.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Indicates who or what performed the medication administration.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getActor() {
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
            Performer other = (Performer) obj;
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
            private CodeableReference actor;

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
             * Distinguishes the type of involvement of the performer in the medication administration.
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
             * Indicates who or what performed the medication administration.
             * 
             * <p>This element is required.
             * 
             * @param actor
             *     Who or what performed the medication administration
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(CodeableReference actor) {
                this.actor = actor;
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
                ValidationSupport.requireValueOrChildren(performer);
            }

            protected Builder from(Performer performer) {
                super.from(performer);
                function = performer.function;
                actor = performer.actor;
                return this;
            }
        }
    }

    /**
     * Describes the medication dosage information details e.g. dose, rate, site, route, etc.
     */
    public static class Dosage extends BackboneElement {
        private final String text;
        @Binding(
            bindingName = "MedicationAdministrationSite",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A coded concept describing the site location the medicine enters into or onto the body.",
            valueSet = "http://hl7.org/fhir/ValueSet/approach-site-codes"
        )
        private final CodeableConcept site;
        @Binding(
            bindingName = "RouteOfAdministration",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A coded concept describing the route or physiological path of administration of a therapeutic agent into or onto the body of a subject.",
            valueSet = "http://hl7.org/fhir/ValueSet/route-codes"
        )
        private final CodeableConcept route;
        @Binding(
            bindingName = "MedicationAdministrationMethod",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A coded concept describing the technique by which the medicine is administered.",
            valueSet = "http://hl7.org/fhir/ValueSet/administration-method-codes"
        )
        private final CodeableConcept method;
        private final SimpleQuantity dose;
        @Choice({ Ratio.class, SimpleQuantity.class })
        private final Element rate;

        private Dosage(Builder builder) {
            super(builder);
            text = builder.text;
            site = builder.site;
            route = builder.route;
            method = builder.method;
            dose = builder.dose;
            rate = builder.rate;
        }

        /**
         * Free text dosage can be used for cases where the dosage administered is too complex to code. When coded dosage is 
         * present, the free text dosage may still be present for display to humans. The dosage instructions should reflect the 
         * dosage of the medication that was administered.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getText() {
            return text;
        }

        /**
         * A coded specification of the anatomic site where the medication first entered the body. For example, "left arm".
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getSite() {
            return site;
        }

        /**
         * A code specifying the route or physiological path of administration of a therapeutic agent into or onto the patient. 
         * For example, topical, intravenous, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRoute() {
            return route;
        }

        /**
         * A coded value indicating the method by which the medication is intended to be or was introduced into or on the body. 
         * This attribute will most often NOT be populated. It is most commonly used for injections. For example, Slow Push, Deep 
         * IV.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getMethod() {
            return method;
        }

        /**
         * The amount of the medication given at one administration event. Use this value when the administration is essentially 
         * an instantaneous event such as a swallowing a tablet or giving an injection.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getDose() {
            return dose;
        }

        /**
         * Identifies the speed with which the medication was or will be introduced into the patient. Typically, the rate for an 
         * infusion e.g. 100 ml per 1 hour or 100 ml/hr. May also be expressed as a rate per unit of time, e.g. 500 ml per 2 
         * hours. Other examples: 200 mcg/min or 200 mcg/1 minute; 1 liter/8 hours.
         * 
         * @return
         *     An immutable object of type {@link Ratio} or {@link SimpleQuantity} that may be null.
         */
        public Element getRate() {
            return rate;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (text != null) || 
                (site != null) || 
                (route != null) || 
                (method != null) || 
                (dose != null) || 
                (rate != null);
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
                    accept(text, "text", visitor);
                    accept(site, "site", visitor);
                    accept(route, "route", visitor);
                    accept(method, "method", visitor);
                    accept(dose, "dose", visitor);
                    accept(rate, "rate", visitor);
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
            Dosage other = (Dosage) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(text, other.text) && 
                Objects.equals(site, other.site) && 
                Objects.equals(route, other.route) && 
                Objects.equals(method, other.method) && 
                Objects.equals(dose, other.dose) && 
                Objects.equals(rate, other.rate);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    text, 
                    site, 
                    route, 
                    method, 
                    dose, 
                    rate);
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
            private String text;
            private CodeableConcept site;
            private CodeableConcept route;
            private CodeableConcept method;
            private SimpleQuantity dose;
            private Element rate;

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
             * Convenience method for setting {@code text}.
             * 
             * @param text
             *     Free text dosage instructions e.g. SIG
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #text(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder text(java.lang.String text) {
                this.text = (text == null) ? null : String.of(text);
                return this;
            }

            /**
             * Free text dosage can be used for cases where the dosage administered is too complex to code. When coded dosage is 
             * present, the free text dosage may still be present for display to humans. The dosage instructions should reflect the 
             * dosage of the medication that was administered.
             * 
             * @param text
             *     Free text dosage instructions e.g. SIG
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder text(String text) {
                this.text = text;
                return this;
            }

            /**
             * A coded specification of the anatomic site where the medication first entered the body. For example, "left arm".
             * 
             * @param site
             *     Body site administered to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder site(CodeableConcept site) {
                this.site = site;
                return this;
            }

            /**
             * A code specifying the route or physiological path of administration of a therapeutic agent into or onto the patient. 
             * For example, topical, intravenous, etc.
             * 
             * @param route
             *     Path of substance into body
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder route(CodeableConcept route) {
                this.route = route;
                return this;
            }

            /**
             * A coded value indicating the method by which the medication is intended to be or was introduced into or on the body. 
             * This attribute will most often NOT be populated. It is most commonly used for injections. For example, Slow Push, Deep 
             * IV.
             * 
             * @param method
             *     How drug was administered
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder method(CodeableConcept method) {
                this.method = method;
                return this;
            }

            /**
             * The amount of the medication given at one administration event. Use this value when the administration is essentially 
             * an instantaneous event such as a swallowing a tablet or giving an injection.
             * 
             * @param dose
             *     Amount of medication per dose
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dose(SimpleQuantity dose) {
                this.dose = dose;
                return this;
            }

            /**
             * Identifies the speed with which the medication was or will be introduced into the patient. Typically, the rate for an 
             * infusion e.g. 100 ml per 1 hour or 100 ml/hr. May also be expressed as a rate per unit of time, e.g. 500 ml per 2 
             * hours. Other examples: 200 mcg/min or 200 mcg/1 minute; 1 liter/8 hours.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Ratio}</li>
             * <li>{@link SimpleQuantity}</li>
             * </ul>
             * 
             * @param rate
             *     Dose quantity per unit of time
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder rate(Element rate) {
                this.rate = rate;
                return this;
            }

            /**
             * Build the {@link Dosage}
             * 
             * @return
             *     An immutable object of type {@link Dosage}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Dosage per the base specification
             */
            @Override
            public Dosage build() {
                Dosage dosage = new Dosage(this);
                if (validating) {
                    validate(dosage);
                }
                return dosage;
            }

            protected void validate(Dosage dosage) {
                super.validate(dosage);
                ValidationSupport.choiceElement(dosage.rate, "rate", Ratio.class, SimpleQuantity.class);
                ValidationSupport.requireValueOrChildren(dosage);
            }

            protected Builder from(Dosage dosage) {
                super.from(dosage);
                text = dosage.text;
                site = dosage.site;
                route = dosage.route;
                method = dosage.method;
                dose = dosage.dose;
                rate = dosage.rate;
                return this;
            }
        }
    }
}
