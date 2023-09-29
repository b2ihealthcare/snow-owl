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

@System("http://hl7.org/fhir/coverage-kind")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CoverageKind extends Code {
    /**
     * Insurance
     * 
     * <p>The Coverage provides the identifiers and card-level details of an insurance policy.
     */
    public static final CoverageKind INSURANCE = CoverageKind.builder().value(Value.INSURANCE).build();

    /**
     * Self-pay
     * 
     * <p>One or more persons and/or organizations are paying for the services rendered.
     */
    public static final CoverageKind SELF_PAY = CoverageKind.builder().value(Value.SELF_PAY).build();

    /**
     * Other
     * 
     * <p>Some other organization is paying for the service.
     */
    public static final CoverageKind OTHER = CoverageKind.builder().value(Value.OTHER).build();

    private volatile int hashCode;

    private CoverageKind(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this CoverageKind as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating CoverageKind objects from a passed enum value.
     */
    public static CoverageKind of(Value value) {
        switch (value) {
        case INSURANCE:
            return INSURANCE;
        case SELF_PAY:
            return SELF_PAY;
        case OTHER:
            return OTHER;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating CoverageKind objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static CoverageKind of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating CoverageKind objects from a passed string value.
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
     * Inherited factory method for creating CoverageKind objects from a passed string value.
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
        CoverageKind other = (CoverageKind) obj;
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
         *     An enum constant for CoverageKind
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public CoverageKind build() {
            CoverageKind coverageKind = new CoverageKind(this);
            if (validating) {
                validate(coverageKind);
            }
            return coverageKind;
        }

        protected void validate(CoverageKind coverageKind) {
            super.validate(coverageKind);
        }

        protected Builder from(CoverageKind coverageKind) {
            super.from(coverageKind);
            return this;
        }
    }

    public enum Value {
        /**
         * Insurance
         * 
         * <p>The Coverage provides the identifiers and card-level details of an insurance policy.
         */
        INSURANCE("insurance"),

        /**
         * Self-pay
         * 
         * <p>One or more persons and/or organizations are paying for the services rendered.
         */
        SELF_PAY("self-pay"),

        /**
         * Other
         * 
         * <p>Some other organization is paying for the service.
         */
        OTHER("other");

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
         * Factory method for creating CoverageKind.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding CoverageKind.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "insurance":
                return INSURANCE;
            case "self-pay":
                return SELF_PAY;
            case "other":
                return OTHER;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
