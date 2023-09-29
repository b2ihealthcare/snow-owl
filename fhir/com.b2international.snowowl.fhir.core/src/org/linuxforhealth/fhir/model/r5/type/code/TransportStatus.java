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

@System("http://hl7.org/fhir/transport-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class TransportStatus extends Code {
    /**
     * In Progress
     * 
     * <p>Transport has started but not completed.
     */
    public static final TransportStatus IN_PROGRESS = TransportStatus.builder().value(Value.IN_PROGRESS).build();

    /**
     * Completed
     * 
     * <p>Transport has been completed.
     */
    public static final TransportStatus COMPLETED = TransportStatus.builder().value(Value.COMPLETED).build();

    /**
     * Abandoned
     * 
     * <p>Transport was started but not completed.
     */
    public static final TransportStatus ABANDONED = TransportStatus.builder().value(Value.ABANDONED).build();

    /**
     * Cancelled
     * 
     * <p>Transport was cancelled before started.
     */
    public static final TransportStatus CANCELLED = TransportStatus.builder().value(Value.CANCELLED).build();

    /**
     * Planned
     * 
     * <p>Planned transport that is not yet requested.
     */
    public static final TransportStatus PLANNED = TransportStatus.builder().value(Value.PLANNED).build();

    /**
     * Entered In Error
     * 
     * <p>This electronic record should never have existed, though it is possible that real-world decisions were based on it. 
     * (If real-world activity has occurred, the status should be "abandoned" rather than "entered-in-error".).
     */
    public static final TransportStatus ENTERED_IN_ERROR = TransportStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    private volatile int hashCode;

    private TransportStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this TransportStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating TransportStatus objects from a passed enum value.
     */
    public static TransportStatus of(Value value) {
        switch (value) {
        case IN_PROGRESS:
            return IN_PROGRESS;
        case COMPLETED:
            return COMPLETED;
        case ABANDONED:
            return ABANDONED;
        case CANCELLED:
            return CANCELLED;
        case PLANNED:
            return PLANNED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating TransportStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static TransportStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating TransportStatus objects from a passed string value.
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
     * Inherited factory method for creating TransportStatus objects from a passed string value.
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
        TransportStatus other = (TransportStatus) obj;
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
         *     An enum constant for TransportStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public TransportStatus build() {
            TransportStatus transportStatus = new TransportStatus(this);
            if (validating) {
                validate(transportStatus);
            }
            return transportStatus;
        }

        protected void validate(TransportStatus transportStatus) {
            super.validate(transportStatus);
        }

        protected Builder from(TransportStatus transportStatus) {
            super.from(transportStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * In Progress
         * 
         * <p>Transport has started but not completed.
         */
        IN_PROGRESS("in-progress"),

        /**
         * Completed
         * 
         * <p>Transport has been completed.
         */
        COMPLETED("completed"),

        /**
         * Abandoned
         * 
         * <p>Transport was started but not completed.
         */
        ABANDONED("abandoned"),

        /**
         * Cancelled
         * 
         * <p>Transport was cancelled before started.
         */
        CANCELLED("cancelled"),

        /**
         * Planned
         * 
         * <p>Planned transport that is not yet requested.
         */
        PLANNED("planned"),

        /**
         * Entered In Error
         * 
         * <p>This electronic record should never have existed, though it is possible that real-world decisions were based on it. 
         * (If real-world activity has occurred, the status should be "abandoned" rather than "entered-in-error".).
         */
        ENTERED_IN_ERROR("entered-in-error");

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
         * Factory method for creating TransportStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding TransportStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "in-progress":
                return IN_PROGRESS;
            case "completed":
                return COMPLETED;
            case "abandoned":
                return ABANDONED;
            case "cancelled":
                return CANCELLED;
            case "planned":
                return PLANNED;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
