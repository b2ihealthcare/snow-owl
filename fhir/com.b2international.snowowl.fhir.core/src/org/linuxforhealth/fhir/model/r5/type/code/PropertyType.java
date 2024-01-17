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

@System("http://hl7.org/fhir/conceptmap-property-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PropertyType extends Code {
    /**
     * Coding (external reference)
     * 
     * <p>The property value is a code defined in an external code system. This may be used for translations, but is not the 
     * intent.
     */
    public static final PropertyType CODING = PropertyType.builder().value(Value.CODING).build();

    /**
     * string
     * 
     * <p>The property value is a string.
     */
    public static final PropertyType STRING = PropertyType.builder().value(Value.STRING).build();

    /**
     * integer
     * 
     * <p>The property value is an integer (often used to assign ranking values to concepts for supporting score assessments).
     */
    public static final PropertyType INTEGER = PropertyType.builder().value(Value.INTEGER).build();

    /**
     * boolean
     * 
     * <p>The property value is a boolean true | false.
     */
    public static final PropertyType BOOLEAN = PropertyType.builder().value(Value.BOOLEAN).build();

    /**
     * dateTime
     * 
     * <p>The property is a date or a date + time.
     */
    public static final PropertyType DATE_TIME = PropertyType.builder().value(Value.DATE_TIME).build();

    /**
     * decimal
     * 
     * <p>The property value is a decimal number.
     */
    public static final PropertyType DECIMAL = PropertyType.builder().value(Value.DECIMAL).build();

    /**
     * code
     * 
     * <p>The property value is a code as defined in the CodeSystem in ConceptMap.property.system.
     */
    public static final PropertyType CODE = PropertyType.builder().value(Value.CODE).build();

    private volatile int hashCode;

    private PropertyType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this PropertyType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating PropertyType objects from a passed enum value.
     */
    public static PropertyType of(Value value) {
        switch (value) {
        case CODING:
            return CODING;
        case STRING:
            return STRING;
        case INTEGER:
            return INTEGER;
        case BOOLEAN:
            return BOOLEAN;
        case DATE_TIME:
            return DATE_TIME;
        case DECIMAL:
            return DECIMAL;
        case CODE:
            return CODE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating PropertyType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static PropertyType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating PropertyType objects from a passed string value.
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
     * Inherited factory method for creating PropertyType objects from a passed string value.
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
        PropertyType other = (PropertyType) obj;
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
         *     An enum constant for PropertyType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public PropertyType build() {
            PropertyType propertyType = new PropertyType(this);
            if (validating) {
                validate(propertyType);
            }
            return propertyType;
        }

        protected void validate(PropertyType propertyType) {
            super.validate(propertyType);
        }

        protected Builder from(PropertyType propertyType) {
            super.from(propertyType);
            return this;
        }
    }

    public enum Value {
        /**
         * Coding (external reference)
         * 
         * <p>The property value is a code defined in an external code system. This may be used for translations, but is not the 
         * intent.
         */
        CODING("Coding"),

        /**
         * string
         * 
         * <p>The property value is a string.
         */
        STRING("string"),

        /**
         * integer
         * 
         * <p>The property value is an integer (often used to assign ranking values to concepts for supporting score assessments).
         */
        INTEGER("integer"),

        /**
         * boolean
         * 
         * <p>The property value is a boolean true | false.
         */
        BOOLEAN("boolean"),

        /**
         * dateTime
         * 
         * <p>The property is a date or a date + time.
         */
        DATE_TIME("dateTime"),

        /**
         * decimal
         * 
         * <p>The property value is a decimal number.
         */
        DECIMAL("decimal"),

        /**
         * code
         * 
         * <p>The property value is a code as defined in the CodeSystem in ConceptMap.property.system.
         */
        CODE("code");

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
         * Factory method for creating PropertyType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding PropertyType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "Coding":
                return CODING;
            case "string":
                return STRING;
            case "integer":
                return INTEGER;
            case "boolean":
                return BOOLEAN;
            case "dateTime":
                return DATE_TIME;
            case "decimal":
                return DECIMAL;
            case "code":
                return CODE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
