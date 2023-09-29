/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PriceComponentType;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Availability data for an {item}.
 */
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MonetaryComponent extends DataType {
    @Summary
    @Binding(
        bindingName = "PriceComponentType",
        strength = BindingStrength.Value.REQUIRED,
        description = "The purpose for which an extended contact detail should be used.",
        valueSet = "http://hl7.org/fhir/ValueSet/price-component-type|5.0.0"
    )
    @Required
    private final PriceComponentType type;
    @Summary
    @Binding(
        bindingName = "PriceComponentCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes may be used to differentiate between kinds of taxes, surcharges, discounts etc."
    )
    private final CodeableConcept code;
    @Summary
    private final Decimal factor;
    @Summary
    private final Money amount;

    private MonetaryComponent(Builder builder) {
        super(builder);
        type = builder.type;
        code = builder.code;
        factor = builder.factor;
        amount = builder.amount;
    }

    /**
     * base | surcharge | deduction | discount | tax | informational.
     * 
     * @return
     *     An immutable object of type {@link PriceComponentType} that is non-null.
     */
    public PriceComponentType getType() {
        return type;
    }

    /**
     * Codes may be used to differentiate between kinds of taxes, surcharges, discounts etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * Factor used for calculating this component.
     * 
     * @return
     *     An immutable object of type {@link Decimal} that may be null.
     */
    public Decimal getFactor() {
        return factor;
    }

    /**
     * Explicit value amount to be used.
     * 
     * @return
     *     An immutable object of type {@link Money} that may be null.
     */
    public Money getAmount() {
        return amount;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (type != null) || 
            (code != null) || 
            (factor != null) || 
            (amount != null);
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(type, "type", visitor);
                accept(code, "code", visitor);
                accept(factor, "factor", visitor);
                accept(amount, "amount", visitor);
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
        MonetaryComponent other = (MonetaryComponent) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(type, other.type) && 
            Objects.equals(code, other.code) && 
            Objects.equals(factor, other.factor) && 
            Objects.equals(amount, other.amount);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                type, 
                code, 
                factor, 
                amount);
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

    public static class Builder extends DataType.Builder {
        private PriceComponentType type;
        private CodeableConcept code;
        private Decimal factor;
        private Money amount;

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
         * base | surcharge | deduction | discount | tax | informational.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     base | surcharge | deduction | discount | tax | informational
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(PriceComponentType type) {
            this.type = type;
            return this;
        }

        /**
         * Codes may be used to differentiate between kinds of taxes, surcharges, discounts etc.
         * 
         * @param code
         *     Codes may be used to differentiate between kinds of taxes, surcharges, discounts etc.
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * Factor used for calculating this component.
         * 
         * @param factor
         *     Factor used for calculating this component
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder factor(Decimal factor) {
            this.factor = factor;
            return this;
        }

        /**
         * Explicit value amount to be used.
         * 
         * @param amount
         *     Explicit value amount to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Build the {@link MonetaryComponent}
         * 
         * <p>Required elements:
         * <ul>
         * <li>type</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link MonetaryComponent}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MonetaryComponent per the base specification
         */
        @Override
        public MonetaryComponent build() {
            MonetaryComponent monetaryComponent = new MonetaryComponent(this);
            if (validating) {
                validate(monetaryComponent);
            }
            return monetaryComponent;
        }

        protected void validate(MonetaryComponent monetaryComponent) {
            super.validate(monetaryComponent);
            ValidationSupport.requireNonNull(monetaryComponent.type, "type");
            ValidationSupport.requireValueOrChildren(monetaryComponent);
        }

        protected Builder from(MonetaryComponent monetaryComponent) {
            super.from(monetaryComponent);
            type = monetaryComponent.type;
            code = monetaryComponent.code;
            factor = monetaryComponent.factor;
            amount = monetaryComponent.amount;
            return this;
        }
    }
}
