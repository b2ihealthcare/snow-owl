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

@System("http://hl7.org/fhir/consent-state-codes")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ConsentState extends Code {
    /**
     * Pending
     * 
     * <p>The consent is in development or awaiting use but is not yet intended to be acted upon.
     */
    public static final ConsentState DRAFT = ConsentState.builder().value(Value.DRAFT).build();

    /**
     * Active
     * 
     * <p>The consent is to be followed and enforced.
     */
    public static final ConsentState ACTIVE = ConsentState.builder().value(Value.ACTIVE).build();

    /**
     * Inactive
     * 
     * <p>The consent is terminated or replaced.
     */
    public static final ConsentState INACTIVE = ConsentState.builder().value(Value.INACTIVE).build();

    /**
     * Abandoned
     * 
     * <p>The consent development has been terminated prior to completion.
     */
    public static final ConsentState NOT_DONE = ConsentState.builder().value(Value.NOT_DONE).build();

    /**
     * Entered in Error
     * 
     * <p>The consent was created wrongly (e.g. wrong patient) and should be ignored.
     */
    public static final ConsentState ENTERED_IN_ERROR = ConsentState.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Unknown
     * 
     * <p>The resource is in an indeterminate state.
     */
    public static final ConsentState UNKNOWN = ConsentState.builder().value(Value.UNKNOWN).build();

    private volatile int hashCode;

    private ConsentState(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ConsentState as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ConsentState objects from a passed enum value.
     */
    public static ConsentState of(Value value) {
        switch (value) {
        case DRAFT:
            return DRAFT;
        case ACTIVE:
            return ACTIVE;
        case INACTIVE:
            return INACTIVE;
        case NOT_DONE:
            return NOT_DONE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case UNKNOWN:
            return UNKNOWN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ConsentState objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ConsentState of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ConsentState objects from a passed string value.
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
     * Inherited factory method for creating ConsentState objects from a passed string value.
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
        ConsentState other = (ConsentState) obj;
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
         *     An enum constant for ConsentState
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ConsentState build() {
            ConsentState consentState = new ConsentState(this);
            if (validating) {
                validate(consentState);
            }
            return consentState;
        }

        protected void validate(ConsentState consentState) {
            super.validate(consentState);
        }

        protected Builder from(ConsentState consentState) {
            super.from(consentState);
            return this;
        }
    }

    public enum Value {
        /**
         * Pending
         * 
         * <p>The consent is in development or awaiting use but is not yet intended to be acted upon.
         */
        DRAFT("draft"),

        /**
         * Active
         * 
         * <p>The consent is to be followed and enforced.
         */
        ACTIVE("active"),

        /**
         * Inactive
         * 
         * <p>The consent is terminated or replaced.
         */
        INACTIVE("inactive"),

        /**
         * Abandoned
         * 
         * <p>The consent development has been terminated prior to completion.
         */
        NOT_DONE("not-done"),

        /**
         * Entered in Error
         * 
         * <p>The consent was created wrongly (e.g. wrong patient) and should be ignored.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Unknown
         * 
         * <p>The resource is in an indeterminate state.
         */
        UNKNOWN("unknown");

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
         * Factory method for creating ConsentState.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ConsentState.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "draft":
                return DRAFT;
            case "active":
                return ACTIVE;
            case "inactive":
                return INACTIVE;
            case "not-done":
                return NOT_DONE;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "unknown":
                return UNKNOWN;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
