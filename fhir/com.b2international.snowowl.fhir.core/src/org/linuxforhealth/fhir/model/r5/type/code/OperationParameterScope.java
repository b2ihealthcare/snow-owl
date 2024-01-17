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

@System("http://hl7.org/fhir/operation-parameter-scope")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class OperationParameterScope extends Code {
    /**
     * Instance
     * 
     * <p>This is a parameter that can be used at the instance level.
     */
    public static final OperationParameterScope INSTANCE = OperationParameterScope.builder().value(Value.INSTANCE).build();

    /**
     * Type
     * 
     * <p>This is a parameter that can be used at the type level.
     */
    public static final OperationParameterScope TYPE = OperationParameterScope.builder().value(Value.TYPE).build();

    /**
     * System
     * 
     * <p>This is a parameter that can be used at the system level.
     */
    public static final OperationParameterScope SYSTEM = OperationParameterScope.builder().value(Value.SYSTEM).build();

    private volatile int hashCode;

    private OperationParameterScope(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this OperationParameterScope as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating OperationParameterScope objects from a passed enum value.
     */
    public static OperationParameterScope of(Value value) {
        switch (value) {
        case INSTANCE:
            return INSTANCE;
        case TYPE:
            return TYPE;
        case SYSTEM:
            return SYSTEM;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating OperationParameterScope objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static OperationParameterScope of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating OperationParameterScope objects from a passed string value.
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
     * Inherited factory method for creating OperationParameterScope objects from a passed string value.
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
        OperationParameterScope other = (OperationParameterScope) obj;
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
         *     An enum constant for OperationParameterScope
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public OperationParameterScope build() {
            OperationParameterScope operationParameterScope = new OperationParameterScope(this);
            if (validating) {
                validate(operationParameterScope);
            }
            return operationParameterScope;
        }

        protected void validate(OperationParameterScope operationParameterScope) {
            super.validate(operationParameterScope);
        }

        protected Builder from(OperationParameterScope operationParameterScope) {
            super.from(operationParameterScope);
            return this;
        }
    }

    public enum Value {
        /**
         * Instance
         * 
         * <p>This is a parameter that can be used at the instance level.
         */
        INSTANCE("instance"),

        /**
         * Type
         * 
         * <p>This is a parameter that can be used at the type level.
         */
        TYPE("type"),

        /**
         * System
         * 
         * <p>This is a parameter that can be used at the system level.
         */
        SYSTEM("system");

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
         * Factory method for creating OperationParameterScope.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding OperationParameterScope.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "instance":
                return INSTANCE;
            case "type":
                return TYPE;
            case "system":
                return SYSTEM;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
