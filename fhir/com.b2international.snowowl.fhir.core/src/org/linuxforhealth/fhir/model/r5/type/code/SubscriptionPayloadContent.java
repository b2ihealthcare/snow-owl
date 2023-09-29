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

@System("http://hl7.org/fhir/subscription-payload-content")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SubscriptionPayloadContent extends Code {
    /**
     * Empty
     * 
     * <p>No resource content is transacted in the notification payload.
     */
    public static final SubscriptionPayloadContent EMPTY = SubscriptionPayloadContent.builder().value(Value.EMPTY).build();

    /**
     * Id-only
     * 
     * <p>Only the resource id is transacted in the notification payload.
     */
    public static final SubscriptionPayloadContent ID_ONLY = SubscriptionPayloadContent.builder().value(Value.ID_ONLY).build();

    /**
     * Full-resource
     * 
     * <p>The entire resource is transacted in the notification payload.
     */
    public static final SubscriptionPayloadContent FULL_RESOURCE = SubscriptionPayloadContent.builder().value(Value.FULL_RESOURCE).build();

    private volatile int hashCode;

    private SubscriptionPayloadContent(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this SubscriptionPayloadContent as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating SubscriptionPayloadContent objects from a passed enum value.
     */
    public static SubscriptionPayloadContent of(Value value) {
        switch (value) {
        case EMPTY:
            return EMPTY;
        case ID_ONLY:
            return ID_ONLY;
        case FULL_RESOURCE:
            return FULL_RESOURCE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating SubscriptionPayloadContent objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static SubscriptionPayloadContent of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating SubscriptionPayloadContent objects from a passed string value.
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
     * Inherited factory method for creating SubscriptionPayloadContent objects from a passed string value.
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
        SubscriptionPayloadContent other = (SubscriptionPayloadContent) obj;
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
         *     An enum constant for SubscriptionPayloadContent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public SubscriptionPayloadContent build() {
            SubscriptionPayloadContent subscriptionPayloadContent = new SubscriptionPayloadContent(this);
            if (validating) {
                validate(subscriptionPayloadContent);
            }
            return subscriptionPayloadContent;
        }

        protected void validate(SubscriptionPayloadContent subscriptionPayloadContent) {
            super.validate(subscriptionPayloadContent);
        }

        protected Builder from(SubscriptionPayloadContent subscriptionPayloadContent) {
            super.from(subscriptionPayloadContent);
            return this;
        }
    }

    public enum Value {
        /**
         * Empty
         * 
         * <p>No resource content is transacted in the notification payload.
         */
        EMPTY("empty"),

        /**
         * Id-only
         * 
         * <p>Only the resource id is transacted in the notification payload.
         */
        ID_ONLY("id-only"),

        /**
         * Full-resource
         * 
         * <p>The entire resource is transacted in the notification payload.
         */
        FULL_RESOURCE("full-resource");

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
         * Factory method for creating SubscriptionPayloadContent.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding SubscriptionPayloadContent.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "empty":
                return EMPTY;
            case "id-only":
                return ID_ONLY;
            case "full-resource":
                return FULL_RESOURCE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
