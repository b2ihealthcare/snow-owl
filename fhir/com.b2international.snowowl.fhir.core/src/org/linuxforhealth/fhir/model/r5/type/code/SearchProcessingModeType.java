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

@System("http://hl7.org/fhir/search-processingmode")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SearchProcessingModeType extends Code {
    /**
     * Normal
     * 
     * <p>The search parameter is derived directly from the selected nodes based on the type definitions.
     */
    public static final SearchProcessingModeType NORMAL = SearchProcessingModeType.builder().value(Value.NORMAL).build();

    /**
     * Phonetic
     * 
     * <p>The search parameter is derived by a phonetic transform from the selected nodes.
     */
    public static final SearchProcessingModeType PHONETIC = SearchProcessingModeType.builder().value(Value.PHONETIC).build();

    /**
     * Other
     * 
     * <p>The interpretation of the xpath statement is unknown (and can't be automated).
     */
    public static final SearchProcessingModeType OTHER = SearchProcessingModeType.builder().value(Value.OTHER).build();

    private volatile int hashCode;

    private SearchProcessingModeType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this SearchProcessingModeType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating SearchProcessingModeType objects from a passed enum value.
     */
    public static SearchProcessingModeType of(Value value) {
        switch (value) {
        case NORMAL:
            return NORMAL;
        case PHONETIC:
            return PHONETIC;
        case OTHER:
            return OTHER;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating SearchProcessingModeType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static SearchProcessingModeType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating SearchProcessingModeType objects from a passed string value.
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
     * Inherited factory method for creating SearchProcessingModeType objects from a passed string value.
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
        SearchProcessingModeType other = (SearchProcessingModeType) obj;
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
         *     An enum constant for SearchProcessingModeType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public SearchProcessingModeType build() {
            SearchProcessingModeType searchProcessingModeType = new SearchProcessingModeType(this);
            if (validating) {
                validate(searchProcessingModeType);
            }
            return searchProcessingModeType;
        }

        protected void validate(SearchProcessingModeType searchProcessingModeType) {
            super.validate(searchProcessingModeType);
        }

        protected Builder from(SearchProcessingModeType searchProcessingModeType) {
            super.from(searchProcessingModeType);
            return this;
        }
    }

    public enum Value {
        /**
         * Normal
         * 
         * <p>The search parameter is derived directly from the selected nodes based on the type definitions.
         */
        NORMAL("normal"),

        /**
         * Phonetic
         * 
         * <p>The search parameter is derived by a phonetic transform from the selected nodes.
         */
        PHONETIC("phonetic"),

        /**
         * Other
         * 
         * <p>The interpretation of the xpath statement is unknown (and can't be automated).
         */
        OTHER("other");

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
         * Factory method for creating SearchProcessingModeType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding SearchProcessingModeType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "normal":
                return NORMAL;
            case "phonetic":
                return PHONETIC;
            case "other":
                return OTHER;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
