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

@System("http://hl7.org/fhir/questionnaire-disabled-display")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DisabledDisplay extends Code {
    /**
     * Hidden
     * 
     * <p>The item (and its children) should not be visible to the user at all.
     */
    public static final DisabledDisplay HIDDEN = DisabledDisplay.builder().value(Value.HIDDEN).build();

    /**
     * Protected
     * 
     * <p>The item (and possibly its children) should not be selectable or editable but should still be visible - to allow 
     * the user to see what questions *could* have been completed had other answers caused the item to be enabled.
     */
    public static final DisabledDisplay PROTECTED = DisabledDisplay.builder().value(Value.PROTECTED).build();

    private volatile int hashCode;

    private DisabledDisplay(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DisabledDisplay as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DisabledDisplay objects from a passed enum value.
     */
    public static DisabledDisplay of(Value value) {
        switch (value) {
        case HIDDEN:
            return HIDDEN;
        case PROTECTED:
            return PROTECTED;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DisabledDisplay objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DisabledDisplay of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DisabledDisplay objects from a passed string value.
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
     * Inherited factory method for creating DisabledDisplay objects from a passed string value.
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
        DisabledDisplay other = (DisabledDisplay) obj;
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
         *     An enum constant for DisabledDisplay
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DisabledDisplay build() {
            DisabledDisplay disabledDisplay = new DisabledDisplay(this);
            if (validating) {
                validate(disabledDisplay);
            }
            return disabledDisplay;
        }

        protected void validate(DisabledDisplay disabledDisplay) {
            super.validate(disabledDisplay);
        }

        protected Builder from(DisabledDisplay disabledDisplay) {
            super.from(disabledDisplay);
            return this;
        }
    }

    public enum Value {
        /**
         * Hidden
         * 
         * <p>The item (and its children) should not be visible to the user at all.
         */
        HIDDEN("hidden"),

        /**
         * Protected
         * 
         * <p>The item (and possibly its children) should not be selectable or editable but should still be visible - to allow 
         * the user to see what questions *could* have been completed had other answers caused the item to be enabled.
         */
        PROTECTED("protected");

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
         * Factory method for creating DisabledDisplay.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DisabledDisplay.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "hidden":
                return HIDDEN;
            case "protected":
                return PROTECTED;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
