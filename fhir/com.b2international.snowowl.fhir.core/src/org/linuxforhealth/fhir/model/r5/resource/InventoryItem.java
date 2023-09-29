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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.InventoryItemStatus;
import org.linuxforhealth.fhir.model.r5.type.code.ItemDescriptionLanguage;
import org.linuxforhealth.fhir.model.r5.type.code.NameLanguage;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A functional description of an inventory item used in inventory and supply-related workflows.
 * 
 * <p>Maturity level: FMM0 (draft)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.DRAFT
)
@Constraint(
    id = "inventoryItem-0",
    level = "Warning",
    location = "name.nameType",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/inventoryitem-nametype",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/inventoryitem-nametype', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/InventoryItem",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class InventoryItem extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "InventoryItemStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Status of the inventory item.",
        valueSet = "http://hl7.org/fhir/ValueSet/inventoryitem-status|5.0.0"
    )
    @Required
    private final InventoryItemStatus status;
    @Summary
    private final List<CodeableConcept> category;
    @Summary
    private final List<CodeableConcept> code;
    @Summary
    private final List<Name> name;
    private final List<ResponsibleOrganization> responsibleOrganization;
    private final Description description;
    @Summary
    private final List<CodeableConcept> inventoryStatus;
    @Summary
    private final CodeableConcept baseUnit;
    @Summary
    private final SimpleQuantity netContent;
    private final List<Association> association;
    private final List<Characteristic> characteristic;
    private final Instance instance;
    @ReferenceTarget({ "Medication", "Device", "NutritionProduct", "BiologicallyDerivedProduct" })
    private final Reference productReference;

    private InventoryItem(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        category = Collections.unmodifiableList(builder.category);
        code = Collections.unmodifiableList(builder.code);
        name = Collections.unmodifiableList(builder.name);
        responsibleOrganization = Collections.unmodifiableList(builder.responsibleOrganization);
        description = builder.description;
        inventoryStatus = Collections.unmodifiableList(builder.inventoryStatus);
        baseUnit = builder.baseUnit;
        netContent = builder.netContent;
        association = Collections.unmodifiableList(builder.association);
        characteristic = Collections.unmodifiableList(builder.characteristic);
        instance = builder.instance;
        productReference = builder.productReference;
    }

    /**
     * Business identifier for the inventory item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Status of the item entry.
     * 
     * @return
     *     An immutable object of type {@link InventoryItemStatus} that is non-null.
     */
    public InventoryItemStatus getStatus() {
        return status;
    }

    /**
     * Category or class of the item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Code designating the specific type of item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCode() {
        return code;
    }

    /**
     * The item name(s) - the brand name, or common name, functional name, generic name.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Name} that may be empty.
     */
    public List<Name> getName() {
        return name;
    }

    /**
     * Organization(s) responsible for the product.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ResponsibleOrganization} that may be empty.
     */
    public List<ResponsibleOrganization> getResponsibleOrganization() {
        return responsibleOrganization;
    }

    /**
     * The descriptive characteristics of the inventory item.
     * 
     * @return
     *     An immutable object of type {@link Description} that may be null.
     */
    public Description getDescription() {
        return description;
    }

    /**
     * The usage status e.g. recalled, in use, discarded... This can be used to indicate that the items have been taken out 
     * of inventory, or are in use, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getInventoryStatus() {
        return inventoryStatus;
    }

    /**
     * The base unit of measure - the unit in which the product is used or counted.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getBaseUnit() {
        return baseUnit;
    }

    /**
     * Net content or amount present in the item.
     * 
     * @return
     *     An immutable object of type {@link SimpleQuantity} that may be null.
     */
    public SimpleQuantity getNetContent() {
        return netContent;
    }

    /**
     * Association with other items or products.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Association} that may be empty.
     */
    public List<Association> getAssociation() {
        return association;
    }

    /**
     * The descriptive or identifying characteristics of the item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Characteristic} that may be empty.
     */
    public List<Characteristic> getCharacteristic() {
        return characteristic;
    }

    /**
     * Instances or occurrences of the product.
     * 
     * @return
     *     An immutable object of type {@link Instance} that may be null.
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Link to a product resource used in clinical workflows.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getProductReference() {
        return productReference;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !category.isEmpty() || 
            !code.isEmpty() || 
            !name.isEmpty() || 
            !responsibleOrganization.isEmpty() || 
            (description != null) || 
            !inventoryStatus.isEmpty() || 
            (baseUnit != null) || 
            (netContent != null) || 
            !association.isEmpty() || 
            !characteristic.isEmpty() || 
            (instance != null) || 
            (productReference != null);
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
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor, CodeableConcept.class);
                accept(name, "name", visitor, Name.class);
                accept(responsibleOrganization, "responsibleOrganization", visitor, ResponsibleOrganization.class);
                accept(description, "description", visitor);
                accept(inventoryStatus, "inventoryStatus", visitor, CodeableConcept.class);
                accept(baseUnit, "baseUnit", visitor);
                accept(netContent, "netContent", visitor);
                accept(association, "association", visitor, Association.class);
                accept(characteristic, "characteristic", visitor, Characteristic.class);
                accept(instance, "instance", visitor);
                accept(productReference, "productReference", visitor);
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
        InventoryItem other = (InventoryItem) obj;
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
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(name, other.name) && 
            Objects.equals(responsibleOrganization, other.responsibleOrganization) && 
            Objects.equals(description, other.description) && 
            Objects.equals(inventoryStatus, other.inventoryStatus) && 
            Objects.equals(baseUnit, other.baseUnit) && 
            Objects.equals(netContent, other.netContent) && 
            Objects.equals(association, other.association) && 
            Objects.equals(characteristic, other.characteristic) && 
            Objects.equals(instance, other.instance) && 
            Objects.equals(productReference, other.productReference);
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
                category, 
                code, 
                name, 
                responsibleOrganization, 
                description, 
                inventoryStatus, 
                baseUnit, 
                netContent, 
                association, 
                characteristic, 
                instance, 
                productReference);
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
        private InventoryItemStatus status;
        private List<CodeableConcept> category = new ArrayList<>();
        private List<CodeableConcept> code = new ArrayList<>();
        private List<Name> name = new ArrayList<>();
        private List<ResponsibleOrganization> responsibleOrganization = new ArrayList<>();
        private Description description;
        private List<CodeableConcept> inventoryStatus = new ArrayList<>();
        private CodeableConcept baseUnit;
        private SimpleQuantity netContent;
        private List<Association> association = new ArrayList<>();
        private List<Characteristic> characteristic = new ArrayList<>();
        private Instance instance;
        private Reference productReference;

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
         * Business identifier for the inventory item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the inventory item
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
         * Business identifier for the inventory item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the inventory item
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
         * Status of the item entry.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | inactive | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(InventoryItemStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Category or class of the item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Category or class of the item
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
         * Category or class of the item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Category or class of the item
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
         * Code designating the specific type of item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param code
         *     Code designating the specific type of item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept... code) {
            for (CodeableConcept value : code) {
                this.code.add(value);
            }
            return this;
        }

        /**
         * Code designating the specific type of item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param code
         *     Code designating the specific type of item
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder code(Collection<CodeableConcept> code) {
            this.code = new ArrayList<>(code);
            return this;
        }

        /**
         * The item name(s) - the brand name, or common name, functional name, generic name.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     The item name(s) - the brand name, or common name, functional name, generic name or others
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(Name... name) {
            for (Name value : name) {
                this.name.add(value);
            }
            return this;
        }

        /**
         * The item name(s) - the brand name, or common name, functional name, generic name.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     The item name(s) - the brand name, or common name, functional name, generic name or others
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder name(Collection<Name> name) {
            this.name = new ArrayList<>(name);
            return this;
        }

        /**
         * Organization(s) responsible for the product.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param responsibleOrganization
         *     Organization(s) responsible for the product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder responsibleOrganization(ResponsibleOrganization... responsibleOrganization) {
            for (ResponsibleOrganization value : responsibleOrganization) {
                this.responsibleOrganization.add(value);
            }
            return this;
        }

        /**
         * Organization(s) responsible for the product.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param responsibleOrganization
         *     Organization(s) responsible for the product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder responsibleOrganization(Collection<ResponsibleOrganization> responsibleOrganization) {
            this.responsibleOrganization = new ArrayList<>(responsibleOrganization);
            return this;
        }

        /**
         * The descriptive characteristics of the inventory item.
         * 
         * @param description
         *     Descriptive characteristics of the item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Description description) {
            this.description = description;
            return this;
        }

        /**
         * The usage status e.g. recalled, in use, discarded... This can be used to indicate that the items have been taken out 
         * of inventory, or are in use, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param inventoryStatus
         *     The usage status like recalled, in use, discarded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder inventoryStatus(CodeableConcept... inventoryStatus) {
            for (CodeableConcept value : inventoryStatus) {
                this.inventoryStatus.add(value);
            }
            return this;
        }

        /**
         * The usage status e.g. recalled, in use, discarded... This can be used to indicate that the items have been taken out 
         * of inventory, or are in use, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param inventoryStatus
         *     The usage status like recalled, in use, discarded
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder inventoryStatus(Collection<CodeableConcept> inventoryStatus) {
            this.inventoryStatus = new ArrayList<>(inventoryStatus);
            return this;
        }

        /**
         * The base unit of measure - the unit in which the product is used or counted.
         * 
         * @param baseUnit
         *     The base unit of measure - the unit in which the product is used or counted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder baseUnit(CodeableConcept baseUnit) {
            this.baseUnit = baseUnit;
            return this;
        }

        /**
         * Net content or amount present in the item.
         * 
         * @param netContent
         *     Net content or amount present in the item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder netContent(SimpleQuantity netContent) {
            this.netContent = netContent;
            return this;
        }

        /**
         * Association with other items or products.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param association
         *     Association with other items or products
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder association(Association... association) {
            for (Association value : association) {
                this.association.add(value);
            }
            return this;
        }

        /**
         * Association with other items or products.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param association
         *     Association with other items or products
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder association(Collection<Association> association) {
            this.association = new ArrayList<>(association);
            return this;
        }

        /**
         * The descriptive or identifying characteristics of the item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     Characteristic of the item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder characteristic(Characteristic... characteristic) {
            for (Characteristic value : characteristic) {
                this.characteristic.add(value);
            }
            return this;
        }

        /**
         * The descriptive or identifying characteristics of the item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     Characteristic of the item
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder characteristic(Collection<Characteristic> characteristic) {
            this.characteristic = new ArrayList<>(characteristic);
            return this;
        }

        /**
         * Instances or occurrences of the product.
         * 
         * @param instance
         *     Instances or occurrences of the product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instance(Instance instance) {
            this.instance = instance;
            return this;
        }

        /**
         * Link to a product resource used in clinical workflows.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Medication}</li>
         * <li>{@link Device}</li>
         * <li>{@link NutritionProduct}</li>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * </ul>
         * 
         * @param productReference
         *     Link to a product resource used in clinical workflows
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productReference(Reference productReference) {
            this.productReference = productReference;
            return this;
        }

        /**
         * Build the {@link InventoryItem}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link InventoryItem}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid InventoryItem per the base specification
         */
        @Override
        public InventoryItem build() {
            InventoryItem inventoryItem = new InventoryItem(this);
            if (validating) {
                validate(inventoryItem);
            }
            return inventoryItem;
        }

        protected void validate(InventoryItem inventoryItem) {
            super.validate(inventoryItem);
            ValidationSupport.checkList(inventoryItem.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(inventoryItem.status, "status");
            ValidationSupport.checkList(inventoryItem.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(inventoryItem.code, "code", CodeableConcept.class);
            ValidationSupport.checkList(inventoryItem.name, "name", Name.class);
            ValidationSupport.checkList(inventoryItem.responsibleOrganization, "responsibleOrganization", ResponsibleOrganization.class);
            ValidationSupport.checkList(inventoryItem.inventoryStatus, "inventoryStatus", CodeableConcept.class);
            ValidationSupport.checkList(inventoryItem.association, "association", Association.class);
            ValidationSupport.checkList(inventoryItem.characteristic, "characteristic", Characteristic.class);
            ValidationSupport.checkReferenceType(inventoryItem.productReference, "productReference", "Medication", "Device", "NutritionProduct", "BiologicallyDerivedProduct");
        }

        protected Builder from(InventoryItem inventoryItem) {
            super.from(inventoryItem);
            identifier.addAll(inventoryItem.identifier);
            status = inventoryItem.status;
            category.addAll(inventoryItem.category);
            code.addAll(inventoryItem.code);
            name.addAll(inventoryItem.name);
            responsibleOrganization.addAll(inventoryItem.responsibleOrganization);
            description = inventoryItem.description;
            inventoryStatus.addAll(inventoryItem.inventoryStatus);
            baseUnit = inventoryItem.baseUnit;
            netContent = inventoryItem.netContent;
            association.addAll(inventoryItem.association);
            characteristic.addAll(inventoryItem.characteristic);
            instance = inventoryItem.instance;
            productReference = inventoryItem.productReference;
            return this;
        }
    }

    /**
     * The item name(s) - the brand name, or common name, functional name, generic name.
     */
    public static class Name extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "NameType",
            strength = BindingStrength.Value.PREFERRED,
            description = "Name types.",
            valueSet = "http://hl7.org/fhir/ValueSet/inventoryitem-nametype"
        )
        @Required
        private final Coding nameType;
        @Summary
        @Binding(
            bindingName = "NameLanguage",
            strength = BindingStrength.Value.REQUIRED,
            description = "Name languages.",
            valueSet = "http://hl7.org/fhir/ValueSet/languages|5.0.0"
        )
        @Required
        private final NameLanguage language;
        @Summary
        @Required
        private final String name;

        private Name(Builder builder) {
            super(builder);
            nameType = builder.nameType;
            language = builder.language;
            name = builder.name;
        }

        /**
         * The type of name e.g. 'brand-name', 'functional-name', 'common-name'.
         * 
         * @return
         *     An immutable object of type {@link Coding} that is non-null.
         */
        public Coding getNameType() {
            return nameType;
        }

        /**
         * The language that the item name is expressed in.
         * 
         * @return
         *     An immutable object of type {@link NameLanguage} that is non-null.
         */
        public NameLanguage getLanguage() {
            return language;
        }

        /**
         * The name or designation that the item is given.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getName() {
            return name;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (nameType != null) || 
                (language != null) || 
                (name != null);
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
                    accept(nameType, "nameType", visitor);
                    accept(language, "language", visitor);
                    accept(name, "name", visitor);
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
            Name other = (Name) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(nameType, other.nameType) && 
                Objects.equals(language, other.language) && 
                Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    nameType, 
                    language, 
                    name);
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
            private Coding nameType;
            private NameLanguage language;
            private String name;

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
             * The type of name e.g. 'brand-name', 'functional-name', 'common-name'.
             * 
             * <p>This element is required.
             * 
             * @param nameType
             *     The type of name e.g. 'brand-name', 'functional-name', 'common-name'
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder nameType(Coding nameType) {
                this.nameType = nameType;
                return this;
            }

            /**
             * The language that the item name is expressed in.
             * 
             * <p>This element is required.
             * 
             * @param language
             *     The language used to express the item name
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder language(NameLanguage language) {
                this.language = language;
                return this;
            }

            /**
             * Convenience method for setting {@code name}.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     The name or designation of the item
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #name(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder name(java.lang.String name) {
                this.name = (name == null) ? null : String.of(name);
                return this;
            }

            /**
             * The name or designation that the item is given.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     The name or designation of the item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Build the {@link Name}
             * 
             * <p>Required elements:
             * <ul>
             * <li>nameType</li>
             * <li>language</li>
             * <li>name</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Name}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Name per the base specification
             */
            @Override
            public Name build() {
                Name name = new Name(this);
                if (validating) {
                    validate(name);
                }
                return name;
            }

            protected void validate(Name name) {
                super.validate(name);
                ValidationSupport.requireNonNull(name.nameType, "nameType");
                ValidationSupport.requireNonNull(name.language, "language");
                ValidationSupport.requireNonNull(name.name, "name");
                ValidationSupport.requireValueOrChildren(name);
            }

            protected Builder from(Name name) {
                super.from(name);
                nameType = name.nameType;
                language = name.language;
                this.name = name.name;
                return this;
            }
        }
    }

    /**
     * Organization(s) responsible for the product.
     */
    public static class ResponsibleOrganization extends BackboneElement {
        @Required
        private final CodeableConcept role;
        @ReferenceTarget({ "Organization" })
        @Required
        private final Reference organization;

        private ResponsibleOrganization(Builder builder) {
            super(builder);
            role = builder.role;
            organization = builder.organization;
        }

        /**
         * The role of the organization e.g. manufacturer, distributor, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getRole() {
            return role;
        }

        /**
         * An organization that has an association with the item, e.g. manufacturer, distributor, responsible, etc.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getOrganization() {
            return organization;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (role != null) || 
                (organization != null);
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
                    accept(role, "role", visitor);
                    accept(organization, "organization", visitor);
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
            ResponsibleOrganization other = (ResponsibleOrganization) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(role, other.role) && 
                Objects.equals(organization, other.organization);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    role, 
                    organization);
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
            private CodeableConcept role;
            private Reference organization;

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
             * The role of the organization e.g. manufacturer, distributor, etc.
             * 
             * <p>This element is required.
             * 
             * @param role
             *     The role of the organization e.g. manufacturer, distributor, or other
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder role(CodeableConcept role) {
                this.role = role;
                return this;
            }

            /**
             * An organization that has an association with the item, e.g. manufacturer, distributor, responsible, etc.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param organization
             *     An organization that is associated with the item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder organization(Reference organization) {
                this.organization = organization;
                return this;
            }

            /**
             * Build the {@link ResponsibleOrganization}
             * 
             * <p>Required elements:
             * <ul>
             * <li>role</li>
             * <li>organization</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ResponsibleOrganization}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ResponsibleOrganization per the base specification
             */
            @Override
            public ResponsibleOrganization build() {
                ResponsibleOrganization responsibleOrganization = new ResponsibleOrganization(this);
                if (validating) {
                    validate(responsibleOrganization);
                }
                return responsibleOrganization;
            }

            protected void validate(ResponsibleOrganization responsibleOrganization) {
                super.validate(responsibleOrganization);
                ValidationSupport.requireNonNull(responsibleOrganization.role, "role");
                ValidationSupport.requireNonNull(responsibleOrganization.organization, "organization");
                ValidationSupport.checkReferenceType(responsibleOrganization.organization, "organization", "Organization");
                ValidationSupport.requireValueOrChildren(responsibleOrganization);
            }

            protected Builder from(ResponsibleOrganization responsibleOrganization) {
                super.from(responsibleOrganization);
                role = responsibleOrganization.role;
                organization = responsibleOrganization.organization;
                return this;
            }
        }
    }

    /**
     * The descriptive characteristics of the inventory item.
     */
    public static class Description extends BackboneElement {
        @Binding(
            bindingName = "ItemDescriptionLanguage",
            strength = BindingStrength.Value.REQUIRED,
            description = "Description languages.",
            valueSet = "http://hl7.org/fhir/ValueSet/languages|5.0.0"
        )
        private final ItemDescriptionLanguage language;
        private final String description;

        private Description(Builder builder) {
            super(builder);
            language = builder.language;
            description = builder.description;
        }

        /**
         * The language for the item description, when an item must be described in different languages and those languages may 
         * be authoritative and not translations of a 'main' language.
         * 
         * @return
         *     An immutable object of type {@link ItemDescriptionLanguage} that may be null.
         */
        public ItemDescriptionLanguage getLanguage() {
            return language;
        }

        /**
         * Textual description of the item.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (language != null) || 
                (description != null);
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
                    accept(language, "language", visitor);
                    accept(description, "description", visitor);
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
            Description other = (Description) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(language, other.language) && 
                Objects.equals(description, other.description);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    language, 
                    description);
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
            private ItemDescriptionLanguage language;
            private String description;

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
             * The language for the item description, when an item must be described in different languages and those languages may 
             * be authoritative and not translations of a 'main' language.
             * 
             * @param language
             *     The language that is used in the item description
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder language(ItemDescriptionLanguage language) {
                this.language = language;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     Textual description of the item
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
             * Textual description of the item.
             * 
             * @param description
             *     Textual description of the item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * Build the {@link Description}
             * 
             * @return
             *     An immutable object of type {@link Description}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Description per the base specification
             */
            @Override
            public Description build() {
                Description description = new Description(this);
                if (validating) {
                    validate(description);
                }
                return description;
            }

            protected void validate(Description description) {
                super.validate(description);
                ValidationSupport.requireValueOrChildren(description);
            }

            protected Builder from(Description description) {
                super.from(description);
                language = description.language;
                this.description = description.description;
                return this;
            }
        }
    }

    /**
     * Association with other items or products.
     */
    public static class Association extends BackboneElement {
        @Summary
        @Required
        private final CodeableConcept associationType;
        @Summary
        @ReferenceTarget({ "InventoryItem", "Medication", "MedicationKnowledge", "Device", "DeviceDefinition", "NutritionProduct", "BiologicallyDerivedProduct" })
        @Required
        private final Reference relatedItem;
        @Summary
        @Required
        private final Ratio quantity;

        private Association(Builder builder) {
            super(builder);
            associationType = builder.associationType;
            relatedItem = builder.relatedItem;
            quantity = builder.quantity;
        }

        /**
         * This attribute defined the type of association when establishing associations or relations between items, e.g. 
         * 'packaged within' or 'used with' or 'to be mixed with.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getAssociationType() {
            return associationType;
        }

        /**
         * The related item or product.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getRelatedItem() {
            return relatedItem;
        }

        /**
         * The quantity of the related product in this product - Numerator is the quantity of the related product. Denominator is 
         * the quantity of the present product. For example a value of 20 means that this product contains 20 units of the 
         * related product; a value of 1:20 means the inverse - that the contained product contains 20 units of the present 
         * product.
         * 
         * @return
         *     An immutable object of type {@link Ratio} that is non-null.
         */
        public Ratio getQuantity() {
            return quantity;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (associationType != null) || 
                (relatedItem != null) || 
                (quantity != null);
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
                    accept(associationType, "associationType", visitor);
                    accept(relatedItem, "relatedItem", visitor);
                    accept(quantity, "quantity", visitor);
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
            Association other = (Association) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(associationType, other.associationType) && 
                Objects.equals(relatedItem, other.relatedItem) && 
                Objects.equals(quantity, other.quantity);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    associationType, 
                    relatedItem, 
                    quantity);
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
            private CodeableConcept associationType;
            private Reference relatedItem;
            private Ratio quantity;

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
             * This attribute defined the type of association when establishing associations or relations between items, e.g. 
             * 'packaged within' or 'used with' or 'to be mixed with.
             * 
             * <p>This element is required.
             * 
             * @param associationType
             *     The type of association between the device and the other item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder associationType(CodeableConcept associationType) {
                this.associationType = associationType;
                return this;
            }

            /**
             * The related item or product.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link InventoryItem}</li>
             * <li>{@link Medication}</li>
             * <li>{@link MedicationKnowledge}</li>
             * <li>{@link Device}</li>
             * <li>{@link DeviceDefinition}</li>
             * <li>{@link NutritionProduct}</li>
             * <li>{@link BiologicallyDerivedProduct}</li>
             * </ul>
             * 
             * @param relatedItem
             *     The related item or product
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatedItem(Reference relatedItem) {
                this.relatedItem = relatedItem;
                return this;
            }

            /**
             * The quantity of the related product in this product - Numerator is the quantity of the related product. Denominator is 
             * the quantity of the present product. For example a value of 20 means that this product contains 20 units of the 
             * related product; a value of 1:20 means the inverse - that the contained product contains 20 units of the present 
             * product.
             * 
             * <p>This element is required.
             * 
             * @param quantity
             *     The quantity of the product in this product
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(Ratio quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * Build the {@link Association}
             * 
             * <p>Required elements:
             * <ul>
             * <li>associationType</li>
             * <li>relatedItem</li>
             * <li>quantity</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Association}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Association per the base specification
             */
            @Override
            public Association build() {
                Association association = new Association(this);
                if (validating) {
                    validate(association);
                }
                return association;
            }

            protected void validate(Association association) {
                super.validate(association);
                ValidationSupport.requireNonNull(association.associationType, "associationType");
                ValidationSupport.requireNonNull(association.relatedItem, "relatedItem");
                ValidationSupport.requireNonNull(association.quantity, "quantity");
                ValidationSupport.checkReferenceType(association.relatedItem, "relatedItem", "InventoryItem", "Medication", "MedicationKnowledge", "Device", "DeviceDefinition", "NutritionProduct", "BiologicallyDerivedProduct");
                ValidationSupport.requireValueOrChildren(association);
            }

            protected Builder from(Association association) {
                super.from(association);
                associationType = association.associationType;
                relatedItem = association.relatedItem;
                quantity = association.quantity;
                return this;
            }
        }
    }

    /**
     * The descriptive or identifying characteristics of the item.
     */
    public static class Characteristic extends BackboneElement {
        @Required
        private final CodeableConcept characteristicType;
        @Choice({ String.class, Integer.class, Decimal.class, Boolean.class, Url.class, DateTime.class, Quantity.class, Range.class, Ratio.class, Annotation.class, Address.class, Duration.class, CodeableConcept.class })
        @Required
        private final Element value;

        private Characteristic(Builder builder) {
            super(builder);
            characteristicType = builder.characteristicType;
            value = builder.value;
        }

        /**
         * The type of characteristic that is being defined.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCharacteristicType() {
            return characteristicType;
        }

        /**
         * The value of the attribute.
         * 
         * @return
         *     An immutable object of type {@link String}, {@link Integer}, {@link Decimal}, {@link Boolean}, {@link Url}, {@link 
         *     DateTime}, {@link Quantity}, {@link Range}, {@link Ratio}, {@link Annotation}, {@link Address}, {@link Duration} or 
         *     {@link CodeableConcept} that is non-null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (characteristicType != null) || 
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
                    accept(characteristicType, "characteristicType", visitor);
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
            Characteristic other = (Characteristic) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(characteristicType, other.characteristicType) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    characteristicType, 
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
            private CodeableConcept characteristicType;
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
             * The type of characteristic that is being defined.
             * 
             * <p>This element is required.
             * 
             * @param characteristicType
             *     The characteristic that is being defined
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder characteristicType(CodeableConcept characteristicType) {
                this.characteristicType = characteristicType;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type String.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The value of the attribute
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
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The value of the attribute
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
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The value of the attribute
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
             * The value of the attribute.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link String}</li>
             * <li>{@link Integer}</li>
             * <li>{@link Decimal}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Url}</li>
             * <li>{@link DateTime}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Ratio}</li>
             * <li>{@link Annotation}</li>
             * <li>{@link Address}</li>
             * <li>{@link Duration}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * @param value
             *     The value of the attribute
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Characteristic}
             * 
             * <p>Required elements:
             * <ul>
             * <li>characteristicType</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Characteristic}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Characteristic per the base specification
             */
            @Override
            public Characteristic build() {
                Characteristic characteristic = new Characteristic(this);
                if (validating) {
                    validate(characteristic);
                }
                return characteristic;
            }

            protected void validate(Characteristic characteristic) {
                super.validate(characteristic);
                ValidationSupport.requireNonNull(characteristic.characteristicType, "characteristicType");
                ValidationSupport.requireChoiceElement(characteristic.value, "value", String.class, Integer.class, Decimal.class, Boolean.class, Url.class, DateTime.class, Quantity.class, Range.class, Ratio.class, Annotation.class, Address.class, Duration.class, CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(characteristic);
            }

            protected Builder from(Characteristic characteristic) {
                super.from(characteristic);
                characteristicType = characteristic.characteristicType;
                value = characteristic.value;
                return this;
            }
        }
    }

    /**
     * Instances or occurrences of the product.
     */
    public static class Instance extends BackboneElement {
        private final List<Identifier> identifier;
        private final String lotNumber;
        private final DateTime expiry;
        @ReferenceTarget({ "Patient", "Organization" })
        private final Reference subject;
        @ReferenceTarget({ "Location" })
        private final Reference location;

        private Instance(Builder builder) {
            super(builder);
            identifier = Collections.unmodifiableList(builder.identifier);
            lotNumber = builder.lotNumber;
            expiry = builder.expiry;
            subject = builder.subject;
            location = builder.location;
        }

        /**
         * The identifier for the physical instance, typically a serial number.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getIdentifier() {
            return identifier;
        }

        /**
         * The lot or batch number of the item.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLotNumber() {
            return lotNumber;
        }

        /**
         * The expiry date or date and time for the product.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getExpiry() {
            return expiry;
        }

        /**
         * The subject that the item is associated with.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSubject() {
            return subject;
        }

        /**
         * The location that the item is associated with.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getLocation() {
            return location;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !identifier.isEmpty() || 
                (lotNumber != null) || 
                (expiry != null) || 
                (subject != null) || 
                (location != null);
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
                    accept(identifier, "identifier", visitor, Identifier.class);
                    accept(lotNumber, "lotNumber", visitor);
                    accept(expiry, "expiry", visitor);
                    accept(subject, "subject", visitor);
                    accept(location, "location", visitor);
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
            Instance other = (Instance) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(identifier, other.identifier) && 
                Objects.equals(lotNumber, other.lotNumber) && 
                Objects.equals(expiry, other.expiry) && 
                Objects.equals(subject, other.subject) && 
                Objects.equals(location, other.location);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    identifier, 
                    lotNumber, 
                    expiry, 
                    subject, 
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

        public static class Builder extends BackboneElement.Builder {
            private List<Identifier> identifier = new ArrayList<>();
            private String lotNumber;
            private DateTime expiry;
            private Reference subject;
            private Reference location;

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
             * The identifier for the physical instance, typically a serial number.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     The identifier for the physical instance, typically a serial number
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
             * The identifier for the physical instance, typically a serial number.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     The identifier for the physical instance, typically a serial number
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
             * Convenience method for setting {@code lotNumber}.
             * 
             * @param lotNumber
             *     The lot or batch number of the item
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #lotNumber(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder lotNumber(java.lang.String lotNumber) {
                this.lotNumber = (lotNumber == null) ? null : String.of(lotNumber);
                return this;
            }

            /**
             * The lot or batch number of the item.
             * 
             * @param lotNumber
             *     The lot or batch number of the item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder lotNumber(String lotNumber) {
                this.lotNumber = lotNumber;
                return this;
            }

            /**
             * The expiry date or date and time for the product.
             * 
             * @param expiry
             *     The expiry date or date and time for the product
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expiry(DateTime expiry) {
                this.expiry = expiry;
                return this;
            }

            /**
             * The subject that the item is associated with.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param subject
             *     The subject that the item is associated with
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subject(Reference subject) {
                this.subject = subject;
                return this;
            }

            /**
             * The location that the item is associated with.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     The location that the item is associated with
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * Build the {@link Instance}
             * 
             * @return
             *     An immutable object of type {@link Instance}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Instance per the base specification
             */
            @Override
            public Instance build() {
                Instance instance = new Instance(this);
                if (validating) {
                    validate(instance);
                }
                return instance;
            }

            protected void validate(Instance instance) {
                super.validate(instance);
                ValidationSupport.checkList(instance.identifier, "identifier", Identifier.class);
                ValidationSupport.checkReferenceType(instance.subject, "subject", "Patient", "Organization");
                ValidationSupport.checkReferenceType(instance.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(instance);
            }

            protected Builder from(Instance instance) {
                super.from(instance);
                identifier.addAll(instance.identifier);
                lotNumber = instance.lotNumber;
                expiry = instance.expiry;
                subject = instance.subject;
                location = instance.location;
                return this;
            }
        }
    }
}
