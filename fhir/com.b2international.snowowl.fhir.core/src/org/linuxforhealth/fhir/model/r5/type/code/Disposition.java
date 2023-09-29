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

@System("http://hl7.org/fhir/artifactassessment-disposition")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Disposition extends Code {
    /**
     * Unresolved
     * 
     * <p>The comment is unresolved
     */
    public static final Disposition UNRESOLVED = Disposition.builder().value(Value.UNRESOLVED).build();

    /**
     * Not Persuasive
     * 
     * <p>The comment is not persuasive (rejected in full)
     */
    public static final Disposition NOT_PERSUASIVE = Disposition.builder().value(Value.NOT_PERSUASIVE).build();

    /**
     * Persuasive
     * 
     * <p>The comment is persuasive (accepted in full)
     */
    public static final Disposition PERSUASIVE = Disposition.builder().value(Value.PERSUASIVE).build();

    /**
     * Persuasive with Modification
     * 
     * <p>The comment is persuasive with modification (partially accepted)
     */
    public static final Disposition PERSUASIVE_WITH_MODIFICATION = Disposition.builder().value(Value.PERSUASIVE_WITH_MODIFICATION).build();

    /**
     * Not Persuasive with Modification
     * 
     * <p>The comment is not persuasive with modification (partially rejected)
     */
    public static final Disposition NOT_PERSUASIVE_WITH_MODIFICATION = Disposition.builder().value(Value.NOT_PERSUASIVE_WITH_MODIFICATION).build();

    private volatile int hashCode;

    private Disposition(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this Disposition as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating Disposition objects from a passed enum value.
     */
    public static Disposition of(Value value) {
        switch (value) {
        case UNRESOLVED:
            return UNRESOLVED;
        case NOT_PERSUASIVE:
            return NOT_PERSUASIVE;
        case PERSUASIVE:
            return PERSUASIVE;
        case PERSUASIVE_WITH_MODIFICATION:
            return PERSUASIVE_WITH_MODIFICATION;
        case NOT_PERSUASIVE_WITH_MODIFICATION:
            return NOT_PERSUASIVE_WITH_MODIFICATION;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating Disposition objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Disposition of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating Disposition objects from a passed string value.
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
     * Inherited factory method for creating Disposition objects from a passed string value.
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
        Disposition other = (Disposition) obj;
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
         *     An enum constant for Disposition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public Disposition build() {
            Disposition disposition = new Disposition(this);
            if (validating) {
                validate(disposition);
            }
            return disposition;
        }

        protected void validate(Disposition disposition) {
            super.validate(disposition);
        }

        protected Builder from(Disposition disposition) {
            super.from(disposition);
            return this;
        }
    }

    public enum Value {
        /**
         * Unresolved
         * 
         * <p>The comment is unresolved
         */
        UNRESOLVED("unresolved"),

        /**
         * Not Persuasive
         * 
         * <p>The comment is not persuasive (rejected in full)
         */
        NOT_PERSUASIVE("not-persuasive"),

        /**
         * Persuasive
         * 
         * <p>The comment is persuasive (accepted in full)
         */
        PERSUASIVE("persuasive"),

        /**
         * Persuasive with Modification
         * 
         * <p>The comment is persuasive with modification (partially accepted)
         */
        PERSUASIVE_WITH_MODIFICATION("persuasive-with-modification"),

        /**
         * Not Persuasive with Modification
         * 
         * <p>The comment is not persuasive with modification (partially rejected)
         */
        NOT_PERSUASIVE_WITH_MODIFICATION("not-persuasive-with-modification");

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
         * Factory method for creating Disposition.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding Disposition.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "unresolved":
                return UNRESOLVED;
            case "not-persuasive":
                return NOT_PERSUASIVE;
            case "persuasive":
                return PERSUASIVE;
            case "persuasive-with-modification":
                return PERSUASIVE_WITH_MODIFICATION;
            case "not-persuasive-with-modification":
                return NOT_PERSUASIVE_WITH_MODIFICATION;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
