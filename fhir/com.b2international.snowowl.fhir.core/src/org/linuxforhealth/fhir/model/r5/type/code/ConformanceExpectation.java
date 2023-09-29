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

/*
 * XXX: Added by hand based on the code system definition on HL7's website; apparently this does exist in R4 as well but the converter
 * didn't pick it up 
 */

@System("http://hl7.org/fhir/conformance-expectation")
public class ConformanceExpectation extends Code {
    /**
     * SHALL
     * 
     * <p>Support for the specified capability is required to be considered conformant.
     */
    public static final ConformanceExpectation SHALL = ConformanceExpectation.builder().value(Value.SHALL).build();

    /**
     * SHOULD
     * 
     * <p>Support for the specified capability is strongly encouraged, and failure to support it should only occur after careful consideration.
     */
    public static final ConformanceExpectation SHOULD = ConformanceExpectation.builder().value(Value.SHOULD).build();

    /**
     * MAY
     * 
     * <p>Support for the specified capability is not necessary to be considered conformant, and the requirement should be considered strictly optional.
     */
    public static final ConformanceExpectation MAY = ConformanceExpectation.builder().value(Value.MAY).build();

    /**
     * SHOULD-NOT
     * 
     * <p>Support for the specified capability is strongly discouraged and should occur only after careful consideration.
     */
    public static final ConformanceExpectation SHOULD_NOT = ConformanceExpectation.builder().value(Value.SHOULD_NOT).build();

    private volatile int hashCode;

    private ConformanceExpectation(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ConformanceExpectation as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ConformanceExpectation objects from a passed enum value.
     */
    public static ConformanceExpectation of(Value value) {
        switch (value) {
        case SHALL:
            return SHALL;
        case SHOULD:
            return SHOULD;
        case MAY:
            return MAY;
        case SHOULD_NOT:
            return SHOULD_NOT;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ConformanceExpectation objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ConformanceExpectation of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ConformanceExpectation objects from a passed string value.
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
     * Inherited factory method for creating ConformanceExpectation objects from a passed string value.
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
        ConformanceExpectation other = (ConformanceExpectation) obj;
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
         *     An enum constant for ConformanceExpectation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ConformanceExpectation build() {
            ConformanceExpectation consentDataMeaning = new ConformanceExpectation(this);
            if (validating) {
                validate(consentDataMeaning);
            }
            return consentDataMeaning;
        }

        protected void validate(ConformanceExpectation consentDataMeaning) {
            super.validate(consentDataMeaning);
        }

        protected Builder from(ConformanceExpectation consentDataMeaning) {
            super.from(consentDataMeaning);
            return this;
        }
    }

    public enum Value {
        /**
         * SHALL
         * 
         * <p>Support for the specified capability is required to be considered conformant.
         */
        SHALL("SHALL"),

        /**
         * SHOULD
         * 
         * <p>Support for the specified capability is strongly encouraged, and failure to support it should only occur after careful consideration.
         */
        SHOULD("SHOULD"),

        /**
         * MAY
         * 
         * <p>Support for the specified capability is not necessary to be considered conformant, and the requirement should be considered strictly optional.
         */
        MAY("MAY"),

        /**
         * SHOULD-NOT
         * 
         * <p>Support for the specified capability is strongly discouraged and should occur only after careful consideration.
         */
        SHOULD_NOT("SHOULD-NOT");

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
         * Factory method for creating ConformanceExpectation.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ConformanceExpectation.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "SHALL":
                return SHALL;
            case "SHOULD":
                return SHOULD;
            case "MAY":
                return MAY;
            case "SHOULD-NOT":
                return SHOULD_NOT;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
