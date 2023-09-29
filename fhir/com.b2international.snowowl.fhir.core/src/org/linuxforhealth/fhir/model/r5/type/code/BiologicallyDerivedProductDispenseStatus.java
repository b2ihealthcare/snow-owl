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

@System("http://hl7.org/fhir/biologicallyderivedproductdispense-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class BiologicallyDerivedProductDispenseStatus extends Code {
    /**
     * Preparation
     * 
     * <p>The dispense process has started but not yet completed.
     */
    public static final BiologicallyDerivedProductDispenseStatus PREPARATION = BiologicallyDerivedProductDispenseStatus.builder().value(Value.PREPARATION).build();

    /**
     * In Progress
     * 
     * <p>The dispense process is in progress.
     */
    public static final BiologicallyDerivedProductDispenseStatus IN_PROGRESS = BiologicallyDerivedProductDispenseStatus.builder().value(Value.IN_PROGRESS).build();

    /**
     * Allocated
     * 
     * <p>The requested product has been allocated and is ready for transport.
     */
    public static final BiologicallyDerivedProductDispenseStatus ALLOCATED = BiologicallyDerivedProductDispenseStatus.builder().value(Value.ALLOCATED).build();

    /**
     * Issued
     * 
     * <p>The dispensed product has been picked up.
     */
    public static final BiologicallyDerivedProductDispenseStatus ISSUED = BiologicallyDerivedProductDispenseStatus.builder().value(Value.ISSUED).build();

    /**
     * Unfulfilled
     * 
     * <p>The dispense could not be completed.
     */
    public static final BiologicallyDerivedProductDispenseStatus UNFULFILLED = BiologicallyDerivedProductDispenseStatus.builder().value(Value.UNFULFILLED).build();

    /**
     * Returned
     * 
     * <p>The dispensed product was returned.
     */
    public static final BiologicallyDerivedProductDispenseStatus RETURNED = BiologicallyDerivedProductDispenseStatus.builder().value(Value.RETURNED).build();

    /**
     * Entered in Error
     * 
     * <p>The dispense was entered in error and therefore nullified.
     */
    public static final BiologicallyDerivedProductDispenseStatus ENTERED_IN_ERROR = BiologicallyDerivedProductDispenseStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Unknown
     * 
     * <p>The authoring system does not know which of the status values applies for this dispense. Note: this concept is not 
     * to be used for other - one of the listed statuses is presumed to apply, it's just not known which one.
     */
    public static final BiologicallyDerivedProductDispenseStatus UNKNOWN = BiologicallyDerivedProductDispenseStatus.builder().value(Value.UNKNOWN).build();

    private volatile int hashCode;

    private BiologicallyDerivedProductDispenseStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this BiologicallyDerivedProductDispenseStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating BiologicallyDerivedProductDispenseStatus objects from a passed enum value.
     */
    public static BiologicallyDerivedProductDispenseStatus of(Value value) {
        switch (value) {
        case PREPARATION:
            return PREPARATION;
        case IN_PROGRESS:
            return IN_PROGRESS;
        case ALLOCATED:
            return ALLOCATED;
        case ISSUED:
            return ISSUED;
        case UNFULFILLED:
            return UNFULFILLED;
        case RETURNED:
            return RETURNED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case UNKNOWN:
            return UNKNOWN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating BiologicallyDerivedProductDispenseStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static BiologicallyDerivedProductDispenseStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating BiologicallyDerivedProductDispenseStatus objects from a passed string value.
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
     * Inherited factory method for creating BiologicallyDerivedProductDispenseStatus objects from a passed string value.
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
        BiologicallyDerivedProductDispenseStatus other = (BiologicallyDerivedProductDispenseStatus) obj;
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
         *     An enum constant for BiologicallyDerivedProductDispenseStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public BiologicallyDerivedProductDispenseStatus build() {
            BiologicallyDerivedProductDispenseStatus biologicallyDerivedProductDispenseStatus = new BiologicallyDerivedProductDispenseStatus(this);
            if (validating) {
                validate(biologicallyDerivedProductDispenseStatus);
            }
            return biologicallyDerivedProductDispenseStatus;
        }

        protected void validate(BiologicallyDerivedProductDispenseStatus biologicallyDerivedProductDispenseStatus) {
            super.validate(biologicallyDerivedProductDispenseStatus);
        }

        protected Builder from(BiologicallyDerivedProductDispenseStatus biologicallyDerivedProductDispenseStatus) {
            super.from(biologicallyDerivedProductDispenseStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Preparation
         * 
         * <p>The dispense process has started but not yet completed.
         */
        PREPARATION("preparation"),

        /**
         * In Progress
         * 
         * <p>The dispense process is in progress.
         */
        IN_PROGRESS("in-progress"),

        /**
         * Allocated
         * 
         * <p>The requested product has been allocated and is ready for transport.
         */
        ALLOCATED("allocated"),

        /**
         * Issued
         * 
         * <p>The dispensed product has been picked up.
         */
        ISSUED("issued"),

        /**
         * Unfulfilled
         * 
         * <p>The dispense could not be completed.
         */
        UNFULFILLED("unfulfilled"),

        /**
         * Returned
         * 
         * <p>The dispensed product was returned.
         */
        RETURNED("returned"),

        /**
         * Entered in Error
         * 
         * <p>The dispense was entered in error and therefore nullified.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Unknown
         * 
         * <p>The authoring system does not know which of the status values applies for this dispense. Note: this concept is not 
         * to be used for other - one of the listed statuses is presumed to apply, it's just not known which one.
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
         * Factory method for creating BiologicallyDerivedProductDispenseStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding BiologicallyDerivedProductDispenseStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "preparation":
                return PREPARATION;
            case "in-progress":
                return IN_PROGRESS;
            case "allocated":
                return ALLOCATED;
            case "issued":
                return ISSUED;
            case "unfulfilled":
                return UNFULFILLED;
            case "returned":
                return RETURNED;
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
