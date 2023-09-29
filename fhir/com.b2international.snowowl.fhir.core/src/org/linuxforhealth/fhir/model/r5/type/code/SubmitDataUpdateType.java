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

@System("http://hl7.org/fhir/CodeSystem/submit-data-update-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SubmitDataUpdateType extends Code {
    /**
     * Incremental
     * 
     * <p>In contrast to the Snapshot Update, the FHIR Parameters resource used in a Submit Data or the Collect Data scenario 
     * contains only the new and updated DEQM and QI Core Profiles since the last transaction. If the Consumer supports 
     * incremental updates, the contents of the updated payload updates the previous payload data.
     */
    public static final SubmitDataUpdateType INCREMENTAL = SubmitDataUpdateType.builder().value(Value.INCREMENTAL).build();

    /**
     * Snapshot
     * 
     * <p>In contrast to the Incremental Update, the FHIR Parameters resource used in a Submit Data or the Collect Data 
     * scenario contains all the DEQM and QI Core Profiles for each transaction. If the Consumer supports snapshot updates, 
     * the contents of the updated payload entirely replaces the previous payload
     */
    public static final SubmitDataUpdateType SNAPSHOT = SubmitDataUpdateType.builder().value(Value.SNAPSHOT).build();

    private volatile int hashCode;

    private SubmitDataUpdateType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this SubmitDataUpdateType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating SubmitDataUpdateType objects from a passed enum value.
     */
    public static SubmitDataUpdateType of(Value value) {
        switch (value) {
        case INCREMENTAL:
            return INCREMENTAL;
        case SNAPSHOT:
            return SNAPSHOT;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating SubmitDataUpdateType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static SubmitDataUpdateType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating SubmitDataUpdateType objects from a passed string value.
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
     * Inherited factory method for creating SubmitDataUpdateType objects from a passed string value.
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
        SubmitDataUpdateType other = (SubmitDataUpdateType) obj;
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
         *     An enum constant for SubmitDataUpdateType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public SubmitDataUpdateType build() {
            SubmitDataUpdateType submitDataUpdateType = new SubmitDataUpdateType(this);
            if (validating) {
                validate(submitDataUpdateType);
            }
            return submitDataUpdateType;
        }

        protected void validate(SubmitDataUpdateType submitDataUpdateType) {
            super.validate(submitDataUpdateType);
        }

        protected Builder from(SubmitDataUpdateType submitDataUpdateType) {
            super.from(submitDataUpdateType);
            return this;
        }
    }

    public enum Value {
        /**
         * Incremental
         * 
         * <p>In contrast to the Snapshot Update, the FHIR Parameters resource used in a Submit Data or the Collect Data scenario 
         * contains only the new and updated DEQM and QI Core Profiles since the last transaction. If the Consumer supports 
         * incremental updates, the contents of the updated payload updates the previous payload data.
         */
        INCREMENTAL("incremental"),

        /**
         * Snapshot
         * 
         * <p>In contrast to the Incremental Update, the FHIR Parameters resource used in a Submit Data or the Collect Data 
         * scenario contains all the DEQM and QI Core Profiles for each transaction. If the Consumer supports snapshot updates, 
         * the contents of the updated payload entirely replaces the previous payload
         */
        SNAPSHOT("snapshot");

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
         * Factory method for creating SubmitDataUpdateType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding SubmitDataUpdateType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "incremental":
                return INCREMENTAL;
            case "snapshot":
                return SNAPSHOT;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
