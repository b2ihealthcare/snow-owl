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
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.MarketingStatus;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The definition and characteristics of a medicinal manufactured item, such as a tablet or capsule, as contained in a 
 * packaged medicinal product.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ManufacturedItemDefinition extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The lifecycle status of an artifact.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    @Summary
    private final String name;
    @Summary
    @Binding(
        bindingName = "ManufacturedDoseForm",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Dose form for a medication, in the form suitable for administering to the patient, after mixing, where necessary.",
        valueSet = "http://hl7.org/fhir/ValueSet/manufactured-dose-form"
    )
    @Required
    private final CodeableConcept manufacturedDoseForm;
    @Summary
    @Binding(
        bindingName = "UnitOfPresentation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The presentation type in which an administrable medicinal product is given to a patient.",
        valueSet = "http://hl7.org/fhir/ValueSet/unit-of-presentation"
    )
    private final CodeableConcept unitOfPresentation;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final List<Reference> manufacturer;
    @Summary
    private final List<MarketingStatus> marketingStatus;
    @Summary
    @Binding(
        bindingName = "SNOMEDCTSubstanceCodes",
        strength = BindingStrength.Value.EXAMPLE,
        description = "This value set includes all substance codes from SNOMED CT - provided as an exemplar value set.",
        valueSet = "http://hl7.org/fhir/ValueSet/substance-codes"
    )
    private final List<CodeableConcept> ingredient;
    @Summary
    private final List<Property> property;
    @Summary
    private final List<Component> component;

    private ManufacturedItemDefinition(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        name = builder.name;
        manufacturedDoseForm = builder.manufacturedDoseForm;
        unitOfPresentation = builder.unitOfPresentation;
        manufacturer = Collections.unmodifiableList(builder.manufacturer);
        marketingStatus = Collections.unmodifiableList(builder.marketingStatus);
        ingredient = Collections.unmodifiableList(builder.ingredient);
        property = Collections.unmodifiableList(builder.property);
        component = Collections.unmodifiableList(builder.component);
    }

    /**
     * Unique identifier.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The status of this item. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A descriptive name applied to this item.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * Dose form as manufactured and before any transformation into the pharmaceutical product.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getManufacturedDoseForm() {
        return manufacturedDoseForm;
    }

    /**
     * The “real-world�? units in which the quantity of the manufactured item is described.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getUnitOfPresentation() {
        return unitOfPresentation;
    }

    /**
     * Manufacturer of the item, one of several possible.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getManufacturer() {
        return manufacturer;
    }

    /**
     * Allows specifying that an item is on the market for sale, or that it is not available, and the dates and locations 
     * associated.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link MarketingStatus} that may be empty.
     */
    public List<MarketingStatus> getMarketingStatus() {
        return marketingStatus;
    }

    /**
     * The ingredients of this manufactured item. This is only needed if the ingredients are not specified by incoming 
     * references from the Ingredient resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getIngredient() {
        return ingredient;
    }

    /**
     * General characteristics of this item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
    }

    /**
     * Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
     * are part of its chemical makeup.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Component} that may be empty.
     */
    public List<Component> getComponent() {
        return component;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (name != null) || 
            (manufacturedDoseForm != null) || 
            (unitOfPresentation != null) || 
            !manufacturer.isEmpty() || 
            !marketingStatus.isEmpty() || 
            !ingredient.isEmpty() || 
            !property.isEmpty() || 
            !component.isEmpty();
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
                accept(name, "name", visitor);
                accept(manufacturedDoseForm, "manufacturedDoseForm", visitor);
                accept(unitOfPresentation, "unitOfPresentation", visitor);
                accept(manufacturer, "manufacturer", visitor, Reference.class);
                accept(marketingStatus, "marketingStatus", visitor, MarketingStatus.class);
                accept(ingredient, "ingredient", visitor, CodeableConcept.class);
                accept(property, "property", visitor, Property.class);
                accept(component, "component", visitor, Component.class);
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
        ManufacturedItemDefinition other = (ManufacturedItemDefinition) obj;
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
            Objects.equals(name, other.name) && 
            Objects.equals(manufacturedDoseForm, other.manufacturedDoseForm) && 
            Objects.equals(unitOfPresentation, other.unitOfPresentation) && 
            Objects.equals(manufacturer, other.manufacturer) && 
            Objects.equals(marketingStatus, other.marketingStatus) && 
            Objects.equals(ingredient, other.ingredient) && 
            Objects.equals(property, other.property) && 
            Objects.equals(component, other.component);
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
                name, 
                manufacturedDoseForm, 
                unitOfPresentation, 
                manufacturer, 
                marketingStatus, 
                ingredient, 
                property, 
                component);
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
        private PublicationStatus status;
        private String name;
        private CodeableConcept manufacturedDoseForm;
        private CodeableConcept unitOfPresentation;
        private List<Reference> manufacturer = new ArrayList<>();
        private List<MarketingStatus> marketingStatus = new ArrayList<>();
        private List<CodeableConcept> ingredient = new ArrayList<>();
        private List<Property> property = new ArrayList<>();
        private List<Component> component = new ArrayList<>();

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
         * Unique identifier.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique identifier
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
         * Unique identifier.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique identifier
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
         * The status of this item. Enables tracking the life-cycle of the content.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(PublicationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     A descriptive name applied to this item
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
         * A descriptive name applied to this item.
         * 
         * @param name
         *     A descriptive name applied to this item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Dose form as manufactured and before any transformation into the pharmaceutical product.
         * 
         * <p>This element is required.
         * 
         * @param manufacturedDoseForm
         *     Dose form as manufactured (before any necessary transformation)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufacturedDoseForm(CodeableConcept manufacturedDoseForm) {
            this.manufacturedDoseForm = manufacturedDoseForm;
            return this;
        }

        /**
         * The “real-world�? units in which the quantity of the manufactured item is described.
         * 
         * @param unitOfPresentation
         *     The “real-world�? units in which the quantity of the item is described
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder unitOfPresentation(CodeableConcept unitOfPresentation) {
            this.unitOfPresentation = unitOfPresentation;
            return this;
        }

        /**
         * Manufacturer of the item, one of several possible.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param manufacturer
         *     Manufacturer of the item, one of several possible
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufacturer(Reference... manufacturer) {
            for (Reference value : manufacturer) {
                this.manufacturer.add(value);
            }
            return this;
        }

        /**
         * Manufacturer of the item, one of several possible.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param manufacturer
         *     Manufacturer of the item, one of several possible
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder manufacturer(Collection<Reference> manufacturer) {
            this.manufacturer = new ArrayList<>(manufacturer);
            return this;
        }

        /**
         * Allows specifying that an item is on the market for sale, or that it is not available, and the dates and locations 
         * associated.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param marketingStatus
         *     Allows specifying that an item is on the market for sale, or that it is not available, and the dates and locations 
         *     associated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder marketingStatus(MarketingStatus... marketingStatus) {
            for (MarketingStatus value : marketingStatus) {
                this.marketingStatus.add(value);
            }
            return this;
        }

        /**
         * Allows specifying that an item is on the market for sale, or that it is not available, and the dates and locations 
         * associated.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param marketingStatus
         *     Allows specifying that an item is on the market for sale, or that it is not available, and the dates and locations 
         *     associated
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder marketingStatus(Collection<MarketingStatus> marketingStatus) {
            this.marketingStatus = new ArrayList<>(marketingStatus);
            return this;
        }

        /**
         * The ingredients of this manufactured item. This is only needed if the ingredients are not specified by incoming 
         * references from the Ingredient resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredient
         *     The ingredients of this manufactured item. Only needed if these are not specified by incoming references from the 
         *     Ingredient resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder ingredient(CodeableConcept... ingredient) {
            for (CodeableConcept value : ingredient) {
                this.ingredient.add(value);
            }
            return this;
        }

        /**
         * The ingredients of this manufactured item. This is only needed if the ingredients are not specified by incoming 
         * references from the Ingredient resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredient
         *     The ingredients of this manufactured item. Only needed if these are not specified by incoming references from the 
         *     Ingredient resource
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder ingredient(Collection<CodeableConcept> ingredient) {
            this.ingredient = new ArrayList<>(ingredient);
            return this;
        }

        /**
         * General characteristics of this item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     General characteristics of this item
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder property(Property... property) {
            for (Property value : property) {
                this.property.add(value);
            }
            return this;
        }

        /**
         * General characteristics of this item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     General characteristics of this item
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder property(Collection<Property> property) {
            this.property = new ArrayList<>(property);
            return this;
        }

        /**
         * Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
         * are part of its chemical makeup.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
         *     are part of its chemical makeup
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder component(Component... component) {
            for (Component value : component) {
                this.component.add(value);
            }
            return this;
        }

        /**
         * Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
         * are part of its chemical makeup.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
         *     are part of its chemical makeup
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder component(Collection<Component> component) {
            this.component = new ArrayList<>(component);
            return this;
        }

        /**
         * Build the {@link ManufacturedItemDefinition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>manufacturedDoseForm</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ManufacturedItemDefinition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ManufacturedItemDefinition per the base specification
         */
        @Override
        public ManufacturedItemDefinition build() {
            ManufacturedItemDefinition manufacturedItemDefinition = new ManufacturedItemDefinition(this);
            if (validating) {
                validate(manufacturedItemDefinition);
            }
            return manufacturedItemDefinition;
        }

        protected void validate(ManufacturedItemDefinition manufacturedItemDefinition) {
            super.validate(manufacturedItemDefinition);
            ValidationSupport.checkList(manufacturedItemDefinition.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(manufacturedItemDefinition.status, "status");
            ValidationSupport.requireNonNull(manufacturedItemDefinition.manufacturedDoseForm, "manufacturedDoseForm");
            ValidationSupport.checkList(manufacturedItemDefinition.manufacturer, "manufacturer", Reference.class);
            ValidationSupport.checkList(manufacturedItemDefinition.marketingStatus, "marketingStatus", MarketingStatus.class);
            ValidationSupport.checkList(manufacturedItemDefinition.ingredient, "ingredient", CodeableConcept.class);
            ValidationSupport.checkList(manufacturedItemDefinition.property, "property", Property.class);
            ValidationSupport.checkList(manufacturedItemDefinition.component, "component", Component.class);
            ValidationSupport.checkReferenceType(manufacturedItemDefinition.manufacturer, "manufacturer", "Organization");
        }

        protected Builder from(ManufacturedItemDefinition manufacturedItemDefinition) {
            super.from(manufacturedItemDefinition);
            identifier.addAll(manufacturedItemDefinition.identifier);
            status = manufacturedItemDefinition.status;
            name = manufacturedItemDefinition.name;
            manufacturedDoseForm = manufacturedItemDefinition.manufacturedDoseForm;
            unitOfPresentation = manufacturedItemDefinition.unitOfPresentation;
            manufacturer.addAll(manufacturedItemDefinition.manufacturer);
            marketingStatus.addAll(manufacturedItemDefinition.marketingStatus);
            ingredient.addAll(manufacturedItemDefinition.ingredient);
            property.addAll(manufacturedItemDefinition.property);
            component.addAll(manufacturedItemDefinition.component);
            return this;
        }
    }

    /**
     * General characteristics of this item.
     */
    public static class Property extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "SNOMEDCTCharacteristicCodes",
            strength = BindingStrength.Value.EXAMPLE,
            description = "This value set includes all observable entity codes from SNOMED CT - provided as an exemplar value set.",
            valueSet = "http://hl7.org/fhir/ValueSet/product-characteristic-codes"
        )
        @Required
        private final CodeableConcept type;
        @Summary
        @ReferenceTarget({ "Binary" })
        @Choice({ CodeableConcept.class, Quantity.class, Date.class, Boolean.class, Markdown.class, Attachment.class, Reference.class })
        private final Element value;

        private Property(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * A code expressing the type of characteristic.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * A value for the characteristic.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept}, {@link Quantity}, {@link Date}, {@link Boolean}, {@link 
         *     Markdown}, {@link Attachment} or {@link Reference} that may be null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
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
                    accept(type, "type", visitor);
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
            Property other = (Property) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
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
            private CodeableConcept type;
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
             * A code expressing the type of characteristic.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     A code expressing the type of characteristic
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Date.
             * 
             * @param value
             *     A value for the characteristic
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.LocalDate value) {
                this.value = (value == null) ? null : Date.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * @param value
             *     A value for the characteristic
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
             * A value for the characteristic.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Date}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Markdown}</li>
             * <li>{@link Attachment}</li>
             * <li>{@link Reference}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Binary}</li>
             * </ul>
             * 
             * @param value
             *     A value for the characteristic
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Property}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Property}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Property per the base specification
             */
            @Override
            public Property build() {
                Property property = new Property(this);
                if (validating) {
                    validate(property);
                }
                return property;
            }

            protected void validate(Property property) {
                super.validate(property);
                ValidationSupport.requireNonNull(property.type, "type");
                ValidationSupport.choiceElement(property.value, "value", CodeableConcept.class, Quantity.class, Date.class, Boolean.class, Markdown.class, Attachment.class, Reference.class);
                ValidationSupport.checkReferenceType(property.value, "value", "Binary");
                ValidationSupport.requireValueOrChildren(property);
            }

            protected Builder from(Property property) {
                super.from(property);
                type = property.type;
                value = property.value;
                return this;
            }
        }
    }

    /**
     * Physical parts of the manufactured item, that it is intrisically made from. This is distinct from the ingredients that 
     * are part of its chemical makeup.
     */
    public static class Component extends BackboneElement {
        @Summary
        @Required
        private final CodeableConcept type;
        @Summary
        private final List<CodeableConcept> function;
        @Summary
        private final List<Quantity> amount;
        @Summary
        private final List<Constituent> constituent;
        @Summary
        private final List<ManufacturedItemDefinition.Property> property;
        @Summary
        private final List<ManufacturedItemDefinition.Component> component;

        private Component(Builder builder) {
            super(builder);
            type = builder.type;
            function = Collections.unmodifiableList(builder.function);
            amount = Collections.unmodifiableList(builder.amount);
            constituent = Collections.unmodifiableList(builder.constituent);
            property = Collections.unmodifiableList(builder.property);
            component = Collections.unmodifiableList(builder.component);
        }

        /**
         * Defining type of the component e.g. shell, layer, ink.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The function of this component within the item e.g. delivers active ingredient, masks taste.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getFunction() {
            return function;
        }

        /**
         * The measurable amount of total quantity of all substances in the component, expressable in different ways (e.g. by 
         * mass or volume).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Quantity} that may be empty.
         */
        public List<Quantity> getAmount() {
            return amount;
        }

        /**
         * A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
         * the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
         * ManufacturedItemDefinition.ingredient).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Constituent} that may be empty.
         */
        public List<Constituent> getConstituent() {
            return constituent;
        }

        /**
         * General characteristics of this component.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
         */
        public List<ManufacturedItemDefinition.Property> getProperty() {
            return property;
        }

        /**
         * A component that this component contains or is made from.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Component} that may be empty.
         */
        public List<ManufacturedItemDefinition.Component> getComponent() {
            return component;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !function.isEmpty() || 
                !amount.isEmpty() || 
                !constituent.isEmpty() || 
                !property.isEmpty() || 
                !component.isEmpty();
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
                    accept(type, "type", visitor);
                    accept(function, "function", visitor, CodeableConcept.class);
                    accept(amount, "amount", visitor, Quantity.class);
                    accept(constituent, "constituent", visitor, Constituent.class);
                    accept(property, "property", visitor, ManufacturedItemDefinition.Property.class);
                    accept(component, "component", visitor, ManufacturedItemDefinition.Component.class);
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
            Component other = (Component) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(function, other.function) && 
                Objects.equals(amount, other.amount) && 
                Objects.equals(constituent, other.constituent) && 
                Objects.equals(property, other.property) && 
                Objects.equals(component, other.component);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    function, 
                    amount, 
                    constituent, 
                    property, 
                    component);
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
            private CodeableConcept type;
            private List<CodeableConcept> function = new ArrayList<>();
            private List<Quantity> amount = new ArrayList<>();
            private List<Constituent> constituent = new ArrayList<>();
            private List<ManufacturedItemDefinition.Property> property = new ArrayList<>();
            private List<ManufacturedItemDefinition.Component> component = new ArrayList<>();

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
             * Defining type of the component e.g. shell, layer, ink.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Defining type of the component e.g. shell, layer, ink
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The function of this component within the item e.g. delivers active ingredient, masks taste.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param function
             *     The function of this component within the item e.g. delivers active ingredient, masks taste
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept... function) {
                for (CodeableConcept value : function) {
                    this.function.add(value);
                }
                return this;
            }

            /**
             * The function of this component within the item e.g. delivers active ingredient, masks taste.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param function
             *     The function of this component within the item e.g. delivers active ingredient, masks taste
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder function(Collection<CodeableConcept> function) {
                this.function = new ArrayList<>(function);
                return this;
            }

            /**
             * The measurable amount of total quantity of all substances in the component, expressable in different ways (e.g. by 
             * mass or volume).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param amount
             *     The measurable amount of total quantity of all substances in the component, expressable in different ways (e.g. by 
             *     mass or volume)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(Quantity... amount) {
                for (Quantity value : amount) {
                    this.amount.add(value);
                }
                return this;
            }

            /**
             * The measurable amount of total quantity of all substances in the component, expressable in different ways (e.g. by 
             * mass or volume).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param amount
             *     The measurable amount of total quantity of all substances in the component, expressable in different ways (e.g. by 
             *     mass or volume)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder amount(Collection<Quantity> amount) {
                this.amount = new ArrayList<>(amount);
                return this;
            }

            /**
             * A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
             * the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
             * ManufacturedItemDefinition.ingredient).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param constituent
             *     A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
             *     the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
             *     ManufacturedItemDefinition.ingredient)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder constituent(Constituent... constituent) {
                for (Constituent value : constituent) {
                    this.constituent.add(value);
                }
                return this;
            }

            /**
             * A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
             * the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
             * ManufacturedItemDefinition.ingredient).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param constituent
             *     A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
             *     the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
             *     ManufacturedItemDefinition.ingredient)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder constituent(Collection<Constituent> constituent) {
                this.constituent = new ArrayList<>(constituent);
                return this;
            }

            /**
             * General characteristics of this component.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param property
             *     General characteristics of this component
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder property(ManufacturedItemDefinition.Property... property) {
                for (ManufacturedItemDefinition.Property value : property) {
                    this.property.add(value);
                }
                return this;
            }

            /**
             * General characteristics of this component.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param property
             *     General characteristics of this component
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder property(Collection<ManufacturedItemDefinition.Property> property) {
                this.property = new ArrayList<>(property);
                return this;
            }

            /**
             * A component that this component contains or is made from.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param component
             *     A component that this component contains or is made from
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder component(ManufacturedItemDefinition.Component... component) {
                for (ManufacturedItemDefinition.Component value : component) {
                    this.component.add(value);
                }
                return this;
            }

            /**
             * A component that this component contains or is made from.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param component
             *     A component that this component contains or is made from
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder component(Collection<ManufacturedItemDefinition.Component> component) {
                this.component = new ArrayList<>(component);
                return this;
            }

            /**
             * Build the {@link Component}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Component}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Component per the base specification
             */
            @Override
            public Component build() {
                Component component = new Component(this);
                if (validating) {
                    validate(component);
                }
                return component;
            }

            protected void validate(Component component) {
                super.validate(component);
                ValidationSupport.requireNonNull(component.type, "type");
                ValidationSupport.checkList(component.function, "function", CodeableConcept.class);
                ValidationSupport.checkList(component.amount, "amount", Quantity.class);
                ValidationSupport.checkList(component.constituent, "constituent", Constituent.class);
                ValidationSupport.checkList(component.property, "property", ManufacturedItemDefinition.Property.class);
                ValidationSupport.checkList(component.component, "component", ManufacturedItemDefinition.Component.class);
                ValidationSupport.requireValueOrChildren(component);
            }

            protected Builder from(Component component) {
                super.from(component);
                type = component.type;
                function.addAll(component.function);
                amount.addAll(component.amount);
                constituent.addAll(component.constituent);
                property.addAll(component.property);
                this.component.addAll(component.component);
                return this;
            }
        }

        /**
         * A reference to a constituent of the manufactured item as a whole, linked here so that its component location within 
         * the item can be indicated. This not where the item's ingredient are primarily stated (for which see Ingredient.for or 
         * ManufacturedItemDefinition.ingredient).
         */
        public static class Constituent extends BackboneElement {
            @Summary
            private final List<Quantity> amount;
            @Summary
            private final List<CodeableConcept> location;
            @Summary
            private final List<CodeableConcept> function;
            @Summary
            private final List<CodeableReference> hasIngredient;

            private Constituent(Builder builder) {
                super(builder);
                amount = Collections.unmodifiableList(builder.amount);
                location = Collections.unmodifiableList(builder.location);
                function = Collections.unmodifiableList(builder.function);
                hasIngredient = Collections.unmodifiableList(builder.hasIngredient);
            }

            /**
             * The measurable amount of the substance, expressable in different ways (e.g. by mass or volume).
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Quantity} that may be empty.
             */
            public List<Quantity> getAmount() {
                return amount;
            }

            /**
             * The physical location of the constituent/ingredient within the component. Example – if the component is the bead in 
             * the capsule, then the location would be where the ingredient resides within the product part – intragranular, extra-
             * granular, etc.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getLocation() {
                return location;
            }

            /**
             * The function of this constituent within the component e.g. binder.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getFunction() {
                return function;
            }

            /**
             * The ingredient that is the constituent of the given component.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
             */
            public List<CodeableReference> getHasIngredient() {
                return hasIngredient;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !amount.isEmpty() || 
                    !location.isEmpty() || 
                    !function.isEmpty() || 
                    !hasIngredient.isEmpty();
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
                        accept(amount, "amount", visitor, Quantity.class);
                        accept(location, "location", visitor, CodeableConcept.class);
                        accept(function, "function", visitor, CodeableConcept.class);
                        accept(hasIngredient, "hasIngredient", visitor, CodeableReference.class);
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
                Constituent other = (Constituent) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(amount, other.amount) && 
                    Objects.equals(location, other.location) && 
                    Objects.equals(function, other.function) && 
                    Objects.equals(hasIngredient, other.hasIngredient);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        amount, 
                        location, 
                        function, 
                        hasIngredient);
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
                private List<Quantity> amount = new ArrayList<>();
                private List<CodeableConcept> location = new ArrayList<>();
                private List<CodeableConcept> function = new ArrayList<>();
                private List<CodeableReference> hasIngredient = new ArrayList<>();

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
                 * The measurable amount of the substance, expressable in different ways (e.g. by mass or volume).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param amount
                 *     The measurable amount of the substance, expressable in different ways (e.g. by mass or volume)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder amount(Quantity... amount) {
                    for (Quantity value : amount) {
                        this.amount.add(value);
                    }
                    return this;
                }

                /**
                 * The measurable amount of the substance, expressable in different ways (e.g. by mass or volume).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param amount
                 *     The measurable amount of the substance, expressable in different ways (e.g. by mass or volume)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder amount(Collection<Quantity> amount) {
                    this.amount = new ArrayList<>(amount);
                    return this;
                }

                /**
                 * The physical location of the constituent/ingredient within the component. Example – if the component is the bead in 
                 * the capsule, then the location would be where the ingredient resides within the product part – intragranular, extra-
                 * granular, etc.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param location
                 *     The physical location of the constituent/ingredient within the component
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder location(CodeableConcept... location) {
                    for (CodeableConcept value : location) {
                        this.location.add(value);
                    }
                    return this;
                }

                /**
                 * The physical location of the constituent/ingredient within the component. Example – if the component is the bead in 
                 * the capsule, then the location would be where the ingredient resides within the product part – intragranular, extra-
                 * granular, etc.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param location
                 *     The physical location of the constituent/ingredient within the component
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder location(Collection<CodeableConcept> location) {
                    this.location = new ArrayList<>(location);
                    return this;
                }

                /**
                 * The function of this constituent within the component e.g. binder.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param function
                 *     The function of this constituent within the component e.g. binder
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder function(CodeableConcept... function) {
                    for (CodeableConcept value : function) {
                        this.function.add(value);
                    }
                    return this;
                }

                /**
                 * The function of this constituent within the component e.g. binder.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param function
                 *     The function of this constituent within the component e.g. binder
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder function(Collection<CodeableConcept> function) {
                    this.function = new ArrayList<>(function);
                    return this;
                }

                /**
                 * The ingredient that is the constituent of the given component.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param hasIngredient
                 *     The ingredient that is the constituent of the given component
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder hasIngredient(CodeableReference... hasIngredient) {
                    for (CodeableReference value : hasIngredient) {
                        this.hasIngredient.add(value);
                    }
                    return this;
                }

                /**
                 * The ingredient that is the constituent of the given component.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param hasIngredient
                 *     The ingredient that is the constituent of the given component
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder hasIngredient(Collection<CodeableReference> hasIngredient) {
                    this.hasIngredient = new ArrayList<>(hasIngredient);
                    return this;
                }

                /**
                 * Build the {@link Constituent}
                 * 
                 * @return
                 *     An immutable object of type {@link Constituent}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Constituent per the base specification
                 */
                @Override
                public Constituent build() {
                    Constituent constituent = new Constituent(this);
                    if (validating) {
                        validate(constituent);
                    }
                    return constituent;
                }

                protected void validate(Constituent constituent) {
                    super.validate(constituent);
                    ValidationSupport.checkList(constituent.amount, "amount", Quantity.class);
                    ValidationSupport.checkList(constituent.location, "location", CodeableConcept.class);
                    ValidationSupport.checkList(constituent.function, "function", CodeableConcept.class);
                    ValidationSupport.checkList(constituent.hasIngredient, "hasIngredient", CodeableReference.class);
                    ValidationSupport.requireValueOrChildren(constituent);
                }

                protected Builder from(Constituent constituent) {
                    super.from(constituent);
                    amount.addAll(constituent.amount);
                    location.addAll(constituent.location);
                    function.addAll(constituent.function);
                    hasIngredient.addAll(constituent.hasIngredient);
                    return this;
                }
            }
        }
    }
}
