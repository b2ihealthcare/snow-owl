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

@System("http://hl7.org/fhir/search-comparator")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ValueFilterComparator extends Code {
    /**
     * Equals
     */
    public static final ValueFilterComparator EQ = ValueFilterComparator.builder().value(Value.EQ).build();

    /**
     * Greater Than
     */
    public static final ValueFilterComparator GT = ValueFilterComparator.builder().value(Value.GT).build();

    /**
     * Less Than
     */
    public static final ValueFilterComparator LT = ValueFilterComparator.builder().value(Value.LT).build();

    /**
     * Greater or Equals
     */
    public static final ValueFilterComparator GE = ValueFilterComparator.builder().value(Value.GE).build();

    /**
     * Less of Equal
     */
    public static final ValueFilterComparator LE = ValueFilterComparator.builder().value(Value.LE).build();

    /**
     * Starts After
     */
    public static final ValueFilterComparator SA = ValueFilterComparator.builder().value(Value.SA).build();

    /**
     * Ends Before
     */
    public static final ValueFilterComparator EB = ValueFilterComparator.builder().value(Value.EB).build();

    private volatile int hashCode;

    private ValueFilterComparator(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ValueFilterComparator as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ValueFilterComparator objects from a passed enum value.
     */
    public static ValueFilterComparator of(Value value) {
        switch (value) {
        case EQ:
            return EQ;
        case GT:
            return GT;
        case LT:
            return LT;
        case GE:
            return GE;
        case LE:
            return LE;
        case SA:
            return SA;
        case EB:
            return EB;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ValueFilterComparator objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ValueFilterComparator of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ValueFilterComparator objects from a passed string value.
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
     * Inherited factory method for creating ValueFilterComparator objects from a passed string value.
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
        ValueFilterComparator other = (ValueFilterComparator) obj;
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
         *     An enum constant for ValueFilterComparator
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ValueFilterComparator build() {
            ValueFilterComparator valueFilterComparator = new ValueFilterComparator(this);
            if (validating) {
                validate(valueFilterComparator);
            }
            return valueFilterComparator;
        }

        protected void validate(ValueFilterComparator valueFilterComparator) {
            super.validate(valueFilterComparator);
        }

        protected Builder from(ValueFilterComparator valueFilterComparator) {
            super.from(valueFilterComparator);
            return this;
        }
    }

    public enum Value {
        /**
         * Equals
         */
        EQ("eq"),

        /**
         * Greater Than
         */
        GT("gt"),

        /**
         * Less Than
         */
        LT("lt"),

        /**
         * Greater or Equals
         */
        GE("ge"),

        /**
         * Less of Equal
         */
        LE("le"),

        /**
         * Starts After
         */
        SA("sa"),

        /**
         * Ends Before
         */
        EB("eb");

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
         * Factory method for creating ValueFilterComparator.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ValueFilterComparator.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "eq":
                return EQ;
            case "gt":
                return GT;
            case "lt":
                return LT;
            case "ge":
                return GE;
            case "le":
                return LE;
            case "sa":
                return SA;
            case "eb":
                return EB;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
