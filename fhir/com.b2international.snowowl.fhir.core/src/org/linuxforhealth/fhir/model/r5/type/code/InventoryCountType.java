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

@System("http://hl7.org/fhir/inventoryreport-counttype")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class InventoryCountType extends Code {
    /**
     * Snapshot
     * 
     * <p>The inventory report is a current absolute snapshot, i.e. it represents the quantities at hand.
     */
    public static final InventoryCountType SNAPSHOT = InventoryCountType.builder().value(Value.SNAPSHOT).build();

    /**
     * Difference
     * 
     * <p>The inventory report is about the difference between a previous count and a current count, i.e. it represents the 
     * items that have been added/subtracted from inventory.
     */
    public static final InventoryCountType DIFFERENCE = InventoryCountType.builder().value(Value.DIFFERENCE).build();

    private volatile int hashCode;

    private InventoryCountType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this InventoryCountType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating InventoryCountType objects from a passed enum value.
     */
    public static InventoryCountType of(Value value) {
        switch (value) {
        case SNAPSHOT:
            return SNAPSHOT;
        case DIFFERENCE:
            return DIFFERENCE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating InventoryCountType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static InventoryCountType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating InventoryCountType objects from a passed string value.
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
     * Inherited factory method for creating InventoryCountType objects from a passed string value.
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
        InventoryCountType other = (InventoryCountType) obj;
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
         *     An enum constant for InventoryCountType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public InventoryCountType build() {
            InventoryCountType inventoryCountType = new InventoryCountType(this);
            if (validating) {
                validate(inventoryCountType);
            }
            return inventoryCountType;
        }

        protected void validate(InventoryCountType inventoryCountType) {
            super.validate(inventoryCountType);
        }

        protected Builder from(InventoryCountType inventoryCountType) {
            super.from(inventoryCountType);
            return this;
        }
    }

    public enum Value {
        /**
         * Snapshot
         * 
         * <p>The inventory report is a current absolute snapshot, i.e. it represents the quantities at hand.
         */
        SNAPSHOT("snapshot"),

        /**
         * Difference
         * 
         * <p>The inventory report is about the difference between a previous count and a current count, i.e. it represents the 
         * items that have been added/subtracted from inventory.
         */
        DIFFERENCE("difference");

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
         * Factory method for creating InventoryCountType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding InventoryCountType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "snapshot":
                return SNAPSHOT;
            case "difference":
                return DIFFERENCE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
