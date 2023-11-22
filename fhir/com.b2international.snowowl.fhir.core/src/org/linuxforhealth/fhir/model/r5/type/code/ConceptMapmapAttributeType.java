/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/conceptmap-attribute-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ConceptMapmapAttributeType extends Code {
    /**
     * code
     * 
     * <p>The attribute value is a code defined in the code system in context.
     */
    public static final ConceptMapmapAttributeType CODE = ConceptMapmapAttributeType.builder().value(Value.CODE).build();

    /**
     * Coding
     * 
     * <p>The attribute value is a code defined in a code system.
     */
    public static final ConceptMapmapAttributeType CODING = ConceptMapmapAttributeType.builder().value(Value.CODING).build();

    /**
     * string
     * 
     * <p>The attribute value is a string.
     */
    public static final ConceptMapmapAttributeType STRING = ConceptMapmapAttributeType.builder().value(Value.STRING).build();

    /**
     * boolean
     * 
     * <p>The attribute value is a boolean true | false.
     */
    public static final ConceptMapmapAttributeType BOOLEAN = ConceptMapmapAttributeType.builder().value(Value.BOOLEAN).build();

    /**
     * Quantity
     * 
     * <p>The attribute is a Quantity (may represent an integer or a decimal with no units).
     */
    public static final ConceptMapmapAttributeType QUANTITY = ConceptMapmapAttributeType.builder().value(Value.QUANTITY).build();

    private volatile int hashCode;

    private ConceptMapmapAttributeType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ConceptMapmapAttributeType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ConceptMapmapAttributeType objects from a passed enum value.
     */
    public static ConceptMapmapAttributeType of(Value value) {
        switch (value) {
        case CODE:
            return CODE;
        case CODING:
            return CODING;
        case STRING:
            return STRING;
        case BOOLEAN:
            return BOOLEAN;
        case QUANTITY:
            return QUANTITY;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ConceptMapmapAttributeType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ConceptMapmapAttributeType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ConceptMapmapAttributeType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ConceptMapmapAttributeType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
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
        ConceptMapmapAttributeType other = (ConceptMapmapAttributeType) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for ConceptMapmapAttributeType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ConceptMapmapAttributeType build() {
            ConceptMapmapAttributeType conceptMapmapAttributeType = new ConceptMapmapAttributeType(this);
            if (validating) {
                validate(conceptMapmapAttributeType);
            }
            return conceptMapmapAttributeType;
        }

        protected void validate(ConceptMapmapAttributeType conceptMapmapAttributeType) {
            super.validate(conceptMapmapAttributeType);
        }

        protected Builder from(ConceptMapmapAttributeType conceptMapmapAttributeType) {
            super.from(conceptMapmapAttributeType);
            return this;
        }
    }

    public enum Value {
        /**
         * code
         * 
         * <p>The attribute value is a code defined in the code system in context.
         */
        CODE("code"),

        /**
         * Coding
         * 
         * <p>The attribute value is a code defined in a code system.
         */
        CODING("Coding"),

        /**
         * string
         * 
         * <p>The attribute value is a string.
         */
        STRING("string"),

        /**
         * boolean
         * 
         * <p>The attribute value is a boolean true | false.
         */
        BOOLEAN("boolean"),

        /**
         * Quantity
         * 
         * <p>The attribute is a Quantity (may represent an integer or a decimal with no units).
         */
        QUANTITY("Quantity");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating ConceptMapmapAttributeType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ConceptMapmapAttributeType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "code":
                return CODE;
            case "Coding":
                return CODING;
            case "string":
                return STRING;
            case "boolean":
                return BOOLEAN;
            case "Quantity":
                return QUANTITY;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
