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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of association or dissociation of a device with a patient.
 * 
 * <p>Maturity level: FMM0 (draft)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.DRAFT
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceAssociation extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "Device" })
    @Required
    private final Reference device;
    @Summary
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "DeviceAssociationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the lifecycle of the association.",
        valueSet = "http://hl7.org/fhir/ValueSet/deviceassociation-status|5.0.0"
    )
    @Required
    private final CodeableConcept status;
    @Summary
    @Binding(
        bindingName = "DeviceAssociationStatusReason",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the reason for changing the status of the association.",
        valueSet = "http://hl7.org/fhir/ValueSet/deviceassociation-status-reason|5.0.0"
    )
    private final List<CodeableConcept> statusReason;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Practitioner", "RelatedPerson", "Device" })
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "BodyStructure" })
    private final Reference bodyStructure;
    @Summary
    private final Period period;
    @Summary
    private final List<Operation> operation;

    private DeviceAssociation(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        device = builder.device;
        category = Collections.unmodifiableList(builder.category);
        status = builder.status;
        statusReason = Collections.unmodifiableList(builder.statusReason);
        subject = builder.subject;
        bodyStructure = builder.bodyStructure;
        period = builder.period;
        operation = Collections.unmodifiableList(builder.operation);
    }

    /**
     * Instance identifier.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Reference to the devices associated with the patient or group.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getDevice() {
        return device;
    }

    /**
     * Describes the relationship between the device and subject.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Indicates the state of the Device association.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getStatus() {
        return status;
    }

    /**
     * The reasons given for the current association status.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getStatusReason() {
        return statusReason;
    }

    /**
     * The individual, group of individuals or device that the device is on or associated with.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Current anatomical location of the device in/on subject.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getBodyStructure() {
        return bodyStructure;
    }

    /**
     * Begin and end dates and times for the device association.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * The details about the device when it is in use to describe its operation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Operation} that may be empty.
     */
    public List<Operation> getOperation() {
        return operation;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (device != null) || 
            !category.isEmpty() || 
            (status != null) || 
            !statusReason.isEmpty() || 
            (subject != null) || 
            (bodyStructure != null) || 
            (period != null) || 
            !operation.isEmpty();
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
                accept(device, "device", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(bodyStructure, "bodyStructure", visitor);
                accept(period, "period", visitor);
                accept(operation, "operation", visitor, Operation.class);
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
        DeviceAssociation other = (DeviceAssociation) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(device, other.device) && 
            Objects.equals(category, other.category) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(bodyStructure, other.bodyStructure) && 
            Objects.equals(period, other.period) && 
            Objects.equals(operation, other.operation);
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
                device, 
                category, 
                status, 
                statusReason, 
                subject, 
                bodyStructure, 
                period, 
                operation);
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
        private Reference device;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept status;
        private List<CodeableConcept> statusReason = new ArrayList<>();
        private Reference subject;
        private Reference bodyStructure;
        private Period period;
        private List<Operation> operation = new ArrayList<>();

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
         * Instance identifier.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Instance identifier.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Reference to the devices associated with the patient or group.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param device
         *     Reference to the devices associated with the patient or group
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(Reference device) {
            this.device = device;
            return this;
        }

        /**
         * Describes the relationship between the device and subject.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Describes the relationship between the device and subject
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
         * Describes the relationship between the device and subject.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Describes the relationship between the device and subject
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
         * Indicates the state of the Device association.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     implanted | explanted | attached | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(CodeableConcept status) {
            this.status = status;
            return this;
        }

        /**
         * The reasons given for the current association status.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     The reasons given for the current association status
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
         * The reasons given for the current association status.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     The reasons given for the current association status
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
         * The individual, group of individuals or device that the device is on or associated with.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param subject
         *     The individual, group of individuals or device that the device is on or associated with
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Current anatomical location of the device in/on subject.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link BodyStructure}</li>
         * </ul>
         * 
         * @param bodyStructure
         *     Current anatomical location of the device in/on subject
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodyStructure(Reference bodyStructure) {
            this.bodyStructure = bodyStructure;
            return this;
        }

        /**
         * Begin and end dates and times for the device association.
         * 
         * @param period
         *     Begin and end dates and times for the device association
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * The details about the device when it is in use to describe its operation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param operation
         *     The details about the device when it is in use to describe its operation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder operation(Operation... operation) {
            for (Operation value : operation) {
                this.operation.add(value);
            }
            return this;
        }

        /**
         * The details about the device when it is in use to describe its operation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param operation
         *     The details about the device when it is in use to describe its operation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder operation(Collection<Operation> operation) {
            this.operation = new ArrayList<>(operation);
            return this;
        }

        /**
         * Build the {@link DeviceAssociation}
         * 
         * <p>Required elements:
         * <ul>
         * <li>device</li>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DeviceAssociation}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceAssociation per the base specification
         */
        @Override
        public DeviceAssociation build() {
            DeviceAssociation deviceAssociation = new DeviceAssociation(this);
            if (validating) {
                validate(deviceAssociation);
            }
            return deviceAssociation;
        }

        protected void validate(DeviceAssociation deviceAssociation) {
            super.validate(deviceAssociation);
            ValidationSupport.checkList(deviceAssociation.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(deviceAssociation.device, "device");
            ValidationSupport.checkList(deviceAssociation.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(deviceAssociation.status, "status");
            ValidationSupport.checkList(deviceAssociation.statusReason, "statusReason", CodeableConcept.class);
            ValidationSupport.checkList(deviceAssociation.operation, "operation", Operation.class);
            ValidationSupport.checkValueSetBinding(deviceAssociation.status, "status", "http://hl7.org/fhir/ValueSet/deviceassociation-status", "http://hl7.org/fhir/deviceassociation-status", "implanted", "explanted", "entered-in-error", "attached", "unknown");
            ValidationSupport.checkValueSetBinding(deviceAssociation.statusReason, "statusReason", "http://hl7.org/fhir/ValueSet/deviceassociation-status-reason", "http://hl7.org/fhir/deviceassociation-status-reason", "attached", "disconnected", "failed", "placed", "replaced");
            ValidationSupport.checkReferenceType(deviceAssociation.device, "device", "Device");
            ValidationSupport.checkReferenceType(deviceAssociation.subject, "subject", "Patient", "Group", "Practitioner", "RelatedPerson", "Device");
            ValidationSupport.checkReferenceType(deviceAssociation.bodyStructure, "bodyStructure", "BodyStructure");
        }

        protected Builder from(DeviceAssociation deviceAssociation) {
            super.from(deviceAssociation);
            identifier.addAll(deviceAssociation.identifier);
            device = deviceAssociation.device;
            category.addAll(deviceAssociation.category);
            status = deviceAssociation.status;
            statusReason.addAll(deviceAssociation.statusReason);
            subject = deviceAssociation.subject;
            bodyStructure = deviceAssociation.bodyStructure;
            period = deviceAssociation.period;
            operation.addAll(deviceAssociation.operation);
            return this;
        }
    }

    /**
     * The details about the device when it is in use to describe its operation.
     */
    public static class Operation extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "DeviceAssociationOperationStatus",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Describes the the status of the association operation.",
            valueSet = "http://hl7.org/fhir/ValueSet/deviceassociation-operationstatus"
        )
        @Required
        private final CodeableConcept status;
        @Summary
        @ReferenceTarget({ "Patient", "Practitioner", "RelatedPerson" })
        private final List<Reference> operator;
        @Summary
        private final Period period;

        private Operation(Builder builder) {
            super(builder);
            status = builder.status;
            operator = Collections.unmodifiableList(builder.operator);
            period = builder.period;
        }

        /**
         * Device operational condition corresponding to the association.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getStatus() {
            return status;
        }

        /**
         * The individual performing the action enabled by the device.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getOperator() {
            return operator;
        }

        /**
         * Begin and end dates and times for the device's operation.
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
                (status != null) || 
                !operator.isEmpty() || 
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
                    accept(status, "status", visitor);
                    accept(operator, "operator", visitor, Reference.class);
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
            Operation other = (Operation) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(status, other.status) && 
                Objects.equals(operator, other.operator) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    status, 
                    operator, 
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
            private CodeableConcept status;
            private List<Reference> operator = new ArrayList<>();
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
             * Device operational condition corresponding to the association.
             * 
             * <p>This element is required.
             * 
             * @param status
             *     Device operational condition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder status(CodeableConcept status) {
                this.status = status;
                return this;
            }

            /**
             * The individual performing the action enabled by the device.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param operator
             *     The individual performing the action enabled by the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder operator(Reference... operator) {
                for (Reference value : operator) {
                    this.operator.add(value);
                }
                return this;
            }

            /**
             * The individual performing the action enabled by the device.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param operator
             *     The individual performing the action enabled by the device
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder operator(Collection<Reference> operator) {
                this.operator = new ArrayList<>(operator);
                return this;
            }

            /**
             * Begin and end dates and times for the device's operation.
             * 
             * @param period
             *     Begin and end dates and times for the device's operation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link Operation}
             * 
             * <p>Required elements:
             * <ul>
             * <li>status</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Operation}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Operation per the base specification
             */
            @Override
            public Operation build() {
                Operation operation = new Operation(this);
                if (validating) {
                    validate(operation);
                }
                return operation;
            }

            protected void validate(Operation operation) {
                super.validate(operation);
                ValidationSupport.requireNonNull(operation.status, "status");
                ValidationSupport.checkList(operation.operator, "operator", Reference.class);
                ValidationSupport.checkReferenceType(operation.operator, "operator", "Patient", "Practitioner", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(operation);
            }

            protected Builder from(Operation operation) {
                super.from(operation);
                status = operation.status;
                operator.addAll(operation.operator);
                period = operation.period;
                return this;
            }
        }
    }
}
