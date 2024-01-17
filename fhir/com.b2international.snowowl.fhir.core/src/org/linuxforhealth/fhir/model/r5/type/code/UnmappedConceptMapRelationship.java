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

@System("http://hl7.org/fhir/concept-map-relationship")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class UnmappedConceptMapRelationship extends Code {
    /**
     * Related To
     * 
     * <p>The concepts are related to each other, but the exact relationship is not known.
     */
    public static final UnmappedConceptMapRelationship RELATED_TO = UnmappedConceptMapRelationship.builder().value(Value.RELATED_TO).build();

    /**
     * Equivalent
     * 
     * <p>The definitions of the concepts mean the same thing.
     */
    public static final UnmappedConceptMapRelationship EQUIVALENT = UnmappedConceptMapRelationship.builder().value(Value.EQUIVALENT).build();

    /**
     * Source Is Narrower Than Target
     * 
     * <p>The source concept is narrower in meaning than the target concept.
     */
    public static final UnmappedConceptMapRelationship SOURCE_IS_NARROWER_THAN_TARGET = UnmappedConceptMapRelationship.builder().value(Value.SOURCE_IS_NARROWER_THAN_TARGET).build();

    /**
     * Source Is Broader Than Target
     * 
     * <p>The source concept is broader in meaning than the target concept.
     */
    public static final UnmappedConceptMapRelationship SOURCE_IS_BROADER_THAN_TARGET = UnmappedConceptMapRelationship.builder().value(Value.SOURCE_IS_BROADER_THAN_TARGET).build();

    /**
     * Not Related To
     * 
     * <p>This is an explicit assertion that the target concept is not related to the source concept.
     */
    public static final UnmappedConceptMapRelationship NOT_RELATED_TO = UnmappedConceptMapRelationship.builder().value(Value.NOT_RELATED_TO).build();

    private volatile int hashCode;

    private UnmappedConceptMapRelationship(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this UnmappedConceptMapRelationship as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating UnmappedConceptMapRelationship objects from a passed enum value.
     */
    public static UnmappedConceptMapRelationship of(Value value) {
        switch (value) {
        case RELATED_TO:
            return RELATED_TO;
        case EQUIVALENT:
            return EQUIVALENT;
        case SOURCE_IS_NARROWER_THAN_TARGET:
            return SOURCE_IS_NARROWER_THAN_TARGET;
        case SOURCE_IS_BROADER_THAN_TARGET:
            return SOURCE_IS_BROADER_THAN_TARGET;
        case NOT_RELATED_TO:
            return NOT_RELATED_TO;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating UnmappedConceptMapRelationship objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static UnmappedConceptMapRelationship of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating UnmappedConceptMapRelationship objects from a passed string value.
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
     * Inherited factory method for creating UnmappedConceptMapRelationship objects from a passed string value.
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
        UnmappedConceptMapRelationship other = (UnmappedConceptMapRelationship) obj;
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
         *     An enum constant for UnmappedConceptMapRelationship
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public UnmappedConceptMapRelationship build() {
            UnmappedConceptMapRelationship unmappedConceptMapRelationship = new UnmappedConceptMapRelationship(this);
            if (validating) {
                validate(unmappedConceptMapRelationship);
            }
            return unmappedConceptMapRelationship;
        }

        protected void validate(UnmappedConceptMapRelationship unmappedConceptMapRelationship) {
            super.validate(unmappedConceptMapRelationship);
        }

        protected Builder from(UnmappedConceptMapRelationship unmappedConceptMapRelationship) {
            super.from(unmappedConceptMapRelationship);
            return this;
        }
    }

    public enum Value {
        /**
         * Related To
         * 
         * <p>The concepts are related to each other, but the exact relationship is not known.
         */
        RELATED_TO("related-to"),

        /**
         * Equivalent
         * 
         * <p>The definitions of the concepts mean the same thing.
         */
        EQUIVALENT("equivalent"),

        /**
         * Source Is Narrower Than Target
         * 
         * <p>The source concept is narrower in meaning than the target concept.
         */
        SOURCE_IS_NARROWER_THAN_TARGET("source-is-narrower-than-target"),

        /**
         * Source Is Broader Than Target
         * 
         * <p>The source concept is broader in meaning than the target concept.
         */
        SOURCE_IS_BROADER_THAN_TARGET("source-is-broader-than-target"),

        /**
         * Not Related To
         * 
         * <p>This is an explicit assertion that the target concept is not related to the source concept.
         */
        NOT_RELATED_TO("not-related-to");

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
         * Factory method for creating UnmappedConceptMapRelationship.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding UnmappedConceptMapRelationship.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "related-to":
                return RELATED_TO;
            case "equivalent":
                return EQUIVALENT;
            case "source-is-narrower-than-target":
                return SOURCE_IS_NARROWER_THAN_TARGET;
            case "source-is-broader-than-target":
                return SOURCE_IS_BROADER_THAN_TARGET;
            case "not-related-to":
                return NOT_RELATED_TO;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
