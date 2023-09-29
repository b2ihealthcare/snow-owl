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

@System("http://hl7.org/fhir/device-correctiveactionscope")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceCorrectiveActionScope extends Code {
    /**
     * Model
     * 
     * <p>The corrective action was intended for all units of the same model.
     */
    public static final DeviceCorrectiveActionScope MODEL = DeviceCorrectiveActionScope.builder().value(Value.MODEL).build();

    /**
     * Lot Numbers
     * 
     * <p>The corrective action was intended for a specific batch of units identified by a lot number.
     */
    public static final DeviceCorrectiveActionScope LOT_NUMBERS = DeviceCorrectiveActionScope.builder().value(Value.LOT_NUMBERS).build();

    /**
     * Serial Numbers
     * 
     * <p>The corrective action was intended for an individual unit (or a set of units) individually identified by serial 
     * number.
     */
    public static final DeviceCorrectiveActionScope SERIAL_NUMBERS = DeviceCorrectiveActionScope.builder().value(Value.SERIAL_NUMBERS).build();

    private volatile int hashCode;

    private DeviceCorrectiveActionScope(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DeviceCorrectiveActionScope as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DeviceCorrectiveActionScope objects from a passed enum value.
     */
    public static DeviceCorrectiveActionScope of(Value value) {
        switch (value) {
        case MODEL:
            return MODEL;
        case LOT_NUMBERS:
            return LOT_NUMBERS;
        case SERIAL_NUMBERS:
            return SERIAL_NUMBERS;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DeviceCorrectiveActionScope objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DeviceCorrectiveActionScope of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DeviceCorrectiveActionScope objects from a passed string value.
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
     * Inherited factory method for creating DeviceCorrectiveActionScope objects from a passed string value.
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
        DeviceCorrectiveActionScope other = (DeviceCorrectiveActionScope) obj;
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
         *     An enum constant for DeviceCorrectiveActionScope
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DeviceCorrectiveActionScope build() {
            DeviceCorrectiveActionScope deviceCorrectiveActionScope = new DeviceCorrectiveActionScope(this);
            if (validating) {
                validate(deviceCorrectiveActionScope);
            }
            return deviceCorrectiveActionScope;
        }

        protected void validate(DeviceCorrectiveActionScope deviceCorrectiveActionScope) {
            super.validate(deviceCorrectiveActionScope);
        }

        protected Builder from(DeviceCorrectiveActionScope deviceCorrectiveActionScope) {
            super.from(deviceCorrectiveActionScope);
            return this;
        }
    }

    public enum Value {
        /**
         * Model
         * 
         * <p>The corrective action was intended for all units of the same model.
         */
        MODEL("model"),

        /**
         * Lot Numbers
         * 
         * <p>The corrective action was intended for a specific batch of units identified by a lot number.
         */
        LOT_NUMBERS("lot-numbers"),

        /**
         * Serial Numbers
         * 
         * <p>The corrective action was intended for an individual unit (or a set of units) individually identified by serial 
         * number.
         */
        SERIAL_NUMBERS("serial-numbers");

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
         * Factory method for creating DeviceCorrectiveActionScope.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DeviceCorrectiveActionScope.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "model":
                return MODEL;
            case "lot-numbers":
                return LOT_NUMBERS;
            case "serial-numbers":
                return SERIAL_NUMBERS;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
