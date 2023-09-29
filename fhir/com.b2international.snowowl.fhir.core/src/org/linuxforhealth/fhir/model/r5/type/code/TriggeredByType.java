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

@System("http://hl7.org/fhir/observation-triggeredbytype")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class TriggeredByType extends Code {
    /**
     * Reflex
     * 
     * <p>Performance of one or more other tests depending on the results of the initial test. This may include collection of 
     * additional specimen. While a new ServiceRequest is not required to perform the additional test, where it is still 
     * needed (e.g., requesting another laboratory to perform the reflex test), the Observation.basedOn would reference the 
     * new ServiceRequest that requested the additional test to be performed as well as the original ServiceRequest to 
     * reflect the one that provided the authorization.
     */
    public static final TriggeredByType REFLEX = TriggeredByType.builder().value(Value.REFLEX).build();

    /**
     * Repeat (per policy)
     * 
     * <p>Performance of the same test again with the same parameters/settings/solution.
     */
    public static final TriggeredByType REPEAT = TriggeredByType.builder().value(Value.REPEAT).build();

    /**
     * Re-run (per policy)
     * 
     * <p>Performance of the same test but with different parameters/settings/solution.
     */
    public static final TriggeredByType RE_RUN = TriggeredByType.builder().value(Value.RE_RUN).build();

    private volatile int hashCode;

    private TriggeredByType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this TriggeredByType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating TriggeredByType objects from a passed enum value.
     */
    public static TriggeredByType of(Value value) {
        switch (value) {
        case REFLEX:
            return REFLEX;
        case REPEAT:
            return REPEAT;
        case RE_RUN:
            return RE_RUN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating TriggeredByType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static TriggeredByType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating TriggeredByType objects from a passed string value.
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
     * Inherited factory method for creating TriggeredByType objects from a passed string value.
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
        TriggeredByType other = (TriggeredByType) obj;
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
         *     An enum constant for TriggeredByType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public TriggeredByType build() {
            TriggeredByType triggeredByType = new TriggeredByType(this);
            if (validating) {
                validate(triggeredByType);
            }
            return triggeredByType;
        }

        protected void validate(TriggeredByType triggeredByType) {
            super.validate(triggeredByType);
        }

        protected Builder from(TriggeredByType triggeredByType) {
            super.from(triggeredByType);
            return this;
        }
    }

    public enum Value {
        /**
         * Reflex
         * 
         * <p>Performance of one or more other tests depending on the results of the initial test. This may include collection of 
         * additional specimen. While a new ServiceRequest is not required to perform the additional test, where it is still 
         * needed (e.g., requesting another laboratory to perform the reflex test), the Observation.basedOn would reference the 
         * new ServiceRequest that requested the additional test to be performed as well as the original ServiceRequest to 
         * reflect the one that provided the authorization.
         */
        REFLEX("reflex"),

        /**
         * Repeat (per policy)
         * 
         * <p>Performance of the same test again with the same parameters/settings/solution.
         */
        REPEAT("repeat"),

        /**
         * Re-run (per policy)
         * 
         * <p>Performance of the same test but with different parameters/settings/solution.
         */
        RE_RUN("re-run");

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
         * Factory method for creating TriggeredByType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding TriggeredByType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "reflex":
                return REFLEX;
            case "repeat":
                return REPEAT;
            case "re-run":
                return RE_RUN;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
