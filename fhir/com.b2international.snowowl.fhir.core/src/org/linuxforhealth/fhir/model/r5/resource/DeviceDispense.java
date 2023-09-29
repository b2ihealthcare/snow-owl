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
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceDispenseStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of dispensation of a device - i.e., assigning a device to a patient, or to a professional for their use.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceDispense extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @ReferenceTarget({ "CarePlan", "DeviceRequest" })
    private final List<Reference> basedOn;
    @ReferenceTarget({ "Procedure" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "DeviceDispenseStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the lifecycle of the dispense.",
        valueSet = "http://hl7.org/fhir/ValueSet/devicedispense-status|5.0.0"
    )
    @Required
    private final DeviceDispenseStatus status;
    @Binding(
        bindingName = "DeviceDispenseStatusReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code describing why a dispense was or was not performed.",
        valueSet = "http://hl7.org/fhir/ValueSet/devicedispense-status-reason"
    )
    private final CodeableReference statusReason;
    private final List<CodeableConcept> category;
    @Summary
    @Required
    private final CodeableReference device;
    @Summary
    @ReferenceTarget({ "Patient", "Practitioner" })
    @Required
    private final Reference subject;
    @ReferenceTarget({ "Patient", "Practitioner", "RelatedPerson", "Location", "PractitionerRole" })
    private final Reference receiver;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final List<Reference> supportingInformation;
    private final List<Performer> performer;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    private final CodeableConcept type;
    private final SimpleQuantity quantity;
    @Summary
    private final DateTime preparedDate;
    private final DateTime whenHandedOver;
    @ReferenceTarget({ "Location" })
    private final Reference destination;
    private final List<Annotation> note;
    private final Markdown usageInstruction;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> eventHistory;

    private DeviceDispense(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        statusReason = builder.statusReason;
        category = Collections.unmodifiableList(builder.category);
        device = builder.device;
        subject = builder.subject;
        receiver = builder.receiver;
        encounter = builder.encounter;
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        performer = Collections.unmodifiableList(builder.performer);
        location = builder.location;
        type = builder.type;
        quantity = builder.quantity;
        preparedDate = builder.preparedDate;
        whenHandedOver = builder.whenHandedOver;
        destination = builder.destination;
        note = Collections.unmodifiableList(builder.note);
        usageInstruction = builder.usageInstruction;
        eventHistory = Collections.unmodifiableList(builder.eventHistory);
    }

    /**
     * Business identifier for this dispensation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The order or request that this dispense is fulfilling.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The bigger event that this dispense is a part of.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code specifying the state of the set of dispense events.
     * 
     * @return
     *     An immutable object of type {@link DeviceDispenseStatus} that is non-null.
     */
    public DeviceDispenseStatus getStatus() {
        return status;
    }

    /**
     * Indicates the reason why a dispense was or was not performed.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getStatusReason() {
        return statusReason;
    }

    /**
     * Indicates the type of device dispense.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Identifies the device being dispensed. This is either a link to a resource representing the details of the device or a 
     * simple attribute carrying a code that identifies the device from a known list of devices.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getDevice() {
        return device;
    }

    /**
     * A link to a resource representing the person to whom the device is intended.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Identifies the person who picked up the device or the person or location where the device was delivered. This may be a 
     * patient or their caregiver, but some cases exist where it can be a healthcare professional or a location.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReceiver() {
        return receiver;
    }

    /**
     * The encounter that establishes the context for this event.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Additional information that supports the device being dispensed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * Indicates who or what performed the event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * The principal physical location where the dispense was performed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Indicates the type of dispensing event that is performed.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * The number of devices that have been dispensed.
     * 
     * @return
     *     An immutable object of type {@link SimpleQuantity} that may be null.
     */
    public SimpleQuantity getQuantity() {
        return quantity;
    }

    /**
     * The time when the dispensed product was packaged and reviewed.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPreparedDate() {
        return preparedDate;
    }

    /**
     * The time the dispensed product was made available to the patient or their representative.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getWhenHandedOver() {
        return whenHandedOver;
    }

    /**
     * Identification of the facility/location where the device was /should be shipped to, as part of the dispense process.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDestination() {
        return destination;
    }

    /**
     * Extra information about the dispense that could not be conveyed in the other attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * The full representation of the instructions.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getUsageInstruction() {
        return usageInstruction;
    }

    /**
     * A summary of the events of interest that have occurred, such as when the dispense was verified.
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
            (statusReason != null) || 
            !category.isEmpty() || 
            (device != null) || 
            (subject != null) || 
            (receiver != null) || 
            (encounter != null) || 
            !supportingInformation.isEmpty() || 
            !performer.isEmpty() || 
            (location != null) || 
            (type != null) || 
            (quantity != null) || 
            (preparedDate != null) || 
            (whenHandedOver != null) || 
            (destination != null) || 
            !note.isEmpty() || 
            (usageInstruction != null) || 
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
                accept(statusReason, "statusReason", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(device, "device", visitor);
                accept(subject, "subject", visitor);
                accept(receiver, "receiver", visitor);
                accept(encounter, "encounter", visitor);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(performer, "performer", visitor, Performer.class);
                accept(location, "location", visitor);
                accept(type, "type", visitor);
                accept(quantity, "quantity", visitor);
                accept(preparedDate, "preparedDate", visitor);
                accept(whenHandedOver, "whenHandedOver", visitor);
                accept(destination, "destination", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(usageInstruction, "usageInstruction", visitor);
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
        DeviceDispense other = (DeviceDispense) obj;
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
            Objects.equals(device, other.device) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(receiver, other.receiver) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(location, other.location) && 
            Objects.equals(type, other.type) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(preparedDate, other.preparedDate) && 
            Objects.equals(whenHandedOver, other.whenHandedOver) && 
            Objects.equals(destination, other.destination) && 
            Objects.equals(note, other.note) && 
            Objects.equals(usageInstruction, other.usageInstruction) && 
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
                device, 
                subject, 
                receiver, 
                encounter, 
                supportingInformation, 
                performer, 
                location, 
                type, 
                quantity, 
                preparedDate, 
                whenHandedOver, 
                destination, 
                note, 
                usageInstruction, 
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
        private DeviceDispenseStatus status;
        private CodeableReference statusReason;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableReference device;
        private Reference subject;
        private Reference receiver;
        private Reference encounter;
        private List<Reference> supportingInformation = new ArrayList<>();
        private List<Performer> performer = new ArrayList<>();
        private Reference location;
        private CodeableConcept type;
        private SimpleQuantity quantity;
        private DateTime preparedDate;
        private DateTime whenHandedOver;
        private Reference destination;
        private List<Annotation> note = new ArrayList<>();
        private Markdown usageInstruction;
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
         * Business identifier for this dispensation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this dispensation
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
         * Business identifier for this dispensation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this dispensation
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
         * The order or request that this dispense is fulfilling.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     The order or request that this dispense is fulfilling
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
         * The order or request that this dispense is fulfilling.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link DeviceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     The order or request that this dispense is fulfilling
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
         * The bigger event that this dispense is a part of.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * </ul>
         * 
         * @param partOf
         *     The bigger event that this dispense is a part of
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
         * The bigger event that this dispense is a part of.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * </ul>
         * 
         * @param partOf
         *     The bigger event that this dispense is a part of
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
         * A code specifying the state of the set of dispense events.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     preparation | in-progress | cancelled | on-hold | completed | entered-in-error | stopped | declined | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(DeviceDispenseStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the reason why a dispense was or was not performed.
         * 
         * @param statusReason
         *     Why a dispense was or was not performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusReason(CodeableReference statusReason) {
            this.statusReason = statusReason;
            return this;
        }

        /**
         * Indicates the type of device dispense.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of device dispense
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
         * Indicates the type of device dispense.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of device dispense
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
         * Identifies the device being dispensed. This is either a link to a resource representing the details of the device or a 
         * simple attribute carrying a code that identifies the device from a known list of devices.
         * 
         * <p>This element is required.
         * 
         * @param device
         *     What device was supplied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(CodeableReference device) {
            this.device = device;
            return this;
        }

        /**
         * A link to a resource representing the person to whom the device is intended.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param subject
         *     Who the dispense is for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Identifies the person who picked up the device or the person or location where the device was delivered. This may be a 
         * patient or their caregiver, but some cases exist where it can be a healthcare professional or a location.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Location}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param receiver
         *     Who collected the device or where the medication was delivered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder receiver(Reference receiver) {
            this.receiver = receiver;
            return this;
        }

        /**
         * The encounter that establishes the context for this event.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter associated with event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Additional information that supports the device being dispensed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information that supports the dispensing of the device
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
         * Additional information that supports the device being dispensed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information that supports the dispensing of the device
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
         * Indicates who or what performed the event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed event
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
         * Indicates who or what performed the event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed event
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
         * The principal physical location where the dispense was performed.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where the dispense occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Indicates the type of dispensing event that is performed.
         * 
         * @param type
         *     Trial fill, partial fill, emergency fill, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * The number of devices that have been dispensed.
         * 
         * @param quantity
         *     Amount dispensed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder quantity(SimpleQuantity quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * The time when the dispensed product was packaged and reviewed.
         * 
         * @param preparedDate
         *     When product was packaged and reviewed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preparedDate(DateTime preparedDate) {
            this.preparedDate = preparedDate;
            return this;
        }

        /**
         * The time the dispensed product was made available to the patient or their representative.
         * 
         * @param whenHandedOver
         *     When product was given out
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder whenHandedOver(DateTime whenHandedOver) {
            this.whenHandedOver = whenHandedOver;
            return this;
        }

        /**
         * Identification of the facility/location where the device was /should be shipped to, as part of the dispense process.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param destination
         *     Where the device was sent or should be sent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder destination(Reference destination) {
            this.destination = destination;
            return this;
        }

        /**
         * Extra information about the dispense that could not be conveyed in the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the dispense
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
         * Extra information about the dispense that could not be conveyed in the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Information about the dispense
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
         * The full representation of the instructions.
         * 
         * @param usageInstruction
         *     Full representation of the usage instructions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usageInstruction(Markdown usageInstruction) {
            this.usageInstruction = usageInstruction;
            return this;
        }

        /**
         * A summary of the events of interest that have occurred, such as when the dispense was verified.
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
         *     A list of relevant lifecycle events
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
         * A summary of the events of interest that have occurred, such as when the dispense was verified.
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
         *     A list of relevant lifecycle events
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
         * Build the {@link DeviceDispense}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>device</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DeviceDispense}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceDispense per the base specification
         */
        @Override
        public DeviceDispense build() {
            DeviceDispense deviceDispense = new DeviceDispense(this);
            if (validating) {
                validate(deviceDispense);
            }
            return deviceDispense;
        }

        protected void validate(DeviceDispense deviceDispense) {
            super.validate(deviceDispense);
            ValidationSupport.checkList(deviceDispense.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(deviceDispense.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(deviceDispense.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(deviceDispense.status, "status");
            ValidationSupport.checkList(deviceDispense.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(deviceDispense.device, "device");
            ValidationSupport.requireNonNull(deviceDispense.subject, "subject");
            ValidationSupport.checkList(deviceDispense.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.checkList(deviceDispense.performer, "performer", Performer.class);
            ValidationSupport.checkList(deviceDispense.note, "note", Annotation.class);
            ValidationSupport.checkList(deviceDispense.eventHistory, "eventHistory", Reference.class);
            ValidationSupport.checkReferenceType(deviceDispense.basedOn, "basedOn", "CarePlan", "DeviceRequest");
            ValidationSupport.checkReferenceType(deviceDispense.partOf, "partOf", "Procedure");
            ValidationSupport.checkReferenceType(deviceDispense.subject, "subject", "Patient", "Practitioner");
            ValidationSupport.checkReferenceType(deviceDispense.receiver, "receiver", "Patient", "Practitioner", "RelatedPerson", "Location", "PractitionerRole");
            ValidationSupport.checkReferenceType(deviceDispense.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(deviceDispense.location, "location", "Location");
            ValidationSupport.checkReferenceType(deviceDispense.destination, "destination", "Location");
            ValidationSupport.checkReferenceType(deviceDispense.eventHistory, "eventHistory", "Provenance");
        }

        protected Builder from(DeviceDispense deviceDispense) {
            super.from(deviceDispense);
            identifier.addAll(deviceDispense.identifier);
            basedOn.addAll(deviceDispense.basedOn);
            partOf.addAll(deviceDispense.partOf);
            status = deviceDispense.status;
            statusReason = deviceDispense.statusReason;
            category.addAll(deviceDispense.category);
            device = deviceDispense.device;
            subject = deviceDispense.subject;
            receiver = deviceDispense.receiver;
            encounter = deviceDispense.encounter;
            supportingInformation.addAll(deviceDispense.supportingInformation);
            performer.addAll(deviceDispense.performer);
            location = deviceDispense.location;
            type = deviceDispense.type;
            quantity = deviceDispense.quantity;
            preparedDate = deviceDispense.preparedDate;
            whenHandedOver = deviceDispense.whenHandedOver;
            destination = deviceDispense.destination;
            note.addAll(deviceDispense.note);
            usageInstruction = deviceDispense.usageInstruction;
            eventHistory.addAll(deviceDispense.eventHistory);
            return this;
        }
    }

    /**
     * Indicates who or what performed the event.
     */
    public static class Performer extends BackboneElement {
        private final CodeableConcept function;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "Device", "RelatedPerson", "CareTeam" })
        @Required
        private final Reference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Distinguishes the type of performer in the dispense. For example, date enterer, packager, final checker.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * The device, practitioner, etc. who performed the action. It should be assumed that the actor is the dispenser of the 
         * device.
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
             * Distinguishes the type of performer in the dispense. For example, date enterer, packager, final checker.
             * 
             * @param function
             *     Who performed the dispense and what they did
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * The device, practitioner, etc. who performed the action. It should be assumed that the actor is the dispenser of the 
             * device.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Device}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link CareTeam}</li>
             * </ul>
             * 
             * @param actor
             *     Individual who was performing
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
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
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "Patient", "Device", "RelatedPerson", "CareTeam");
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
}
