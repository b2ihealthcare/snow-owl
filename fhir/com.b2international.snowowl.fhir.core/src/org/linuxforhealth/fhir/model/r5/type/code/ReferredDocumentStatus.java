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

@System("http://hl7.org/fhir/composition-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ReferredDocumentStatus extends Code {
    /**
     * Registered
     * 
     * <p>The existence of the composition is registered, but there is nothing yet available.
     */
    public static final ReferredDocumentStatus REGISTERED = ReferredDocumentStatus.builder().value(Value.REGISTERED).build();

    /**
     * Partial
     * 
     * <p>This is a partial (e.g. initial, interim or preliminary) composition: data in the composition may be incomplete or 
     * unverified.
     */
    public static final ReferredDocumentStatus PARTIAL = ReferredDocumentStatus.builder().value(Value.PARTIAL).build();

    /**
     * Preliminary
     * 
     * <p>Verified early results are available, but not all results are final.
     */
    public static final ReferredDocumentStatus PRELIMINARY = ReferredDocumentStatus.builder().value(Value.PRELIMINARY).build();

    /**
     * Final
     * 
     * <p>This version of the composition is complete and verified by an appropriate person and no further work is planned. 
     * Any subsequent updates would be on a new version of the composition.
     */
    public static final ReferredDocumentStatus FINAL = ReferredDocumentStatus.builder().value(Value.FINAL).build();

    /**
     * Amended
     * 
     * <p>The composition content or the referenced resources have been modified (edited or added to) subsequent to being 
     * released as "final" and the composition is complete and verified by an authorized person.
     */
    public static final ReferredDocumentStatus AMENDED = ReferredDocumentStatus.builder().value(Value.AMENDED).build();

    /**
     * Corrected
     * 
     * <p>Subsequent to being final, the composition content has been modified to correct an error in the composition or 
     * referenced results.
     */
    public static final ReferredDocumentStatus CORRECTED = ReferredDocumentStatus.builder().value(Value.CORRECTED).build();

    /**
     * Appended
     * 
     * <p>Subsequent to being final, the composition content has been modified by adding new content. The existing content is 
     * unchanged.
     */
    public static final ReferredDocumentStatus APPENDED = ReferredDocumentStatus.builder().value(Value.APPENDED).build();

    /**
     * Cancelled
     * 
     * <p>The composition is unavailable because the measurement was not started or not completed (also sometimes called 
     * "aborted").
     */
    public static final ReferredDocumentStatus CANCELLED = ReferredDocumentStatus.builder().value(Value.CANCELLED).build();

    /**
     * Entered in Error
     * 
     * <p>The composition or document was originally created/issued in error, and this is an amendment that marks that the 
     * entire series should not be considered as valid.
     */
    public static final ReferredDocumentStatus ENTERED_IN_ERROR = ReferredDocumentStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Deprecated
     * 
     * <p>This composition has been withdrawn or superseded and should no longer be used.
     */
    public static final ReferredDocumentStatus DEPRECATED = ReferredDocumentStatus.builder().value(Value.DEPRECATED).build();

    /**
     * Unknown
     * 
     * <p>The authoring/source system does not know which of the status values currently applies for this observation. Note: 
     * This concept is not to be used for "other" - one of the listed statuses is presumed to apply, but the authoring/source 
     * system does not know which.
     */
    public static final ReferredDocumentStatus UNKNOWN = ReferredDocumentStatus.builder().value(Value.UNKNOWN).build();

    private volatile int hashCode;

    private ReferredDocumentStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ReferredDocumentStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ReferredDocumentStatus objects from a passed enum value.
     */
    public static ReferredDocumentStatus of(Value value) {
        switch (value) {
        case REGISTERED:
            return REGISTERED;
        case PARTIAL:
            return PARTIAL;
        case PRELIMINARY:
            return PRELIMINARY;
        case FINAL:
            return FINAL;
        case AMENDED:
            return AMENDED;
        case CORRECTED:
            return CORRECTED;
        case APPENDED:
            return APPENDED;
        case CANCELLED:
            return CANCELLED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case DEPRECATED:
            return DEPRECATED;
        case UNKNOWN:
            return UNKNOWN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ReferredDocumentStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ReferredDocumentStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ReferredDocumentStatus objects from a passed string value.
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
     * Inherited factory method for creating ReferredDocumentStatus objects from a passed string value.
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
        ReferredDocumentStatus other = (ReferredDocumentStatus) obj;
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
         *     An enum constant for ReferredDocumentStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ReferredDocumentStatus build() {
            ReferredDocumentStatus referredDocumentStatus = new ReferredDocumentStatus(this);
            if (validating) {
                validate(referredDocumentStatus);
            }
            return referredDocumentStatus;
        }

        protected void validate(ReferredDocumentStatus referredDocumentStatus) {
            super.validate(referredDocumentStatus);
        }

        protected Builder from(ReferredDocumentStatus referredDocumentStatus) {
            super.from(referredDocumentStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Registered
         * 
         * <p>The existence of the composition is registered, but there is nothing yet available.
         */
        REGISTERED("registered"),

        /**
         * Partial
         * 
         * <p>This is a partial (e.g. initial, interim or preliminary) composition: data in the composition may be incomplete or 
         * unverified.
         */
        PARTIAL("partial"),

        /**
         * Preliminary
         * 
         * <p>Verified early results are available, but not all results are final.
         */
        PRELIMINARY("preliminary"),

        /**
         * Final
         * 
         * <p>This version of the composition is complete and verified by an appropriate person and no further work is planned. 
         * Any subsequent updates would be on a new version of the composition.
         */
        FINAL("final"),

        /**
         * Amended
         * 
         * <p>The composition content or the referenced resources have been modified (edited or added to) subsequent to being 
         * released as "final" and the composition is complete and verified by an authorized person.
         */
        AMENDED("amended"),

        /**
         * Corrected
         * 
         * <p>Subsequent to being final, the composition content has been modified to correct an error in the composition or 
         * referenced results.
         */
        CORRECTED("corrected"),

        /**
         * Appended
         * 
         * <p>Subsequent to being final, the composition content has been modified by adding new content. The existing content is 
         * unchanged.
         */
        APPENDED("appended"),

        /**
         * Cancelled
         * 
         * <p>The composition is unavailable because the measurement was not started or not completed (also sometimes called 
         * "aborted").
         */
        CANCELLED("cancelled"),

        /**
         * Entered in Error
         * 
         * <p>The composition or document was originally created/issued in error, and this is an amendment that marks that the 
         * entire series should not be considered as valid.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Deprecated
         * 
         * <p>This composition has been withdrawn or superseded and should no longer be used.
         */
        DEPRECATED("deprecated"),

        /**
         * Unknown
         * 
         * <p>The authoring/source system does not know which of the status values currently applies for this observation. Note: 
         * This concept is not to be used for "other" - one of the listed statuses is presumed to apply, but the authoring/source 
         * system does not know which.
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
         * Factory method for creating ReferredDocumentStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ReferredDocumentStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "registered":
                return REGISTERED;
            case "partial":
                return PARTIAL;
            case "preliminary":
                return PRELIMINARY;
            case "final":
                return FINAL;
            case "amended":
                return AMENDED;
            case "corrected":
                return CORRECTED;
            case "appended":
                return APPENDED;
            case "cancelled":
                return CANCELLED;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "deprecated":
                return DEPRECATED;
            case "unknown":
                return UNKNOWN;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
