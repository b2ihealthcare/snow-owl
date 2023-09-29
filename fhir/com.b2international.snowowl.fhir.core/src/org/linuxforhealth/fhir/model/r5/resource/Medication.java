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
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource is primarily used for the identification and definition of a medication, including ingredients, for the 
 * purposes of prescribing, dispensing, and administering a medication as well as for making statements about medication 
 * use.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "medication-0",
    level = "Warning",
    location = "ingredient.strength",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/medication-ingredientstrength",
    expression = "$this.as(Ratio).memberOf('http://hl7.org/fhir/ValueSet/medication-ingredientstrength', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Medication",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Medication extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "MedicationFormalRepresentation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept that defines the type of a medication.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    private final CodeableConcept code;
    @Summary
    @Binding(
        bindingName = "MedicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept defining if the medication is in active use.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-status|5.0.0"
    )
    private final MedicationStatus status;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference marketingAuthorizationHolder;
    @Binding(
        bindingName = "MedicationForm",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept defining the form of a medication.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-form-codes"
    )
    private final CodeableConcept doseForm;
    @Summary
    private final Quantity totalVolume;
    private final List<Ingredient> ingredient;
    private final Batch batch;
    @ReferenceTarget({ "MedicationKnowledge" })
    private final Reference definition;

    private Medication(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        code = builder.code;
        status = builder.status;
        marketingAuthorizationHolder = builder.marketingAuthorizationHolder;
        doseForm = builder.doseForm;
        totalVolume = builder.totalVolume;
        ingredient = Collections.unmodifiableList(builder.ingredient);
        batch = builder.batch;
        definition = builder.definition;
    }

    /**
     * Business identifier for this medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A code (or set of codes) that specify this medication, or a textual description if no code is available. Usage note: 
     * This could be a standard medication code such as a code from RxNorm, SNOMED CT, IDMP etc. It could also be a national 
     * or local formulary code, optionally with translations to other code systems.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * A code to indicate if the medication is in active use.
     * 
     * @return
     *     An immutable object of type {@link MedicationStatus} that may be null.
     */
    public MedicationStatus getStatus() {
        return status;
    }

    /**
     * The company or other legal entity that has authorization, from the appropriate drug regulatory authority, to market a 
     * medicine in one or more jurisdictions. Typically abbreviated MAH.Note: The MAH may manufacture the product and may 
     * also contract the manufacturing of the product to one or more companies (organizations).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getMarketingAuthorizationHolder() {
        return marketingAuthorizationHolder;
    }

    /**
     * Describes the form of the item. Powder; tablets; capsule.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getDoseForm() {
        return doseForm;
    }

    /**
     * When the specified product code does not infer a package size, this is the specific amount of drug in the product. For 
     * example, when specifying a product that has the same strength (For example, Insulin glargine 100 unit per mL solution 
     * for injection), this attribute provides additional clarification of the package amount (For example, 3 mL, 10mL, etc.).
     * 
     * @return
     *     An immutable object of type {@link Quantity} that may be null.
     */
    public Quantity getTotalVolume() {
        return totalVolume;
    }

    /**
     * Identifies a particular constituent of interest in the product.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Ingredient} that may be empty.
     */
    public List<Ingredient> getIngredient() {
        return ingredient;
    }

    /**
     * Information that only applies to packages (not products).
     * 
     * @return
     *     An immutable object of type {@link Batch} that may be null.
     */
    public Batch getBatch() {
        return batch;
    }

    /**
     * A reference to a knowledge resource that provides more information about this medication.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDefinition() {
        return definition;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (code != null) || 
            (status != null) || 
            (marketingAuthorizationHolder != null) || 
            (doseForm != null) || 
            (totalVolume != null) || 
            !ingredient.isEmpty() || 
            (batch != null) || 
            (definition != null);
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
                accept(code, "code", visitor);
                accept(status, "status", visitor);
                accept(marketingAuthorizationHolder, "marketingAuthorizationHolder", visitor);
                accept(doseForm, "doseForm", visitor);
                accept(totalVolume, "totalVolume", visitor);
                accept(ingredient, "ingredient", visitor, Ingredient.class);
                accept(batch, "batch", visitor);
                accept(definition, "definition", visitor);
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
        Medication other = (Medication) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(code, other.code) && 
            Objects.equals(status, other.status) && 
            Objects.equals(marketingAuthorizationHolder, other.marketingAuthorizationHolder) && 
            Objects.equals(doseForm, other.doseForm) && 
            Objects.equals(totalVolume, other.totalVolume) && 
            Objects.equals(ingredient, other.ingredient) && 
            Objects.equals(batch, other.batch) && 
            Objects.equals(definition, other.definition);
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
                code, 
                status, 
                marketingAuthorizationHolder, 
                doseForm, 
                totalVolume, 
                ingredient, 
                batch, 
                definition);
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
        private CodeableConcept code;
        private MedicationStatus status;
        private Reference marketingAuthorizationHolder;
        private CodeableConcept doseForm;
        private Quantity totalVolume;
        private List<Ingredient> ingredient = new ArrayList<>();
        private Batch batch;
        private Reference definition;

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
         * Business identifier for this medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this medication
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
         * Business identifier for this medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this medication
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
         * A code (or set of codes) that specify this medication, or a textual description if no code is available. Usage note: 
         * This could be a standard medication code such as a code from RxNorm, SNOMED CT, IDMP etc. It could also be a national 
         * or local formulary code, optionally with translations to other code systems.
         * 
         * @param code
         *     Codes that identify this medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * A code to indicate if the medication is in active use.
         * 
         * @param status
         *     active | inactive | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MedicationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The company or other legal entity that has authorization, from the appropriate drug regulatory authority, to market a 
         * medicine in one or more jurisdictions. Typically abbreviated MAH.Note: The MAH may manufacture the product and may 
         * also contract the manufacturing of the product to one or more companies (organizations).
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param marketingAuthorizationHolder
         *     Organization that has authorization to market medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder marketingAuthorizationHolder(Reference marketingAuthorizationHolder) {
            this.marketingAuthorizationHolder = marketingAuthorizationHolder;
            return this;
        }

        /**
         * Describes the form of the item. Powder; tablets; capsule.
         * 
         * @param doseForm
         *     powder | tablets | capsule +
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doseForm(CodeableConcept doseForm) {
            this.doseForm = doseForm;
            return this;
        }

        /**
         * When the specified product code does not infer a package size, this is the specific amount of drug in the product. For 
         * example, when specifying a product that has the same strength (For example, Insulin glargine 100 unit per mL solution 
         * for injection), this attribute provides additional clarification of the package amount (For example, 3 mL, 10mL, etc.).
         * 
         * @param totalVolume
         *     When the specified product code does not infer a package size, this is the specific amount of drug in the product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder totalVolume(Quantity totalVolume) {
            this.totalVolume = totalVolume;
            return this;
        }

        /**
         * Identifies a particular constituent of interest in the product.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredient
         *     Active or inactive ingredient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder ingredient(Ingredient... ingredient) {
            for (Ingredient value : ingredient) {
                this.ingredient.add(value);
            }
            return this;
        }

        /**
         * Identifies a particular constituent of interest in the product.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredient
         *     Active or inactive ingredient
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder ingredient(Collection<Ingredient> ingredient) {
            this.ingredient = new ArrayList<>(ingredient);
            return this;
        }

        /**
         * Information that only applies to packages (not products).
         * 
         * @param batch
         *     Details about packaged medications
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder batch(Batch batch) {
            this.batch = batch;
            return this;
        }

        /**
         * A reference to a knowledge resource that provides more information about this medication.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link MedicationKnowledge}</li>
         * </ul>
         * 
         * @param definition
         *     Knowledge about this medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder definition(Reference definition) {
            this.definition = definition;
            return this;
        }

        /**
         * Build the {@link Medication}
         * 
         * @return
         *     An immutable object of type {@link Medication}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Medication per the base specification
         */
        @Override
        public Medication build() {
            Medication medication = new Medication(this);
            if (validating) {
                validate(medication);
            }
            return medication;
        }

        protected void validate(Medication medication) {
            super.validate(medication);
            ValidationSupport.checkList(medication.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(medication.ingredient, "ingredient", Ingredient.class);
            ValidationSupport.checkReferenceType(medication.marketingAuthorizationHolder, "marketingAuthorizationHolder", "Organization");
            ValidationSupport.checkReferenceType(medication.definition, "definition", "MedicationKnowledge");
        }

        protected Builder from(Medication medication) {
            super.from(medication);
            identifier.addAll(medication.identifier);
            code = medication.code;
            status = medication.status;
            marketingAuthorizationHolder = medication.marketingAuthorizationHolder;
            doseForm = medication.doseForm;
            totalVolume = medication.totalVolume;
            ingredient.addAll(medication.ingredient);
            batch = medication.batch;
            definition = medication.definition;
            return this;
        }
    }

    /**
     * Identifies a particular constituent of interest in the product.
     */
    public static class Ingredient extends BackboneElement {
        @Binding(
            bindingName = "MedicationFormalRepresentation",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
        )
        @Required
        private final CodeableReference item;
        private final Boolean isActive;
        @Choice({ Ratio.class, CodeableConcept.class, Quantity.class })
        @Binding(
            bindingName = "MedicationIngredientStrength",
            strength = BindingStrength.Value.PREFERRED,
            description = "A coded concpet defining the strength of an ingredient.",
            valueSet = "http://hl7.org/fhir/ValueSet/medication-ingredientstrength"
        )
        private final Element strength;

        private Ingredient(Builder builder) {
            super(builder);
            item = builder.item;
            isActive = builder.isActive;
            strength = builder.strength;
        }

        /**
         * The ingredient (substance or medication) that the ingredient.strength relates to. This is represented as a concept 
         * from a code system or described in another resource (Substance or Medication).
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getItem() {
            return item;
        }

        /**
         * Indication of whether this ingredient affects the therapeutic action of the drug.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getIsActive() {
            return isActive;
        }

        /**
         * Specifies how many (or how much) of the items there are in this Medication. For example, 250 mg per tablet. This is 
         * expressed as a ratio where the numerator is 250mg and the denominator is 1 tablet but can also be expressed a quantity 
         * when the denominator is assumed to be 1 tablet.
         * 
         * @return
         *     An immutable object of type {@link Ratio}, {@link CodeableConcept} or {@link Quantity} that may be null.
         */
        public Element getStrength() {
            return strength;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (item != null) || 
                (isActive != null) || 
                (strength != null);
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
                    accept(item, "item", visitor);
                    accept(isActive, "isActive", visitor);
                    accept(strength, "strength", visitor);
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
            Ingredient other = (Ingredient) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(item, other.item) && 
                Objects.equals(isActive, other.isActive) && 
                Objects.equals(strength, other.strength);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    item, 
                    isActive, 
                    strength);
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
            private CodeableReference item;
            private Boolean isActive;
            private Element strength;

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
             * The ingredient (substance or medication) that the ingredient.strength relates to. This is represented as a concept 
             * from a code system or described in another resource (Substance or Medication).
             * 
             * <p>This element is required.
             * 
             * @param item
             *     The ingredient (substance or medication) that the ingredient.strength relates to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder item(CodeableReference item) {
                this.item = item;
                return this;
            }

            /**
             * Convenience method for setting {@code isActive}.
             * 
             * @param isActive
             *     Active ingredient indicator
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #isActive(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder isActive(java.lang.Boolean isActive) {
                this.isActive = (isActive == null) ? null : Boolean.of(isActive);
                return this;
            }

            /**
             * Indication of whether this ingredient affects the therapeutic action of the drug.
             * 
             * @param isActive
             *     Active ingredient indicator
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder isActive(Boolean isActive) {
                this.isActive = isActive;
                return this;
            }

            /**
             * Specifies how many (or how much) of the items there are in this Medication. For example, 250 mg per tablet. This is 
             * expressed as a ratio where the numerator is 250mg and the denominator is 1 tablet but can also be expressed a quantity 
             * when the denominator is assumed to be 1 tablet.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Ratio}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Quantity}</li>
             * </ul>
             * 
             * @param strength
             *     Quantity of ingredient present
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder strength(Element strength) {
                this.strength = strength;
                return this;
            }

            /**
             * Build the {@link Ingredient}
             * 
             * <p>Required elements:
             * <ul>
             * <li>item</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Ingredient}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Ingredient per the base specification
             */
            @Override
            public Ingredient build() {
                Ingredient ingredient = new Ingredient(this);
                if (validating) {
                    validate(ingredient);
                }
                return ingredient;
            }

            protected void validate(Ingredient ingredient) {
                super.validate(ingredient);
                ValidationSupport.requireNonNull(ingredient.item, "item");
                ValidationSupport.choiceElement(ingredient.strength, "strength", Ratio.class, CodeableConcept.class, Quantity.class);
                ValidationSupport.requireValueOrChildren(ingredient);
            }

            protected Builder from(Ingredient ingredient) {
                super.from(ingredient);
                item = ingredient.item;
                isActive = ingredient.isActive;
                strength = ingredient.strength;
                return this;
            }
        }
    }

    /**
     * Information that only applies to packages (not products).
     */
    public static class Batch extends BackboneElement {
        private final String lotNumber;
        private final DateTime expirationDate;

        private Batch(Builder builder) {
            super(builder);
            lotNumber = builder.lotNumber;
            expirationDate = builder.expirationDate;
        }

        /**
         * The assigned lot number of a batch of the specified product.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLotNumber() {
            return lotNumber;
        }

        /**
         * When this specific batch of product will expire.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getExpirationDate() {
            return expirationDate;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (lotNumber != null) || 
                (expirationDate != null);
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
                    accept(lotNumber, "lotNumber", visitor);
                    accept(expirationDate, "expirationDate", visitor);
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
            Batch other = (Batch) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(lotNumber, other.lotNumber) && 
                Objects.equals(expirationDate, other.expirationDate);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    lotNumber, 
                    expirationDate);
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
            private String lotNumber;
            private DateTime expirationDate;

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
             * Convenience method for setting {@code lotNumber}.
             * 
             * @param lotNumber
             *     Identifier assigned to batch
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
             * The assigned lot number of a batch of the specified product.
             * 
             * @param lotNumber
             *     Identifier assigned to batch
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder lotNumber(String lotNumber) {
                this.lotNumber = lotNumber;
                return this;
            }

            /**
             * When this specific batch of product will expire.
             * 
             * @param expirationDate
             *     When batch will expire
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expirationDate(DateTime expirationDate) {
                this.expirationDate = expirationDate;
                return this;
            }

            /**
             * Build the {@link Batch}
             * 
             * @return
             *     An immutable object of type {@link Batch}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Batch per the base specification
             */
            @Override
            public Batch build() {
                Batch batch = new Batch(this);
                if (validating) {
                    validate(batch);
                }
                return batch;
            }

            protected void validate(Batch batch) {
                super.validate(batch);
                ValidationSupport.requireValueOrChildren(batch);
            }

            protected Builder from(Batch batch) {
                super.from(batch);
                lotNumber = batch.lotNumber;
                expirationDate = batch.expirationDate;
                return this;
            }
        }
    }
}
