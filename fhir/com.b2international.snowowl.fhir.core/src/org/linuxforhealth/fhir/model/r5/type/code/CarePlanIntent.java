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

@System("http://hl7.org/fhir/request-intent")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CarePlanIntent extends Code {
    public static final CarePlanIntent PROPOSAL = CarePlanIntent.builder().value(Value.PROPOSAL).build();

    public static final CarePlanIntent PLAN = CarePlanIntent.builder().value(Value.PLAN).build();

    public static final CarePlanIntent ORDER = CarePlanIntent.builder().value(Value.ORDER).build();

    public static final CarePlanIntent OPTION = CarePlanIntent.builder().value(Value.OPTION).build();

    public static final CarePlanIntent DIRECTIVE = CarePlanIntent.builder().value(Value.DIRECTIVE).build();

    private volatile int hashCode;

    private CarePlanIntent(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this CarePlanIntent as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating CarePlanIntent objects from a passed enum value.
     */
    public static CarePlanIntent of(Value value) {
        switch (value) {
        case PROPOSAL:
            return PROPOSAL;
        case PLAN:
            return PLAN;
        case ORDER:
            return ORDER;
        case OPTION:
            return OPTION;
        case DIRECTIVE:
            return DIRECTIVE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating CarePlanIntent objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static CarePlanIntent of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating CarePlanIntent objects from a passed string value.
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
     * Inherited factory method for creating CarePlanIntent objects from a passed string value.
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
        CarePlanIntent other = (CarePlanIntent) obj;
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
         *     An enum constant for CarePlanIntent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public CarePlanIntent build() {
            CarePlanIntent carePlanIntent = new CarePlanIntent(this);
            if (validating) {
                validate(carePlanIntent);
            }
            return carePlanIntent;
        }

        protected void validate(CarePlanIntent carePlanIntent) {
            super.validate(carePlanIntent);
        }

        protected Builder from(CarePlanIntent carePlanIntent) {
            super.from(carePlanIntent);
            return this;
        }
    }

    public enum Value {
        PROPOSAL("proposal"),

        PLAN("plan"),

        ORDER("order"),

        OPTION("option"),

        DIRECTIVE("directive");

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
         * Factory method for creating CarePlanIntent.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding CarePlanIntent.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "proposal":
                return PROPOSAL;
            case "plan":
                return PLAN;
            case "order":
                return ORDER;
            case "option":
                return OPTION;
            case "directive":
                return DIRECTIVE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}