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

@System("http://hl7.org/fhir/observation-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DetectedIssueStatus extends Code {
    /**
     * Preliminary
     */
    public static final DetectedIssueStatus PRELIMINARY = DetectedIssueStatus.builder().value(Value.PRELIMINARY).build();

    /**
     * Final
     */
    public static final DetectedIssueStatus FINAL = DetectedIssueStatus.builder().value(Value.FINAL).build();

    /**
     * Entered in Error
     */
    public static final DetectedIssueStatus ENTERED_IN_ERROR = DetectedIssueStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Mitigated
     */
    public static final DetectedIssueStatus MITIGATED = DetectedIssueStatus.builder().value(Value.MITIGATED).build();

    private volatile int hashCode;

    private DetectedIssueStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DetectedIssueStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DetectedIssueStatus objects from a passed enum value.
     */
    public static DetectedIssueStatus of(Value value) {
        switch (value) {
        case PRELIMINARY:
            return PRELIMINARY;
        case FINAL:
            return FINAL;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case MITIGATED:
            return MITIGATED;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DetectedIssueStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DetectedIssueStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DetectedIssueStatus objects from a passed string value.
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
     * Inherited factory method for creating DetectedIssueStatus objects from a passed string value.
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
        DetectedIssueStatus other = (DetectedIssueStatus) obj;
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
         *     An enum constant for DetectedIssueStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DetectedIssueStatus build() {
            DetectedIssueStatus detectedIssueStatus = new DetectedIssueStatus(this);
            if (validating) {
                validate(detectedIssueStatus);
            }
            return detectedIssueStatus;
        }

        protected void validate(DetectedIssueStatus detectedIssueStatus) {
            super.validate(detectedIssueStatus);
        }

        protected Builder from(DetectedIssueStatus detectedIssueStatus) {
            super.from(detectedIssueStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Preliminary
         */
        PRELIMINARY("preliminary"),

        /**
         * Final
         */
        FINAL("final"),

        /**
         * Entered in Error
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Mitigated
         */
        MITIGATED("mitigated");

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
         * Factory method for creating DetectedIssueStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DetectedIssueStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "preliminary":
                return PRELIMINARY;
            case "final":
                return FINAL;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "mitigated":
                return MITIGATED;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
