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

@System("http://hl7.org/fhir/CodeSystem/formularyitem-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FormularyItemStatus extends Code {
    /**
     * Active
     * 
     * <p>The service or product referred to by this FormularyItem is in active use within the drug database or inventory 
     * system.
     */
    public static final FormularyItemStatus ACTIVE = FormularyItemStatus.builder().value(Value.ACTIVE).build();

    /**
     * Entered in Error
     * 
     * <p>The service or product referred to by this FormularyItem was entered in error within the drug database or inventory 
     * system.
     */
    public static final FormularyItemStatus ENTERED_IN_ERROR = FormularyItemStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Inactive
     * 
     * <p>The service or product referred to by this FormularyItem is not in active use within the drug database or inventory 
     * system.
     */
    public static final FormularyItemStatus INACTIVE = FormularyItemStatus.builder().value(Value.INACTIVE).build();

    private volatile int hashCode;

    private FormularyItemStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this FormularyItemStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating FormularyItemStatus objects from a passed enum value.
     */
    public static FormularyItemStatus of(Value value) {
        switch (value) {
        case ACTIVE:
            return ACTIVE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case INACTIVE:
            return INACTIVE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating FormularyItemStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static FormularyItemStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating FormularyItemStatus objects from a passed string value.
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
     * Inherited factory method for creating FormularyItemStatus objects from a passed string value.
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
        FormularyItemStatus other = (FormularyItemStatus) obj;
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
         *     An enum constant for FormularyItemStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public FormularyItemStatus build() {
            FormularyItemStatus formularyItemStatus = new FormularyItemStatus(this);
            if (validating) {
                validate(formularyItemStatus);
            }
            return formularyItemStatus;
        }

        protected void validate(FormularyItemStatus formularyItemStatus) {
            super.validate(formularyItemStatus);
        }

        protected Builder from(FormularyItemStatus formularyItemStatus) {
            super.from(formularyItemStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Active
         * 
         * <p>The service or product referred to by this FormularyItem is in active use within the drug database or inventory 
         * system.
         */
        ACTIVE("active"),

        /**
         * Entered in Error
         * 
         * <p>The service or product referred to by this FormularyItem was entered in error within the drug database or inventory 
         * system.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Inactive
         * 
         * <p>The service or product referred to by this FormularyItem is not in active use within the drug database or inventory 
         * system.
         */
        INACTIVE("inactive");

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
         * Factory method for creating FormularyItemStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding FormularyItemStatus.Value or null if a null value was passed
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
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "inactive":
                return INACTIVE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
