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

@System("http://hl7.org/fhir/artifactassessment-information-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class InformationType extends Code {
    /**
     * Comment
     * 
     * <p>A comment on the artifact
     */
    public static final InformationType COMMENT = InformationType.builder().value(Value.COMMENT).build();

    /**
     * Classifier
     * 
     * <p>A classifier of the artifact
     */
    public static final InformationType CLASSIFIER = InformationType.builder().value(Value.CLASSIFIER).build();

    /**
     * Rating
     * 
     * <p>A rating of the artifact
     */
    public static final InformationType RATING = InformationType.builder().value(Value.RATING).build();

    /**
     * Container
     * 
     * <p>A container for multiple components
     */
    public static final InformationType CONTAINER = InformationType.builder().value(Value.CONTAINER).build();

    /**
     * Response
     * 
     * <p>A response to a comment
     */
    public static final InformationType RESPONSE = InformationType.builder().value(Value.RESPONSE).build();

    /**
     * Change Request
     * 
     * <p>A change request for the artifact
     */
    public static final InformationType CHANGE_REQUEST = InformationType.builder().value(Value.CHANGE_REQUEST).build();

    private volatile int hashCode;

    private InformationType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this InformationType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating InformationType objects from a passed enum value.
     */
    public static InformationType of(Value value) {
        switch (value) {
        case COMMENT:
            return COMMENT;
        case CLASSIFIER:
            return CLASSIFIER;
        case RATING:
            return RATING;
        case CONTAINER:
            return CONTAINER;
        case RESPONSE:
            return RESPONSE;
        case CHANGE_REQUEST:
            return CHANGE_REQUEST;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating InformationType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static InformationType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating InformationType objects from a passed string value.
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
     * Inherited factory method for creating InformationType objects from a passed string value.
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
        InformationType other = (InformationType) obj;
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
         *     An enum constant for InformationType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public InformationType build() {
            InformationType informationType = new InformationType(this);
            if (validating) {
                validate(informationType);
            }
            return informationType;
        }

        protected void validate(InformationType informationType) {
            super.validate(informationType);
        }

        protected Builder from(InformationType informationType) {
            super.from(informationType);
            return this;
        }
    }

    public enum Value {
        /**
         * Comment
         * 
         * <p>A comment on the artifact
         */
        COMMENT("comment"),

        /**
         * Classifier
         * 
         * <p>A classifier of the artifact
         */
        CLASSIFIER("classifier"),

        /**
         * Rating
         * 
         * <p>A rating of the artifact
         */
        RATING("rating"),

        /**
         * Container
         * 
         * <p>A container for multiple components
         */
        CONTAINER("container"),

        /**
         * Response
         * 
         * <p>A response to a comment
         */
        RESPONSE("response"),

        /**
         * Change Request
         * 
         * <p>A change request for the artifact
         */
        CHANGE_REQUEST("change-request");

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
         * Factory method for creating InformationType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding InformationType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "comment":
                return COMMENT;
            case "classifier":
                return CLASSIFIER;
            case "rating":
                return RATING;
            case "container":
                return CONTAINER;
            case "response":
                return RESPONSE;
            case "change-request":
                return CHANGE_REQUEST;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
