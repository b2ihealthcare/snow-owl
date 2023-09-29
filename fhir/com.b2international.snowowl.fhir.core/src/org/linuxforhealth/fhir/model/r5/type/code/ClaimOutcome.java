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

@System("http://hl7.org/fhir/claim-outcome")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ClaimOutcome extends Code {
    /**
     * Queued
     * 
     * <p>The Claim/Pre-authorization/Pre-determination has been received but processing has not begun.
     */
    public static final ClaimOutcome QUEUED = ClaimOutcome.builder().value(Value.QUEUED).build();

    /**
     * Processing Complete
     * 
     * <p>The processing has completed without errors
     */
    public static final ClaimOutcome COMPLETE = ClaimOutcome.builder().value(Value.COMPLETE).build();

    /**
     * Error
     * 
     * <p>One or more errors have been detected in the Claim
     */
    public static final ClaimOutcome ERROR = ClaimOutcome.builder().value(Value.ERROR).build();

    /**
     * Partial Processing
     * 
     * <p>No errors have been detected in the Claim and some of the adjudication has been performed.
     */
    public static final ClaimOutcome PARTIAL = ClaimOutcome.builder().value(Value.PARTIAL).build();

    private volatile int hashCode;

    private ClaimOutcome(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ClaimOutcome as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ClaimOutcome objects from a passed enum value.
     */
    public static ClaimOutcome of(Value value) {
        switch (value) {
        case QUEUED:
            return QUEUED;
        case COMPLETE:
            return COMPLETE;
        case ERROR:
            return ERROR;
        case PARTIAL:
            return PARTIAL;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ClaimOutcome objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ClaimOutcome of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ClaimOutcome objects from a passed string value.
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
     * Inherited factory method for creating ClaimOutcome objects from a passed string value.
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
        ClaimOutcome other = (ClaimOutcome) obj;
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
         *     An enum constant for ClaimOutcome
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ClaimOutcome build() {
            ClaimOutcome claimOutcome = new ClaimOutcome(this);
            if (validating) {
                validate(claimOutcome);
            }
            return claimOutcome;
        }

        protected void validate(ClaimOutcome claimOutcome) {
            super.validate(claimOutcome);
        }

        protected Builder from(ClaimOutcome claimOutcome) {
            super.from(claimOutcome);
            return this;
        }
    }

    public enum Value {
        /**
         * Queued
         * 
         * <p>The Claim/Pre-authorization/Pre-determination has been received but processing has not begun.
         */
        QUEUED("queued"),

        /**
         * Processing Complete
         * 
         * <p>The processing has completed without errors
         */
        COMPLETE("complete"),

        /**
         * Error
         * 
         * <p>One or more errors have been detected in the Claim
         */
        ERROR("error"),

        /**
         * Partial Processing
         * 
         * <p>No errors have been detected in the Claim and some of the adjudication has been performed.
         */
        PARTIAL("partial");

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
         * Factory method for creating ClaimOutcome.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ClaimOutcome.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "queued":
                return QUEUED;
            case "complete":
                return COMPLETE;
            case "error":
                return ERROR;
            case "partial":
                return PARTIAL;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
