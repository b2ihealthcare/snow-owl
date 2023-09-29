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

@System("http://hl7.org/fhir/device-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FHIRDeviceStatus extends Code {
    /**
     * Active
     * 
     * <p>The device record is current and is appropriate for reference in new instances.
     */
    public static final FHIRDeviceStatus ACTIVE = FHIRDeviceStatus.builder().value(Value.ACTIVE).build();

    /**
     * Inactive
     * 
     * <p>The device record is not current and is not appropriate for reference in new instances.
     */
    public static final FHIRDeviceStatus INACTIVE = FHIRDeviceStatus.builder().value(Value.INACTIVE).build();

    /**
     * Entered in Error
     * 
     * <p>The device record is not current and is not appropriate for reference in new instances.
     */
    public static final FHIRDeviceStatus ENTERED_IN_ERROR = FHIRDeviceStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    private volatile int hashCode;

    private FHIRDeviceStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this FHIRDeviceStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating FHIRDeviceStatus objects from a passed enum value.
     */
    public static FHIRDeviceStatus of(Value value) {
        switch (value) {
        case ACTIVE:
            return ACTIVE;
        case INACTIVE:
            return INACTIVE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating FHIRDeviceStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static FHIRDeviceStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating FHIRDeviceStatus objects from a passed string value.
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
     * Inherited factory method for creating FHIRDeviceStatus objects from a passed string value.
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
        FHIRDeviceStatus other = (FHIRDeviceStatus) obj;
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
         *     An enum constant for FHIRDeviceStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public FHIRDeviceStatus build() {
            FHIRDeviceStatus fHIRDeviceStatus = new FHIRDeviceStatus(this);
            if (validating) {
                validate(fHIRDeviceStatus);
            }
            return fHIRDeviceStatus;
        }

        protected void validate(FHIRDeviceStatus fHIRDeviceStatus) {
            super.validate(fHIRDeviceStatus);
        }

        protected Builder from(FHIRDeviceStatus fHIRDeviceStatus) {
            super.from(fHIRDeviceStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Active
         * 
         * <p>The device record is current and is appropriate for reference in new instances.
         */
        ACTIVE("active"),

        /**
         * Inactive
         * 
         * <p>The device record is not current and is not appropriate for reference in new instances.
         */
        INACTIVE("inactive"),

        /**
         * Entered in Error
         * 
         * <p>The device record is not current and is not appropriate for reference in new instances.
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
         * Factory method for creating FHIRDeviceStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding FHIRDeviceStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "active":
                return ACTIVE;
            case "inactive":
                return INACTIVE;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
