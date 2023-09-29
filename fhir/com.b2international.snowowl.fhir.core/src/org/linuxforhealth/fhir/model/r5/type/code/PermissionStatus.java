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

@System("http://hl7.org/fhir/permission-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PermissionStatus extends Code {
    /**
     * Active
     * 
     * <p>Permission is given.
     */
    public static final PermissionStatus ACTIVE = PermissionStatus.builder().value(Value.ACTIVE).build();

    /**
     * Entered in Error
     * 
     * <p>Permission was entered in error and is not active.
     */
    public static final PermissionStatus ENTERED_IN_ERROR = PermissionStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Draft
     * 
     * <p>Permission is being defined.
     */
    public static final PermissionStatus DRAFT = PermissionStatus.builder().value(Value.DRAFT).build();

    /**
     * Rejected
     * 
     * <p>Permission not granted.
     */
    public static final PermissionStatus REJECTED = PermissionStatus.builder().value(Value.REJECTED).build();

    private volatile int hashCode;

    private PermissionStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this PermissionStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating PermissionStatus objects from a passed enum value.
     */
    public static PermissionStatus of(Value value) {
        switch (value) {
        case ACTIVE:
            return ACTIVE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case DRAFT:
            return DRAFT;
        case REJECTED:
            return REJECTED;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating PermissionStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static PermissionStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating PermissionStatus objects from a passed string value.
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
     * Inherited factory method for creating PermissionStatus objects from a passed string value.
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
        PermissionStatus other = (PermissionStatus) obj;
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
         *     An enum constant for PermissionStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public PermissionStatus build() {
            PermissionStatus permissionStatus = new PermissionStatus(this);
            if (validating) {
                validate(permissionStatus);
            }
            return permissionStatus;
        }

        protected void validate(PermissionStatus permissionStatus) {
            super.validate(permissionStatus);
        }

        protected Builder from(PermissionStatus permissionStatus) {
            super.from(permissionStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Active
         * 
         * <p>Permission is given.
         */
        ACTIVE("active"),

        /**
         * Entered in Error
         * 
         * <p>Permission was entered in error and is not active.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Draft
         * 
         * <p>Permission is being defined.
         */
        DRAFT("draft"),

        /**
         * Rejected
         * 
         * <p>Permission not granted.
         */
        REJECTED("rejected");

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
         * Factory method for creating PermissionStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding PermissionStatus.Value or null if a null value was passed
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
            case "draft":
                return DRAFT;
            case "rejected":
                return REJECTED;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
