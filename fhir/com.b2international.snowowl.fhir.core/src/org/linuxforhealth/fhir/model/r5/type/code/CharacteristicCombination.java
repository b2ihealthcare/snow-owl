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

@System("http://hl7.org/fhir/characteristic-combination")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CharacteristicCombination extends Code {
    /**
     * All of
     * 
     * <p>Combine characteristics with AND.
     */
    public static final CharacteristicCombination ALL_OF = CharacteristicCombination.builder().value(Value.ALL_OF).build();

    /**
     * Any of
     * 
     * <p>Combine characteristics with OR.
     */
    public static final CharacteristicCombination ANY_OF = CharacteristicCombination.builder().value(Value.ANY_OF).build();

    /**
     * At least
     * 
     * <p>Meet at least the threshold number of characteristics for definition.
     */
    public static final CharacteristicCombination AT_LEAST = CharacteristicCombination.builder().value(Value.AT_LEAST).build();

    /**
     * At most
     * 
     * <p>Meet at most the threshold number of characteristics for definition.
     */
    public static final CharacteristicCombination AT_MOST = CharacteristicCombination.builder().value(Value.AT_MOST).build();

    /**
     * Statistical
     * 
     * <p>Combine characteristics statistically. Use method to specify the statistical method.
     */
    public static final CharacteristicCombination STATISTICAL = CharacteristicCombination.builder().value(Value.STATISTICAL).build();

    /**
     * Net effect
     * 
     * <p>Combine characteristics by addition of benefits and subtraction of harms.
     */
    public static final CharacteristicCombination NET_EFFECT = CharacteristicCombination.builder().value(Value.NET_EFFECT).build();

    /**
     * Dataset
     * 
     * <p>Combine characteristics as a collection used as the dataset.
     */
    public static final CharacteristicCombination DATASET = CharacteristicCombination.builder().value(Value.DATASET).build();

    private volatile int hashCode;

    private CharacteristicCombination(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this CharacteristicCombination as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating CharacteristicCombination objects from a passed enum value.
     */
    public static CharacteristicCombination of(Value value) {
        switch (value) {
        case ALL_OF:
            return ALL_OF;
        case ANY_OF:
            return ANY_OF;
        case AT_LEAST:
            return AT_LEAST;
        case AT_MOST:
            return AT_MOST;
        case STATISTICAL:
            return STATISTICAL;
        case NET_EFFECT:
            return NET_EFFECT;
        case DATASET:
            return DATASET;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating CharacteristicCombination objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static CharacteristicCombination of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating CharacteristicCombination objects from a passed string value.
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
     * Inherited factory method for creating CharacteristicCombination objects from a passed string value.
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
        CharacteristicCombination other = (CharacteristicCombination) obj;
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
         *     An enum constant for CharacteristicCombination
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public CharacteristicCombination build() {
            CharacteristicCombination characteristicCombination = new CharacteristicCombination(this);
            if (validating) {
                validate(characteristicCombination);
            }
            return characteristicCombination;
        }

        protected void validate(CharacteristicCombination characteristicCombination) {
            super.validate(characteristicCombination);
        }

        protected Builder from(CharacteristicCombination characteristicCombination) {
            super.from(characteristicCombination);
            return this;
        }
    }

    public enum Value {
        /**
         * All of
         * 
         * <p>Combine characteristics with AND.
         */
        ALL_OF("all-of"),

        /**
         * Any of
         * 
         * <p>Combine characteristics with OR.
         */
        ANY_OF("any-of"),

        /**
         * At least
         * 
         * <p>Meet at least the threshold number of characteristics for definition.
         */
        AT_LEAST("at-least"),

        /**
         * At most
         * 
         * <p>Meet at most the threshold number of characteristics for definition.
         */
        AT_MOST("at-most"),

        /**
         * Statistical
         * 
         * <p>Combine characteristics statistically. Use method to specify the statistical method.
         */
        STATISTICAL("statistical"),

        /**
         * Net effect
         * 
         * <p>Combine characteristics by addition of benefits and subtraction of harms.
         */
        NET_EFFECT("net-effect"),

        /**
         * Dataset
         * 
         * <p>Combine characteristics as a collection used as the dataset.
         */
        DATASET("dataset");

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
         * Factory method for creating CharacteristicCombination.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding CharacteristicCombination.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "all-of":
                return ALL_OF;
            case "any-of":
                return ANY_OF;
            case "at-least":
                return AT_LEAST;
            case "at-most":
                return AT_MOST;
            case "statistical":
                return STATISTICAL;
            case "net-effect":
                return NET_EFFECT;
            case "dataset":
                return DATASET;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
