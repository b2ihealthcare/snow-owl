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

@System("http://hl7.org/fhir/imagingselection-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ImagingSelectionStatus extends Code {
    /**
     * Available
     * 
     * <p>The selected resources are available..
     */
    public static final ImagingSelectionStatus AVAILABLE = ImagingSelectionStatus.builder().value(Value.AVAILABLE).build();

    /**
     * Entered in Error
     * 
     * <p>The imaging selection has been withdrawn following a release. This electronic record should never have existed, 
     * though it is possible that real-world decisions were based on it. (If real-world activity has occurred, the status 
     * should be "cancelled" rather than "entered-in-error".).
     */
    public static final ImagingSelectionStatus ENTERED_IN_ERROR = ImagingSelectionStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Unknown
     * 
     * <p>The system does not know which of the status values currently applies for this request. Note: This concept is not 
     * to be used for "other" - one of the listed statuses is presumed to apply, it's just not known which one.
     */
    public static final ImagingSelectionStatus UNKNOWN = ImagingSelectionStatus.builder().value(Value.UNKNOWN).build();

    private volatile int hashCode;

    private ImagingSelectionStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ImagingSelectionStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ImagingSelectionStatus objects from a passed enum value.
     */
    public static ImagingSelectionStatus of(Value value) {
        switch (value) {
        case AVAILABLE:
            return AVAILABLE;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case UNKNOWN:
            return UNKNOWN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ImagingSelectionStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ImagingSelectionStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ImagingSelectionStatus objects from a passed string value.
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
     * Inherited factory method for creating ImagingSelectionStatus objects from a passed string value.
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
        ImagingSelectionStatus other = (ImagingSelectionStatus) obj;
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
         *     An enum constant for ImagingSelectionStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ImagingSelectionStatus build() {
            ImagingSelectionStatus imagingSelectionStatus = new ImagingSelectionStatus(this);
            if (validating) {
                validate(imagingSelectionStatus);
            }
            return imagingSelectionStatus;
        }

        protected void validate(ImagingSelectionStatus imagingSelectionStatus) {
            super.validate(imagingSelectionStatus);
        }

        protected Builder from(ImagingSelectionStatus imagingSelectionStatus) {
            super.from(imagingSelectionStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Available
         * 
         * <p>The selected resources are available..
         */
        AVAILABLE("available"),

        /**
         * Entered in Error
         * 
         * <p>The imaging selection has been withdrawn following a release. This electronic record should never have existed, 
         * though it is possible that real-world decisions were based on it. (If real-world activity has occurred, the status 
         * should be "cancelled" rather than "entered-in-error".).
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Unknown
         * 
         * <p>The system does not know which of the status values currently applies for this request. Note: This concept is not 
         * to be used for "other" - one of the listed statuses is presumed to apply, it's just not known which one.
         */
        UNKNOWN("unknown");

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
         * Factory method for creating ImagingSelectionStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ImagingSelectionStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "available":
                return AVAILABLE;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "unknown":
                return UNKNOWN;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
