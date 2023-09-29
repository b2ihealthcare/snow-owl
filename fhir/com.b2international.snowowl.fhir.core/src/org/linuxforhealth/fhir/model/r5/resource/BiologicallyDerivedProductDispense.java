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
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.BiologicallyDerivedProductDispenseStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource reflects an instance of a biologically derived product dispense. The supply or dispense of a 
 * biologically derived product from the supply organization or department (e.g. hospital transfusion laboratory) to the 
 * clinical team responsible for clinical application.
 * 
 * <p>Maturity level: FMM0 (draft)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.DRAFT
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class BiologicallyDerivedProductDispense extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "BiologicallyDerivedProductDispense" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "BiologicallyDerivedProductDispenseStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the lifecycle of the dispense.",
        valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderivedproductdispense-status|5.0.0"
    )
    @Required
    private final BiologicallyDerivedProductDispenseStatus status;
    @Summary
    @Binding(
        bindingName = "BiologicallyDerivedProductDispenseOriginRelationship",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Describes the relationship between the recipient and origin of the dispensed product.",
        valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderivedproductdispense-origin-relationship"
    )
    private final CodeableConcept originRelationshipType;
    @Summary
    @ReferenceTarget({ "BiologicallyDerivedProduct" })
    @Required
    private final Reference product;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;
    @Summary
    @Binding(
        bindingName = "BiologicallyDerivedProductDispenseMatchStatus",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Describes the type of matching between the recipient and origin of the dispensed product.",
        valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderivedproductdispense-match-status"
    )
    private final CodeableConcept matchStatus;
    @Summary
    private final List<Performer> performer;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Summary
    private final SimpleQuantity quantity;
    @Summary
    private final DateTime preparedDate;
    @Summary
    private final DateTime whenHandedOver;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference destination;
    @Summary
    private final List<Annotation> note;
    @Summary
    private final String usageInstruction;

    private BiologicallyDerivedProductDispense(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        originRelationshipType = builder.originRelationshipType;
        product = builder.product;
        patient = builder.patient;
        matchStatus = builder.matchStatus;
        performer = Collections.unmodifiableList(builder.performer);
        location = builder.location;
        quantity = builder.quantity;
        preparedDate = builder.preparedDate;
        whenHandedOver = builder.whenHandedOver;
        destination = builder.destination;
        note = Collections.unmodifiableList(builder.note);
        usageInstruction = builder.usageInstruction;
    }

    /**
     * Unique instance identifiers assigned to a biologically derived product dispense. Note: This is a business identifier, 
     * not a resource identifier.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The order or request that the dispense is fulfilling. This is a reference to a ServiceRequest resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A larger event of which this particular event is a component.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code specifying the state of the dispense event.
     * 
     * @return
     *     An immutable object of type {@link BiologicallyDerivedProductDispenseStatus} that is non-null.
     */
    public BiologicallyDerivedProductDispenseStatus getStatus() {
        return status;
    }

    /**
     * Indicates the relationship between the donor of the biologically derived product and the intended recipient.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getOriginRelationshipType() {
        return originRelationshipType;
    }

    /**
     * A link to a resource identifying the biologically derived product that is being dispensed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getProduct() {
        return product;
    }

    /**
     * A link to a resource representing the patient that the product is dispensed for.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * Indicates the type of matching associated with the dispense.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getMatchStatus() {
        return matchStatus;
    }

    /**
     * Indicates who or what performed an action.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * The physical location where the dispense was performed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * The amount of product in the dispense. Quantity will depend on the product being dispensed. Examples are: volume; cell 
     * count; concentration.
     * 
     * @return
     *     An immutable object of type {@link SimpleQuantity} that may be null.
     */
    public SimpleQuantity getQuantity() {
        return quantity;
    }

    /**
     * When the product was selected/ matched.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getPreparedDate() {
        return preparedDate;
    }

    /**
     * When the product was dispatched for clinical use.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getWhenHandedOver() {
        return whenHandedOver;
    }

    /**
     * Link to a resource identifying the physical location that the product was dispatched to.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDestination() {
        return destination;
    }

    /**
     * Additional notes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Specific instructions for use.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getUsageInstruction() {
        return usageInstruction;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !basedOn.isEmpty() || 
            !partOf.isEmpty() || 
            (status != null) || 
            (originRelationshipType != null) || 
            (product != null) || 
            (patient != null) || 
            (matchStatus != null) || 
            !performer.isEmpty() || 
            (location != null) || 
            (quantity != null) || 
            (preparedDate != null) || 
            (whenHandedOver != null) || 
            (destination != null) || 
            !note.isEmpty() || 
            (usageInstruction != null);
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
                accept(originRelationshipType, "originRelationshipType", visitor);
                accept(product, "product", visitor);
                accept(patient, "patient", visitor);
                accept(matchStatus, "matchStatus", visitor);
                accept(performer, "performer", visitor, Performer.class);
                accept(location, "location", visitor);
                accept(quantity, "quantity", visitor);
                accept(preparedDate, "preparedDate", visitor);
                accept(whenHandedOver, "whenHandedOver", visitor);
                accept(destination, "destination", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(usageInstruction, "usageInstruction", visitor);
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
        BiologicallyDerivedProductDispense other = (BiologicallyDerivedProductDispense) obj;
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
            Objects.equals(originRelationshipType, other.originRelationshipType) && 
            Objects.equals(product, other.product) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(matchStatus, other.matchStatus) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(location, other.location) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(preparedDate, other.preparedDate) && 
            Objects.equals(whenHandedOver, other.whenHandedOver) && 
            Objects.equals(destination, other.destination) && 
            Objects.equals(note, other.note) && 
            Objects.equals(usageInstruction, other.usageInstruction);
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
                originRelationshipType, 
                product, 
                patient, 
                matchStatus, 
                performer, 
                location, 
                quantity, 
                preparedDate, 
                whenHandedOver, 
                destination, 
                note, 
                usageInstruction);
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
        private BiologicallyDerivedProductDispenseStatus status;
        private CodeableConcept originRelationshipType;
        private Reference product;
        private Reference patient;
        private CodeableConcept matchStatus;
        private List<Performer> performer = new ArrayList<>();
        private Reference location;
        private SimpleQuantity quantity;
        private DateTime preparedDate;
        private DateTime whenHandedOver;
        private Reference destination;
        private List<Annotation> note = new ArrayList<>();
        private String usageInstruction;

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
         * Unique instance identifiers assigned to a biologically derived product dispense. Note: This is a business identifier, 
         * not a resource identifier.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this dispense
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
         * Unique instance identifiers assigned to a biologically derived product dispense. Note: This is a business identifier, 
         * not a resource identifier.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this dispense
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
         * The order or request that the dispense is fulfilling. This is a reference to a ServiceRequest resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
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
         * The order or request that the dispense is fulfilling. This is a reference to a ServiceRequest resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
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
         * A larger event of which this particular event is a component.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link BiologicallyDerivedProductDispense}</li>
         * </ul>
         * 
         * @param partOf
         *     Short description
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
         * A larger event of which this particular event is a component.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link BiologicallyDerivedProductDispense}</li>
         * </ul>
         * 
         * @param partOf
         *     Short description
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
         * A code specifying the state of the dispense event.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     preparation | in-progress | allocated | issued | unfulfilled | returned | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(BiologicallyDerivedProductDispenseStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the relationship between the donor of the biologically derived product and the intended recipient.
         * 
         * @param originRelationshipType
         *     Relationship between the donor and intended recipient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder originRelationshipType(CodeableConcept originRelationshipType) {
            this.originRelationshipType = originRelationshipType;
            return this;
        }

        /**
         * A link to a resource identifying the biologically derived product that is being dispensed.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * </ul>
         * 
         * @param product
         *     The BiologicallyDerivedProduct that is dispensed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder product(Reference product) {
            this.product = product;
            return this;
        }

        /**
         * A link to a resource representing the patient that the product is dispensed for.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     The intended recipient of the dispensed product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * Indicates the type of matching associated with the dispense.
         * 
         * @param matchStatus
         *     Indicates the type of matching associated with the dispense
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder matchStatus(CodeableConcept matchStatus) {
            this.matchStatus = matchStatus;
            return this;
        }

        /**
         * Indicates who or what performed an action.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Indicates who or what performed an action
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
         * Indicates who or what performed an action.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Indicates who or what performed an action
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
         * The physical location where the dispense was performed.
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
         * The amount of product in the dispense. Quantity will depend on the product being dispensed. Examples are: volume; cell 
         * count; concentration.
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
         * When the product was selected/ matched.
         * 
         * @param preparedDate
         *     When product was selected/matched
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preparedDate(DateTime preparedDate) {
            this.preparedDate = preparedDate;
            return this;
        }

        /**
         * When the product was dispatched for clinical use.
         * 
         * @param whenHandedOver
         *     When the product was dispatched
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder whenHandedOver(DateTime whenHandedOver) {
            this.whenHandedOver = whenHandedOver;
            return this;
        }

        /**
         * Link to a resource identifying the physical location that the product was dispatched to.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param destination
         *     Where the product was dispatched to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder destination(Reference destination) {
            this.destination = destination;
            return this;
        }

        /**
         * Additional notes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional notes
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
         * Additional notes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional notes
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
         * Convenience method for setting {@code usageInstruction}.
         * 
         * @param usageInstruction
         *     Specific instructions for use
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #usageInstruction(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder usageInstruction(java.lang.String usageInstruction) {
            this.usageInstruction = (usageInstruction == null) ? null : String.of(usageInstruction);
            return this;
        }

        /**
         * Specific instructions for use.
         * 
         * @param usageInstruction
         *     Specific instructions for use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usageInstruction(String usageInstruction) {
            this.usageInstruction = usageInstruction;
            return this;
        }

        /**
         * Build the {@link BiologicallyDerivedProductDispense}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>product</li>
         * <li>patient</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link BiologicallyDerivedProductDispense}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid BiologicallyDerivedProductDispense per the base specification
         */
        @Override
        public BiologicallyDerivedProductDispense build() {
            BiologicallyDerivedProductDispense biologicallyDerivedProductDispense = new BiologicallyDerivedProductDispense(this);
            if (validating) {
                validate(biologicallyDerivedProductDispense);
            }
            return biologicallyDerivedProductDispense;
        }

        protected void validate(BiologicallyDerivedProductDispense biologicallyDerivedProductDispense) {
            super.validate(biologicallyDerivedProductDispense);
            ValidationSupport.checkList(biologicallyDerivedProductDispense.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(biologicallyDerivedProductDispense.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(biologicallyDerivedProductDispense.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(biologicallyDerivedProductDispense.status, "status");
            ValidationSupport.requireNonNull(biologicallyDerivedProductDispense.product, "product");
            ValidationSupport.requireNonNull(biologicallyDerivedProductDispense.patient, "patient");
            ValidationSupport.checkList(biologicallyDerivedProductDispense.performer, "performer", Performer.class);
            ValidationSupport.checkList(biologicallyDerivedProductDispense.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.basedOn, "basedOn", "ServiceRequest");
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.partOf, "partOf", "BiologicallyDerivedProductDispense");
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.product, "product", "BiologicallyDerivedProduct");
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.location, "location", "Location");
            ValidationSupport.checkReferenceType(biologicallyDerivedProductDispense.destination, "destination", "Location");
        }

        protected Builder from(BiologicallyDerivedProductDispense biologicallyDerivedProductDispense) {
            super.from(biologicallyDerivedProductDispense);
            identifier.addAll(biologicallyDerivedProductDispense.identifier);
            basedOn.addAll(biologicallyDerivedProductDispense.basedOn);
            partOf.addAll(biologicallyDerivedProductDispense.partOf);
            status = biologicallyDerivedProductDispense.status;
            originRelationshipType = biologicallyDerivedProductDispense.originRelationshipType;
            product = biologicallyDerivedProductDispense.product;
            patient = biologicallyDerivedProductDispense.patient;
            matchStatus = biologicallyDerivedProductDispense.matchStatus;
            performer.addAll(biologicallyDerivedProductDispense.performer);
            location = biologicallyDerivedProductDispense.location;
            quantity = biologicallyDerivedProductDispense.quantity;
            preparedDate = biologicallyDerivedProductDispense.preparedDate;
            whenHandedOver = biologicallyDerivedProductDispense.whenHandedOver;
            destination = biologicallyDerivedProductDispense.destination;
            note.addAll(biologicallyDerivedProductDispense.note);
            usageInstruction = biologicallyDerivedProductDispense.usageInstruction;
            return this;
        }
    }

    /**
     * Indicates who or what performed an action.
     */
    public static class Performer extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "BiologicallyDerivedProductDispensPerformerFunction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Describes the the role or function of the performer in the dispense.",
            valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderivedproductdispense-performer-function"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner" })
        @Required
        private final Reference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Identifies the function of the performer during the dispense.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Identifies the person responsible for the action.
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
             * Identifies the function of the performer during the dispense.
             * 
             * @param function
             *     Identifies the function of the performer during the dispense
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Identifies the person responsible for the action.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * </ul>
             * 
             * @param actor
             *     Who performed the action
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
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner");
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
