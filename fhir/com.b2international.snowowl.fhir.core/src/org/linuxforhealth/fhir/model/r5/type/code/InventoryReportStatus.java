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

@System("http://hl7.org/fhir/inventoryreport-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class InventoryReportStatus extends Code {
    /**
     * Draft
     * 
     * <p>The existence of the report is registered, but it is still without content or only some preliminary content.
     */
    public static final InventoryReportStatus DRAFT = InventoryReportStatus.builder().value(Value.DRAFT).build();

    /**
     * Requested
     * 
     * <p>The inventory report has been requested but there is no data available.
     */
    public static final InventoryReportStatus REQUESTED = InventoryReportStatus.builder().value(Value.REQUESTED).build();

    /**
     * Active
     * 
     * <p>This report is submitted as current.
     */
    public static final InventoryReportStatus ACTIVE = InventoryReportStatus.builder().value(Value.ACTIVE).build();

    /**
     * Entered in Error
     * 
     * <p>The report has been withdrawn following a previous final release. This electronic record should never have existed, 
     * though it is possible that real-world decisions were based on it.
     */
    public static final InventoryReportStatus ENTERED_IN_ERROR = InventoryReportStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    private volatile int hashCode;

    private InventoryReportStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this InventoryReportStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating InventoryReportStatus objects from a passed enum value.
     */
    public static InventoryReportStatus of(Value value) {
        switch (value) {
        case DRAFT:
            return DRAFT;
        case REQUESTED:
            return REQUESTED;
        case ACTIVE:
            return ACTIVE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating InventoryReportStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static InventoryReportStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating InventoryReportStatus objects from a passed string value.
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
     * Inherited factory method for creating InventoryReportStatus objects from a passed string value.
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
        InventoryReportStatus other = (InventoryReportStatus) obj;
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
         *     An enum constant for InventoryReportStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public InventoryReportStatus build() {
            InventoryReportStatus inventoryReportStatus = new InventoryReportStatus(this);
            if (validating) {
                validate(inventoryReportStatus);
            }
            return inventoryReportStatus;
        }

        protected void validate(InventoryReportStatus inventoryReportStatus) {
            super.validate(inventoryReportStatus);
        }

        protected Builder from(InventoryReportStatus inventoryReportStatus) {
            super.from(inventoryReportStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Draft
         * 
         * <p>The existence of the report is registered, but it is still without content or only some preliminary content.
         */
        DRAFT("draft"),

        /**
         * Requested
         * 
         * <p>The inventory report has been requested but there is no data available.
         */
        REQUESTED("requested"),

        /**
         * Active
         * 
         * <p>This report is submitted as current.
         */
        ACTIVE("active"),

        /**
         * Entered in Error
         * 
         * <p>The report has been withdrawn following a previous final release. This electronic record should never have existed, 
         * though it is possible that real-world decisions were based on it.
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
         * Factory method for creating InventoryReportStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding InventoryReportStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "draft":
                return DRAFT;
            case "requested":
                return REQUESTED;
            case "active":
                return ACTIVE;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
