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

@System("http://hl7.org/fhir/devicedefinition-regulatory-identifier-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceRegulatoryIdentifierType extends Code {
    /**
     * Basic
     * 
     * <p>EUDAMED's basic UDI-DI identifier.
     */
    public static final DeviceRegulatoryIdentifierType BASIC = DeviceRegulatoryIdentifierType.builder().value(Value.BASIC).build();

    /**
     * Master
     * 
     * <p>EUDAMED's master UDI-DI identifier.
     */
    public static final DeviceRegulatoryIdentifierType MASTER = DeviceRegulatoryIdentifierType.builder().value(Value.MASTER).build();

    /**
     * License
     * 
     * <p>The identifier is a license number.
     */
    public static final DeviceRegulatoryIdentifierType LICENSE = DeviceRegulatoryIdentifierType.builder().value(Value.LICENSE).build();

    private volatile int hashCode;

    private DeviceRegulatoryIdentifierType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DeviceRegulatoryIdentifierType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DeviceRegulatoryIdentifierType objects from a passed enum value.
     */
    public static DeviceRegulatoryIdentifierType of(Value value) {
        switch (value) {
        case BASIC:
            return BASIC;
        case MASTER:
            return MASTER;
        case LICENSE:
            return LICENSE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DeviceRegulatoryIdentifierType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DeviceRegulatoryIdentifierType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DeviceRegulatoryIdentifierType objects from a passed string value.
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
     * Inherited factory method for creating DeviceRegulatoryIdentifierType objects from a passed string value.
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
        DeviceRegulatoryIdentifierType other = (DeviceRegulatoryIdentifierType) obj;
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
         *     An enum constant for DeviceRegulatoryIdentifierType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DeviceRegulatoryIdentifierType build() {
            DeviceRegulatoryIdentifierType deviceRegulatoryIdentifierType = new DeviceRegulatoryIdentifierType(this);
            if (validating) {
                validate(deviceRegulatoryIdentifierType);
            }
            return deviceRegulatoryIdentifierType;
        }

        protected void validate(DeviceRegulatoryIdentifierType deviceRegulatoryIdentifierType) {
            super.validate(deviceRegulatoryIdentifierType);
        }

        protected Builder from(DeviceRegulatoryIdentifierType deviceRegulatoryIdentifierType) {
            super.from(deviceRegulatoryIdentifierType);
            return this;
        }
    }

    public enum Value {
        /**
         * Basic
         * 
         * <p>EUDAMED's basic UDI-DI identifier.
         */
        BASIC("basic"),

        /**
         * Master
         * 
         * <p>EUDAMED's master UDI-DI identifier.
         */
        MASTER("master"),

        /**
         * License
         * 
         * <p>The identifier is a license number.
         */
        LICENSE("license");

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
         * Factory method for creating DeviceRegulatoryIdentifierType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DeviceRegulatoryIdentifierType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "basic":
                return BASIC;
            case "master":
                return MASTER;
            case "license":
                return LICENSE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
