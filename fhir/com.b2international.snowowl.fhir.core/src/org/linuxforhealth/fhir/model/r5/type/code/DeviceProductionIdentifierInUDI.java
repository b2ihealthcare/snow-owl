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

@System("http://hl7.org/fhir/device-productidentifierinudi")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceProductionIdentifierInUDI extends Code {
    /**
     * Lot Number
     * 
     * <p>The label includes the lot number.
     */
    public static final DeviceProductionIdentifierInUDI LOT_NUMBER = DeviceProductionIdentifierInUDI.builder().value(Value.LOT_NUMBER).build();

    /**
     * Manufactured date
     * 
     * <p>The label includes the manufacture date.
     */
    public static final DeviceProductionIdentifierInUDI MANUFACTURED_DATE = DeviceProductionIdentifierInUDI.builder().value(Value.MANUFACTURED_DATE).build();

    /**
     * Serial Number
     * 
     * <p>The label includes the serial number.
     */
    public static final DeviceProductionIdentifierInUDI SERIAL_NUMBER = DeviceProductionIdentifierInUDI.builder().value(Value.SERIAL_NUMBER).build();

    /**
     * Expiration date
     * 
     * <p>The label includes the expiration date.
     */
    public static final DeviceProductionIdentifierInUDI EXPIRATION_DATE = DeviceProductionIdentifierInUDI.builder().value(Value.EXPIRATION_DATE).build();

    /**
     * Biological source
     * 
     * <p>The label includes the biological source identifier.
     */
    public static final DeviceProductionIdentifierInUDI BIOLOGICAL_SOURCE = DeviceProductionIdentifierInUDI.builder().value(Value.BIOLOGICAL_SOURCE).build();

    /**
     * Software Version
     * 
     * <p>The label includes the software version.
     */
    public static final DeviceProductionIdentifierInUDI SOFTWARE_VERSION = DeviceProductionIdentifierInUDI.builder().value(Value.SOFTWARE_VERSION).build();

    private volatile int hashCode;

    private DeviceProductionIdentifierInUDI(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DeviceProductionIdentifierInUDI as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DeviceProductionIdentifierInUDI objects from a passed enum value.
     */
    public static DeviceProductionIdentifierInUDI of(Value value) {
        switch (value) {
        case LOT_NUMBER:
            return LOT_NUMBER;
        case MANUFACTURED_DATE:
            return MANUFACTURED_DATE;
        case SERIAL_NUMBER:
            return SERIAL_NUMBER;
        case EXPIRATION_DATE:
            return EXPIRATION_DATE;
        case BIOLOGICAL_SOURCE:
            return BIOLOGICAL_SOURCE;
        case SOFTWARE_VERSION:
            return SOFTWARE_VERSION;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DeviceProductionIdentifierInUDI objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DeviceProductionIdentifierInUDI of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DeviceProductionIdentifierInUDI objects from a passed string value.
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
     * Inherited factory method for creating DeviceProductionIdentifierInUDI objects from a passed string value.
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
        DeviceProductionIdentifierInUDI other = (DeviceProductionIdentifierInUDI) obj;
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
         *     An enum constant for DeviceProductionIdentifierInUDI
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DeviceProductionIdentifierInUDI build() {
            DeviceProductionIdentifierInUDI deviceProductionIdentifierInUDI = new DeviceProductionIdentifierInUDI(this);
            if (validating) {
                validate(deviceProductionIdentifierInUDI);
            }
            return deviceProductionIdentifierInUDI;
        }

        protected void validate(DeviceProductionIdentifierInUDI deviceProductionIdentifierInUDI) {
            super.validate(deviceProductionIdentifierInUDI);
        }

        protected Builder from(DeviceProductionIdentifierInUDI deviceProductionIdentifierInUDI) {
            super.from(deviceProductionIdentifierInUDI);
            return this;
        }
    }

    public enum Value {
        /**
         * Lot Number
         * 
         * <p>The label includes the lot number.
         */
        LOT_NUMBER("lot-number"),

        /**
         * Manufactured date
         * 
         * <p>The label includes the manufacture date.
         */
        MANUFACTURED_DATE("manufactured-date"),

        /**
         * Serial Number
         * 
         * <p>The label includes the serial number.
         */
        SERIAL_NUMBER("serial-number"),

        /**
         * Expiration date
         * 
         * <p>The label includes the expiration date.
         */
        EXPIRATION_DATE("expiration-date"),

        /**
         * Biological source
         * 
         * <p>The label includes the biological source identifier.
         */
        BIOLOGICAL_SOURCE("biological-source"),

        /**
         * Software Version
         * 
         * <p>The label includes the software version.
         */
        SOFTWARE_VERSION("software-version");

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
         * Factory method for creating DeviceProductionIdentifierInUDI.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DeviceProductionIdentifierInUDI.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "lot-number":
                return LOT_NUMBER;
            case "manufactured-date":
                return MANUFACTURED_DATE;
            case "serial-number":
                return SERIAL_NUMBER;
            case "expiration-date":
                return EXPIRATION_DATE;
            case "biological-source":
                return BIOLOGICAL_SOURCE;
            case "software-version":
                return SOFTWARE_VERSION;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
