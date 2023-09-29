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

@System("http://hl7.org/fhir/CodeSystem/additional-binding-purpose")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AdditionalBindingPurpose extends Code {
    /**
     * Maximum Binding
     * 
     * <p>A required binding, for use when the binding strength is 'extensible' or 'preferred'
     */
    public static final AdditionalBindingPurpose MAXIMUM = AdditionalBindingPurpose.builder().value(Value.MAXIMUM).build();

    /**
     * Minimum Binding
     * 
     * <p>The minimum allowable value set - any conformant system SHALL support all these codes
     */
    public static final AdditionalBindingPurpose MINIMUM = AdditionalBindingPurpose.builder().value(Value.MINIMUM).build();

    /**
     * Required Binding
     * 
     * <p>This value set is used as a required binding (in addition to the base binding (not a replacement), usually in a 
     * particular usage context)
     */
    public static final AdditionalBindingPurpose REQUIRED = AdditionalBindingPurpose.builder().value(Value.REQUIRED).build();

    /**
     * Conformance Binding
     * 
     * <p>This value set is used as an extensible binding (in addition to the base binding (not a replacement), usually in a 
     * particular usage context)
     */
    public static final AdditionalBindingPurpose EXTENSIBLE = AdditionalBindingPurpose.builder().value(Value.EXTENSIBLE).build();

    /**
     * Candidate Binding
     * 
     * <p>This value set is a candidate to substitute for the overall conformance value set in some situations; usually these 
     * are defined in the documentation
     */
    public static final AdditionalBindingPurpose CANDIDATE = AdditionalBindingPurpose.builder().value(Value.CANDIDATE).build();

    /**
     * Current Binding
     * 
     * <p>New records are required to use this value set, but legacy records may use other codes. The definition of 'new 
     * record' is difficult, since systems often create new records based on pre-existing data. Usually 'current' bindings 
     * are mandated by an external authority that makes clear rules around this
     */
    public static final AdditionalBindingPurpose CURRENT = AdditionalBindingPurpose.builder().value(Value.CURRENT).build();

    /**
     * Preferred Binding
     * 
     * <p>This is the value set that is preferred in a given context (documentation should explain why)
     */
    public static final AdditionalBindingPurpose PREFERRED = AdditionalBindingPurpose.builder().value(Value.PREFERRED).build();

    /**
     * UI Suggested Binding
     * 
     * <p>This value set is provided for user look up in a given context. Typically, these valuesets only include a subset of 
     * codes relevant for input in a context
     */
    public static final AdditionalBindingPurpose UI = AdditionalBindingPurpose.builder().value(Value.UI).build();

    /**
     * Starter Binding
     * 
     * <p>This value set is a good set of codes to start with when designing your system
     */
    public static final AdditionalBindingPurpose STARTER = AdditionalBindingPurpose.builder().value(Value.STARTER).build();

    /**
     * Component Binding
     * 
     * <p>This value set is a component of the base value set. Usually this is called out so that documentation can be 
     * written about a portion of the value set
     */
    public static final AdditionalBindingPurpose COMPONENT = AdditionalBindingPurpose.builder().value(Value.COMPONENT).build();

    private volatile int hashCode;

    private AdditionalBindingPurpose(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this AdditionalBindingPurpose as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating AdditionalBindingPurpose objects from a passed enum value.
     */
    public static AdditionalBindingPurpose of(Value value) {
        switch (value) {
        case MAXIMUM:
            return MAXIMUM;
        case MINIMUM:
            return MINIMUM;
        case REQUIRED:
            return REQUIRED;
        case EXTENSIBLE:
            return EXTENSIBLE;
        case CANDIDATE:
            return CANDIDATE;
        case CURRENT:
            return CURRENT;
        case PREFERRED:
            return PREFERRED;
        case UI:
            return UI;
        case STARTER:
            return STARTER;
        case COMPONENT:
            return COMPONENT;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating AdditionalBindingPurpose objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static AdditionalBindingPurpose of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating AdditionalBindingPurpose objects from a passed string value.
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
     * Inherited factory method for creating AdditionalBindingPurpose objects from a passed string value.
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
        AdditionalBindingPurpose other = (AdditionalBindingPurpose) obj;
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
         *     An enum constant for AdditionalBindingPurpose
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public AdditionalBindingPurpose build() {
            AdditionalBindingPurpose additionalBindingPurpose = new AdditionalBindingPurpose(this);
            if (validating) {
                validate(additionalBindingPurpose);
            }
            return additionalBindingPurpose;
        }

        protected void validate(AdditionalBindingPurpose additionalBindingPurpose) {
            super.validate(additionalBindingPurpose);
        }

        protected Builder from(AdditionalBindingPurpose additionalBindingPurpose) {
            super.from(additionalBindingPurpose);
            return this;
        }
    }

    public enum Value {
        /**
         * Maximum Binding
         * 
         * <p>A required binding, for use when the binding strength is 'extensible' or 'preferred'
         */
        MAXIMUM("maximum"),

        /**
         * Minimum Binding
         * 
         * <p>The minimum allowable value set - any conformant system SHALL support all these codes
         */
        MINIMUM("minimum"),

        /**
         * Required Binding
         * 
         * <p>This value set is used as a required binding (in addition to the base binding (not a replacement), usually in a 
         * particular usage context)
         */
        REQUIRED("required"),

        /**
         * Conformance Binding
         * 
         * <p>This value set is used as an extensible binding (in addition to the base binding (not a replacement), usually in a 
         * particular usage context)
         */
        EXTENSIBLE("extensible"),

        /**
         * Candidate Binding
         * 
         * <p>This value set is a candidate to substitute for the overall conformance value set in some situations; usually these 
         * are defined in the documentation
         */
        CANDIDATE("candidate"),

        /**
         * Current Binding
         * 
         * <p>New records are required to use this value set, but legacy records may use other codes. The definition of 'new 
         * record' is difficult, since systems often create new records based on pre-existing data. Usually 'current' bindings 
         * are mandated by an external authority that makes clear rules around this
         */
        CURRENT("current"),

        /**
         * Preferred Binding
         * 
         * <p>This is the value set that is preferred in a given context (documentation should explain why)
         */
        PREFERRED("preferred"),

        /**
         * UI Suggested Binding
         * 
         * <p>This value set is provided for user look up in a given context. Typically, these valuesets only include a subset of 
         * codes relevant for input in a context
         */
        UI("ui"),

        /**
         * Starter Binding
         * 
         * <p>This value set is a good set of codes to start with when designing your system
         */
        STARTER("starter"),

        /**
         * Component Binding
         * 
         * <p>This value set is a component of the base value set. Usually this is called out so that documentation can be 
         * written about a portion of the value set
         */
        COMPONENT("component");

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
         * Factory method for creating AdditionalBindingPurpose.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding AdditionalBindingPurpose.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "maximum":
                return MAXIMUM;
            case "minimum":
                return MINIMUM;
            case "required":
                return REQUIRED;
            case "extensible":
                return EXTENSIBLE;
            case "candidate":
                return CANDIDATE;
            case "current":
                return CURRENT;
            case "preferred":
                return PREFERRED;
            case "ui":
                return UI;
            case "starter":
                return STARTER;
            case "component":
                return COMPONENT;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
